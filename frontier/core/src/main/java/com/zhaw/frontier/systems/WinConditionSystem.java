package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.screens.WinScreen;

public class WinConditionSystem extends EntitySystem {

    private final FrontierGame frontierGame;
    private boolean triggered = false;

    public WinConditionSystem(FrontierGame frontierGame) {
        this.frontierGame = frontierGame;
    }

    @Override
    public void update(float deltaTime) {
        if (triggered) return;

        if (TurnSystem.getInstance().getTurnCounter() >= 10) {
            triggered = true;
            frontierGame.setScreen(new WinScreen(frontierGame));
        }
    }
}
