package com.cpp.moviejournal.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Splash Screen for MovieJournal Application
 */
public class SplashScreen extends JWindow {
    
    private static final int SPLASH_WIDTH = 500;
    private static final int SPLASH_HEIGHT = 300;
    private static final int SPLASH_DURATION = 3000; // 3 seconds
    
    public SplashScreen() {
        initializeSplash();
    }
    
    private void initializeSplash() {
        setSize(SPLASH_WIDTH, SPLASH_HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        
        // Create main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(0, 123, 255),
                    getWidth(), getHeight(), new Color(40, 167, 69)
                );
                
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        
        // Title
        JLabel titleLabel = new JLabel("MovieJournal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 20, 20, 20));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Your Personal Movie Review Manager", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Loading indicator
        JPanel loadingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loadingPanel.setOpaque(false);
        
        JLabel loadingLabel = new JLabel("Loading...");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loadingLabel.setForeground(Color.WHITE);
        
        // Animated dots
        JLabel dotsLabel = new JLabel("...");
        dotsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dotsLabel.setForeground(Color.WHITE);
        
        // Animation timer for dots
        Timer animationTimer = new Timer(500, new ActionListener() {
            private int dotCount = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                dotCount = (dotCount + 1) % 4;
                String dots = ".".repeat(dotCount);
                dotsLabel.setText(dots);
            }
        });
        animationTimer.start();
        
        loadingPanel.add(loadingLabel);
        loadingPanel.add(dotsLabel);
        
        // Version info
        JLabel versionLabel = new JLabel("Version 1.0.0", SwingConstants.CENTER);
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(255, 255, 255, 150));
        versionLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add components
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(subtitleLabel, BorderLayout.CENTER);
        mainPanel.add(loadingPanel, BorderLayout.SOUTH);
        mainPanel.add(versionLabel, BorderLayout.PAGE_END);
        
        add(mainPanel);
        
        // Auto-close timer
        Timer closeTimer = new Timer(SPLASH_DURATION, e -> {
            animationTimer.stop();
            dispose();
        });
        closeTimer.setRepeats(false);
        closeTimer.start();
    }
    
    public void showSplash() {
        setVisible(true);
    }
}


