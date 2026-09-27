package ui.components;

import ui.theme.Theme;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import javax.swing.Icon;

/**
 * High-definition vector icons drawn directly with Java 2D Graphics.
 * Guaranteed pixel-crisp rendering on 100%, 125%, 150%, and 200% DPI screens.
 */
public final class VectorIcons {

    private VectorIcons() {}

    /**
     * White Shield icon for the top brand header.
     */
    public static Icon createShieldIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);

                Path2D path = new Path2D.Double();
                double w = size;
                double h = size;
                double ox = x;
                double oy = y;

                path.moveTo(ox + w * 0.5, oy + h * 0.08);
                path.lineTo(ox + w * 0.88, oy + h * 0.22);
                path.curveTo(ox + w * 0.88, oy + h * 0.65, ox + w * 0.5, oy + h * 0.92, ox + w * 0.5, oy + h * 0.95);
                path.curveTo(ox + w * 0.5, oy + h * 0.92, ox + w * 0.12, oy + h * 0.65, ox + w * 0.12, oy + h * 0.22);
                path.closePath();

                g2.fill(path);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Small padlock icon.
     */
    public static Icon createLockIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int bodyW = (int) (size * 0.75);
                int bodyH = (int) (size * 0.52);
                int bx = x + (size - bodyW) / 2;
                int by = y + size - bodyH - 1;

                // Body
                g2.fillRoundRect(bx, by, bodyW, bodyH, 4, 4);

                // Shackle
                int shackleW = (int) (size * 0.46);
                int shackleH = (int) (size * 0.44);
                int sx = x + (size - shackleW) / 2;
                int sy = by - (int)(shackleH * 0.7);
                g2.drawArc(sx, sy, shackleW, shackleH, 0, 180);
                g2.drawLine(sx, sy + shackleH / 2, sx, by);
                g2.drawLine(sx + shackleW, sy + shackleH / 2, sx + shackleW, by);

                // Keyhole
                g2.setColor(Color.WHITE);
                g2.fillOval(x + size / 2 - 1, by + bodyH / 2 - 2, 3, 3);
                g2.drawLine(x + size / 2, by + bodyH / 2, x + size / 2, by + bodyH / 2 + 3);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * User silhouette icon for CCCD / Username.
     */
    public static Icon createUserIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Head
                int headR = (int) (size * 0.24);
                int hx = x + size / 2 - headR;
                int hy = y + (int) (size * 0.12);
                g2.drawOval(hx, hy, headR * 2, headR * 2);

                // Body / Shoulders
                int bodyW = (int) (size * 0.65);
                int bodyH = (int) (size * 0.40);
                int bx = x + (size - bodyW) / 2;
                int by = y + (int) (size * 0.58);
                g2.drawArc(bx, by, bodyW, bodyH * 2, 0, 180);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Clear (X in circle) icon.
     */
    public static Icon createClearIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);

                // Circle outline
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(x + 1, y + 1, size - 3, size - 3);

                // X mark
                int offset = (int) (size * 0.3);
                g2.drawLine(x + offset, y + offset, x + size - offset, y + size - offset);
                g2.drawLine(x + size - offset, y + offset, x + offset, y + size - offset);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Eye icon for show/hide password.
     */
    public static Icon createEyeIcon(int size, Color color, boolean slashed) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Eye outline
                Path2D path = new Path2D.Double();
                double cy = y + size * 0.5;
                path.moveTo(x + 2, cy);
                path.curveTo(x + size * 0.25, cy - size * 0.35, x + size * 0.75, cy - size * 0.35, x + size - 2, cy);
                path.curveTo(x + size * 0.75, cy + size * 0.35, x + size * 0.25, cy + size * 0.35, x + 2, cy);
                g2.draw(path);

                // Pupil
                int pupilR = (int) (size * 0.16);
                g2.fillOval(x + size / 2 - pupilR, (int) cy - pupilR, pupilR * 2, pupilR * 2);

                // Slash if eye off
                if (slashed) {
                    g2.drawLine(x + 3, y + 3, x + size - 3, y + size - 3);
                }

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Right Arrow icon for primary login button ("Đăng nhập an toàn →").
     */
    public static Icon createArrowRightIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int midY = y + size / 2;
                g2.drawLine(x + 2, midY, x + size - 2, midY);
                g2.drawLine(x + size - 6, midY - 4, x + size - 2, midY);
                g2.drawLine(x + size - 6, midY + 4, x + size - 2, midY);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Face ID / Biometrics smiley icon.
     */
    public static Icon createFaceIdIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Head outline (smiling/biometric avatar circle)
                g2.drawOval(x + 1, y + 1, size - 2, size - 2);

                // Eyes
                int eyeY = y + (int)(size * 0.4);
                int eye1X = x + (int)(size * 0.33);
                int eye2X = x + (int)(size * 0.67);
                g2.fillOval(eye1X - 1, eyeY - 1, 3, 3);
                g2.fillOval(eye2X - 1, eyeY - 1, 3, 3);

                // Smile
                int smileW = (int) (size * 0.44);
                int smileH = (int) (size * 0.28);
                int sx = x + (size - smileW) / 2;
                int sy = y + (int) (size * 0.48);
                g2.drawArc(sx, sy, smileW, smileH, 200, 140);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * External link icon (↗) for "Đăng ký ngay ↗".
     */
    public static Icon createExternalLinkIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Square frame
                int boxW = (int)(size * 0.7);
                int boxH = (int)(size * 0.7);
                int bx = x + 1;
                int by = y + size - boxH - 1;
                g2.drawRoundRect(bx, by, boxW, boxH, 3, 3);

                // Arrow pointing top-right
                int ax1 = x + (int)(size * 0.45);
                int ay1 = y + (int)(size * 0.55);
                int ax2 = x + size - 2;
                int ay2 = y + 2;
                g2.drawLine(ax1, ay1, ax2, ay2);
                g2.drawLine(ax2 - 4, ay2, ax2, ay2);
                g2.drawLine(ax2, ay2 + 4, ax2, ay2);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Security badge with checkmark (for Alert & footer SBV COMPLIANT).
     */
    public static Icon createShieldCheckIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                Path2D path = new Path2D.Double();
                double w = size;
                double h = size;
                double ox = x;
                double oy = y;

                path.moveTo(ox + w * 0.5, oy + h * 0.08);
                path.lineTo(ox + w * 0.88, oy + h * 0.22);
                path.curveTo(ox + w * 0.88, oy + h * 0.65, ox + w * 0.5, oy + h * 0.92, ox + w * 0.5, oy + h * 0.95);
                path.curveTo(ox + w * 0.5, oy + h * 0.92, ox + w * 0.12, oy + h * 0.65, ox + w * 0.12, oy + h * 0.22);
                path.closePath();
                g2.draw(path);

                // Checkmark inside
                GeneralPath check = new GeneralPath();
                check.moveTo(ox + w * 0.32, oy + h * 0.50);
                check.lineTo(ox + w * 0.46, oy + h * 0.64);
                check.lineTo(ox + w * 0.68, oy + h * 0.38);
                g2.draw(check);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Bank building icon (🏛).
     */
    public static Icon createBankIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Roof triangle
                Path2D roof = new Path2D.Double();
                roof.moveTo(x + size * 0.5, y + size * 0.15);
                roof.lineTo(x + size * 0.88, y + size * 0.35);
                roof.lineTo(x + size * 0.12, y + size * 0.35);
                roof.closePath();
                g2.fill(roof);

                // Pillars
                int py1 = (int) (y + size * 0.40);
                int py2 = (int) (y + size * 0.75);
                g2.drawLine((int)(x + size * 0.22), py1, (int)(x + size * 0.22), py2);
                g2.drawLine((int)(x + size * 0.41), py1, (int)(x + size * 0.41), py2);
                g2.drawLine((int)(x + size * 0.59), py1, (int)(x + size * 0.59), py2);
                g2.drawLine((int)(x + size * 0.78), py1, (int)(x + size * 0.78), py2);

                // Base floor
                int by = (int) (y + size * 0.78);
                g2.fillRect((int)(x + size * 0.10), by, (int)(size * 0.80), (int)(size * 0.10));

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Lightning bolt icon (⚡).
     */
    public static Icon createLightningIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);

                Path2D bolt = new Path2D.Double();
                bolt.moveTo(x + size * 0.55, y + size * 0.05);
                bolt.lineTo(x + size * 0.20, y + size * 0.55);
                bolt.lineTo(x + size * 0.50, y + size * 0.55);
                bolt.lineTo(x + size * 0.42, y + size * 0.95);
                bolt.lineTo(x + size * 0.82, y + size * 0.42);
                bolt.lineTo(x + size * 0.52, y + size * 0.42);
                bolt.closePath();
                g2.fill(bolt);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Mobile phone icon (📱).
     */
    public static Icon createPhoneIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int pw = (int) (size * 0.55);
                int ph = (int) (size * 0.85);
                int px = x + (size - pw) / 2;
                int py = y + (size - ph) / 2;

                g2.drawRoundRect(px, py, pw, ph, 4, 4);
                // Home indicator dot
                g2.fillOval(x + size / 2 - 1, py + ph - 4, 3, 3);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * ID Card / Credit Card icon (💳).
     */
    public static Icon createCardIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int cw = (int) (size * 0.85);
                int ch = (int) (size * 0.60);
                int cx = x + (size - cw) / 2;
                int cy = y + (size - ch) / 2;

                g2.drawRoundRect(cx, cy, cw, ch, 4, 4);
                g2.drawLine(cx, cy + (int)(ch * 0.35), cx + cw, cy + (int)(ch * 0.35));

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }
}
