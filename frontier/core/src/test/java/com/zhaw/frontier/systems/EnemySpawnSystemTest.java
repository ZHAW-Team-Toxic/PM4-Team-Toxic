package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.zhaw.frontier.GdxExtension;
import com.zhaw.frontier.TestMapEnvironment;
import com.zhaw.frontier.components.EnemyComponent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

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
    }

    private void removeAllEnemies() {
        ImmutableArray<Entity> enemies = testEngine.getEntitiesFor(Family.all(EnemyComponent.class).get());
        for (Entity enemy : enemies) {
            testEngine.removeEntity(enemy);
        }
    }
}
