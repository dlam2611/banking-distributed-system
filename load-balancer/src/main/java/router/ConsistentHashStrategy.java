package router;

import model.Request;
import model.ServerNode;

import java.util.ArrayList;
import java.util.List;

public class ConsistentHashStrategy implements RoutingStrategy {

    private final HashRing hashRing;

    public ConsistentHashStrategy() {
        this(150);
    }

    public ConsistentHashStrategy(int virtualNodes) {
        this.hashRing = new HashRing(virtualNodes);
    }

    @Override
    public ServerNode selectNode(Request request) {
        String key = request != null ? request.getRoutingKey() : "DEFAULT";
        return hashRing.getNode(key);
    }

    @Override
    public void addServer(ServerNode server) {
        if (server != null) {
            hashRing.addNode(server);
        }
    }

    @Override
    public void removeServer(ServerNode server) {
        if (server != null) {
            hashRing.removeNode(server);
        }
    }

    @Override
    public List<ServerNode> getAllServers() {
        return new ArrayList<>(hashRing.getPhysicalNodes());
    }

    public HashRing getHashRing() {
        return hashRing;
    }
}
