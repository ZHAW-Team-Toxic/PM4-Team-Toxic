package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 *
 */
public class BuildingManager {

    private final TiledMapTileLayer sampleLayer;

    private final BuildingPlacer buildingPlacer;
    private final BuildingRemover buildingRemover;

    /**
     *
     */
    public BuildingManager(TiledMapTileLayer sampleLayer, Viewport viewport, Engine engine){
        this.sampleLayer = sampleLayer;

        this.buildingPlacer = new BuildingPlacer(viewport, engine);
        this.buildingRemover = new BuildingRemover(viewport, engine);
    }

    public boolean placeBuilding(Entity entityType) {
        return buildingPlacer.placeBuilding(entityType, sampleLayer);
    }

    public boolean removeBuilding(float x, float y) {
        return buildingRemover.removeBuilding(sampleLayer, x, y);
    }

}
