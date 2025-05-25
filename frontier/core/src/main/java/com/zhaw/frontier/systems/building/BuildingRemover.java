package com.zhaw.frontier.systems.building;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.CostComponent;
import com.zhaw.frontier.components.InventoryComponent;
import com.zhaw.frontier.components.NonRemovalObjectComponent;
import com.zhaw.frontier.components.OccupiesTilesComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.WallPieceComponent;
import com.zhaw.frontier.systems.ErrorSystem;
import com.zhaw.frontier.systems.WallManager;
import com.zhaw.frontier.utils.WorldCoordinateUtils;

/**
 * Responsible for removing building entities from the map.
 * <p>
 * The {@code BuildingRemover} calculates the world coordinates from the input coordinates
 * using the provided viewport and tile layer. It then iterates over all entities with a
 * {@link PositionComponent} to find and remove an entity located at the specified position.
 * </p>
 */
public class BuildingRemover {

    private final Viewport viewport;
    private final Engine engine;

    /**
     * Constructs a new {@code BuildingRemover} with the specified viewport and engine.
     *
     * @param viewport the {@link Viewport} used for converting screen coordinates to world coordinates.
     * @param engine   the {@link Engine} used for managing entities.
     */
    public BuildingRemover(Viewport viewport, Engine engine) {
        this.viewport = viewport;
        this.engine = engine;
    }

    /**
     * Attempts to remove a building entity from the map at the specified coordinates.
     * <p>
     * The method converts the given coordinates into world coordinates using the provided tile layer,
     * then iterates over all entities that have a {@link PositionComponent} and {@link OccupiesTilesComponent}.
     * If an entity is found occupying the calculated tile position, it is removed from the engine.
     * </p>
     *
     * @param sampleLayer the {@link TiledMapTileLayer} used for coordinate conversion.
     * @param x           the x-coordinate (in screen space) where removal is attempted.
     * @param y           the y-coordinate (in screen space) where removal is attempted.
     * @return {@code true} if a building entity was found and removed; {@code false} otherwise.
     */
    public boolean removeBuilding(TiledMapTileLayer sampleLayer, float x, float y, InventoryComponent inventoryComponent) {
        Vector2 worldCoordinate = WorldCoordinateUtils.calculateWorldCoordinate(
            viewport,
            sampleLayer,
            x,
            y
        );
        int worldCoordinateX = (int) worldCoordinate.x;
        int worldCoordinateY = (int) worldCoordinate.y;

        ImmutableArray<Entity> entitiesWithPosition = engine.getEntitiesFor(
            Family.all(PositionComponent.class, OccupiesTilesComponent.class).get()
        );
        for (Entity entity : entitiesWithPosition) {
            OccupiesTilesComponent occupiesTilesComponent = entity.getComponent(
                OccupiesTilesComponent.class
            );

            if (occupiesTilesComponent != null) {
                for (Vector2 tile : occupiesTilesComponent.occupiedTiles) {
                    if (tile.x == worldCoordinateX && tile.y == worldCoordinateY) {
                        if (entity.getComponent(NonRemovalObjectComponent.class) != null) {
                            ErrorSystem.getInstance().showObjectCannotBeDestroyed();
                            return false;
                        }
                        engine.removeEntity(entity);
                        updateWalls(entity);
                        refundResources(entity, inventoryComponent);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void updateWalls(Entity entity) {
        if (entity.getComponent(WallPieceComponent.class) != null) {
            WallManager.update(engine);
        }
    }

    private void refundResources(Entity entity, InventoryComponent inventory) {
        CostComponent costComponent = entity.getComponent(CostComponent.class);
        if (costComponent == null) {
            return;
        }
        costComponent.resouceCosts.forEach((type, cost) -> {
            int current = inventory.resources.getOrDefault(type, 0);
            inventory.resources.put(type, current + cost);
        });
    }
}
