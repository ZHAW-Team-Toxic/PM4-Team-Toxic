package com.zhaw.frontier.enums;

/**
 * Enum representing the different game phases.
 */
public enum GamePhase {
    BUILD_AND_PLAN("Build and Plan"),
    COLLECTION("Collection"),
    BUILD_PROGRESS("Build Progress"),
    ENEMY_TURN("Battle Phase");

    private final String displayText;

    GamePhase(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }
}
