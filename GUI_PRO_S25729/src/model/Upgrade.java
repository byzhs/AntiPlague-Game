package model;

import controller.GameController;
import view.GameView;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;


// TODO: In this class:

// 1. TODO: Hygiene Promotion Upgrade
// 2. TODO: Travel Restriction Upgrade
// 3. TODO: Close Airlines Upgrade
// 4. TODO: Makes it harder to infect Hot country Upgrade
// 5. TODO: Makes it harder to infect Cold country Upgrade
// 6. TODO: Scientific Research Upgrade
// 7. TODO: ANtigen SUppressors Upgrade
// 8. TODO: Quarantine Zone Upgrade
// 9. TODO: Vaccination Campaign Upgrade
// 10. TODO: Cure Deployment Upgrade


public class Upgrade {
    private String name;
    private String description;
    private int cost;
    private Consumer<GameController> effect;
    private Upgrade prerequisite;


    private int hotUpgradeUses = 0;
    private static final int MAX_HOT_UPGRADE_USES = 5;

    private static final int MAX_COLD_UPGRADE_USES = 5;
    public Upgrade(String name, String description, int cost, Consumer<GameController> effect, Upgrade prerequisite, boolean reusable) {
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.effect = effect;
        this.prerequisite = prerequisite;
    }
    public Upgrade(String name, String description, int cost, Consumer<GameController> effect, boolean reusable) {
        this(name, description, cost, effect, null, reusable);
    }

    public Upgrade(String name, String description, int cost, Consumer<GameController> effect) {
        this(name, description, cost, effect, null, true);
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public void apply(GameController controller) {
        effect.accept(controller);
    }



    // 1. TODO: Hygiene Promotion Upgrade
    public static Upgrade hygienePromotion() {
        return new Upgrade(
                "Hygiene Promotion",
                "Reduces infection spread probability in a specific country by 5%.",
                5,
                controller -> {
                    System.out.println("Hygiene Promotion activated!");

                    String countryName = JOptionPane.showInputDialog(
                            controller.getGameFrame(),
                            "Enter the name of the country to promote hygiene:",
                            "Hygiene Promotion",
                            JOptionPane.QUESTION_MESSAGE
                    );

                    if (countryName == null || countryName.trim().isEmpty()) {
                        System.out.println("Hygiene Promotion canceled: No country entered.");
                        controller.addPoints(5);
                        return;
                    }

                    Country country = controller.getWorldMap().getCountryByName(countryName.trim());
                    if (country != null) {
                        country.adjustInfectionProbabilityModifier(-5);

                        controller.getGameView().addHygieneHighlight(country);

                        System.out.println("Hygiene Promotion applied to " + countryName + ". Infection spread probability reduced by 5%.");
                    } else {
                        JOptionPane.showMessageDialog(
                                controller.getGameFrame(),
                                "Country not found: " + countryName,
                                "Error",
                                JOptionPane.ERROR_MESSAGE,

                                controller.addPoints(5)
                        );
                        System.out.println("Hygiene Promotion failed: Country not found.");

                    }
                }
        );
    }

    // 2. TODO: Travel Restriction Upgrade
    public static Upgrade travelRestriction() {
        return new Upgrade(
                "Travel Restriction",
                "Close borders of a specific country for 15 seconds.",
                10,
                controller -> {
                    System.out.println("Travel Restriction activated!");

                    String countryName = JOptionPane.showInputDialog("Enter the name of the country to lock:");
                    if (countryName == null || countryName.trim().isEmpty()) {
                        System.out.println("Travel Restriction canceled: No country entered.");
                        controller.addPoints(10);
                        return;
                    }

                    Country country = controller.getWorldMap().getCountryByName(countryName.trim());
                    if (country != null) {
                        country.setLocked(true);
                        controller.getGameView().repaint();
                        System.out.println("Borders of " + countryName + " are now closed for 15 seconds.");

                        Timer unlockTimer = new Timer(15000, e -> {
                            country.setLocked(false);
                            controller.getGameView().repaint();
                        });
                        unlockTimer.setRepeats(false);
                        unlockTimer.start();
                    } else {
                        JOptionPane.showMessageDialog(
                                controller.getGameFrame(),
                                "Country not found: " + countryName,
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                        System.out.println("Travel Restriction failed: Country not found.");
                        controller.addPoints(10);
                    }
                }
        );
    }


    // 3. TODO: Close Airlines Upgrade
    public static Upgrade closeAirlines() {
        return new Upgrade(
                "Close Airlines",
                "Temporarily disables all airplane connections for 15 seconds.",
                15,
                controller -> {
                    System.out.println("Close Airlines activated! All airplane routes are now locked.");

                    controller.getWorldMap().setAirplaneRoutesLocked(true);
                    controller.getGameView().repaint();
                    JOptionPane.showMessageDialog(null, "All airplane routes are now locked for 15 seconds!");

                    Timer unlockTimer = new Timer(15000, e -> {
                        controller.getWorldMap().setAirplaneRoutesLocked(false);
                        controller.getGameView().repaint(); // Update UI
                        System.out.println("Airplane routes are now unlocked.");
                    });
                    unlockTimer.setRepeats(false);
                    unlockTimer.start();
                }
        );
    }

    // 4. TODO: Makes it harder to infect Hot country Upgrade
    public static Upgrade harderToSpreadInHotCountries() {
        return new Upgrade(
                "Harder to Spread in Hot Countries",
                "Slows infection rates in hot climates.",
                20,
                controller -> {
                    GameView gameView = controller.getGameView();
                    WorldMap worldMap = controller.getWorldMap();

                    if (gameView.hotUpgradeUses >= MAX_HOT_UPGRADE_USES) {
                        JOptionPane.showMessageDialog(
                                controller.getGameFrame(),
                                "Hot Countries upgrade can only be used 5 times.",
                                "Upgrade Limit Reached",
                                JOptionPane.WARNING_MESSAGE,
                                controller.addPoints(20)
                        );

                        return;
                    }

                    gameView.hotUpgradeUses++;

                    List<Country> hotCountries = Arrays.asList(
                            worldMap.getCountryByName("Spain"),
                            worldMap.getCountryByName("Italy"),
                            worldMap.getCountryByName("Greece"),
                            worldMap.getCountryByName("Turkey")
                    );

                    for (Country country : hotCountries) {
                        if (country != null) {
                            country.adjustInfectionProbabilityModifier(-5);
                            System.out.println(country.getName() + " infection rate reduced by 5%.");
                        }
                    }

                    gameView.highlightHotCountries(hotCountries);

                    JOptionPane.showMessageDialog(
                            controller.getGameFrame(),
                            "Hot Countries infection spread reduced by 5%.",
                            "Upgrade Activated",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    gameView.repaint();
                }
        );
    }

    // 5. TODO: Makes it harder to infect Cold country Upgrade
    public static Upgrade harderToSpreadInColdCountries() {
        return new Upgrade(
                "Harder to Spread in Cold Countries",
                "Slows infection rates in cold climates.",
                20,
                controller -> {
                    GameView gameView = controller.getGameView();
                    WorldMap worldMap = controller.getWorldMap();

                    if (gameView.coldUpgradeUses >= MAX_COLD_UPGRADE_USES) {
                        JOptionPane.showMessageDialog(
                                controller.getGameFrame(),
                                "Cold Countries upgrade can only be used 5 times.",
                                "Upgrade Limit Reached",
                                JOptionPane.WARNING_MESSAGE,

                                controller.addPoints(20)
                        );
                        return;

                    }

                    gameView.coldUpgradeUses++;

                    List<Country> coldCountries = Arrays.asList(
                            worldMap.getCountryByName("Russia"),
                            worldMap.getCountryByName("Ukraine"),
                            worldMap.getCountryByName("Norway"),
                            worldMap.getCountryByName("Poland")
                    );

                    for (Country country : coldCountries) {
                        if (country != null) {
                            country.adjustInfectionProbabilityModifier(-5);
                            System.out.println(country.getName() + " infection rate reduced by 5%.");
                        }
                    }

                    gameView.highlightColdCountries(coldCountries);

                    JOptionPane.showMessageDialog(
                            controller.getGameFrame(),
                            "Cold Countries infection spread reduced by 5%.",
                            "Upgrade Activated",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    gameView.repaint();
                }
        );
    }


    // 6. TODO: Scientific Research Upgrade
    public static Upgrade scientificResearch() {
        return new Upgrade(
                "Scientific Research",
                "Invest in studies to better understand and counteract the virus.",
                25,
                controller -> {
                    System.out.println("Scientific Research activated!");

                    if (controller.isUpgradeUsed("Scientific Research")) {
                        JOptionPane.showMessageDialog(
                                controller.getGameFrame(),
                                "Scientific Research has already been used!",
                                "Upgrade Already Used",
                                JOptionPane.WARNING_MESSAGE,

                                controller.addPoints(25)
                        );

                        return;
                    }

                    for (Country country : controller.getWorldMap().getCountries()) {
                        country.adjustInfectionProbabilityModifier(-10);
                    }
                    controller.unlockUpgrade("Antigen Suppressors");
                    controller.unlockUpgrade("Vaccination Campaign");
                    controller.getGameView().addGlobalIcon("Scientific Research", "image/science.jpg");
                    controller.markUpgradeAsUsed("Scientific Research");

                    JOptionPane.showMessageDialog(
                            controller.getGameFrame(),
                            "Scientific Research completed! Global infection probability reduced by 10%. Advanced upgrades unlocked.",
                            "Upgrade Activated",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
        );
    }

    // 7. TODO: ANtigen SUppressors Upgrade
    public static Upgrade antigenSuppressors() {
        return new Upgrade(
                "Antigen Suppressors",
                "Slow virus mutations, reducing infection probability globally by 15%.",
                30,
                controller -> {
                    if (controller.isUpgradeUsed("Antigen Suppressors")) {
                        JOptionPane.showMessageDialog(
                                controller.getGameFrame(),
                                "Antigen Suppressors has already been used!",
                                "Upgrade Already Used",
                                JOptionPane.WARNING_MESSAGE,

                                controller.addPoints(30)
                        );
                        return;
                    }

                    for (Country country : controller.getWorldMap().getCountries()) {
                        country.adjustInfectionProbabilityModifier(-10);
                    }
                    controller.getGameView().addGlobalIcon("Antigen Suppressors", "image/antigen.jpg");
                    controller.markUpgradeAsUsed("Antigen Suppressors");
                    JOptionPane.showMessageDialog(
                            controller.getGameFrame(),
                            "Antigen Suppressors activated! Global infection probability reduced by 10%.",
                            "Upgrade Activated",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    controller.getGameView().repaint();
                },
                scientificResearch(),
                false
        );
    }


    // 8. TODO: Quarantine Zone Upgrade
    public static Upgrade quarantineZones() {
        return new Upgrade(
                "Quarantine Zones",
                "Isolate infected areas: reduce infection spread by 15% and recover 15% of infected population.",
                35,
                controller -> {
                    System.out.println("Quarantine Zones activated!");

                    String countryName = JOptionPane.showInputDialog(
                            controller.getGameFrame(),
                            "Enter the name of the country to quarantine:",
                            "Quarantine Zones",
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (countryName == null || countryName.trim().isEmpty()) {
                        System.out.println("Quarantine Zones canceled: No country entered.");
                        JOptionPane.showMessageDialog(
                                controller.getGameFrame(),
                                "No country entered. Points refunded.",
                                "Action Canceled",
                                JOptionPane.INFORMATION_MESSAGE,
                                controller.addPoints(35)
                        );
                        return;
                    }

                    Country country = controller.getWorldMap().getCountryByName(countryName.trim());
                    if (country != null) {
                        int initialInfected = country.getInfected();
                        int recoveredAmount = (int) (initialInfected * 0.15);
                        country.recover(recoveredAmount);

                        System.out.println("Recovered 15% of infected population in " + country.getName() +
                                ". Recovered: " + recoveredAmount +
                                ", Remaining infections: " + country.getInfected());

                        country.adjustInfectionProbabilityModifier(-15);
                        System.out.println("Infection spread modifier for " + country.getName() + " reduced by 15%.");

                        controller.getGameView().addQuarantineHighlight(country);

                        JOptionPane.showMessageDialog(controller.getGameFrame(),
                                "Quarantine Zones applied to " + countryName + ":\n" +
                                        "- Recovered 15% of infected population.\n" +
                                        "- Spread probability reduced by 15%.",
                                "Upgrade Activated",
                                JOptionPane.INFORMATION_MESSAGE);

                        controller.getGameView().repaint();
                    } else {
                        JOptionPane.showMessageDialog(controller.getGameFrame(),
                                "Country not found: " + countryName,
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        System.out.println("Quarantine Zones failed: Country not found.");
                        controller.addPoints(35);
                    }
                },
                true
        );
    }

    // 9. TODO: Vaccination Campaign Upgrade
    public static Upgrade vaccinationCampaign() {
        return new Upgrade(
                "Vaccination Campaign",
                "Immunize a specific country to reduce infection rates by 50%.",
                40,
                controller -> {

                    System.out.println("Vaccination Campaign activated!");

                    if (!controller.isUpgradeUsed("Antigen Suppressors")) {
                        JOptionPane.showMessageDialog(
                                controller.getGameFrame(),
                                "Vaccination Campaign requires Antigen Suppressors to be completed first!",
                                "Requirement Not Met",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }

                    String countryName = JOptionPane.showInputDialog(
                            controller.getGameFrame(),
                            "Enter the name of the country to vaccinate:",
                            "Vaccination Campaign",
                            JOptionPane.QUESTION_MESSAGE
                    );

                    if (countryName == null || countryName.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(
                                controller.getGameFrame(),
                                "No country name provided. Vaccination Campaign cancelled.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                        controller.addPoints(40);
                        return;
                    }

                    Country country = controller.getWorldMap().getCountryByName(countryName.trim());
                    if (country == null) {
                        JOptionPane.showMessageDialog(
                                controller.getGameFrame(),
                                "Country not found: " + countryName,
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                        System.out.println("Vaccination Campaign failed: Country not found.");
                        controller.addPoints(40);
                        return;
                    }

                    if (country.isVaccinated()) {
                        JOptionPane.showMessageDialog(
                                controller.getGameFrame(),
                                countryName + " is already vaccinated!",
                                "Vaccination Failed",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }

                    country.vaccinate();
                    country.adjustInfectionProbabilityModifier(-50);


                    controller.getGameView().addVaccinatedHighlight(country);

                    System.out.println("Vaccination Campaign applied to " + country.getName() +
                            ": Infection rates reduced by 50%.");

                    JOptionPane.showMessageDialog(
                            controller.getGameFrame(),
                            "Vaccination Campaign applied to " + countryName + ":\n" +
                                    "- Infection rates reduced by 50%.",
                            "Upgrade Activated",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    controller.getGameView().repaint();
                },
                antigenSuppressors(),
                true
        );
    }

    // 10. TODO: Cure Deployment Upgrade
    public static Upgrade cureDeployment() {
        return new Upgrade(
                "Cure Deployment",
                "Reduce infections in a specific country by 60% instantly and start gradual recovery over time.",
                50,
                controller -> {
                    System.out.println("Cure Deployment activated!");

                    String countryName = JOptionPane.showInputDialog(
                            controller.getGameFrame(),
                            "Enter the name of the country to deploy the cure:",
                            "Cure Deployment",
                            JOptionPane.QUESTION_MESSAGE
                    );

                    if (countryName == null || countryName.trim().isEmpty()) {
                        System.out.println("Cure Deployment canceled: No country entered.");
                        JOptionPane.showMessageDialog(
                                controller.getGameFrame(),
                                "Operation canceled. No points deducted.",
                                "Operation Canceled",
                                JOptionPane.INFORMATION_MESSAGE,
                                controller.addPoints(50)
                        );
                        return;
                    }

                    Country country = controller.getWorldMap().getCountryByName(countryName.trim());
                    if (country == null) {
                        JOptionPane.showMessageDialog(
                                controller.getGameFrame(),
                                "Country not found: " + countryName,
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                        System.out.println("Cure Deployment failed: Country not found.");
                        controller.addPoints(50);
                        return;
                    }

                    int initialInfected = country.getInfected();
                    int recoveredInstantly = (int) (initialInfected * 0.6);
                    country.recover(recoveredInstantly);

                    System.out.println("Cure deployed to " + country.getName() + ". Instantly recovered: " + recoveredInstantly +
                            ". Remaining infected: " + country.getInfected());

                    controller.getGameView().addCureHighlight(country);

                    Timer recoveryTimer = new Timer(1000, null);
                    final int[] recoveryRate = {2};
                    final int[] recoveryCount = {0};

                    recoveryTimer.addActionListener(e -> {
                        if (country.getInfected() > 0) {
                            country.recover(recoveryRate[0]);
                            recoveryCount[0]++;

                            if (recoveryCount[0] % 10 == 0) {
                                recoveryRate[0] += 3;
                            }

                            System.out.println("Recovered " + recoveryRate[0] + " people in " + country.getName() +
                                    ". Remaining infected: " + country.getInfected());
                        }
                        controller.getGameView().repaint();
                    });

                    recoveryTimer.start();

                    JOptionPane.showMessageDialog(
                            controller.getGameFrame(),
                            "Cure deployed to " + countryName + ":\n" +
                                    "- Instantly recovered 60% of infections.\n" +
                                    "- Gradual recovery has started.",
                            "Cure Deployment Successful",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    controller.getGameView().repaint();
                },
                true
        );
    }

}