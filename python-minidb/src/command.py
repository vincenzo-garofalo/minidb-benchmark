"""Parser dei comandi supportati da MiniDB."""

from dataclasses import dataclass

@dataclass(frozen=True)
class Command:
    """Rappresenta un comando MiniDB gia validato."""
    name: str
    key: str | None = None
    value: str | None = None

    @staticmethod
    def parse(input_line: str) -> "Command":
        """Converte una riga di testo in un comando MiniDB."""
        parts = input_line.split()

        if not parts:
            raise ValueError("empty command")

        command_name = parts[0].upper()

        if command_name == "PING":      # verifica che MiniDB risponda
            if len(parts) != 1:
                raise ValueError("wrong number of arguments for PING")
            return Command("PING")

        if command_name == "SET":       # imposta il valore associato a una chiave
            if len(parts) != 3:
                raise ValueError("wrong number of arguments for SET")
            return Command("SET", key=parts[1], value=parts[2])

        if command_name == "GET":       # recupera il valore associato a una chiave
            if len(parts) != 2:
                raise ValueError("wrong number of arguments for GET")
            return Command("GET", key=parts[1])

        if command_name == "DEL":       # elimina una chiave dal db
            if len(parts) != 2:
                raise ValueError("wrong number of arguments for DEL")
            return Command("DEL", key=parts[1])

        if command_name == "EXISTS":    # verifica se una chiave esiste
            if len(parts) != 2:
                raise ValueError("wrong number of arguments for EXISTS")
            return Command("EXISTS", key=parts[1])

        if command_name == "INCR":      # incrementa di 1 il valore numerico di una chiave
            if len(parts) != 2:
                raise ValueError("wrong number of arguments for INCR")
            return Command("INCR", key=parts[1])

        raise ValueError(f"unknown command {command_name}")
