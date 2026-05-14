/// Definizione possibili risposte ai comandi di MiniDB.
#[derive(Debug, PartialEq)]
pub enum Response {
    Text(String),  // risposta ai comandi Ping, Set, Get
    NotFound,      // risposta al comando Get
    Integer(i64),  // risposta ai comandi Del, Exists, Incr
    Error(String), // risposta ai comandi non validi
}

impl Response {
    /// Metodo per trasformare una Response nel testo da mostrare all'utente.
    pub fn to_text(&self) -> String {
        match self {
            Response::Text(value) => value.clone(),
            Response::NotFound => "NOT_FOUND".to_string(),
            Response::Integer(value) => value.to_string(),
            Response::Error(message) => format!("ERR {}", message),
        }
    }
}

/// Unit tests per la conversione delle Response in String (metodo to_text di Response).
#[cfg(test)]
mod tests {
    use super::Response;

    #[test]
    fn converts_text_response_to_text() {
        let response = Response::Text("PONG".to_string());
        assert_eq!(response.to_text(), "PONG");
    }

    #[test]
    fn converts_not_found_response_to_text() {
        let response = Response::NotFound;
        assert_eq!(response.to_text(), "NOT_FOUND");
    }

    #[test]
    fn converts_integer_response_to_text() {
        let response = Response::Integer(1);
        assert_eq!(response.to_text(), "1");
    }

    #[test]
    fn converts_error_response_to_text() {
        let response = Response::Error("unknown command UNKNOWN".to_string());
        assert_eq!(response.to_text(), "ERR unknown command UNKNOWN");
    }
}
