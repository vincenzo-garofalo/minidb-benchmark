import java.util.Objects;

/// Rappresenta un comando MiniDB gia validato.
public final class Command {
    private final String name;
    private final String key;
    private final String value;
    private final Long seconds;

    private Command(String name, String key, String value, Long seconds) {
        this.name = name;
        this.key = key;
        this.value = value;
        this.seconds = seconds;
    }

    public static Command ping() {
        return new Command("PING", null, null, null);
    }

    public static Command set(String key, String value) {
        return new Command("SET", key, value, null);
    }

    public static Command get(String key) {
        return new Command("GET", key, null, null);
    }

    public static Command del(String key) {
        return new Command("DEL", key, null, null);
    }

    public static Command exists(String key) {
        return new Command("EXISTS", key, null, null);
    }

    public static Command incr(String key) {
        return new Command("INCR", key, null, null);
    }

    public static Command expire(String key, long seconds) {
        return new Command("EXPIRE", key, null, seconds);
    }

    public static Command ttl(String key) {
        return new Command("TTL", key, null, null);
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
            case "EXPIRE":
                requireArgumentCount(parts, 3, "EXPIRE");
                return expire(parts[1], parseExpireSeconds(parts[2]));
            case "TTL":
                requireArgumentCount(parts, 2, "TTL");
                return ttl(parts[1]);
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

    /// Metodo helper per convertire un valore (String) di scadenza in secondi (long).
    private static long parseExpireSeconds(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("invalid expire seconds");
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

    public Long seconds() {
        return seconds;
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
                && Objects.equals(value, command.value)
                && Objects.equals(seconds, command.seconds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, key, value, seconds);
    }

    @Override
    public String toString() {
        return "Command{name='" + name + "', key='" + key + "', value='" + value + "', seconds=" + seconds + "}";
    }
}
