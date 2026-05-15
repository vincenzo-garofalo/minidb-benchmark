"""Interfaccia di linea di comando per MiniDB."""

from db import MiniDb
from command import Command
from response import Response

def main() -> None:
    db = MiniDb()

    while True:
        try:
            input_line = input("> ")
        except EOFError:
            break

        input_line = input_line.strip()

        if input_line.upper() in {"QUIT", "EXIT"}:
            break

        try:
            response = db.execute(Command.parse(input_line))
        except ValueError as error:
            response = Response.error(str(error))

        print(response.to_text())

if __name__ == "__main__":
    main()
