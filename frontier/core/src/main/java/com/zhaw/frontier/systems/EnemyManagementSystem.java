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

    private final TiledMapTileLayer sampleLayer;
    private final EnemySpawner enemeySpawner;

    /**
     * Constructs a new {@code EnemyManagementSystem}.
     *
     * @param sampleLayer the tile layer used for coordinate conversion and placement
     * @param viewport    the viewport used for screen-to-world coordinate mapping
     * @param engine      the Ashley engine for adding new entities
     */
    public EnemyManagementSystem(TiledMapTileLayer sampleLayer, Viewport viewport, Engine engine) {
        this.sampleLayer = sampleLayer;
        this.enemeySpawner = new EnemySpawner(viewport, engine);
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
        return enemeySpawner.spawnEnemy(enemEntity, sampleLayer);
    }
}
