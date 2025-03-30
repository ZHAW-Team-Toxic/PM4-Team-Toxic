package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.ResourceProductionComponent;
import com.zhaw.frontier.components.map.MapLayerEnum;
import com.zhaw.frontier.components.map.ResourceTypeEnum;

import java.util.Objects;

public class ResourceAdjacencyChecker {

    public static boolean hasAdjacentResource(Entity entity, TiledMapTileLayer resourceLayer) {
        if(!Objects.equals(resourceLayer.getName(), MapLayerEnum.RESOURCE_LAYER.toString())) {
            throw new IllegalArgumentException("Invalid layer type. Expected RESOURCE_LAYER.");
        }

        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        if (positionComponent == null) {
            throw new IllegalArgumentException("Entity does not have a PositionComponent.");
        }
        Vector2 position = positionComponent.position;

        Vector2[] directions = {
            new Vector2(-1, 0), new Vector2(1, 0),
            new Vector2(0, -1), new Vector2(0, 1),
            new Vector2(-1, -1), new Vector2(1, -1),
            new Vector2(-1, 1), new Vector2(1, 1)
        };

        int amount = 0;

        for (Vector2 dir : directions) {
            Vector2 adjacent = new Vector2(position).add(dir);
            TiledMapTileLayer.Cell cell = resourceLayer.getCell((int) adjacent.x, (int) adjacent.y);
            if (cell != null && cell.getTile().getProperties().containsKey("resourceType")) {
                ResourceTypeEnum resourcetype = entity.getComponent(ResourceProductionComponent.class).productionRate.keySet().iterator().next();
                if (cell.getTile().getProperties().get("resourceType").equals(resourcetype.toString())) {
                    amount++;
                }
            }
        }
        if(amount > 0) {
            entity.getComponent(ResourceProductionComponent.class).countOfAdjacentResources = amount;
            return true;
        } else {
            return false;
        }
    }
}

