package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import com.zhaw.frontier.enums.Team;
import com.zhaw.frontier.utils.AssetManagerInstance;
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
        HashMap<TileOffset, TextureRegion>
    > woodResourceBuildingCache = new HashMap<>();
    private static final Map<
        Enum<?>,
        HashMap<TileOffset, TextureRegion>
    > stoneResourceBuildingCache = new HashMap<>();
    private static final Map<
        Enum<?>,
        HashMap<TileOffset, TextureRegion>
    > ironResourceBuildingCache = new HashMap<>();

    public static Entity woodResourceBuilding(Engine engine, float x, float y) {
        initWoodResourceBuilding();
        Entity resourceBuilding = createResourceBuildingWithType(
            engine,
            x,
            y,
            ResourceTypeEnum.RESOURCE_TYPE_WOOD
        );
        RenderComponent renderComponent = new RenderComponent(
            RenderComponent.RenderType.BUILDING,
            10,
            TILE_SIZE,
            TILE_SIZE
        );
        renderComponent.sprites =
        new HashMap<>(woodResourceBuildingCache.get(ResourceTypeEnum.RESOURCE_TYPE_WOOD));
        renderComponent.heightInTiles = 3;
        renderComponent.widthInTiles = 3;

        resourceBuilding.add(renderComponent);
        return resourceBuilding;
    }

    public static Entity stoneResourceBuilding(Engine engine, float x, float y) {
        initStoneResourceBuilding();
        Entity resourceBuilding = createResourceBuildingWithType(
            engine,
            x,
            y,
            ResourceTypeEnum.RESOURCE_TYPE_STONE
        );
        RenderComponent renderComponent = new RenderComponent(
            RenderComponent.RenderType.BUILDING,
            10,
            TILE_SIZE,
            TILE_SIZE
        );
        renderComponent.sprites =
        new HashMap<>(stoneResourceBuildingCache.get(ResourceTypeEnum.RESOURCE_TYPE_STONE));
        renderComponent.heightInTiles = 3;
        renderComponent.widthInTiles = 3;
        resourceBuilding.add(renderComponent);
        return resourceBuilding;
    }

    public static Entity ironResourceBuilding(Engine engine, float x, float y) {
        initIronResourceBuilding();
        Entity resourceBuilding = createResourceBuildingWithType(
            engine,
            x,
            y,
            ResourceTypeEnum.RESOURCE_TYPE_IRON
        );
        RenderComponent renderComponent = new RenderComponent(
            RenderComponent.RenderType.BUILDING,
            10,
            TILE_SIZE,
            TILE_SIZE
        );
        renderComponent.sprites =
        new HashMap<>(ironResourceBuildingCache.get(ResourceTypeEnum.RESOURCE_TYPE_IRON));
        renderComponent.heightInTiles = 3;
        renderComponent.widthInTiles = 3;
        resourceBuilding.add(renderComponent);
        return resourceBuilding;
    }

    private static Entity createResourceBuildingWithType(
        Engine engine,
        float x,
        float y,
        ResourceTypeEnum resourceType
    ) {
        Entity resourceBuilding = engine.createEntity();
        resourceBuilding.add(new HealthComponent());
        resourceBuilding.add(new ResourceGeneratorComponent());

        ResourceProductionComponent resourceProductionComponent = new ResourceProductionComponent();
        resourceProductionComponent.productionRate.put(resourceType, 1);

        resourceBuilding.add(new PositionComponent(x, y, 3, 3));
        resourceBuilding.add(resourceProductionComponent);
        resourceBuilding.add(new OccupiesTilesComponent());
        resourceBuilding.add(new HealthComponent());
        resourceBuilding.add(new ResourceGeneratorComponent());
        resourceBuilding.add(new BuildingAnimationComponent());
        resourceBuilding.add(
            new EntityTypeComponent(EntityTypeComponent.EntityType.RESOURCE_BUILDING)
        );
        resourceBuilding.add(new TeamComponent(Team.PLAYER));
        return resourceBuilding;
    }

    private static void initWoodResourceBuilding() {
        if (!woodResourceBuildingCache.isEmpty()) return;

        TextureAtlas atlas = AssetManagerInstance
            .getManager()
            .get("packed/textures.atlas", TextureAtlas.class);
        HashMap<TileOffset, TextureRegion> map = new HashMap<>();

        map.put(new TileOffset(0, 0), atlas.findRegion("wood_00_S"));
        map.put(new TileOffset(1, 0), atlas.findRegion("wood_10_S"));
        map.put(new TileOffset(2, 0), atlas.findRegion("wood_20_S"));
        map.put(new TileOffset(0, 1), atlas.findRegion("wood_01_S"));
        map.put(new TileOffset(1, 1), atlas.findRegion("wood_11_S"));
        map.put(new TileOffset(2, 1), atlas.findRegion("wood_21_S"));
        map.put(new TileOffset(0, 2), atlas.findRegion("wood_02_S"));
        map.put(new TileOffset(1, 2), atlas.findRegion("wood_12_S"));
        map.put(new TileOffset(2, 2), atlas.findRegion("wood_22_S"));

        woodResourceBuildingCache.put(ResourceTypeEnum.RESOURCE_TYPE_WOOD, map);
    }

    private static void initStoneResourceBuilding() {
        if (!stoneResourceBuildingCache.isEmpty()) return;

        TextureAtlas atlas = AssetManagerInstance
            .getManager()
            .get("packed/textures.atlas", TextureAtlas.class);
        HashMap<TileOffset, TextureRegion> map = new HashMap<>();

        map.put(new TileOffset(0, 0), atlas.findRegion("stone_00_S"));
        map.put(new TileOffset(1, 0), atlas.findRegion("stone_10_S"));
        map.put(new TileOffset(2, 0), atlas.findRegion("stone_20_S"));
        map.put(new TileOffset(0, 1), atlas.findRegion("stone_01_S"));
        map.put(new TileOffset(1, 1), atlas.findRegion("stone_11_S"));
        map.put(new TileOffset(2, 1), atlas.findRegion("stone_21_S"));
        map.put(new TileOffset(0, 2), atlas.findRegion("stone_02_S"));
        map.put(new TileOffset(1, 2), atlas.findRegion("stone_12_S"));
        map.put(new TileOffset(2, 2), atlas.findRegion("stone_22_S"));

        stoneResourceBuildingCache.put(ResourceTypeEnum.RESOURCE_TYPE_STONE, map);
    }

    private static void initIronResourceBuilding() {
        if (!ironResourceBuildingCache.isEmpty()) return;

        TextureAtlas atlas = AssetManagerInstance
            .getManager()
            .get("packed/textures.atlas", TextureAtlas.class);
        HashMap<TileOffset, TextureRegion> map = new HashMap<>();

        map.put(new TileOffset(0, 0), atlas.findRegion("iron_00_S"));
        map.put(new TileOffset(1, 0), atlas.findRegion("iron_10_S"));
        map.put(new TileOffset(2, 0), atlas.findRegion("iron_20_S"));
        map.put(new TileOffset(0, 1), atlas.findRegion("iron_01_S"));
        map.put(new TileOffset(1, 1), atlas.findRegion("iron_11_S"));
        map.put(new TileOffset(2, 1), atlas.findRegion("iron_21_S"));
        map.put(new TileOffset(0, 2), atlas.findRegion("iron_02_S"));
        map.put(new TileOffset(1, 2), atlas.findRegion("iron_12_S"));
        map.put(new TileOffset(2, 2), atlas.findRegion("iron_22_S"));

        ironResourceBuildingCache.put(ResourceTypeEnum.RESOURCE_TYPE_IRON, map);
    }
}
