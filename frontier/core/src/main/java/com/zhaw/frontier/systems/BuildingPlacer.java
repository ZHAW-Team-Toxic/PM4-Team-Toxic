package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.ResourceProductionComponent;
import com.zhaw.frontier.components.map.TiledPropertiesEnum;
import com.zhaw.frontier.mappers.MapLayerMapper;
import com.zhaw.frontier.utils.WorldCoordinateUtils;

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
        Vector2 worldCoordinate = WorldCoordinateUtils.calculateWorldCoordinate(
            viewport,
            sampleLayer,
            positionComponent.position.x,
            positionComponent.position.y
        );
        int worldCoordinateX = (int) worldCoordinate.x;
        int worldCoordinateY = (int) worldCoordinate.y;
        positionComponent.position.x = worldCoordinateX;
        positionComponent.position.y = worldCoordinateY;
        Gdx.app.debug(
            "[DEBUG] - BuildingPlacer",
            "Checking if tile is buildable on coordinates: " +
            worldCoordinateX +
            " x " +
            worldCoordinateY +
            " y"
        );

        if (!checkIfTileIsBuildableOnBottomLayer(engine, worldCoordinateX, worldCoordinateY)) {
            Gdx.app.debug("[DEBUG] - BuildingPlacer", "Tile is not buildable on bottom layer.");
            return false;
        }

        if (!checkIfTileIsBuildableOnResourceLayer(engine, worldCoordinateX, worldCoordinateY)) {
            Gdx.app.debug("[DEBUG] - BuildingPlacer", "Tile is not buildable on resource layer.");
            return false;
        }

        if (checkIfPlaceIsOccupiedByBuilding(engine, worldCoordinateX, worldCoordinateY)) {
            Gdx.app.debug("[DEBUG] - BuildingPlacer", "Place is occupied by building.");
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
    private boolean checkIfPlaceIsOccupiedByBuilding(Engine engine, float tileX, float tileY) {
        ImmutableArray<Entity> entitiesWithPosition = engine.getEntitiesFor(
            Family.all(PositionComponent.class).get()
        );

        for (Entity entity : entitiesWithPosition) {
            PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
            if (positionComponent.position.x == tileX && positionComponent.position.y == tileY) {
                return true;
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
    private boolean checkIfTileIsBuildableOnBottomLayer(Engine engine, float tileX, float tileY) {
        TiledMapTileLayer bottomLayer = mapLayerMapper.bottomLayerMapper.get(
            engine.getEntitiesFor(mapLayerMapper.mapLayerFamily).first()
        )
            .bottomLayer;
        if (bottomLayer.getCell((int) tileX, (int) tileY) == null) {
            return false;
        }
        return (boolean) bottomLayer
            .getCell((int) tileX, (int) tileY)
            .getTile()
            .getProperties()
            .get(TiledPropertiesEnum.IS_BUILDABLE.toString());
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
    private boolean checkIfTileIsBuildableOnResourceLayer(Engine engine, float tileX, float tileY) {
        TiledMapTileLayer resourceLayer = mapLayerMapper.resourceLayerMapper.get(
            engine.getEntitiesFor(mapLayerMapper.mapLayerFamily).first()
        )
            .resourceLayer;
        if (resourceLayer.getCell((int) tileX, (int) tileY) == null) {
            return true;
        }
        return (boolean) resourceLayer
            .getCell((int) tileX, (int) tileY)
            .getTile()
            .getProperties()
            .get(TiledPropertiesEnum.IS_BUILDABLE.toString());
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
     * @param entityType   the resource building to be placed
     * @param sampleLayer  the resource tile layer to check against
     * @return {@code true} if adjacent resources are found; {@code false} otherwise
     */
    private boolean checkIfResourceBuildingIsPlaceable(
        Entity entityType,
        TiledMapTileLayer sampleLayer
    ) {
        return ResourceAdjacencyChecker.hasAdjacentResource(entityType, sampleLayer);
    }
}
