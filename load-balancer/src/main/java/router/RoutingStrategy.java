package router;

import model.Request;
import model.ServerNode;

import java.util.List;

public interface RoutingStrategy {
    ServerNode selectNode(Request request);
    void addServer(ServerNode server);
    void removeServer(ServerNode server);
    List<ServerNode> getAllServers();
}
