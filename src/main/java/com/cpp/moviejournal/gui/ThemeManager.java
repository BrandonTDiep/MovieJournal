package com.cpp.moviejournal.gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Theme Manager for consistent styling across the application
 */
public class ThemeManager {
    
    // Color palette
    public static final Color PRIMARY_COLOR = new Color(0, 123, 255);
    public static final Color SECONDARY_COLOR = new Color(108, 117, 125);
    public static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    public static final Color WARNING_COLOR = new Color(255, 193, 7);
    public static final Color DANGER_COLOR = new Color(220, 53, 69);
    public static final Color INFO_COLOR = new Color(23, 162, 184);
    
    public static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    public static final Color CARD_BACKGROUND = Color.WHITE;
    public static final Color BORDER_COLOR = new Color(206, 212, 218);
    public static final Color TEXT_PRIMARY = new Color(52, 58, 64);
    public static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    public static final Color TEXT_MUTED = new Color(134, 142, 150);
    
    // Fonts
    public static final Font FONT_PRIMARY = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 18);
    
    // Borders
    public static final Border CARD_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER_COLOR, 1),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    );
    
    public static final Border INPUT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(BORDER_COLOR, 2),
        BorderFactory.createEmptyBorder(8, 12, 8, 12)
    );
    
    public static final Border FOCUSED_INPUT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
        BorderFactory.createEmptyBorder(8, 12, 8, 12)
    );
    
    /**
     * Apply modern button styling
     */
    public static void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(FONT_BOLD);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }
    
    /**
     * Apply primary button styling
     */
    public static void stylePrimaryButton(JButton button) {
        styleButton(button, PRIMARY_COLOR, Color.WHITE);
        button.setPreferredSize(new Dimension(120, 35));
    }
    
    /**
     * Apply secondary button styling
     */
    public static void styleSecondaryButton(JButton button) {
        styleButton(button, SECONDARY_COLOR, Color.WHITE);
        button.setPreferredSize(new Dimension(120, 35));
    }
    
    /**
     * Apply success button styling
     */
    public static void styleSuccessButton(JButton button) {
        styleButton(button, SUCCESS_COLOR, Color.WHITE);
        button.setPreferredSize(new Dimension(120, 35));
    }
    
    /**
     * Apply danger button styling
     */
    public static void styleDangerButton(JButton button) {
        styleButton(button, DANGER_COLOR, Color.WHITE);
        button.setPreferredSize(new Dimension(120, 35));
    }
    
    /**
     * Apply warning button styling
     */
    public static void styleWarningButton(JButton button) {
        styleButton(button, WARNING_COLOR, Color.BLACK);
        button.setPreferredSize(new Dimension(120, 35));
    }
    
    /**
     * Apply modern text field styling
     */
    public static void styleTextField(JTextField field) {
        field.setFont(FONT_PRIMARY);
        field.setBorder(INPUT_BORDER);
        field.setBackground(CARD_BACKGROUND);
        field.setForeground(TEXT_PRIMARY);
        
        // Add focus effect
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(FOCUSED_INPUT_BORDER);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(INPUT_BORDER);
            }
        });
    }
    
    /**
     * Apply modern text area styling
     */
    public static void styleTextArea(JTextArea area) {
        area.setFont(FONT_PRIMARY);
        area.setBorder(INPUT_BORDER);
        area.setBackground(CARD_BACKGROUND);
        area.setForeground(TEXT_PRIMARY);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
    }
    
    /**
     * Apply modern password field styling
     */
    public static void stylePasswordField(JPasswordField field) {
        styleTextField(field);
    }
    
    /**
     * Apply modern combo box styling
     */
    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(FONT_PRIMARY);
        comboBox.setBorder(INPUT_BORDER);
        comboBox.setBackground(CARD_BACKGROUND);
        comboBox.setForeground(TEXT_PRIMARY);
    }
    
    /**
     * Apply modern table styling
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_PRIMARY);
        table.setRowHeight(25);
        table.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 100));
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setDefaultEditor(Object.class, null);
        
        // Header styling
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(TEXT_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
    }
    
    /**
     * Apply modern label styling
     */
    public static void styleLabel(JLabel label, LabelType type) {
        switch (type) {
            case TITLE:
                label.setFont(FONT_TITLE);
                label.setForeground(TEXT_PRIMARY);
                break;
            case SUBTITLE:
                label.setFont(FONT_SUBTITLE);
                label.setForeground(TEXT_SECONDARY);
                break;
            case HEADING:
                label.setFont(FONT_HEADING);
                label.setForeground(TEXT_PRIMARY);
                break;
            case PRIMARY:
                label.setFont(FONT_PRIMARY);
                label.setForeground(TEXT_PRIMARY);
                break;
            case SECONDARY:
                label.setFont(FONT_PRIMARY);
                label.setForeground(TEXT_SECONDARY);
                break;
            case MUTED:
                label.setFont(FONT_PRIMARY);
                label.setForeground(TEXT_MUTED);
                break;
        }
    }
    
    /**
     * Create a modern card panel
     */
    public static JPanel createCardPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(CARD_BORDER);
        
        if (title != null && !title.isEmpty()) {
            JLabel titleLabel = new JLabel(title);
            styleLabel(titleLabel, LabelType.HEADING);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            panel.add(titleLabel, BorderLayout.NORTH);
        }
        
        return panel;
    }
    
    /**
     * Create a modern input panel with label and field
     */
    public static JPanel createInputPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        JLabel label = new JLabel(labelText);
        styleLabel(label, LabelType.PRIMARY);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Apply modern panel styling
     */
    public static void stylePanel(JPanel panel) {
        panel.setBackground(BACKGROUND_COLOR);
    }
    
    /**
     * Create a gradient background
     */
    public static void applyGradientBackground(JPanel panel, Color startColor, Color endColor) {
        panel.setOpaque(false);
        
        // Override paintComponent to draw gradient
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                int w = getWidth();
                int h = getHeight();
                
                GradientPaint gp = new GradientPaint(0, 0, startColor, w, h, endColor);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                
                g2d.dispose();
            }
        };
        
        gradientPanel.setLayout(panel.getLayout());
        
        // Copy all components from original panel to gradient panel
        Component[] components = panel.getComponents();
        for (Component component : components) {
            panel.remove(component);
            gradientPanel.add(component);
        }
        
        // Replace the panel
        Container parent = panel.getParent();
        if (parent != null) {
            parent.remove(panel);
            parent.add(gradientPanel);
        }
    }
    
    /**
     * Label types for consistent styling
     */
    public enum LabelType {
        TITLE, SUBTITLE, HEADING, PRIMARY, SECONDARY, MUTED
    }
}


