package com.zhaw.frontier;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.systems.AnimationSystem;
import com.zhaw.frontier.systems.MovementSystem;
import com.zhaw.frontier.utils.QueueAnimation;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Unit tests for verifying the animation logic managed by {@link AnimationSystem}.
 *
 * <p>
 * These tests simulate enemy movement and conditional animations and verify
 * that the correct animations are selected and processed in the correct sequence.
 * </p>
 */
@ExtendWith(GdxExtension.class)
public class AnimationSystemTest {

    private static Engine testEngine;

    /**
     * Initializes the test engine and adds the systems under test.
     */
    @BeforeAll
    public static void setup() {
        testEngine = new Engine();
        testEngine.addSystem(new MovementSystem());
        addSystemsUnderTestHere();
    }

    /**
     * Fügt das zu testende System in die Engine ein: {@link AnimationSystem}.
     */
    private static void addSystemsUnderTestHere() {
        testEngine.addSystem(new AnimationSystem());
    }

    /**
     * Clears all entities from the engine before each test.
     * This ensures that each test starts with a clean state.
     */
    @BeforeEach
    public void clearEntities() {
        testEngine.removeAllEntities();
    }

    /**
     * Verifies that when an entity moves upward, the animation system selects the WALK_UP animation.
     */
    @Test
    public void testWalkUpAnimationIsSelectedWhenMovingUp() {
        // Entity vorbereiten
        Entity entity = testEngine.createEntity();

        PositionComponent position = new PositionComponent();
        position.basePosition.x = 4;
        position.basePosition.y = 4;
        position.lookingDirection = new Vector2(0, 1); // Bewegung nach oben
        entity.add(position);

        RenderComponent render = new RenderComponent();
        render.renderType = RenderComponent.RenderType.ENEMY;
        render.heightInTiles = 1;
        render.widthInTiles = 1;
        entity.add(render);

        EnemyAnimationComponent anim = new EnemyAnimationComponent();
        anim.animations.put(
            EnemyAnimationComponent.EnemyAnimationType.WALK_UP,
            new DummyAnimation("WALK_UP")
        );
        anim.animations.put(
            EnemyAnimationComponent.EnemyAnimationType.WALK_DOWN,
            new DummyAnimation("WALK_DOWN")
        );
        anim.animations.put(
            EnemyAnimationComponent.EnemyAnimationType.WALK_LEFT,
            new DummyAnimation("WALK_LEFT")
        );
        anim.animations.put(
            EnemyAnimationComponent.EnemyAnimationType.WALK_RIGHT,
            new DummyAnimation("WALK_RIGHT")
        );
        anim.currentAnimation = EnemyAnimationComponent.EnemyAnimationType.WALK_DOWN; // Initiale Animation

        VelocityComponent vel = new VelocityComponent();
        vel.velocity.set(new Vector2(0, 1)); // Bewegung nach oben

        entity.add(anim);
        entity.add(vel);

        testEngine.addEntity(entity);

        // System verarbeiten
        AnimationSystem animationSystem = testEngine.getSystem(AnimationSystem.class);

        animationSystem.update(entity, 2f);

        // Erwartung: WALK_UP wurde gesetzt
        assertEquals(EnemyAnimationComponent.EnemyAnimationType.WALK_UP, anim.currentAnimation);
    }

    /**
     * Verifies that multiple conditional animations in a queue are processed in order.
     * <p>
     * The test simulates two sequential conditional animations:
     * ATTACK_DOWN followed by IDLE_RIGHT. It asserts that the animation system:
     * <ul>
     *     <li>Sets ATTACK_DOWN as the current animation first</li>
     *     <li>After time runs out, switches to IDLE_RIGHT</li>
     *     <li>Removes both animations from the queue</li>
     * </ul>
     * </p>
     */
    @Test
    public void testMultipleConditionalAnimationsProcessedInSequence() {
        AnimationSystem animationSystem = testEngine.getSystem(AnimationSystem.class);

        Entity enemy = testEngine.createEntity();
        PositionComponent position = new PositionComponent();
        position.lookingDirection = new Vector2(-1, 0);
        enemy.add(position);

        RenderComponent render = new RenderComponent();
        render.renderType = RenderComponent.RenderType.ENEMY;
        render.heightInTiles = 1;
        render.widthInTiles = 1;
        enemy.add(render);

        EnemyAnimationComponent enemyAnim = new EnemyAnimationComponent();
        enemyAnim.currentAnimation = EnemyAnimationComponent.EnemyAnimationType.WALK_LEFT;
        enemy.add(enemyAnim);

        AnimationQueueComponent queue = new AnimationQueueComponent();
        enemy.add(queue);

        animationSystem.update(enemy, 1f);
        assertEquals(
            EnemyAnimationComponent.EnemyAnimationType.WALK_LEFT,
            enemyAnim.currentAnimation
        );
        assertTrue(queue.queue.isEmpty());

        QueueAnimation attack = new QueueAnimation();
        attack.animationType = EnemyAnimationComponent.EnemyAnimationType.ATTACK_DOWN;
        attack.timeLeft = 1.0f;
        position.lookingDirection = new Vector2(0, -1);

        queue.queue.add(attack);

        QueueAnimation idle_right = new QueueAnimation();
        idle_right.animationType = EnemyAnimationComponent.EnemyAnimationType.IDLE_RIGHT;
        idle_right.timeLeft = 1.0f;
        position.lookingDirection = new Vector2(1, 0);

        queue.queue.add(idle_right);

        animationSystem.update(enemy, 0.5f);
        assertEquals(
            EnemyAnimationComponent.EnemyAnimationType.ATTACK_DOWN,
            enemyAnim.currentAnimation
        );
        animationSystem.update(enemy, 0.5f);

        assertEquals(
            EnemyAnimationComponent.EnemyAnimationType.IDLE_RIGHT,
            enemyAnim.currentAnimation
        );

        animationSystem.update(enemy, 1f);

        assertEquals(
            EnemyAnimationComponent.EnemyAnimationType.IDLE_RIGHT,
            enemyAnim.currentAnimation
        );
        assertTrue(queue.queue.isEmpty());
    }

    /**
     * DummyAnimation is a minimal Animation implementation used for testing animation switching logic
     * without requiring actual texture assets.
     */
    private static class DummyAnimation extends Animation<TextureRegion> {

        private final String id;

        public DummyAnimation(String id) {
            super(1f, new TextureRegion());
            this.id = id;
        }

        @Override
        public String toString() {
            return id;
        }
    }

    @AfterAll
    public static void tearDown() {
        testEngine.removeAllEntities();
        testEngine.removeAllSystems();
    }
}
