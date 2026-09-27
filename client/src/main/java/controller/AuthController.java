package controller;

import dto.LoginDTO;
import model.Account;
import model.Response;
import network.SocketClient;
import protocol.Status;
import ui.LoginFrame;
import util.JsonUtil;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Controller xử lý logic Đăng nhập và các luồng tương tác trên giao diện NexBank.
 */
public class AuthController {

    private final LoginFrame loginFrame;
    private final SocketClient socketClient;

    public AuthController(LoginFrame loginFrame) {
        this(loginFrame, SocketClient.getInstance());
    }

    public AuthController(LoginFrame loginFrame, SocketClient socketClient) {
        this.loginFrame = loginFrame;
        this.socketClient = socketClient;
        initHandlers();
    }

    private void initHandlers() {
        // 1. Xử lý nút "Đăng nhập an toàn →"
        loginFrame.getLoginButton().addActionListener(e -> performLogin());

        // 2. Hỗ trợ nhấn Enter trong ô CCCD và Mật khẩu để đăng nhập nhanh
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        loginFrame.getCccdFieldComponent().getUnderlyingField().addKeyListener(enterKeyAdapter);
        loginFrame.getPasswordFieldComponent().getUnderlyingField().addKeyListener(enterKeyAdapter);

        // 3. Xử lý nút "Đăng nhập bằng Face ID / Vân tay"
        loginFrame.getBiometricButton().addActionListener(e -> handleBiometricLogin());

        // 4. Xử lý liên kết "Quên mật khẩu?"
        loginFrame.getForgotPasswordLink().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JOptionPane.showMessageDialog(loginFrame,
                        "Hệ thống khôi phục mật khẩu trực tuyến qua CCCD đang được nâng cấp bảo mật NexGuard.",
                        "Quên mật khẩu",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 5. Xử lý liên kết "Đăng ký ngay ↗"
        loginFrame.getRegisterLink().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                loginFrame.dispose();
                ui.RegisterFrame registerFrame = new ui.RegisterFrame();
                registerFrame.setVisible(true);
            }
        });
    }

    /**
     * Logic thực hiện Đăng nhập kết nối với máy chủ backend.
     */
    private void performLogin() {
        String cccd = loginFrame.getCccd();
        String password = loginFrame.getPassword();
        boolean remember = loginFrame.isRememberMe();

        // 1. Kiểm tra tính hợp lệ dữ liệu đầu vào
        if (cccd.isEmpty()) {
            JOptionPane.showMessageDialog(loginFrame,
                    "Vui lòng nhập Số CCCD hoặc Tên đăng nhập!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            loginFrame.getCccdFieldComponent().getUnderlyingField().requestFocusInWindow();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(loginFrame,
                    "Vui lòng nhập Mật khẩu tài khoản!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            loginFrame.getPasswordFieldComponent().getUnderlyingField().requestFocusInWindow();
            return;
        }

        // 2. Chuyển nút sang trạng thái đang xử lý (loading)
        loginFrame.getLoginButton().setEnabled(false);
        loginFrame.getLoginButton().setText("Đang xác thực bảo mật...");

        // 3. Gửi request đăng nhập trong luồng riêng biệt để không chặn giao diện (non-blocking UI)
        new Thread(() -> {
            try {
                Response response = socketClient.login(cccd, password);

                SwingUtilities.invokeLater(() -> {
                    // Khôi phục trạng thái nút bấm
                    loginFrame.getLoginButton().setEnabled(true);
                    loginFrame.getLoginButton().setText("Đăng nhập an toàn");

                    if (response != null && response.getStatus() == Status.SUCCESS) {
                        // Đăng nhập THÀNH CÔNG -> Vào thẳng Trang chủ ngay lập tức
                        Account account = null;
                        if (response.getData() != null && !response.getData().isEmpty()) {
                            try {
                                account = JsonUtil.fromJson(response.getData(), Account.class);
                            } catch (Exception ignored) {
                            }
                        }

                        loginFrame.dispose();
                        new ui.HomeFrame(account).setVisible(true);

                    } else {
                        // Đăng nhập THẤT BẠI
                        String errMsg = (response != null && response.getMessage() != null)
                                ? response.getMessage()
                                : "Đăng nhập không thành công. Vui lòng thử lại!";

                        JOptionPane.showMessageDialog(loginFrame,
                                errMsg,
                                "Đăng nhập thất bại",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    loginFrame.getLoginButton().setEnabled(true);
                    loginFrame.getLoginButton().setText("Đăng nhập an toàn");

                    JOptionPane.showMessageDialog(loginFrame,
                            "Lỗi khi thực hiện đăng nhập: " + ex.getMessage(),
                            "Lỗi hệ thống",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void handleBiometricLogin() {
        JOptionPane.showMessageDialog(loginFrame,
                "Đang kiểm tra mô-đun sinh trắc học Face ID / Vân tay qua thiết bị xác thực bảo mật NexGuard...",
                "Xác thực sinh trắc học",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
