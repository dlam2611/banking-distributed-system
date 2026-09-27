package ui.components;

import ui.theme.Theme;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import javax.swing.Icon;
import javax.swing.JCheckBox;

public class ModernCheckBox extends JCheckBox {

    public ModernCheckBox(String text) {
        super(text);
        setOpaque(false);
        setFocusPainted(false);
        setCursor(Theme.HAND_CURSOR);
        setFont(Theme.LABEL_FONT);
        setForeground(Theme.TEXT_SECONDARY);
        setIconTextGap(8);

        setIcon(new CheckBoxIcon(false));
        setSelectedIcon(new CheckBoxIcon(true));
    }

    private static class CheckBoxIcon implements Icon {
        private final boolean selected;
        private static final int SIZE = 18;

        public CheckBoxIcon(boolean selected) {
            this.selected = selected;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            Theme.applyQualityRendering(g2);

            int boxSize = SIZE;
            int corner = 5;

            if (selected) {

                g2.setColor(Theme.PRIMARY);
                g2.fillRoundRect(x, y, boxSize, boxSize, corner, corner);


                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                GeneralPath check = new GeneralPath();
                check.moveTo(x + 4, y + 9);
                check.lineTo(x + 7.5, y + 13);
                check.lineTo(x + 14, y + 5);
                g2.draw(check);
            } else {

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(x, y, boxSize, boxSize, corner, corner);

                g2.setColor(new Color(203, 213, 225));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(x + 1, y + 1, boxSize - 2, boxSize - 2, corner, corner);
            }

            g2.dispose();
        }

        @Override public int getIconWidth() { return SIZE; }
        @Override public int getIconHeight() { return SIZE; }
    }
}
