import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/// Unit tests per l'esecuzione dei comandi sul database.
final class MiniDbTest {
    @Test
    void parsesCommands() {
        assertEquals(Command.ping(), Command.parse("PING"));
        assertEquals(Command.set("user", "Mario"), Command.parse("SET user Mario"));
        assertEquals(Command.get("user"), Command.parse("GET user"));
        assertEquals(Command.del("user"), Command.parse("DEL user"));
        assertEquals(Command.exists("user"), Command.parse("EXISTS user"));
        assertEquals(Command.incr("counter"), Command.parse("INCR counter"));
        assertEquals(Command.ping(), Command.parse("ping"));
        assertEquals(Command.set("user", "Mario"), Command.parse("  SET   user   Mario  "));
    }

    @Test
    void rejectsInvalidCommands() {
        IllegalArgumentException emptyCommand = assertThrows(
                IllegalArgumentException.class,
                () -> Command.parse("   ")
        );
        assertEquals("empty command", emptyCommand.getMessage());

        IllegalArgumentException wrongArguments = assertThrows(
                IllegalArgumentException.class,
                () -> Command.parse("GET")
        );
        assertEquals("wrong number of arguments for GET", wrongArguments.getMessage());

        IllegalArgumentException unknownCommand = assertThrows(
                IllegalArgumentException.class,
                () -> Command.parse("UNKNOWN x")
        );
        assertEquals("unknown command UNKNOWN", unknownCommand.getMessage());
    }

    @Test
    void convertsResponsesToText() {
        assertEquals("PONG", Response.text("PONG").toText());
        assertEquals("NOT_FOUND", Response.notFound().toText());
        assertEquals("1", Response.integer(1).toText());
        assertEquals("ERR unknown command UNKNOWN", Response.error("unknown command UNKNOWN").toText());
    }

    @Test
    void executesDatabaseOperations() {
        MiniDb db = new MiniDb();
        assertEquals("PONG", db.execute(Command.parse("PING")).toText());
        assertEquals("OK", db.execute(Command.parse("SET a 10")).toText());
        assertEquals("10", db.execute(Command.parse("GET a")).toText());
        assertEquals("NOT_FOUND", db.execute(Command.parse("GET missing")).toText());
        assertEquals("1", db.execute(Command.parse("EXISTS a")).toText());
        assertEquals("0", db.execute(Command.parse("EXISTS missing")).toText());
        assertEquals("1", db.execute(Command.parse("DEL a")).toText());
        assertEquals("0", db.execute(Command.parse("DEL a")).toText());
        assertEquals("1", db.execute(Command.parse("INCR counter")).toText());
        assertEquals("2", db.execute(Command.parse("INCR counter")).toText());
        assertEquals("OK", db.execute(Command.parse("SET name mario")).toText());
        assertEquals("ERR value is not an integer", db.execute(Command.parse("INCR name")).toText());
    }
}
