package com.zhaw.frontier.systems.movement;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.algorithm.SimpleAStarPathfinder;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.TargetTypeComponent;
import com.zhaw.frontier.components.behaviours.PathfindingBehaviourComponent;

public class PathfindingSystem extends EntitySystem {

    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(
        PositionComponent.class
    );
    private final ComponentMapper<PathfindingBehaviourComponent> pathm = ComponentMapper.getFor(
        PathfindingBehaviourComponent.class
    );
    private final ComponentMapper<TargetTypeComponent> ttm = ComponentMapper.getFor(
        TargetTypeComponent.class
    );

    private final SimpleAStarPathfinder pathfinder;
    private Engine engine;

    private final Family pathingFamily = Family
        .all(
            PositionComponent.class,
            PathfindingBehaviourComponent.class,
            TargetTypeComponent.class
        )
        .get();

    public PathfindingSystem(SimpleAStarPathfinder pathfinder) {
        this.pathfinder = pathfinder;
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void update(float deltaTime) {
        ImmutableArray<Entity> pathingEntities = engine.getEntitiesFor(pathingFamily);

        for (Entity entity : pathingEntities) {
            PathfindingBehaviourComponent path = pathm.get(entity);
            PositionComponent pos = pm.get(entity);
            TargetTypeComponent targetType = ttm.get(entity);

            if (
                path.targetEntity != null && !engine.getEntities().contains(path.targetEntity, true)
            ) {
                path.destination = null;
                path.targetEntity = null;
                path.pathCompleted = false;
                path.needsRepath = true;
            }

            if ((path.hasPath() || path.pathCompleted) && !path.needsRepath) continue;

            if (path.destination == null) {
                // Try to find nearest entity with the target component type
                Entity closest = findClosestTarget(
                    pos.basePosition,
                    targetType.targetComponentType
                );

                if (closest != null) {
                    Vector2 targetPos = pm.get(closest).basePosition;
                    path.destination = new Vector2((int) targetPos.x, (int) targetPos.y);
                    path.targetEntity = closest;
                    path.needsRepath = true;
                }
            }

            if (path.destination != null && path.needsRepath) {
                Vector2 start = new Vector2((int) pos.basePosition.x, (int) pos.basePosition.y);
                Vector2 end = new Vector2((int) path.destination.x, (int) path.destination.y);
                path.waypoints.clear();
                path.waypoints.addAll(pathfinder.findPath(start, end));
                path.pathCompleted = path.waypoints.isEmpty();
                path.needsRepath = false;
            }
        }
    }

    private Entity findClosestTarget(Vector2 fromPos, Class<? extends Component> targetType) {
        ImmutableArray<Entity> candidates = engine.getEntitiesFor(
            Family.all(targetType, PositionComponent.class).get()
        );

        Entity closest = null;
        float closestDist = Float.MAX_VALUE;

        for (Entity candidate : candidates) {
            Vector2 candidatePos = pm.get(candidate).basePosition;
            float dist = fromPos.dst2(candidatePos); // use squared distance for performance
            if (dist < closestDist) {
                closestDist = dist;
                closest = candidate;
            }
        }

        return closest;
    }
}
