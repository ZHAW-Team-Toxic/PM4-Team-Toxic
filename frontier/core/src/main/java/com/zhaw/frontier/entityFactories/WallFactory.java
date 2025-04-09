package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.components.OccupiesTilesComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.utils.LayeredSprite;
import com.zhaw.frontier.utils.TileOffset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final Map<Enum<?>, HashMap<TileOffset, Animation<TextureRegion>>> woodWallAnimationCache = new HashMap<>();
    private static final Map<Enum<?>, HashMap<TileOffset, Animation<TextureRegion>>> stoneWallAnimationCache = new HashMap<>();
    private static final Map<Enum<?>, HashMap<TileOffset, Animation<TextureRegion>>> ironWallAnimationCache = new HashMap<>();

    public Entity createWoodWall(Engine engine, AssetManager assetManager) {
        return createDefaultWall(engine);
    }

    public Entity createStoneWall(Engine engine, AssetManager assetManager) {
        return createDefaultWall(engine);
    }

    public Entity createIronWall(Engine engine, AssetManager assetManager) {
        return createDefaultWall(engine);
    }

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
    public static Entity createDefaultWall(Engine engine, AssetManager assetManager) {
        Entity wall = engine.createEntity();

        PositionComponent position = new PositionComponent();
        position.heightInTiles = 1;
        position.widthInTiles = 1;

        wall.add(position);
        wall.add(new OccupiesTilesComponent());
        wall.add(new HealthComponent());

        RenderComponent render = new RenderComponent();
        render.renderType = RenderComponent.RenderType.BUILDING;
        render.heightInTiles = 1;
        render.widthInTiles = 1;

        TextureRegion region = new TextureRegion(createPlaceHolder());
        LayeredSprite sprite = new LayeredSprite(region, 0);

        render.sprites.put(
            new TileOffset(0, 0),
            new ArrayList<>(List.of(sprite))
        );

        wall.add(render);
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
        Pixmap pixmap = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
        pixmap.setColor(64 / 255f, 0, 0, 1); // red tone
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
