package com.cpp.moviejournal.gui;

import com.cpp.moviejournal.manager.MovieReviewManager;
import com.cpp.moviejournal.manager.ReviewChangeListener;
import com.cpp.moviejournal.model.MovieReview;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Comprehensive Review Management Panel with CRUD operations, search, and sort
 */
public class ReviewManagementPanel extends JPanel implements ReviewChangeListener {
    
    private static final Path TICKET_STORAGE = Path.of(System.getProperty("user.home"), ".moviejournal", "tickets");
    private static final int PREVIEW_SIZE = 180;

    private MovieReviewManager movieReviewManager;
    
    // Main components
    private JTable reviewsTable;
    private JScrollPane tableScrollPane;
    private TableRowSorter<DefaultTableModel> tableSorter;
    
    // Search and filter components
    private JTextField searchField;
    private JComboBox<String> sortComboBox;
    private JButton searchButton;
    private JButton clearSearchButton;
    
    // Action buttons
    private JButton addReviewButton;
    private JButton editReviewButton;
    private JButton deleteReviewButton;
    private JButton toggleFavoriteButton;
    private JButton bulkDeleteButton;
    private JButton selectAllButton;
    private JButton clearSelectionButton;
    private JButton refreshButton;
    
    // Review dialog components
    private JDialog reviewDialog;
    private JTextField titleField;
    private JTextField directorField;
    private JTextField genreField;
    private StarRatingComponent starRatingComponent;
    private JTextArea reviewTextArea;
    private JTextField dateField;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton uploadTicketButton;
    private JButton clearTicketButton;
    private JLabel ticketPreviewLabel;
    private JLabel ticketFileLabel;
    private JCheckBox favoriteCheckBox;
    
    // Current editing review
    private MovieReview currentEditingReview;
    private String currentTicketPath;
    
    // Bulk selection
    private List<MovieReview> allReviews;
    private boolean[] selectedReviews;
    
    public ReviewManagementPanel() {
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
        // Table setup
        String[] columnNames = {"Select", "ID", "Title", "Director", "Genre", "Rating", "Favorite", "Date Watched", "Review"};
        Object[][] data = {};
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only the checkbox column is editable
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) {
                    return Boolean.class; // Checkbox column
                }
                return super.getColumnClass(column);
            }
        };
        
        reviewsTable = new JTable(tableModel);
        // Keep ID in the model for internal lookup, but hide it from the user-visible table
        // Column index 1 is the ID column in the model
        try {
            reviewsTable.removeColumn(reviewsTable.getColumnModel().getColumn(1));
        } catch (Exception ignored) {
            // If removal fails for any reason, continue without throwing — model still contains ID
        }
        tableScrollPane = new JScrollPane(reviewsTable);
        tableSorter = new TableRowSorter<>(tableModel);
        reviewsTable.setRowSorter(tableSorter);
        
        // Search and filter components
        searchField = new JTextField(20);
        sortComboBox = new JComboBox<>(new String[]{
            "Date (Newest)", "Date (Oldest)", "Rating (High)", "Rating (Low)", 
            "Title (A-Z)", "Title (Z-A)"
        });
        searchButton = new JButton("🔍 Search");
        clearSearchButton = new JButton("🗑️ Clear");
        
        // Action buttons
        addReviewButton = new JButton("➕ Add Review");
        editReviewButton = new JButton("✏️ Edit");
        deleteReviewButton = new JButton("🗑️ Delete");
        toggleFavoriteButton = new JButton("⭐ Toggle Favorite");
        bulkDeleteButton = new JButton("🗑️ Bulk Delete");
        selectAllButton = new JButton("☑️ Select All");
        clearSelectionButton = new JButton("☐ Clear Selection");
        refreshButton = new JButton("🔄 Refresh");
        
        // Initialize review dialog
        initializeReviewDialog();
    }
    
    private void initializeReviewDialog() {
        reviewDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Movie Review", true);
        reviewDialog.setSize(550, 720);
        reviewDialog.setLocationRelativeTo(this);
        
        // Dialog components
        titleField = new JTextField(20);
        directorField = new JTextField(20);
        genreField = new JTextField(20);
        starRatingComponent = new StarRatingComponent(5, true);
        reviewTextArea = new JTextArea(8, 20);
        dateField = new JTextField(20);
        saveButton = new JButton("💾 Save");
        cancelButton = new JButton("❌ Cancel");
        uploadTicketButton = new JButton("📷 Upload Ticket");
        clearTicketButton = new JButton("🧹 Clear Ticket");
        ticketPreviewLabel = new JLabel("No ticket uploaded", SwingConstants.CENTER);
        ticketFileLabel = new JLabel("No file selected");
        favoriteCheckBox = new JCheckBox("⭐ Mark as Favorite");
        
        // Set default date
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        
        // Setup dialog layout
        setupDialogLayout();
    }
    
    private void setupDialogLayout() {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("🎬 Title:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(titleField, gbc);
        
        // Director
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("🎭 Director:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(directorField, gbc);
        
        // Genre
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("🎪 Genre:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(genreField, gbc);
        
        // Rating
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("⭐ Rating:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(starRatingComponent, gbc);
        
        // Date
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("📅 Date Watched:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(dateField, gbc);
        
        // Review text
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("📝 Review:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JScrollPane reviewScrollPane = new JScrollPane(reviewTextArea);
        reviewScrollPane.setPreferredSize(new Dimension(300, 150));
        formPanel.add(reviewScrollPane, gbc);

        // Favorite checkbox
        gbc.gridx = 1; gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        favoriteCheckBox.setOpaque(false);
        formPanel.add(favoriteCheckBox, gbc);

        // Ticket controls
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("🎟️ Ticket:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel ticketButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        ticketButtonsPanel.setOpaque(false);
        ticketButtonsPanel.add(uploadTicketButton);
        ticketButtonsPanel.add(clearTicketButton);
        formPanel.add(ticketButtonsPanel, gbc);

        // Ticket file label
        gbc.gridx = 1; gbc.gridy = 8;
        ticketFileLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        formPanel.add(ticketFileLabel, gbc);

        // Ticket preview
        gbc.gridx = 1; gbc.gridy = 9;
        gbc.fill = GridBagConstraints.BOTH;
        ticketPreviewLabel.setPreferredSize(new Dimension(PREVIEW_SIZE, PREVIEW_SIZE));
        ticketPreviewLabel.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
        formPanel.add(ticketPreviewLabel, gbc);
        
        dialogPanel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);
        dialogPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        reviewDialog.add(dialogPanel);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Top panel with search and controls
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with table
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel with action buttons
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(new Color(248, 249, 250));
        
        searchPanel.add(new JLabel("🔍 Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearSearchButton);
        
        // Sort panel
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        sortPanel.setBackground(new Color(248, 249, 250));
        
        sortPanel.add(new JLabel("📊 Sort by:"));
        sortPanel.add(sortComboBox);
        sortPanel.add(refreshButton);
        
        panel.add(searchPanel, BorderLayout.WEST);
        panel.add(sortPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        
        // Table
        tableScrollPane.setPreferredSize(new Dimension(0, 400));
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218), 1));
        
        panel.add(tableScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        // Left side - CRUD buttons
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(new Color(248, 249, 250));
        leftPanel.add(addReviewButton);
        leftPanel.add(editReviewButton);
        leftPanel.add(deleteReviewButton);
        leftPanel.add(toggleFavoriteButton);
        
        // Right side - Bulk operations
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(new Color(248, 249, 250));
        rightPanel.add(selectAllButton);
        rightPanel.add(clearSelectionButton);
        rightPanel.add(bulkDeleteButton);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void setupEventListeners() {
        // Search functionality
        searchButton.addActionListener(e -> performSearch());
        clearSearchButton.addActionListener(e -> clearSearch());
        searchField.addActionListener(e -> performSearch());
        
        // Sort functionality
        sortComboBox.addActionListener(e -> performSort());
        
        // Action buttons
        addReviewButton.addActionListener(e -> showAddReviewDialog());
        editReviewButton.addActionListener(e -> showEditReviewDialog());
        deleteReviewButton.addActionListener(e -> deleteSelectedReview());
        toggleFavoriteButton.addActionListener(e -> toggleFavoriteSelectedReview());
        bulkDeleteButton.addActionListener(e -> bulkDeleteReviews());
        selectAllButton.addActionListener(e -> selectAllReviews());
        clearSelectionButton.addActionListener(e -> clearSelection());
        refreshButton.addActionListener(e -> refreshData());
        
        // Dialog buttons
        saveButton.addActionListener(e -> saveReview());
        cancelButton.addActionListener(e -> reviewDialog.dispose());
        uploadTicketButton.addActionListener(e -> handleTicketUpload());
        clearTicketButton.addActionListener(e -> clearTicketSelection());
        
        // Table selection
        reviewsTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = reviewsTable.getSelectedRow() != -1;
            editReviewButton.setEnabled(hasSelection);
            deleteReviewButton.setEnabled(hasSelection);
            toggleFavoriteButton.setEnabled(hasSelection);
        });

        // Double-click to edit: select the clicked row and open the edit dialog
        reviewsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                    int viewRow = reviewsTable.rowAtPoint(e.getPoint());
                    if (viewRow != -1) {
                        // Select the row so other listeners/update UI know about selection
                        reviewsTable.setRowSelectionInterval(viewRow, viewRow);
                        // Reuse existing logic to show the edit dialog
                        showEditReviewDialog();
                    }
                }
            }
        });
        
        // Table model listener for checkbox changes
        reviewsTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 0 && selectedReviews != null) { // Checkbox column
                int row = e.getFirstRow();
                if (row >= 0 && row < selectedReviews.length) {
                    Boolean value = (Boolean) reviewsTable.getModel().getValueAt(row, 0);
                    selectedReviews[row] = value != null ? value : false;
                }
            }
        });
    }
    
    private void applyStyling() {
        // Table styling
        reviewsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        reviewsTable.setRowHeight(25);
        reviewsTable.setSelectionBackground(new Color(0, 123, 255, 100));
        reviewsTable.setGridColor(new Color(206, 212, 218));
        reviewsTable.setShowGrid(true);
        reviewsTable.setIntercellSpacing(new Dimension(1, 1));
        
        // Header styling
        reviewsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        reviewsTable.getTableHeader().setBackground(new Color(52, 58, 64));
        reviewsTable.getTableHeader().setForeground(Color.WHITE);
        reviewsTable.getTableHeader().setReorderingAllowed(false);
        
        // Button styling
        styleButton(addReviewButton, new Color(40, 167, 69), Color.WHITE);
        styleButton(editReviewButton, new Color(255, 193, 7), Color.BLACK);
        styleButton(deleteReviewButton, new Color(220, 53, 69), Color.WHITE);
        styleButton(toggleFavoriteButton, new Color(255, 193, 7), Color.BLACK);
        styleButton(bulkDeleteButton, new Color(220, 53, 69), Color.WHITE);
        styleButton(selectAllButton, new Color(0, 123, 255), Color.WHITE);
        styleButton(clearSelectionButton, new Color(108, 117, 125), Color.WHITE);
        styleButton(refreshButton, new Color(108, 117, 125), Color.WHITE);
        styleButton(searchButton, new Color(0, 123, 255), Color.WHITE);
        styleButton(clearSearchButton, new Color(108, 117, 125), Color.WHITE);
        styleButton(saveButton, new Color(40, 167, 69), Color.WHITE);
        styleButton(cancelButton, new Color(220, 53, 69), Color.WHITE);
        styleButton(uploadTicketButton, new Color(0, 123, 255), Color.WHITE);
        styleButton(clearTicketButton, new Color(108, 117, 125), Color.WHITE);
        
        // Initially disable edit and delete buttons
        editReviewButton.setEnabled(false);
        deleteReviewButton.setEnabled(false);
        toggleFavoriteButton.setEnabled(false);
        
        // Field styling
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        directorField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        genreField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        reviewTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ticketPreviewLabel.setOpaque(true);
        ticketPreviewLabel.setBackground(Color.WHITE);
        favoriteCheckBox.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        favoriteCheckBox.setForeground(new Color(52, 58, 64));
        
        // Combo box styling
        sortComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
    
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 30));
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
    
    private void performSearch() {
        String query = searchField.getText().trim();
        if (movieReviewManager == null) return;
        
        List<MovieReview> results = movieReviewManager.searchReviews(query);
        updateTable(results);
    }
    
    private void clearSearch() {
        searchField.setText("");
        refreshData();
    }
    
    private void performSort() {
        String sortOption = (String) sortComboBox.getSelectedItem();
        if (movieReviewManager == null) return;
        
        List<MovieReview> sortedReviews = movieReviewManager.getSortedReviews(sortOption);
        updateTable(sortedReviews);
    }
    
    private void showAddReviewDialog() {
        currentEditingReview = null;
        clearDialogFields();
        reviewDialog.setTitle("Add New Review");
        reviewDialog.setVisible(true);
    }
    
    private void showEditReviewDialog() {
        int selectedRow = reviewsTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // Get the actual model row (accounting for sorting and checkbox column)
        int modelRow = reviewsTable.convertRowIndexToModel(selectedRow);
        DefaultTableModel model = (DefaultTableModel) reviewsTable.getModel();
        
        // Find the review by ID (ID is now in column 1)
        int reviewId = (Integer) model.getValueAt(modelRow, 1);
        if (allReviews == null) {
            allReviews = movieReviewManager.getAllMovies();
        }
        
        for (MovieReview review : allReviews) {
            if (review.getId() == reviewId) {
                currentEditingReview = review;
                break;
            }
        }
        
        if (currentEditingReview != null) {
            populateDialogFields(currentEditingReview);
            reviewDialog.setTitle("Edit Review");
            reviewDialog.setVisible(true);
        }
    }
    
    private void deleteSelectedReview() {
        int selectedRow = reviewsTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this review?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            // Get the actual model row (accounting for checkbox column)
            int modelRow = reviewsTable.convertRowIndexToModel(selectedRow);
            DefaultTableModel model = (DefaultTableModel) reviewsTable.getModel();
            int reviewId = (Integer) model.getValueAt(modelRow, 1); // ID is now in column 1
            
            // Find and delete the review
            if (allReviews != null) {
                for (MovieReview review : allReviews) {
                    if (review.getId() == reviewId) {
                        movieReviewManager.deleteReview(review);
                        break;
                    }
                }
            }
            
            refreshData();
            JOptionPane.showMessageDialog(this, "Review deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void saveReview() {
        System.out.println("Save review button clicked!"); // Debug
        try {
            // Validate input
            if (titleField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(reviewDialog, "Please enter a title!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (directorField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(reviewDialog, "Please enter a director!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (genreField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(reviewDialog, "Please enter a genre!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (movieReviewManager == null) {
                JOptionPane.showMessageDialog(reviewDialog, "Movie review manager not initialized!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
        // Parse date
        try {
            String[] dateParts = dateField.getText().trim().split("/");
            if (dateParts.length == 3) {
                int month = Integer.parseInt(dateParts[0]);
                int day = Integer.parseInt(dateParts[1]);
                int year = Integer.parseInt(dateParts[2]);
                LocalDate.of(year, month, day); // Validate date format
            } else {
                throw new NumberFormatException();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(reviewDialog, "Please enter a valid date (MM/dd/yyyy)!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create or update review
        int userId = movieReviewManager != null ? movieReviewManager.getCurrentUserId() : 0;
        double rating = starRatingComponent.getRating();
        
        System.out.println("Creating review with userId: " + userId + ", rating: " + rating); // Debug
        
        MovieReview review = new MovieReview(
            userId,
            titleField.getText().trim(),
            directorField.getText().trim(),
            genreField.getText().trim(),
            rating,
            dateField.getText().trim()
        );
        
        review.setReview(reviewTextArea.getText().trim());
        // add ticket image path and set favorite status to review
        review.setTicketImagePath(currentTicketPath);
        review.setFavorite(favoriteCheckBox.isSelected());
        
        if (currentEditingReview != null) {
            // Update existing review
            review.setId(currentEditingReview.getId());
            movieReviewManager.updateReview(currentEditingReview, review);
            JOptionPane.showMessageDialog(reviewDialog, "Review updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Add new review
            movieReviewManager.addReview(review);
            JOptionPane.showMessageDialog(reviewDialog, "Review added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        
        reviewDialog.dispose();
        refreshData();
        
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(reviewDialog, 
                "Error saving review: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearDialogFields() {
        titleField.setText("");
        directorField.setText("");
        genreField.setText("");
        starRatingComponent.setRating(5);
        reviewTextArea.setText("");
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        favoriteCheckBox.setSelected(false);
        clearTicketSelection();
    }
    
    private void populateDialogFields(MovieReview review) {
        titleField.setText(review.getTitle());
        directorField.setText(review.getDirector());
        genreField.setText(review.getGenre());
        starRatingComponent.setRating((int) review.getRating());
        reviewTextArea.setText(review.getReview());
        dateField.setText(review.getDateWatchedAsString());
        favoriteCheckBox.setSelected(review.isFavorite());
        currentTicketPath = review.getTicketImagePath();
        updateTicketPreview(currentTicketPath);
    }
    
    private void updateTable(List<MovieReview> reviews) {
        DefaultTableModel model = (DefaultTableModel) reviewsTable.getModel();
        model.setRowCount(0);
        
        for (int i = 0; i < reviews.size(); i++) {
            MovieReview review = reviews.get(i);
            Object[] row = {
                selectedReviews != null ? selectedReviews[i] : false, // Checkbox
                review.getId(),
                review.getTitle(),
                review.getDirector(),
                review.getGenre(),
                createStarRatingDisplay(review.getRating()),
                review.isFavorite() ? "★ Favorite" : "☆",
                review.getDateWatchedAsString(),
                review.getReview().length() > 50 ? 
                    review.getReview().substring(0, 50) + "..." : 
                    review.getReview()
            };
            model.addRow(row);
        }
        reviewsTable.getColumn("Rating").setCellRenderer(new HtmlTableCellRenderer());
        reviewsTable.getColumn("Favorite").setCellRenderer(new HtmlTableCellRenderer());

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
    
    private void bulkDeleteReviews() {
        if (allReviews == null || selectedReviews == null) {
            JOptionPane.showMessageDialog(this, "No reviews to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Count selected reviews
        int selectedCount = 0;
        List<MovieReview> reviewsToDelete = new ArrayList<>();
        
        for (int i = 0; i < selectedReviews.length; i++) {
            if (selectedReviews[i]) {
                selectedCount++;
                reviewsToDelete.add(allReviews.get(i));
            }
        }
        
        if (selectedCount == 0) {
            JOptionPane.showMessageDialog(this, "Please select reviews to delete!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirm deletion
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete " + selectedCount + " review(s)?\nThis action cannot be undone!",
            "Confirm Bulk Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                int deletedCount = movieReviewManager.deleteReviews(reviewsToDelete);
                JOptionPane.showMessageDialog(this, 
                    "Successfully deleted " + deletedCount + " review(s)!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error deleting reviews: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void selectAllReviews() {
        if (selectedReviews == null) return;
        
        for (int i = 0; i < selectedReviews.length; i++) {
            selectedReviews[i] = true;
        }
        updateTableCheckboxes();
    }
    
    private void clearSelection() {
        if (selectedReviews == null) return;
        
        for (int i = 0; i < selectedReviews.length; i++) {
            selectedReviews[i] = false;
        }
        updateTableCheckboxes();
    }
    
    private void toggleFavoriteSelectedReview() {
        if (movieReviewManager == null) {
            return;
        }
        int selectedRow = reviewsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a review first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = reviewsTable.convertRowIndexToModel(selectedRow);
        DefaultTableModel model = (DefaultTableModel) reviewsTable.getModel();
        Integer reviewId = (Integer) model.getValueAt(modelRow, 1);
        if (reviewId == null) {
            return;
        }
        
        MovieReview target = null;
        if (allReviews != null) {
            for (MovieReview review : allReviews) {
                if (review.getId() == reviewId) {
                    target = review;
                    break;
                }
            }
        }
        
        if (target == null) {
            JOptionPane.showMessageDialog(this, "Unable to locate the selected review.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean newStatus = !target.isFavorite();
        movieReviewManager.setFavoriteStatus(target, newStatus); // db update favorite status
        String message = newStatus ? "Review added to favorites!" : "Review removed from favorites.";
        JOptionPane.showMessageDialog(this, message, "Favorites", JOptionPane.INFORMATION_MESSAGE);
        refreshData();
    }
    
    private void updateTableCheckboxes() {
        DefaultTableModel model = (DefaultTableModel) reviewsTable.getModel();
        for (int i = 0; i < selectedReviews.length; i++) {
            model.setValueAt(selectedReviews[i], i, 0);
        }
    }
    
    public void refreshData() {
        if (movieReviewManager == null) return;
        
        allReviews = movieReviewManager.getAllMovies();
        selectedReviews = new boolean[allReviews.size()];
        updateTable(allReviews);
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

    private void handleTicketUpload() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Ticket Image");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif", "webp"));
        int result = chooser.showOpenDialog(reviewDialog);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            try {
                Files.createDirectories(TICKET_STORAGE);
                String extension = "";
                String name = selectedFile.getName();
                int idx = name.lastIndexOf('.');
                if (idx > 0) {
                    extension = name.substring(idx);
                }
                Path target = TICKET_STORAGE.resolve(UUID.randomUUID() + extension);
                Files.copy(selectedFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
                currentTicketPath = target.toString();
                updateTicketPreview(currentTicketPath); 
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(reviewDialog,
                        "Unable to save ticket image: " + ex.getMessage(),
                        "Upload Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearTicketSelection() {
        currentTicketPath = null;
        updateTicketPreview(null);
    }

    private void updateTicketPreview(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            ticketPreviewLabel.setText("No ticket uploaded");
            ticketPreviewLabel.setIcon(null);
            ticketFileLabel.setText("No file selected");
            return;
        }

        File file = new File(imagePath);
        ticketFileLabel.setText(file.getName());
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            if (bufferedImage != null) {
                Image scaled = bufferedImage.getScaledInstance(PREVIEW_SIZE, PREVIEW_SIZE, Image.SCALE_SMOOTH);
                ticketPreviewLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
                ticketPreviewLabel.setText("");
                ticketPreviewLabel.setIcon(new ImageIcon(scaled));
            } else {
                ticketPreviewLabel.setText("Unable to preview");
                ticketPreviewLabel.setIcon(null);
            }
        } catch (IOException e) {
            ticketPreviewLabel.setText("Unable to preview");
            ticketPreviewLabel.setIcon(null);
        }
    }
}