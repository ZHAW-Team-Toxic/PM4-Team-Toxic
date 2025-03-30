package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.ResourceProductionComponent;
import com.zhaw.frontier.components.map.MapLayerEnum;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import java.util.Objects;

/**
 * Utility class for checking if a building entity has adjacent resource tiles of the required type.
 * <p>
 * This class verifies whether any of the eight surrounding tiles (including diagonals) around a building
 * contain a resource tile matching the {@link ResourceTypeEnum} defined in the building's
 * {@link ResourceProductionComponent}.
 * <p>
 * If one or more matching resource tiles are found, the entity's production component is updated with the
 * count of adjacent resources.
 *
 * <p>Usage context:
 * <ul>
 *     <li>To validate building placement based on proximity to required resources</li>
 *     <li>To avoid running resource search logic in runtime systems like {@code ResourceBuildingRangeSystem}</li>
 * </ul>
 *
 * @see PositionComponent
 * @see ResourceProductionComponent
 * @see MapLayerEnum#RESOURCE_LAYER
 */
public class ResourceAdjacencyChecker {

    /**
     * Checks whether the given entity has at least one adjacent resource tile of the required type.
     * <p>
     * The method examines all eight neighboring tiles around the entity's position
     * and compares their {@code resourceType} property with the resource type defined
     * in the entity's {@link ResourceProductionComponent}.
     * <p>
     * If matching tiles are found, their count is stored in the production component's
     * {@code countOfAdjacentResources} field.
     *
     * @param entity        the building entity to check; must have a {@link PositionComponent} and {@link ResourceProductionComponent}
     * @param resourceLayer the {@link TiledMapTileLayer} representing the resource layer; must be named {@code RESOURCE_LAYER}
     * @return {@code true} if one or more matching resource tiles are adjacent; {@code false} otherwise
     * @throws IllegalArgumentException if the layer is not the RESOURCE_LAYER or if the entity lacks required components
     */
    public static boolean hasAdjacentResource(Entity entity, TiledMapTileLayer resourceLayer) {
        if (!Objects.equals(resourceLayer.getName(), MapLayerEnum.RESOURCE_LAYER.toString())) {
            throw new IllegalArgumentException("Invalid layer type. Expected RESOURCE_LAYER.");
        }

        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        if (positionComponent == null) {
            throw new IllegalArgumentException("Entity does not have a PositionComponent.");
        }

        Vector2 position = positionComponent.position;
        Vector2[] directions = {
            new Vector2(-1, 0),
            new Vector2(1, 0),
            new Vector2(0, -1),
            new Vector2(0, 1),
            new Vector2(-1, -1),
            new Vector2(1, -1),
            new Vector2(-1, 1),
            new Vector2(1, 1),
        };

        int amount = 0;

        for (Vector2 dir : directions) {
            Vector2 adjacent = new Vector2(position).add(dir);
            TiledMapTileLayer.Cell cell = resourceLayer.getCell((int) adjacent.x, (int) adjacent.y);
            if (cell != null && cell.getTile().getProperties().containsKey("resourceType")) {
                ResourceTypeEnum resourceType = entity
                    .getComponent(ResourceProductionComponent.class)
                    .productionRate.keySet()
                    .iterator()
                    .next();
                if (
                    cell
                        .getTile()
                        .getProperties()
                        .get("resourceType")
                        .equals(resourceType.toString())
                ) {
                    amount++;
                }
            }
        }

        if (amount > 0) {
            entity.getComponent(ResourceProductionComponent.class).countOfAdjacentResources =
            amount;
            Gdx.app.debug(
                "[DEBUG] - ResourceAdjacencyChecker",
                "Found " +
                amount +
                " adjacent resources of type " +
                entity
                    .getComponent(ResourceProductionComponent.class)
                    .productionRate.keySet()
                    .iterator()
                    .next()
            );
            return true;
        } else {
            return false;
        }
    }
}
