package com.zhaw.frontier;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.AnimationQueueComponent;
import com.zhaw.frontier.components.ConditionalAnimationComponent;
import com.zhaw.frontier.components.EnemyAnimationComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.systems.ConditionalAnimationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConditionalAnimationManagerTest {

    private Engine testEngine;
    private ConditionalAnimationManager manager;

    @BeforeEach
    public void setup() {
        testEngine = new Engine();
        manager = new ConditionalAnimationManager();
    }

    @Test
    public void testEnemyConditionalAnimationFinishesAfterTime() {
        Entity enemy = testEngine.createEntity();

        PositionComponent position = new PositionComponent();
        position.lookingDirection = new Vector2(0, -1);
        enemy.add(position);

        EnemyAnimationComponent enemyAnim = new EnemyAnimationComponent();
        enemyAnim.currentAnimation = EnemyAnimationComponent.EnemyAnimationType.IDLE_DOWN;
        enemyAnim.stateTime = 0;

        enemy.add(enemyAnim);

        assertEquals(
            EnemyAnimationComponent.EnemyAnimationType.IDLE_DOWN,
            enemyAnim.currentAnimation
        );

        AnimationQueueComponent queue = new AnimationQueueComponent();
        ConditionalAnimationComponent anim = new ConditionalAnimationComponent();
        anim.animationType = EnemyAnimationComponent.EnemyAnimationType.ATTACK_DOWN;
        anim.timeLeft = 2.0f;
        anim.loop = false;

        queue.queue.add(anim);

        enemy.add(queue);

        float dt = 1.0f;

        manager.process(enemy, dt);
        assertEquals(
            EnemyAnimationComponent.EnemyAnimationType.ATTACK_DOWN,
            enemyAnim.currentAnimation
        );
        assertEquals(1.0f, enemyAnim.stateTime);
        assertFalse(queue.queue.isEmpty());

        manager.process(enemy, dt);
        assertEquals(
            EnemyAnimationComponent.EnemyAnimationType.IDLE_DOWN,
            enemyAnim.currentAnimation
        );
        assertEquals(0.0f, enemyAnim.stateTime); // Reset
        assertTrue(queue.queue.isEmpty());
    }
}
