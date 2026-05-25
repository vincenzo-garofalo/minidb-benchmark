use crate::command::Command;
use crate::response::Response;
use std::collections::HashMap;
use std::time::{Duration, Instant};

/// Definizione del MiniDb (implementato tramite un HashMap).
pub struct MiniDb {
    data: HashMap<String, String>,          // chiave -> valore
    expires_at: HashMap<String, Instant>,   // chiave -> momento di scadenza
}

impl MiniDb {
    /// Crea un database vuoto.
    pub fn new() -> Self {
        Self {
            data: HashMap::new(),
            expires_at: HashMap::new(),
        }
    }

    /// Esegue un comando e restituisce la risposta corrispondente.
    pub fn execute(&mut self, command: Command) -> Response {
        match command {
            Command::Ping => Response::Text("PONG".to_string()),
            Command::Set { key, value } => {
                self.expires_at.remove(&key);
                self.data.insert(key, value);
                Response::Text("OK".to_string())
            }
            Command::Get { key } => {
                self.remove_if_expired(&key);
                match self.data.get(&key) {
                    Some(value) => Response::Text(value.clone()),
                    None => Response::NotFound,
                }
            }
            Command::Del { key } => {
                self.remove_if_expired(&key);
                self.expires_at.remove(&key);
                if self.data.remove(&key).is_some() {
                    Response::Integer(1)
                } else {
                    Response::Integer(0)
                }
            }
            Command::Exists { key } => {
                self.remove_if_expired(&key);
                if self.data.contains_key(&key) {
                    Response::Integer(1)
                } else {
                    Response::Integer(0)
                }
            }
            Command::Incr { key } => {
                self.remove_if_expired(&key);
                let current_value = self.data.get(&key);
                let next_value = match current_value {
                    Some(value) => match value.parse::<i64>() {
                        Ok(number) => number + 1,
                        Err(_) => return Response::Error("value is not an integer".to_string()),
                    },
                    None => 1,
                };
                self.data.insert(key, next_value.to_string());
                Response::Integer(next_value)
            }
            Command::Expire { key, seconds } => {
                self.remove_if_expired(&key);
                if !self.data.contains_key(&key) {
                    return Response::Integer(0);
                }
                if seconds <= 0 {
                    self.data.remove(&key);
                    self.expires_at.remove(&key);
                } else {
                    self.expires_at
                        .insert(key, Instant::now() + Duration::from_secs(seconds as u64));
                }
                Response::Integer(1)
            }
            Command::Ttl { key } => {
                self.remove_if_expired(&key);
                if !self.data.contains_key(&key) {
                    return Response::Integer(-2);
                }
                match self.expires_at.get(&key) {
                    Some(expires_at) => {
                        let remaining = expires_at.saturating_duration_since(Instant::now());
                        Response::Integer(remaining.as_secs() as i64)
                    }
                    None => Response::Integer(-1),
                }
            }
        }
    }

    /// Metodo helper che rimuove una entry se è scaduta.
    fn remove_if_expired(&mut self, key: &str) {
        if self
            .expires_at
            .get(key)
            .is_some_and(|expires_at| Instant::now() >= *expires_at)
        {
            self.data.remove(key);
            self.expires_at.remove(key);
        }
    }
}

impl Default for MiniDb {
    /// Crea un database di default, vuoto (funziona come il metodo new di MiniDb).
    fn default() -> Self {
        Self::new()
    }
}

/// Unit tests per l'esecuzione dei comandi sul database.
#[cfg(test)]
mod tests {
    use super::MiniDb;
    use crate::command::Command;
    use crate::response::Response;

    #[test]
    fn creates_empty_database() {
        let mut db = MiniDb::new();
        assert_eq!(
            db.execute(Command::Get {
                key: "missing".to_string(),
            }),
            Response::NotFound
        );
    }

    #[test]
    fn responds_to_ping() {
        let mut db = MiniDb::new();
        assert_eq!(
            db.execute(Command::Ping),
            Response::Text("PONG".to_string())
        );
    }

    #[test]
    fn sets_and_gets_value() {
        let mut db = MiniDb::new();
        assert_eq!(
            db.execute(Command::Set {
                key: "user".to_string(),
                value: "Mario".to_string(),
            }),
            Response::Text("OK".to_string())
        );
        assert_eq!(
            db.execute(Command::Get {
                key: "user".to_string(),
            }),
            Response::Text("Mario".to_string())
        );
    }

    #[test]
    fn overwrites_existing_value() {
        let mut db = MiniDb::new();
        db.execute(Command::Set {
            key: "user".to_string(),
            value: "Mario".to_string(),
        });
        db.execute(Command::Set {
            key: "user".to_string(),
            value: "Luigi".to_string(),
        });
        assert_eq!(
            db.execute(Command::Get {
                key: "user".to_string(),
            }),
            Response::Text("Luigi".to_string())
        );
    }

    #[test]
    fn deletes_existing_key_once() {
        let mut db = MiniDb::new();
        db.execute(Command::Set {
            key: "user".to_string(),
            value: "Mario".to_string(),
        });
        assert_eq!(
            db.execute(Command::Del {
                key: "user".to_string(),
            }),
            Response::Integer(1)
        );
        assert_eq!(
            db.execute(Command::Del {
                key: "user".to_string(),
            }),
            Response::Integer(0)
        );
    }

    #[test]
    fn checks_key_existence() {
        let mut db = MiniDb::new();
        db.execute(Command::Set {
            key: "user".to_string(),
            value: "Mario".to_string(),
        });
        assert_eq!(
            db.execute(Command::Exists {
                key: "user".to_string(),
            }),
            Response::Integer(1)
        );
        assert_eq!(
            db.execute(Command::Exists {
                key: "missing".to_string(),
            }),
            Response::Integer(0)
        );
    }

    #[test]
    fn increments_missing_key_from_one() {
        let mut db = MiniDb::new();
        assert_eq!(
            db.execute(Command::Incr {
                key: "counter".to_string(),
            }),
            Response::Integer(1)
        );
    }

    #[test]
    fn increments_existing_numeric_value() {
        let mut db = MiniDb::new();
        db.execute(Command::Set {
            key: "counter".to_string(),
            value: "41".to_string(),
        });
        assert_eq!(
            db.execute(Command::Incr {
                key: "counter".to_string(),
            }),
            Response::Integer(42)
        );
    }

    #[test]
    fn rejects_increment_on_non_numeric_value() {
        let mut db = MiniDb::new();
        db.execute(Command::Set {
            key: "name".to_string(),
            value: "Mario".to_string(),
        });
        assert_eq!(
            db.execute(Command::Incr {
                key: "name".to_string(),
            }),
            Response::Error("value is not an integer".to_string())
        );
    }

    #[test]
    fn expires_existing_key() {
        let mut db = MiniDb::new();
        db.execute(Command::Set {
            key: "session".to_string(),
            value: "open".to_string(),
        });
        assert_eq!(
            db.execute(Command::Expire {
                key: "session".to_string(),
                seconds: 0,
            }),
            Response::Integer(1)
        );
        assert_eq!(
            db.execute(Command::Get {
                key: "session".to_string(),
            }),
            Response::NotFound
        );
    }

    #[test]
    fn returns_ttl_status_codes() {
        let mut db = MiniDb::new();
        assert_eq!(
            db.execute(Command::Ttl {
                key: "missing".to_string(),
            }),
            Response::Integer(-2)
        );
        db.execute(Command::Set {
            key: "session".to_string(),
            value: "open".to_string(),
        });
        assert_eq!(
            db.execute(Command::Ttl {
                key: "session".to_string(),
            }),
            Response::Integer(-1)
        );
    }

    #[test]
    fn set_clears_existing_ttl() {
        let mut db = MiniDb::new();
        db.execute(Command::Set {
            key: "session".to_string(),
            value: "open".to_string(),
        });
        db.execute(Command::Expire {
            key: "session".to_string(),
            seconds: 30,
        });
        db.execute(Command::Set {
            key: "session".to_string(),
            value: "renewed".to_string(),
        });
        assert_eq!(
            db.execute(Command::Ttl {
                key: "session".to_string(),
            }),
            Response::Integer(-1)
        );
    }
}
