import config.ServerConfig;
import model.ServerNode;
import network.ClientAcceptor;
import network.TcpForwarder;
import registry.ServerRegistry;
import router.ConsistentHashStrategy;

import java.io.IOException;

public class LoadBalancerApplication {

    public static void main(String[] args) {
        ServerConfig config = new ServerConfig();

        ConsistentHashStrategy routingStrategy = new ConsistentHashStrategy();
        ServerRegistry registry = new ServerRegistry(routingStrategy);

        for (ServerNode node : config.getServers()) {
            registry.register(node);
        }

        registry.startHealthChecks(5000, 2000);

        TcpForwarder forwarder = new TcpForwarder(routingStrategy, 10000);
        ClientAcceptor acceptor = new ClientAcceptor(config.getPort(), routingStrategy, forwarder);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            acceptor.stop();
            registry.shutdown();
        }));

        try {
            acceptor.start();
        } catch (IOException e) {
            System.err.println("Failed to start Load Balancer: " + e.getMessage());
            registry.shutdown();
            System.exit(1);
        }
    }
}
