package com.zhaw.frontier.entityFactories;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.GdxExtension;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.behaviours.IdleBehaviourComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxExtension.class)
public class EnemyFactoryTest {

    private AssetManager assetManager;

    @BeforeEach
    void setUp() {
        assetManager = new AssetManager();
        assetManager.load("packed/textures.atlas", TextureAtlas.class);
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

        Entity enemy = createMockEnemy(x, y);

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
}
