package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.mappers.MapLayerMapper;

/**
 * Handles the creation and rendering of health bars for entities with health.
 * This class draws colored bars above entities, representing their current health status.
 */
public class HealthBarManager {

    private static MapLayerMapper mapLayerMapper = new MapLayerMapper();

    /**
     * Initializes the health bar sprite as a 1x1 white pixel, which will be resized and tinted dynamically.
     *
     * @return a reusable {@link Sprite} representing the health bar.
     */
    private static Sprite createHealthBarSprite() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        Sprite bar = new Sprite(texture);
        bar.setSize(1, 1);
        bar.setOrigin(0, 0);
        bar.setScale(1, 1);
        bar.setColor(Color.WHITE);
        bar.setAlpha(1f);
        return bar;
    }

    /**
     * Draws the health bar for a single entity if it has health and render data.
     *
     * @param renderer the {@link SpriteBatch} used for drawing.
     * @param entity   the {@link Entity} to draw a health bar for.
     */
    public static void drawHealthBar(SpriteBatch renderer, Entity entity, Engine engine) {
        renderHealthBar(renderer, entity, engine);
    }

    /**
     * Renders the health bar above the entity based on its current health.
     * If the entity is fully healed or dead, no bar is drawn.
     *
     * @param renderer the {@link SpriteBatch} for rendering.
     * @param entity   the entity to render a health bar for.
     */
    private static void renderHealthBar(SpriteBatch renderer, Entity entity, Engine engine) {
        HealthComponent health = entity.getComponent(HealthComponent.class);
        PositionComponent position = entity.getComponent(PositionComponent.class);
        RenderComponent render = entity.getComponent(RenderComponent.class);

        if (health == null || position == null || render == null) return;

        Sprite healthBar = createHealthBarSprite();

        float hpPercent = MathUtils.clamp((float) health.currentHealth / health.maxHealth, 0f, 1f);
        if (hpPercent >= 1f || hpPercent <= 0f) return;

        Color barColor = getHealthColor(hpPercent);
        healthBar.setColor(barColor);

        float scalarOffsetY = 1.3f;
        float barWidth = render.sprite.getWidth() * hpPercent;
        float barHeight = 3f;
        float offsetY = render.sprite.getHeight() * scalarOffsetY;

        Vector2 pixelCoordinate = calculatePixelCoordinate(
            (int) position.position.x,
            (int) position.position.y,
            engine
        );

        healthBar.setSize(barWidth, barHeight);
        healthBar.setPosition(pixelCoordinate.x, pixelCoordinate.y + offsetY);
        healthBar.draw(renderer);

        renderer.setColor(Color.WHITE); // Reset batch color
    }

    /**
     * Computes the color of the health bar based on the health percentage.
     * Green for high HP, orange in the middle, red for low HP.
     *
     * @param healthPercent a float from 0 (dead) to 1 (full health)
     * @return the {@link Color} to use for the bar
     */
    private static Color getHealthColor(float healthPercent) {
        Color healthColor = new Color();

        if (healthPercent >= 0.7f) {
            // 70%–99%: Green
            healthColor.set(0f, 1f, 0f, 1f);
        } else if (healthPercent >= 0.5f) {
            // 50%–69%: Green → Orange
            float t = (healthPercent - 0.5f) / 0.2f;
            float r = MathUtils.lerp(1f, 1f, t);
            float g = MathUtils.lerp(0.5f, 0.4f, t);
            healthColor.set(r, g, 0f, 1f);
        } else if (healthPercent >= 0.2f) {
            // 20%–49%: Orange → Red
            float t = (healthPercent - 0.2f) / 0.3f;
            float g = MathUtils.lerp(0f, 0.5f, t);
            healthColor.set(1f, g, 0f, 1f);
        } else {
            // 1%–19%: Red
            healthColor.set(1f, 0f, 0f, 1f);
        }

        return healthColor;
    }

    /**
     * Converts tile coordinates to pixel coordinates using the tile size of the map.
     *
     * @param x the tile x-coordinate.
     * @param y the tile y-coordinate.
     * @return a {@link Vector2} representing the pixel position on screen.
     */
    private static Vector2 calculatePixelCoordinate(int x, int y, Engine engine) {
        Entity map = engine.getEntitiesFor(mapLayerMapper.mapLayerFamily).first();
        int tileX = x * mapLayerMapper.bottomLayerMapper.get(map).bottomLayer.getTileWidth();
        int tileY = y * mapLayerMapper.bottomLayerMapper.get(map).bottomLayer.getTileHeight();
        return new Vector2(tileX, tileY);
    }
}
