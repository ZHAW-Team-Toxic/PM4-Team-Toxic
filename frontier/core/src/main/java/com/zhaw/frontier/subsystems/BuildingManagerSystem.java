package com.zhaw.frontier.subsystems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.ResourceLayerComponent;
import com.zhaw.frontier.entities.EntitiesUtils;
import com.zhaw.frontier.entities.Map;
import com.zhaw.frontier.entities.Tower;
import com.zhaw.frontier.exceptions.EntityNotFoundException;
import com.zhaw.frontier.mappers.MapLayerMapper;
import com.zhaw.frontier.mappers.TowerMapper;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages the building of towers on the map. It checks if a building can be placed on a certain position.
 * It holds a list of all buildings on the map and a representation of the map.
 * It also provides methods to place, remove and clear buildings.
 * It uses the @TowerMapper and @MapLayerMapper to access the necessary components
 */
public class BuildingManagerSystem {

    @Getter
    private final Map map;
    private final Viewport viewport;
    @Getter
    private final List<Entity> buildingEntities;
    private final static String BUILDABLE_PROPERTY = "buildable";

    private final TowerMapper towerMapper = new TowerMapper();
    private final MapLayerMapper mapLayerMapper = new MapLayerMapper();

    /**
     * Constructor for the BuildingManagerSystem.
     *
     * @param map      The current map.
     * @param viewport The viewport.
     */
    public BuildingManagerSystem(Map map, Viewport viewport) {
        this.map = map;
        this.viewport = viewport;
        this.buildingEntities = new ArrayList<>();
    }

    /**
     * Places a building on the map on following conditions:
     * - The tile is buildable on the bottom layer
     * - The tile is buildable on the resource layer
     * - The place is not occupied by another building
     * It first translates the screen coordinates to world coordinates and then checks the conditions.
     * Before plccing the building it calculates the position based on the pixel-coordinates (tile-coordinate * tile-width in pixels).
     *
     * @param screenX    Raw x coordinate of the screen
     * @param screenY    Raw y coordinate of the screen
     * @param entityType Which type of building should be placed
     * @return True if the building was placed, false if not
     * @throws EntityNotFoundException If the entity is not found
     */
    public boolean placeBuilding(float screenX, float screenY, Entity entityType) throws EntityNotFoundException {
        Vector2 worldCoordinate = calculateWorldCoordinate(screenX, screenY);
        Gdx.app.log("BuildingManagerSystem", "World Coordinate: " + worldCoordinate);
        Entity buildingEntity = entityMapper(entityType);
        PositionComponent buildingPosition = towerMapper.pm.get(buildingEntity);
        buildingPosition.position = worldCoordinate;
        if (checkIfTileIsBuildableOnBottomLayer(buildingPosition)) {
            Gdx.app.log("BuildingManagerSystem", "Tile is buildable on bottom layer");
            if (checkIfTileIsBuildableOnResourceLayer(buildingPosition)) {
                Gdx.app.log("BuildingManagerSystem", "Tile is buildable on resource layer");
                if (!checkIfPlaceIsOccupiedByBuilding(buildingPosition)) {
                    buildingEntities.add(buildingEntity);
                    Gdx.app.log("BuildingManagerSystem", "Building placed at: " + buildingPosition.position);
                    return true;
                } else {
                    Gdx.app.log("BuildingManagerSystem", "Place is occupied by building");
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Removes a building from the buildingEntities list.
     *
     * @param buildingEntity The building to remove.
     * @return True if the building was removed, false if not.
     */
    public boolean removeBuilding(Entity buildingEntity) {
        buildingEntities.remove(buildingEntity);
        return true;
    }

    /**
     * Clears all buildings from the buildingEntities list.
     *
     * @return True if the buildings were removed, false if not.
     */
    public boolean clearBuildings() {
        buildingEntities.clear();
        return true;
    }

    private Entity entityMapper(Entity entity) throws EntityNotFoundException {
        if (entity instanceof Tower tower) {
            EntitiesUtils.buildEntity(tower, List.of(towerMapper.pm.get(entity), towerMapper.rm.get(entity)));
            return tower;
        }
        throw new EntityNotFoundException("Entity not found");
    }

    private Vector2 calculateWorldCoordinate(float screenX, float screenY) {
        TiledMapTileLayer sampleLayer = mapLayerMapper.bottomLayerMapper.get(map).bottomLayer;
        Gdx.app.log("BuildingManagerSystem", "tileWidth: " + sampleLayer.getTileWidth() + " tileHeight: " + sampleLayer.getTileHeight());

        Vector3 worldCoords = new Vector3(screenX, screenY, 0);
        viewport.getCamera().unproject(worldCoords);
        Gdx.app.log("BuildingManagerSystem", "WorldCoords: " + worldCoords);

        int tileX = (int) (worldCoords.x / sampleLayer.getTileWidth());
        int tileY = (int) (worldCoords.y / sampleLayer.getTileHeight());

        Gdx.app.log("BuildingManagerSystem", "TileX: " + tileX + " TileY: " + tileY);

        return new Vector2(tileX, tileY);

    }

    private boolean checkIfPlaceIsOccupiedByBuilding(PositionComponent buildingToCheck) {
        for (Entity buildingEntity : buildingEntities) {
            PositionComponent buildingPosition = towerMapper.pm.get(buildingEntity);

            if (buildingPosition.position.x == buildingToCheck.position.x && buildingPosition.position.y == buildingToCheck.position.y) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIfTileIsBuildableOnBottomLayer(PositionComponent buildingToCheck) {
        BottomLayerComponent bottomLayerComponent = mapLayerMapper.bottomLayerMapper.get(map);
        TiledMapTileLayer bottomLayer = bottomLayerComponent.bottomLayer;
        Gdx.app.log("BuildingManagerSystem", "bottomlayer: " + bottomLayer);
        TiledMapTileLayer.Cell cell = bottomLayer.getCell((int) buildingToCheck.position.x, (int) buildingToCheck.position.y);
        Gdx.app.log("BuildingManagerSystem", "Cell: " + cell);
        if (cell != null && cell.getTile() != null && cell.getTile().getProperties().get(BUILDABLE_PROPERTY) != null) {
            return (boolean) cell.getTile().getProperties().get(BUILDABLE_PROPERTY);
        }

        return false;
    }

    private boolean checkIfTileIsBuildableOnResourceLayer(PositionComponent buildingToCheck) {
        ResourceLayerComponent resourceLayerComponent = mapLayerMapper.resourceLayerMapper.get(map);
        TiledMapTileLayer resourceLayer = resourceLayerComponent.resourceLayer;
        TiledMapTileLayer.Cell cell = resourceLayer.getCell((int) buildingToCheck.position.x, (int) buildingToCheck.position.y);
        Gdx.app.log("BuildingManagerSystem", "Cell: " + cell);
        return cell == null;
    }


}
