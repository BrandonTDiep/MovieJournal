package com.cpp.moviejournal.gui;

import com.cpp.moviejournal.manager.MovieReviewManager;
import com.cpp.moviejournal.manager.ReviewChangeListener;
import com.cpp.moviejournal.model.MovieReview;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * Dashboard Panel showing user statistics and recent reviews
 */
public class DashboardPanel extends JPanel implements ReviewChangeListener {
    
    private MovieReviewManager movieReviewManager;
    
    // Statistics components
    private JLabel totalReviewsLabel;
    private JLabel averageRatingLabel;
    private JLabel theaterVisitsLabel;
    private JLabel recentReviewsLabel;
    
    // Recent reviews table
    private JTable recentReviewsTable;
    private JScrollPane recentReviewsScrollPane;
    
    // Quick action buttons
    private JButton addReviewButton;
    private JButton viewAllReviewsButton;
    
    public DashboardPanel() {
        initializeComponents();
        setupLayout();
        setupEventListeners();
        applyStyling();
    }
    
    public void setMovieReviewManager(MovieReviewManager manager) {
        if (this.movieReviewManager != null) {
            this.movieReviewManager.removeReviewChangeListener(this);
        }
        this.movieReviewManager = manager;
        if (this.movieReviewManager != null) {
            this.movieReviewManager.addReviewChangeListener(this);
        }
        refreshData();
    }
    
    private void initializeComponents() {
        // Statistics labels
        totalReviewsLabel = new JLabel("0");
        averageRatingLabel = new JLabel("0.0");
        theaterVisitsLabel = new JLabel("0");
        recentReviewsLabel = new JLabel("Recent Reviews");
        
        // Recent reviews table
        String[] columnNames = {"Title", "Director", "Genre", "Rating", "Date Watched"};
        Object[][] data = {};
        recentReviewsTable = new JTable(data, columnNames);
        recentReviewsScrollPane = new JScrollPane(recentReviewsTable);
        
        // Quick action buttons
        addReviewButton = new JButton("➕ Add New Review");
        viewAllReviewsButton = new JButton("📋 View All Reviews");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Top panel with statistics
        JPanel topPanel = createStatisticsPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with recent reviews
        JPanel centerPanel = createRecentReviewsPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel with quick actions
        JPanel bottomPanel = createQuickActionsPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Total reviews card
        JPanel totalReviewsCard = createStatCard("📊 Total Reviews", totalReviewsLabel, new Color(0, 123, 255));
        panel.add(totalReviewsCard);
        
        // Average rating card
        JPanel averageRatingCard = createStatCard("⭐ Average Rating", averageRatingLabel, new Color(40, 167, 69));
        panel.add(averageRatingCard);

        JPanel theaterVisitsCard = createStatCard("🎟️ Times You Went", theaterVisitsLabel, new Color(255, 193, 7));
        panel.add(theaterVisitsCard);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Add shadow effect
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 2, 2, 2),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
            )
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        titleLabel.setForeground(new Color(108, 117, 125));
        
        valueLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createRecentReviewsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(248, 249, 250));
        
        recentReviewsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        recentReviewsLabel.setForeground(new Color(52, 58, 64));
        
        headerPanel.add(recentReviewsLabel, BorderLayout.WEST);
        headerPanel.add(Box.createHorizontalGlue(), BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        recentReviewsScrollPane.setPreferredSize(new Dimension(0, 200));
        recentReviewsScrollPane.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218), 1));
        recentReviewsScrollPane.setBackground(Color.WHITE);
        
        panel.add(recentReviewsScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setBackground(new Color(248, 249, 250));
        
        // Style buttons
        styleButton(addReviewButton, new Color(40, 167, 69), Color.WHITE);
        styleButton(viewAllReviewsButton, new Color(0, 123, 255), Color.WHITE);
        
        panel.add(addReviewButton);
        panel.add(viewAllReviewsButton);
        
        return panel;
    }
    
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 40));
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
        // Event listeners will be set up by the main GUI
    }
    
    private void applyStyling() {
        // Table styling
        recentReviewsTable.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 12));
        recentReviewsTable.setRowHeight(25);
        recentReviewsTable.setSelectionBackground(new Color(0, 123, 255, 100));
        recentReviewsTable.setGridColor(new Color(206, 212, 218));
        recentReviewsTable.setShowGrid(true);
        recentReviewsTable.setIntercellSpacing(new Dimension(1, 1));
        
        // Make table non-editable
        recentReviewsTable.setDefaultEditor(Object.class, null);
        
        // Header styling
        recentReviewsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        recentReviewsTable.getTableHeader().setBackground(new Color(52, 58, 64));
        recentReviewsTable.getTableHeader().setForeground(Color.WHITE);
        recentReviewsTable.getTableHeader().setReorderingAllowed(false);
    }
    
    public void refreshData() {
        if (movieReviewManager == null) {
            return;
        }
        
        // Update statistics
        int totalReviews = movieReviewManager.getTotalReviews();
        double averageRating = movieReviewManager.getAverageRating();
        int theaterVisits = movieReviewManager.getTheaterVisitCount();
        
        totalReviewsLabel.setText(String.valueOf(totalReviews));
        averageRatingLabel.setText(String.format("%.1f", averageRating));
        theaterVisitsLabel.setText(String.valueOf(theaterVisits));
        
        // Update recent reviews table
        List<MovieReview> recentReviews = movieReviewManager.getAllMovies();
        
        int displayCount = Math.min(5, recentReviews.size());
        Object[][] data = new Object[displayCount][5];
        
        for (int i = 0; i < displayCount; i++) {
            MovieReview review = recentReviews.get(i);
            data[i][0] = review.getTitle();
            data[i][1] = review.getDirector();
            data[i][2] = review.getGenre();
            data[i][3] = createStarRatingDisplay(review.getRating());
            data[i][4] = review.getDateWatchedAsString();
        }
        
        String[] columnNames = {"Title", "Director", "Genre", "Rating", "Date Watched"};
        recentReviewsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        // ✅ Enable HTML rendering for the "Rating" column
        recentReviewsTable.getColumn("Rating").setCellRenderer(new HtmlTableCellRenderer());

        // Reapply table styling
        applyStyling();
    }

    // ReviewChangeListener implementation
    @Override
    public void onReviewAdded(MovieReview review) {
        SwingUtilities.invokeLater(this::refreshData);
    }

    @Override
    public void onReviewUpdated(MovieReview review) {
        SwingUtilities.invokeLater(this::refreshData);
    }

    @Override
    public void onReviewDeleted(int reviewId) {
        SwingUtilities.invokeLater(this::refreshData);
    }

    @Override
    public void onReviewsBulkDeleted(int count) {
        SwingUtilities.invokeLater(this::refreshData);
    }

    @Override
    public void onReviewsCleared() {
        SwingUtilities.invokeLater(this::refreshData);
    }

    private String createStarRatingDisplay(double rating) {
        StringBuilder stars = new StringBuilder();
        int fullStars = (int) rating;
        boolean hasHalfStar = (rating - fullStars) >= 0.5;
        
        // Add full stars
        for (int i = 0; i < fullStars; i++) {
            stars.append("★");
        }
        
        // Add half star if needed
        if (hasHalfStar) {
            stars.append("☆");
        }
        
        return stars.toString() + " (" + String.format("%.1f", rating) + ")";
    }

    private static class HtmlTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                    boolean isSelected, boolean hasFocus,
                                                    int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                String text = value.toString();
                label.setText(text);
                if (text.contains("★") || text.contains("☆")) {
                    label.setForeground(new Color(255, 193, 7)); // gold
                    label.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    label.setForeground(Color.BLACK);
                }
            }
            return label;
        }
    }

    
    // Getters for buttons (to be used by main GUI)
    public JButton getAddReviewButton() {
        return addReviewButton;
    }
    
    public JButton getViewAllReviewsButton() {
        return viewAllReviewsButton;
    }
}
