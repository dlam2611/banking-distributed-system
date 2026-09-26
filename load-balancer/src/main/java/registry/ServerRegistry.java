package registry;

import model.ServerNode;
import router.RoutingStrategy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ServerRegistry {

    private final Map<String, ServerNode> serverMap = new ConcurrentHashMap<>();
    private final RoutingStrategy routingStrategy;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    public ServerRegistry(RoutingStrategy routingStrategy) {
        this.routingStrategy = routingStrategy;
    }

    public void register(ServerNode node) {
        if (node == null) return;
        serverMap.put(node.getId(), node);
        routingStrategy.addServer(node);
        System.out.println("Registered: " + node.getId() + " -> " + node.getAddress());
    }

    public void startHealthChecks(long intervalMs, int timeoutMs) {
        scheduler.scheduleWithFixedDelay(() -> {
            for (ServerNode node : serverMap.values()) {
                boolean reachable = ping(node.getHost(), node.getPort(), timeoutMs);
                if (reachable != node.isHealthy()) {
                    node.setHealthy(reachable);
                    System.out.println("Node " + node.getId() + " status changed: healthy=" + reachable);
                }
            }
        }, 1000, intervalMs, TimeUnit.MILLISECONDS);
    }

    private boolean ping(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<ServerNode> getAllServers() {
        return new ArrayList<>(serverMap.values());
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }
}
