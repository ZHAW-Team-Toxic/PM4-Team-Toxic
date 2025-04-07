package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.EnemyComponent;
import com.zhaw.frontier.components.OccupiesTilesComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.ResourceProductionComponent;
import com.zhaw.frontier.components.map.TiledPropertiesEnum;
import com.zhaw.frontier.mappers.MapLayerMapper;
import com.zhaw.frontier.utils.WorldCoordinateUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for validating and placing building entities on a tiled map.
 *
 * <p>
 * The {@code BuildingPlacer} is responsible for checking whether a building can be placed on a given tile.
 * It verifies that the tile is buildable on both the bottom and resource layer, that it is not already
 * occupied by another building, and—if the entity is a resource-producing building—that it has adjacent
 * resource tiles of the correct type.
 * </p>
 *
 * <p>
 * The coordinates of the building are automatically converted from screen to world coordinates
 * using the provided {@link Viewport}.
 * </p>
 *
 * <p>
 * If all placement conditions are met, the building entity is added to the {@link Engine}.
 * </p>
 *
 * @see com.zhaw.frontier.components.ResourceProductionComponent
 * @see com.zhaw.frontier.components.PositionComponent
 * @see ResourceAdjacencyChecker
 */
public class BuildingPlacer {

    private final Viewport viewport;
    private final Engine engine;
    private final MapLayerMapper mapLayerMapper = new MapLayerMapper();

    /**
     * Constructs a new {@code BuildingPlacer} with the specified viewport and engine.
     *
     * @param viewport the {@link Viewport} used for coordinate conversion.
     * @param engine   the {@link Engine} used to manage entities.
     */
    public BuildingPlacer(Viewport viewport, Engine engine) {
        this.viewport = viewport;
        this.engine = engine;
    }

    /**
     * Attempts to place a building entity on the map based on its current position and map layers.
     *
     * <p>
     * This method performs the following validation steps in order:
     * <ol>
     *     <li>Converts the entity's screen position to world coordinates</li>
     *     <li>Checks if the tile is buildable on the bottom layer</li>
     *     <li>Checks if the tile is buildable on the resource layer</li>
     *     <li>Checks if the tile is already occupied by another building</li>
     *     <li>If the building is a resource producer, checks for adjacent resources</li>
     * </ol>
     * </p>
     *
     * @param entityType  the building entity to be placed
     * @param sampleLayer the tile layer used for coordinate conversion
     * @return {@code true} if the building was successfully placed; {@code false} otherwise
     */
    boolean placeBuilding(Entity entityType, TiledMapTileLayer sampleLayer) {
        PositionComponent positionComponent = entityType.getComponent(PositionComponent.class);
        Vector2 worldCoordinate = WorldCoordinateUtils.centerClickWithBuilding(
            viewport,
            sampleLayer,
            positionComponent.basePosition.x,
            positionComponent.basePosition.y,
            entityType,
            engine
        );
        Gdx.app.debug(
            "[DEBUG] - BuildingPlacer",
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
            "[DEBUG] - BuildingPlacer",
            "Checking if tile is buildable on coordinates: " +
            worldCoordinateX +
            " x " +
            worldCoordinateY +
            " y"
        );

        if (!checkIfTileIsBuildableOnBottomLayer(engine, entityType)) {
            Gdx.app.debug("[DEBUG] - BuildingPlacer", "Tile is not buildable on bottom layer.");
            return false;
        }

        if (!checkIfTileIsBuildableOnResourceLayer(engine, entityType)) {
            Gdx.app.debug("[DEBUG] - BuildingPlacer", "Tile is not buildable on resource layer.");
            return false;
        }

        if (checkIfPlaceIsOccupiedByBuilding(engine, entityType)) {
            Gdx.app.debug(
                "[DEBUG] - BuildingPlacer",
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
                "[DEBUG] - BuildingPlacer",
                "Building is a resource building. Checking for adjacent resources."
            );
            TiledMapTileLayer resourceLayer = mapLayerMapper.resourceLayerMapper.get(
                engine.getEntitiesFor(mapLayerMapper.mapLayerFamily).first()
            )
                .resourceLayer;
            if (!checkIfResourceBuildingIsPlaceable(entityType, resourceLayer)) {
                Gdx.app.debug(
                    "[DEBUG] - BuildingPlacer",
                    "Tile is buildable on resource layer but has no adjacent resource."
                );
                return false;
            }
        }

        occupyTile(entityType);
        Gdx.app.debug(
            "[DEBUG] - BuildingPlacer",
            "Tile is buildable on resource layer and has adjacent resource."
        );

        engine.addEntity(entityType);
        return true;
    }

    /**
     * Checks whether the specified tile is already occupied by a building.
     *
     * @param engine the {@link Engine} used to retrieve existing entities.
     * @param tileX  the x-coordinate of the tile.
     * @param tileY  the y-coordinate of the tile.
     * @return {@code true} if the tile is occupied; {@code false} otherwise.
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
     * Checks whether the tile at the specified coordinates is buildable on the bottom layer.
     *
     * @param engine the {@link Engine} used to retrieve the map layer entity.
     * @param tileX  the x-coordinate of the tile.
     * @param tileY  the y-coordinate of the tile.
     * @return {@code true} if the tile is buildable; {@code false} otherwise.
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
     * Checks whether the tile at the specified coordinates is buildable on the resource layer.
     *
     * @param engine the {@link Engine} used to retrieve the map layer entity.
     * @param tileX  the x-coordinate of the tile.
     * @param tileY  the y-coordinate of the tile.
     * @return {@code true} if the tile is buildable on the resource layer or if the cell is absent;
     * {@code false} otherwise.
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
     * Determines if the given entity is a resource-producing building.
     *
     * @param entityType the building entity
     * @return {@code true} if the entity has a {@link ResourceProductionComponent}; {@code false} otherwise
     */
    private boolean checkIfBuildingIsResourceBuilding(Entity entityType) {
        return entityType.getComponent(ResourceProductionComponent.class) != null;
    }

    /**
     * Handles the placement check for a resource-producing building.
     * <p>
     * This method delegates to {@link ResourceAdjacencyChecker} to validate if the building
     * has any adjacent resource tiles of the required type.
     * </p>
     *
     * @param entityType  the resource building to be placed
     * @param sampleLayer the resource tile layer to check against
     * @return {@code true} if adjacent resources are found; {@code false} otherwise
     */
    private boolean checkIfResourceBuildingIsPlaceable(
        Entity entityType,
        TiledMapTileLayer sampleLayer
    ) {
        return ResourceAdjacencyChecker.hasAdjacentResource(entityType, sampleLayer);
    }

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
