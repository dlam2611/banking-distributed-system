import handler.LoginHandler;
import handler.RegisterHandler;
import handler.TransferHandler;
import model.Request;
import model.Response;
import network.MessageReader;
import network.MessageWriter;
import protocol.Command;
import protocol.Status;
import service.AccountService;
import service.TransferService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BankServer {

    private static final AccountService accountService = new AccountService();
    private static final TransferService transferService = new TransferService();

    private static final RegisterHandler registerHandler = new RegisterHandler(accountService);
    private static final LoginHandler loginHandler = new LoginHandler(accountService);
    private static final TransferHandler transferHandler = new TransferHandler(transferService);

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

                System.out.println("[" + serverId + "] " + clientAddr + " -> " + request.getCommand() + " (Key: " + request.getRoutingKey() + ")");

                Response response;

                if (request.getCommand() == null) {
                    response = Response.error(request.getRequestId(), Status.INVALID_INPUT, "Thiếu trường command trong request!");
                    response.setServerNodeId(serverId);
                    writer.writeResponse(response);
                    continue;
                }

                switch (request.getCommand()) {
                    case REGISTER:
                        response = registerHandler.handle(request, serverId);
                        break;

                    case LOGIN:
                        response = loginHandler.handle(request, serverId);
                        break;

                    case TRANSFER:
                        response = transferHandler.handle(request, serverId);
                        break;

                    case PING:
                        response = Response.success(request.getRequestId(), "PONG", null);
                        response.setServerNodeId(serverId);
                        break;

                    case LOGOUT:
                        response = Response.success(request.getRequestId(), "Đăng xuất thành công!", null);
                        response.setServerNodeId(serverId);
                        writer.writeResponse(response);
                        return; // Đóng session client

                    default:
                        response = Response.error(request.getRequestId(), Status.INVALID_INPUT, "Lệnh không được hỗ trợ: " + request.getCommand());
                        response.setServerNodeId(serverId);
                        break;
                }

                writer.writeResponse(response);
            }
        } catch (IOException ignored) {
        }
    }
}
