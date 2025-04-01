package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.zhaw.frontier.components.MultiTileAnimationComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;

public class BuildingAnimationSystem extends EntitySystem {

    private final Engine engine;
    private ImmutableArray<Entity> entities;

    /**
     * Constructs a new BuildingAnimationSystem.
     *
     * @param engine the engine to which this system belongs
     */
    public BuildingAnimationSystem(Engine engine) {
        this.engine = engine;

    }

    @Override
    public void addedToEngine(Engine engine) {
        entities =
            engine.getEntitiesFor(Family.all(PositionComponent.class, RenderComponent.class, MultiTileAnimationComponent.class).get());

    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            MultiTileAnimationComponent anim = entity.getComponent(MultiTileAnimationComponent.class);
            anim.stateTime += deltaTime;
        }
    }






}
