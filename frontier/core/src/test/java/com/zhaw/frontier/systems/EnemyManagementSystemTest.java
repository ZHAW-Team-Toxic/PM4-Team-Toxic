package com.zhaw.frontier.systems;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.GdxExtension;
import com.zhaw.frontier.components.EnemyComponent;
import com.zhaw.frontier.components.PositionComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxExtension.class)
public class EnemyManagementSystemTest {

    private TiledMapTileLayer sampleLayer;
    private Viewport viewport;
    private Engine engine;
    private EnemySpawner enemySpawner;
    private EnemyManagementSystem managementSystem;

    @BeforeEach
    void setUp() {
        sampleLayer = mock(TiledMapTileLayer.class);
        viewport = mock(Viewport.class);
        engine = mock(Engine.class);
        enemySpawner = mock(EnemySpawner.class);

        when(sampleLayer.getTileWidth()).thenReturn(1);
        when(sampleLayer.getTileHeight()).thenReturn(1);

        // Mock viewport unproject behavior
        when(viewport.unproject(any(Vector3.class)))
            .thenAnswer(invocation -> {
                Vector3 input = invocation.getArgument(0);
                // Assume that screen coordinates are same as world coordinates for simplicity
                return new Vector3(
                    input.x * sampleLayer.getTileWidth(),
                    input.y * sampleLayer.getTileHeight(),
                    0
                );
            });

        EnemyManagementSystem.init(sampleLayer, viewport, engine);
        managementSystem = EnemyManagementSystem.getInstance();
    }

    @Test
    void testSpawnEnemy() {
        Entity enemy = new Entity();
        PositionComponent position = new PositionComponent();
        position.basePosition.set(5, 5);
        enemy.add(new EnemyComponent());
        enemy.add(position);
        when(enemySpawner.spawnEnemy(enemy, sampleLayer)).thenReturn(true);

        boolean result = managementSystem.spawnEnemy(enemy);

        assertTrue(result, "Enemy should be successfully spawned");
    }
}
