package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.zhaw.frontier.components.AttackComponent;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;

/**
 * A factory class responsible for creating Tower entities.
 * <p>
 * This factory provides a method to create a default Tower entity which is initialized with
 * the necessary components:
 * <ul>
 *   <li>{@link PositionComponent} for position data,</li>
 *   <li>{@link HealthComponent} for health data,</li>
 *   <li>{@link AttackComponent} for attack capabilities, and</li>
 *   <li>{@link RenderComponent} for rendering. The {@code renderType} is set to
 *       {@link RenderComponent.RenderType#BUILDING} and a placeholder texture is used.</li>
 * </ul>
 * </p>
 */
public class TowerFactory {

    /**
     * Creates a default Tower entity with the required components.
     * <p>
     * The tower entity is initialized with a {@link PositionComponent}, {@link HealthComponent},
     * {@link AttackComponent}, and a {@link RenderComponent}. The {@code RenderComponent} is set up
     * with a placeholder texture. This texture should be replaced with the actual tower texture in the future.
     * </p>
     *
     * @param engine the {@link Engine} used to create and manage the entity.
     * @return the newly created Tower entity.
     */
    public static Entity createDefaultTower(Engine engine) {
        Entity tower = engine.createEntity();
        tower.add(new PositionComponent());
        tower.add(new HealthComponent());
        tower.add(new AttackComponent());

        RenderComponent renderComponent = new RenderComponent();
        renderComponent.renderType = RenderComponent.RenderType.BUILDING;

        // TODO: Replace placeholder texture with the actual tower texture.
        Texture texture = createPlaceHolder();
        renderComponent.sprite = new Sprite(texture);

        tower.add(renderComponent);
        return tower;
    }

    private static Texture createPlaceHolder() {
        // Create a Pixmap with dimensions 12x12 using RGBA8888 format.
        Pixmap pixmap = new Pixmap(12, 12, Format.RGBA8888);
        // Set the drawing color to black.
        pixmap.setColor(0, 0, 0, 1);
        // Fill the entire pixmap with the black color.
        pixmap.fill();
        // Create a texture from the pixmap.
        Texture texture = new Texture(pixmap);
        // Dispose of the pixmap since it's no longer needed.
        pixmap.dispose();
        return texture;
    }
}
