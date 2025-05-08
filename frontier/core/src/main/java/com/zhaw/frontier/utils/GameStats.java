package com.zhaw.frontier.utils;

import lombok.Getter;

public class GameStats {

    @Getter
    private int enemiesKilled = 0;

    public void addKill() {
        enemiesKilled++;
    }

    public void reset() {
        enemiesKilled = 0;
    }
}
