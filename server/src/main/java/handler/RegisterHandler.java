package handler;

import dto.RegisterDTO;
import model.Request;
import model.Response;
import protocol.Status;
import service.AccountService;
import service.ServiceResult;
import util.JsonUtil;

public class RegisterHandler {

    private final AccountService accountService;

    public RegisterHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    public Response handle(Request request, String serverId) {
        if (request == null || request.getPayload() == null || request.getPayload().trim().isEmpty()) {
            Response res = Response.error(
                    request != null ? request.getRequestId() : null,
                    Status.INVALID_INPUT,
                    "Payload đăng ký không được để trống!"
            );
            res.setServerNodeId(serverId);
            return res;
        }

        try {
            RegisterDTO dto = JsonUtil.fromJson(request.getPayload(), RegisterDTO.class);
            if (dto == null) {
                Response res = Response.error(request.getRequestId(), Status.INVALID_INPUT, "Không thể đọc dữ liệu đăng ký!");
                res.setServerNodeId(serverId);
                return res;
            }

            ServiceResult result = accountService.register(dto);
            Response response;
            if (result.isSuccess()) {
                response = Response.success(request.getRequestId(), result.getMessage(), result.getData());
            } else {
                response = Response.error(request.getRequestId(), result.getStatus(), result.getMessage());
            }

            response.setServerNodeId(serverId);
            return response;

        } catch (Exception e) {
            System.err.println("[" + serverId + "] Lỗi xử lý RegisterHandler: " + e.getMessage());
            Response res = Response.error(request.getRequestId(), Status.INTERNAL_ERROR, "Lỗi máy chủ khi đăng ký: " + e.getMessage());
            res.setServerNodeId(serverId);
            return res;
        }
    }
}
