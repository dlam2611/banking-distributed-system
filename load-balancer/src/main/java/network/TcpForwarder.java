package network;

import model.Request;
import model.Response;
import model.ServerNode;
import protocol.Status;
import router.ConsistentHashStrategy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpForwarder {

    private final ConsistentHashStrategy routingStrategy;
    private final int timeoutMs;

    public TcpForwarder(ConsistentHashStrategy routingStrategy, int timeoutMs) {
        this.routingStrategy = routingStrategy;
        this.timeoutMs = timeoutMs;
    }

    public Response forward(Request request, ServerNode primaryNode) {
        if (primaryNode == null) {
            return Response.error(request != null ? request.getRequestId() : null,
                    Status.SERVER_UNAVAILABLE, "No server available");
        }

        Response response = send(request, primaryNode);
        if (response == null || response.getStatus() == Status.SERVER_UNAVAILABLE) {
            primaryNode.setHealthy(false);
            ServerNode backup = routingStrategy.selectNode(request);
            if (backup != null && !backup.equals(primaryNode)) {
                response = send(request, backup);
                if (response != null && response.getServerNodeId() == null) {
                    response.setServerNodeId(backup.getId());
                }
            }
        }
        return response != null ? response : Response.error(
                request != null ? request.getRequestId() : null,
                Status.SERVER_UNAVAILABLE, "Server unreachable");
    }

    private Response send(Request request, ServerNode node) {
        try (Socket socket = new Socket()) {
            socket.setSoTimeout(timeoutMs);
            socket.connect(new InetSocketAddress(node.getHost(), node.getPort()), timeoutMs);

            try (MessageWriter writer = new MessageWriter(socket.getOutputStream());
                 MessageReader reader = new MessageReader(socket.getInputStream())) {

                writer.writeRequest(request);
                Response response = reader.readResponse();
                if (response != null && response.getServerNodeId() == null) {
                    response.setServerNodeId(node.getId());
                }
                return response;
            }
        } catch (IOException e) {
            return null;
        }
    }
}
