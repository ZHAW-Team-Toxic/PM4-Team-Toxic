package com.zhaw.frontier.systems;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.AnimationQueueComponent;
import com.zhaw.frontier.components.AttackComponent;
import com.zhaw.frontier.components.CooldownComponent;
import com.zhaw.frontier.components.EnemyAnimationComponent.EnemyAnimationType;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.TeamComponent;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.components.behaviours.PathfindingBehaviourComponent;
import com.zhaw.frontier.enums.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnemyAttackAnimationSystemTest {

    private Engine engine;
    private EnemyAttackAnimationSystem system;

    @BeforeEach
    void setup() {
        engine = new Engine();
        system = new EnemyAttackAnimationSystem();
        engine.addSystem(system);
    }

    private Entity createAttacker(Vector2 position, float range) {
        Entity entity = new Entity();
        entity.add(new PositionComponent(position.x, position.y, 1, 1));
        entity.add(new AttackComponent(5f, range, 1000f));
        entity.add(new CooldownComponent());
        entity.add(new AnimationQueueComponent());
        entity.add(new VelocityComponent());
        entity.add(new PathfindingBehaviourComponent(1f));
        entity.add(new TeamComponent(Team.ENEMY));
        return entity;
    }

    private Entity createTarget(Vector2 position, boolean isDead) {
        Entity entity = new Entity();
        PositionComponent pos = new PositionComponent(position.x, position.y, 1, 1);
        HealthComponent hc = new HealthComponent();
        hc.currentHealth = isDead ? 0 : 10;
        hc.isDead = isDead;
        entity.add(pos);
        entity.add(hc);
        entity.add(new TeamComponent(Team.PLAYER));
        return entity;
    }

    @Test
    void testAttackAnimationPlaysWhenTargetInRangeAndStill() {
        Entity attacker = createAttacker(new Vector2(1, 1), 2f);
        Entity target = createTarget(new Vector2(2, 1), false);

        PathfindingBehaviourComponent path = attacker.getComponent(
            PathfindingBehaviourComponent.class
        );
        path.targetEntity = target;

        engine.addEntity(attacker);
        engine.addEntity(target);

        engine.update(0.016f);

        AnimationQueueComponent queue = attacker.getComponent(AnimationQueueComponent.class);
        assertFalse(queue.queue.isEmpty());
        assertTrue(queue.queue.peek().animationType instanceof EnemyAnimationType);
        assertTrue(queue.queue.peek().animationType.name().startsWith("ATTACK"));
    }

    @Test
    void testNoAnimationWhenTargetIsDead() {
        Entity attacker = createAttacker(new Vector2(1, 1), 2f);
        Entity target = createTarget(new Vector2(2, 1), true);

        PathfindingBehaviourComponent path = attacker.getComponent(
            PathfindingBehaviourComponent.class
        );
        path.targetEntity = target;

        engine.addEntity(attacker);
        engine.addEntity(target);

        engine.update(0.016f);

        AnimationQueueComponent queue = attacker.getComponent(AnimationQueueComponent.class);
        assertTrue(queue.queue.isEmpty());
    }

    @Test
    void testNoAnimationWhenTargetOutOfRange() {
        Entity attacker = createAttacker(new Vector2(1, 1), 1f);
        Entity target = createTarget(new Vector2(5, 1), false);

        PathfindingBehaviourComponent path = attacker.getComponent(
            PathfindingBehaviourComponent.class
        );
        path.targetEntity = target;

        engine.addEntity(attacker);
        engine.addEntity(target);

        engine.update(0.016f);

        AnimationQueueComponent queue = attacker.getComponent(AnimationQueueComponent.class);
        assertTrue(queue.queue.isEmpty());
    }

    @Test
    void testNoAnimationWhenAttackerIsMoving() {
        Entity attacker = createAttacker(new Vector2(1, 1), 2f);
        Entity target = createTarget(new Vector2(2, 1), false);

        PathfindingBehaviourComponent path = attacker.getComponent(
            PathfindingBehaviourComponent.class
        );
        path.targetEntity = target;

        VelocityComponent vel = attacker.getComponent(VelocityComponent.class);
        vel.velocity.set(1f, 0f);

        engine.addEntity(attacker);
        engine.addEntity(target);

        engine.update(0.016f);

        AnimationQueueComponent queue = attacker.getComponent(AnimationQueueComponent.class);
        assertTrue(queue.queue.isEmpty());
    }
}
