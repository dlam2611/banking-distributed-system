package ui;

import model.Account;
import model.Response;
import model.Transaction;
import network.SocketClient;
import protocol.Status;
import ui.components.CardPanel;
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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class HomeFrame extends JFrame {

    private Account account;
    private boolean isBalanceHidden = false;
    private JLabel balanceLabel;
    private JLabel eyeToggleLabel;
    private CardPanel transactionsCard;

    private javax.swing.Timer autoSyncTimer;
    private JPanel notificationBanner;
    private JLabel notifTitleLabel;
    private JLabel notifDescLabel;

    public HomeFrame() {
        this(createDefaultAccount());
    }

    public HomeFrame(Account account) {
        this.account = account != null ? account : createDefaultAccount();
        setTitle("NexBank Digital - Trang chủ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(430, 700);
        setMinimumSize(new Dimension(380, 560));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BACKGROUND);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (autoSyncTimer != null) {
                    autoSyncTimer.stop();
                }
            }
        });

        initUI();
    }

    private static Account createDefaultAccount() {
        Account acc = new Account();
        acc.setAccountId("102988399999");
        acc.setFullName("Nguyễn Văn An");
        acc.setCccd("001203012345");
        acc.setPhone("0988123456");
        acc.setBalance(new BigDecimal("158450000"));
        acc.setStatus("ACTIVE");
        return acc;
    }

    private void initUI() {
        setLayout(new BorderLayout());


        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        mainContent.setBorder(BorderFactory.createEmptyBorder(10, 16, 16, 16));


        mainContent.add(createTopHeader());
        mainContent.add(Box.createVerticalStrut(6));


        mainContent.add(createNotificationBanner());
        mainContent.add(Box.createVerticalStrut(6));


        mainContent.add(createGreetingSection());
        mainContent.add(Box.createVerticalStrut(12));


        mainContent.add(createVirtualCard());
        mainContent.add(Box.createVerticalStrut(10));


        mainContent.add(createQuickActionsRow());
        mainContent.add(Box.createVerticalStrut(12));


        mainContent.add(createRecentTransactionsSection());
        mainContent.add(Box.createVerticalStrut(16));


        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Theme.BACKGROUND);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);


        add(createBottomDock(), BorderLayout.SOUTH);


        loadTransactionsFromBackend();


        startRealtimeSync();
    }



    private JPanel createTopHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.setPreferredSize(new Dimension(380, 38));
        header.setMaximumSize(new Dimension(380, 38));


        JPanel leftBrand = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftBrand.setOpaque(false);


        JPanel logoBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Theme.CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoBox.setOpaque(false);
        logoBox.setPreferredSize(new Dimension(34, 34));
        logoBox.setLayout(new BorderLayout());
        JLabel logoIcon = new JLabel(VectorIcons.createShieldIcon(18, Theme.PRIMARY));
        logoIcon.setHorizontalAlignment(JLabel.CENTER);
        logoBox.add(logoIcon, BorderLayout.CENTER);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel brandLbl = new JLabel("NEXBANK DIGITAL");
        brandLbl.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 10));
        brandLbl.setForeground(new Color(100, 116, 139));

        JLabel pageLbl = new JLabel("Home");
        pageLbl.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 15));
        pageLbl.setForeground(Theme.TEXT_PRIMARY);

        textPanel.add(brandLbl);
        textPanel.add(pageLbl);

        leftBrand.add(logoBox);
        leftBrand.add(textPanel);
        header.add(leftBrand, BorderLayout.WEST);


        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightActions.setOpaque(false);


        JLabel bell = new JLabel(VectorIcons.createBellIcon(22, new Color(71, 85, 105), true));
        bell.setCursor(Theme.HAND_CURSOR);
        bell.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(HomeFrame.this,
                        "Bạn có 1 thông báo mới: Biến động số dư +18,500,000 VND từ chuyển lương.",
                        "Thông báo NexBank",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });


        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(new Color(254, 215, 170));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(180, 83, 9));
                g2.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 12));
                String initials = "VA";
                int tx = (getWidth() - g2.getFontMetrics().stringWidth(initials)) / 2;
                int ty = (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2;
                g2.drawString(initials, tx, ty);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(34, 34));
        avatar.setCursor(Theme.HAND_CURSOR);

        rightActions.add(bell);
        rightActions.add(avatar);
        header.add(rightActions, BorderLayout.EAST);

        return header;
    }



    private JPanel createGreetingSection() {
        JPanel section = new JPanel(new BorderLayout(8, 0));
        section.setOpaque(false);
        section.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.setPreferredSize(new Dimension(380, 48));
        section.setMaximumSize(new Dimension(380, 48));

        JPanel textCol = new JPanel();
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
        textCol.setOpaque(false);

        JPanel subRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        subRow.setOpaque(false);
        JLabel sub = new JLabel("Xin chào,");
        sub.setFont(Theme.CAPTION_FONT);
        sub.setForeground(new Color(100, 116, 139));
        JLabel sparkle = new JLabel(VectorIcons.createSparkleIcon(13, new Color(245, 158, 11)));
        subRow.add(sub);
        subRow.add(sparkle);

        JLabel name = new JLabel(account.getFullName() != null ? account.getFullName() : "Nguyễn Văn An");
        name.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 18));
        name.setForeground(Theme.TEXT_PRIMARY);

        textCol.add(subRow);
        textCol.add(Box.createVerticalStrut(2));
        textCol.add(name);
        section.add(textCol, BorderLayout.CENTER);


        JPanel priorityBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(new Color(224, 242, 254));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        priorityBadge.setOpaque(false);
        priorityBadge.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 4));
        priorityBadge.setPreferredSize(new Dimension(96, 26));

        JLabel checkIcon = new JLabel(VectorIcons.createCheckIcon(10, new Color(3, 105, 161)));

        JLabel priorityText = new JLabel("NexPriority");
        priorityText.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 10));
        priorityText.setForeground(new Color(3, 105, 161));

        priorityBadge.add(checkIcon);
        priorityBadge.add(priorityText);

        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        badgeWrapper.setOpaque(false);
        badgeWrapper.add(priorityBadge);
        section.add(badgeWrapper, BorderLayout.EAST);

        return section;
    }



    private JPanel createVirtualCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);

                int w = getWidth();
                int h = getHeight();


                g2.setColor(new Color(0, 103, 71, 40));
                g2.fillRoundRect(2, 4, w - 4, h - 4, 20, 20);


                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 71, 48),
                        w, h, new Color(3, 110, 76)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w, h, 18, 18);


                g2.setColor(new Color(255, 255, 255, 14));
                g2.drawOval(w - 110, -30, 180, 180);
                g2.drawOval(w - 70, 20, 140, 140);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(380, 168));
        card.setMaximumSize(new Dimension(380, 168));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));


        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JPanel leftChipRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftChipRow.setOpaque(false);


        JPanel goldChip = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);

                g2.setColor(new Color(245, 158, 11));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.setColor(new Color(217, 119, 6));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 4, 4);

                g2.drawLine(getWidth() / 2, 2, getWidth() / 2, getHeight() - 3);
                g2.drawLine(2, getHeight() / 2, getWidth() - 3, getHeight() / 2);
                g2.dispose();
            }
        };
        goldChip.setPreferredSize(new Dimension(30, 22));
        goldChip.setOpaque(false);

        leftChipRow.add(goldChip);
        topRow.add(leftChipRow, BorderLayout.WEST);


        JPanel rightCardName = new JPanel();
        rightCardName.setLayout(new BoxLayout(rightCardName, BoxLayout.Y_AXIS));
        rightCardName.setOpaque(false);

        JLabel brand = new JLabel("NEXBANK");
        brand.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 12));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel type = new JLabel("Platinum Virtual");
        type.setFont(new Font(Theme.FONT_FAMILY, Font.ITALIC, 10));
        type.setForeground(new Color(255, 255, 255, 180));
        type.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightCardName.add(brand);
        rightCardName.add(type);
        topRow.add(rightCardName, BorderLayout.EAST);

        card.add(topRow, BorderLayout.NORTH);


        JPanel middle = new JPanel();
        middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
        middle.setOpaque(false);
        middle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel balanceHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        balanceHeader.setOpaque(false);
        balanceHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        balanceHeader.setMaximumSize(new Dimension(380, 20));

        JLabel balTitle = new JLabel("Số dư khả dụng");
        balTitle.setFont(new Font(Theme.FONT_FAMILY, Font.PLAIN, 11));
        balTitle.setForeground(new Color(255, 255, 255, 190));


        eyeToggleLabel = new JLabel(" Ẩn");
        eyeToggleLabel.setIcon(VectorIcons.createEyeIcon(14, new Color(255, 255, 255, 220)));
        eyeToggleLabel.setFont(new Font(Theme.FONT_FAMILY, Font.PLAIN, 11));
        eyeToggleLabel.setForeground(new Color(255, 255, 255, 220));
        eyeToggleLabel.setCursor(Theme.HAND_CURSOR);
        eyeToggleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleBalanceVisibility();
            }
        });

        balanceHeader.add(balTitle);
        balanceHeader.add(Box.createHorizontalStrut(8));
        balanceHeader.add(eyeToggleLabel);


        JPanel balRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        balRow.setOpaque(false);
        balRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        balRow.setMaximumSize(new Dimension(380, 34));

        balanceLabel = new JLabel(formatBalanceString(account.getBalance()));
        balanceLabel.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 24));
        balanceLabel.setForeground(Color.WHITE);
        balRow.add(balanceLabel);

        middle.add(Box.createVerticalStrut(2));
        middle.add(balanceHeader);
        middle.add(Box.createVerticalStrut(3));
        middle.add(balRow);

        card.add(middle, BorderLayout.CENTER);


        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);

        JPanel accNumRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        accNumRow.setOpaque(false);

        String displayAcc = formatAccountNumber(account.getAccountId());
        JLabel accLabel = new JLabel(displayAcc);
        accLabel.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 12));
        accLabel.setForeground(Color.WHITE);

        JLabel copyBtn = new JLabel(VectorIcons.createCopyIcon(14, Color.WHITE));
        copyBtn.setCursor(Theme.HAND_CURSOR);
        copyBtn.setToolTipText("Sao chép số tài khoản");
        copyBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                StringSelection ss = new StringSelection(account.getAccountId());
                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
                JOptionPane.showMessageDialog(HomeFrame.this,
                        "Đã sao chép số tài khoản: " + account.getAccountId(),
                        "Sao chép thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        accNumRow.add(accLabel);
        accNumRow.add(copyBtn);

        JPanel bottomDetails = new JPanel();
        bottomDetails.setLayout(new BoxLayout(bottomDetails, BoxLayout.Y_AXIS));
        bottomDetails.setOpaque(false);

        JLabel holderName = new JLabel(account.getFullName() != null ? account.getFullName().toUpperCase() : "NGUYEN VAN AN");
        holderName.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 10));
        holderName.setForeground(new Color(255, 255, 255, 210));

        bottomDetails.add(accNumRow);
        bottomDetails.add(Box.createVerticalStrut(2));
        bottomDetails.add(holderName);

        bottomRow.add(bottomDetails, BorderLayout.WEST);


        JPanel expirePanel = new JPanel();
        expirePanel.setLayout(new BoxLayout(expirePanel, BoxLayout.Y_AXIS));
        expirePanel.setOpaque(false);

        JLabel expTitle = new JLabel("Hết hạn");
        expTitle.setFont(new Font(Theme.FONT_FAMILY, Font.PLAIN, 9));
        expTitle.setForeground(new Color(255, 255, 255, 170));
        expTitle.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel expDate = new JLabel("09/29");
        expDate.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 11));
        expDate.setForeground(Color.WHITE);
        expDate.setAlignmentX(Component.RIGHT_ALIGNMENT);

        expirePanel.add(expTitle);
        expirePanel.add(expDate);
        bottomRow.add(expirePanel, BorderLayout.EAST);

        card.add(bottomRow, BorderLayout.SOUTH);

        return card;
    }



    private JPanel createQuickActionsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.setPreferredSize(new Dimension(380, 72));
        row.setMaximumSize(new Dimension(380, 72));


        row.add(createActionButton(
                "Chuyển tiền",
                VectorIcons.createTransferArrowsIcon(20, Color.WHITE),
                Theme.PRIMARY,
                null,
                () -> openTransferScreen()
        ));


        row.add(createActionButton(
                "Nạp / Quét QR",
                VectorIcons.createQrIcon(20, new Color(2, 132, 199)),
                new Color(224, 242, 254),
                null,
                () -> JOptionPane.showMessageDialog(this, "Mở máy ảnh quét mã VietQR chuyển khoản.", "Quét QR", JOptionPane.INFORMATION_MESSAGE)
        ));


        row.add(createActionButton(
                "Tiết kiệm",
                VectorIcons.createPiggyBankIcon(20, new Color(2, 132, 199)),
                new Color(224, 242, 254),
                "8.2%",
                () -> JOptionPane.showMessageDialog(this, "Mở gói gửi tiết kiệm sinh lời 8.2%/năm.", "Tiết kiệm", JOptionPane.INFORMATION_MESSAGE)
        ));


        row.add(createActionButton(
                "Hóa đơn",
                VectorIcons.createReceiptIcon(20, new Color(2, 132, 199)),
                new Color(224, 242, 254),
                null,
                () -> JOptionPane.showMessageDialog(this, "Thanh toán hóa đơn điện, nước, internet tự động.", "Hóa đơn", JOptionPane.INFORMATION_MESSAGE)
        ));

        return row;
    }

    private JPanel createActionButton(String label, Icon icon, Color circleBg, String badgeText, Runnable onClick) {
        JPanel btnCol = new JPanel();
        btnCol.setLayout(new BoxLayout(btnCol, BoxLayout.Y_AXIS));
        btnCol.setOpaque(false);
        btnCol.setCursor(Theme.HAND_CURSOR);


        JPanel circle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);

                int d = Math.min(getWidth(), getHeight()) - 4;
                int ox = (getWidth() - d) / 2;
                int oy = (getHeight() - d) / 2;

                g2.setColor(circleBg);
                g2.fillOval(ox, oy, d, d);


                if (badgeText != null) {
                    g2.setColor(new Color(245, 158, 11));
                    int bw = 30;
                    int bh = 14;
                    int bx = getWidth() - bw - 2;
                    int by = 0;
                    g2.fillRoundRect(bx, by, bw, bh, 6, 6);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 8));
                    int tw = g2.getFontMetrics().stringWidth(badgeText);
                    g2.drawString(badgeText, bx + (bw - tw) / 2, by + 10);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        circle.setOpaque(false);
        circle.setPreferredSize(new Dimension(48, 48));
        circle.setMaximumSize(new Dimension(48, 48));
        circle.setAlignmentX(Component.CENTER_ALIGNMENT);
        circle.setLayout(new BorderLayout());

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setHorizontalAlignment(JLabel.CENTER);
        circle.add(iconLbl, BorderLayout.CENTER);

        JLabel textLbl = new JLabel(label);
        textLbl.setFont(new Font(Theme.FONT_FAMILY, Font.PLAIN, 11));
        textLbl.setForeground(Theme.TEXT_SECONDARY);
        textLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnCol.add(circle);
        btnCol.add(Box.createVerticalStrut(6));
        btnCol.add(textLbl);

        btnCol.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) onClick.run();
            }
        });

        return btnCol;
    }



    private JPanel createRecentTransactionsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.setMaximumSize(new Dimension(380, Integer.MAX_VALUE));


        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerRow.setMaximumSize(new Dimension(380, 24));

        JLabel title = new JLabel("Giao dịch gần đây");
        title.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 16));
        title.setForeground(Theme.TEXT_PRIMARY);

        JLabel viewAll = new JLabel("Xem tất cả ›");
        viewAll.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 12));
        viewAll.setForeground(Theme.PRIMARY);
        viewAll.setCursor(Theme.HAND_CURSOR);
        viewAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(HomeFrame.this,
                        "Mở toàn bộ danh sách lịch sử sao kê chi tiết.",
                        "Lịch sử giao dịch",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        headerRow.add(title, BorderLayout.WEST);
        headerRow.add(viewAll, BorderLayout.EAST);
        section.add(headerRow);

        section.add(Box.createVerticalStrut(8));


        transactionsCard = new CardPanel();
        transactionsCard.setLayout(new BoxLayout(transactionsCard, BoxLayout.Y_AXIS));
        transactionsCard.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        transactionsCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        transactionsCard.setMaximumSize(new Dimension(380, Integer.MAX_VALUE));


        JPanel loadingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 14));
        loadingPanel.setOpaque(false);
        JLabel loadingLbl = new JLabel("Đang tải dữ liệu giao dịch...");
        loadingLbl.setFont(new Font(Theme.FONT_FAMILY, Font.PLAIN, 12));
        loadingLbl.setForeground(Theme.TEXT_MUTED);
        loadingPanel.add(loadingLbl);
        transactionsCard.add(loadingPanel);

        section.add(transactionsCard);
        return section;
    }



    private void loadTransactionsFromBackend() {
        new Thread(() -> {
            List<Transaction> list = null;
            try {
                if (account != null && account.getAccountId() != null) {
                    list = SocketClient.getInstance().getRecentTransactions(account.getAccountId());
                }
            } catch (Exception ignored) {
            }

            final List<Transaction> finalList = list;
            SwingUtilities.invokeLater(() -> renderTransactions(finalList));
        }).start();
    }



    private void renderTransactions(List<Transaction> list) {
        if (transactionsCard == null) return;
        transactionsCard.removeAll();

        if (list != null && !list.isEmpty()) {
            int displayCount = Math.min(list.size(), 5);
            for (int i = 0; i < displayCount; i++) {
                Transaction tx = list.get(i);
                boolean isOutgoing = account.getAccountId() != null && account.getAccountId().equals(tx.getFromAccount());

                Icon icon = isOutgoing ? VectorIcons.createUserIcon(17, new Color(220, 38, 38))
                        : VectorIcons.createBuildingIcon(18, new Color(5, 150, 105));
                Color iconBg = isOutgoing ? new Color(254, 226, 226) : new Color(209, 250, 229);

                String title = tx.getDescription() != null && !tx.getDescription().trim().isEmpty()
                        ? tx.getDescription().trim()
                        : (isOutgoing ? "Chuyển tiền đến " + (tx.getToAccount() != null ? tx.getToAccount() : "---")
                        : "Nhận tiền từ " + (tx.getFromAccount() != null ? tx.getFromAccount() : "---"));

                String subtitle = formatDateTime(tx.getCreatedAt());
                String amountStr = (isOutgoing ? "-" : "+") + formatAmount(tx.getAmount()) + " đ";
                Color amountColor = isOutgoing ? Theme.TEXT_PRIMARY : new Color(5, 150, 105);

                transactionsCard.add(createTransactionItem(icon, iconBg, title, subtitle, amountStr, amountColor));
                if (i < displayCount - 1) {
                    transactionsCard.add(createDivider());
                }
            }
        } else {

            JPanel emptyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 16));
            emptyPanel.setOpaque(false);
            JLabel emptyLbl = new JLabel("Chưa có giao dịch phát sinh gần đây");
            emptyLbl.setFont(new Font(Theme.FONT_FAMILY, Font.PLAIN, 12));
            emptyLbl.setForeground(Theme.TEXT_MUTED);
            emptyPanel.add(emptyLbl);
            transactionsCard.add(emptyPanel);
        }

        transactionsCard.revalidate();
        transactionsCard.repaint();
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) return "0";
        return String.format("%,.0f", amount);
    }

    private String formatDateTime(LocalDateTime dt) {
        if (dt == null) return "Giao dịch gần đây";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dt.format(dtf);
    }

    private JPanel createTransactionItem(Icon icon, Color iconBg, String title, String subtitle, String amount, Color amountColor) {
        JPanel item = new JPanel(new BorderLayout(12, 0));
        item.setOpaque(false);
        item.setAlignmentX(Component.CENTER_ALIGNMENT);
        item.setPreferredSize(new Dimension(356, 56));
        item.setMaximumSize(new Dimension(380, 56));
        item.setCursor(Theme.HAND_CURSOR);


        JPanel iconBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(iconBg);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBadge.setOpaque(false);
        iconBadge.setPreferredSize(new Dimension(38, 38));
        iconBadge.setLayout(new BorderLayout());
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setHorizontalAlignment(JLabel.CENTER);
        iconBadge.add(iconLbl, BorderLayout.CENTER);

        JPanel leftCol = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 9));
        leftCol.setOpaque(false);
        leftCol.add(iconBadge);
        item.add(leftCol, BorderLayout.WEST);


        JPanel centerCol = new JPanel();
        centerCol.setLayout(new BoxLayout(centerCol, BoxLayout.Y_AXIS));
        centerCol.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 13));
        titleLbl.setForeground(Theme.TEXT_PRIMARY);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font(Theme.FONT_FAMILY, Font.PLAIN, 11));
        subLbl.setForeground(new Color(100, 116, 139));

        centerCol.add(Box.createVerticalStrut(10));
        centerCol.add(titleLbl);
        centerCol.add(Box.createVerticalStrut(2));
        centerCol.add(subLbl);
        item.add(centerCol, BorderLayout.CENTER);


        JPanel rightCol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 18));
        rightCol.setOpaque(false);

        JLabel amountLbl = new JLabel(amount);
        amountLbl.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 14));
        amountLbl.setForeground(amountColor);

        rightCol.add(amountLbl);
        item.add(rightCol, BorderLayout.EAST);

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(HomeFrame.this,
                        "Chi tiết giao dịch:\n\n• Đối tác: " + title + "\n• Thời gian: " + subtitle + "\n• Số tiền: " + amount,
                        "Chi tiết giao dịch",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return item;
    }

    private JPanel createDivider() {
        JPanel divider = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(241, 245, 249));
                g.drawLine(0, 0, getWidth(), 0);
            }
        };
        divider.setOpaque(false);
        divider.setPreferredSize(new Dimension(350, 1));
        divider.setMaximumSize(new Dimension(350, 1));
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);
        return divider;
    }



    private JPanel createBottomDock() {
        JPanel dock = new JPanel(new GridLayout(1, 4, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(226, 232, 240));
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        dock.setPreferredSize(new Dimension(430, 56));


        dock.add(createDockTab("Trang chủ", VectorIcons.createGridIcon(18, Theme.PRIMARY), Theme.PRIMARY, true, null));


        dock.add(createDockTab("Chuyển tiền", VectorIcons.createTransferArrowsIcon(18, Theme.TEXT_MUTED), Theme.TEXT_MUTED, false, () -> {
            openTransferScreen();
        }));


        dock.add(createDockTab("Thẻ & GD", VectorIcons.createCardIcon(18, Theme.TEXT_MUTED), Theme.TEXT_MUTED, false, () -> {
            JOptionPane.showMessageDialog(this, "Quản lý thẻ tín dụng, thẻ ảo và hạn mức giao dịch.", "Thẻ & Giao dịch", JOptionPane.INFORMATION_MESSAGE);
        }));


        dock.add(createDockTab("Tài khoản", VectorIcons.createUserIcon(18, Theme.TEXT_MUTED), Theme.TEXT_MUTED, false, () -> {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Bạn muốn đăng xuất khỏi tài khoản " + account.getFullName() + "?",
                    "Đăng xuất",
                    JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                dispose();
                LoginFrame.main(new String[0]);
            }
        }));

        return dock;
    }

    private JPanel createDockTab(String text, Icon icon, Color textColor, boolean isActive, Runnable onClick) {
        JPanel tab = new JPanel();
        tab.setLayout(new BoxLayout(tab, BoxLayout.Y_AXIS));
        tab.setOpaque(false);
        tab.setCursor(Theme.HAND_CURSOR);

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textLbl = new JLabel(text);
        textLbl.setFont(new Font(Theme.FONT_FAMILY, isActive ? Font.BOLD : Font.PLAIN, 10));
        textLbl.setForeground(textColor);
        textLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        tab.add(Box.createVerticalStrut(5));
        tab.add(iconLbl);
        tab.add(Box.createVerticalStrut(2));
        tab.add(textLbl);

        tab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) onClick.run();
            }
        });

        return tab;
    }

    private void toggleBalanceVisibility() {
        isBalanceHidden = !isBalanceHidden;
        if (isBalanceHidden) {
            balanceLabel.setText("••••••••• VND");
            eyeToggleLabel.setText(" Hiện");
            eyeToggleLabel.setIcon(VectorIcons.createEyeIcon(14, new Color(255, 255, 255, 220), true));
        } else {
            balanceLabel.setText(formatBalanceString(account.getBalance()));
            eyeToggleLabel.setText(" Ẩn");
            eyeToggleLabel.setIcon(VectorIcons.createEyeIcon(14, new Color(255, 255, 255, 220), false));
        }
    }

    private String formatBalanceString(BigDecimal balance) {
        if (balance == null) return "0 VND";
        return String.format("%,.0f VND", balance);
    }

    private String formatAccountNumber(String raw) {
        if (raw == null || raw.isEmpty()) return "1029  8839  9999";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            if (i > 0 && i % 4 == 0) sb.append("  ");
            sb.append(raw.charAt(i));
        }
        return sb.toString();
    }

    public void openTransferScreen() {
        TransferFrame tf = new TransferFrame(account, this);
        tf.setVisible(true);
    }

    private void updateBalanceDisplay() {
        if (balanceLabel != null && account != null) {
            balanceLabel.setText(isBalanceHidden ? "••••••••• VND" : formatBalanceString(account.getBalance()));
        }
    }

    public void updateAccountAndRefresh(Account updatedAccount) {
        if (updatedAccount != null) {
            this.account = updatedAccount;
        }
        updateBalanceDisplay();
        loadTransactionsFromBackend();
    }



    private JPanel createNotificationBanner() {
        notificationBanner = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(new Color(236, 253, 245));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(16, 185, 129));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        notificationBanner.setOpaque(false);
        notificationBanner.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        notificationBanner.setMaximumSize(new Dimension(380, 48));
        notificationBanner.setPreferredSize(new Dimension(380, 48));
        notificationBanner.setAlignmentX(Component.CENTER_ALIGNMENT);
        notificationBanner.setVisible(false);

        JLabel bellIcon = new JLabel(VectorIcons.createBellIcon(18, new Color(5, 150, 105), true));
        notificationBanner.add(bellIcon, BorderLayout.WEST);

        JPanel textCol = new JPanel();
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
        textCol.setOpaque(false);

        notifTitleLabel = new JLabel("Biến động số dư: +0 đ");
        notifTitleLabel.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 12));
        notifTitleLabel.setForeground(new Color(6, 95, 70));

        notifDescLabel = new JLabel("Nhận tiền từ đối tác");
        notifDescLabel.setFont(new Font(Theme.FONT_FAMILY, Font.PLAIN, 10));
        notifDescLabel.setForeground(new Color(4, 120, 87));

        textCol.add(notifTitleLabel);
        textCol.add(Box.createVerticalStrut(2));
        textCol.add(notifDescLabel);
        notificationBanner.add(textCol, BorderLayout.CENTER);

        JLabel closeX = new JLabel(VectorIcons.createClearIcon(14, new Color(5, 150, 105)));
        closeX.setCursor(Theme.HAND_CURSOR);
        closeX.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                notificationBanner.setVisible(false);
                if (notificationBanner.getParent() != null) {
                    notificationBanner.getParent().revalidate();
                    notificationBanner.getParent().repaint();
                }
            }
        });
        notificationBanner.add(closeX, BorderLayout.EAST);

        return notificationBanner;
    }

    private void showBalanceChangeNotification(String amountStr, String descStr) {
        if (notifTitleLabel != null) {
            notifTitleLabel.setText("Biến động số dư: " + amountStr);
        }
        if (notifDescLabel != null) {
            notifDescLabel.setText(descStr);
        }
        if (notificationBanner != null) {
            notificationBanner.setVisible(true);
            notificationBanner.revalidate();
            notificationBanner.repaint();
            if (notificationBanner.getParent() != null) {
                notificationBanner.getParent().revalidate();
                notificationBanner.getParent().repaint();
            }
            try {
                java.awt.Toolkit.getDefaultToolkit().beep();
            } catch (Exception ignored) {}

            javax.swing.Timer hideTimer = new javax.swing.Timer(8000, evt -> {
                if (notificationBanner != null) {
                    notificationBanner.setVisible(false);
                    if (notificationBanner.getParent() != null) {
                        notificationBanner.getParent().revalidate();
                        notificationBanner.getParent().repaint();
                    }
                }
            });
            hideTimer.setRepeats(false);
            hideTimer.start();
        }
    }



    private void startRealtimeSync() {
        if (account == null || account.getAccountId() == null) return;

        autoSyncTimer = new javax.swing.Timer(2000, e -> {
            new Thread(() -> {
                try {
                    String accId = account.getAccountId();
                    Response accResp = SocketClient.getInstance().checkAccount(accId);
                    if (accResp != null && accResp.getStatus() == Status.SUCCESS && accResp.getData() != null) {
                        Account latest = JsonUtil.fromJson(accResp.getData(), Account.class);
                        if (latest != null && latest.getBalance() != null) {
                            BigDecimal oldBal = account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO;
                            BigDecimal newBal = latest.getBalance();

                            if (oldBal.compareTo(newBal) != 0) {
                                BigDecimal diff = newBal.subtract(oldBal);
                                account.setBalance(newBal);

                                List<Transaction> newTxList = SocketClient.getInstance().getRecentTransactions(accId);

                                SwingUtilities.invokeLater(() -> {
                                    updateBalanceDisplay();
                                    renderTransactions(newTxList);


                                    if (diff.compareTo(BigDecimal.ZERO) > 0) {
                                        String senderName = (newTxList != null && !newTxList.isEmpty())
                                                ? newTxList.get(0).getDescription()
                                                : "Đối tác";
                                        showBalanceChangeNotification("+" + formatAmount(diff) + " đ", "Nhận tiền từ " + senderName);
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }).start();
        });
        autoSyncTimer.start();
    }

    public static void main(String[] args) {
        try {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            HomeFrame frame = new HomeFrame();
            frame.setVisible(true);
        });
    }
}
