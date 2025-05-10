package com.zhaw.frontier.utils;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.PositionComponent;

/**
 * Singleton responsible for spawning enemy entities and assigning their behavior.
 */
public class EnemySpawner {

    private static EnemySpawner instance;

    private Viewport viewport;
    private Engine engine;

    private EnemySpawner() {}

    public static EnemySpawner getInstance() {
        if (instance == null) {
            instance = new EnemySpawner();
        }
        return instance;
    }

    public void init(Viewport viewport, Engine engine) {
        this.viewport = viewport;
        this.engine = engine;
    }

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

        positionComponent.basePosition.set(worldCoordinateX, worldCoordinateY);

        Gdx.app.debug(
            "[DEBUG] - EnemySpawner",
            "Spawning enemy on coordinates: " + worldCoordinateX + " x " + worldCoordinateY + " y"
        );
        engine.addEntity(entityType);
        return true;
    }
}
