/// Definizione dei comandi supportati da MiniDB.
#[derive(Debug, PartialEq)]
pub enum Command {
    Ping,                                   // verifica che MiniDB risponda
    Set { key: String, value: String },     // imposta il valore associato a una chiave
    Get { key: String },                    // recupera il valore associato a una chiave
    Del { key: String },                    // elimina una chiave dal db
    Exists { key: String },                 // verifica se una chiave esiste
    Incr { key: String },                   // incrementa di 1 il valore numerico di una chiave
    Expire { key: String, seconds: i64 },   // imposta una scadenza in secondi
    Ttl { key: String },                    // restituisce i secondi rimanenti prima della scadenza
}

impl Command {
    /// Converte una riga di testo in un comando MiniDB.
    pub fn parse(input: &str) -> Result<Self, String> {
        let parts: Vec<&str> = input.split_whitespace().collect();

        if parts.is_empty() {
            return Err("empty command".to_string());
        }

        let command_name = parts[0].to_uppercase();

        match command_name.as_str() {
            "PING" => {
                if parts.len() != 1 {
                    return Err("wrong number of arguments for PING".to_string());
                }
                Ok(Command::Ping)
            }
            "SET" => {
                if parts.len() != 3 {
                    return Err("wrong number of arguments for SET".to_string());
                }
                Ok(Command::Set {
                    key: parts[1].to_string(),
                    value: parts[2].to_string(),
                })
            }
            "GET" => {
                if parts.len() != 2 {
                    return Err("wrong number of arguments for GET".to_string());
                }
                Ok(Command::Get {
                    key: parts[1].to_string(),
                })
            }
            "DEL" => {
                if parts.len() != 2 {
                    return Err("wrong number of arguments for DEL".to_string());
                }
                Ok(Command::Del {
                    key: parts[1].to_string(),
                })
            }
            "EXISTS" => {
                if parts.len() != 2 {
                    return Err("wrong number of arguments for EXISTS".to_string());
                }
                Ok(Command::Exists {
                    key: parts[1].to_string(),
                })
            }
            "INCR" => {
                if parts.len() != 2 {
                    return Err("wrong number of arguments for INCR".to_string());
                }
                Ok(Command::Incr {
                    key: parts[1].to_string(),
                })
            }
            "EXPIRE" => {
                if parts.len() != 3 {
                    return Err("wrong number of arguments for EXPIRE".to_string());
                }
                let seconds = parts[2]
                    .parse::<i64>()
                    .map_err(|_| "invalid expire seconds".to_string())?;
                Ok(Command::Expire {
                    key: parts[1].to_string(),
                    seconds,
                })
            }
            "TTL" => {
                if parts.len() != 2 {
                    return Err("wrong number of arguments for TTL".to_string());
                }
                Ok(Command::Ttl {
                    key: parts[1].to_string(),
                })
            }
            _ => Err(format!("unknown command {}", command_name)),
        }
    }
}

/// Unit tests per conversione input-comando (metodo parse di Command)
#[cfg(test)]
mod tests {
    use super::Command;

    #[test]
    fn parses_ping_command() {
        assert_eq!(Command::parse("PING"), Ok(Command::Ping));
    }

    #[test]
    fn parses_set_command() {
        assert_eq!(
            Command::parse("SET user Mario"),
            Ok(Command::Set {
                key: "user".to_string(),
                value: "Mario".to_string(),
            })
        );
    }

    #[test]
    fn parses_get_command() {
        assert_eq!(
            Command::parse("GET user"),
            Ok(Command::Get {
                key: "user".to_string(),
            })
        );
    }

    #[test]
    fn parses_del_command() {
        assert_eq!(
            Command::parse("DEL user"),
            Ok(Command::Del {
                key: "user".to_string(),
            })
        );
    }

    #[test]
    fn parses_exists_command() {
        assert_eq!(
            Command::parse("EXISTS user"),
            Ok(Command::Exists {
                key: "user".to_string(),
            })
        );
    }

    #[test]
    fn parses_incr_command() {
        assert_eq!(
            Command::parse("INCR counter"),
            Ok(Command::Incr {
                key: "counter".to_string(),
            })
        );
    }

    #[test]
    fn parses_expire_command() {
        assert_eq!(
            Command::parse("EXPIRE session 30"),
            Ok(Command::Expire {
                key: "session".to_string(),
                seconds: 30,
            })
        );
    }

    #[test]
    fn parses_ttl_command() {
        assert_eq!(
            Command::parse("TTL session"),
            Ok(Command::Ttl {
                key: "session".to_string(),
            })
        );
    }

    #[test]
    fn parses_commands_case_insensitively() {
        assert_eq!(Command::parse("ping"), Ok(Command::Ping));
    }

    #[test]
    fn ignores_extra_spaces() {
        assert_eq!(
            Command::parse("  SET   user   Mario  "),
            Ok(Command::Set {
                key: "user".to_string(),
                value: "Mario".to_string(),
            })
        );
    }

    #[test]
    fn rejects_empty_command() {
        assert_eq!(Command::parse("   "), Err("empty command".to_string()));
    }

    #[test]
    fn rejects_wrong_argument_count() {
        assert_eq!(
            Command::parse("GET"),
            Err("wrong number of arguments for GET".to_string())
        );
    }

    #[test]
    fn rejects_invalid_expire_seconds() {
        assert_eq!(
            Command::parse("EXPIRE user soon"),
            Err("invalid expire seconds".to_string())
        );
    }

    #[test]
    fn rejects_unknown_command() {
        assert_eq!(
            Command::parse("UNKNOWN x"),
            Err("unknown command UNKNOWN".to_string())
        );
    }
}
