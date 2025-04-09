package com.zhaw.frontier;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.AnimationQueueComponent;
import com.zhaw.frontier.utils.QueueAnimation;
import com.zhaw.frontier.components.EnemyAnimationComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.systems.QueueAnimationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for verifying conditional animation logic handled by {@link QueueAnimationManager}.
 *
 * <p>
 * The tests check whether queued animations are activated, run for the specified duration,
 * and return to an idle state when done. Also verifies that looping animations are not removed.
 * </p>
 */
public class QueueAnimationManagerTest {

    private Engine testEngine;
    private QueueAnimationManager manager;

    /**
     * Initializes the test engine and the animation manager before each test.
     */
    @BeforeEach
    public void setup() {
        testEngine = new Engine();
        addSystemsUnderTestHere();
    }

    public void addSystemsUnderTestHere() {
        manager = new QueueAnimationManager();
    }

    /**
     * Tests that a non-looping conditional animation (e.g., ATTACK_DOWN) runs for the given time
     * and then resets to the appropriate IDLE animation based on the entity's direction.
     */
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
        QueueAnimation anim = new QueueAnimation();
        anim.animationType = EnemyAnimationComponent.EnemyAnimationType.ATTACK_DOWN;
        anim.timeLeft = 2.0f;
        anim.loop = false;

        queue.queue.add(anim);

        enemy.add(queue);

        float dt = 1.0f;

        // Erster Tick: Start der Animation
        manager.process(enemy, dt);
        assertEquals(
            EnemyAnimationComponent.EnemyAnimationType.ATTACK_DOWN,
            enemyAnim.currentAnimation
        );
        assertEquals(0.0f, enemyAnim.stateTime); // Reset beim Animationswechsel
        assertFalse(queue.queue.isEmpty());

        // Zweiter Tick: Laufzeit überschritten → zurück zu Idle
        manager.process(enemy, dt);
        assertEquals(
            EnemyAnimationComponent.EnemyAnimationType.IDLE_DOWN,
            enemyAnim.currentAnimation
        );
        assertTrue(enemyAnim.stateTime <= 0.001f, "StateTime should be reset after returning to idle");
        assertTrue(queue.queue.isEmpty());
    }

    /**
     * Tests that a looping conditional animation is not removed from the queue after time passes.
     * <p>
     * Even though timeLeft is decreased, the animation should remain active.
     * </p>
     */
    @Test
    public void testLoopingConditionalAnimationPersists() {
        Entity enemy = testEngine.createEntity();

        PositionComponent position = new PositionComponent();
        position.lookingDirection = new Vector2(0, -1);
        enemy.add(position);

        EnemyAnimationComponent enemyAnim = new EnemyAnimationComponent();
        enemyAnim.currentAnimation = EnemyAnimationComponent.EnemyAnimationType.IDLE_DOWN;
        enemyAnim.stateTime = 0;

        enemy.add(enemyAnim);

        AnimationQueueComponent queue = new AnimationQueueComponent();
        QueueAnimation anim = new QueueAnimation();
        anim.animationType = EnemyAnimationComponent.EnemyAnimationType.ATTACK_DOWN;
        anim.timeLeft = 2.0f;
        anim.loop = true;

        queue.queue.add(anim);
        enemy.add(queue);

        float dt = 1.0f;

        // Erster Tick: Animation beginnt (Reset erwartet)
        manager.process(enemy, dt);
        assertEquals(
            EnemyAnimationComponent.EnemyAnimationType.ATTACK_DOWN,
            enemyAnim.currentAnimation
        );
        assertEquals(0.0f, enemyAnim.stateTime); // reset

        // Zweiter Tick: weiterlaufende Loop-Animation
        manager.process(enemy, dt);
        assertEquals(1.0f, enemyAnim.stateTime);
        assertEquals(
            EnemyAnimationComponent.EnemyAnimationType.ATTACK_DOWN,
            enemyAnim.currentAnimation
        );
        assertFalse(queue.queue.isEmpty(), "Looping animation should not be removed.");
    }
}
