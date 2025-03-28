package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.zhaw.frontier.components.EnemyComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.components.behaviours.IdleBehaviourComponent;
import com.zhaw.frontier.components.behaviours.PatrolBehaviourComponent;

/**
 * Factory class responsible for creating enemy entities with their
 * initial components and visual representation.
 */
public class EnemyFactory {

    /**
     * Creates and returns a new enemy entity of the specified type and position.
     * <p>
     * The entity will be initialized with position, velocity, render, and enemy
     * components.
     * A placeholder sprite is used for rendering and should be replaced with the
     * actual texture.
     * </p>
     *
     * @param x            the initial x-position of the enemy
     * @param y            the initial y-position of the enemy
     * @param assetManager the {@link AssetManager} used to retrieve textures
     * @return a fully constructed enemy {@link Entity}
     */
    public static Entity createPatrolEnemy(float x, float y, AssetManager assetManager) {
        Entity enemy = createBaseEnemy(x, y, assetManager);
        enemy.add(new PatrolBehaviourComponent(30f));
        return enemy;
    }

    /**
     * Creates and returns a new idle enemy entity at the specified position.
     * <p>
     * This enemy does not move and is initialized with idle behavior.
     * A placeholder sprite is used for rendering and should be replaced with the
     * actual texture.
     * </p>
     *
     * @param x            the initial x-position of the enemy
     * @param y            the initial y-position of the enemy
     * @param assetManager the {@link AssetManager} used to retrieve textures
     * @return a fully constructed idle enemy {@link Entity}
     */
    public static Entity createIdleEnemy(float x, float y, AssetManager assetManager) {
        Entity enemy = createBaseEnemy(x, y, assetManager);
        enemy.add(new IdleBehaviourComponent());
        return enemy;
    }

    private static Entity createBaseEnemy(float x, float y, AssetManager assetManager) {
        Entity enemy = new Entity();

        PositionComponent position = new PositionComponent();
        position.position.set(x, y);

        VelocityComponent velocity = new VelocityComponent();

        RenderComponent renderComponent = new RenderComponent();
        renderComponent.renderType = RenderComponent.RenderType.ENEMY;

        // TODO: Replace placeholder texture with the actual resource building texture.
        TextureAtlas atlas = assetManager.get("packed/textures.atlas", TextureAtlas.class);
        Sprite texture = atlas.createSprite("demo/donkey");
        texture.setSize(16, 16);
        renderComponent.sprite = new Sprite(texture);

        enemy.add(renderComponent);
        enemy.add(position);
        enemy.add(velocity);
        enemy.add(new EnemyComponent());

        return enemy;
    }
}
