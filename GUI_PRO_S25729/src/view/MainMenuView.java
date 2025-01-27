package view;

import controller.GameController;
import model.HighScore;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

// TODO: In this class:

// 1. TODO: Shows the main menu with start, high scores, and exit options
// 2. TODO: Start Game Button - Opens difficulty selection and starts a new game
// 3. TODO: Dropdown for selecting difficulty
// 4. TODO: Displays the high scores in a scrollable table

public class MainMenuView {
    private final GameController controller;
    private static final String HIGH_SCORES_FILE = System.getProperty("user.home") + File.separator + "high_scores.txt";

    public MainMenuView(GameController controller) {
        this.controller = controller;
    }


    // 1. TODO: Shows the main menu with start, high scores, and exit options
    public void show() {
        JFrame menuFrame = new JFrame("AntiPlague Game - Main Menu");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(600, 600);
        menuFrame.setLayout(new BorderLayout());
        menuFrame.getContentPane().setBackground(Color.BLACK);

        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon background = new ImageIcon(getClass().getResource("/image/background.png"));
                    g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
                } catch (NullPointerException e) {
                    System.err.println("Background image not found: " + e.getMessage());
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(menuFrame.getWidth(), 300));
        imagePanel.setBackground(Color.BLACK);
        menuFrame.add(imagePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.setBackground(Color.BLACK);

        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        Color buttonBackground = Color.DARK_GRAY;
        Color buttonForeground = Color.WHITE;


        // 2. TODO: Start Game Button - Opens difficulty selection and starts a new game
        JButton startButton = new JButton("Start Game");
        styleButton(startButton, buttonFont, buttonBackground, buttonForeground);

        startButton.addActionListener(e -> {
            JFrame difficultyFrame = new JFrame("Select Difficulty");
            difficultyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            difficultyFrame.setSize(400, 300);
            difficultyFrame.setLayout(new BoxLayout(difficultyFrame.getContentPane(), BoxLayout.Y_AXIS));

            difficultyFrame.getContentPane().setBackground(Color.BLACK);

            JLabel difficultyLabel = new JLabel("Choose Difficulty Level", SwingConstants.CENTER);
            difficultyLabel.setFont(new Font("Arial", Font.BOLD, 20));
            difficultyLabel.setForeground(Color.WHITE);
            difficultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            difficultyFrame.add(Box.createRigidArea(new Dimension(0, 20)));
            difficultyFrame.add(difficultyLabel);


            // 3. TODO: Dropdown for selecting difficulty
            JComboBox<String> difficultyDropdown = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});

            difficultyDropdown.setSelectedIndex(1);
            difficultyDropdown.setBackground(buttonBackground);
            difficultyDropdown.setForeground(buttonForeground);
            difficultyDropdown.setFont(buttonFont);
            difficultyDropdown.setMaximumSize(new Dimension(200, 30));
            difficultyDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);
            difficultyFrame.add(Box.createRigidArea(new Dimension(0, 20)));
            difficultyFrame.add(difficultyDropdown);

            JButton confirmButton = new JButton("Start Game");
            styleButton(confirmButton, buttonFont, buttonBackground, buttonForeground);
            confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            confirmButton.addActionListener(event -> {
                String selectedDifficulty = (String) difficultyDropdown.getSelectedItem();
                controller.setDifficulty(selectedDifficulty);
                difficultyFrame.dispose();
                menuFrame.dispose();
                controller.startNewGame();
            });

            difficultyFrame.add(Box.createRigidArea(new Dimension(0, 30)));
            difficultyFrame.add(confirmButton);

            difficultyFrame.setLocationRelativeTo(null);
            difficultyFrame.setVisible(true);
        });

        JButton highScoresButton = new JButton("High Scores");
        styleButton(highScoresButton, buttonFont, buttonBackground, buttonForeground);

        highScoresButton.addActionListener(e -> showHighScores());

        JButton exitButton = new JButton("Exit");
        styleButton(exitButton, buttonFont, buttonBackground, buttonForeground);

        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(highScoresButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(exitButton);

        menuFrame.add(buttonPanel, BorderLayout.SOUTH);
        menuFrame.setVisible(true);
    }

    private void styleButton(JButton button, Font font, Color background, Color foreground) {
        button.setFont(font);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button.setMaximumSize(new Dimension(200, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    //4. TODO: Displays the high scores in a scrollable table
    public void showHighScores() {
        List<HighScore> highScores = controller.getHighScores();
        highScores.sort((hs1, hs2) -> {
            int scoreComparison = Integer.compare(hs2.calculateScore(), hs1.calculateScore());
            if (scoreComparison == 0) {
                return Boolean.compare(hs2.isWin(), hs1.isWin());
            }
            return scoreComparison;
        });

        String[] columnNames = {"Name", "Score", "Time (s)", "Upgrades", "Result", "Difficulty"};
        Object[][] tableData = new Object[highScores.size()][6];
        for (int i = 0; i < highScores.size(); i++) {
            HighScore score = highScores.get(i);
            tableData[i][0] = score.getName();
            tableData[i][1] = score.calculateScore();
            tableData[i][2] = score.getTime();
            tableData[i][3] = score.getUpgradesUsed();
            tableData[i][4] = score.isWin() ? "Win" : "Lose";
            tableData[i][5] = score.getDifficulty();
        }

        JTable highScoresTable = new JTable(tableData, columnNames);
        highScoresTable.setEnabled(false);
        highScoresTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(highScoresTable);
        scrollPane.setPreferredSize(new Dimension(600, 300));

        JOptionPane.showMessageDialog(null, scrollPane, "High Scores", JOptionPane.INFORMATION_MESSAGE);
    }

}
