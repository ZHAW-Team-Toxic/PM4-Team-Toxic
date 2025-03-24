package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.BuildingPositionComponent;
import com.zhaw.frontier.components.map.TiledPropertiesEnum;
import com.zhaw.frontier.mappers.MapLayerMapper;

/**
 *
 */
public class BuildingPlacer {

    private final Viewport viewport;
    private final Engine engine;

    private final MapLayerMapper mapLayerMapper = new MapLayerMapper();

    /**
     *
     */
    public BuildingPlacer(Viewport viewport, Engine engine) {
        this.viewport = viewport;
        this.engine = engine;
    }

    /**
     *
     */
    boolean placeBuilding(Entity entityType, TiledMapTileLayer sampleLayer) {
        BuildingPositionComponent buildingPositionComponent = entityType.getComponent(
            BuildingPositionComponent.class
        );
        Vector2 worldCoordinate = BuildingUtils.calculateWorldCoordinate(
            viewport,
            sampleLayer,
            buildingPositionComponent.position.x,
            buildingPositionComponent.position.y
        );
        int worldCoordinateX = (int) worldCoordinate.x;
        int worldCoordinateY = (int) worldCoordinate.y;
        buildingPositionComponent.position.x = worldCoordinateX;
        buildingPositionComponent.position.y = worldCoordinateY;
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

    private boolean checkIfPlaceIsOccupiedByBuilding(Engine engine, float tileX, float tileY) {
        ImmutableArray<Entity> entitiesWithPosition = engine.getEntitiesFor(
            Family.all(BuildingPositionComponent.class).get()
        );

        for (Entity entity : entitiesWithPosition) {
            BuildingPositionComponent buildingPositionComponent = entity.getComponent(
                BuildingPositionComponent.class
            );
            if (
                buildingPositionComponent.position.x == tileX &&
                buildingPositionComponent.position.y == tileY
            ) {
                return true;
            }
        }
        return false;
    }

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
