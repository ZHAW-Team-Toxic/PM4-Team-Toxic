package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.EnemyAnimationComponent.EnemyAnimationType;
import com.zhaw.frontier.components.behaviours.PathfindingBehaviourComponent;
import com.zhaw.frontier.utils.QueueAnimation;

/**
 * A system that triggers and manages enemy attack animations based on combat conditions.
 * <p>
 * This system plays directional attack animations when an enemy:
 * <ul>
 *     <li>Has a valid target entity</li>
 *     <li>Is within attack range</li>
 *     <li>Is not moving</li>
 * </ul>
 * <p>
 * If any of those conditions are not met, it clears the current attack animation.
 * Animations are enqueued through {@link AnimationQueueComponent} using {@link QueueAnimation}.
 * </p>
 */
public class EnemyAttackAnimationSystem extends IteratingSystem {

    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(
        PositionComponent.class
    );
    private final ComponentMapper<PathfindingBehaviourComponent> pfm = ComponentMapper.getFor(
        PathfindingBehaviourComponent.class
    );
    private final ComponentMapper<AnimationQueueComponent> aqm = ComponentMapper.getFor(
        AnimationQueueComponent.class
    );
    private final ComponentMapper<CooldownComponent> cm = ComponentMapper.getFor(
        CooldownComponent.class
    );
    private final ComponentMapper<AttackComponent> am = ComponentMapper.getFor(
        AttackComponent.class
    );
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(
        VelocityComponent.class
    );
    private final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(
        HealthComponent.class
    );

    /**
     * Constructs a system that updates all entities with position, attack, animation queue, and cooldown.
     */
    public EnemyAttackAnimationSystem() {
        super(
            Family
                .all(
                    PositionComponent.class,
                    AttackComponent.class,
                    AnimationQueueComponent.class,
                    CooldownComponent.class
                )
                .get()
        );
    }

    /**
     * Processes a single enemy entity per frame.
     * If the entity has a valid target in range and is not moving, an attack animation is played.
     * Otherwise, any ongoing attack animation is cleared.
     *
     * @param attacker  the attacking entity
     * @param deltaTime time since last frame (unused here)
     */
    @Override
    protected void processEntity(Entity attacker, float deltaTime) {
        if (!pm.has(attacker) || !am.has(attacker)) return;

        PathfindingBehaviourComponent path = pfm.get(attacker);
        Entity target = (path != null &&
                path.targetEntity != null &&
                !path.targetEntity.isScheduledForRemoval())
            ? path.targetEntity
            : null;

        if (target == null || !pm.has(target) || !hm.has(target) || hm.get(target).isDead) {
            clearAttackAnimationIfPresent(attacker);
            return;
        }

        Vector2 from = pm.get(attacker).basePosition;
        Vector2 to = pm.get(target).basePosition;
        float distance = from.dst(to);
        float range = am.get(attacker).attackRange;

        if (distance > range) {
            clearAttackAnimationIfPresent(attacker);
            return;
        }

        if (vm.has(attacker) && vm.get(attacker).velocity.len2() > 0.01f) {
            clearAttackAnimationIfPresent(attacker);
            return;
        }

        // Play directional attack animation
        Vector2 dir = new Vector2(to).sub(from).nor();
        EnemyAnimationType animType = getAttackAnimationDirection(dir);

        AnimationQueueComponent animQueue = aqm.get(attacker);
        animQueue.queue.clear();

        QueueAnimation attackAnim = new QueueAnimation();
        attackAnim.animationType = animType;
        attackAnim.loop = true;
        attackAnim.timeLeft = am.get(attacker).attackInterval;

        animQueue.queue.add(attackAnim);
    }

    /**
     * Clears any current attack animation from the queue if the entity is no longer attacking.
     *
     * @param attacker the entity whose animation should be cleared
     */
    private void clearAttackAnimationIfPresent(Entity attacker) {
        AnimationQueueComponent queue = aqm.get(attacker);
        if (queue != null && !queue.queue.isEmpty()) {
            QueueAnimation current = queue.queue.peek();
            boolean isAttacking =
                current.animationType instanceof EnemyAnimationType &&
                ((EnemyAnimationType) current.animationType).name().startsWith("ATTACK");

            if (isAttacking) {
                queue.queue.clear();
            }
        }
    }

    /**
     * Determines the attack animation direction based on movement vector.
     *
     * @param dir normalized direction vector
     * @return corresponding {@link EnemyAnimationType} (e.g., ATTACK_LEFT, ATTACK_UP)
     */
    private EnemyAnimationType getAttackAnimationDirection(Vector2 dir) {
        if (Math.abs(dir.x) > Math.abs(dir.y)) {
            return dir.x > 0 ? EnemyAnimationType.ATTACK_RIGHT : EnemyAnimationType.ATTACK_LEFT;
        } else {
            return dir.y > 0 ? EnemyAnimationType.ATTACK_UP : EnemyAnimationType.ATTACK_DOWN;
        }
    }
}
