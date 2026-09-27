package ui.theme;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Reusable Design System & Theme for NexBank Digital Core.
 * Defines standard colors, typography, dimensions, and visual styling
 * used consistently across the entire application.
 */
public final class Theme {

    private Theme() {}

    // === Primary Palette (NexBank Signature Deep Emerald) ===
    public static final Color PRIMARY = new Color(0, 103, 71);           // #006747 - Deep Emerald Green
    public static final Color PRIMARY_HOVER = new Color(0, 85, 58);      // #00553A
    public static final Color PRIMARY_PRESSED = new Color(0, 66, 45);    // #00422D
    public static final Color PRIMARY_LIGHT = new Color(230, 244, 238);  // #E6F4EE
    public static final Color PRIMARY_BORDER = new Color(0, 103, 71, 70);

    // === Secondary / Accent Palette ===
    public static final Color SECONDARY_BTN_BG = new Color(229, 240, 253);       // #E5F0FD - Soft sky/ice blue
    public static final Color SECONDARY_BTN_HOVER = new Color(214, 230, 250);    // #D6E6FA
    public static final Color SECONDARY_BTN_PRESSED = new Color(200, 220, 245);  // #C8DCF5
    public static final Color ACCENT_TEAL = new Color(13, 148, 136);             // #0D9488 - Security tag teal
    public static final Color ALERT_BG = new Color(235, 243, 254);                // #EBF3FE - Security banner bg
    public static final Color ALERT_BORDER = new Color(216, 230, 248);            // #D8E6F8
    public static final Color PILL_BG = new Color(232, 238, 245);                 // #E8EEF5 - Top badge pill bg

    // === Background & Card Colors ===
    public static final Color BACKGROUND = new Color(243, 245, 248);     // #F3F5F8 - Cool slate canvas
    public static final Color CARD_BG = new Color(255, 255, 255);        // #FFFFFF - Pure white card
    public static final Color CARD_BORDER = new Color(226, 232, 240);    // #E2E8F0 - Subtle card border
    public static final Color CARD_SHADOW = new Color(15, 23, 42, 10);   // Soft ambient shadow

    // === Input Field Styling ===
    public static final Color INPUT_BG = new Color(241, 245, 249);       // #F1F5F9 - Light input fill
    public static final Color INPUT_BORDER = new Color(226, 232, 240);   // #E2E8F0
    public static final Color INPUT_BORDER_FOCUS = PRIMARY;              // #006747 - Focused stroke
    public static final Color INPUT_PLACEHOLDER = new Color(148, 163, 184); // #94A3B8

    // === Text Hierarchy ===
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);      // #0F172A - Dark navy / high contrast
    public static final Color TEXT_SECONDARY = new Color(51, 65, 85);    // #334155 - Medium dark
    public static final Color TEXT_MUTED = new Color(100, 116, 139);     // #64748B - Gray / description
    public static final Color TEXT_WHITE = Color.WHITE;

    // === Dimensions & Radii ===
    public static final int RADIUS_CARD = 20;
    public static final int RADIUS_BUTTON = 12;
    public static final int RADIUS_INPUT = 12;
    public static final int RADIUS_PILL = 20;

    // === Fonts ===
    public static final String FONT_FAMILY = getPreferredFontFamily();

    public static final Font TITLE_FONT = new Font(FONT_FAMILY, Font.BOLD, 22);
    public static final Font SUBTITLE_FONT = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font SECTION_FONT = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font LABEL_FONT = new Font(FONT_FAMILY, Font.BOLD, 12);
    public static final Font INPUT_FONT = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font SMALL_BUTTON_FONT = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font BADGE_FONT = new Font(FONT_FAMILY, Font.BOLD, 10);
    public static final Font CAPTION_FONT = new Font(FONT_FAMILY, Font.PLAIN, 11);
    public static final Font FOOTER_FONT = new Font(FONT_FAMILY, Font.PLAIN, 10);

    // === Cursors ===
    public static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
    public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

    /**
     * Enables supreme anti-aliasing and text rendering quality on Graphics2D.
     */
    public static void applyQualityRendering(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    private static String getPreferredFontFamily() {
        String[] candidates = {"Segoe UI", "Inter", "Roboto", "San Francisco", "Helvetica Neue", "Arial"};
        java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        java.util.Set<String> fontNames = new java.util.HashSet<>(java.util.Arrays.asList(ge.getAvailableFontFamilyNames()));
        for (String candidate : candidates) {
            if (fontNames.contains(candidate)) {
                return candidate;
            }
        }
        return Font.SANS_SERIF;
    }
}
