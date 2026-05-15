import unittest

from command import Command

class CommandTests(unittest.TestCase):
    """Unit tests per conversione input-comando (metodo parse di Command)"""
    def test_parses_ping_command(self):
        self.assertEqual(Command.parse("PING"), Command("PING"))

    def test_parses_set_command(self):
        self.assertEqual(
            Command.parse("SET user Mario"),
            Command("SET", key="user", value="Mario"),
        )

    def test_parses_get_command(self):
        self.assertEqual(Command.parse("GET user"), Command("GET", key="user"))

    def test_parses_del_command(self):
        self.assertEqual(Command.parse("DEL user"), Command("DEL", key="user"))

    def test_parses_exists_command(self):
        self.assertEqual(Command.parse("EXISTS user"), Command("EXISTS", key="user"))

    def test_parses_incr_command(self):
        self.assertEqual(Command.parse("INCR counter"), Command("INCR", key="counter"))

    def test_parses_commands_case_insensitively(self):
        self.assertEqual(Command.parse("ping"), Command("PING"))

    def test_ignores_extra_spaces(self):
        self.assertEqual(
            Command.parse("  SET   user   Mario  "),
            Command("SET", key="user", value="Mario"),
        )

    def test_rejects_empty_command(self):
        with self.assertRaisesRegex(ValueError, "empty command"):
            Command.parse("   ")

    def test_rejects_wrong_argument_count(self):
        with self.assertRaisesRegex(ValueError, "wrong number of arguments for GET"):
            Command.parse("GET")

    def test_rejects_unknown_command(self):
        with self.assertRaisesRegex(ValueError, "unknown command UNKNOWN"):
            Command.parse("UNKNOWN x")

if __name__ == "__main__":
    unittest.main()
