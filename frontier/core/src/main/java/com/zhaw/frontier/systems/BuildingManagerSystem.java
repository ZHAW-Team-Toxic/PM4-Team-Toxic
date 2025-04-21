package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * System responsible for managing building entities.
 * <p>
 * The BuildingManagerSystem handles the placement and removal of building entities on a map.
 * It uses a {@link BuildingPlacer} to add buildings to a tile layer and a {@link BuildingRemover}
 * to remove them. This system is event-driven, meaning that building operations are triggered
 * by user input or other events rather than by continuous processing.
 * </p>
 */
public class BuildingManagerSystem extends EntitySystem {

    private final TiledMapTileLayer sampleLayer;
    private final BuildingPlacer buildingPlacer;
    private final BuildingRemover buildingRemover;

    /**
     * Constructs a new BuildingManagerSystem.
     *
     * @param sampleLayer the tile layer used for building placement and removal.
     * @param viewport    the {@link Viewport} used for coordinate conversion.
     * @param engine      the {@link Engine} that manages the entities.
     */
    public BuildingManagerSystem(TiledMapTileLayer sampleLayer, Viewport viewport, Engine engine) {
        this.sampleLayer = sampleLayer;
        this.buildingPlacer = new BuildingPlacer(viewport, engine);
        this.buildingRemover = new BuildingRemover(viewport, engine);
    }

    /**
     * Updates the building manager system.
     * <p>
     * This method can be used to process continuous building-related logic. Currently,
     * building placement and removal are triggered by events, so no processing is performed
     * on every update call.
     * </p>
     *
     * @param deltaTime the time elapsed since the last frame.
     *
     */
    @Override
    public void update(float deltaTime) {
        // If you have any continuous building-related logic, you can process it here.
        // Otherwise, building placement and removal might be triggered by events.
    }

    /**
     * Attempts to place a building on the map.
     *
     * @param buildingEntity the building entity to be placed.
     * @return {@code true} if the building was successfully placed; {@code false} otherwise.
     */
    public boolean placeBuilding(Entity buildingEntity) {
        return buildingPlacer.placeBuilding(buildingEntity, sampleLayer);
    }

    /**
     * Attempts to remove a building from the map at the specified coordinates.
     *
     * @param x the x-coordinate (in screen or world space, depending on implementation).
     * @param y the y-coordinate (in screen or world space, depending on implementation).
     * @return {@code true} if a building was successfully removed; {@code false} otherwise.
     */
    public boolean removeBuilding(float x, float y) {
        return buildingRemover.removeBuilding(sampleLayer, x, y);
    }
}
