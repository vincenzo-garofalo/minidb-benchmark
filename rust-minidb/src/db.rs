use crate::command::Command;
use crate::response::Response;
use std::collections::HashMap;

/// Definizione del MiniDb (implementato tramite un HashMap).
pub struct MiniDb {
    data: HashMap<String, String>,
}

impl MiniDb {
    /// Crea un database vuoto.
    pub fn new() -> Self {
        Self {
            data: HashMap::new(),
        }
    }

    /// Esegue un comando e restituisce la risposta corrispondente.
    pub fn execute(&mut self, command: Command) -> Response {
        match command {
            Command::Ping => Response::Text("PONG".to_string()),
            Command::Set { key, value } => {
                self.data.insert(key, value);
                Response::Text("OK".to_string())
            }
            Command::Get { key } => match self.data.get(&key) {
                Some(value) => Response::Text(value.clone()),
                None => Response::NotFound,
            },
            Command::Del { key } => {
                if self.data.remove(&key).is_some() {
                    Response::Integer(1)
                } else {
                    Response::Integer(0)
                }
            }
            Command::Exists { key } => {
                if self.data.contains_key(&key) {
                    Response::Integer(1)
                } else {
                    Response::Integer(0)
                }
            }
            Command::Incr { key } => {
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
}
