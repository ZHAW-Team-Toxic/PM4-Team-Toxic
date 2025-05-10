package com.zhaw.frontier.systems.movement;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.algorithm.SimpleAStarPathfinder;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.TargetTypeComponent;
import com.zhaw.frontier.components.behaviours.PathfindingBehaviourComponent;

/**
 * A system that manages pathfinding for entities with {@link PathfindingBehaviourComponent}.
 * <p>
 * It uses a {@link SimpleAStarPathfinder} to compute paths toward the nearest target entity
 * matching the component type specified in {@link TargetTypeComponent}.
 * </p>
 * <p>This system performs the following tasks:</p>
 * <ul>
 *   <li>Checks if a path is needed or must be recalculated</li>
 *   <li>Finds the closest target entity of the specified type</li>
 *   <li>Computes a new path to the destination using A*</li>
 *   <li>Clears the path and marks it complete if no path is found</li>
 * </ul>
 */
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

    /**
     * Constructs a {@code PathfindingSystem} with the given pathfinder.
     *
     * @param pathfinder the A* pathfinding engine to use
     */
    public PathfindingSystem(SimpleAStarPathfinder pathfinder) {
        this.pathfinder = pathfinder;
    }

    /**
     * Called when the system is added to the engine. Stores a reference to the engine.
     *
     * @param engine the Ashley engine instance
     */
    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
    }

    /**
     * Processes all entities needing a path update.
     * <p>
     * If an entity has no destination or its target has disappeared, it searches for a new one.
     * Then it uses A* to compute a new path to the target.
     * </p>
     *
     * @param deltaTime time since last update (not used)
     */
    @Override
    public void update(float deltaTime) {
        ImmutableArray<Entity> pathingEntities = engine.getEntitiesFor(pathingFamily);

        for (Entity entity : pathingEntities) {
            PathfindingBehaviourComponent path = pathm.get(entity);
            PositionComponent pos = pm.get(entity);
            TargetTypeComponent targetType = ttm.get(entity);

            // Target removed externally? Reset.
            if (
                path.targetEntity != null && !engine.getEntities().contains(path.targetEntity, true)
            ) {
                path.destination = null;
                path.targetEntity = null;
                path.pathCompleted = false;
                path.needsRepath = true;
            }

            if ((path.hasPath() || path.pathCompleted) && !path.needsRepath) continue;

            // No destination yet? Try to acquire one.
            if (path.destination == null) {
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

            // Perform pathfinding if needed
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

    /**
     * Finds the closest entity with the given component type and a position.
     *
     * @param fromPos     the source position
     * @param targetType  the component class that valid target entities must have
     * @return the closest matching entity or {@code null} if none found
     */
    private Entity findClosestTarget(Vector2 fromPos, Class<? extends Component> targetType) {
        ImmutableArray<Entity> candidates = engine.getEntitiesFor(
            Family.all(targetType, PositionComponent.class).get()
        );

        Entity closest = null;
        float closestDist = Float.MAX_VALUE;

        for (Entity candidate : candidates) {
            Vector2 candidatePos = pm.get(candidate).basePosition;
            float dist = fromPos.dst2(candidatePos); // Squared distance for performance
            if (dist < closestDist) {
                closestDist = dist;
                closest = candidate;
            }
        }

        return closest;
    }
}
