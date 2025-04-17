package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
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
    public static Entity createDefaultResourceBuilding(Engine engine, float x, float y) {
        Entity resourceBuilding = engine.createEntity();
        resourceBuilding.add(new PositionComponent(x, y, 1, 1));
        resourceBuilding.add(new HealthComponent());
        resourceBuilding.add(new ResourceGeneratorComponent());
        resourceBuilding.add(new ResourceProductionComponent());
        resourceBuilding.add(new RenderComponent());
        return resourceBuilding;
    }

    public static Entity woodResourceBuilding(Engine engine, float x, float y) {
        return createResourceBuildingWithType(engine, x, y, ResourceTypeEnum.RESOURCE_TYPE_WOOD);
    }

    public static Entity stoneResourceBuilding(Engine engine, float x, float y) {
        return createResourceBuildingWithType(engine, x, y, ResourceTypeEnum.RESOURCE_TYPE_STONE);
    }

    public static Entity ironResourceBuilding(Engine engine, float x, float y) {
        return createResourceBuildingWithType(engine, x, y, ResourceTypeEnum.RESOURCE_TYPE_IRON);
    }

    private static Entity createResourceBuildingWithType(
        Engine engine,
        float x,
        float y,
        ResourceTypeEnum resourceType
    ) {
        Entity resourceBuilding = engine.createEntity();

        ResourceProductionComponent resourceProductionComponent = new ResourceProductionComponent();
        resourceProductionComponent.productionRate.put(resourceType, 1);

        RenderComponent render = new RenderComponent(RenderComponent.RenderType.BUILDING, 10, 1, 1);
        //TODO: Replace with asset
        TextureRegion region = createPlaceholderRegion(Color.SCARLET, TILE_SIZE);
        render.sprites.put(new TileOffset(0, 0), new TextureRegion(region));

        resourceBuilding.add(new PositionComponent(x, y, 1, 1));
        resourceBuilding.add(resourceProductionComponent);
        resourceBuilding.add(render);
        resourceBuilding.add(new OccupiesTilesComponent());
        resourceBuilding.add(new HealthComponent());
        resourceBuilding.add(new ResourceGeneratorComponent());
        resourceBuilding.add(new BuildingAnimationComponent());
        resourceBuilding.add(new EntityTypeComponent(EntityTypeComponent.EntityType.RESOURCE_BUILDING));
        return resourceBuilding;
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
