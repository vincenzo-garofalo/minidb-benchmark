"""Database key-value in memoria per MiniDB."""

from command import Command
from response import Response

class MiniDb:
    """Definizione del MiniDb (implementato tramite un Dizionario)."""

    def __init__(self) -> None:
        self._data: dict[str, str] = {}

    def execute(self, command: Command) -> Response:
        """Esegue un comando e restituisce la risposta corrispondente."""
        if command.name == "PING":
            return Response.text("PONG")

        if command.name == "SET":
            key = command.key
            value = command.value
            if key is None or value is None:
                return Response.error("wrong number of arguments for SET")
            self._data[key] = value
            return Response.text("OK")

        if command.name == "GET":
            key = command.key
            if key is None:
                return Response.error("wrong number of arguments for GET")
            value = self._data.get(key)
            if value is None:
                return Response.not_found()
            return Response.text(value)

        if command.name == "DEL":
            key = command.key
            if key is None:
                return Response.error("wrong number of arguments for DEL")
            if key in self._data:
                del self._data[key]
                return Response.integer(1)
            return Response.integer(0)

        if command.name == "EXISTS":
            key = command.key
            if key is None:
                return Response.error("wrong number of arguments for EXISTS")
            return Response.integer(1 if key in self._data else 0)

        if command.name == "INCR":
            key = command.key
            if key is None:
                return Response.error("wrong number of arguments for INCR")
            value = self._data.get(key)
            if value is None:
                next_value = 1
            else:
                try:
                    next_value = int(value) + 1
                except ValueError:
                    return Response.error("value is not an integer")
            self._data[key] = str(next_value)
            return Response.integer(next_value)

        return Response.error(f"unknown command {command.name}")
