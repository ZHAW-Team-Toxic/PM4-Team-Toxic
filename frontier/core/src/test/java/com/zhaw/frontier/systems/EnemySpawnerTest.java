package com.zhaw.frontier.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
public class EnemySpawnerTest {

    private Viewport viewport;
    private Engine engine;
    private TiledMapTileLayer sampleLayer;
    private EnemySpawner enemySpawner;

    @BeforeEach
    void setUp() {
        viewport = mock(Viewport.class);
        engine = mock(Engine.class);
        sampleLayer = mock(TiledMapTileLayer.class);
        enemySpawner = new EnemySpawner(viewport, engine);

        // Mock tile dimensions
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
    }

    @Test
    void testSpawnEnemy() {
        Entity enemy = new Entity();
        PositionComponent position = new PositionComponent();
        position.currentPosition.set(5, 5);
        enemy.add(position);
        enemy.add(new EnemyComponent());

        boolean result = enemySpawner.spawnEnemy(enemy, sampleLayer);

        assertTrue(result, "Enemy should be successfully spawned");
        verify(engine).addEntity(enemy);
    }

    @Test
    void testSpawnEnemyWithRealViewport() {
        Entity enemy = new Entity();
        PositionComponent position = new PositionComponent();
        position.currentPosition.set(400, 300); // screen coordinates
        enemy.add(position);
        enemy.add(new EnemyComponent());

        boolean result = enemySpawner.spawnEnemy(enemy, sampleLayer);

        assertTrue(result, "Enemy should be successfully spawned");
        verify(engine).addEntity(enemy);

        // Manually calculate expected world coordinate and convert to tile space
        Vector3 world = viewport.unproject(new Vector3(400, 300, 0));
        float expectedX = world.x;
        float expectedY = world.y;

        // Make sure position has been updated correctly
        assertEquals(expectedX, position.currentPosition.x, 0.001f);
        assertEquals(expectedY, position.currentPosition.y, 0.001f);
    }
}
