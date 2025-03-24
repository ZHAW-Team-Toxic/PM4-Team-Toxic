package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.viewport.Viewport;

public class BuildingManagerSystem extends EntitySystem {

    private final TiledMapTileLayer sampleLayer;
    private final Viewport viewport;
    private final Engine engine;
    private final BuildingPlacer buildingPlacer;
    private final BuildingRemover buildingRemover;

    public BuildingManagerSystem(TiledMapTileLayer sampleLayer, Viewport viewport, Engine engine) {
        this.sampleLayer = sampleLayer;
        this.viewport = viewport;
        this.engine = engine;
        this.buildingPlacer = new BuildingPlacer(viewport, engine);
        this.buildingRemover = new BuildingRemover(viewport, engine);
    }

    @Override
    public void update(float deltaTime) {
        // If you have any continuous building-related logic, you can process it here.
        // Otherwise, building placement and removal might be triggered by events.
    }

    public boolean placeBuilding(Entity buildingEntity) {
        return buildingPlacer.placeBuilding(buildingEntity, sampleLayer);
    }

    public boolean removeBuilding(float x, float y) {
        return buildingRemover.removeBuilding(sampleLayer, x, y);
    }
}
