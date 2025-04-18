package com.zhaw.frontier.systems.movement;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.components.behaviours.PathfindingBehaviourComponent;

public class PathFollowerSystem extends EntitySystem {

    private static final float SPEED = 2f; // tiles per second
    private static final float THRESHOLD = 0.1f; // how close to stop at waypoint

    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(
        PositionComponent.class
    );
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(
        VelocityComponent.class
    );
    private final ComponentMapper<PathfindingBehaviourComponent> pathm = ComponentMapper.getFor(
        PathfindingBehaviourComponent.class
    );

    private ImmutableArray<Entity> entities;

    @Override
    public void addedToEngine(Engine engine) {
        entities =
        engine.getEntitiesFor(
            Family
                .all(
                    PositionComponent.class,
                    VelocityComponent.class,
                    PathfindingBehaviourComponent.class
                )
                .get()
        );
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            PositionComponent pos = pm.get(entity);
            VelocityComponent vel = vm.get(entity);
            PathfindingBehaviourComponent path = pathm.get(entity);

            if (path.hasPath()) {
                Vector2 target = path.getNextWaypoint();
                Vector2 direction = new Vector2(target).sub(pos.basePosition);

                if (direction.len2() < THRESHOLD * THRESHOLD) {
                    path.advanceToNextWaypoint();
                    vel.desiredVelocity.setZero(); // pause when snapping to tile
                } else {
                    direction.nor().scl(SPEED);
                    vel.desiredVelocity.set(direction);
                }
            } else {
                vel.desiredVelocity.setZero(); // stop when path is done
            }
        }
    }
}
