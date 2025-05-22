package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.entityFactories.EnemyFactory;
import com.zhaw.frontier.enums.EnemyType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

/**
 * {@code EnemySpawnSystem} is an Ashley system responsible for spawning enemies on the map.
 *
 * <p>The spawn logic is based on a mathematical function that increases difficulty over rounds.
 * Spawn points are initialized from TiledMap tile properties and used to randomly place enemies
 * on the map.</p>
 *
 * <p>This system supports spawning Orcs, Goblins, and Demons in different distributions depending
 * on the current round. The system is implemented as a singleton and should be created via {@link #create(Engine)}.</p>
 */
public class EnemySpawnSystem {

    /**
     * Singleton instance of {@link EnemySpawnSystem}.
     */
    @Getter
    private static EnemySpawnSystem instance;

    private final Engine engine;
    private final TiledMapTileLayer bottomLayer;
    private final List<Vector2> spawnPoints = new ArrayList<>();

    // Parameters for the spawn scaling function: f(x) = a * b^(x * c) + d * sin(x + offset) + base
    private final float exponentialStretcher = 25F; //a
    private final float exponentialFlatOut = 0.1F; //c
    private final float difficultyVarianceDuringRound = 5F; //d
    private final float enemyStartingAmount = 40; //Starts actually at 62

    // Round thresholds to define enemy type distribution phases
    private final int phaseOne = 5;
    private final int phaseTwo = 8;
    private final int phaseThree = 12;

    private final float[] enemyDistribution = new float[3];

    /**
     * Private constructor. Use {@link #create(Engine)} to create and register the system.
     *
     * @param engine the engine to which this system belongs
     */
    private EnemySpawnSystem(Engine engine) {
        this.engine = engine;
        this.bottomLayer =
            engine
                .getEntitiesFor(Family.all(BottomLayerComponent.class).get())
                .first()
                .getComponent(BottomLayerComponent.class)
                .bottomLayer;

        initSpawnPoints();

        Collections.shuffle(spawnPoints);
        Gdx.app.debug(
            "[DEBUG] - EnemySpawnManager",
            "Spawn points initialized: " + spawnPoints.size()
        );
    }

    /**
     * Creates and registers the {@link EnemySpawnSystem} singleton with the given engine.
     *
     * @param engine the game engine
     * @return the singleton instance
     */
    public static EnemySpawnSystem create(Engine engine) {
        instance = new EnemySpawnSystem(engine);
        return instance;
    }

    /**
     * Spawns enemies for the specified round based on enemy distribution logic.
     *
     * @param round the current game round
     * @return true if spawning succeeded
     */
    public boolean spawnEnemies(int round) {
        calculateEnemyDistribution(round);
        int enemyCount = (int) calculateEnemyCount(round);

        Gdx.app.debug(
            "[DEBUG] - EnemySpawnManager",
            "Enemy count for round " + round + ": " + enemyCount
        );

        int orcCount = (int) (enemyCount * enemyDistribution[0]);
        int goblinCount = (int) (enemyCount * enemyDistribution[1]);
        int demonCount = (int) (enemyCount * enemyDistribution[2]);

        Gdx.app.debug(
            "[DEBUG] - EnemySpawnManager",
            "Enemy distribution for round " +
                round +
                ": " +
                "Orc: " +
                orcCount +
                ", Goblin: " +
                goblinCount +
                ", Demon: " +
                demonCount
        );

        return spawnOrc(orcCount) && spawnGoblin(goblinCount) && spawnDemon(demonCount);
    }

    /**
     * Initializes the list of spawn points by scanning the bottom layer of the tile map.
     *
     * @return true if spawn points were found
     */
    private void initSpawnPoints() {
        for (int i = 0; i < bottomLayer.getWidth(); i++) {
            for (int j = 0; j < bottomLayer.getHeight(); j++) {
                TiledMapTile tile = bottomLayer.getCell(i, j).getTile();
                if (
                    tile != null &&
                        tile.getProperties().containsKey("isSpawnPoint") &&
                        tile.getProperties().get("isSpawnPoint", Boolean.class)
                ) {
                    spawnPoints.add(new Vector2(i, j));
                }
            }
        }
        if (spawnPoints.isEmpty()) {
            Gdx.app.error(
                "[ERROR] - EnemySpawnManager",
                "No spawn points found in the bottom layer"
            );
        }
    }

    /**
     * Spawns a given number of Orcs at random spawn points.
     *
     * @param count number of orcs to spawn
     * @return true if successful
     */
    private boolean spawnOrc(int count) {
        for (int i = 0; i < count; i++) {
            Vector2 spawn = getRandSpawnPointOnMap();
            Entity orc = EnemyFactory.createPathfindingEnemy(EnemyType.ORC, spawn.x, spawn.y);
            engine.addEntity(orc);
        }
        Gdx.app.debug("[DEBUG] - EnemySpawnManager", "Orcs spawned " + count);
        return true;
    }

    /**
     * Spawns a given number of Goblins at random spawn points.
     *
     * @param count number of goblins to spawn
     * @return true if successful
     */
    private boolean spawnGoblin(int count) {
        for (int i = 0; i < count; i++) {
            Vector2 spawn = getRandSpawnPointOnMap();
            Entity goblin = EnemyFactory.createPathfindingEnemy(EnemyType.GOBLIN, spawn.x, spawn.y);
            engine.addEntity(goblin);
        }
        Gdx.app.debug("[DEBUG] - EnemySpawnManager", "Goblins spawned " + count);
        return true;
    }

    /**
     * Spawns a given number of Demons at random spawn points.
     *
     * @param count number of demons to spawn
     * @return true if successful
     */
    private boolean spawnDemon(int count) {
        for (int i = 0; i < count; i++) {
            Vector2 spawn = getRandSpawnPointOnMap();
            Entity demon = EnemyFactory.createPathfindingEnemy(EnemyType.DEMON, spawn.x, spawn.y);
            engine.addEntity(demon);
        }
        Gdx.app.debug("[DEBUG] - EnemySpawnManager", "Demons spawned " + count);
        return true;
    }

    /**
     * Calculates the number of enemies to spawn for the given round.
     * Uses an exponential curve with sinusoidal variation to simulate difficulty scaling.
     *
     * @param round the current round
     * @return the calculated number of enemies
     */
    private double calculateEnemyCount(int round) {
        float offset = 4.55F;
        float base = 2.5F; //b
        double x = round;
        return (
            exponentialStretcher * Math.pow(base, x * exponentialFlatOut) +
                difficultyVarianceDuringRound * Math.sin(x + offset) +
                enemyStartingAmount
        );
    }

    /**
     * Determines the enemy type distribution (Orc, Goblin, Demon) for the current round.
     *
     * @param round the current round
     */
    private void calculateEnemyDistribution(int round) {
        if (round <= phaseOne) {
            enemyDistribution[0] = 1;
            enemyDistribution[1] = 0;
            enemyDistribution[2] = 0;
        } else if (round <= phaseTwo) {
            enemyDistribution[0] = 0.9F;
            enemyDistribution[1] = 0.1F;
            enemyDistribution[2] = 0;
        } else if (round <= phaseThree) {
            enemyDistribution[0] = 0.7F;
            enemyDistribution[1] = 0.15F;
            enemyDistribution[2] = 0.15F;
        } else {
            enemyDistribution[0] = 0.6F;
            enemyDistribution[1] = 0.2F;
            enemyDistribution[2] = 0.2F;
        }
    }

    /**
     * Returns a random spawn point from the initialized spawn points.
     *
     * @return a random spawn point
     * @throws IllegalStateException if no spawn points are available
     */
    private Vector2 getRandSpawnPointOnMap() {
        if (spawnPoints == null || spawnPoints.isEmpty()) {
            throw new IllegalStateException("No spawn points available");
        }
        int randomIndex = (int) (Math.random() * spawnPoints.size());
        return spawnPoints.get(randomIndex);
    }
}
