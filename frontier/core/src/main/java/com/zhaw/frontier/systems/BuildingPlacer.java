package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.map.TiledPropertiesEnum;
import com.zhaw.frontier.mappers.MapLayerMapper;
import com.zhaw.frontier.utils.WorldCoordinateUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles validation and placement of buildings onto a tiled map based on
 * buildability, occupancy, and adjacency rules.
 *
 * <p>
 * The {@code BuildingPlacer} coordinates the following logic:
 * <ul>
 * <li>Converts screen coordinates to world/tile coordinates</li>
 * <li>Checks if tiles are buildable on both bottom and resource layers</li>
 * <li>Checks for existing building collisions</li>
 * <li>If the building is a resource producer, checks for adjacent
 * resources</li>
 * <li>Marks the tile area as occupied and adds the entity to the engine</li>
 * </ul>
 * </p>
 */
public class BuildingPlacer {

    private final Viewport viewport;
    private final Engine engine;
    private final MapLayerMapper mapLayerMapper = new MapLayerMapper();

    /**
     * Constructs a new {@code BuildingPlacer} with the specified viewport and
     * engine.
     *
     * @param viewport the {@link Viewport} used for coordinate conversion.
     * @param engine   the {@link Engine} used to manage entities.
     */
    public BuildingPlacer(Viewport viewport, Engine engine) {
        this.viewport = viewport;
        this.engine = engine;
    }

    /**
     * Attempts to place a building entity on the map based on its current position.
     *
     * <p>
     * This method performs multiple checks to ensure valid placement:
     * </p>
     * <ul>
     * <li>Convert screen to world coordinates using {@link Viewport}</li>
     * <li>Check for buildable tiles on both bottom and resource layers</li>
     * <li>Check for collisions with existing entities</li>
     * <li>If applicable, validate adjacency to matching resource tiles</li>
     * </ul>
     *
     * @param entityType  the building entity to attempt placement for
     * @param sampleLayer the layer used to calculate tile positions and sizes
     * @return true if the building was successfully placed, false otherwise
     */
    boolean placeBuilding(
        Entity entityType,
        TiledMapTileLayer sampleLayer,
        InventoryComponent inventory
    ) {
        if (!hasResources(entityType, inventory)) {
            Gdx.app.debug(
                "BuildingPlacer",
                "Player does not have the resources to build this building \n" +
                inventory.toString()
            );
            return false;
        }

        PositionComponent positionComponent = entityType.getComponent(PositionComponent.class);
        Vector2 worldCoordinate = WorldCoordinateUtils.centerClickWithBuilding(
            viewport,
            sampleLayer,
            positionComponent.basePosition.x,
            positionComponent.basePosition.y,
            entityType
        );
        Gdx.app.debug(
            "BuildingPlacer",
            "World coordinates for building placement: " +
            worldCoordinate.x +
            " x " +
            worldCoordinate.y +
            " y"
        );
        int worldCoordinateX = (int) worldCoordinate.x;
        int worldCoordinateY = (int) worldCoordinate.y;

        positionComponent.basePosition.x = worldCoordinateX;
        positionComponent.basePosition.y = worldCoordinateY;
        Gdx.app.debug(
            "BuildingPlacer",
            "Checking if tile is buildable on coordinates: " +
            worldCoordinateX +
            " x " +
            worldCoordinateY +
            " y"
        );

        if (!checkIfTileIsBuildableOnBottomLayer(engine, entityType)) {
            Gdx.app.debug("BuildingPlacer", "Tile is not buildable on bottom layer.");
            return false;
        }

        if (!checkIfTileIsBuildableOnResourceLayer(engine, entityType)) {
            Gdx.app.debug("BuildingPlacer", "Tile is not buildable on resource layer.");
            return false;
        }

        if (checkIfPlaceIsOccupiedByBuilding(engine, entityType)) {
            Gdx.app.debug(
                "BuildingPlacer",
                "Tile is occupied by another building at coordinates: " +
                worldCoordinateY +
                " x " +
                worldCoordinateY +
                " y"
            );
            return false;
        }

        if (checkIfBuildingIsResourceBuilding(entityType)) {
            Gdx.app.debug(
                "BuildingPlacer",
                "Building is a resource building. Checking for adjacent resources."
            );
            TiledMapTileLayer resourceLayer = mapLayerMapper.resourceLayerMapper.get(
                engine.getEntitiesFor(mapLayerMapper.mapLayerFamily).first()
            )
                .resourceLayer;
            if (!checkIfResourceBuildingIsPlaceable(entityType, resourceLayer)) {
                Gdx.app.debug(
                    "BuildingPlacer",
                    "Tile is buildable on resource layer but has no adjacent resource."
                );
                return false;
            }
        }

        occupyTile(entityType);

        Gdx.app.debug(
            "BuildingPlacer",
            "Tile is buildable on resource layer and has adjacent resource."
        );

        engine.addEntity(entityType);

        if (checkIfBuildingIsWallBuilding(entityType)) {
            Gdx.app.debug(
                "[DEBUG] - BuildingPlacer",
                "Building is a wall building. No adjacency check needed."
            );
            WallManager.update(engine);
        }

        removeResources(entityType, inventory);
        return true;
    }

    private boolean checkIfBuildingIsWallBuilding(Entity entityType) {
        return entityType.getComponent(WallPieceComponent.class) != null;
    }

    private boolean hasResources(Entity entity, InventoryComponent inventory) {
        var costs = entity.getComponent(CostComponent.class);
        if (costs == null) return true;

        for (var resoucreCost : costs.resouceCosts.keySet()) {
            var has = inventory.resources.get(resoucreCost);
            var cost = costs.resouceCosts.get(resoucreCost);
            if (has == null || cost == null) {
                Gdx.app.error(
                    "BuildingPlacer",
                    "inventory or item cost is null.",
                    new NullPointerException()
                );
            }

            if (has < cost) return false;
        }

        return true;
    }

    private void removeResources(Entity entity, InventoryComponent inventory) {
        var costs = entity.getComponent(CostComponent.class);
        if (costs == null) return;

        for (var resoucreCost : costs.resouceCosts.keySet()) {
            var has = inventory.resources.get(resoucreCost);
            var cost = costs.resouceCosts.get(resoucreCost);
            if (has == null || cost == null) {
                Gdx.app.error(
                    "BuildingPlacer",
                    "inventory or item cost is null.",
                    new NullPointerException()
                );
                return;
            }

            inventory.resources.put(resoucreCost, has - cost);
        }
    }

    /**
     * Checks whether the tiles required by the building are already occupied by
     * another building.
     *
     * @param engine         the ECS engine
     * @param entityBuilding the building being placed
     * @return true if a tile is already occupied, false otherwise
     */
    private boolean checkIfPlaceIsOccupiedByBuilding(Engine engine, Entity entityBuilding) {
        ImmutableArray<Entity> entitiesWithPosition = engine.getEntitiesFor(
            Family.all(PositionComponent.class).exclude(EnemyComponent.class).get()
        );

        Vector2 targetTile = new Vector2(
            (int) entityBuilding.getComponent(PositionComponent.class).basePosition.x,
            (int) entityBuilding.getComponent(PositionComponent.class).basePosition.y
        );

        int width = entityBuilding.getComponent(PositionComponent.class).widthInTiles;
        int height = entityBuilding.getComponent(PositionComponent.class).heightInTiles;

        // Alle Tiles, die das neue Gebäude beanspruchen würde
        List<Vector2> tilesToCheck = new ArrayList<>();
        for (int dx = 0; dx < width; dx++) {
            for (int dy = 0; dy < height; dy++) {
                tilesToCheck.add(new Vector2(targetTile.x + dx, targetTile.y + dy));
            }
        }

        // Prüfe, ob eins dieser Tiles bereits von anderen Entities belegt ist
        for (Entity entity : entitiesWithPosition) {
            if (entity == entityBuilding) continue;

            PositionComponent pos = entity.getComponent(PositionComponent.class);
            for (int x = (int) pos.basePosition.x; x < pos.basePosition.x + pos.widthInTiles; x++) {
                for (
                    int y = (int) pos.basePosition.y;
                    y < pos.basePosition.y + pos.heightInTiles;
                    y++
                ) {
                    Vector2 occupiedTile = new Vector2(x, y);
                    if (tilesToCheck.contains(occupiedTile)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Validates whether the tiles the building wants to occupy are marked as
     * buildable
     * on the bottom layer.
     *
     * @param engine         the ECS engine
     * @param entityBuilding the building being placed
     * @return true if all tiles are buildable, false otherwise
     */
    private boolean checkIfTileIsBuildableOnBottomLayer(Engine engine, Entity entityBuilding) {
        PositionComponent position = entityBuilding.getComponent(PositionComponent.class);

        TiledMapTileLayer bottomLayer = mapLayerMapper.bottomLayerMapper.get(
            engine.getEntitiesFor(mapLayerMapper.mapLayerFamily).first()
        )
            .bottomLayer;

        for (
            int x = (int) position.basePosition.x;
            x < position.basePosition.x + position.widthInTiles;
            x++
        ) {
            for (
                int y = (int) position.basePosition.y;
                y < position.basePosition.y + position.heightInTiles;
                y++
            ) {
                TiledMapTileLayer.Cell cell = bottomLayer.getCell(x, y);
                if (cell == null) return false;

                Boolean buildable = cell
                    .getTile()
                    .getProperties()
                    .get(TiledPropertiesEnum.IS_BUILDABLE.toString(), Boolean.class);
                if (!Boolean.TRUE.equals(buildable)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Validates whether the tiles the building wants to occupy are buildable on the
     * resource layer, if applicable.
     *
     * <p>
     * If no cell is present on the resource layer, it's considered buildable.
     * If a cell exists and is marked as not buildable, the tile is rejected.
     * </p>
     *
     * @param engine         the ECS engine
     * @param entityBuilding the building being placed
     * @return true if all resource layer tiles are buildable or empty, false
     *         otherwise
     */
    private boolean checkIfTileIsBuildableOnResourceLayer(Engine engine, Entity entityBuilding) {
        PositionComponent position = entityBuilding.getComponent(PositionComponent.class);

        TiledMapTileLayer resourceLayer = mapLayerMapper.resourceLayerMapper.get(
            engine.getEntitiesFor(mapLayerMapper.mapLayerFamily).first()
        )
            .resourceLayer;

        for (
            int x = (int) position.basePosition.x;
            x < position.basePosition.x + position.widthInTiles;
            x++
        ) {
            for (
                int y = (int) position.basePosition.y;
                y < position.basePosition.y + position.heightInTiles;
                y++
            ) {
                TiledMapTileLayer.Cell cell = resourceLayer.getCell(x, y);
                if (cell != null) {
                    Boolean buildable = cell
                        .getTile()
                        .getProperties()
                        .get(TiledPropertiesEnum.IS_BUILDABLE.toString(), Boolean.class);
                    if (Boolean.FALSE.equals(buildable)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks whether the entity has a {@link ResourceProductionComponent},
     * identifying
     * it as a resource-generating building.
     *
     * @param entityType the building to check
     * @return true if it produces resources, false otherwise
     */
    private boolean checkIfBuildingIsResourceBuilding(Entity entityType) {
        return entityType.getComponent(ResourceProductionComponent.class) != null;
    }

    /**
     * Validates that a resource-producing building has at least one adjacent
     * matching
     * resource tile, using the {@link ResourceAdjacencyChecker}.
     *
     * @param entityType  the building to validate
     * @param sampleLayer the resource tile layer
     * @return true if valid placement is possible, false otherwise
     */
    private boolean checkIfResourceBuildingIsPlaceable(
        Entity entityType,
        TiledMapTileLayer sampleLayer
    ) {
        return ResourceAdjacencyChecker.hasAdjacentResource(entityType, sampleLayer);
    }

    /**
     * Registers the tiles a building occupies into its
     * {@link OccupiesTilesComponent}.
     *
     * <p>
     * Populates all covered tile coordinates based on its position and size.
     * </p>
     *
     * @param entity the building to register
     */
    private void occupyTile(Entity entity) {
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        OccupiesTilesComponent occupiesTilesComponent = entity.getComponent(
            OccupiesTilesComponent.class
        );
        int tileX = (int) positionComponent.basePosition.x;
        int tileY = (int) positionComponent.basePosition.y;

        int offsetX = positionComponent.widthInTiles;
        int offsetY = positionComponent.heightInTiles;

        for (int x = tileX; x < tileX + offsetX; x++) {
            for (int y = tileY; y < tileY + offsetY; y++) {
                occupiesTilesComponent.occupiedTiles.add(new Vector2(x, y));
            }
        }
    }
}
