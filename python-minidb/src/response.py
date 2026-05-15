"""Risposte restituite dai comandi MiniDB."""

from dataclasses import dataclass

@dataclass(frozen=True)
class Response:
    """Rappresenta una risposta testuale di MiniDB."""
    kind: str
    value: str | int | None = None

    @staticmethod
    def text(value: str) -> "Response":     # risposta ai comandi Ping, Set, Get
        return Response("text", value)

    @staticmethod
    def not_found() -> "Response":          # risposta al comando Get
        return Response("not_found")

    @staticmethod
    def integer(value: int) -> "Response":  # risposta ai comandi Del, Exists, Incr
        return Response("integer", value)

    @staticmethod
    def error(message: str) -> "Response":  # risposta ai comandi non validi
        return Response("error", message)

    def to_text(self) -> str:
        """Converte la risposta nel testo da mostrare all'utente."""
        if self.kind == "text":
            return str(self.value)
        if self.kind == "not_found":
            return "NOT_FOUND"
        if self.kind == "integer":
            return str(self.value)
        if self.kind == "error":
            return f"ERR {self.value}"

        raise ValueError(f"unknown response kind {self.kind}")
