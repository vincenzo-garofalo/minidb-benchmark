use rust_minidb::command::Command;
use rust_minidb::db::MiniDb;
use rust_minidb::response::Response;
use std::io::{self, Write};

/// Interfaccia di linea di comando per MiniDB.
fn main() {
    let mut db = MiniDb::new();

    loop {
        print!("> ");
        io::stdout().flush().expect("failed to flush stdout");

        let mut input = String::new();
        let bytes_read = io::stdin()
            .read_line(&mut input)
            .expect("failed to read input line");

        if bytes_read == 0 {
            break;
        }

        let input = input.trim();

        if input.eq_ignore_ascii_case("QUIT") || input.eq_ignore_ascii_case("EXIT") {
            break;
        }

        let response = match Command::parse(input) {
            Ok(command) => db.execute(command),
            Err(message) => Response::Error(message),
        };

        println!("{}", response.to_text());
    }
}
