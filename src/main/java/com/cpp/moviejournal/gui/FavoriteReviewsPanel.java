package com.cpp.moviejournal.gui;

import com.cpp.moviejournal.manager.MovieReviewManager;
import com.cpp.moviejournal.manager.ReviewChangeListener;
import com.cpp.moviejournal.model.MovieReview;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel that shows only the user's favorite reviews along with ticket preview if available.
 */
public class FavoriteReviewsPanel extends JPanel implements ReviewChangeListener {

    private MovieReviewManager movieReviewManager;
    private JTable favoritesTable;
    private JLabel ticketPreviewLabel;
    private JTextArea reviewDetailsArea;
    private DefaultTableModel tableModel;
    private List<MovieReview> cachedFavorites = new ArrayList<>();

    public FavoriteReviewsPanel() {
        initializeComponents();
        setupLayout();
        applyStyling();
        setupListeners();
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
        String[] columns = {"Title", "Director", "Genre", "Rating", "Date Watched", "Ticket"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        favoritesTable = new JTable(tableModel);
        ticketPreviewLabel = new JLabel("Select a review to preview ticket", SwingConstants.CENTER);
        reviewDetailsArea = new JTextArea();
        reviewDetailsArea.setEditable(false);
        reviewDetailsArea.setLineWrap(true);
        reviewDetailsArea.setWrapStyleWord(true);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(248, 249, 250));

        JLabel header = new JLabel("⭐ Favorite Reviews");
        header.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        header.setForeground(new Color(52, 58, 64));
        add(header, BorderLayout.NORTH);

        JScrollPane tableScroll = new JScrollPane(favoritesTable);
        tableScroll.setPreferredSize(new Dimension(0, 300));
        add(tableScroll, BorderLayout.CENTER);

        JPanel detailPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        detailPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        detailPanel.setOpaque(false);
        
        ticketPreviewLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        ticketPreviewLabel.setPreferredSize(new Dimension(250, 250));
        ticketPreviewLabel.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
        ticketPreviewLabel.setOpaque(true);
        ticketPreviewLabel.setBackground(Color.WHITE);
        detailPanel.add(ticketPreviewLabel);

        JPanel reviewPanel = new JPanel(new BorderLayout());
        reviewPanel.setOpaque(false);
        JLabel reviewHeader = new JLabel("Review Notes");
        reviewHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        reviewPanel.add(reviewHeader, BorderLayout.NORTH);
        reviewPanel.add(new JScrollPane(reviewDetailsArea), BorderLayout.CENTER);

        detailPanel.add(reviewPanel);

        add(detailPanel, BorderLayout.SOUTH);
    }

    private void applyStyling() {
        favoritesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        favoritesTable.setRowHeight(24);
        favoritesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        favoritesTable.getTableHeader().setBackground(new Color(52, 58, 64));
        favoritesTable.getTableHeader().setForeground(Color.WHITE);
        favoritesTable.setGridColor(new Color(206, 212, 218));
        favoritesTable.setShowGrid(true);
        favoritesTable.setSelectionBackground(new Color(0, 123, 255, 80));
    }

    private void setupListeners() {
        favoritesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = favoritesTable.getSelectedRow();
                if (row >= 0 && row < cachedFavorites.size()) {
                    updateDetails(cachedFavorites.get(row));
                }
            }
        });
    }

    private void updateDetails(MovieReview review) {
        reviewDetailsArea.setText(review.getReview());
        String imagePath = review.getTicketImagePath();
        if (imagePath == null || imagePath.isBlank()) {
            ticketPreviewLabel.setText("No ticket image");
            ticketPreviewLabel.setIcon(null);
            return;
        }
        File file = new File(imagePath);
        if (!file.exists()) {
            ticketPreviewLabel.setText("Ticket image missing");
            ticketPreviewLabel.setIcon(null);
            return;
        }
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            if (bufferedImage != null) {
                Image scaled = bufferedImage.getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                ticketPreviewLabel.setText("");
                ticketPreviewLabel.setIcon(new ImageIcon(scaled));
                ticketPreviewLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
            } else {
                ticketPreviewLabel.setText("Unable to preview");
                ticketPreviewLabel.setIcon(null);
            }
        } catch (IOException ex) {
            ticketPreviewLabel.setText("Unable to preview");
            ticketPreviewLabel.setIcon(null);
        }
    }

    public void refreshData() {
        if (movieReviewManager == null) {
            tableModel.setRowCount(0);
            ticketPreviewLabel.setText("No data");
            reviewDetailsArea.setText("");
            return;
        }
        cachedFavorites = movieReviewManager.getFavoriteReviews();
        tableModel.setRowCount(0);
        for (MovieReview review : cachedFavorites) {
            tableModel.addRow(new Object[]{
                    review.getTitle(),
                    review.getDirector(),
                    review.getGenre(),
                    String.format("%.1f", review.getRating()),
                    review.getDateWatchedAsString(),
                    review.getTicketImagePath() != null && !review.getTicketImagePath().isBlank() ? "🎟️" : ""
            });
        }

        if (!cachedFavorites.isEmpty()) {
            favoritesTable.setRowSelectionInterval(0, 0);
        } else {
            ticketPreviewLabel.setText("No favorites yet");
            ticketPreviewLabel.setIcon(null);
            reviewDetailsArea.setText("");
        }
    }

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
}

