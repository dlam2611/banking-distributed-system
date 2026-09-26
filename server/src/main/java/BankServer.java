import model.Request;
import model.Response;
import network.MessageReader;
import network.MessageWriter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BankServer {

    public static void main(String[] args) {
        String serverId = args.length > 0 ? args[0] : "server-1";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 8080;

        ExecutorService threadPool = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Bank Server [" + serverId + "] listening on port " + port);

            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                threadPool.submit(() -> handleClient(socket, serverId));
            }
        } catch (IOException e) {
            System.err.println("Server [" + serverId + "] error: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket, String serverId) {
        String clientAddr = socket.getRemoteSocketAddress().toString();
        try (socket;
             MessageReader reader = new MessageReader(socket.getInputStream());
             MessageWriter writer = new MessageWriter(socket.getOutputStream())) {

            while (!socket.isClosed()) {
                Request request = reader.readRequest();
                if (request == null) break;

                System.out.println("[" + serverId + "] Client " + clientAddr + " -> Command: " + request.getCommand() + " | Key: " + request.getRoutingKey() + " | Data: " + request.getPayload());

                Response response = Response.success(
                        request.getRequestId(),
                        "Hello from " + serverId + "! Handled: " + request.getRoutingKey(),
                        request.getPayload()
                );
                response.setServerNodeId(serverId);

                writer.writeResponse(response);
            }
        } catch (IOException ignored) {}
    }
}
