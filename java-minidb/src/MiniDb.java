import java.util.HashMap;
import java.util.Map;

/// Database key-value in memoria per MiniDB.
public final class MiniDb {
    private final Map<String, String> data = new HashMap<>();

    /// Esegue un comando e restituisce la risposta corrispondente.
    public Response execute(Command command) {
        switch (command.name()) {
            case "PING":
                return Response.text("PONG");
            case "SET":
                data.put(command.key(), command.value());
                return Response.text("OK");
            case "GET":
                String value = data.get(command.key());
                if (value == null) {
                    return Response.notFound();
                }
                return Response.text(value);
            case "DEL":
                if (data.remove(command.key()) != null) {
                    return Response.integer(1);
                }
                return Response.integer(0);
            case "EXISTS":
                return Response.integer(data.containsKey(command.key()) ? 1 : 0);
            case "INCR":
                return incr(command.key());
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
}
