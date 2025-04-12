package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * A system responsible for managing enemy-related logic,
 * such as spawning new enemies on the map.
 * <p>
 * Enemy spawning is delegated to an internal {@link EnemySpawner} instance.
 * </p>
 */
public class EnemyManagementSystem extends EntitySystem {

    private static EnemyManagementSystem instance;
    private TiledMapTileLayer sampleLayer;
    private EnemySpawner enemySpawner;

    private EnemyManagementSystem() {}

    /**
     * Initializes the singleton instance of {@code EnemyManagementSystem}.
     * @param sampleLayer   the sample layer to be used for enemy spawning
     * @param viewport      the viewport to be used for enemy spawning
     * @param engine        the engine to be used for enemy spawning
     */
    public static void init(TiledMapTileLayer sampleLayer, Viewport viewport, Engine engine) {
        if (instance != null) {
            throw new IllegalStateException("EnemyManagementSystem already initialized");
        }
        instance = new EnemyManagementSystem();
        instance.sampleLayer = sampleLayer;
        instance.enemySpawner = new EnemySpawner(viewport, engine);
    }

    /**
     * Returns the singleton instance of {@code EnemyManagementSystem}.
     * @return  the singleton instance of EnemyManagementSystem
     */
    public static EnemyManagementSystem getInstance() {
        if (instance == null) {
            throw new IllegalStateException("EnemyManagementSystem not initialized");
        }
        return instance;
    }

    /**
     * Called once per frame. Can be used to implement continuous enemy logic if needed.
     *
     * @param deltaTime the time elapsed since the last frame (in seconds)
     */
    @Override
    public void update(float deltaTime) {
        // If you have any continuous building-related logic, you can process it here.
        // Otherwise, building placement and removal might be triggered by events.
    }

    /**
     * Attempts to spawn an enemy on the map using the {@link EnemySpawner}.
     *
     * @param enemyEntity the enemy entity to spawn
     * @return {@code true} if the enemy was successfully spawned; {@code false} otherwise
     */
    public boolean spawnEnemy(Entity enemEntity) {
        return enemySpawner.spawnEnemy(enemEntity, sampleLayer);
    }
}
