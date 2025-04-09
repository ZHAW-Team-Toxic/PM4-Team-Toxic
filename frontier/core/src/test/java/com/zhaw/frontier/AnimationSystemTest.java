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

@ExtendWith(GdxExtension.class)
public class AnimationSystemTest {

    public static Engine testEngine;

    @BeforeAll
    public static void setup() {
        testEngine = new Engine();
        addSystemsUnderTestHere();
    }

    /**
     * Fügt das zu testende System in die Engine ein: {@link AnimationSystem}.
     */
    private static void addSystemsUnderTestHere() {
        testEngine.addSystem(new MovementSystem());
        testEngine.addSystem(new AnimationSystem());
    }

    @BeforeEach
    public void clearEntities() {
        testEngine.removeAllEntities();
    }

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
        assertEquals(EnemyAnimationComponent.EnemyAnimationType.WALK_LEFT, enemyAnim.currentAnimation);
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


        animationSystem.update(enemy,0.5f);
        assertEquals(EnemyAnimationComponent.EnemyAnimationType.ATTACK_DOWN, enemyAnim.currentAnimation);
        animationSystem.update(enemy,0.5f);

        assertEquals(EnemyAnimationComponent.EnemyAnimationType.IDLE_RIGHT, enemyAnim.currentAnimation);

        animationSystem.update(enemy,1f);

        assertEquals(EnemyAnimationComponent.EnemyAnimationType.IDLE_RIGHT, enemyAnim.currentAnimation);
        assertTrue(queue.queue.isEmpty());

    }

    /**
     * DummyAnimation dient als Platzhalter für echte Animationen,
     * damit wir ohne Texturen testen können.
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
