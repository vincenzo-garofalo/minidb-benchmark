import java.util.HashMap;
import java.util.Map;

/// Database key-value in memoria per MiniDB.
public final class MiniDb {
    private final Map<String, String> data = new HashMap<>();       // chiave -> valore
    private final Map<String, Long> expiresAt = new HashMap<>();    // chiave -> scadenza

    /// Esegue un comando e restituisce la risposta corrispondente.
    public Response execute(Command command) {
        switch (command.name()) {
            case "PING":
                return Response.text("PONG");
            case "SET":
                data.put(command.key(), command.value());
                expiresAt.remove(command.key());
                return Response.text("OK");
            case "GET":
                removeIfExpired(command.key());
                String value = data.get(command.key());
                if (value == null) {
                    return Response.notFound();
                }
                return Response.text(value);
            case "DEL":
                removeIfExpired(command.key());
                if (data.remove(command.key()) != null) {
                    expiresAt.remove(command.key());
                    return Response.integer(1);
                }
                return Response.integer(0);
            case "EXISTS":
                removeIfExpired(command.key());
                return Response.integer(data.containsKey(command.key()) ? 1 : 0);
            case "INCR":
                removeIfExpired(command.key());
                return incr(command.key());
            case "EXPIRE":
                return expire(command.key(), command.seconds());
            case "TTL":
                return ttl(command.key());
            default:
                return Response.error("unknown command " + command.name());
        }
    }

    /// Metodo helper per incrementare il valore associato a una chiave (comando INCR).
    private Response incr(String key) {
        String currentValue = data.get(key);
        long nextValue;
        if (currentValue == null) {
            nextValue = 1;
        } else {
            try {
                nextValue = Long.parseLong(currentValue) + 1;
            } catch (NumberFormatException error) {
                return Response.error("value is not an integer");
            }
        }
        data.put(key, Long.toString(nextValue));
        return Response.integer(nextValue);
    }

    /// Metodo helper per impostare la scadenza di una chiave (comando EXPIRE).
    private Response expire(String key, long seconds) {
        removeIfExpired(key);
        if (!data.containsKey(key)) {
            return Response.integer(0);
        }
        if (seconds <= 0) {
            data.remove(key);
            expiresAt.remove(key);
        } else {
            expiresAt.put(key, System.nanoTime() + seconds * 1_000_000_000L);
        }
        return Response.integer(1);
    }

    /// Metodo helper per leggere i secondi rimanenti prima della scadenza (comando TTL).
    private Response ttl(String key) {
        removeIfExpired(key);
        if (!data.containsKey(key)) {
            return Response.integer(-2);
        }
        Long expiresAtNanos = expiresAt.get(key);
        if (expiresAtNanos == null) {
            return Response.integer(-1);
        }
        long remainingNanos = Math.max(0, expiresAtNanos - System.nanoTime());
        return Response.integer(remainingNanos / 1_000_000_000L);
    }

    /// Metodo helper per rimuovere una chiave se scaduta.
    private void removeIfExpired(String key) {
        Long expiresAtNanos = expiresAt.get(key);
        if (expiresAtNanos != null && System.nanoTime() >= expiresAtNanos) {
            data.remove(key);
            expiresAt.remove(key);
        }
    }
}
