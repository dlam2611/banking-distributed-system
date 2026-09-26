package model;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerNode implements Serializable, Comparable<ServerNode> {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String host;
    private final int port;
    private final AtomicBoolean healthy = new AtomicBoolean(true);

    public ServerNode(String id, String host, int port) {
        this.id = Objects.requireNonNull(id);
        this.host = Objects.requireNonNull(host);
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isHealthy() {
        return healthy.get();
    }

    public void setHealthy(boolean healthy) {
        this.healthy.set(healthy);
    }

    public String getAddress() {
        return host + ":" + port;
    }

    @Override
    public int compareTo(ServerNode o) {
        return this.id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerNode that)) return false;
        return port == that.port && Objects.equals(id, that.id) && Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, host, port);
    }

    @Override
    public String toString() {
        return id + "(" + getAddress() + ")";
    }
}
