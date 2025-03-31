package com.zhaw.frontier.entityFactories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.zhaw.frontier.GdxExtension;
import com.zhaw.frontier.components.EnemyComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.VelocityComponent;
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

    @Test
    void testCreateEnemy() {
        float x = 10f;
        float y = 20f;

        Entity enemy = EnemyFactory.createIdleEnemy(x, y, assetManager);

        // Verify PositionComponent
        PositionComponent position = enemy.getComponent(PositionComponent.class);
        assertNotNull(position, "PositionComponent should not be null");
        assertEquals(x, position.currentPosition.x, "X position should match");
        assertEquals(y, position.currentPosition.y, "Y position should match");

        // Verify VelocityComponent
        VelocityComponent velocity = enemy.getComponent(VelocityComponent.class);
        assertNotNull(velocity, "VelocityComponent should not be null");

        // Verify RenderComponent
        RenderComponent render = enemy.getComponent(RenderComponent.class);
        assertNotNull(render, "RenderComponent should not be null");
        assertNotNull(render.sprite, "Sprite in RenderComponent should not be null");

        // Verify EnemyComponent
        EnemyComponent enemyComponent = enemy.getComponent(EnemyComponent.class);
        assertNotNull(enemyComponent, "EnemyComponent should not be null");

        // Verify IdleBehaviourComponent
        IdleBehaviourComponent idle = enemy.getComponent(IdleBehaviourComponent.class);
        assertNotNull(idle, "IdleBehaviourComponent should not be null");
    }
}
