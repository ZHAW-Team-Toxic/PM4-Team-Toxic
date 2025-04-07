package com.zhaw.frontier;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.EnemyAnimationComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.systems.AnimationSystem;
import com.zhaw.frontier.systems.MovementSystem;
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
        entity.add(position);

        RenderComponent render = new RenderComponent();
        render.renderType = RenderComponent.RenderType.ENEMY;
        render.widthInTiles = 1;
        render.heightInTiles = 1;
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

        VelocityComponent vel = new VelocityComponent();
        vel.velocity.set(new Vector2(0, 1)); // Bewegung nach oben

        entity.add(anim);
        entity.add(vel);

        testEngine.addEntity(entity);

        // System verarbeiten
        testEngine.update(2f);

        // Erwartung: WALK_UP wurde gesetzt
        assertEquals(EnemyAnimationComponent.EnemyAnimationType.WALK_UP, anim.currentAnimation);
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
        // Optionale Aufräumarbeiten
    }
}
