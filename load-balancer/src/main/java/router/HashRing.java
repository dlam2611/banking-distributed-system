package router;

import model.ServerNode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HashRing {

    private final HashFunction hashFunction;
    private final int virtualNodes;
    private final TreeMap<Long, ServerNode> ring = new TreeMap<>();
    private final Set<ServerNode> physicalNodes = ConcurrentHashMap.newKeySet();
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public HashRing(int virtualNodes) {
        this.hashFunction = new HashFunction();
        this.virtualNodes = Math.max(1, virtualNodes);
    }

    public void addNode(ServerNode node) {
        if (node == null) return;
        rwLock.writeLock().lock();
        try {
            physicalNodes.add(node);
            for (int i = 0; i < virtualNodes; i++) {
                long hash = hashFunction.hash(node.getId() + "#VN-" + i);
                ring.put(hash, node);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void removeNode(ServerNode node) {
        if (node == null) return;
        rwLock.writeLock().lock();
        try {
            physicalNodes.remove(node);
            for (int i = 0; i < virtualNodes; i++) {
                long hash = hashFunction.hash(node.getId() + "#VN-" + i);
                if (node.equals(ring.get(hash))) {
                    ring.remove(hash);
                }
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public ServerNode getNode(String routingKey) {
        if (routingKey == null) {
            routingKey = "DEFAULT";
        }

        rwLock.readLock().lock();
        try {
            if (ring.isEmpty()) {
                return null;
            }

            long hash = hashFunction.hash(routingKey);
            SortedMap<Long, ServerNode> tailMap = ring.tailMap(hash);
            Long targetKey = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
            ServerNode primary = ring.get(targetKey);

            if (primary != null && primary.isHealthy()) {
                return primary;
            }

            for (Map.Entry<Long, ServerNode> entry : tailMap.entrySet()) {
                if (entry.getValue().isHealthy()) {
                    return entry.getValue();
                }
            }

            for (Map.Entry<Long, ServerNode> entry : ring.entrySet()) {
                if (entry.getValue().isHealthy()) {
                    return entry.getValue();
                }
            }

            return primary;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public Set<ServerNode> getPhysicalNodes() {
        return Collections.unmodifiableSet(physicalNodes);
    }

    public int getRingSize() {
        rwLock.readLock().lock();
        try {
            return ring.size();
        } finally {
            rwLock.readLock().unlock();
        }
    }
}
