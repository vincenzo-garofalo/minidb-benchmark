import java.util.Scanner;

/// Interfaccia di linea di comando per MiniDB.
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        MiniDb db = new MiniDb();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String inputLine = scanner.nextLine().trim();

            if (inputLine.equalsIgnoreCase("QUIT") || inputLine.equalsIgnoreCase("EXIT")) {
                break;
            }

            Response response;
            try {
                response = db.execute(Command.parse(inputLine));
            } catch (IllegalArgumentException error) {
                response = Response.error(error.getMessage());
            }

            System.out.println(response.toText());
        }
    }
}
