package ui.components;

import ui.theme.Theme;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * High-security session info banner ("Phiên làm việc bảo mật cao").
 */
public class AlertBox extends JPanel {

    public AlertBox(String title, String subtitle) {
        setLayout(new BorderLayout(12, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        setPreferredSize(new Dimension(320, 56));

        // Left Icon Badge
        JPanel iconBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(new Color(219, 234, 254)); // #DBEAFE
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconBadge.setOpaque(false);
        iconBadge.setPreferredSize(new Dimension(28, 28));
        iconBadge.setLayout(new BorderLayout());
        JLabel iconLabel = new JLabel(VectorIcons.createShieldCheckIcon(18, Theme.PRIMARY));
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        iconBadge.add(iconLabel, BorderLayout.CENTER);

        JPanel westContainer = new JPanel(new BorderLayout());
        westContainer.setOpaque(false);
        westContainer.add(iconBadge, BorderLayout.NORTH);
        add(westContainer, BorderLayout.WEST);

        // Text Section
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Theme.FONT_FAMILY, Font.BOLD, 12));
        titleLabel.setForeground(Theme.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font(Theme.FONT_FAMILY, Font.PLAIN, 11));
        subtitleLabel.setForeground(Theme.TEXT_MUTED);

        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        add(textPanel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();

        // Background
        g2.setColor(Theme.ALERT_BG);
        g2.fillRoundRect(0, 0, w, h, 12, 12);

        // Subtle Border
        g2.setColor(Theme.ALERT_BORDER);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);

        g2.dispose();
        super.paintComponent(g);
    }
}
