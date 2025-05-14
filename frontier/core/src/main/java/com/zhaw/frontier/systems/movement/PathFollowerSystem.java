package com.zhaw.frontier.systems.movement;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.behaviours.PathfindingBehaviourComponent;
import com.zhaw.frontier.configs.AppProperties;

/**
 * A system that moves entities along a path defined by {@link PathfindingBehaviourComponent}.
 * <p>
 * The system checks whether the next waypoint is blocked by any static entity
 * (with {@link OccupiesTilesComponent} but without {@link PathfindingBehaviourComponent})
 * and updates the {@link VelocityComponent} accordingly.
 * </p>
 *
 * <p>Main responsibilities:</p>
 * <ul>
 *     <li>Detects whether the next waypoint is blocked by a static entity</li>
 *     <li>Moves the entity toward the next waypoint using its movement speed</li>
 *     <li>Snaps the entity to the waypoint once it is reached</li>
 *     <li>Clears velocity if the path is completed or blocked</li>
 * </ul>
 */
public class PathFollowerSystem extends EntitySystem {

    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(
        PositionComponent.class
    );
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(
        VelocityComponent.class
    );
    private final ComponentMapper<PathfindingBehaviourComponent> pathm = ComponentMapper.getFor(
        PathfindingBehaviourComponent.class
    );
    private final ComponentMapper<OccupiesTilesComponent> occm = ComponentMapper.getFor(
        OccupiesTilesComponent.class
    );

    private ImmutableArray<Entity> entities;
    private ImmutableArray<Entity> tileOccupiers;

    /**
     * Called when the system is added to the engine. Sets up the entity filters.
     *
     * @param engine the Ashley engine
     */
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

        tileOccupiers =
        engine.getEntitiesFor(
            Family
                .all(OccupiesTilesComponent.class)
                .exclude(PathfindingBehaviourComponent.class)
                .get()
        );
    }

    /**
     * Updates all path-following entities.
     * <p>
     * If the next waypoint is blocked, movement is halted.
     * Otherwise, the entity moves toward the waypoint, and once close enough,
     * it snaps to the tile and advances the path.
     * </p>
     *
     * @param deltaTime the time elapsed since the last frame (unused in logic)
     */
    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            PositionComponent pos = pm.get(entity);
            VelocityComponent vel = vm.get(entity);
            PathfindingBehaviourComponent path = pathm.get(entity);

            float speed = path.speed;

            if (path.hasPath()) {
                Vector2 nextWaypoint = path.getNextWaypoint();
                int tileX = (int) nextWaypoint.x;
                int tileY = (int) nextWaypoint.y;

                boolean isBlocked = false;

                // Check if the waypoint is occupied
                for (int j = 0; j < tileOccupiers.size(); j++) {
                    Entity blocker = tileOccupiers.get(j);
                    OccupiesTilesComponent occ = occm.get(blocker);
                    for (Vector2 tile : occ.occupiedTiles) {
                        if ((int) tile.x == tileX && (int) tile.y == tileY) {
                            isBlocked = true;
                            break;
                        }
                    }
                    if (isBlocked) break;
                }

                if (isBlocked) {
                    vel.desiredVelocity.setZero(); // Blocked: stop movement
                    continue;
                }

                // Move toward next waypoint
                Vector2 direction = new Vector2(nextWaypoint).sub(pos.basePosition);

                if (
                    direction.len2() <
                    AppProperties.PATH_FOLLOWER_STOP_TRESHOLD *
                        AppProperties.PATH_FOLLOWER_STOP_TRESHOLD
                ) {
                    path.advanceToNextWaypoint();
                    vel.desiredVelocity.setZero(); // Reached waypoint
                } else {
                    direction.nor().scl(speed);
                    vel.desiredVelocity.set(direction);
                }
            } else {
                vel.desiredVelocity.setZero(); // No path
            }
        }
    }
}
