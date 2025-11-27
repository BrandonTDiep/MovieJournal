package com.cpp.moviejournal.gui;

import com.cpp.moviejournal.factory.PanelFactory;
import com.cpp.moviejournal.manager.MovieReviewManager;
import com.cpp.moviejournal.manager.UserManager;
import com.cpp.moviejournal.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


/**
 * Main GUI Application for MovieJournal
 * Features: Login/Signup, CRUD operations, Search, Sort, User Management
 */
public class MovieJournalGUI extends JFrame {
    
    // Managers
    private UserManager userManager;
    private MovieReviewManager movieReviewManager;
    
    // Current user
    private User currentUser;
    
    // Main components
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // Login/Signup components
    private LoginPanel loginPanel;
    private SignupPanel signupPanel;
    
    // Main application components
    private DashboardPanel dashboardPanel;
    private ReviewManagementPanel reviewManagementPanel;
    private FavoriteReviewsPanel favoriteReviewsPanel;
    private UserProfilePanel userProfilePanel;
    
    // Navigation
    private JPanel navigationPanel;
    private JButton dashboardBtn, reviewsBtn, favoritesBtn, profileBtn, logoutBtn;
    
    public MovieJournalGUI() {
        // Show splash screen first
        SplashScreen splash = new SplashScreen();
        splash.showSplash();
        
        // Initialize after splash
        SwingUtilities.invokeLater(() -> {
            initializeManagers();
            initializeGUI();
            showLoginScreen();
        });
    }
    
    private void initializeManagers() {
        userManager = new UserManager();
        movieReviewManager = new MovieReviewManager();
    }
    
    private void initializeGUI() {
        setTitle("MovieJournal - Your Personal Movie Review Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Use default look and feel
        
        // Main panel with card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(248, 249, 250));
        
        // Initialize panels using Factory Pattern
        loginPanel = (LoginPanel) PanelFactory.createPanel(
            PanelFactory.PanelType.LOGIN, null, null);
        signupPanel = (SignupPanel) PanelFactory.createPanel(
            PanelFactory.PanelType.SIGNUP, null, null);
        dashboardPanel = (DashboardPanel) PanelFactory.createPanel(
            PanelFactory.PanelType.DASHBOARD, movieReviewManager, null);
        reviewManagementPanel = (ReviewManagementPanel) PanelFactory.createPanel(
            PanelFactory.PanelType.REVIEW_MANAGEMENT, movieReviewManager, null);
        favoriteReviewsPanel = (FavoriteReviewsPanel) PanelFactory.createPanel(
            PanelFactory.PanelType.FAVORITE_REVIEWS, movieReviewManager, null);
        userProfilePanel = (UserProfilePanel) PanelFactory.createPanel(
            PanelFactory.PanelType.USER_PROFILE, null, userManager);
        
        // Add panels to card layout
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(signupPanel, "SIGNUP");
        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(reviewManagementPanel, "REVIEWS");
        mainPanel.add(favoriteReviewsPanel, "FAVORITES");
        mainPanel.add(userProfilePanel, "PROFILE");
        
        // Create navigation panel
        createNavigationPanel();
        
        // Add components to frame
        setLayout(new BorderLayout());
        add(navigationPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        
        // Set up event listeners
        setupEventListeners();
    }
    
    private void createNavigationPanel() {
        navigationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navigationPanel.setBackground(new Color(52, 58, 64));
        navigationPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        navigationPanel.setVisible(false); // Hidden initially
        
        // Navigation buttons
        dashboardBtn = createNavButton("🏠 Dashboard", new Color(40, 167, 69));
        reviewsBtn = createNavButton("🎬 Reviews", new Color(0, 123, 255));
        favoritesBtn = createNavButton("⭐ Favorites", new Color(255, 193, 7));
        favoritesBtn.setForeground(Color.BLACK);
        profileBtn = createNavButton("👤 Profile", new Color(108, 117, 125));
        logoutBtn = createNavButton("🚪 Logout", new Color(220, 53, 69));
        
        navigationPanel.add(dashboardBtn);
        navigationPanel.add(reviewsBtn);
        navigationPanel.add(favoritesBtn);
        navigationPanel.add(profileBtn);
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(logoutBtn);
    }
    
    private JButton createNavButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(120, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void setupEventListeners() {
        // Login panel events
        loginPanel.setLoginListener((username, password) -> {
            User user = userManager.loginUser(username, password);
            if (user != null) {
                currentUser = user;
                movieReviewManager = new MovieReviewManager(user.getId());
                
                // Set managers for panels
                dashboardPanel.setMovieReviewManager(movieReviewManager);
                reviewManagementPanel.setMovieReviewManager(movieReviewManager);
                favoriteReviewsPanel.setMovieReviewManager(movieReviewManager);
                userProfilePanel.setUserManager(userManager);
                
                showMainApplication();
                showMessage("Welcome back, " + user.getUsername() + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showMessage("Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        loginPanel.setSignupListener(() -> showSignupScreen());
        
        // Signup panel events
        signupPanel.setSignupListener((username, email, password) -> {
            User newUser = new User(username, email, password);
            if (userManager.registerUser(newUser)) {
                showMessage("Account created successfully! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                showLoginScreen();
            } else {
                showMessage("Registration failed! Username or email may already exist.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        signupPanel.setBackToLoginListener(() -> showLoginScreen());
        
        // Navigation events
        dashboardBtn.addActionListener(e -> showDashboard());
        reviewsBtn.addActionListener(e -> showReviews());
        profileBtn.addActionListener(e -> showProfile());
        favoritesBtn.addActionListener(e -> showFavorites());
        logoutBtn.addActionListener(e -> logout());
        
        // Dashboard button events
        dashboardPanel.getAddReviewButton().addActionListener(e -> showReviews());
        dashboardPanel.getViewAllReviewsButton().addActionListener(e -> showReviews());
    }
    
    private void showLoginScreen() {
        cardLayout.show(mainPanel, "LOGIN");
        navigationPanel.setVisible(false);
        setTitle("MovieJournal - Login");
    }
    
    private void showSignupScreen() {
        cardLayout.show(mainPanel, "SIGNUP");
        navigationPanel.setVisible(false);
        setTitle("MovieJournal - Sign Up");
    }
    
    private void showMainApplication() {
        navigationPanel.setVisible(true);
        showDashboard();
        setTitle("MovieJournal - Welcome, " + currentUser.getUsername());
    }
    
    private void showDashboard() {
        cardLayout.show(mainPanel, "DASHBOARD");
        dashboardPanel.refreshData();
    }
    
    private void showReviews() {
        cardLayout.show(mainPanel, "REVIEWS");
        reviewManagementPanel.refreshData();
    }

    private void showFavorites() {
        cardLayout.show(mainPanel, "FAVORITES");
        favoriteReviewsPanel.refreshData();
    }
    
    private void showProfile() {
        cardLayout.show(mainPanel, "PROFILE");
        userProfilePanel.setUser(currentUser);
    }
    
    private void logout() {
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            currentUser = null;
            movieReviewManager = new MovieReviewManager();
            showLoginScreen();
        }
    }
    
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MovieJournalGUI().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error starting application: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
