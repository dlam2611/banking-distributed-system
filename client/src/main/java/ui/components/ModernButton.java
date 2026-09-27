package ui.components;

import ui.theme.Theme;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * Standard Modern Button for NexBank Digital Core.
 * Reusable across all application screens with consistent styling.
 */
public class ModernButton extends JButton {

    public enum ButtonType {
        PRIMARY,
        SECONDARY,
        OUTLINE,
        TEXT_LINK
    }

    private final ButtonType type;
    private Color customBg;
    private Color customHoverBg;
    private Color customPressedBg;
    private Color customTextColor;
    private int cornerRadius = Theme.RADIUS_BUTTON;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private Icon rightIcon;

    public ModernButton(String text, ButtonType type) {
        super(text);
        this.type = type;
        setupStyle();
    }

    public static ModernButton createPrimary(String text) {
        ModernButton btn = new ModernButton(text, ButtonType.PRIMARY);
        btn.setRightIcon(VectorIcons.createArrowRightIcon(16, Color.WHITE));
        return btn;
    }

    public static ModernButton createSecondary(String text) {
        return new ModernButton(text, ButtonType.SECONDARY);
    }

    public static ModernButton createBiometric(String text) {
        ModernButton btn = new ModernButton(text, ButtonType.SECONDARY);
        btn.setIcon(VectorIcons.createFaceIdIcon(18, Theme.PRIMARY));
        btn.setIconTextGap(10);
        return btn;
    }

    private void setupStyle() {
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Theme.HAND_CURSOR);
        setFont(Theme.BUTTON_FONT);

        switch (type) {
            case PRIMARY -> {
                customBg = Theme.PRIMARY;
                customHoverBg = Theme.PRIMARY_HOVER;
                customPressedBg = Theme.PRIMARY_PRESSED;
                customTextColor = Theme.TEXT_WHITE;
                setPreferredSize(new Dimension(320, 48));
            }
            case SECONDARY -> {
                customBg = Theme.SECONDARY_BTN_BG;
                customHoverBg = Theme.SECONDARY_BTN_HOVER;
                customPressedBg = Theme.SECONDARY_BTN_PRESSED;
                customTextColor = Theme.TEXT_PRIMARY;
                setFont(Theme.SMALL_BUTTON_FONT);
                setPreferredSize(new Dimension(320, 46));
            }
            case OUTLINE -> {
                customBg = new Color(0, 0, 0, 0);
                customHoverBg = Theme.PRIMARY_LIGHT;
                customPressedBg = new Color(0, 103, 71, 30);
                customTextColor = Theme.PRIMARY;
                setPreferredSize(new Dimension(320, 46));
            }
            case TEXT_LINK -> {
                customBg = new Color(0, 0, 0, 0);
                customHoverBg = new Color(0, 0, 0, 0);
                customPressedBg = new Color(0, 0, 0, 0);
                customTextColor = Theme.PRIMARY;
                setFont(Theme.LABEL_FONT);
            }
        }
        setForeground(customTextColor);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = false;
                    repaint();
                }
            }
        });
    }

    public void setRightIcon(Icon rightIcon) {
        this.rightIcon = rightIcon;
        repaint();
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();

        // Determine current background
        Color bg = customBg;
        if (!isEnabled()) {
            bg = new Color(203, 213, 225); // Disabled slate
        } else if (isPressed) {
            bg = customPressedBg;
        } else if (isHovered) {
            bg = customHoverBg;
        }

        // Draw background
        if (type != ButtonType.TEXT_LINK) {
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);

            if (type == ButtonType.OUTLINE) {
                g2.setColor(Theme.PRIMARY);
                g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
            }
        }

        // Calculate text and icon layout
        g2.setFont(getFont());
        g2.setColor(isEnabled() ? customTextColor : Theme.TEXT_MUTED);

        String text = getText();
        int textW = g2.getFontMetrics().stringWidth(text);
        int textH = g2.getFontMetrics().getAscent();

        int leftIconW = getIcon() != null ? getIcon().getIconWidth() + getIconTextGap() : 0;
        int rightIconW = rightIcon != null ? rightIcon.getIconWidth() + 8 : 0;
        int totalContentW = leftIconW + textW + rightIconW;

        int startX = (w - totalContentW) / 2;
        int curX = startX;

        // Draw Left Icon
        if (getIcon() != null) {
            int iconY = (h - getIcon().getIconHeight()) / 2;
            getIcon().paintIcon(this, g2, curX, iconY);
            curX += leftIconW;
        }

        // Draw Text
        int textY = (h + textH) / 2 - 2;
        g2.drawString(text, curX, textY);
        curX += textW;

        // Draw Right Icon
        if (rightIcon != null) {
            curX += 8;
            int rightIconY = (h - rightIcon.getIconHeight()) / 2;
            rightIcon.paintIcon(this, g2, curX, rightIconY);
        }

        g2.dispose();
    }
}
