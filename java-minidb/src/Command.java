import java.util.Objects;

/// Rappresenta un comando MiniDB gia validato.
public final class Command {
    private final String name;
    private final String key;
    private final String value;

    private Command(String name, String key, String value) {
        this.name = name;
        this.key = key;
        this.value = value;
    }

    public static Command ping() {
        return new Command("PING", null, null);
    }

    public static Command set(String key, String value) {
        return new Command("SET", key, value);
    }

    public static Command get(String key) {
        return new Command("GET", key, null);
    }

    public static Command del(String key) {
        return new Command("DEL", key, null);
    }

    public static Command exists(String key) {
        return new Command("EXISTS", key, null);
    }

    public static Command incr(String key) {
        return new Command("INCR", key, null);
    }

    /// Converte una riga di testo in un comando MiniDB.
    public static Command parse(String inputLine) {
        String[] parts = inputLine.trim().split("\\s+");

        if (parts.length == 1 && parts[0].isEmpty()) {
            throw new IllegalArgumentException("empty command");
        }

        String commandName = parts[0].toUpperCase();

        switch (commandName) {
            case "PING":
                requireArgumentCount(parts, 1, "PING");
                return ping();
            case "SET":
                requireArgumentCount(parts, 3, "SET");
                return set(parts[1], parts[2]);
            case "GET":
                requireArgumentCount(parts, 2, "GET");
                return get(parts[1]);
            case "DEL":
                requireArgumentCount(parts, 2, "DEL");
                return del(parts[1]);
            case "EXISTS":
                requireArgumentCount(parts, 2, "EXISTS");
                return exists(parts[1]);
            case "INCR":
                requireArgumentCount(parts, 2, "INCR");
                return incr(parts[1]);
            default:
                throw new IllegalArgumentException("unknown command " + commandName);
        }
    }

    /// Metodo helper per verificare il numero corretto di argomenti per un comando specifico.
    private static void requireArgumentCount(String[] parts, int expected, String commandName) {
        if (parts.length != expected) {
            throw new IllegalArgumentException("wrong number of arguments for " + commandName);
        }
    }

    public String name() {
        return name;
    }

    public String key() {
        return key;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Command)) {
            return false;
        }
        Command command = (Command) other;
        return Objects.equals(name, command.name)
                && Objects.equals(key, command.key)
                && Objects.equals(value, command.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, key, value);
    }

    @Override
    public String toString() {
        return "Command{name='" + name + "', key='" + key + "', value='" + value + "'}";
    }
}
