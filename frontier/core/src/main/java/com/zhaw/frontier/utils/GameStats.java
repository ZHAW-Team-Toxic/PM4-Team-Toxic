package com.zhaw.frontier.utils;

import lombok.Getter;

public class GameStats {
    private static GameStats instance;

    private GameStats() {}

    public static GameStats getInstance() {
        if (instance == null) {
            instance = new GameStats();
        }
        return instance;
    }

    private int enemiesKilled = 0;

    public void addKill() {
        enemiesKilled++;
    }

    public void reset() {
        enemiesKilled = 0;
    }

    public int getEnemiesKilled() {
        return enemiesKilled;
    }
}
