package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.screens.WinScreen;

public class WinConditionSystem extends IteratingSystem {

    private final FrontierGame frontierGame;

    private boolean triggered = false;

    public WinConditionSystem(FrontierGame frontierGame) {
        super(Family.all().get());
        this.frontierGame = frontierGame;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (triggered) return;

        if ((TurnSystem.getInstance().getTurnCounter() / 5) >= 10) {
            triggered = true;
            frontierGame.setScreen(new WinScreen(frontierGame));
        }
    }
}
