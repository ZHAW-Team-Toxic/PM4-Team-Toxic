package com.zhaw.frontier.systems;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.zhaw.frontier.GdxExtension;
import com.zhaw.frontier.TestMapEnvironment;
import com.zhaw.frontier.components.EnemyComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.utils.AssetManagerInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Unit tests for the {@link EnemySpawnSystem}.
 */
@ExtendWith(GdxExtension.class)
public class EnemySpawnSystemTest {

    private static Engine testEngine;
    private static ExtendViewport gameWorldView;
    private static TestMapEnvironment testMapEnvironment;
    private static EnemySpawnSystem enemySpawnSystem;

    /**
     * Sets up the test environment by initializing the test map, engine, and viewport.
     *
     * <p>Uses {@link TestMapEnvironment} to prepare the map and provide tile layer access.
     * Adds the {@link EnemySpawnSystem} to the test engine.
     * </p>
     */
    @BeforeAll
    public static void setUp() {
        testMapEnvironment = new TestMapEnvironment();
        testEngine = testMapEnvironment.getTestEngine();
        gameWorldView = testMapEnvironment.getGameWorldView();
        AssetManagerInstance.getManager().load("packed/textures.atlas", TextureAtlas.class);
        AssetManagerInstance.getManager().finishLoading();

        addSystemsUnderTestHere();

        assertEquals(1, testEngine.getEntities().size(), "Only the map entity should be present.");
    }

    /**
     * Adds the system under test to the engine: {@link EnemySpawnSystem}.
     */
    private static void addSystemsUnderTestHere() {
        enemySpawnSystem = EnemySpawnSystem.create(testEngine);
    }

    @Test
    public void testSpawnPointSystemRoundOne() {
        removeAllEnemies();
        enemySpawnSystem.spawnEnemies(1);
        ImmutableArray<Entity> enemies = testEngine.getEntitiesFor(
            Family.all(EnemyComponent.class).get()
        );
        assertFalse(enemies.size() == 0, "Enemies should have been spawned in round one.");
    }

    @Test
    public void testNoDuplicateEnemiesAfterMultipleSpawns() {
        removeAllEnemies();
        enemySpawnSystem.spawnEnemies(1);
        int firstWave = testEngine.getEntitiesFor(Family.all(EnemyComponent.class).get()).size();

        enemySpawnSystem.spawnEnemies(1);
        int secondWave = testEngine.getEntitiesFor(Family.all(EnemyComponent.class).get()).size();

        assertTrue(secondWave > firstWave, "New enemies should be added on additional spawn call.");
    }

    @Test
    public void testEnemiesAreWithinMapBounds() {
        removeAllEnemies();
        enemySpawnSystem.spawnEnemies(1);

        ImmutableArray<Entity> enemies = testEngine.getEntitiesFor(
            Family.all(EnemyComponent.class, PositionComponent.class).get()
        );

        for (Entity e : enemies) {
            PositionComponent pos = e.getComponent(PositionComponent.class);
            assertTrue(
                pos.basePosition.x >= 0 && pos.basePosition.x < TestMapEnvironment.MAP_WIDTH_TILES,
                "Enemy X position should be within map bounds."
            );
            assertTrue(
                pos.basePosition.y >= 0 && pos.basePosition.y < TestMapEnvironment.MAP_HEIGHT_TILES,
                "Enemy Y position should be within map bounds."
            );
        }
    }

    @Test
    public void testSpawnPointsExistOnMap() {
        int count =
            testMapEnvironment.getBottomLayer().getWidth() *
            testMapEnvironment.getBottomLayer().getHeight();
        int spawnPointCount = 0;
        for (int i = 0; i < testMapEnvironment.getBottomLayer().getWidth(); i++) {
            for (int j = 0; j < testMapEnvironment.getBottomLayer().getHeight(); j++) {
                var cell = testMapEnvironment.getBottomLayer().getCell(i, j);
                if (
                    cell != null &&
                    cell.getTile() != null &&
                    Boolean.TRUE.equals(
                        cell.getTile().getProperties().get("isSpawnPoint", Boolean.class)
                    )
                ) {
                    spawnPointCount++;
                }
            }
        }
        assertTrue(spawnPointCount > 0, "Map should contain tiles marked as spawn points.");
    }

    private void removeAllEnemies() {
        ImmutableArray<Entity> enemies = testEngine.getEntitiesFor(
            Family.all(EnemyComponent.class).get()
        );
        for (Entity enemy : enemies) {
            testEngine.removeEntity(enemy);
        }
    }
}
