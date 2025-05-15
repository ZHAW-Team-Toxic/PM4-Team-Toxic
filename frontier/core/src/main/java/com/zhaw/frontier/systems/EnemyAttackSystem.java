package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.behaviours.PathfindingBehaviourComponent;
import java.util.List;

/**
 * System that enables AI-controlled entities (enemies) to attack hostile targets.
 * <p>
 * Entities using this system must have:
 * <ul>
 *   <li>{@link PositionComponent}</li>
 *   <li>{@link AttackComponent}</li>
 *   <li>{@link HealthComponent}</li>
 *   <li>{@link TeamComponent}</li>
 * </ul>
 * This system excludes {@link TowerComponent} and {@link DeathComponent}.
 * </p>
 *
 * <p>Attack priorities:</p>
 * <ol>
 *   <li>If the entity has a {@link PathfindingBehaviourComponent} with a valid targetEntity, that is used.</li>
 *   <li>Otherwise, the system searches for the nearest valid target within range.</li>
 * </ol>
 *
 * <p>When an attack occurs:</p>
 * <ul>
 *   <li>Target's health is reduced by the attacker's damage</li>
 *   <li>The attacker enters a cooldown state via {@link CooldownComponent}</li>
 *   <li>If the target dies, pathfinding is reset</li>
 * </ul>
 */
public class EnemyAttackSystem extends EntitySystem {

    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(
        PositionComponent.class
    );
    private final ComponentMapper<AttackComponent> am = ComponentMapper.getFor(
        AttackComponent.class
    );
    private final ComponentMapper<TeamComponent> tm = ComponentMapper.getFor(TeamComponent.class);
    private final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(
        HealthComponent.class
    );
    private final ComponentMapper<PathfindingBehaviourComponent> pfm = ComponentMapper.getFor(
        PathfindingBehaviourComponent.class
    );
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(
        VelocityComponent.class
    );
    private final ComponentMapper<CooldownComponent> cm = ComponentMapper.getFor(
        CooldownComponent.class
    );
    private final ComponentMapper<OccupiesTilesComponent> otm = ComponentMapper.getFor(
        OccupiesTilesComponent.class
    );

    private ImmutableArray<Entity> attackers;
    private ImmutableArray<Entity> potentialTargets;

    /**
     * Called when the system is added to the engine.
     * Filters attacker and target entities.
     *
     * @param engine the Ashley engine instance
     */
    @Override
    public void addedToEngine(Engine engine) {
        attackers =
        engine.getEntitiesFor(
            Family
                .all(
                    PositionComponent.class,
                    AttackComponent.class,
                    TeamComponent.class,
                    HealthComponent.class
                )
                .exclude(TowerComponent.class, DeathComponent.class)
                .get()
        );

        potentialTargets =
        engine.getEntitiesFor(
            Family
                .all(PositionComponent.class, TeamComponent.class, HealthComponent.class)
                .exclude(DeathComponent.class)
                .get()
        );
    }

    /**
     * Updates all attacking entities.
     * Checks if an entity can attack its target (path target or closest valid enemy).
     * If in range and not on cooldown, applies damage and sets a cooldown.
     *
     * @param deltaTime time since the last frame (not used)
     */
    @Override
    public void update(float deltaTime) {
        for (Entity attacker : attackers) {
            if (cm.has(attacker)) continue; // Skip if in cooldown

            AttackComponent attack = am.get(attacker);
            PositionComponent pos = pm.get(attacker);
            PathfindingBehaviourComponent path = pfm.get(attacker);

            Entity target = null;
            Vector2 attackerPos = pos.basePosition;
            float closestDistance = Float.MAX_VALUE;

            // Priority 1: current pathfinding target
            if (
                path != null &&
                path.targetEntity != null &&
                !path.targetEntity.isScheduledForRemoval()
            ) {
                Entity potential = path.targetEntity;
                if (
                    attacker != potential &&
                    isHostile(attacker, potential) &&
                    hm.has(potential) &&
                    pm.has(potential)
                ) {
                    float distance = getDistanceToTarget(attackerPos, potential);
                    if (distance <= attack.attackRange) {
                        target = potential;
                        closestDistance = distance;
                    }
                }
            }

            // Priority 2: any nearby hostile target
            if (target == null) {
                for (Entity possible : potentialTargets) {
                    if (attacker == possible || !isHostile(attacker, possible)) continue;
                    if (!pm.has(possible)) continue;

                    float distance = pos.basePosition.dst(pm.get(possible).basePosition);
                    if (distance <= attack.attackRange) {
                        target = possible;
                        break;
                    }
                }
            }

            if (target == null) continue;

            boolean inRange = isTargetInRange(attackerPos, target, attack.attackRange);
            if (inRange) {
                // Stop before attacking
                if (vm.has(attacker)) {
                    Vector2 velocity = vm.get(attacker).velocity;
                    if (velocity.len2() > 0.01f) continue;
                    vm.get(attacker).desiredVelocity.setZero();
                }

                // Deal damage
                HealthComponent health = hm.get(target);
                health.currentHealth -= attack.damage;

                // Apply attack cooldown
                CooldownComponent cooldown = new CooldownComponent();
                cooldown.start = System.currentTimeMillis();
                cooldown.duration = (long) attack.attackInterval;
                attacker.add(cooldown);

                // Handle target death
                if (health.currentHealth <= 0) {
                    if (path != null && target == path.targetEntity) {
                        path.clearTargetEntity();
                        path.resetPath();
                    } else if (path != null) {
                        path.resetPath();
                    }
                }
            }
        }
    }

    private boolean isTargetInRange(Vector2 attackerPos, Entity target, float attackRange) {
        if (otm.has(target)) {
            List<Vector2> tiles = otm.get(target).occupiedTiles;
            for (Vector2 tile : tiles) {
                if (attackerPos.dst(tile) <= attackRange) return true;
            }
            return false;
        } else {
            Vector2 targetPos = pm.get(target).basePosition;
            return attackerPos.dst(targetPos) <= attackRange;
        }
    }

    private float getDistanceToTarget(Vector2 attackerPos, Entity target) {
        if (otm.has(target)) {
            List<Vector2> tiles = otm.get(target).occupiedTiles;
            float minDist = Float.MAX_VALUE;
            for (Vector2 tile : tiles) {
                float dist = attackerPos.dst(tile);
                if (dist < minDist) minDist = dist;
            }
            return minDist;
        } else {
            return attackerPos.dst(pm.get(target).basePosition);
        }
    }

    /**
     * Checks whether two entities are hostile to each other based on their teams.
     *
     * @param a the first entity
     * @param b the second entity
     * @return true if the entities are on different teams; false otherwise
     */
    private boolean isHostile(Entity a, Entity b) {
        return tm.has(a) && tm.has(b) && tm.get(a).team != tm.get(b).team;
    }
}
