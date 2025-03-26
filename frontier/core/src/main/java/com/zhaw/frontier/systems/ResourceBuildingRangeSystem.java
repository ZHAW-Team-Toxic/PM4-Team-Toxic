package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.ResourceCollectionRangeComponent;
import com.zhaw.frontier.components.ResourceProductionComponent;
import com.zhaw.frontier.components.map.TiledPropertiesEnum;
import com.zhaw.frontier.exceptions.ResourceBuildingRangeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The ResourceBuildingRangeSystem calculates and updates the number of resource tiles
 * within range of resource buildings. For each entity with a {@link ResourceCollectionRangeComponent},
 * it computes the surrounding tiles (excluding the center tile) and counts those that match the resource type
 * defined in the entity's {@link ResourceProductionComponent}. This calculation is performed only once per entity,
 * as controlled by the {@code isCalculated} flag in the component.
 *
 * <p>This system assumes that resource tiles are static and do not change during runtime.
 * It also validates that the provided resource layer is valid using the {@link #checkResourceLayer(TiledMapTileLayer)}
 * method.</p>
 *
 * @see ResourceCollectionRangeComponent
 * @see ResourceProductionComponent
 * @see PositionComponent
 * @see TiledPropertiesEnum
 */
public class ResourceBuildingRangeSystem extends EntitySystem {

    public final Engine engine;
    public TiledMapTileLayer resourceLayer;

    /**
     * Constructs a new ResourceBuildingRangeSystem.
     *
     * @param engine        the Ashley engine containing entities
     * @param resourceLayer the TiledMapTileLayer representing the resource layer; must not be null and its name must equal {@code TiledPropertiesEnum.RESOURCE_LAYER.toString()}
     * @throws IllegalArgumentException if resourceLayer is null or its name is invalid
     */
    public ResourceBuildingRangeSystem(Engine engine, TiledMapTileLayer resourceLayer) {
        super();
        this.engine = engine;
        checkResourceLayer(resourceLayer);
    }

    /**
     * Updates each entity with a {@link ResourceCollectionRangeComponent} by calculating the number of resource tiles in range.
     * The calculation is only performed if the component's {@code isCalculated} flag is false.
     *
     * <p>For each applicable entity, it retrieves its position and resource production data, calculates the surrounding tile coordinates (excluding the center),
     * counts the matching resource tiles, and updates the component with the total count.</p>
     *
     * @param deltaTime the time in seconds since the last update
     */
    @Override
    public void update(float deltaTime) {
        // Process each entity with a ResourceCollectionRangeComponent
        for (Entity entity : engine.getEntitiesFor(
            Family.all(ResourceCollectionRangeComponent.class).get()
        )) {
            ResourceCollectionRangeComponent range = entity.getComponent(
                ResourceCollectionRangeComponent.class
            );
            ResourceProductionComponent production = entity.getComponent(
                ResourceProductionComponent.class
            );

            // Skip entities that have already been calculated
            if (range.isCalculated) {
                continue;
            }

            // Calculate the coordinates of tiles within range (excluding the center tile)
            List<Vector2> tilesInRange = getTileCoordinatesInRange(
                entity.getComponent(PositionComponent.class).position,
                range.range
            );
            //check the map for the resource type before accessing the production rate
            if(production.productionRate.isEmpty()) {
                continue;
            }
            // Retrieve the first resource type from production rate map, if available
            Object key = production.productionRate.keySet().iterator().next();
            if (Objects.nonNull(key)) {
                TiledPropertiesEnum resourceType = production.productionRate
                    .keySet()
                    .iterator()
                    .next();
                List<TiledMapTile> tiles = getTilesForResourceType(tilesInRange, resourceType);
                int amount = tiles.size();

                range.tilesInRange += amount;
                range.isCalculated = true;
            }
        }
    }

    private List<Vector2> getTileCoordinatesInRange(Vector2 position, int range) {
        if (range < 1) {
            throw new ResourceBuildingRangeException("Range must be at least 1");
        }

        List<Vector2> tiles = new ArrayList<>();

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                // Skip the center tile
                if (!(x == 0 && y == 0)) {
                    // Add all other tiles to the list
                    tiles.add(new Vector2(position.x + x, position.y + y));
                }
            }
        }

        return tiles;
    }

    private List<TiledMapTile> getTilesForResourceType(
        List<Vector2> positions,
        TiledPropertiesEnum resourceType
    ) {
        List<TiledMapTile> tiles = new ArrayList<>();

        for (Vector2 position : positions) {
            TiledMapTileLayer.Cell cell = resourceLayer.getCell((int) position.x, (int) position.y);
            if (cell != null) {
                TiledMapTile tile = cell.getTile();
                if (
                    tile.getProperties().containsKey("resourceType") &&
                    tile.getProperties().get("resourceType").equals(resourceType.toString())
                ) {
                    tiles.add(tile);
                }
            }
        }

        return tiles;
    }

    private void checkResourceLayer(TiledMapTileLayer resourceLayer) {
        if (resourceLayer == null) {
            throw new IllegalArgumentException("Resource layer must not be null");
        }
        if (!TiledPropertiesEnum.RESOURCE_LAYER.toString().equals(resourceLayer.getName())) {
            throw new IllegalArgumentException("Must be resource layer");
        }
        this.resourceLayer = resourceLayer;
    }
}
