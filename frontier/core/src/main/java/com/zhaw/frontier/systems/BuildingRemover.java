package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.BuildingPositionComponent;

/**
 *
 */
public class BuildingRemover {

    private final Viewport viewport;
    private final Engine engine;

    /**
     *
     * @param viewport
     * @param engine
     */
    public BuildingRemover(Viewport viewport, Engine engine) {
        this.viewport = viewport;
        this.engine = engine;
    }

    /**
     * TODO
     */
    public boolean removeBuilding(TiledMapTileLayer sampleLayer, float x, float y) {
        Vector2 worldCoordinate = BuildingUtils.calculateWorldCoordinate(
            viewport,
            sampleLayer,
            x,
            y
        );
        int worldCoordinateX = (int) worldCoordinate.x;
        int worldCoordinateY = (int) worldCoordinate.y;
        ImmutableArray<Entity> entitiesWithPosition = engine.getEntitiesFor(
            Family.all(BuildingPositionComponent.class).get()
        );
        for (Entity entity : entitiesWithPosition) {
            BuildingPositionComponent buildingPositionComponent = entity.getComponent(
                BuildingPositionComponent.class
            );
            if (
                buildingPositionComponent.position.x == worldCoordinateX &&
                buildingPositionComponent.position.y == worldCoordinateY
            ) {
                engine.removeEntity(entity);
                return true;
            }
        }
        return false;
    }
}
