package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.ResourceProductionComponent;
import com.zhaw.frontier.components.map.MapLayerEnum;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Utility class for checking if a multi-tile building has adjacent resource tiles.
 */
public class ResourceAdjacencyChecker {

    public static boolean hasAdjacentResource(Entity entity, TiledMapTileLayer resourceLayer) {
        if (!Objects.equals(resourceLayer.getName(), MapLayerEnum.RESOURCE_LAYER.toString())) {
            throw new IllegalArgumentException("Invalid layer type. Expected RESOURCE_LAYER.");
        }

        PositionComponent pos = entity.getComponent(PositionComponent.class);
        ResourceProductionComponent prod = entity.getComponent(ResourceProductionComponent.class);

        if (pos == null || prod == null) {
            throw new IllegalArgumentException(
                "Entity is missing PositionComponent or ResourceProductionComponent."
            );
        }

        ResourceTypeEnum requiredType = prod.productionRate.keySet().iterator().next();
        int baseX = (int) pos.basePosition.x;
        int baseY = (int) pos.basePosition.y;

        int width = pos.widthInTiles;
        int height = pos.heightInTiles;

        // Directions to check around the whole building area
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

        Set<Vector2> checked = new HashSet<>();
        int adjacentCount = 0;

        for (int x = baseX; x < baseX + width; x++) {
            for (int y = baseY; y < baseY + height; y++) {
                for (Vector2 dir : directions) {
                    int checkX = x + (int) dir.x;
                    int checkY = y + (int) dir.y;

                    Vector2 checkPos = new Vector2(checkX, checkY);
                    if (checked.contains(checkPos)) continue;
                    checked.add(checkPos);

                    TiledMapTileLayer.Cell cell = resourceLayer.getCell(checkX, checkY);
                    if (
                        cell != null &&
                        cell.getTile() != null &&
                        cell.getTile().getProperties().containsKey("resourceType")
                    ) {
                        String cellType = (String) cell
                            .getTile()
                            .getProperties()
                            .get("resourceType");
                        if (cellType.equals(requiredType.toString())) {
                            adjacentCount++;
                        }
                    }
                }
            }
        }

        if (adjacentCount > 0) {
            prod.countOfAdjacentResources = adjacentCount;
            Gdx.app.debug(
                "[DEBUG] - ResourceAdjacencyChecker",
                "Found " + adjacentCount + " adjacent resources of type " + requiredType
            );
            return true;
        }

        return false;
    }
}
