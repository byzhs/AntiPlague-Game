package model;

import java.io.Serializable;

// TODO: The HighScore class represents a player's high score in the game, including their name, game duration,
//       number of upgrades used, win status, and difficulty level. At the end, it includes a method to calculate
//       the score based on these factors.
public class HighScore implements Serializable {
    private String name;
    private int time;
    private int upgradesUsed;
    private boolean isWin;
    private String difficulty;

    public HighScore(String name, int time, int upgradesUsed, boolean isWin, String difficulty) {
        this.name = name;
        this.time = time;
        this.upgradesUsed = upgradesUsed;
        this.isWin = isWin;
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public int getUpgradesUsed() {
        return upgradesUsed;
    }

    public boolean isWin() {
        return isWin;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int calculateScore() {
        int score = 10000 - (time * 10);
        score += upgradesUsed * 100;
        if (isWin) {
            score += 5000;
        }
        return Math.max(score, 0);
    }


}
