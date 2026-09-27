package network;

import dto.LoginDTO;
import dto.RegisterDTO;
import dto.TransferDTO;
import model.Request;
import model.Response;
import model.Transaction;
import protocol.Command;
import protocol.Status;
import util.JsonUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketClient {

    private String host = "127.0.0.1";
    private int primaryPort = 9000;
    private int fallbackPort = 8080;
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



    public Response send(Request request) {
        if (request == null) {
            return Response.error(null, Status.INVALID_REQUEST, "Yêu cầu không được để trống!");
        }


        try {
            return executeSocketCall(host, primaryPort, request);
        } catch (IOException e1) {
            System.out.println("Không thể kết nối Load Balancer tại " + host + ":" + primaryPort + " (" + e1.getMessage() + "). Đang thử kết nối trực tiếp Server " + host + ":" + fallbackPort + "...");

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



    public Response login(String cccd, String password) {
        LoginDTO dto = new LoginDTO(cccd, password);
        String payloadJson = JsonUtil.toJson(dto);

        Request request = new Request(Command.LOGIN, cccd);
        request.setAccountId(cccd);
        request.setPayload(payloadJson);

        return send(request);
    }



    public Response register(dto.RegisterDTO dto) {
        String payloadJson = JsonUtil.toJson(dto);

        Request request = new Request(Command.REGISTER, dto.getCccd());
        request.setAccountId(dto.getAccountId());
        request.setPayload(payloadJson);

        return send(request);
    }



    public Response checkAccount(String targetAccount) {
        if (targetAccount == null || targetAccount.trim().isEmpty()) {
            return Response.error(null, Status.INVALID_INPUT, "Số tài khoản không được để trống!");
        }
        String cleanAcc = targetAccount.trim();
        Request request = new Request(Command.CHECK_ACCOUNT, cleanAcc);
        request.setAccountId(cleanAcc);
        request.setPayload(cleanAcc);
        return send(request);
    }



    public Response transfer(String fromAccount, TransferDTO dto) {
        if (fromAccount == null || dto == null) {
            return Response.error(null, Status.INVALID_INPUT, "Thông tin chuyển tiền không hợp lệ!");
        }
        String payloadJson = JsonUtil.toJson(dto);
        Request request = new Request(Command.TRANSFER, fromAccount);
        request.setAccountId(fromAccount);
        request.setPayload(payloadJson);
        return send(request);
    }



    public List<Transaction> getRecentTransactions(String accountId) {
        List<Transaction> list = new ArrayList<>();
        if (accountId == null || accountId.trim().isEmpty()) {
            return list;
        }

        Request request = new Request(Command.GET_TRANSACTIONS, accountId);
        request.setAccountId(accountId);
        request.setPayload(accountId);
        Response response = send(request);

        if (response != null && response.getStatus() == Status.SUCCESS && response.getData() != null) {
            String json = response.getData().trim();
            if (json.startsWith("[") && json.endsWith("]")) {
                json = json.substring(1, json.length() - 1).trim();
                if (!json.isEmpty()) {
                    String[] items = json.split("(?<=\\}),\\s*(?=\\{)");
                    for (String item : items) {
                        try {
                            Transaction tx = JsonUtil.fromJson(item, Transaction.class);
                            if (tx != null) {
                                list.add(tx);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }
        return list;
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
