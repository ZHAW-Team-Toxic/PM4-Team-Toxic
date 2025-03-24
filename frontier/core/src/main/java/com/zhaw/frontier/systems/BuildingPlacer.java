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
import com.zhaw.frontier.components.map.TiledPropertiesEnum;
import com.zhaw.frontier.mappers.MapLayerMapper;

/**
 * Responsible for placing building entities on a tiled map.
 * <p>
 * The {@code BuildingPlacer} checks whether a given tile is buildable based on the properties
 * from the bottom and resource layers, and ensures that the tile is not already occupied by another building.
 * If all conditions are met, the building entity is added to the engine.
 * </p>
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
     * Attempts to place a building entity on the map.
     * <p>
     * This method calculates the world coordinate from the building's current position and
     * the given tile layer. It then verifies whether the target tile is buildable on both the
     * bottom and resource layers and whether the location is already occupied. If the tile is
     * buildable and unoccupied, the building entity is added to the engine.
     * </p>
     *
     * @param entityType  the building entity to be placed.
     * @param sampleLayer the {@link TiledMapTileLayer} representing the map layer used for placement.
     * @return {@code true} if the building is successfully placed; {@code false} otherwise.
     */
    boolean placeBuilding(Entity entityType, TiledMapTileLayer sampleLayer) {
        PositionComponent positionComponent = entityType.getComponent(PositionComponent.class);
        Vector2 worldCoordinate = BuildingUtils.calculateWorldCoordinate(
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

        Gdx.app.debug(
            "[DEBUG] - BuildingPlacer",
            "Placing building on coordinates: " + worldCoordinateX + " x " + worldCoordinateY + " y"
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
     *         {@code false} otherwise.
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
}
