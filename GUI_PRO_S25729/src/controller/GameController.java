package controller;

import model.*;
import view.Difficulty;
import view.GameView;
import view.MainMenuView;

import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.List;
import java.util.*;

// TODO: In this class:

// 1. TODO: Redirects to the main menu, stopping all active timers
// 2. TODO: Initializes a new game with the selected difficulty
// 3. TODO: Button creation
// 4. TODO: Handles the end of the game
// 5. TODO: Initializes the available upgrades
// 6. TODO: High score handling

public class GameController {
    private WorldMap worldMap;
    private GameView gameView;
    private String difficulty;
    private int points = 0;
    private int timeElapsed = 0;
    private Timer gameTimer;
    private JFrame gameFrame;
    private Map<String, Upgrade> upgrades;
    private List<HighScore> highScores;
    private double infectionRate;
    private boolean gameEnded = false;
    private List<JButton> upgradeButtons = new ArrayList<>();
    private Timer recoveryTimer;
    private Set<String> unlockedUpgrades = new HashSet<>();

    private List<Upgrade> availableUpgrades = new ArrayList<>();

    private Set<String> usedUpgrades = new HashSet<>();

    private int upgradesUsed = 0;


    public GameController() {
        worldMap = new WorldMap();
        initializeUpgrades();

        this.highScores = loadHighScores();
    }

    public void start() {
        new MainMenuView(this).show();
    }

    // 1. TODO: Redirects to the main menu, stopping all active timers
    private void redirectToMainMenu() {
        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer = null;
        }

        if (gameView != null) {
            gameView.stopInfectionSpread();
        }

        stopRecoveryTimers();

        gameFrame.dispose();
        new MainMenuView(this).show();
    }


    private void stopRecoveryTimers() {
        if (recoveryTimer != null) {
            recoveryTimer.stop();
            recoveryTimer = null;
        }
        System.out.println("Recovery timers stopped.");
    }

    // 2. TODO: Initializes a new game with the selected difficulty
    public void startNewGame() {
        stopAllTimers();

        gameEnded = false;
        points = 0;
        upgradesUsed = 0;
        timeElapsed = 0;
        infectionRate = 0.0;

        if (worldMap != null) {
            worldMap.resetCountries();
        }

        Difficulty gameDifficulty = Difficulty.valueOf(difficulty.toUpperCase());
        Country randomCountry = worldMap.getCountries().get(new Random().nextInt(worldMap.getCountries().size()));
        randomCountry.infect(1);

        gameView = new GameView(worldMap, gameDifficulty);
        gameView.startInfectionSpread();

        if (gameFrame != null) {
            gameFrame.dispose();
        }


        gameFrame = new JFrame("AntiPlague Game - Gameplay");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setLayout(new BorderLayout());

        Dimension maxSize = new Dimension(1085, 800);
        gameFrame.setSize(maxSize);
        gameFrame.setPreferredSize(maxSize);
        gameFrame.setMaximumSize(maxSize);

        JPanel leftPanel = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 800);
            }

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(900, 900);
            }
        };
        leftPanel.add(gameView, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 3. TODO: Button creation
        Color startColor = new Color(255, 0, 0);
        Color endColor = new Color(0, 0, 0);

        int totalButtons = upgrades.size();
        int redStep = (endColor.getRed() - startColor.getRed()) / totalButtons;
        int greenStep = (endColor.getGreen() - startColor.getGreen()) / totalButtons;
        int blueStep = (endColor.getBlue() - startColor.getBlue()) / totalButtons;

        int buttonIndex = 0;

        for (Map.Entry<String, Upgrade> entry : upgrades.entrySet()) {
            String upgradeName = entry.getKey();
            Upgrade upgrade = entry.getValue();

            int red = Math.max(0, Math.min(255, startColor.getRed() + buttonIndex * redStep));
            int green = Math.max(0, Math.min(255, startColor.getGreen() + buttonIndex * greenStep));
            int blue = Math.max(0, Math.min(255, startColor.getBlue() + buttonIndex * blueStep));
            Color buttonColor = new Color(red, green, blue);

            JButton upgradeButton = new JButton("<html>" + upgradeName + "<br>(" + upgrade.getCost() + " Points)</html>");
            upgradeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            upgradeButton.setPreferredSize(new Dimension(200, 60));
            upgradeButton.setMinimumSize(new Dimension(150, 40));
            upgradeButton.setMaximumSize(new Dimension(200, 70));
            upgradeButton.setForeground(Color.WHITE);
            upgradeButton.setBackground(buttonColor);
            upgradeButton.setOpaque(true);
            upgradeButton.setBorderPainted(false);
            upgradeButton.addActionListener(e -> handleUpgrade(upgradeName, upgrade));
            rightPanel.add(upgradeButton, gbc);

            buttonIndex++;
        }

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerSize(10);
        splitPane.setDividerLocation(800);
        splitPane.setResizeWeight(0.8);
        splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(true);
        gameFrame.add(splitPane, BorderLayout.CENTER);

        InputMap inputMap = gameFrame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = gameFrame.getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("ctrl shift Q"), "interruptGame");
        actionMap.put("interruptGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        gameFrame,
                        "Are you sure you want to interrupt and quit the game? Your score will not be saved.",
                        "Confirm Interruption",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    System.out.println("Game interrupted! Closing without saving...");
                    stopAllTimers();
                    if (gameFrame != null) {
                        gameFrame.dispose();
                    }
                    new MainMenuView(GameController.this).show();
                }
            }
        });

        gameFrame.setMinimumSize(new Dimension(1085, 800));

        gameFrame.pack();
        gameFrame.setVisible(true);

        startGameTimer();
    }

    private void handleUpgrade(String upgradeName, Upgrade upgrade) {
        if (points < upgrade.getCost()) {
            JOptionPane.showMessageDialog(
                    gameFrame,
                    "Not enough points for \"" + upgradeName + "\"!",
                    "Insufficient Points",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        points -= upgrade.getCost();
        updateTitleBar();

        upgrade.apply(this);
        usedUpgrades.add(upgradeName);

        upgradesUsed++;
        System.out.println("Upgrade \"" + upgradeName + "\" activated. Total upgrades used: " + upgradesUsed);
    }

    private void stopAllTimers() {
        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer = null;
        }

        if (gameView != null) {
            gameView.stopInfectionSpread();
        }

        for (Country country : worldMap.getCountries()) {
            country.stopRecovery();
        }

        System.out.println("All timers stopped.");
    }



    private void startGameTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        timeElapsed = 0;
        gameTimer = new Timer(1000, e -> {
            timeElapsed++;
            updatePoints();
            updateTitleBar();

            if (!gameEnded && timeElapsed >= 10) {
                double infectionRate = gameView.calculateOverallInfectionRate();

                if (infectionRate == 0) {
                    gameEnded = true;
                    handleGameEnd("You Win! The infection has been eradicated.", true, timeElapsed, upgradesUsed);
                } else if (infectionRate > 90) {
                    gameEnded = true;
                    handleGameEnd("You Lose! The infection rate exceeded 90%.", false, timeElapsed, upgradesUsed);
                }
            }
        });

        gameTimer.start();
    }

    // 4. TODO: Handles the end of the game
    private void handleGameEnd(String message, boolean isWin, int timeElapsed, int upgradesUsed) {
        stopAllTimers();

        if (gameTimer != null) {
            gameTimer.stop();
        }

        if (gameView != null) {
            gameView.stopInfectionSpread();
        }

        gameEnded = true;

        String imagePath = isWin ? "/image/youwin.jpg" : "/image/gameover.jpg";
        ImageIcon imageIcon = null;

        try {
            imageIcon = new ImageIcon(getClass().getResource(imagePath));
            Image image = imageIcon.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(image);
        } catch (NullPointerException e) {
            System.err.println("Failed to load image: " + imagePath);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);

        if (imageIcon != null) {
            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(imageLabel);
        }

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(messageLabel);

        JTextField playerNameField = new JTextField();
        playerNameField.setPreferredSize(new Dimension(200, 30));
        playerNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(playerNameField);

        int option = JOptionPane.showConfirmDialog(
                null,
                panel,
                isWin ? "Congratulations!" : "Game Over",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            String playerName = playerNameField.getText();
            saveHighScore(playerName, timeElapsed, upgradesUsed, isWin, difficulty.toUpperCase());
        }

        redirectToMainMenu();
    }

    // 5. TODO: Initializes the available upgrades
    private void initializeUpgrades() {
        upgrades = new LinkedHashMap<>();
        upgrades.put("Hygiene Promotion", Upgrade.hygienePromotion());
        upgrades.put("Travel Restriction", Upgrade.travelRestriction());
        upgrades.put("Close Airlines", Upgrade.closeAirlines());
        upgrades.put("Harder to Spread in Hot Countries (Spain, Italy, Greece, Turkey)", Upgrade.harderToSpreadInHotCountries());
        upgrades.put("Harder to Spread in Cold Countries (Russia, Ukraine, Norway, Poland)", Upgrade.harderToSpreadInColdCountries());
        upgrades.put("Scientific Research", Upgrade.scientificResearch());
        upgrades.put("Antigen Suppressors", Upgrade.antigenSuppressors());
        upgrades.put("Quarantine Zone", Upgrade.quarantineZones());
        upgrades.put("Vaccination Campaign", Upgrade.vaccinationCampaign());
        upgrades.put("Cure Deployment", Upgrade.cureDeployment());

    }

    // 6. TODO: High score handling
    private static final String HIGH_SCORES_FILE = "high_scores.txt";

    public List<HighScore> loadHighScores() {
        List<HighScore> highScores = new ArrayList<>();
        File file = new File(HIGH_SCORES_FILE);

        if (!file.exists()) {
            System.out.println("High scores file not found. Creating a new one.");
            saveHighScores(highScores);
            return highScores;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            highScores = (List<HighScore>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return highScores;
    }


    public void saveHighScores(List<HighScore> highScores) {
        File file = new File(HIGH_SCORES_FILE);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(highScores);
            System.out.println("High scores saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sortHighScores() {
        highScores.sort((hs1, hs2) -> {
            int scoreComparison = Integer.compare(hs2.calculateScore(), hs1.calculateScore());
            if (scoreComparison == 0) {
                return Boolean.compare(hs2.isWin(), hs1.isWin());
            }
            return scoreComparison;
        });
    }

    public List<HighScore> getHighScores() {
        return highScores;
    }

    public void addHighScore(HighScore highScore) {
        highScores.add(highScore);
        sortHighScores();
        saveHighScores(highScores);
    }
    public void saveHighScore(String playerName, int time, int upgradesUsed, boolean isWin, String difficulty) {
        HighScore highScore = new HighScore(playerName, time, upgradesUsed, isWin, difficulty);
        addHighScore(highScore);
    }

    ////

    public Icon addPoints(int pointsToAdd) {
        this.points += pointsToAdd;
        gameView.updatePointsLabel(points);
        return null;
    }

    public void unlockUpgrade(String upgradeName) {
        unlockedUpgrades.add(upgradeName);
    }

    public boolean isUpgradeUsed(String upgradeName) {
        return usedUpgrades.contains(upgradeName);
    }

    public void markUpgradeAsUsed(String upgradeName) {
        usedUpgrades.add(upgradeName);
    }


    private void updatePoints() {
        if (timeElapsed >= 30) {
            points += 3;
        } else if (timeElapsed >= 15) {
            points += 2;
        } else {
            points += 1;
        }
    }

    private void updateTitleBar() {
        if (gameFrame == null || gameView == null) {
            return;
        }
        double infectionRate = gameView.calculateOverallInfectionRate();
        gameFrame.setTitle("Time: " + timeElapsed + "s | Points: " + points + " | Infection Rate: " + String.format("%.2f%%", infectionRate));
    }


    public JFrame getGameFrame() {
        return gameFrame;
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }

    public GameView getGameView() {
        return gameView;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

}
