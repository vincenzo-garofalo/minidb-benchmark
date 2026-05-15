import unittest

from command import Command
from db import MiniDb
from response import Response

class MiniDbTests(unittest.TestCase):
    """Unit tests per l'esecuzione dei comandi sul database."""
    def test_creates_empty_database(self):
        db = MiniDb()
        self.assertEqual(db.execute(Command("GET", key="missing")), Response.not_found())

    def test_responds_to_ping(self):
        db = MiniDb()
        self.assertEqual(db.execute(Command("PING")), Response.text("PONG"))

    def test_sets_and_gets_value(self):
        db = MiniDb()
        self.assertEqual(
            db.execute(Command("SET", key="user", value="Mario")),
            Response.text("OK"),
        )
        self.assertEqual(db.execute(Command("GET", key="user")), Response.text("Mario"))

    def test_overwrites_existing_value(self):
        db = MiniDb()
        db.execute(Command("SET", key="user", value="Mario"))
        db.execute(Command("SET", key="user", value="Luigi"))
        self.assertEqual(db.execute(Command("GET", key="user")), Response.text("Luigi"))

    def test_deletes_existing_key_once(self):
        db = MiniDb()
        db.execute(Command("SET", key="user", value="Mario"))
        self.assertEqual(db.execute(Command("DEL", key="user")), Response.integer(1))
        self.assertEqual(db.execute(Command("DEL", key="user")), Response.integer(0))

    def test_checks_key_existence(self):
        db = MiniDb()
        db.execute(Command("SET", key="user", value="Mario"))
        self.assertEqual(db.execute(Command("EXISTS", key="user")), Response.integer(1))
        self.assertEqual(db.execute(Command("EXISTS", key="missing")), Response.integer(0))

    def test_increments_missing_key_from_one(self):
        db = MiniDb()
        self.assertEqual(db.execute(Command("INCR", key="counter")), Response.integer(1))

    def test_increments_existing_numeric_value(self):
        db = MiniDb()
        db.execute(Command("SET", key="counter", value="41"))
        self.assertEqual(db.execute(Command("INCR", key="counter")), Response.integer(42))

    def test_rejects_increment_on_non_numeric_value(self):
        db = MiniDb()
        db.execute(Command("SET", key="name", value="Mario"))
        self.assertEqual(
            db.execute(Command("INCR", key="name")),
            Response.error("value is not an integer"),
        )

if __name__ == "__main__":
    unittest.main()
