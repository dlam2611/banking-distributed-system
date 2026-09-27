import controller.AuthController;
import model.Request;
import model.Response;
import network.MessageReader;
import network.MessageWriter;
import protocol.Command;
import ui.LoginFrame;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ClientApplication {

    public static void main(String[] args) {
        if (args.length > 0 && "--cli".equalsIgnoreCase(args[0])) {
            runCli(args);
        } else {
            launchGui();
        }
    }

    public static void launchGui() {
        try {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            new AuthController(loginFrame);
            loginFrame.setVisible(true);
        });
    }

    private static void runCli(String[] args) {
        String host = args.length > 1 ? args[1] : "127.0.0.1";
        int port = args.length > 2 ? Integer.parseInt(args[2]) : 9000;

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

                Request request = new Request(Command.TRANSFER, text);
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
