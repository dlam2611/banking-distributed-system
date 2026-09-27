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
     * Eye icon for show/hide with default unslashed.
     */
    public static Icon createEyeIcon(int size, Color color) {
        return createEyeIcon(size, color, false);
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
     * Left Arrow icon for back navigation ("←").
     */
    public static Icon createArrowLeftIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int midY = y + size / 2;
                g2.drawLine(x + 2, midY, x + size - 2, midY);
                g2.drawLine(x + 6, midY - 4, x + 2, midY);
                g2.drawLine(x + 6, midY + 4, x + 2, midY);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Triangle warning alert icon (⚠).
     */
    public static Icon createWarningIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                Path2D tri = new Path2D.Double();
                tri.moveTo(x + size * 0.5, y + 2);
                tri.lineTo(x + size - 2, y + size - 2);
                tri.lineTo(x + 2, y + size - 2);
                tri.closePath();
                g2.draw(tri);

                // Exclamation mark
                int mx = x + size / 2;
                g2.drawLine(mx, y + (int)(size * 0.36), mx, y + (int)(size * 0.64));
                g2.fillOval(mx - 1, y + (int)(size * 0.74), 2, 2);

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

    /**
     * Notification Bell icon (🔔) with optional red unread dot.
     */
    public static Icon createBellIcon(int size, Color color, boolean hasUnread) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Bell body
                Path2D bell = new Path2D.Double();
                bell.moveTo(x + size * 0.5, y + size * 0.18);
                bell.curveTo(x + size * 0.70, y + size * 0.18, x + size * 0.78, y + size * 0.55, x + size * 0.85, y + size * 0.72);
                bell.lineTo(x + size * 0.15, y + size * 0.72);
                bell.curveTo(x + size * 0.22, y + size * 0.55, x + size * 0.30, y + size * 0.18, x + size * 0.5, y + size * 0.18);
                bell.closePath();
                g2.draw(bell);

                // Clapper
                g2.drawArc((int)(x + size * 0.42), (int)(y + size * 0.72), (int)(size * 0.16), (int)(size * 0.16), 180, 180);

                // Unread red dot
                if (hasUnread) {
                    g2.setColor(new Color(239, 68, 68)); // #EF4444
                    g2.fillOval(x + size - 5, y + 1, 6, 6);
                }

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * QR Code icon.
     */
    public static Icon createQrIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);

                int s = (int) (size * 0.35);
                // Top-left
                g2.drawRoundRect(x + 1, y + 1, s, s, 2, 2);
                g2.fillRect(x + 3, y + 3, s - 4, s - 4);

                // Top-right
                g2.drawRoundRect(x + size - s - 1, y + 1, s, s, 2, 2);
                g2.fillRect(x + size - s + 1, y + 3, s - 4, s - 4);

                // Bottom-left
                g2.drawRoundRect(x + 1, y + size - s - 1, s, s, 2, 2);
                g2.fillRect(x + 3, y + size - s + 1, s - 4, s - 4);

                // Bottom-right pattern
                int dot = 3;
                g2.fillRect(x + size - s + 2, y + size - s + 2, dot, dot);
                g2.fillRect(x + size - 4, y + size - 4, dot, dot);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Piggy Bank / Savings icon.
     */
    public static Icon createPiggyBankIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Piggy bank facing left matching mockup
                int bw = (int)(size * 0.65);
                int bh = (int)(size * 0.54);
                int bx = x + (int)(size * 0.22);
                int by = y + (int)(size * 0.22);

                // Body
                g2.drawOval(bx, by, bw, bh);

                // Snout protruding left
                g2.drawRoundRect(bx - 3, by + (int)(bh * 0.32), 4, (int)(bh * 0.36), 2, 2);

                // Ear on top right
                g2.drawArc(bx + bw - 5, by - 3, 5, 5, 0, 180);

                // Eye
                g2.fillOval(bx + 4, by + (int)(bh * 0.35), 2, 2);

                // Coin slot on top
                g2.drawLine(bx + bw / 2 - 2, by, bx + bw / 2 + 2, by);

                // Short legs
                g2.drawLine(bx + 3, by + bh - 1, bx + 3, by + bh + 3);
                g2.drawLine(bx + bw - 4, by + bh - 1, bx + bw - 4, by + bh + 3);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Receipt / Bill icon.
     */
    public static Icon createReceiptIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int rw = (int)(size * 0.65);
                int rh = (int)(size * 0.85);
                int rx = x + (size - rw) / 2;
                int ry = y + (size - rh) / 2;

                g2.drawRoundRect(rx, ry, rw, rh, 3, 3);
                g2.drawLine(rx + 4, ry + (int)(rh * 0.35), rx + rw - 4, ry + (int)(rh * 0.35));
                g2.drawLine(rx + 4, ry + (int)(rh * 0.55), rx + rw - 4, ry + (int)(rh * 0.55));
                g2.drawLine(rx + 4, ry + (int)(rh * 0.75), rx + rw - 8, ry + (int)(rh * 0.75));

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Two opposite arrows (⇄) for Transfer.
     */
    public static Icon createTransferArrowsIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Top arrow pointing right
                int y1 = y + (int)(size * 0.35);
                g2.drawLine(x + 3, y1, x + size - 3, y1);
                g2.drawLine(x + size - 7, y1 - 3, x + size - 3, y1);
                g2.drawLine(x + size - 7, y1 + 3, x + size - 3, y1);

                // Bottom arrow pointing left
                int y2 = y + (int)(size * 0.65);
                g2.drawLine(x + 3, y2, x + size - 3, y2);
                g2.drawLine(x + 7, y2 - 3, x + 3, y2);
                g2.drawLine(x + 7, y2 + 3, x + 3, y2);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Building / Office icon for CTY TNHH.
     */
    public static Icon createBuildingIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int bw = (int)(size * 0.70);
                int bh = (int)(size * 0.80);
                int bx = x + (size - bw) / 2;
                int by = y + size - bh - 1;

                g2.drawRoundRect(bx, by, bw, bh, 3, 3);
                // Windows
                g2.drawLine(bx + 4, by + 4, bx + 7, by + 4);
                g2.drawLine(bx + bw - 7, by + 4, bx + bw - 4, by + 4);
                g2.drawLine(bx + 4, by + 9, bx + 7, by + 9);
                g2.drawLine(bx + bw - 7, by + 9, bx + bw - 4, by + 9);
                // Door
                g2.drawRect(bx + bw / 2 - 3, by + bh - 6, 6, 6);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Coffee cup icon for Highland Coffee.
     */
    public static Icon createCoffeeIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int cw = (int)(size * 0.52);
                int ch = (int)(size * 0.46);
                int cx = x + (size - cw) / 2 - 2;
                int cy = y + (int)(size * 0.26);

                // Cup body (slightly rounded base)
                g2.drawRoundRect(cx, cy, cw, ch, 4, 4);
                // Handle on the right
                g2.drawArc(cx + cw - 2, cy + 2, (int)(size * 0.24), (int)(ch * 0.65), -90, 180);
                // Saucer line under the cup
                g2.drawLine(cx - 2, cy + ch + 3, cx + cw + 3, cy + ch + 3);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Copy icon (for copying account number).
     */
    public static Icon createCopyIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int box = (int)(size * 0.65);
                // Back sheet
                g2.drawRoundRect(x + size - box, y, box, box, 2, 2);
                // Front sheet
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(x, y + size - box, box, box, 2, 2);
                g2.setColor(color);
                g2.drawRoundRect(x, y + size - box, box, box, 2, 2);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Grid / Dashboard Dock icon.
     */
    public static Icon createGridIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);

                int dot = (int)(size * 0.38);
                int gap = (int)(size * 0.16);
                g2.fillRoundRect(x, y, dot, dot, 2, 2);
                g2.fillRoundRect(x + dot + gap, y, dot, dot, 2, 2);
                g2.fillRoundRect(x, y + dot + gap, dot, dot, 2, 2);
                g2.fillRoundRect(x + dot + gap, y + dot + gap, dot, dot, 2, 2);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Contactless payment wave icon (RFID/NFC arcs).
     */
    public static Icon createContactlessIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // 3 concentric curved arcs oriented towards right
                int cx = x - size / 4;
                int cy = y - size / 4;
                int span = 55;
                int start = -28;

                g2.drawArc(cx + (int)(size * 0.25), cy + (int)(size * 0.25), (int)(size * 0.70), (int)(size * 0.70), start, span);
                g2.drawArc(cx + (int)(size * 0.10), cy + (int)(size * 0.10), (int)(size * 1.00), (int)(size * 1.00), start, span);
                g2.drawArc(cx - (int)(size * 0.05), cy - (int)(size * 0.05), (int)(size * 1.30), (int)(size * 1.30), start, span);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * Crisp checkmark icon (✓).
     */
    public static Icon createCheckIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int x1 = x + (int)(size * 0.20);
                int y1 = y + (int)(size * 0.52);
                int x2 = x + (int)(size * 0.44);
                int y2 = y + (int)(size * 0.76);
                int x3 = x + (int)(size * 0.82);
                int y3 = y + (int)(size * 0.24);

                g2.drawLine(x1, y1, x2, y2);
                g2.drawLine(x2, y2, x3, y3);

                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    /**
     * 4-pointed golden sparkle icon (✨).
     */
    public static Icon createSparkleIcon(int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.applyQualityRendering(g2);
                g2.setColor(color);

                Path2D p = new Path2D.Double();
                double cx = x + size / 2.0;
                double cy = y + size / 2.0;
                double rOuter = size * 0.48;
                double rInner = size * 0.14;

                p.moveTo(cx, cy - rOuter);
                p.quadTo(cx, cy, cx + rInner, cy);
                p.lineTo(cx + rOuter, cy);
                p.quadTo(cx, cy, cx, cy + rInner);
                p.lineTo(cx, cy + rOuter);
                p.quadTo(cx, cy, cx - rInner, cy);
                p.lineTo(cx - rOuter, cy);
                p.quadTo(cx, cy, cx, cy - rInner);
                p.closePath();

                g2.fill(p);
                g2.dispose();
            }

            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }
}

