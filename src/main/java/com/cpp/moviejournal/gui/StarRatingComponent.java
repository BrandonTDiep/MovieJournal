package com.cpp.moviejournal.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Interactive Star Rating Component
 */
public class StarRatingComponent extends JPanel {
    
    private int rating = 0;
    private int maxRating = 5;
    private boolean editable = true;
    private StarRatingListener listener;
    
    private static final Color FILLED_COLOR = new Color(255, 193, 7); // Gold
    private static final Color EMPTY_COLOR = new Color(206, 212, 218); // Light gray
    private static final Color HOVER_COLOR = new Color(255, 235, 59); // Light gold
    
    public interface StarRatingListener {
        void ratingChanged(int newRating);
    }
    
    public StarRatingComponent() {
        this(0, true);
    }
    
    public StarRatingComponent(int initialRating, boolean editable) {
        this.rating = Math.max(0, Math.min(initialRating, maxRating));
        this.editable = editable;
        initializeComponent();
    }
    
    private void initializeComponent() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
        setOpaque(false);
        setPreferredSize(new Dimension(120, 25));
        
        for (int i = 1; i <= maxRating; i++) {
            StarButton star = new StarButton(i);
            add(star);
        }
    }
    
    public void setRating(int rating) {
        this.rating = Math.max(0, Math.min(rating, maxRating));
        updateStars();
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setEditable(boolean editable) {
        this.editable = editable;
        updateStars();
    }
    
    public void setStarRatingListener(StarRatingListener listener) {
        this.listener = listener;
    }
    
    private void updateStars() {
        for (Component comp : getComponents()) {
            if (comp instanceof StarButton) {
                StarButton star = (StarButton) comp;
                star.updateAppearance();
            }
        }
    }
    
    private void notifyRatingChanged(int newRating) {
        if (listener != null) {
            listener.ratingChanged(newRating);
        }
    }
    
    private class StarButton extends JButton {
        private final int starNumber;
        private boolean isHovered = false;
        
        public StarButton(int starNumber) {
            this.starNumber = starNumber;
            setPreferredSize(new Dimension(20, 20));
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            
            if (editable) {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        updateAppearance();
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        updateAppearance();
                    }
                    
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (editable) {
                            setRating(starNumber);
                            notifyRatingChanged(rating);
                        }
                    }
                });
            }
            
            updateAppearance();
        }
        
        public void updateAppearance() {
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int size = Math.min(getWidth(), getHeight()) - 4;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            
            // Determine star color
            Color starColor;
            if (starNumber <= rating) {
                starColor = FILLED_COLOR;
            } else if (isHovered && editable) {
                starColor = HOVER_COLOR;
            } else {
                starColor = EMPTY_COLOR;
            }
            
            // Draw star
            drawStar(g2d, x, y, size, starColor);
            
            g2d.dispose();
        }
        
        private void drawStar(Graphics2D g2d, int x, int y, int size, Color color) {
            g2d.setColor(color);
            
            // Create star shape
            int[] xPoints = new int[10];
            int[] yPoints = new int[10];
            
            double centerX = x + size / 2.0;
            double centerY = y + size / 2.0;
            double outerRadius = size / 2.0;
            double innerRadius = outerRadius * 0.4;
            
            for (int i = 0; i < 10; i++) {
                double angle = Math.PI / 2 + (i * Math.PI / 5);
                double radius = (i % 2 == 0) ? outerRadius : innerRadius;
                xPoints[i] = (int) (centerX + radius * Math.cos(angle));
                yPoints[i] = (int) (centerY + radius * Math.sin(angle));
            }
            
            g2d.fillPolygon(xPoints, yPoints, 10);
        }
    }
    
    // Static method to create a display-only star rating
    public static JPanel createDisplayStarRating(double rating) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        panel.setOpaque(false);
        
        int fullStars = (int) rating;
        boolean hasHalfStar = (rating - fullStars) >= 0.5;
        
        // Add full stars
        for (int i = 0; i < fullStars; i++) {
            panel.add(createStarLabel(FILLED_COLOR));
        }
        
        // Add half star if needed
        if (hasHalfStar) {
            panel.add(createHalfStarLabel());
        }
        
        // Add rating text
        JLabel ratingLabel = new JLabel(String.format("%.1f", rating));
        ratingLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        ratingLabel.setForeground(new Color(108, 117, 125));
        panel.add(ratingLabel);
        
        return panel;
    }
    
    private static JLabel createStarLabel(Color color) {
        JLabel star = new JLabel("★") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("★")) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString("★", x, y);
                g2d.dispose();
            }
        };
        star.setPreferredSize(new Dimension(20, 20));
        star.setOpaque(false);
        return star;
    }
    
    private static JLabel createHalfStarLabel() {
        JLabel halfStar = new JLabel("☆") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw half-filled star
                g2d.setColor(FILLED_COLOR);
                g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("★")) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                
                // Clip to show only left half
                g2d.setClip(0, 0, getWidth() / 2, getHeight());
                g2d.drawString("★", x, y);
                
                // Draw empty right half
                g2d.setClip(getWidth() / 2, 0, getWidth() / 2, getHeight());
                g2d.setColor(EMPTY_COLOR);
                g2d.drawString("★", x, y);
                
                g2d.dispose();
            }
        };
        halfStar.setPreferredSize(new Dimension(20, 20));
        halfStar.setOpaque(false);
        return halfStar;
    }
}
