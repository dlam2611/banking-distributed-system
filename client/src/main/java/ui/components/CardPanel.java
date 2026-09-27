package ui.components;

import ui.theme.Theme;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import javax.swing.JPanel;

/**
 * Reusable Card Container with rounded corners, subtle border, and soft drop shadow.
 */
public class CardPanel extends JPanel {

    private int cornerRadius = Theme.RADIUS_CARD;
    private Color backgroundColor = Theme.CARD_BG;
    private Color borderColor = Theme.CARD_BORDER;
    private boolean showShadow = true;

    public CardPanel() {
        setOpaque(false);
    }

    public CardPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public void setBackgroundColor(Color bg) {
        this.backgroundColor = bg;
        repaint();
    }

    public void setBorderColor(Color border) {
        this.borderColor = border;
        repaint();
    }

    public void setShowShadow(boolean showShadow) {
        this.showShadow = showShadow;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();

        int shadowOffset = showShadow ? 3 : 0;
        int cardW = w - (showShadow ? 4 : 2);
        int cardH = h - (showShadow ? 6 : 2);
        int cardX = (w - cardW) / 2;
        int cardY = 1;

        // Ambient soft drop shadow
        if (showShadow) {
            g2.setColor(new Color(15, 23, 42, 8));
            g2.fillRoundRect(cardX, cardY + 3, cardW, cardH, cornerRadius + 2, cornerRadius + 2);
            g2.setColor(new Color(15, 23, 42, 12));
            g2.fillRoundRect(cardX, cardY + 2, cardW, cardH, cornerRadius, cornerRadius);
        }

        // Card surface
        g2.setColor(backgroundColor);
        g2.fillRoundRect(cardX, cardY, cardW, cardH, cornerRadius, cornerRadius);

        // Subtle border
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(cardX, cardY, cardW - 1, cardH - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
