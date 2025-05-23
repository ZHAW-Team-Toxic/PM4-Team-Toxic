package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.zhaw.frontier.components.EnemyComponent;
import com.zhaw.frontier.enums.GamePhase;

public class EnemyTurnMonitorSystem extends EntitySystem {

    private final TurnSystem turnSystem;
    private final Family enemyFamily;
    private Engine engine;

    public EnemyTurnMonitorSystem() {
        this.turnSystem = TurnSystem.getInstance();
        this.enemyFamily = Family.all(EnemyComponent.class).get();
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        this.engine = engine;
    }

    @Override
    public void update(float deltaTime) {
        if (turnSystem.getGamePhase() != GamePhase.ENEMY_TURN) {
            return; // Only run during ENEMY_TURN
        }

        ImmutableArray<Entity> entities = engine.getEntitiesFor(enemyFamily);
        if (entities.size() == 0) {
            // No enemies left, go back to BUILD_AND_PLAN
            turnSystem.executeTurn(GamePhase.BUILD_AND_PLAN);
        }
    }
}
