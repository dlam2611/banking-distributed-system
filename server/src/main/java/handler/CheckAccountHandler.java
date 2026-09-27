package handler;

import model.Request;
import model.Response;
import protocol.Status;
import service.AccountService;
import service.ServiceResult;

public class CheckAccountHandler {

    private final AccountService accountService;

    public CheckAccountHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    public Response handle(Request request, String serverId) {
        if (request == null) {
            Response res = Response.error(null, Status.INVALID_INPUT, "Yêu cầu không hợp lệ!");
            res.setServerNodeId(serverId);
            return res;
        }

        String targetAccount = request.getAccountId();
        if ((targetAccount == null || targetAccount.trim().isEmpty()) && request.getPayload() != null) {
            targetAccount = request.getPayload().trim();
        }

        if (targetAccount == null || targetAccount.trim().isEmpty()) {
            Response res = Response.error(request.getRequestId(), Status.INVALID_INPUT, "Vui lòng cung cấp số tài khoản cần tra cứu!");
            res.setServerNodeId(serverId);
            return res;
        }

        try {
            ServiceResult result = accountService.lookupAccount(targetAccount);
            Response response;
            if (result.isSuccess()) {
                response = Response.success(request.getRequestId(), result.getMessage(), result.getData());
            } else {
                response = Response.error(request.getRequestId(), result.getStatus(), result.getMessage());
            }

            response.setServerNodeId(serverId);
            return response;
        } catch (Exception e) {
            System.err.println("[" + serverId + "] Lỗi xử lý CheckAccountHandler: " + e.getMessage());
            Response res = Response.error(request.getRequestId(), Status.INTERNAL_ERROR, "Lỗi máy chủ khi tra cứu tài khoản: " + e.getMessage());
            res.setServerNodeId(serverId);
            return res;
        }
    }
}
