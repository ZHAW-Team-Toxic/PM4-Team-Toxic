package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.utils.WorldCoordinateUtils;

/**
 * Responsible for spawning enemy entities and assigning their behavior based on type.
 * <p>
 * Enemies are placed in the world using screen coordinates converted to world tile coordinates,
 * and are given a suitable {@link EnemyBehaviour} before being added to the engine.
 * </p>
 */
public class EnemySpawner {

    private final Viewport viewport;
    private final Engine engine;

    /**
     * Creates a new {@code EnemySpawner} using the specified {@link Viewport} and {@link Engine}.
     *
     * @param viewport the viewport used to convert screen to world coordinates
     * @param engine   the Ashley engine to which enemies will be added
     */
    public EnemySpawner(Viewport viewport, Engine engine) {
        this.viewport = viewport;
        this.engine = engine;
    }

    /**
     * Spawns an enemy entity at the position defined in its {@link PositionComponent},
     * converts its coordinates to world grid coordinates, and assigns a behavior based on its {@link EnemyType}.
     *
     * @param entityType   the enemy entity to spawn
     * @param sampleLayer  the tile layer used to calculate world coordinates
     * @return true if the enemy was successfully spawned
     */
    public boolean spawnEnemy(Entity entityType, TiledMapTileLayer sampleLayer) {
        PositionComponent positionComponent = entityType.getComponent(PositionComponent.class);
        Gdx.app.debug(
            "[DEBUG] - EnemySpawner",
            "PositionComponent: " +
            positionComponent.basePosition.x +
            " x " +
            positionComponent.basePosition.y
        );
        Vector2 worldCoordinate = WorldCoordinateUtils.calculateWorldCoordinate(
            viewport,
            sampleLayer,
            positionComponent.basePosition.x,
            positionComponent.basePosition.y
        );
        Gdx.app.debug(
            "[DEBUG] - EnemySpawner",
            "World Coordinate: " + worldCoordinate.x + " x " + worldCoordinate.y
        );
        int worldCoordinateX = (int) worldCoordinate.x;
        int worldCoordinateY = (int) worldCoordinate.y;
        positionComponent.basePosition.x = worldCoordinateX;
        positionComponent.basePosition.y = worldCoordinateY;

        Gdx.app.debug(
            "[DEBUG] - EnemySpawner",
            "Spawning enemy on coordinates: " + worldCoordinateX + " x " + worldCoordinateY + " y"
        );
        engine.addEntity(entityType);
        return true;
    }
}
