package model;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

// TODO:The Country class represents individual countries in the game, managing their population, infection,
//       recovery, vaccination, and connectivity status. It includes functionality to infect, recover, lock,
//       vaccinate, and adjust infection probability. Additionally, it tracks connections between countries
//       using different transport types.

public class Country {
    private String name;
    private int population;
    private int infected;
    private int recovered;
    private boolean locked;
    private boolean vaccinated;
    private int infectionProbabilityModifier = 0;
    private Color lineColor = Color.BLACK;
    private Country lastInfectedBy;
    private Timer recoveryTimer;

    private Map<TransportType, String> connections = new HashMap<>();


    public Country(String name, int population) {
        this.name = name;
        this.population = population;
        this.infected = 0;
        this.recovered = 0;
        this.locked = false;
        this.vaccinated = false;
        this.connections = new HashMap<>();
    }

    public int getInfected() {
        return infected;
    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }


    public void stopRecovery() {
        if (recoveryTimer != null) {
            recoveryTimer.stop();
            recoveryTimer = null;
            System.out.println("Recovery timer stopped for " + name);
        }
    }



    public boolean isLocked() {
        return locked;
    }

    public void resetInfectionStatus() {
        this.infected = 0;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        this.lineColor = locked ? Color.RED : Color.BLACK;
        System.out.println(name + " is now " + (locked ? "locked" : "unlocked") + ".");
    }

    public boolean isVaccinated() {
        return vaccinated;
    }

    public void vaccinate() {
        this.vaccinated = true;
        System.out.println(name + " has been vaccinated.");
    }

    public Country getLastInfectedBy() {
        return lastInfectedBy;
    }

    public void setLastInfectedBy(Country lastInfectedBy) {
        this.lastInfectedBy = lastInfectedBy;
    }

    public int getInfectionProbabilityModifier() {
        return infectionProbabilityModifier;
    }

    public void adjustInfectionProbabilityModifier(int modifier) {
        this.infectionProbabilityModifier += modifier;
        System.out.println("Infection probability modifier for " + name + " adjusted by " + modifier + ".");
    }

    public void recover(int amount) {
        if (amount > 0) {
            int recoverable = Math.min(amount, infected);
            infected -= recoverable;
            recovered += recoverable;
            System.out.println(recoverable + " people recovered in " + name + ". Remaining infected: " + infected + ", Total recovered: " + recovered);
        }
    }

    public void infect(int amount) {
        if (infected + amount > population) {
            infected = population;
        } else {
            infected += amount;
        }
        System.out.println(amount + " people infected in " + name + ". Total infected: " + infected);
    }


    public Map<TransportType, String> getConnections() {
        return connections;
    }

    public void addConnection(TransportType transportType, String destinationCountry) {
        connections.put(transportType, destinationCountry);
    }

}
