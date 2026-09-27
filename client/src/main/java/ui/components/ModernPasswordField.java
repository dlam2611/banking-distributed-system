package ui.components;

import ui.theme.Theme;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class ModernPasswordField extends JPanel {

    private final JPasswordField passwordField;
    private final JLabel leftIconLabel;
    private final JLabel eyeButton;
    private final String placeholder;
    private boolean isFocused = false;
    private boolean isPasswordVisible = false;
    private final char defaultEchoChar;

    public ModernPasswordField(String placeholder, Icon leftIcon) {
        this.placeholder = placeholder;
        setLayout(new BorderLayout(10, 0));
        setOpaque(false);
        setPreferredSize(new Dimension(320, 48));
        setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 12));


        leftIconLabel = new JLabel(leftIcon);
        add(leftIconLabel, BorderLayout.WEST);


        passwordField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner() && placeholder != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    Theme.applyQualityRendering(g2);
                    g2.setColor(Theme.INPUT_PLACEHOLDER);
                    g2.setFont(Theme.INPUT_FONT);
                    int textY = (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2;
                    g2.drawString(placeholder, 2, textY);
                    g2.dispose();
                }
            }
        };
        passwordField.setOpaque(false);
        passwordField.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        passwordField.setFont(Theme.INPUT_FONT);
        passwordField.setForeground(Theme.TEXT_PRIMARY);
        passwordField.setCaretColor(Theme.PRIMARY);
        passwordField.setEchoChar('•');
        defaultEchoChar = passwordField.getEchoChar();

        add(passwordField, BorderLayout.CENTER);


        eyeButton = new JLabel(VectorIcons.createEyeIcon(18, Theme.TEXT_MUTED, false));
        eyeButton.setCursor(Theme.HAND_CURSOR);
        eyeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    passwordField.setEchoChar((char) 0);
                    eyeButton.setIcon(VectorIcons.createEyeIcon(18, Theme.PRIMARY, true));
                } else {
                    passwordField.setEchoChar(defaultEchoChar);
                    eyeButton.setIcon(VectorIcons.createEyeIcon(18, Theme.TEXT_MUTED, false));
                }
            }
        });
        add(eyeButton, BorderLayout.EAST);


        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });


        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                passwordField.requestFocusInWindow();
            }
        });
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public String getPasswordString() {
        return new String(passwordField.getPassword());
    }

    public void setText(String text) {
        passwordField.setText(text);
    }

    public JPasswordField getUnderlyingField() {
        return passwordField;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();


        g2.setColor(Theme.INPUT_BG);
        g2.fillRoundRect(0, 0, w, h, Theme.RADIUS_INPUT, Theme.RADIUS_INPUT);


        if (isFocused) {
            g2.setColor(Theme.PRIMARY);
            g2.setStroke(new BasicStroke(1.8f));
        } else {
            g2.setColor(Theme.INPUT_BORDER);
            g2.setStroke(new BasicStroke(1.2f));
        }
        g2.drawRoundRect(1, 1, w - 2, h - 2, Theme.RADIUS_INPUT, Theme.RADIUS_INPUT);

        g2.dispose();
        super.paintComponent(g);
    }
}
