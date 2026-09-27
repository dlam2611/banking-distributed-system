package ui.components;

import ui.theme.Theme;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
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
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ModernTextField extends JPanel {

    private final JTextField textField;
    private final JLabel leftIconLabel;
    private final JLabel clearButton;
    private final String placeholder;
    private boolean isFocused = false;

    public ModernTextField(String placeholder, Icon leftIcon) {
        this.placeholder = placeholder;
        setLayout(new BorderLayout(10, 0));
        setOpaque(false);
        setPreferredSize(new Dimension(320, 48));
        setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 12));


        leftIconLabel = new JLabel(leftIcon);
        add(leftIconLabel, BorderLayout.WEST);


        textField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner() && placeholder != null) {
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
        textField.setOpaque(false);
        textField.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        textField.setFont(Theme.INPUT_FONT);
        textField.setForeground(Theme.TEXT_PRIMARY);
        textField.setCaretColor(Theme.PRIMARY);

        add(textField, BorderLayout.CENTER);


        clearButton = new JLabel(VectorIcons.createClearIcon(16, Theme.TEXT_MUTED));
        clearButton.setCursor(Theme.HAND_CURSOR);
        clearButton.setVisible(false);
        clearButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textField.setText("");
                textField.requestFocusInWindow();
            }
        });
        add(clearButton, BorderLayout.EAST);


        textField.addFocusListener(new FocusAdapter() {
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


        textField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                clearButton.setVisible(!textField.getText().isEmpty());
            }
            @Override public void insertUpdate(DocumentEvent e) { update(); }
            @Override public void removeUpdate(DocumentEvent e) { update(); }
            @Override public void changedUpdate(DocumentEvent e) { update(); }
        });


        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                textField.requestFocusInWindow();
            }
        });
    }

    public String getText() {
        return textField.getText();
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public JTextField getUnderlyingField() {
        return textField;
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
