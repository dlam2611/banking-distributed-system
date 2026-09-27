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
        InputStream in = getClass().getResourceAsStream("/config/server.properties");
        if (in == null) {
            in = getClass().getResourceAsStream("/server.properties");
        }
        if (in == null) {
            in = getClass().getClassLoader().getResourceAsStream("config/server.properties");
        }
        if (in != null) {
            try (InputStream stream = in) {
                props.load(stream);
            } catch (Exception ignored) {}
        }

        this.port = Integer.parseInt(props.getProperty("port", "9000").trim());
        this.serverPort = Integer.parseInt(props.getProperty("server.port", "8080").trim());

        // Danh sách các địa chỉ IP của 3 server (không cần truyền port, tự động dùng serverPort 8080)
        String ips = props.getProperty("server.ips", "192.168.1.6,192.168.1.7,192.168.1.8").trim();
        String[] ipArray = ips.split(",");
        for (int i = 0; i < ipArray.length; i++) {
            String item = ipArray[i].trim();
            if (!item.isEmpty()) {
                String ip = item;
                int port = this.serverPort; // Cùng port
                if (item.contains(":")) {
                    String[] parts = item.split(":");
                    ip = parts[0].trim();
                    try {
                        port = Integer.parseInt(parts[1].trim());
                    } catch (Exception ignored) {}
                }
                servers.add(new ServerNode("server-" + (i + 1), ip, port));
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
