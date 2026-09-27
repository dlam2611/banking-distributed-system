package ui;

import dto.TransferDTO;
import model.Account;
import model.Response;
import model.Transaction;
import network.SocketClient;
import protocol.Status;
import ui.components.CardPanel;
import ui.components.ModernButton;
import ui.components.ModernPasswordField;
import ui.components.ModernTextField;
import ui.components.VectorIcons;
import ui.theme.Theme;
import util.JsonUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TransferFrame extends JFrame {

    private final Account account;
    private final HomeFrame parentHome;
    private final SocketClient socketClient = SocketClient.getInstance();


    private CardPanel recipientCard;
    private ModernTextField toAccountField;
    private JPanel recipientStatusPanel;
    private JLabel recipientStatusLabel;
    private JLabel recipientStatusIcon;

    private ModernTextField amountField;
    private JLabel amountInWordsLabel;
    private ModernTextField descriptionField;
    private ModernPasswordField pinField;
    private ModernButton submitButton;


    private String verifiedTargetAccount = null;
    private String verifiedRecipientName = null;
    private boolean isRecipientValid = false;
    private String lastCheckedInput = null;

    public TransferFrame(Account account, HomeFrame parentHome) {
        this.account = account != null ? account : new Account();
        this.parentHome = parentHome;

        setTitle("NexBank Digital - Chuyển tiền");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(430, 700);
        setMinimumSize(new Dimension(380, 600));
        setLocationRelativeTo(parentHome);
        getContentPane().setBackground(Theme.BACKGROUND);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());


        add(createTopNavBar(), BorderLayout.NORTH);


        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(10, 16, 16, 16));


        content.setFocusable(true);
        content.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                content.requestFocusInWindow();
            }
        });


        content.add(createSourceAccountCard());
        content.add(Box.createVerticalStrut(8));


        content.add(createRecipientCard());
        content.add(Box.createVerticalStrut(8));


        content.add(createTransferDetailsCard());
        content.add(Box.createVerticalStrut(8));


        content.add(createSecurityPinCard());
        content.add(Box.createVerticalStrut(12));


        submitButton = ModernButton.createPrimary("Xác nhận chuyển tiền");
        submitButton.setPreferredSize(new Dimension(380, 46));
        submitButton.setMaximumSize(new Dimension(380, 46));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> executeTransfer());
        content.add(submitButton);

        content.add(Box.createVerticalStrut(8));
        content.add(createSecurityBadge());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Theme.BACKGROUND);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }



    private JPanel createTopNavBar() {
        JPanel nav = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(226, 232, 240));
                g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        nav.setBackground(Color.WHITE);
        nav.setPreferredSize(new Dimension(430, 48));
        nav.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));


        JPanel backBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 10));
        backBtn.setOpaque(false);
        backBtn.setCursor(Theme.HAND_CURSOR);

        JLabel backIcon = new JLabel(VectorIcons.createArrowLeftIcon(18, Theme.TEXT_PRIMARY));
        JLabel backText = new JLabel("Quay lại");
        backText.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 13));
        backText.setForeground(Theme.TEXT_PRIMARY);

        backBtn.add(backIcon);
        backBtn.add(backText);
        backBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                closeAndReturnHome();
            }
        });


        JLabel titleLbl = new JLabel("Chuyển tiền nội bộ 24/7", SwingConstants.CENTER);
        titleLbl.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 15));
        titleLbl.setForeground(Theme.TEXT_PRIMARY);


        JPanel rightSpacer = new JPanel();
        rightSpacer.setOpaque(false);
        rightSpacer.setPreferredSize(new Dimension(75, 48));

        nav.add(backBtn, BorderLayout.WEST);
        nav.add(titleLbl, BorderLayout.CENTER);
        nav.add(rightSpacer, BorderLayout.EAST);

        return nav;
    }



    private JPanel createSourceAccountCard() {
        CardPanel card = new CardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        card.setPreferredSize(new Dimension(380, 84));
        card.setMaximumSize(new Dimension(380, 84));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);


        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel srcTitle = new JLabel("TÀI KHOẢN TRÍCH TIỀN");
        srcTitle.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 10));
        srcTitle.setForeground(Theme.TEXT_MUTED);

        JLabel brandBadge = new JLabel("NexBank Platinum");
        brandBadge.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 10));
        brandBadge.setForeground(Theme.PRIMARY);

        top.add(srcTitle, BorderLayout.WEST);
        top.add(brandBadge, BorderLayout.EAST);


        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        String displayAcc = account.getAccountId() != null ? account.getAccountId() : "---";
        JLabel accLbl = new JLabel(displayAcc);
        accLbl.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 14));
        accLbl.setForeground(Theme.TEXT_PRIMARY);

        BigDecimal bal = account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO;
        JLabel balLbl = new JLabel("Số dư: " + formatCurrency(bal));
        balLbl.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 13));
        balLbl.setForeground(new Color(5, 150, 105));

        bottom.add(accLbl, BorderLayout.WEST);
        bottom.add(balLbl, BorderLayout.EAST);

        card.add(top);
        card.add(Box.createVerticalStrut(6));
        card.add(bottom);

        return card;
    }



    private JPanel createRecipientCard() {
        recipientCard = new CardPanel();
        recipientCard.setLayout(new BoxLayout(recipientCard, BoxLayout.Y_AXIS));
        recipientCard.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        recipientCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel("TÀI KHOẢN THỤ HƯỞNG");
        label.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 10));
        label.setForeground(Theme.TEXT_MUTED);
        recipientCard.add(label);
        recipientCard.add(Box.createVerticalStrut(5));


        toAccountField = new ModernTextField("Nhập số tài khoản nhận", VectorIcons.createCardIcon(17, Theme.TEXT_MUTED));
        toAccountField.setPreferredSize(new Dimension(352, 44));
        toAccountField.setMaximumSize(new Dimension(352, 44));


        toAccountField.getUnderlyingField().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                autoCheckRecipientAccount();
            }
        });


        toAccountField.getUnderlyingField().addActionListener(e -> {
            autoCheckRecipientAccount();
            amountField.getUnderlyingField().requestFocusInWindow();
        });


        toAccountField.getUnderlyingField().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { onTextChanged(); }
            @Override public void removeUpdate(DocumentEvent e) { onTextChanged(); }
            @Override public void changedUpdate(DocumentEvent e) { onTextChanged(); }

            private void onTextChanged() {
                String currentText = toAccountField.getText().trim();
                if (!currentText.equals(lastCheckedInput)) {
                    isRecipientValid = false;
                    verifiedTargetAccount = null;
                    verifiedRecipientName = null;
                }
            }
        });

        recipientCard.add(toAccountField);
        recipientCard.add(Box.createVerticalStrut(6));


        recipientStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        recipientStatusPanel.setOpaque(false);
        recipientStatusPanel.setPreferredSize(new Dimension(352, 32));
        recipientStatusPanel.setMaximumSize(new Dimension(352, 32));
        recipientStatusPanel.setVisible(false);

        recipientStatusIcon = new JLabel();
        recipientStatusLabel = new JLabel();
        recipientStatusLabel.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 12));

        recipientStatusPanel.add(recipientStatusIcon);
        recipientStatusPanel.add(recipientStatusLabel);

        recipientCard.add(recipientStatusPanel);

        return recipientCard;
    }



    private void autoCheckRecipientAccount() {
        String inputAcc = toAccountField.getText().trim();


        if (inputAcc.isEmpty()) {
            recipientStatusPanel.setVisible(false);
            isRecipientValid = false;
            verifiedTargetAccount = null;
            verifiedRecipientName = null;
            lastCheckedInput = null;
            if (recipientCard != null) {
                recipientCard.revalidate();
                recipientCard.repaint();
            }
            return;
        }


        if (inputAcc.equals(lastCheckedInput) && recipientStatusPanel.isVisible()) {
            return;
        }


        if (inputAcc.equalsIgnoreCase(account.getAccountId())) {
            showStatusBanner(
                    VectorIcons.createWarningIcon(14, new Color(180, 83, 9)),
                    "Không thể tự chuyển tiền cho chính mình",
                    new Color(254, 243, 199),
                    new Color(180, 83, 9)
            );
            isRecipientValid = false;
            verifiedTargetAccount = null;
            verifiedRecipientName = null;
            lastCheckedInput = inputAcc;
            return;
        }

        lastCheckedInput = inputAcc;


        showStatusBanner(
                VectorIcons.createShieldIcon(14, new Color(2, 132, 199)),
                "Đang kiểm tra...",
                new Color(240, 249, 255),
                new Color(2, 132, 199)
        );


        new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() {
                return socketClient.checkAccount(inputAcc);
            }

            @Override
            protected void done() {
                try {
                    Response response = get();
                    if (response != null && response.getStatus() == Status.SUCCESS && response.getData() != null) {
                        Account target = JsonUtil.fromJson(response.getData(), Account.class);
                        if (target != null && target.getFullName() != null && !target.getFullName().trim().isEmpty()) {

                            String foundName = target.getFullName().trim().toUpperCase();
                            verifiedTargetAccount = inputAcc;
                            verifiedRecipientName = foundName;
                            isRecipientValid = true;

                            showStatusBanner(
                                    VectorIcons.createCheckIcon(14, new Color(6, 95, 70)),
                                    foundName,
                                    new Color(236, 253, 245),
                                    new Color(6, 95, 70)
                            );
                            return;
                        }
                    }


                    showRecipientNotFound();

                } catch (Exception ex) {
                    showRecipientNotFound();
                }
            }
        }.execute();
    }

    private void showRecipientNotFound() {
        isRecipientValid = false;
        verifiedTargetAccount = null;
        verifiedRecipientName = null;

        showStatusBanner(
                VectorIcons.createWarningIcon(14, new Color(220, 38, 38)),
                "Tài khoản không tồn tại",
                new Color(254, 242, 242),
                new Color(220, 38, 38)
        );
    }

    private void showStatusBanner(Icon icon, String text, Color bgColor, Color textColor) {
        recipientStatusPanel.setBackground(bgColor);
        recipientStatusIcon.setIcon(icon);
        recipientStatusLabel.setText(text);
        recipientStatusLabel.setForeground(textColor);
        recipientStatusPanel.setVisible(true);
        if (recipientCard != null) {
            recipientCard.revalidate();
            recipientCard.repaint();
        }
    }



    private JPanel createTransferDetailsCard() {
        CardPanel card = new CardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);


        JLabel amtTitle = new JLabel("SỐ TIỀN CHUYỂN (VNĐ)");
        amtTitle.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 10));
        amtTitle.setForeground(Theme.TEXT_MUTED);
        card.add(amtTitle);
        card.add(Box.createVerticalStrut(5));


        amountField = new ModernTextField("0 đ", VectorIcons.createBankIcon(17, Theme.TEXT_MUTED));
        amountField.setPreferredSize(new Dimension(352, 44));
        amountField.setMaximumSize(new Dimension(352, 44));

        amountField.getUnderlyingField().setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 16));
        amountField.getUnderlyingField().setForeground(Theme.PRIMARY);


        amountField.getUnderlyingField().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateWords(); }
            @Override public void removeUpdate(DocumentEvent e) { updateWords(); }
            @Override public void changedUpdate(DocumentEvent e) { updateWords(); }

            private void updateWords() {
                BigDecimal amt = parseAmountInput();
                if (amt != null && amt.compareTo(BigDecimal.ZERO) > 0) {
                    amountInWordsLabel.setText("Bằng số: " + formatCurrency(amt));
                } else {
                    amountInWordsLabel.setText("Hạn mức chuyển nhanh: 500.000.000 đ/ngày");
                }
            }
        });

        card.add(amountField);
        card.add(Box.createVerticalStrut(4));


        amountInWordsLabel = new JLabel("Hạn mức chuyển nhanh: 500.000.000 đ/ngày");
        amountInWordsLabel.setFont(new Font(Theme.FONT_FAMILY, Font.PLAIN, 10));
        amountInWordsLabel.setForeground(Theme.TEXT_MUTED);
        card.add(amountInWordsLabel);
        card.add(Box.createVerticalStrut(8));


        card.add(createQuickAmountChips());
        card.add(Box.createVerticalStrut(10));


        JLabel descTitle = new JLabel("NỘI DUNG CHUYỂN KHOẢN");
        descTitle.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 10));
        descTitle.setForeground(Theme.TEXT_MUTED);
        card.add(descTitle);
        card.add(Box.createVerticalStrut(6));

        String defaultDesc = removeDiacritics(account.getFullName() != null ? account.getFullName().toUpperCase() : "KHACH HANG") + " chuyen tien";
        descriptionField = new ModernTextField("Nội dung chuyển", VectorIcons.createReceiptIcon(17, Theme.TEXT_MUTED));
        descriptionField.setText(defaultDesc);
        descriptionField.setPreferredSize(new Dimension(352, 44));
        descriptionField.setMaximumSize(new Dimension(352, 44));
        card.add(descriptionField);

        return card;
    }



    private JPanel createQuickAmountChips() {
        JPanel row = new JPanel(new GridLayout(1, 6, 6, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(352, 26));
        row.setMaximumSize(new Dimension(352, 26));

        row.add(createChip("50k", new BigDecimal("50000")));
        row.add(createChip("100k", new BigDecimal("100000")));
        row.add(createChip("200k", new BigDecimal("200000")));
        row.add(createChip("500k", new BigDecimal("500000")));
        row.add(createChip("1M", new BigDecimal("1000000")));
        row.add(createChip("Tất cả", account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO));

        return row;
    }

    private JPanel createChip(String text, BigDecimal value) {
        JPanel chip = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(new Color(241, 245, 249));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(226, 232, 240));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setOpaque(false);
        chip.setCursor(Theme.HAND_CURSOR);

        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 10));
        lbl.setForeground(new Color(51, 65, 85));
        chip.add(lbl, BorderLayout.CENTER);

        chip.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                amountField.setText(value.toPlainString());
            }
        });

        return chip;
    }



    private JPanel createSecurityPinCard() {
        CardPanel card = new CardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel pinTitle = new JLabel("MÃ PIN GIAO DỊCH (6 SỐ)");
        pinTitle.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 10));
        pinTitle.setForeground(Theme.TEXT_MUTED);
        card.add(pinTitle);
        card.add(Box.createVerticalStrut(5));

        pinField = new ModernPasswordField("Nhập 6 số mã PIN bí mật", VectorIcons.createLockIcon(17, Theme.TEXT_MUTED));
        pinField.setPreferredSize(new Dimension(352, 44));
        pinField.setMaximumSize(new Dimension(352, 44));
        card.add(pinField);

        return card;
    }

    private JPanel createSecurityBadge() {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        badge.setOpaque(false);
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel shield = new JLabel(VectorIcons.createShieldCheckIcon(14, Theme.PRIMARY));
        JLabel text = new JLabel("Giao dịch mã hóa an toàn 256-bit chuẩn SBV Core");
        text.setFont(new Font(Theme.FONT_FAMILY, Font.PLAIN, 11));
        text.setForeground(Theme.TEXT_MUTED);

        badge.add(shield);
        badge.add(text);
        return badge;
    }



    private void executeTransfer() {

        String toAcc = toAccountField.getText().trim();
        if (toAcc.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập số tài khoản người nhận!",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);
            toAccountField.getUnderlyingField().requestFocusInWindow();
            return;
        }

        if (toAcc.equalsIgnoreCase(account.getAccountId())) {
            JOptionPane.showMessageDialog(this,
                    "Không thể tự chuyển tiền cho chính tài khoản của bạn!",
                    "Tài khoản không hợp lệ",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }


        if (!isRecipientValid || verifiedTargetAccount == null || !verifiedTargetAccount.equals(toAcc)) {
            autoCheckRecipientAccount();
            if (!isRecipientValid) {
                JOptionPane.showMessageDialog(this,
                        "Tài khoản người nhận không tồn tại hoặc chưa được xác thực!",
                        "Không tìm thấy tài khoản",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }


        BigDecimal amount = parseAmountInput();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập số tiền chuyển hợp lệ (lớn hơn 0 VNĐ)!",
                    "Số tiền không hợp lệ",
                    JOptionPane.WARNING_MESSAGE);
            amountField.getUnderlyingField().requestFocusInWindow();
            return;
        }

        if (account.getBalance() == null || account.getBalance().compareTo(amount) < 0) {
            JOptionPane.showMessageDialog(this,
                    "Số dư khả dụng không đủ để thực hiện giao dịch này!\n(Số dư hiện tại: " + formatCurrency(account.getBalance()) + ")",
                    "Số dư không đủ",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }


        String pin = pinField.getPasswordString();
        if (pin == null || !pin.trim().matches("^[0-9]{6}$")) {
            JOptionPane.showMessageDialog(this,
                    "Mã PIN giao dịch phải gồm đúng 6 chữ số bí mật!",
                    "Mã PIN không đúng định dạng",
                    JOptionPane.WARNING_MESSAGE);
            pinField.getUnderlyingField().requestFocusInWindow();
            return;
        }

        String rawDesc = descriptionField.getText().trim();
        final String description = rawDesc.isEmpty() ? "Chuyen tien" : rawDesc;
        final String finalToAcc = toAcc;
        final BigDecimal finalAmount = amount;


        submitButton.setEnabled(false);
        submitButton.setText("Đang xử lý giao dịch...");

        final TransferDTO dto = new TransferDTO(finalToAcc, finalAmount, pin.trim(), description);


        new SwingWorker<Response, Void>() {
            @Override
            protected Response doInBackground() {
                return socketClient.transfer(account.getAccountId(), dto);
            }

            @Override
            protected void done() {
                submitButton.setEnabled(true);
                submitButton.setText("Xác nhận chuyển tiền");

                try {
                    Response response = get();
                    if (response != null && response.getStatus() == Status.SUCCESS) {

                        BigDecimal newBalance = account.getBalance().subtract(finalAmount);
                        account.setBalance(newBalance);


                        if (parentHome != null) {
                            parentHome.updateAccountAndRefresh(account);
                        }


                        Transaction tx = null;
                        if (response.getData() != null) {
                            try {
                                tx = JsonUtil.fromJson(response.getData(), Transaction.class);
                            } catch (Exception ignored) {}
                        }


                        showSuccessReceiptDialog(finalToAcc, verifiedRecipientName, finalAmount, description, tx);

                    } else {
                        String errMsg = response != null && response.getMessage() != null
                                ? response.getMessage()
                                : "Giao dịch không thành công! Vui lòng thử lại.";
                        JOptionPane.showMessageDialog(TransferFrame.this,
                                errMsg,
                                "Giao dịch thất bại",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TransferFrame.this,
                            "Lỗi kết nối tới máy chủ khi chuyển tiền: " + ex.getMessage(),
                            "Lỗi hệ thống",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }



    private void showSuccessReceiptDialog(String toAcc, String toName, BigDecimal amount, String desc, Transaction tx) {
        JDialog dialog = new JDialog(this, "Biên lai giao dịch", true);
        dialog.setSize(380, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(24, 24, 20, 24));


        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(new Color(236, 253, 245));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(56, 56));
        iconCircle.setMaximumSize(new Dimension(56, 56));
        iconCircle.setLayout(new BorderLayout());
        iconCircle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel checkLbl = new JLabel(VectorIcons.createCheckIcon(26, new Color(5, 150, 105)), SwingConstants.CENTER);
        iconCircle.add(checkLbl, BorderLayout.CENTER);
        p.add(iconCircle);
        p.add(Box.createVerticalStrut(12));


        JLabel successTitle = new JLabel("Chuyển tiền thành công!", SwingConstants.CENTER);
        successTitle.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 18));
        successTitle.setForeground(Theme.TEXT_PRIMARY);
        successTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(successTitle);
        p.add(Box.createVerticalStrut(6));


        JLabel amtBig = new JLabel("-" + formatCurrency(amount), SwingConstants.CENTER);
        amtBig.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 22));
        amtBig.setForeground(new Color(220, 38, 38));
        amtBig.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(amtBig);
        p.add(Box.createVerticalStrut(18));


        JPanel detailsCard = new JPanel();
        detailsCard.setLayout(new BoxLayout(detailsCard, BoxLayout.Y_AXIS));
        detailsCard.setBackground(new Color(248, 250, 252));
        detailsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        detailsCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        detailsCard.add(createReceiptRow("Người nhận:", toName != null ? toName : toAcc));
        detailsCard.add(Box.createVerticalStrut(6));
        detailsCard.add(createReceiptRow("Số tài khoản:", toAcc));
        detailsCard.add(Box.createVerticalStrut(6));
        detailsCard.add(createReceiptRow("Nội dung:", desc));
        detailsCard.add(Box.createVerticalStrut(6));
        detailsCard.add(createReceiptRow("Phí giao dịch:", "0 đ (Miễn phí)"));
        detailsCard.add(Box.createVerticalStrut(6));

        String txTime = tx != null && tx.getCreatedAt() != null
                ? tx.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        detailsCard.add(createReceiptRow("Thời gian:", txTime));

        p.add(detailsCard);
        p.add(Box.createVerticalStrut(20));


        ModernButton homeBtn = ModernButton.createPrimary("Về trang chủ");
        homeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        homeBtn.addActionListener(e -> {
            dialog.dispose();
            closeAndReturnHome();
        });
        p.add(homeBtn);

        dialog.getContentPane().add(p);
        dialog.setVisible(true);
    }

    private JPanel createReceiptRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(new Font(Theme.FONT_FAMILY, Font.PLAIN, 12));
        l.setForeground(Theme.TEXT_MUTED);

        JLabel v = new JLabel(value);
        v.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 12));
        v.setForeground(Theme.TEXT_PRIMARY);

        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        return row;
    }

    private BigDecimal parseAmountInput() {
        String text = amountField.getText().trim()
                .replaceAll("[^0-9.]", "");
        if (text.isEmpty()) return null;
        try {
            return new BigDecimal(text);
        } catch (Exception e) {
            return null;
        }
    }

    private static String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 đ";
        DecimalFormat df = new DecimalFormat("#,##0");
        return df.format(amount) + " đ";
    }

    private static String removeDiacritics(String str) {
        if (str == null) return "";
        String nfd = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfd).replaceAll("").replace('Đ', 'D').replace('đ', 'd');
    }

    private void closeAndReturnHome() {
        dispose();
        if (parentHome != null) {
            parentHome.setVisible(true);
            parentHome.toFront();
        }
    }
}
