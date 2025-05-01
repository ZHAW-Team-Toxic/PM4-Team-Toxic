package com.zhaw.frontier.utils;

public class GameStats {

    private int enemiesKilled = 0;

    public void addKill() {
        enemiesKilled++;
    }

    public int getEnemiesKilled() {
        return enemiesKilled;
    }

    public void reset() {
        enemiesKilled = 0;
    }
}
