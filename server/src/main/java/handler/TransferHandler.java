package handler;

import dto.TransferDTO;
import model.Request;
import model.Response;
import protocol.Status;
import service.ServiceResult;
import service.TransferService;
import util.JsonUtil;

public class TransferHandler {

    private final TransferService transferService;

    public TransferHandler(TransferService transferService) {
        this.transferService = transferService;
    }

    public Response handle(Request request, String serverId) {
        if (request == null) {
            Response res = Response.error(null, Status.INVALID_INPUT, "Request không hợp lệ!");
            res.setServerNodeId(serverId);
            return res;
        }

        String fromAccount = request.getAccountId();
        if (fromAccount == null || fromAccount.trim().isEmpty()) {
            Response res = Response.error(request.getRequestId(), Status.INVALID_INPUT, "Thiếu số tài khoản người gửi!");
            res.setServerNodeId(serverId);
            return res;
        }

        if (request.getPayload() == null || request.getPayload().trim().isEmpty()) {
            Response res = Response.error(request.getRequestId(), Status.INVALID_INPUT, "Payload chuyển tiền không được để trống!");
            res.setServerNodeId(serverId);
            return res;
        }

        try {
            TransferDTO dto = JsonUtil.fromJson(request.getPayload(), TransferDTO.class);
            if (dto == null) {
                Response res = Response.error(request.getRequestId(), Status.INVALID_INPUT, "Không thể đọc thông tin giao dịch chuyển tiền!");
                res.setServerNodeId(serverId);
                return res;
            }

            ServiceResult result = transferService.transfer(fromAccount, dto);
            Response response;
            if (result.isSuccess()) {
                response = Response.success(request.getRequestId(), result.getMessage(), result.getData());
            } else {
                response = Response.error(request.getRequestId(), result.getStatus(), result.getMessage());
            }

            response.setServerNodeId(serverId);
            return response;

        } catch (Exception e) {
            System.err.println("[" + serverId + "] Lỗi xử lý TransferHandler: " + e.getMessage());
            Response res = Response.error(request.getRequestId(), Status.INTERNAL_ERROR, "Lỗi máy chủ khi chuyển tiền: " + e.getMessage());
            res.setServerNodeId(serverId);
            return res;
        }
    }
}
