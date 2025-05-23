package com.zhaw.frontier.entityFactories;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.GdxExtension;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.behaviours.IdleBehaviourComponent;
import com.zhaw.frontier.components.behaviours.PatrolBehaviourComponent;
import com.zhaw.frontier.configs.AppProperties;
import com.zhaw.frontier.enums.EnemyType;
import com.zhaw.frontier.utils.AssetManagerInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxExtension.class)
public class EnemyFactoryTest {

    private AssetManager assetManager;

    @BeforeEach
    void setUp() {
        assetManager = AssetManagerInstance.getManager();
        assetManager.load(AppProperties.TEXTURE_ATLAS_PATH, TextureAtlas.class);
        assetManager.finishLoading();
    }

    public static Entity createMockEnemy(float x, float y) {
        Entity entity = new Entity();

        PositionComponent position = new PositionComponent();
        position.basePosition.set(x, y);
        position.heightInTiles = 1;
        position.widthInTiles = 1;

        VelocityComponent velocity = new VelocityComponent();
        velocity.velocity.set(new Vector2(0, 0));

        RenderComponent render = new RenderComponent(); // Empty sprites
        EnemyComponent enemyComponent = new EnemyComponent();
        IdleBehaviourComponent idleBehaviour = new IdleBehaviourComponent();

        EnemyAnimationComponent animationComponent = new EnemyAnimationComponent();

        entity.add(position);
        entity.add(velocity);
        entity.add(render);
        entity.add(enemyComponent);
        entity.add(idleBehaviour);
        entity.add(animationComponent);

        return entity;
    }

    @Test
    void testCreateEnemy() {
        float x = 10f;
        float y = 20f;

        Entity enemy = EnemyFactory.createIdleEnemy(EnemyType.ORC, x, y);

        // Verify PositionComponent
        PositionComponent position = enemy.getComponent(PositionComponent.class);
        assertNotNull(position, "PositionComponent should not be null");
        assertEquals(x, position.basePosition.x, "X position should match");
        assertEquals(y, position.basePosition.y, "Y position should match");

        // Verify VelocityComponent
        VelocityComponent velocity = enemy.getComponent(VelocityComponent.class);
        assertNotNull(velocity, "VelocityComponent should not be null");

        // Verify RenderComponent
        RenderComponent render = enemy.getComponent(RenderComponent.class);
        assertNotNull(render, "RenderComponent should not be null");
        //assertTrue(render.sprites.isEmpty(), "Sprite in RenderComponent should not be empty");

        // Verify EnemyComponent
        EnemyComponent enemyComponent = enemy.getComponent(EnemyComponent.class);
        assertNotNull(enemyComponent, "EnemyComponent should not be null");

        // Verify IdleBehaviourComponent
        IdleBehaviourComponent idle = enemy.getComponent(IdleBehaviourComponent.class);
        assertNotNull(idle, "IdleBehaviourComponent should not be null");
    }

    @Test
    void testEnemyValuesAreAppliedCorrectly() {
        for (EnemyType type : EnemyType.values()) {
            // Test patrol enemy
            Entity patrolEnemy = EnemyFactory.createEnemy(type, 10f, 20f);
            assertNotNull(patrolEnemy, type + " patrol enemy is null");

            HealthComponent health = patrolEnemy.getComponent(HealthComponent.class);
            assertNotNull(health, type + " is missing HealthComponent");
            assertEquals(type.getHealth(), health.maxHealth, type + " maxHealth mismatch");
            assertEquals(type.getHealth(), health.currentHealth, type + " currentHealth mismatch");

            PatrolBehaviourComponent patrol = patrolEnemy.getComponent(
                PatrolBehaviourComponent.class
            );
            assertNotNull(patrol, type + " is missing PatrolBehaviourComponent");
            assertEquals(type.getSpeed(), patrol.speed, 0.01f, type + " speed mismatch");

            assertHasAllCoreComponents(patrolEnemy, type);

            // Test idle enemy
            Entity idleEnemy = EnemyFactory.createIdleEnemy(type, 5f, 15f);
            assertNotNull(idleEnemy, type + " idle enemy is null");

            IdleBehaviourComponent idle = idleEnemy.getComponent(IdleBehaviourComponent.class);
            assertNotNull(idle, type + " is missing IdleBehaviourComponent");

            assertHasAllCoreComponents(idleEnemy, type);
        }
    }

    @Test
    void testDifferentEnemyTypesHaveDifferentHealth() {
        Entity orc = EnemyFactory.createEnemy(EnemyType.ORC, 0f, 0f);
        Entity demon = EnemyFactory.createEnemy(EnemyType.DEMON, 0f, 0f);

        int orcHealth = orc.getComponent(HealthComponent.class).maxHealth;
        int demonHealth = demon.getComponent(HealthComponent.class).maxHealth;

        assertNotEquals(
            orcHealth,
            demonHealth,
            "Orc and Demon should have different health values"
        );
    }

    @Test
    void testAllAnimationTypesArePresent() {
        Entity entity = EnemyFactory.createEnemy(EnemyType.ORC, 0f, 0f);
        EnemyAnimationComponent animation = entity.getComponent(EnemyAnimationComponent.class);

        for (EnemyAnimationComponent.EnemyAnimationType type : EnemyAnimationComponent.EnemyAnimationType.values()) {
            assertTrue(
                animation.animations.containsKey(type),
                "Missing animation type: " + type.name()
            );
        }
    }

    @Test
    void testInitialSpriteSetInRenderComponent() {
        Entity entity = EnemyFactory.createIdleEnemy(EnemyType.ORC, 0f, 0f);
        RenderComponent render = entity.getComponent(RenderComponent.class);
        assertNotNull(render);
        assertFalse(render.sprites.isEmpty(), "RenderComponent should have initial sprite set");
    }

    private void assertHasAllCoreComponents(Entity enemy, EnemyType type) {
        assertNotNull(
            enemy.getComponent(PositionComponent.class),
            type + " missing PositionComponent"
        );
        assertNotNull(
            enemy.getComponent(VelocityComponent.class),
            type + " missing VelocityComponent"
        );
        assertNotNull(enemy.getComponent(RenderComponent.class), type + " missing RenderComponent");
        assertNotNull(
            enemy.getComponent(EnemyAnimationComponent.class),
            type + " missing EnemyAnimationComponent"
        );
        assertNotNull(enemy.getComponent(EnemyComponent.class), type + " missing EnemyComponent");
        assertNotNull(
            enemy.getComponent(AnimationQueueComponent.class),
            type + " missing AnimationQueueComponent"
        );

        EnemyAnimationComponent anim = enemy.getComponent(EnemyAnimationComponent.class);
        assertNotNull(
            anim.animations.get(EnemyAnimationComponent.EnemyAnimationType.IDLE_DOWN),
            type + " missing IDLE_DOWN animation"
        );
    }
}
