package com.cpp.moviejournal.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Modern Signup Panel with beautiful design and validation
 */
public class SignupPanel extends JPanel {
    
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton signupButton;
    private JButton backToLoginButton;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    
    // Event listeners
    private SignupListener signupListener;
    private BackToLoginListener backToLoginListener;
    
    public interface SignupListener {
        void onSignup(String username, String email, String password);
    }
    
    public interface BackToLoginListener {
        void onBackToLogin();
    }
    
    public SignupPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
        applyStyling();
    }
    
    private void initializeComponents() {
        // Title and subtitle
        titleLabel = new JLabel("Create Account");
        subtitleLabel = new JLabel("Join MovieJournal and start reviewing movies!");
        
        // Input fields
        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        
        // Buttons
        signupButton = new JButton("Create Account");
        backToLoginButton = new JButton("Back to Login");
        
        // Set placeholders
        usernameField.setText("Enter your username");
        emailField.setText("Enter your email");
        passwordField.setText("Enter your password");
        confirmPasswordField.setText("Confirm your password");
        
        // Add focus listeners for placeholder effect
        setupPlaceholderEffect(usernameField, "Enter your username");
        setupPlaceholderEffect(emailField, "Enter your email");
        setupPlaceholderEffect(passwordField, "Enter your password");
        setupPlaceholderEffect(confirmPasswordField, "Confirm your password");
    }
    
    private void setupPlaceholderEffect(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(248, 249, 250));
        contentPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(titleLabel, gbc);
        
        // Subtitle
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        contentPanel.add(subtitleLabel, gbc);
        
        // Spacer
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.ipady = 15;
        contentPanel.add(Box.createVerticalStrut(15), gbc);
        
        // Username field
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 0;
        contentPanel.add(createFieldPanel("👤 Username", usernameField), gbc);
        
        // Email field
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        contentPanel.add(createFieldPanel("📧 Email", emailField), gbc);
        
        // Password field
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        contentPanel.add(createFieldPanel("🔒 Password", passwordField), gbc);
        
        // Confirm password field
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        contentPanel.add(createFieldPanel("🔒 Confirm Password", confirmPasswordField), gbc);
        
        // Buttons panel
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(createButtonsPanel(), gbc);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Add decorative elements
        add(createDecorativePanel(), BorderLayout.WEST);
        add(createDecorativePanel(), BorderLayout.EAST);
    }
    
    private JPanel createFieldPanel(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(3, 0, 3, 0));
        
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        fieldLabel.setForeground(new Color(52, 58, 64));
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(300, 45));
        
        panel.add(fieldLabel, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(new Color(248, 249, 250));
        
        // Style buttons
        styleButton(signupButton, new Color(40, 167, 69), Color.WHITE);
        styleButton(backToLoginButton, new Color(108, 117, 125), Color.WHITE);
        
        panel.add(signupButton);
        panel.add(backToLoginButton);
        
        return panel;
    }
    
    private JPanel createDecorativePanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(100, 0));
        panel.setBackground(new Color(52, 58, 64));
        
        // Add some decorative elements
        JLabel decorLabel = new JLabel("🎭");
        decorLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        decorLabel.setForeground(new Color(255, 255, 255, 100));
        decorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        decorLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        panel.setLayout(new BorderLayout());
        panel.add(decorLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(140, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }
    
    private void setupEventListeners() {
        // Signup button
        signupButton.addActionListener(e -> performSignup());
        
        // Back to login button
        backToLoginButton.addActionListener(e -> {
            if (backToLoginListener != null) {
                backToLoginListener.onBackToLogin();
            }
        });
        
        // Enter key support
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSignup();
                }
            }
        };
        
        usernameField.addKeyListener(enterKeyAdapter);
        emailField.addKeyListener(enterKeyAdapter);
        passwordField.addKeyListener(enterKeyAdapter);
        confirmPasswordField.addKeyListener(enterKeyAdapter);
    }
    
    private void performSignup() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validate input
        if (username.isEmpty() || username.equals("Enter your username")) {
            showError("Please enter your username!");
            usernameField.requestFocus();
            return;
        }
        
        if (username.length() < 3) {
            showError("Username must be at least 3 characters long!");
            usernameField.requestFocus();
            return;
        }
        
        if (email.isEmpty() || email.equals("Enter your email")) {
            showError("Please enter your email!");
            emailField.requestFocus();
            return;
        }
        
        if (!isValidEmail(email)) {
            showError("Please enter a valid email address!");
            emailField.requestFocus();
            return;
        }
        
        if (password.isEmpty() || password.equals("Enter your password")) {
            showError("Please enter your password!");
            passwordField.requestFocus();
            return;
        }
        
        if (password.length() < 6) {
            showError("Password must be at least 6 characters long!");
            passwordField.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match!");
            confirmPasswordField.requestFocus();
            return;
        }
        
        // Call signup listener
        if (signupListener != null) {
            signupListener.onSignup(username, email, password);
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && 
               !email.startsWith("@") && !email.endsWith("@") &&
               email.indexOf("@") < email.lastIndexOf(".");
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void applyStyling() {
        // Title styling
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(52, 58, 64));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Subtitle styling
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    // Public methods for setting listeners
    public void setSignupListener(SignupListener listener) {
        this.signupListener = listener;
    }
    
    public void setBackToLoginListener(BackToLoginListener listener) {
        this.backToLoginListener = listener;
    }
    
    // Method to clear fields
    public void clearFields() {
        usernameField.setText("Enter your username");
        usernameField.setForeground(Color.GRAY);
        emailField.setText("Enter your email");
        emailField.setForeground(Color.GRAY);
        passwordField.setText("Enter your password");
        passwordField.setForeground(Color.GRAY);
        confirmPasswordField.setText("Confirm your password");
        confirmPasswordField.setForeground(Color.GRAY);
    }
}
