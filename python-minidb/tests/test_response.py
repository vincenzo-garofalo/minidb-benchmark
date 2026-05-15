import unittest

from response import Response

class ResponseTests(unittest.TestCase):
    """Unit tests per la conversione delle Response in str (metodo to_text di Response)."""
    def test_converts_text_response_to_text(self):
        self.assertEqual(Response.text("PONG").to_text(), "PONG")

    def test_converts_not_found_response_to_text(self):
        self.assertEqual(Response.not_found().to_text(), "NOT_FOUND")

    def test_converts_integer_response_to_text(self):
        self.assertEqual(Response.integer(1).to_text(), "1")

    def test_converts_error_response_to_text(self):
        self.assertEqual(
            Response.error("unknown command UNKNOWN").to_text(),
            "ERR unknown command UNKNOWN",
        )

if __name__ == "__main__":
    unittest.main()
