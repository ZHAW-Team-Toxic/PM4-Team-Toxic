package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.components.OccupiesTilesComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.utils.LayeredSprite;
import com.zhaw.frontier.utils.TileOffset;
import java.util.List;

/**
 * Factory class for creating wall entities.
 * <p>
 * This class is responsible for generating default wall entities.
 * A default wall entity is composed of a {@link PositionComponent},
 * a {@link HealthComponent}, and a {@link RenderComponent}. The render component is set
 * to render the wall as a building using a placeholder texture that should be replaced
 * with the actual wall texture in the future.
 * </p>
 */
public class WallFactory {

    /**
     * Creates a default wall entity with the necessary components.
     * <p>
     * The wall entity is initialized with a {@link PositionComponent} for spatial positioning,
     * a {@link HealthComponent} for health management, and a {@link RenderComponent} for rendering.
     * The {@code RenderComponent} uses a placeholder texture (created by {@link #createPlaceHolder()})
     * with its render type set to {@link RenderComponent.RenderType#BUILDING}.
     * </p>
     *
     * @param engine the {@link Engine} used to create and manage the entity.
     * @return the newly created wall entity.
     */
    public static Entity createDefaultWall(Engine engine) {
        Entity wall = engine.createEntity();
        PositionComponent positionComponent = new PositionComponent();
        positionComponent.heightInTiles = 1;
        positionComponent.widthInTiles = 1;
        wall.add(positionComponent);
        wall.add(new OccupiesTilesComponent());
        wall.add(new HealthComponent());

        RenderComponent renderComponent = new RenderComponent();
        renderComponent.renderType = RenderComponent.RenderType.BUILDING;

        // TODO: Replace placeholder texture with the actual wall texture.
        Texture texture = createPlaceHolder();
        renderComponent.sprites.put(
            new TileOffset(0, 0),
            (List<LayeredSprite>) new Sprite(texture)
        );
        renderComponent.heightInTiles = 1;
        renderComponent.widthInTiles = 1;

        wall.add(renderComponent);
        return wall;
    }

    /**
     * Creates a placeholder texture for the wall.
     * <p>
     * This method generates a 12x12 texture using a {@link Pixmap} in {@link Pixmap.Format#RGBA8888} format.
     * The pixmap is filled with a red color and then converted into a {@link Texture}.
     * The generated texture serves as a temporary placeholder until an actual wall texture is provided.
     * </p>
     *
     * @return a {@link Texture} generated from the pixmap.
     */
    private static Texture createPlaceHolder() {
        // Create a Pixmap with dimensions 12x12 using RGBA8888 format.
        Pixmap pixmap = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
        // Set the drawing color to red.
        pixmap.setColor(64, 0, 0, 1);
        // Fill the entire pixmap with the red color.
        pixmap.fill();
        // Create a texture from the pixmap.
        Texture texture = new Texture(pixmap);
        // Dispose of the pixmap since it's no longer needed.
        pixmap.dispose();
        return texture;
    }
}
