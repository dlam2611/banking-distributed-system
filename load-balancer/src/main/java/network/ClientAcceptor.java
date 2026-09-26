package network;

import router.ConsistentHashStrategy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientAcceptor implements Runnable {

    private final int port;
    private final ConsistentHashStrategy routingStrategy;
    private final TcpForwarder tcpForwarder;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private ServerSocket serverSocket;

    public ClientAcceptor(int port, ConsistentHashStrategy routingStrategy, TcpForwarder tcpForwarder) {
        this.port = port;
        this.routingStrategy = routingStrategy;
        this.tcpForwarder = tcpForwarder;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running.set(true);
        new Thread(this, "LB-Acceptor").start();
        System.out.println("Load Balancer listening on port " + port);
    }

    @Override
    public void run() {
        while (running.get() && serverSocket != null && !serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(new ClientHandler(clientSocket, routingStrategy, tcpForwarder));
            } catch (IOException e) {
                if (!running.get()) break;
            }
        }
    }

    public void stop() {
        if (running.compareAndSet(true, false)) {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException ignored) {}
            threadPool.shutdownNow();
        }
    }
}
