package network;

import model.Request;
import model.Response;
import model.ServerNode;
import protocol.Command;
import protocol.Status;
import router.ConsistentHashStrategy;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final ConsistentHashStrategy routingStrategy;
    private final TcpForwarder tcpForwarder;

    public ClientHandler(Socket clientSocket, ConsistentHashStrategy routingStrategy, TcpForwarder tcpForwarder) {
        this.clientSocket = clientSocket;
        this.routingStrategy = routingStrategy;
        this.tcpForwarder = tcpForwarder;
    }

    @Override
    public void run() {
        try (MessageReader reader = new MessageReader(clientSocket.getInputStream());
             MessageWriter writer = new MessageWriter(clientSocket.getOutputStream())) {

            while (!clientSocket.isClosed()) {
                Request request = reader.readRequest();
                if (request == null) {
                    break;
                }

                if (request.getCommand() == Command.PING && "LB".equalsIgnoreCase(request.getAccountId())) {
                    Response pong = Response.success(request.getRequestId(), "PONG", null);
                    pong.setServerNodeId("LB");
                    writer.writeResponse(pong);
                    continue;
                }

                ServerNode target = routingStrategy.selectNode(request);
                if (target == null) {
                    writer.writeResponse(Response.error(request.getRequestId(),
                            Status.SERVER_UNAVAILABLE, "No server available"));
                    continue;
                }

                Response response = tcpForwarder.forward(request, target);
                writer.writeResponse(response);

                if (request.getCommand() == Command.LOGOUT) {
                    break;
                }
            }
        } catch (IOException ignored) {
        } finally {
            try {
                if (!clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException ignored) {}
        }
    }
}
