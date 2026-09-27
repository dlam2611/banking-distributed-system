package ui;

import ui.components.AlertBox;
import ui.components.CardPanel;
import ui.components.ModernButton;
import ui.components.ModernCheckBox;
import ui.components.ModernPasswordField;
import ui.components.ModernTextField;
import ui.components.VectorIcons;
import ui.theme.Theme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * NexBank Digital Core - Modern SaaS Login UI.
 * Implements the exact design specs with deep emerald theme, rounded inputs,
 * high-security badges, and CCCD-based authentication.
 */
public class LoginFrame extends JFrame {

    private ModernTextField cccdField;
    private ModernPasswordField passwordField;
    private ModernCheckBox rememberCheckBox;
    private ModernButton loginButton;
    private ModernButton biometricButton;
    private JLabel forgotPasswordLink;
    private JLabel registerLink;

    public LoginFrame() {
        setTitle("NexBank Digital Core - Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 860);
        setMinimumSize(new Dimension(400, 720));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BACKGROUND);

        initUI();
    }

    private void initUI() {
        // Main container panel with background color
        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
        rootPanel.setBackground(Theme.BACKGROUND);
        rootPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 28, 24));

        // 1. Top Emblem Section (Emerald Circle + Floating Lock Badge)
        rootPanel.add(createHeaderEmblem());
        rootPanel.add(Box.createVerticalStrut(14));

        // 2. Pill Badge ("• NEXBANK DIGITAL CORE")
        rootPanel.add(createPillBadge());
        rootPanel.add(Box.createVerticalStrut(12));

        // 3. Welcome Title & Subtitle
        JLabel titleLabel = new JLabel("Chào mừng trở lại!");
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        rootPanel.add(titleLabel);

        rootPanel.add(Box.createVerticalStrut(6));

        JLabel subLabel1 = new JLabel("Đăng nhập vào tài khoản ngân hàng số an toàn");
        subLabel1.setFont(Theme.SUBTITLE_FONT);
        subLabel1.setForeground(Theme.TEXT_MUTED);
        subLabel1.setAlignmentX(CENTER_ALIGNMENT);
        rootPanel.add(subLabel1);

        JLabel subLabel2 = new JLabel("của bạn");
        subLabel2.setFont(Theme.SUBTITLE_FONT);
        subLabel2.setForeground(Theme.TEXT_MUTED);
        subLabel2.setAlignmentX(CENTER_ALIGNMENT);
        rootPanel.add(subLabel2);

        rootPanel.add(Box.createVerticalStrut(16));

        // 4. High Security Alert Banner
        AlertBox alertBox = new AlertBox("Phiên làm việc bảo mật cao", "Kết nối mã hoá bảo vệ kép tới NexVault");
        alertBox.setAlignmentX(CENTER_ALIGNMENT);
        alertBox.setMaximumSize(new Dimension(380, 56));
        rootPanel.add(alertBox);

        rootPanel.add(Box.createVerticalStrut(18));

        // 5. Main Card Form (White container)
        CardPanel cardPanel = createFormCard();
        cardPanel.setAlignmentX(CENTER_ALIGNMENT);
        cardPanel.setMaximumSize(new Dimension(380, 390));
        rootPanel.add(cardPanel);

        rootPanel.add(Box.createVerticalStrut(18));

        // 6. Registration Prompt Link
        rootPanel.add(createRegisterPrompt());
        rootPanel.add(Box.createVerticalStrut(16));

        // 7. Security Certifications Line (PCI-DSS, AES-256, SBV)
        rootPanel.add(createComplianceBadges());
        rootPanel.add(Box.createVerticalStrut(10));

        // 8. Disclaimer Text
        rootPanel.add(createDisclaimer());

        // Wrap rootPanel in a sleek borderless JScrollPane for small screens
        JScrollPane scrollPane = new JScrollPane(rootPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Theme.BACKGROUND);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Top Emerald Shield Emblem with mini floating Lock badge.
     */
    private JPanel createHeaderEmblem() {
        JPanel emblemPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);

                int w = getWidth();
                int h = getHeight();

                // Main Circle: Emerald Green
                int mainSize = 58;
                int mx = (w - mainSize) / 2;
                int my = 2;
                g2.setColor(Theme.PRIMARY);
                g2.fillOval(mx, my, mainSize, mainSize);

                // White Shield inside main circle
                Icon shieldIcon = VectorIcons.createShieldIcon(26, Color.WHITE);
                int sx = mx + (mainSize - shieldIcon.getIconWidth()) / 2;
                int sy = my + (mainSize - shieldIcon.getIconHeight()) / 2 - 1;
                shieldIcon.paintIcon(this, g2, sx, sy);

                // Small Lock Badge floating at bottom-right of circle
                int badgeSize = 22;
                int bx = mx + mainSize - 17;
                int by = my + mainSize - 18;

                // Badge border & fill
                g2.setColor(new Color(235, 240, 247));
                g2.fillOval(bx, by, badgeSize, badgeSize);
                g2.setColor(Color.WHITE);
                g2.fillOval(bx + 1, by + 1, badgeSize - 2, badgeSize - 2);

                // Mini lock icon
                Icon lockIcon = VectorIcons.createLockIcon(12, new Color(59, 130, 246));
                lockIcon.paintIcon(this, g2, bx + 5, by + 5);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        emblemPanel.setOpaque(false);
        emblemPanel.setPreferredSize(new Dimension(100, 64));
        emblemPanel.setMaximumSize(new Dimension(100, 64));
        emblemPanel.setAlignmentX(CENTER_ALIGNMENT);
        return emblemPanel;
    }

    /**
     * Capsule Badge: "• NEXBANK DIGITAL CORE"
     */
    private JPanel createPillBadge() {
        JPanel pill = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(Theme.PILL_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_PILL, Theme.RADIUS_PILL);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pill.setOpaque(false);
        pill.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
        pill.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

        // Green bullet dot
        JLabel dotLabel = new JLabel("•");
        dotLabel.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 14));
        dotLabel.setForeground(Theme.PRIMARY);

        // Text
        JLabel textLabel = new JLabel("NEXBANK DIGITAL CORE");
        textLabel.setFont(Theme.BADGE_FONT);
        textLabel.setForeground(new Color(71, 85, 105)); // #475569

        pill.add(dotLabel);
        pill.add(textLabel);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(pill);
        wrapper.setAlignmentX(CENTER_ALIGNMENT);
        return wrapper;
    }

    /**
     * White Main Form Card containing CCCD input, Password input, Checkbox, and Action buttons.
     */
    private CardPanel createFormCard() {
        CardPanel card = new CardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 22, 20));

        // 1. Label: Số CCCD / Tên đăng nhập
        JLabel cccdLabel = new JLabel("Số CCCD / Tên đăng nhập");
        cccdLabel.setFont(Theme.LABEL_FONT);
        cccdLabel.setForeground(Theme.TEXT_SECONDARY);
        cccdLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(cccdLabel);

        card.add(Box.createVerticalStrut(7));

        // CCCD Input Field (Replacing phone number as requested)
        cccdField = new ModernTextField("001203012345", VectorIcons.createUserIcon(17, Theme.TEXT_MUTED));
        cccdField.setText("001203012345"); // Sample CCCD number matching style in mockup
        cccdField.setAlignmentX(LEFT_ALIGNMENT);
        cccdField.setMaximumSize(new Dimension(340, 46));
        card.add(cccdField);

        card.add(Box.createVerticalStrut(14));

        // 2. Password Label Row (Label + "🔒 256-bit" security badge)
        JPanel passwordLabelRow = new JPanel(new BorderLayout());
        passwordLabelRow.setOpaque(false);
        passwordLabelRow.setAlignmentX(LEFT_ALIGNMENT);
        passwordLabelRow.setMaximumSize(new Dimension(340, 20));

        JLabel passLabel = new JLabel("Mật khẩu");
        passLabel.setFont(Theme.LABEL_FONT);
        passLabel.setForeground(Theme.TEXT_SECONDARY);
        passwordLabelRow.add(passLabel, BorderLayout.WEST);

        // Right side badge: Lock icon + 256-bit
        JPanel bitBadge = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        bitBadge.setOpaque(false);
        JLabel bitLockIcon = new JLabel(VectorIcons.createLockIcon(12, Theme.ACCENT_TEAL));
        JLabel bitText = new JLabel("256-bit");
        bitText.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 11));
        bitText.setForeground(Theme.ACCENT_TEAL);
        bitBadge.add(bitLockIcon);
        bitBadge.add(bitText);
        passwordLabelRow.add(bitBadge, BorderLayout.EAST);

        card.add(passwordLabelRow);

        card.add(Box.createVerticalStrut(7));

        // Password Input Field
        passwordField = new ModernPasswordField("Nhập mật khẩu của bạn", VectorIcons.createLockIcon(16, Theme.TEXT_MUTED));
        passwordField.setText("BankSecure@2026"); // Mock password for visual fidelity
        passwordField.setAlignmentX(LEFT_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(340, 46));
        card.add(passwordField);

        card.add(Box.createVerticalStrut(12));

        // 3. Options Row: Remember Me & Forgot Password
        JPanel optionsRow = new JPanel(new BorderLayout());
        optionsRow.setOpaque(false);
        optionsRow.setAlignmentX(LEFT_ALIGNMENT);
        optionsRow.setMaximumSize(new Dimension(340, 24));

        rememberCheckBox = new ModernCheckBox("Ghi nhớ đăng nhập");
        rememberCheckBox.setSelected(true);
        optionsRow.add(rememberCheckBox, BorderLayout.WEST);

        forgotPasswordLink = new JLabel("Quên mật khẩu?");
        forgotPasswordLink.setFont(Theme.LABEL_FONT);
        forgotPasswordLink.setForeground(Theme.PRIMARY);
        forgotPasswordLink.setCursor(Theme.HAND_CURSOR);
        optionsRow.add(forgotPasswordLink, BorderLayout.EAST);

        card.add(optionsRow);

        card.add(Box.createVerticalStrut(18));

        // 4. Primary Button: "Đăng nhập an toàn →"
        loginButton = ModernButton.createPrimary("Đăng nhập an toàn");
        loginButton.setAlignmentX(LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(340, 48));
        card.add(loginButton);

        card.add(Box.createVerticalStrut(10));

        // 5. Secondary Button: "Đăng nhập bằng Face ID / Vân tay"
        biometricButton = ModernButton.createBiometric("Đăng nhập bằng Face ID / Vân tay");
        biometricButton.setAlignmentX(LEFT_ALIGNMENT);
        biometricButton.setMaximumSize(new Dimension(340, 46));
        card.add(biometricButton);

        return card;
    }

    /**
     * Registration Prompt: "Chưa có tài khoản NexBank? Đăng ký ngay ↗"
     */
    private JPanel createRegisterPrompt() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel text = new JLabel("Chưa có tài khoản NexBank?");
        text.setFont(Theme.CAPTION_FONT);
        text.setForeground(Theme.TEXT_MUTED);

        registerLink = new JLabel("Đăng ký ngay");
        registerLink.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 11));
        registerLink.setForeground(Theme.PRIMARY);
        registerLink.setCursor(Theme.HAND_CURSOR);

        JLabel linkIcon = new JLabel(VectorIcons.createExternalLinkIcon(12, Theme.PRIMARY));
        linkIcon.setCursor(Theme.HAND_CURSOR);

        panel.add(text);
        panel.add(registerLink);
        panel.add(linkIcon);
        return panel;
    }

    /**
     * Security and Compliance Badges:
     * PCI-DSS LEVEL 1  •  AES-256 BIT  •  SBV COMPLIANT
     */
    private JPanel createComplianceBadges() {
        JPanel badgesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        badgesPanel.setOpaque(false);
        badgesPanel.setAlignmentX(CENTER_ALIGNMENT);

        // Item 1: PCI-DSS LEVEL 1
        JPanel pciItem = createBadgeItem(
                VectorIcons.createShieldCheckIcon(13, Theme.ACCENT_TEAL),
                "PCI-DSS LEVEL 1"
        );

        // Dot separator
        JLabel dot1 = new JLabel("•");
        dot1.setFont(new Font(Theme.FONT_FAMILY, Font.PLAIN, 12));
        dot1.setForeground(new Color(203, 213, 225));

        // Item 2: AES-256 BIT
        JPanel aesItem = createBadgeItem(
                VectorIcons.createLockIcon(12, Theme.ACCENT_TEAL),
                "AES-256 BIT"
        );

        // Dot separator
        JLabel dot2 = new JLabel("•");
        dot2.setFont(new Font(Theme.FONT_FAMILY, Font.PLAIN, 12));
        dot2.setForeground(new Color(203, 213, 225));

        // Item 3: SBV COMPLIANT
        JPanel sbvItem = createBadgeItem(
                VectorIcons.createShieldCheckIcon(13, Theme.ACCENT_TEAL),
                "SBV COMPLIANT"
        );

        badgesPanel.add(pciItem);
        badgesPanel.add(dot1);
        badgesPanel.add(aesItem);
        badgesPanel.add(dot2);
        badgesPanel.add(sbvItem);

        return badgesPanel;
    }

    private JPanel createBadgeItem(Icon icon, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        item.setOpaque(false);

        JLabel iconLbl = new JLabel(icon);
        JLabel textLbl = new JLabel(text);
        textLbl.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 9));
        textLbl.setForeground(Theme.TEXT_MUTED);

        item.add(iconLbl);
        item.add(textLbl);
        return item;
    }

    /**
     * Real-time monitoring disclaimer.
     */
    private JPanel createDisclaimer() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel line1 = new JLabel("Mọi phiên giao dịch đều được giám sát thời gian thực");
        line1.setFont(Theme.FOOTER_FONT);
        line1.setForeground(new Color(148, 163, 184)); // #94A3B8
        line1.setAlignmentX(CENTER_ALIGNMENT);

        JLabel line2 = new JLabel("bởi Trung tâm Phòng ngừa Gian lận NexGuard.");
        line2.setFont(Theme.FOOTER_FONT);
        line2.setForeground(new Color(148, 163, 184));
        line2.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(line1);
        panel.add(Box.createVerticalStrut(2));
        panel.add(line2);
        return panel;
    }

    // === Getters for Future Logic Binding ===

    public String getCccd() {
        return cccdField.getText().trim();
    }

    public String getPassword() {
        return passwordField.getPasswordString();
    }

    public boolean isRememberMe() {
        return rememberCheckBox.isSelected();
    }

    public ModernTextField getCccdFieldComponent() {
        return cccdField;
    }

    public ModernPasswordField getPasswordFieldComponent() {
        return passwordField;
    }

    public ModernButton getLoginButton() {
        return loginButton;
    }

    public ModernButton getBiometricButton() {
        return biometricButton;
    }

    public JLabel getForgotPasswordLink() {
        return forgotPasswordLink;
    }

    public JLabel getRegisterLink() {
        return registerLink;
    }

    /**
     * Standalone main method to preview the interface and run with AuthController.
     */
    public static void main(String[] args) {
        try {
            // Enable native system anti-aliasing hints
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            new controller.AuthController(frame);
            frame.setVisible(true);
        });
    }
}
