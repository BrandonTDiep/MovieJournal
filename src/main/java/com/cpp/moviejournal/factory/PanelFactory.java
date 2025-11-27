package com.cpp.moviejournal.factory;

import com.cpp.moviejournal.gui.DashboardPanel;
import com.cpp.moviejournal.gui.FavoriteReviewsPanel;
import com.cpp.moviejournal.gui.LoginPanel;
import com.cpp.moviejournal.gui.ReviewManagementPanel;
import com.cpp.moviejournal.gui.SignupPanel;
import com.cpp.moviejournal.gui.UserProfilePanel;
import com.cpp.moviejournal.manager.MovieReviewManager;
import com.cpp.moviejournal.manager.UserManager;

import javax.swing.*;

/**
 * Factory Pattern: Creates GUI panels based on panel type
 */
public class PanelFactory {
    
    /**
     * Creates a panel based on the specified panel type
     * @param panelType The type of panel to create
     * @param movieReviewManager Optional MovieReviewManager (can be null)
     * @param userManager Optional UserManager (can be null)
     * @return The created JPanel
     */
    public static JPanel createPanel(PanelType panelType, MovieReviewManager movieReviewManager, UserManager userManager) {
        return switch (panelType) {
            case LOGIN -> createLoginPanel();
            case SIGNUP -> createSignupPanel();
            case DASHBOARD -> createDashboardPanel(movieReviewManager);
            case REVIEW_MANAGEMENT -> createReviewManagementPanel(movieReviewManager);
            case FAVORITE_REVIEWS -> createFavoriteReviewsPanel(movieReviewManager);
            case USER_PROFILE -> createUserProfilePanel(userManager);
        };
    }
    
    private static JPanel createLoginPanel() {
        return new LoginPanel();
    }
    
    private static JPanel createSignupPanel() {
        return new SignupPanel();
    }
    
    private static JPanel createDashboardPanel(MovieReviewManager movieReviewManager) {
        DashboardPanel panel = new DashboardPanel();
        if (movieReviewManager != null) {
            panel.setMovieReviewManager(movieReviewManager);
        }
        return panel;
    }
    
    private static JPanel createReviewManagementPanel(MovieReviewManager movieReviewManager) {
        ReviewManagementPanel panel = new ReviewManagementPanel();
        if (movieReviewManager != null) {
            panel.setMovieReviewManager(movieReviewManager);
        }
        return panel;
    }
    
    private static JPanel createFavoriteReviewsPanel(MovieReviewManager movieReviewManager) {
        FavoriteReviewsPanel panel = new FavoriteReviewsPanel();
        if (movieReviewManager != null) {
            panel.setMovieReviewManager(movieReviewManager);
        }
        return panel;
    }
    
    private static JPanel createUserProfilePanel(UserManager userManager) {
        UserProfilePanel panel = new UserProfilePanel();
        if (userManager != null) {
            panel.setUserManager(userManager);
        }
        return panel;
    }
    
    /**
     * Enum for panel types
     */
    public enum PanelType {
        LOGIN,
        SIGNUP,
        DASHBOARD,
        REVIEW_MANAGEMENT,
        FAVORITE_REVIEWS,
        USER_PROFILE
    }
}


