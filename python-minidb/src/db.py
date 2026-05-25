"""Database key-value in memoria per MiniDB."""

import time
from command import Command
from response import Response

class MiniDb:
    """Definizione del MiniDb (implementato tramite un Dizionario)."""

    def __init__(self) -> None:
        self._data: dict[str, str] = {}         # chiave -> valore
        self._expires_at: dict[str, float] = {} # chiave -> timestamp di scadenza

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
            self._expires_at.pop(key, None)
            return Response.text("OK")

        if command.name == "GET":
            key = command.key
            if key is None:
                return Response.error("wrong number of arguments for GET")
            self._remove_if_expired(key)
            value = self._data.get(key)
            if value is None:
                return Response.not_found()
            return Response.text(value)

        if command.name == "DEL":
            key = command.key
            if key is None:
                return Response.error("wrong number of arguments for DEL")
            self._remove_if_expired(key)
            if key in self._data:
                del self._data[key]
                self._expires_at.pop(key, None)
                return Response.integer(1)
            return Response.integer(0)

        if command.name == "EXISTS":
            key = command.key
            if key is None:
                return Response.error("wrong number of arguments for EXISTS")
            self._remove_if_expired(key)
            return Response.integer(1 if key in self._data else 0)

        if command.name == "INCR":
            key = command.key
            if key is None:
                return Response.error("wrong number of arguments for INCR")
            self._remove_if_expired(key)
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

        if command.name == "EXPIRE":
            key = command.key
            seconds = command.seconds
            if key is None or seconds is None:
                return Response.error("wrong number of arguments for EXPIRE")
            self._remove_if_expired(key)
            if key not in self._data:
                return Response.integer(0)
            if seconds <= 0:
                del self._data[key]
                self._expires_at.pop(key, None)
            else:
                self._expires_at[key] = time.monotonic() + seconds
            return Response.integer(1)

        if command.name == "TTL":
            key = command.key
            if key is None:
                return Response.error("wrong number of arguments for TTL")
            self._remove_if_expired(key)
            if key not in self._data:
                return Response.integer(-2)
            expires_at = self._expires_at.get(key)
            if expires_at is None:
                return Response.integer(-1)
            return Response.integer(max(0, int(expires_at - time.monotonic())))

        return Response.error(f"unknown command {command.name}")

    def _remove_if_expired(self, key: str) -> None:
        """Metodo helper che rimuove una entry se è scaduta."""
        expires_at = self._expires_at.get(key)
        if expires_at is not None and time.monotonic() >= expires_at:
            self._data.pop(key, None)
            self._expires_at.pop(key, None)
