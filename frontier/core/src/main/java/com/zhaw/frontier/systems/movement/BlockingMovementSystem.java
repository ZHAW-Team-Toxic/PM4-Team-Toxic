package com.zhaw.frontier.systems.movement;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.behaviours.PathfindingBehaviourComponent;

/**
 * A system that detects if an entity using {@link PathfindingBehaviourComponent}
 * is blocked by a hostile static entity that occupies tiles (e.g., buildings or walls).
 * <p>
 * If the next waypoint is blocked by a hostile, the entity will mark that blocker as a target.
 * This allows for dynamic path interruption and enemy aggression logic.
 * </p>
 * <p>Entities handled must have:</p>
 * <ul>
 *     <li>{@link PositionComponent}</li>
 *     <li>{@link VelocityComponent}</li>
 *     <li>{@link PathfindingBehaviourComponent}</li>
 * </ul>
 * <p>Blockers must have {@link OccupiesTilesComponent} and not {@link PathfindingBehaviourComponent}.</p>
 */
public class BlockingMovementSystem extends EntitySystem {

    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(
        VelocityComponent.class
    );
    private final ComponentMapper<PathfindingBehaviourComponent> pfm = ComponentMapper.getFor(
        PathfindingBehaviourComponent.class
    );
    private final ComponentMapper<OccupiesTilesComponent> occm = ComponentMapper.getFor(
        OccupiesTilesComponent.class
    );
    private final ComponentMapper<TeamComponent> tm = ComponentMapper.getFor(TeamComponent.class);

    private ImmutableArray<Entity> enemies;
    private ImmutableArray<Entity> tileOccupiers;

    /**
     * Called when the system is added to the engine.
     * Initializes filtered lists for moving pathfinding entities and static tile-occupying blockers.
     *
     * @param engine the Ashley engine
     */
    @Override
    public void addedToEngine(Engine engine) {
        enemies =
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
     * Checks each pathfinding entity to determine if its next waypoint is blocked by a hostile entity.
     * If blocked, sets the blocker as the target in the {@link PathfindingBehaviourComponent}.
     *
     * @param deltaTime time since last frame (unused)
     */
    @Override
    public void update(float deltaTime) {
        int enemyCount = enemies.size();
        int blockerCount = tileOccupiers.size();

        for (int i = 0; i < enemyCount; i++) {
            Entity enemy = enemies.get(i);
            VelocityComponent vel = vm.get(enemy);
            PathfindingBehaviourComponent path = pfm.get(enemy);

            if (!vel.desiredVelocity.isZero() || !path.hasPath()) continue;

            Vector2 nextWaypoint = path.getNextWaypoint();
            int nextX = (int) Math.floor(nextWaypoint.x);
            int nextY = (int) Math.floor(nextWaypoint.y);

            boolean blockerFound = false;

            // Static blockers (e.g., hostile buildings)
            for (int j = 0; j < blockerCount; j++) {
                Entity blocker = tileOccupiers.get(j);
                if (blocker == enemy) continue;

                OccupiesTilesComponent occ = occm.get(blocker);
                for (Vector2 tile : occ.occupiedTiles) {
                    int tileX = (int) Math.floor(tile.x);
                    int tileY = (int) Math.floor(tile.y);

                    if (tileX == nextX && tileY == nextY && isHostile(enemy, blocker)) {
                        path.setTargetEntity(blocker);
                        blockerFound = true;
                        break;
                    }
                }

                if (blockerFound) break;
            }
        }
    }

    /**
     * Returns true if the two entities belong to different teams.
     *
     * @param a the first entity
     * @param b the second entity
     * @return true if they are hostile; false otherwise
     */
    private boolean isHostile(Entity a, Entity b) {
        return tm.has(a) && tm.has(b) && tm.get(a).team != tm.get(b).team;
    }
}
