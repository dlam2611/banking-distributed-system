package ui;

import dto.RegisterDTO;
import model.Response;
import network.SocketClient;
import protocol.Status;
import ui.components.AlertBox;
import ui.components.CardPanel;
import ui.components.ModernButton;
import ui.components.ModernCheckBox;
import ui.components.ModernPasswordField;
import ui.components.ModernTextField;
import ui.components.VectorIcons;
import ui.theme.Theme;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class RegisterFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardsContainer = new JPanel(cardLayout);


    private ModernTextField fullNameField;
    private ModernTextField cccdField;
    private ModernTextField phoneField;
    private ModernPasswordField passwordField;
    private ModernCheckBox termsCheckBox;
    private ModernButton nextStepButton;
    private JLabel backToLoginLink1;


    private ModernTextField accountIdField;
    private ModernPasswordField pinField;
    private ModernPasswordField confirmPinField;
    private ModernButton submitRegisterButton;
    private ModernButton backToStep1Button;
    private JLabel backToLoginLink2;

    private int currentStep = 1;
    private final SocketClient socketClient = SocketClient.getInstance();

    public RegisterFrame() {
        setTitle("NexBank Digital Core - Mở tài khoản số");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(440, 880);
        setMinimumSize(new Dimension(400, 720));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BACKGROUND);

        initUI();
        initEventHandlers();
    }

    private void initUI() {

        cardsContainer.setOpaque(false);
        cardsContainer.add(createStep1Wrapper(), "STEP_1");
        cardsContainer.add(createStep2Wrapper(), "STEP_2");


        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Theme.BACKGROUND);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createStep1Wrapper() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 18));
        wrapper.setOpaque(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setPreferredSize(new Dimension(380, 780));
        content.setMaximumSize(new Dimension(380, 780));


        content.add(createStepperHeader(1, "1. Thông tin cá nhân", "2. Thông tin tài khoản"));
        content.add(Box.createVerticalStrut(14));


        content.add(createTitleSection("Mở tài khoản số", "Nhận ngay tài khoản số đẹp miễn phí chỉ trong 2 phút."));
        content.add(Box.createVerticalStrut(12));


        content.add(createFeaturePill("Định danh tự động 24/7 không cần đến quầy"));
        content.add(Box.createVerticalStrut(14));


        CardPanel card = new CardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 20, 18));
        card.setPreferredSize(new Dimension(380, 560));
        card.setMaximumSize(new Dimension(380, 560));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);


        card.add(createFieldLabel("HỌ VÀ TÊN (THEO CCCD)"));
        card.add(Box.createVerticalStrut(5));
        fullNameField = new ModernTextField("NGUYEN VAN AN", VectorIcons.createUserIcon(17, Theme.TEXT_MUTED));
        fullNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        fullNameField.setMaximumSize(new Dimension(344, 46));
        card.add(fullNameField);

        card.add(Box.createVerticalStrut(12));


        card.add(createFieldLabel("SỐ CCCD / CMND (12 CHỮ SỐ)"));
        card.add(Box.createVerticalStrut(5));
        cccdField = new ModernTextField("001203001234", VectorIcons.createCardIcon(17, Theme.TEXT_MUTED));
        cccdField.setAlignmentX(Component.CENTER_ALIGNMENT);
        cccdField.setMaximumSize(new Dimension(344, 46));
        card.add(cccdField);

        card.add(Box.createVerticalStrut(12));


        card.add(createFieldLabel("SỐ ĐIỆN THOẠI NHẬN OTP"));
        card.add(Box.createVerticalStrut(5));
        phoneField = new ModernTextField("0912345678", VectorIcons.createPhoneIcon(16, Theme.TEXT_MUTED));
        phoneField.setAlignmentX(Component.CENTER_ALIGNMENT);
        phoneField.setMaximumSize(new Dimension(344, 46));
        card.add(phoneField);

        card.add(Box.createVerticalStrut(12));


        JPanel passLabelRow = new JPanel(new BorderLayout());
        passLabelRow.setOpaque(false);
        passLabelRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        passLabelRow.setMaximumSize(new Dimension(344, 18));
        JLabel passLbl = new JLabel("MẬT KHẨU ĐĂNG NHẬP");
        passLbl.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 11));
        passLbl.setForeground(new Color(71, 85, 105));
        JLabel passBadge = new JLabel("🔒 256-bit");
        passBadge.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 10));
        passBadge.setForeground(Theme.ACCENT_TEAL);
        passLabelRow.add(passLbl, BorderLayout.WEST);
        passLabelRow.add(passBadge, BorderLayout.EAST);
        card.add(passLabelRow);

        card.add(Box.createVerticalStrut(5));
        passwordField = new ModernPasswordField("Tối thiểu 6 ký tự", VectorIcons.createLockIcon(16, Theme.TEXT_MUTED));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(344, 46));
        card.add(passwordField);

        card.add(Box.createVerticalStrut(14));


        AlertBox securityAlert = new AlertBox(
                "Mã hoá bảo mật 256-bit chuẩn PCI-DSS",
                "Thông tin được bảo mật tuyệt đối theo quy định Ngân hàng Nhà nước."
        );
        securityAlert.setAlignmentX(Component.CENTER_ALIGNMENT);
        securityAlert.setMaximumSize(new Dimension(344, 52));
        card.add(securityAlert);

        card.add(Box.createVerticalStrut(10));


        termsCheckBox = new ModernCheckBox("Tôi xác nhận thông tin trên là chính xác và đồng ý với điều khoản");
        termsCheckBox.setSelected(true);
        termsCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        termsCheckBox.setMaximumSize(new Dimension(344, 26));
        card.add(termsCheckBox);

        card.add(Box.createVerticalStrut(14));


        nextStepButton = ModernButton.createPrimary("Tiếp tục: Thông tin tài khoản");
        nextStepButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextStepButton.setMaximumSize(new Dimension(344, 46));
        card.add(nextStepButton);

        content.add(card);
        content.add(Box.createVerticalStrut(12));


        backToLoginLink1 = new JLabel("Đã có tài khoản NexBank? Đăng nhập ngay");
        backToLoginLink1.setFont(Theme.CAPTION_FONT);
        backToLoginLink1.setForeground(Theme.PRIMARY);
        backToLoginLink1.setCursor(Theme.HAND_CURSOR);
        backToLoginLink1.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(backToLoginLink1);

        wrapper.add(content);
        return wrapper;
    }

    private JPanel createStep2Wrapper() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 18));
        wrapper.setOpaque(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setPreferredSize(new Dimension(380, 780));
        content.setMaximumSize(new Dimension(380, 780));


        content.add(createStepperHeader(2, "✓ Thông tin cá nhân", "2. Thông tin tài khoản"));
        content.add(Box.createVerticalStrut(14));


        content.add(createTitleSection("Thiết lập tài khoản", "Chọn số tài khoản yêu thích và cài đặt mã PIN bảo mật."));
        content.add(Box.createVerticalStrut(12));


        content.add(createFeaturePill("Miễn phí chọn số tài khoản đẹp, lộc phát tự chọn"));
        content.add(Box.createVerticalStrut(14));


        CardPanel card = new CardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 20, 18));
        card.setPreferredSize(new Dimension(380, 560));
        card.setMaximumSize(new Dimension(380, 560));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);


        JPanel accRow = new JPanel(new BorderLayout());
        accRow.setOpaque(false);
        accRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        accRow.setMaximumSize(new Dimension(344, 18));
        JLabel accLabel = new JLabel("SỐ TÀI KHOẢN MONG MUỐN (6 - 20 SỐ)");
        accLabel.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 11));
        accLabel.setForeground(new Color(71, 85, 105));
        JLabel freeBadge = new JLabel("Miễn phí chọn");
        freeBadge.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 10));
        freeBadge.setForeground(Theme.PRIMARY);
        accRow.add(accLabel, BorderLayout.WEST);
        accRow.add(freeBadge, BorderLayout.EAST);
        card.add(accRow);

        card.add(Box.createVerticalStrut(5));
        accountIdField = new ModernTextField("88886666", VectorIcons.createCardIcon(17, Theme.TEXT_MUTED));
        accountIdField.setAlignmentX(Component.CENTER_ALIGNMENT);
        accountIdField.setMaximumSize(new Dimension(344, 46));
        card.add(accountIdField);

        card.add(Box.createVerticalStrut(6));


        JPanel quickButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        quickButtons.setOpaque(false);
        quickButtons.setAlignmentX(Component.CENTER_ALIGNMENT);
        quickButtons.setMaximumSize(new Dimension(344, 26));

        JButton btnPhone = createMiniSuggestButton("📱 Theo SĐT");
        btnPhone.addActionListener(e -> {
            String p = phoneField.getText().trim();
            if (!p.isEmpty()) accountIdField.setText(p);
        });

        JButton btnCccd = createMiniSuggestButton("🆔 Theo CCCD");
        btnCccd.addActionListener(e -> {
            String c = cccdField.getText().trim();
            if (!c.isEmpty()) accountIdField.setText(c);
        });

        JButton btnRandom = createMiniSuggestButton("🎲 Số phát lộc");
        btnRandom.addActionListener(e -> {
            String randAcc = "88" + (100000 + new Random().nextInt(900000));
            accountIdField.setText(randAcc);
        });

        quickButtons.add(btnPhone);
        quickButtons.add(btnCccd);
        quickButtons.add(btnRandom);
        card.add(quickButtons);

        card.add(Box.createVerticalStrut(12));


        JPanel pinRow = new JPanel(new BorderLayout());
        pinRow.setOpaque(false);
        pinRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        pinRow.setMaximumSize(new Dimension(344, 18));
        JLabel pinLabel = new JLabel("MÃ PIN GIAO DỊCH (ĐÚNG 6 CHỮ SỐ)");
        pinLabel.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 11));
        pinLabel.setForeground(new Color(71, 85, 105));
        JLabel otpBadge = new JLabel("🔒 Smart OTP");
        otpBadge.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 10));
        otpBadge.setForeground(Theme.ACCENT_TEAL);
        pinRow.add(pinLabel, BorderLayout.WEST);
        pinRow.add(otpBadge, BorderLayout.EAST);
        card.add(pinRow);

        card.add(Box.createVerticalStrut(5));
        pinField = new ModernPasswordField("Nhập 6 chữ số bí mật", VectorIcons.createLockIcon(16, Theme.TEXT_MUTED));
        pinField.setAlignmentX(Component.CENTER_ALIGNMENT);
        pinField.setMaximumSize(new Dimension(344, 46));
        card.add(pinField);

        card.add(Box.createVerticalStrut(12));


        card.add(createFieldLabel("XÁC NHẬN LẠI MÃ PIN (6 SỐ)"));
        card.add(Box.createVerticalStrut(5));
        confirmPinField = new ModernPasswordField("Nhập lại 6 chữ số mã PIN", VectorIcons.createLockIcon(16, Theme.TEXT_MUTED));
        confirmPinField.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmPinField.setMaximumSize(new Dimension(344, 46));
        card.add(confirmPinField);

        card.add(Box.createVerticalStrut(14));


        AlertBox pinAlert = new AlertBox(
                "Bảo mật mã PIN giao dịch",
                "Mã PIN dùng để xác thực khi chuyển tiền. Tuyệt đối không chia sẻ cho bất kỳ ai!"
        );
        pinAlert.setAlignmentX(Component.CENTER_ALIGNMENT);
        pinAlert.setMaximumSize(new Dimension(344, 52));
        card.add(pinAlert);

        card.add(Box.createVerticalStrut(14));


        submitRegisterButton = ModernButton.createPrimary("Hoàn tất mở tài khoản");
        submitRegisterButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitRegisterButton.setMaximumSize(new Dimension(344, 46));
        card.add(submitRegisterButton);

        card.add(Box.createVerticalStrut(8));


        backToStep1Button = ModernButton.createSecondary("← Quay lại bước 1");
        backToStep1Button.setAlignmentX(Component.CENTER_ALIGNMENT);
        backToStep1Button.setMaximumSize(new Dimension(344, 42));
        card.add(backToStep1Button);

        content.add(card);
        content.add(Box.createVerticalStrut(12));


        backToLoginLink2 = new JLabel("Đã có tài khoản NexBank? Đăng nhập ngay");
        backToLoginLink2.setFont(Theme.CAPTION_FONT);
        backToLoginLink2.setForeground(Theme.PRIMARY);
        backToLoginLink2.setCursor(Theme.HAND_CURSOR);
        backToLoginLink2.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(backToLoginLink2);

        wrapper.add(content);
        return wrapper;
    }



    private JPanel createStepperHeader(int step, String label1, String label2) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setPreferredSize(new Dimension(380, 56));
        header.setMaximumSize(new Dimension(380, 56));


        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        topRow.setMaximumSize(new Dimension(380, 20));

        JLabel stepIndicator = new JLabel("BƯỚC " + step + " TRÊN 2");
        stepIndicator.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 11));
        stepIndicator.setForeground(Theme.PRIMARY);

        JLabel badgeSecure = new JLabel("🛡️ Bảo mật cấp cao");
        badgeSecure.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 11));
        badgeSecure.setForeground(Theme.ACCENT_TEAL);

        topRow.add(stepIndicator, BorderLayout.WEST);
        topRow.add(badgeSecure, BorderLayout.EAST);
        header.add(topRow);

        header.add(Box.createVerticalStrut(6));


        JPanel barsRow = new JPanel(new GridLayout(1, 2, 8, 0));
        barsRow.setOpaque(false);
        barsRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        barsRow.setPreferredSize(new Dimension(380, 5));
        barsRow.setMaximumSize(new Dimension(380, 5));

        JPanel bar1 = createProgressBarSegment(true);
        JPanel bar2 = createProgressBarSegment(step >= 2);
        barsRow.add(bar1);
        barsRow.add(bar2);
        header.add(barsRow);

        header.add(Box.createVerticalStrut(5));


        JPanel labelRow = new JPanel(new BorderLayout());
        labelRow.setOpaque(false);
        labelRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelRow.setMaximumSize(new Dimension(380, 18));

        JLabel l1 = new JLabel(label1);
        l1.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 10));
        l1.setForeground(step >= 1 ? Theme.PRIMARY : Theme.TEXT_MUTED);

        JLabel l2 = new JLabel(label2);
        l2.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 10));
        l2.setForeground(step >= 2 ? Theme.PRIMARY : Theme.TEXT_MUTED);

        labelRow.add(l1, BorderLayout.WEST);
        labelRow.add(l2, BorderLayout.EAST);
        header.add(labelRow);

        return header;
    }

    private JPanel createProgressBarSegment(boolean active) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(active ? Theme.PRIMARY : new Color(226, 232, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.dispose();
            }
        };
    }



    private JPanel createTitleSection(String title, String subtitle) {
        JPanel section = new JPanel(new BorderLayout(12, 0));
        section.setOpaque(false);
        section.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.setPreferredSize(new Dimension(380, 52));
        section.setMaximumSize(new Dimension(380, 52));

        JPanel textCol = new JPanel();
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
        textCol.setOpaque(false);

        JLabel tLbl = new JLabel(title);
        tLbl.setFont(Theme.TITLE_FONT);
        tLbl.setForeground(Theme.TEXT_PRIMARY);

        JLabel sLbl = new JLabel(subtitle);
        sLbl.setFont(Theme.SUBTITLE_FONT);
        sLbl.setForeground(Theme.TEXT_MUTED);

        textCol.add(tLbl);
        textCol.add(Box.createVerticalStrut(2));
        textCol.add(sLbl);

        section.add(textCol, BorderLayout.CENTER);


        JPanel bankBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(new Color(235, 243, 254));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bankBadge.setOpaque(false);
        bankBadge.setPreferredSize(new Dimension(46, 46));
        bankBadge.setLayout(new BorderLayout());
        JLabel iconLbl = new JLabel(VectorIcons.createBankIcon(22, Theme.PRIMARY));
        iconLbl.setHorizontalAlignment(JLabel.CENTER);
        bankBadge.add(iconLbl, BorderLayout.CENTER);

        section.add(bankBadge, BorderLayout.EAST);
        return section;
    }



    private JPanel createFeaturePill(String text) {
        JPanel pill = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(Theme.ALERT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pill.setOpaque(false);
        pill.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 3));
        pill.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

        JLabel icon = new JLabel(VectorIcons.createLightningIcon(13, Theme.ACCENT_TEAL));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 10));
        lbl.setForeground(new Color(3, 105, 161));

        pill.add(icon);
        pill.add(lbl);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.setPreferredSize(new Dimension(380, 26));
        wrapper.setMaximumSize(new Dimension(380, 26));
        wrapper.add(pill);
        return wrapper;
    }

    private JLabel createFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 11));
        lbl.setForeground(new Color(71, 85, 105));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(344, 18));
        return lbl;
    }

    private JButton createMiniSuggestButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font(Theme.FONT_FAMILY, Font.PLAIN, 10));
        btn.setForeground(Theme.PRIMARY);
        btn.setBackground(Theme.PRIMARY_LIGHT);
        btn.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        btn.setFocusPainted(false);
        btn.setCursor(Theme.HAND_CURSOR);
        return btn;
    }



    private void initEventHandlers() {

        nextStepButton.addActionListener(e -> handleNextToStep2());


        backToStep1Button.addActionListener(e -> {
            currentStep = 1;
            cardLayout.show(cardsContainer, "STEP_1");
        });


        submitRegisterButton.addActionListener(e -> handleFinalRegister());


        MouseAdapter backToLoginAction = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                LoginFrame loginFrame = new LoginFrame();
                new controller.AuthController(loginFrame);
                loginFrame.setVisible(true);
            }
        };
        backToLoginLink1.addMouseListener(backToLoginAction);
        backToLoginLink2.addMouseListener(backToLoginAction);
    }



    private void handleNextToStep2() {
        String fullName = fullNameField.getText().trim();
        String cccd = cccdField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getPasswordString();

        if (fullName.isEmpty()) {
            showError("Vui lòng nhập Họ và tên (theo CCCD)!");
            fullNameField.getUnderlyingField().requestFocusInWindow();
            return;
        }

        if (!cccd.matches("^[0-9]{9,12}$")) {
            showError("Số CCCD không hợp lệ! Vui lòng nhập từ 9 đến 12 chữ số.");
            cccdField.getUnderlyingField().requestFocusInWindow();
            return;
        }

        if (!phone.matches("^[0-9]{10,11}$")) {
            showError("Số điện thoại không hợp lệ! Vui lòng nhập đúng 10 đến 11 chữ số.");
            phoneField.getUnderlyingField().requestFocusInWindow();
            return;
        }

        if (password.length() < 6) {
            showError("Mật khẩu phải có tối thiểu 6 ký tự!");
            passwordField.getUnderlyingField().requestFocusInWindow();
            return;
        }

        if (!termsCheckBox.isSelected()) {
            showError("Vui lòng tích đồng ý với Điều khoản dịch vụ để tiếp tục.");
            return;
        }


        if (accountIdField.getText().trim().isEmpty() || "88886666".equals(accountIdField.getText().trim())) {
            accountIdField.setText(phone);
        }


        currentStep = 2;
        cardLayout.show(cardsContainer, "STEP_2");
    }



    private void handleFinalRegister() {
        String accountId = accountIdField.getText().trim();
        String pin = pinField.getPasswordString().trim();
        String confirmPin = confirmPinField.getPasswordString().trim();

        if (!accountId.matches("^[0-9]{6,20}$")) {
            showError("Số tài khoản chỉ được chứa từ 6 đến 20 chữ số!");
            accountIdField.getUnderlyingField().requestFocusInWindow();
            return;
        }

        if (!pin.matches("^[0-9]{6}$")) {
            showError("Mã PIN giao dịch phải gồm đúng 6 chữ số!");
            pinField.getUnderlyingField().requestFocusInWindow();
            return;
        }

        if (!pin.equals(confirmPin)) {
            showError("Xác nhận mã PIN không khớp! Vui lòng kiểm tra lại.");
            confirmPinField.getUnderlyingField().requestFocusInWindow();
            return;
        }


        RegisterDTO dto = new RegisterDTO(
                accountId,
                fullNameField.getText().trim().toUpperCase(),
                cccdField.getText().trim(),
                phoneField.getText().trim(),
                passwordField.getPasswordString(),
                pin
        );


        submitRegisterButton.setEnabled(false);
        submitRegisterButton.setText("Đang xử lý mở tài khoản...");


        new Thread(() -> {
            try {
                Response response = socketClient.register(dto);

                SwingUtilities.invokeLater(() -> {
                    submitRegisterButton.setEnabled(true);
                    submitRegisterButton.setText("Hoàn tất mở tài khoản");

                    if (response != null && response.getStatus() == Status.SUCCESS) {

                        StringBuilder sb = new StringBuilder();
                        sb.append("🎉 CHÚC MỪNG BẠN ĐÃ MỞ TÀI KHOẢN THÀNH CÔNG!\n\n");
                        sb.append("• Họ và tên: ").append(dto.getFullName()).append("\n");
                        sb.append("• Số tài khoản: ").append(dto.getAccountId()).append("\n");
                        sb.append("• Số CCCD: ").append(dto.getCccd()).append("\n");
                        sb.append("• Số điện thoại: ").append(dto.getPhone()).append("\n");
                        sb.append("• Trạng thái: Kích hoạt thành công (Số dư khởi tạo: 0 VND)\n\n");
                        sb.append("Hệ thống sẽ chuyển bạn về màn hình Đăng nhập để sử dụng tài khoản.");

                        JOptionPane.showMessageDialog(RegisterFrame.this,
                                sb.toString(),
                                "Mở tài khoản thành công",
                                JOptionPane.INFORMATION_MESSAGE);


                        dispose();
                        LoginFrame loginFrame = new LoginFrame();
                        loginFrame.getCccdFieldComponent().setText(dto.getCccd());
                        loginFrame.getPasswordFieldComponent().setText(dto.getPassword());
                        new controller.AuthController(loginFrame);
                        loginFrame.setVisible(true);

                    } else {
                        String errMsg = (response != null && response.getMessage() != null)
                                ? response.getMessage()
                                : "Đăng ký không thành công. Vui lòng thử lại!";
                        showError(errMsg);
                    }
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    submitRegisterButton.setEnabled(true);
                    submitRegisterButton.setText("Hoàn tất mở tài khoản");
                    showError("Lỗi kết nối máy chủ: " + ex.getMessage());
                });
            }
        }).start();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            RegisterFrame frame = new RegisterFrame();
            frame.setVisible(true);
        });
    }
}
