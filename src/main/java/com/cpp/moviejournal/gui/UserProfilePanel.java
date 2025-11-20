package com.cpp.moviejournal.gui;

import com.cpp.moviejournal.manager.UserManager;
import com.cpp.moviejournal.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * User Profile Panel for managing user account settings
 */
public class UserProfilePanel extends JPanel {
    
    private UserManager userManager;
    private User currentUser;
    
    // Profile display components
    private JLabel usernameLabel;
    private JLabel emailLabel;
    private JLabel memberSinceLabel;
    private JLabel lastLoginLabel;
    private JLabel accountStatusLabel;
    
    // Edit profile components
    private JTextField editUsernameField;
    private JTextField editEmailField;
    private JButton editProfileButton;
    private JButton saveProfileButton;
    private JButton cancelEditButton;
    
    // Change password components
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton changePasswordButton;
    private JButton savePasswordButton;
    private JButton cancelPasswordButton;
    
    // Account management components
    private JButton deactivateAccountButton;
    private JButton logoutButton;
    
    // Panels
    private JPanel profileDisplayPanel;
    private JPanel profileEditPanel;
    private JPanel passwordChangePanel;
    private JPanel accountManagementPanel;
    
    // State (tracked for UI state management)
    @SuppressWarnings("unused")
    private boolean isEditingProfile = false;
    @SuppressWarnings("unused")
    private boolean isChangingPassword = false;
    
    public UserProfilePanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
        applyStyling();
    }
    
    public void setUserManager(UserManager manager) {
        this.userManager = manager;
    }
    
    public void setUser(User user) {
        this.currentUser = user;
        refreshProfileData();
    }
    
    private void initializeComponents() {
        // Profile display components
        usernameLabel = new JLabel();
        emailLabel = new JLabel();
        memberSinceLabel = new JLabel();
        lastLoginLabel = new JLabel();
        accountStatusLabel = new JLabel();
        
        // Edit profile components
        editUsernameField = new JTextField(20);
        editEmailField = new JTextField(20);
        editProfileButton = new JButton("✏️ Edit Profile");
        saveProfileButton = new JButton("💾 Save Changes");
        cancelEditButton = new JButton("❌ Cancel");
        
        // Change password components
        currentPasswordField = new JPasswordField(20);
        newPasswordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        changePasswordButton = new JButton("🔒 Change Password");
        savePasswordButton = new JButton("💾 Save Password");
        cancelPasswordButton = new JButton("❌ Cancel");
        
        // Account management components
        deactivateAccountButton = new JButton("⚠️ Deactivate Account");
        logoutButton = new JButton("🚪 Logout");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Main content panel
        JPanel mainContentPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        mainContentPanel.setBackground(new Color(248, 249, 250));
        
        // Create panels
        profileDisplayPanel = createProfileDisplayPanel();
        profileEditPanel = createProfileEditPanel();
        passwordChangePanel = createPasswordChangePanel();
        accountManagementPanel = createAccountManagementPanel();
        
        mainContentPanel.add(profileDisplayPanel);
        mainContentPanel.add(profileEditPanel);
        mainContentPanel.add(passwordChangePanel);
        mainContentPanel.add(accountManagementPanel);
        
        add(mainContentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createProfileDisplayPanel() {
        JPanel panel = createCardPanel("👤 Profile Information");
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(usernameLabel, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(emailLabel, gbc);
        
        // Member since
        gbc.gridx = 0; gbc.gridy = 2;
        contentPanel.add(new JLabel("Member since:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(memberSinceLabel, gbc);
        
        // Last login
        gbc.gridx = 0; gbc.gridy = 3;
        contentPanel.add(new JLabel("Last login:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(lastLoginLabel, gbc);
        
        // Account status
        gbc.gridx = 0; gbc.gridy = 4;
        contentPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(accountStatusLabel, gbc);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createProfileEditPanel() {
        JPanel panel = createCardPanel("✏️ Edit Profile");
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Username field
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(editUsernameField, gbc);
        
        // Email field
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(editEmailField, gbc);
        
        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.add(editProfileButton);
        buttonsPanel.add(saveProfileButton);
        buttonsPanel.add(cancelEditButton);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(buttonsPanel, gbc);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Initially hide edit fields and buttons
        editUsernameField.setVisible(false);
        editEmailField.setVisible(false);
        saveProfileButton.setVisible(false);
        cancelEditButton.setVisible(false);
        
        return panel;
    }
    
    private JPanel createPasswordChangePanel() {
        JPanel panel = createCardPanel("🔒 Change Password");
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Current password
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(new JLabel("Current Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(currentPasswordField, gbc);
        
        // New password
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(newPasswordField, gbc);
        
        // Confirm password
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(confirmPasswordField, gbc);
        
        // Buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.add(changePasswordButton);
        buttonsPanel.add(savePasswordButton);
        buttonsPanel.add(cancelPasswordButton);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(buttonsPanel, gbc);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Initially hide password fields and buttons
        currentPasswordField.setVisible(false);
        newPasswordField.setVisible(false);
        confirmPasswordField.setVisible(false);
        savePasswordButton.setVisible(false);
        cancelPasswordButton.setVisible(false);
        
        return panel;
    }
    
    private JPanel createAccountManagementPanel() {
        JPanel panel = createCardPanel("⚙️ Account Management");
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Deactivate account button
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(deactivateAccountButton, gbc);
        
        // Logout button
        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(logoutButton, gbc);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Add shadow effect
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 2, 2, 2),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
                BorderFactory.createEmptyBorder(13, 13, 13, 13)
            )
        ));
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        titleLabel.setForeground(new Color(52, 58, 64));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private void setupEventListeners() {
        // Profile edit events
        editProfileButton.addActionListener(e -> startEditingProfile());
        saveProfileButton.addActionListener(e -> saveProfileChanges());
        cancelEditButton.addActionListener(e -> cancelEditingProfile());
        
        // Password change events
        changePasswordButton.addActionListener(e -> startChangingPassword());
        savePasswordButton.addActionListener(e -> savePasswordChanges());
        cancelPasswordButton.addActionListener(e -> cancelChangingPassword());
        
        // Account management events
        deactivateAccountButton.addActionListener(e -> deactivateAccount());
        logoutButton.addActionListener(e -> logout());
    }
    
    private void applyStyling() {
        // Label styling
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 12);
        usernameLabel.setFont(labelFont);
        emailLabel.setFont(labelFont);
        memberSinceLabel.setFont(labelFont);
        lastLoginLabel.setFont(labelFont);
        accountStatusLabel.setFont(labelFont);
        
        // Field styling
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 12);
        editUsernameField.setFont(fieldFont);
        editEmailField.setFont(fieldFont);
        currentPasswordField.setFont(fieldFont);
        newPasswordField.setFont(fieldFont);
        confirmPasswordField.setFont(fieldFont);
        
        // Button styling
        styleButton(editProfileButton, new Color(255, 193, 7), Color.BLACK);
        styleButton(saveProfileButton, new Color(40, 167, 69), Color.WHITE);
        styleButton(cancelEditButton, new Color(220, 53, 69), Color.WHITE);
        styleButton(changePasswordButton, new Color(0, 123, 255), Color.WHITE);
        styleButton(savePasswordButton, new Color(40, 167, 69), Color.WHITE);
        styleButton(cancelPasswordButton, new Color(220, 53, 69), Color.WHITE);
        styleButton(deactivateAccountButton, new Color(220, 53, 69), Color.WHITE);
        styleButton(logoutButton, new Color(108, 117, 125), Color.WHITE);
    }
    
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(140, 30));
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
    
    private void startEditingProfile() {
        isEditingProfile = true;
        editUsernameField.setText(currentUser.getUsername());
        editEmailField.setText(currentUser.getEmail());
        
        editUsernameField.setVisible(true);
        editEmailField.setVisible(true);
        saveProfileButton.setVisible(true);
        cancelEditButton.setVisible(true);
        editProfileButton.setVisible(false);
    }
    
    private void saveProfileChanges() {
        String newUsername = editUsernameField.getText().trim();
        String newEmail = editEmailField.getText().trim();
        
        // Validate input
        if (newUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!isValidEmail(newEmail)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if username or email already exists (if changed)
        if (!newUsername.equals(currentUser.getUsername()) && userManager.userExists(newUsername)) {
            JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!newEmail.equals(currentUser.getEmail()) && userManager.emailExists(newEmail)) {
            JOptionPane.showMessageDialog(this, "Email already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Update user object
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);

        // Persist to database via UserManager
        if (userManager == null) {
            JOptionPane.showMessageDialog(this, "User manager not configured. Cannot save profile.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean updated = userManager.updateUserProfile(currentUser);
        if (!updated) {
            // Could be uniqueness conflict or DB error
            JOptionPane.showMessageDialog(this, "Failed to update profile. Username or email may already be in use.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Refresh UI state
        refreshProfileData();
        cancelEditingProfile();

        JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void cancelEditingProfile() {
        isEditingProfile = false;
        editUsernameField.setVisible(false);
        editEmailField.setVisible(false);
        saveProfileButton.setVisible(false);
        cancelEditButton.setVisible(false);
        editProfileButton.setVisible(true);
    }
    
    private void startChangingPassword() {
        isChangingPassword = true;
        currentPasswordField.setVisible(true);
        newPasswordField.setVisible(true);
        confirmPasswordField.setVisible(true);
        savePasswordButton.setVisible(true);
        cancelPasswordButton.setVisible(true);
        changePasswordButton.setVisible(false);
    }
    
    private void savePasswordChanges() {
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validate input
        if (currentPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your current password!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a new password!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "New password must be at least 6 characters long!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verify current password
        if (!currentUser.verifyPassword(currentPassword)) {
            JOptionPane.showMessageDialog(this, "Current password is incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Update password
        if (userManager.updatePassword(currentUser.getUsername(), currentPassword, newPassword)) {
            currentUser.setPlainTextPassword(newPassword);
            JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            cancelChangingPassword();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to change password!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelChangingPassword() {
        isChangingPassword = false;
        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
        currentPasswordField.setVisible(false);
        newPasswordField.setVisible(false);
        confirmPasswordField.setVisible(false);
        savePasswordButton.setVisible(false);
        cancelPasswordButton.setVisible(false);
        changePasswordButton.setVisible(true);
    }
    
    private void deactivateAccount() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to deactivate your account?\nThis action cannot be undone!",
            "Confirm Account Deactivation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            if (userManager.deactivateUser(currentUser.getUsername())) {
                JOptionPane.showMessageDialog(this, "Account deactivated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                logout();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to deactivate account!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void logout() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            // This will be handled by the main GUI
            SwingUtilities.getWindowAncestor(this).dispatchEvent(
                new java.awt.event.WindowEvent(
                    SwingUtilities.getWindowAncestor(this), 
                    java.awt.event.WindowEvent.WINDOW_CLOSING
                )
            );
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && 
               !email.startsWith("@") && !email.endsWith("@") &&
               email.indexOf("@") < email.lastIndexOf(".");
    }
    
    private void refreshProfileData() {
        if (currentUser == null) return;
        
        usernameLabel.setText(currentUser.getUsername());
        emailLabel.setText(currentUser.getEmail());
        memberSinceLabel.setText(currentUser.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        
        if (currentUser.getLastLogin() != null) {
            lastLoginLabel.setText(currentUser.getLastLogin().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        } else {
            lastLoginLabel.setText("Never");
        }
        
        accountStatusLabel.setText(currentUser.isActive() ? "Active" : "Inactive");
        accountStatusLabel.setForeground(currentUser.isActive() ? new Color(40, 167, 69) : new Color(220, 53, 69));
    }
}
