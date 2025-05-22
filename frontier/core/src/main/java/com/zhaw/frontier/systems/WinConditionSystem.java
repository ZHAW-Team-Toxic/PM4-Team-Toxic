package com.zhaw.frontier.systems;

import com.badlogic.ashley.systems.IntervalSystem;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.screens.WinScreen;

/**
 * Checks if the win condition is fulfilled.
 */
public class WinConditionSystem extends IntervalSystem {

    private final FrontierGame game;
    private boolean triggered = false;

    public WinConditionSystem(FrontierGame game) {
        super(0.5f);
        this.game = game;
    }

    @Override
    protected void updateInterval() {
        if (triggered) return;

        if (TurnSystem.getInstance().getTurnCounter() >= 50) {
            triggered = true;
            game.switchScreen(new WinScreen(game));
        }
    }
}
