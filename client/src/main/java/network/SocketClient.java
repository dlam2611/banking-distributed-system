package network;

import dto.LoginDTO;
import model.Request;
import model.Response;
import protocol.Command;
import protocol.Status;
import util.JsonUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Socket Client for communicating with NexBank Load Balancer (port 9000)
 * with automatic failover to direct Backend Server (port 8080).
 */
public class SocketClient {

    private String host = "127.0.0.1";
    private int primaryPort = 9000;    // Load Balancer
    private int fallbackPort = 8080;   // Direct Server fallback
    private int timeoutMs = 5000;

    private static SocketClient instance;

    public SocketClient() {}

    public SocketClient(String host, int port) {
        this.host = host;
        this.primaryPort = port;
    }

    public static synchronized SocketClient getInstance() {
        if (instance == null) {
            instance = new SocketClient();
        }
        return instance;
    }

    /**
     * Gửi request lên hệ thống và nhận response tương ứng.
     * Tự động thử qua Load Balancer (9000), nếu không kết nối được sẽ thử kết nối thẳng Server (8080).
     */
    public Response send(Request request) {
        if (request == null) {
            return Response.error(null, Status.INVALID_REQUEST, "Yêu cầu không được để trống!");
        }

        // 1. Thử gửi qua Load Balancer trước
        try {
            return executeSocketCall(host, primaryPort, request);
        } catch (IOException e1) {
            System.out.println("Không thể kết nối Load Balancer tại " + host + ":" + primaryPort + " (" + e1.getMessage() + "). Đang thử kết nối trực tiếp Server " + host + ":" + fallbackPort + "...");
            // 2. Thử fallback qua Server trực tiếp
            try {
                return executeSocketCall(host, fallbackPort, request);
            } catch (IOException e2) {
                System.err.println("Lỗi kết nối tới cả Load Balancer và Server: " + e2.getMessage());
                return Response.error(
                        request.getRequestId(),
                        Status.SERVER_UNAVAILABLE,
                        "Không thể kết nối đến máy chủ ngân hàng! Vui lòng kiểm tra lại dịch vụ (Load Balancer hoặc Server)."
                );
            }
        }
    }

    /**
     * Thực hiện gửi request đăng nhập bằng CCCD và Mật khẩu.
     */
    public Response login(String cccd, String password) {
        LoginDTO dto = new LoginDTO(cccd, password);
        String payloadJson = JsonUtil.toJson(dto);

        Request request = new Request(Command.LOGIN, cccd);
        request.setAccountId(cccd);
        request.setPayload(payloadJson);

        return send(request);
    }

    /**
     * Thực hiện gửi request ĐĂNG KÝ tài khoản mới.
     */
    public Response register(dto.RegisterDTO dto) {
        String payloadJson = JsonUtil.toJson(dto);

        Request request = new Request(Command.REGISTER, dto.getCccd());
        request.setAccountId(dto.getAccountId());
        request.setPayload(payloadJson);

        return send(request);
    }

    private Response executeSocketCall(String targetHost, int targetPort, Request request) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(targetHost, targetPort), timeoutMs);
            socket.setSoTimeout(timeoutMs);

            MessageWriter writer = new MessageWriter(socket.getOutputStream());
            MessageReader reader = new MessageReader(socket.getInputStream());

            writer.writeRequest(request);

            Response response = reader.readResponse();
            if (response == null) {
                throw new IOException("Không nhận được phản hồi từ máy chủ!");
            }
            return response;
        }
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPrimaryPort(int primaryPort) {
        this.primaryPort = primaryPort;
    }

    public void setFallbackPort(int fallbackPort) {
        this.fallbackPort = fallbackPort;
    }
}
