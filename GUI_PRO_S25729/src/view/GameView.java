

package view;

import model.Country;
import model.TransportType;
import model.WorldMap;

import javax.imageio.ImageIO;
import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;


//  TODO: In this class:

// 1. TODO: Fields
// 2. TODO: Paints the map and all game elements
// 3. TODO: Paints countries and connections
// 4. TODO: Draw connections to other countries
// 5. TODO: Highlight countries based on upgrades
// 8. TODO: Highlights countries based on upgrade effects
// 9. TODO: Transport animation logic here

public class GameView extends JPanel {

    // 1. TODO: Fields for managing the game view and functionality
    private Difficulty difficulty;
    private WorldMap worldMap;
    private BufferedImage mapImage;
    public static final int ORIGINAL_WIDTH = 1000;
    public static final int ORIGINAL_HEIGHT = 800;

    public JLabel pointsLabel;
    private Timer infectionTimer;

    private List<Country> quarantineHighlightedCountries = new ArrayList<>();
    private List<Country> hygieneHighlightedCountries = new ArrayList<>();
    private Map<String, Image> globalIcons = new HashMap<>(); // TODO: Icons for upgrades

    private List<Country> curedHighlightedCountries = new ArrayList<>();

    private List<Country> vaccinatedHighlightedCountries = new ArrayList<>();

    public int hotUpgradeUses = 0;
    public int coldUpgradeUses = 0;
    private int hotUpgradeLevel = 0;
    private int coldUpgradeLevel = 0;
    private static final int MAX_UPGRADE_LEVEL = 5;


    private List<Country> coldHighlightedCountries = new ArrayList<>();

    private List<Country> hotHighlightedCountries = new ArrayList<>();

    public GameView(WorldMap worldMap,  Difficulty difficulty) {
        if (worldMap == null  || difficulty == null) {
            throw new IllegalArgumentException("WorldMap or Difficulty is null! Check initialization.");
        }

        pointsLabel = new JLabel("Points: 0");
        this.worldMap = worldMap;
        this.difficulty = difficulty;
        setBackground(Color.gray);

        loadMapImage();
        startInfectionSpread();
    }


    // 2. TODO: Paints the map and all game elements
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int mapX = 0, mapY = 0, mapWidth = 0, mapHeight = 0;

        if (mapImage != null) {
            double imageAspectRatio = (double) mapImage.getWidth() / mapImage.getHeight();
            mapWidth = panelWidth;
            mapHeight = (int) (panelWidth / imageAspectRatio);

            if (mapHeight > panelHeight) {
                mapHeight = panelHeight;
                mapWidth = (int) (panelHeight * imageAspectRatio);
            }

            mapX = (panelWidth - mapWidth) / 2;
            mapY = (panelHeight - mapHeight) / 2;

            g2.drawImage(mapImage, mapX, mapY, mapWidth, mapHeight, null);
        }
        g2.setClip(mapX, mapY, mapWidth, mapHeight);


        // 3. TODO: Draw countries and connections
        double scaleX = (double) mapWidth / ORIGINAL_WIDTH;
        double scaleY = (double) mapHeight / ORIGINAL_HEIGHT;
        Stroke defaultStroke = g2.getStroke();

        for (Country country : worldMap.getCountries()) {
            int[] originalPos = getOriginalPosition(country);
            int scaledX = mapX + (int) (originalPos[0] * scaleX);
            int scaledY = mapY + (int) (originalPos[1] * scaleY);


            // 4. TODO: Draw connections to other countries
            for (Map.Entry<TransportType, String> connection : country.getConnections().entrySet()) {
                TransportType transport = connection.getKey();
                Country toCountry = worldMap.getCountryByName(connection.getValue());

                if (toCountry != null) {
                    int[] toOriginalPos = getOriginalPosition(toCountry);
                    int toScaledX = mapX + (int) (toOriginalPos[0] * scaleX);
                    int toScaledY = mapY + (int) (toOriginalPos[1] * scaleY);

                    if (transport == TransportType.PLANE && worldMap.areAirplaneRoutesLocked()) {
                        g2.setColor(Color.RED);
                        g2.setStroke(new BasicStroke(3));
                    } else if (country.isLocked() || toCountry.isLocked()) {
                        g2.setColor(Color.RED);
                    } else {
                        g2.setColor(Color.BLACK);
                    }

                    g2.setStroke(defaultStroke);
                    g2.drawLine(scaledX, scaledY, toScaledX, toScaledY);
                }
            }

            // 5. TODO: Highlight countries based on upgrades
            drawCountryHighlights(g2, country, scaledX, scaledY, Math.min(scaleX, scaleY));


            // 6. TODO: Draw country infection levels
            g2.setColor(getInfectionColor(country));
            int dotSize = (int) (10 * Math.min(scaleX, scaleY));
            g2.fillOval(scaledX - dotSize / 2, scaledY - dotSize / 2, dotSize, dotSize);

            g2.setColor(Color.BLACK);
            g2.drawString(country.getName(), scaledX + 10, scaledY - 10);
            g2.drawString(country.getInfected() + "/" + country.getPopulation(), scaledX + 10, scaledY + 10);
        }

        g2.setClip(null);


        // 7. TODO: Draw icons for upgrades
        int iconX = 10;
        int iconY = 10;
        for (Map.Entry<String, Image> entry : globalIcons.entrySet()) {
            g2.drawImage(entry.getValue(), iconX, iconY, 30, 30, null);
            iconX += 40;
        }
    }


    // 8. TODO: Highlights countries based on upgrade effects
    private void drawCountryHighlights(Graphics2D g2, Country country, int scaledX, int scaledY, double scaleFactor) {
        if (hotHighlightedCountries.contains(country)) {
            int colorIndex = Math.min(hotUpgradeLevel - 1, HOT_COLORS.length - 1);
            g2.setColor(HOT_COLORS[colorIndex]);
            g2.setStroke(new BasicStroke(2));
            int highlightSize = (int) (40 * scaleFactor);
            g2.drawOval(scaledX - highlightSize / 2, scaledY - highlightSize / 2, highlightSize, highlightSize);
        }

        if (coldHighlightedCountries.contains(country)) {
            int colorIndex = Math.min(coldUpgradeLevel - 1, COLD_COLORS.length - 1);
            g2.setColor(COLD_COLORS[colorIndex]);
            g2.setStroke(new BasicStroke(2));
            int highlightSize = (int) (40 * scaleFactor);
            g2.drawOval(scaledX - highlightSize / 2, scaledY - highlightSize / 2, highlightSize, highlightSize);
        }

        if (quarantineHighlightedCountries.contains(country)) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3));
            int squareSize = (int) (30 * scaleFactor);
            g2.drawRect(scaledX - squareSize / 2, scaledY - squareSize / 2, squareSize, squareSize);
        }

        if (curedHighlightedCountries.contains(country)) {
            g2.setColor(new Color(255, 0, 255));
            int curedDotSize = (int) (20 * scaleFactor);
            g2.fillOval(scaledX - curedDotSize / 2, scaledY - curedDotSize / 2, curedDotSize, curedDotSize);
        }

        if (vaccinatedHighlightedCountries.contains(country)) {
            g2.setColor(new Color(70, 60, 110));
            int hexRadius = (int) (35 * scaleFactor);
            int[][] hexPoints = calculateHexagonPoints(scaledX, scaledY, hexRadius);
            g2.setStroke(new BasicStroke(3));
            g2.drawPolygon(hexPoints[0], hexPoints[1], 6);
        }

        if (hygieneHighlightedCountries.contains(country)) {
            g2.setColor(Color.PINK);
            int radius = (int) (15 * scaleFactor);
            int circleSize = (int) (15 * scaleFactor);
            int[][] offsets = {
                    {-radius, -radius}, {radius, -radius}, {-radius, radius}, {radius, radius}, {0, -radius}, {0, radius}
            };
            for (int[] offset : offsets) {
                int x = scaledX + offset[0];
                int y = scaledY + offset[1];
                g2.fillOval(x - circleSize / 2, y - circleSize / 2, circleSize, circleSize);
            }
        }
    }

    public void addVaccinatedHighlight(Country country) {
        if (!vaccinatedHighlightedCountries.contains(country)) {
            vaccinatedHighlightedCountries.add(country);
            repaint();
        }
    }

    public void addCureHighlight(Country country) {
        if (!curedHighlightedCountries.contains(country)) {
            curedHighlightedCountries.add(country);
            repaint();
        }
    }

    public void updatePointsLabel(int points) {
        pointsLabel.setText("Points: " + points);
        repaint();
    }

    // TODO: 9. Transport animation logic here
    private void animateTransport(String transportType, Country from, Country to) {
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        double imageAspectRatio = (double) mapImage.getWidth() / mapImage.getHeight();
        int mapWidth = panelWidth;
        int mapHeight = (int) (panelWidth / imageAspectRatio);

        if (mapHeight > panelHeight) {
            mapHeight = panelHeight;
            mapWidth = (int) (panelHeight * imageAspectRatio);
        }

        int mapX = (panelWidth - mapWidth) / 2;
        int mapY = (panelHeight - mapHeight) / 2;

        double scaleX = (double) mapWidth / ORIGINAL_WIDTH;
        double scaleY = (double) mapHeight / ORIGINAL_HEIGHT;

        int[] fromOriginalPosition = getOriginalPosition(from);
        int fromX = mapX + (int) (fromOriginalPosition[0] * scaleX);
        int fromY = mapY + (int) (fromOriginalPosition[1] * scaleY);

        int[] toOriginalPosition = getOriginalPosition(to);
        int toX = mapX + (int) (toOriginalPosition[0] * scaleX);
        int toY = mapY + (int) (toOriginalPosition[1] * scaleY);

        // Use a relative path for the transport image
        ImageIcon icon = null;
        try {
            String imagePath = "/image/" + transportType.toLowerCase() + ".png";
            icon = new ImageIcon(getClass().getResource(imagePath));
        } catch (NullPointerException e) {
            System.err.println("Transport image not found for type: " + transportType);
            return;
        }

        Image scaledImage = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JLabel transportIcon = new JLabel(new ImageIcon(scaledImage));

        transportIcon.setBounds(fromX, fromY, 20, 20);
        add(transportIcon);

        new Thread(() -> {
            for (int i = 0; i <= 100; i++) {
                int updatedFromX = mapX + (int) (fromOriginalPosition[0] * scaleX);
                int updatedFromY = mapY + (int) (fromOriginalPosition[1] * scaleY);
                int updatedToX = mapX + (int) (toOriginalPosition[0] * scaleX);
                int updatedToY = mapY + (int) (toOriginalPosition[1] * scaleY);

                int x = updatedFromX + (updatedToX - updatedFromX) * i / 100;
                int y = updatedFromY + (updatedToY - updatedFromY) * i / 100;

                SwingUtilities.invokeLater(() -> transportIcon.setLocation(x, y));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            SwingUtilities.invokeLater(() -> remove(transportIcon));
            repaint();
        }).start();
    }




    // 10. TODO: Starts the infection spread timer
    public void startInfectionSpread() {
        stopInfectionSpread();

        if (infectionTimer != null && infectionTimer.isRunning()) {
            System.out.println("Infection spread timer is already running.");
            return;
        }

        infectionTimer = new Timer(2000, e -> {
            List<Country> infectedCountries = worldMap.getCountries().stream()
                    .filter(country -> country.getInfected() > 0 && !country.isLocked())
                    .collect(Collectors.toList());

            for (Country fromCountry : infectedCountries) {
                List<Country> reachableCountries = new ArrayList<>();
                for (Map.Entry<TransportType, String> connection : fromCountry.getConnections().entrySet()) {
                    Country toCountry = worldMap.getCountryByName(connection.getValue());
                    TransportType transport = connection.getKey();

                    if (toCountry != null && !toCountry.isLocked() && !toCountry.equals(fromCountry.getLastInfectedBy())) {
                        if (transport == TransportType.PLANE && worldMap.areAirplaneRoutesLocked()) {
                            System.out.println("Airplane connection between " + fromCountry.getName() +
                                    " and " + toCountry.getName() + " is locked. Skipping...");
                            continue;
                        }
                        reachableCountries.add(toCountry);
                    }
                }

                Collections.shuffle(reachableCountries);

                for (Country toCountry : reachableCountries) {
                    TransportType transport = fromCountry.getConnections().entrySet().stream()
                            .filter(entry -> entry.getValue().equals(toCountry.getName()))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElse(null);

                    if (transport != null) {
                        int infectionChance = calculateInfectionChance(transport)
                                + toCountry.getInfectionProbabilityModifier();

                        if (toCountry.isVaccinated()) {
                            infectionChance = (int) (infectionChance * 0.5);
                        }

                        System.out.println("Attempting to spread from " + fromCountry.getName() +
                                " to " + toCountry.getName() + " via " + transport +
                                " with a probability of " + infectionChance + "%.");

                        int infectionSpread = calculateSpreadAmount(fromCountry, toCountry);
                        if (Math.random() * 100 < infectionChance && infectionSpread > 0) {
                            toCountry.infect(infectionSpread);
                            toCountry.setLastInfectedBy(fromCountry);
                            animateTransport(transport.name(), fromCountry, toCountry);
                            System.out.println("Attempting to spread from " + fromCountry.getName() + " to " + toCountry.getName() +
                                    " via " + transport +
                                    " (Vaccinated: " + toCountry.isVaccinated() +
                                    ", Locked: " + toCountry.isLocked() +
                                    ", Modifier: " + toCountry.getInfectionProbabilityModifier() +
                                    ") with a probability of " + infectionChance + "%.");
                        } else {
                            System.out.println("FAILED: Infection did not spread from " + fromCountry.getName() +
                                    " to " + toCountry.getName() + ".");
                        }
                        break;
                    }
                }
            }
            repaint();
        });

        infectionTimer.start();
        System.out.println("Starting infection spread timer...");

    }

    public double calculateOverallInfectionRate() {
        int totalPopulation = 0;
        int totalInfected = 0;

        for (Country country : worldMap.getCountries()) {
            totalPopulation += country.getPopulation();
            totalInfected += country.getInfected();
        }

        return totalPopulation > 0 ? (100.0 * totalInfected / totalPopulation) : 0.0;
    }

    private int[][] calculateHexagonPoints(int centerX, int centerY, int radius) {
        int[] xPoints = new int[6];
        int[] yPoints = new int[6];

        for (int i = 0; i < 6; i++) {
            xPoints[i] = centerX + (int) (radius * Math.cos(i * Math.PI / 3));
            yPoints[i] = centerY + (int) (radius * Math.sin(i * Math.PI / 3));
        }

        return new int[][]{xPoints, yPoints};
    }

    public void addGlobalIcon(String name, String path) {
        try {
            URL resource = getClass().getClassLoader().getResource(path);
            if (resource != null) {
                Image icon = new ImageIcon(resource).getImage();
                globalIcons.put(name, icon);
            } else {
                System.err.println("Resource not found for " + name + ": " + path);
                Image placeholderIcon = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = ((BufferedImage) placeholderIcon).createGraphics();
                g2.setColor(Color.RED);
                g2.fillRect(0, 0, 30, 30);
                g2.dispose();
                globalIcons.put(name, placeholderIcon);
            }
        } catch (Exception e) {
            System.err.println("Error loading icon for " + name + ": " + e.getMessage());
        }
    }

    public void addQuarantineHighlight(Country country) {
        quarantineHighlightedCountries.add(country);
        repaint();
    }
    public void addHygieneHighlight(Country country) {
        hygieneHighlightedCountries.add(country);
        repaint();
    }

    private int[] getOriginalPosition(Country country) {
        int[][] originalPositions = {
                {450, 200}, // Norway
                {300, 340}, // England
                {430, 400}, // Germany
                {550, 400}, // Poland
                {500, 600}, // Italy
                {650, 680}, // Greece
                {200, 660}, // Spain
                {690, 520}, // Romania
                {930, 650}, // Turkey
                {820, 440}, // Ukraine
                {800, 200}  // Russia
        };

        List<Country> countries = worldMap.getCountries();
        int index = countries.indexOf(country);
        return originalPositions[index];
    }


    // 11. TODO: Stops the infection spread timer
    public void stopInfectionSpread() {
        if (infectionTimer != null) {
            System.out.println("Stopping infection spread timer...");
            infectionTimer.stop();
            infectionTimer = null;
        }
    }


    // 12.TODO: Makes more or less infection related to the hardness level
    private int calculateSpreadAmount(Country fromCountry, Country toCountry) {
        int infectionSpread;
        if (difficulty == Difficulty.HARD) {
            infectionSpread = Math.max(1, fromCountry.getInfected());
        } else if (difficulty == Difficulty.MEDIUM) {
            infectionSpread = Math.max(1, fromCountry.getInfected() / 10);
        } else {
            infectionSpread = Math.max(1, fromCountry.getInfected() / 15);
        }
        infectionSpread = Math.min(infectionSpread, toCountry.getPopulation() - toCountry.getInfected());

        return infectionSpread;
    }


    // 13. TODO: Different probability of spread related to transportation type and hardness level
    private int calculateInfectionChance(TransportType transportType) {
        int baseChance;
        switch (transportType) {
            case PLANE: baseChance = 55; break;
            case TRAIN: baseChance = 85; break;
            case BOAT: baseChance = 65; break;
            default: baseChance = 40; break;
        }

        if (difficulty == Difficulty.EASY) {
            baseChance *= 0.8;
        } else if (difficulty == Difficulty.HARD) {
            baseChance *= 1.2;
        }

        return Math.min(100, baseChance);
    }


    private static final Color[] HOT_COLORS = {
            new Color(255, 255, 128),
            new Color(255, 204, 102),
            new Color(255, 153, 51),
            new Color(255, 102, 0),
            new Color(204, 51, 0)
    };

    private static final Color[] COLD_COLORS = {
            new Color(173, 216, 230),
            new Color(135, 206, 250),
            new Color(50, 160, 255),
            new Color(30, 90, 255),
            new Color(0, 0, 255)
    };


    public void highlightHotCountries(List<Country> countries) {
        if (hotUpgradeLevel < MAX_UPGRADE_LEVEL) {
            hotUpgradeLevel++;
        }
        hotHighlightedCountries.clear();
        hotHighlightedCountries.addAll(countries);
        repaint();
    }

    public void highlightColdCountries(List<Country> countries) {
        if (coldUpgradeLevel < MAX_UPGRADE_LEVEL) {
            coldUpgradeLevel++;
        }
        coldHighlightedCountries.clear();
        coldHighlightedCountries.addAll(countries);
        repaint();
    }

    private Color getInfectionColor(Country country) {
        int infectionPercentage = (country.getInfected() * 100) / country.getPopulation();
        if (infectionPercentage > 75) return Color.RED;
        if (infectionPercentage > 50) return Color.ORANGE;
        if (infectionPercentage > 15) return Color.YELLOW;
        return Color.GREEN;
    }


    private void loadMapImage() {
        try {
            mapImage = ImageIO.read(getClass().getResource("/image/map3.jpg"));
        } catch (IOException e) {
            System.err.println("Failed to load map image: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Image file not found in the specified path.");
        }
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ORIGINAL_WIDTH, ORIGINAL_HEIGHT);
    }
}












