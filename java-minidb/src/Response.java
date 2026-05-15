import java.util.Objects;

/// Risposta testuale restituita dai comandi MiniDB.
public final class Response {
    private enum Kind {
        TEXT,
        NOT_FOUND,
        INTEGER,
        ERROR
    }

    private final Kind kind;
    private final String textValue;
    private final Long integerValue;

    private Response(Kind kind, String textValue, Long integerValue) {
        this.kind = kind;
        this.textValue = textValue;
        this.integerValue = integerValue;
    }

    public static Response text(String value) {
        return new Response(Kind.TEXT, value, null);
    }

    public static Response notFound() {
        return new Response(Kind.NOT_FOUND, null, null);
    }

    public static Response integer(long value) {
        return new Response(Kind.INTEGER, null, value);
    }

    public static Response error(String message) {
        return new Response(Kind.ERROR, message, null);
    }

    /// Converte la risposta nel testo da mostrare all'utente.
    public String toText() {
        switch (kind) {
            case TEXT:
                return textValue;
            case NOT_FOUND:
                return "NOT_FOUND";
            case INTEGER:
                return Long.toString(integerValue);
            case ERROR:
                return "ERR " + textValue;
            default:
                throw new IllegalStateException("unknown response kind " + kind);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Response)) {
            return false;
        }
        Response response = (Response) other;
        return kind == response.kind
                && Objects.equals(textValue, response.textValue)
                && Objects.equals(integerValue, response.integerValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, textValue, integerValue);
    }

    @Override
    public String toString() {
        return toText();
    }
}
