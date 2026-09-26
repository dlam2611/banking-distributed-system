import model.Request;
import model.Response;
import network.MessageReader;
import network.MessageWriter;
import protocol.Command;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientApplication {

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 9000;

        System.out.println("Connecting to Load Balancer at " + host + ":" + port + "...");

        try (Socket socket = new Socket(host, port);
             MessageReader reader = new MessageReader(socket.getInputStream());
             MessageWriter writer = new MessageWriter(socket.getOutputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to Load Balancer!");
            System.out.println("Type an accountId or message to send (or 'exit' to quit):");

            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();
                if (input == null || input.trim().equalsIgnoreCase("exit")) {
                    break;
                }

                String text = input.trim();
                if (text.isEmpty()) continue;

                Request request = new Request(Command.BALANCE, text);
                request.setPayload(text);

                writer.writeRequest(request);

                Response response = reader.readResponse();
                if (response != null) {
                    System.out.println("<<< [" + response.getServerNodeId() + "]: " + response.getMessage());
                } else {
                    System.out.println("<<< No response from Load Balancer");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Client connection error: " + e.getMessage());
        }
    }
}
