package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import com.zhaw.frontier.utils.LayeredSprite;
import com.zhaw.frontier.utils.TileOffset;
import java.util.*;

/**
 * Factory class for creating resource building entities with basic setup.
 * <p>
 * Uses placeholder textures which can easily be swapped with actual sprites or atlas-based animations.
 */
public class ResourceBuildingFactory {

    private static final int TILE_SIZE = 16;
    private static final Map<
        Enum<?>,
        HashMap<TileOffset, Animation<TextureRegion>>
    > woodResourceBuildingAnimationCache = new HashMap<>();
    private static final Map<
        Enum<?>,
        HashMap<TileOffset, Animation<TextureRegion>>
    > stoneResourceBuildingAnimationCache = new HashMap<>();
    private static final Map<
        Enum<?>,
        HashMap<TileOffset, Animation<TextureRegion>>
    > ironResourceBuildingAnimationCache = new HashMap<>();

    /**
     * Creates a wood-producing building.
     *
     * @param engine the Ashley engine instance.
     * @return the newly created building entity.
     */
    public static Entity createWoodBuilding(Engine engine, AssetManager assetManager) {
        return createBaseBuilding(engine, ResourceTypeEnum.RESOURCE_TYPE_WOOD, Color.FOREST);
    }

    /**
     * Creates a stone-producing building.
     *
     * @param engine the Ashley engine instance.
     * @return the newly created building entity.
     */
    public static Entity createStoneBuilding(Engine engine, AssetManager assetManager) {
        return createBaseBuilding(engine, ResourceTypeEnum.RESOURCE_TYPE_STONE, Color.GRAY);
    }

    /**
     * Creates an iron-producing building.
     *
     * @param engine the Ashley engine instance.
     * @return the newly created building entity.
     */
    public static Entity createIronBuilding(Engine engine, AssetManager assetManager) {
        return createBaseBuilding(engine, ResourceTypeEnum.RESOURCE_TYPE_IRON, Color.SCARLET);
    }

    /**
     * Shared base logic for creating a 1x1 resource building.
     *
     * @param engine         the Ashley engine instance.
     * @param resourceType   the type of resource it should produce.
     * @param placeholderColor the color of the placeholder region.
     * @return the fully assembled entity.
     */
    private static Entity createBaseBuilding(
        Engine engine,
        ResourceTypeEnum resourceType,
        Color placeholderColor
    ) {
        Entity entity = engine.createEntity();

        // Position & dimensions
        PositionComponent position = new PositionComponent();
        position.widthInTiles = 1;
        position.heightInTiles = 1;

        // Occupation and rendering
        RenderComponent render = new RenderComponent();
        render.renderType = RenderComponent.RenderType.BUILDING;
        render.widthInTiles = 1;
        render.heightInTiles = 1;

        TextureRegion region = createPlaceholderRegion(placeholderColor, TILE_SIZE);
        LayeredSprite sprite = new LayeredSprite(region, 0);
        List<LayeredSprite> layers = new ArrayList<>(List.of(sprite));
        render.sprites.put(new TileOffset(0, 0), layers);

        // Resource production logic
        ResourceProductionComponent resourceProduction = new ResourceProductionComponent();
        resourceProduction.productionRate.put(resourceType, 1);

        // Final assembly
        entity.add(position);
        entity.add(new OccupiesTilesComponent());
        entity.add(new HealthComponent());
        entity.add(new ResourceGeneratorComponent());
        entity.add(resourceProduction);
        entity.add(render);
        entity.add(new BuildingAnimationComponent());

        return entity;
    }

    /**
     * Creates a square placeholder texture as a {@link TextureRegion}.
     *
     * @param color the color of the placeholder.
     * @param size  the width/height in pixels.
     * @return the created {@link TextureRegion}.
     */
    public static TextureRegion createPlaceholderRegion(Color color, int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegion(texture);
    }
}
