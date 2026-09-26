package config;

import model.ServerNode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ServerConfig {
    private int port = 9000;
    private int serverPort = 8080;
    private final List<ServerNode> servers = new ArrayList<>();

    public ServerConfig() {
        Properties props = new Properties();
        try (InputStream in = getClass().getResourceAsStream("/config/server.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception ignored) {}

        this.port = Integer.parseInt(props.getProperty("port", "9000"));
        this.serverPort = Integer.parseInt(props.getProperty("server.port", "8080"));

        String ips = props.getProperty("server.ips", "127.0.0.1");
        String[] ipArray = ips.split(",");
        for (int i = 0; i < ipArray.length; i++) {
            String ip = ipArray[i].trim();
            if (!ip.isEmpty()) {
                if (ip.contains(":")) {
                    String[] parts = ip.split(":");
                    servers.add(new ServerNode("server-" + (i + 1), parts[0].trim(), Integer.parseInt(parts[1].trim())));
                } else {
                    servers.add(new ServerNode("server-" + (i + 1), ip, serverPort));
                }
            }
        }
    }

    public int getPort() {
        return port;
    }

    public int getServerPort() {
        return serverPort;
    }

    public List<ServerNode> getServers() {
        return servers;
    }
}
