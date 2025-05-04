package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.entityFactories.EnemyFactory;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class EnemySpawnSystem extends IteratingSystem {

    @Getter
    private static EnemySpawnSystem instance;

    private final Engine engine;
    private final TiledMapTileLayer bottomLayer;

    private final List<Vector2> spawnPoints = new Vector<>();

    //Base function looks like this f(x)=25*2.5^(x*0.1)+5 sin(x+4.55)+40
    private final float exponentialStretcher = 25F;
    private final float exponentialFlatOut = 0.1F;
    private final float difficultyVarianceDuringRound = 5F;
    private final float enemyStartingAmount = 40; //so it starts actually at 60

    private final int phaseOne = 5;
    private final int phaseTwo = 8;
    private final int phaseThree = 12;

    private final float[] enemyDistribution = new float[3];

    private EnemySpawnSystem(Engine engine) {
        super(Family.all().get());
        this.engine = engine;
        this.bottomLayer = engine.getEntitiesFor(Family.all(BottomLayerComponent.class).get())
            .first().getComponent(BottomLayerComponent.class).bottomLayer;

        if (!initSpawnPoints()) {
            throw new RuntimeException("Failed to initialize spawn points");
        }

        Collections.shuffle(spawnPoints);

        Gdx.app.debug(
            "[DEBUG] - EnemySpawnManager",
            "Spawn points initialized: " + spawnPoints.size()
        );
    }

    public static EnemySpawnSystem create(Engine engine) {
        if (instance == null) {
            instance = new EnemySpawnSystem(engine);
            engine.addSystem(instance);
        }
        return instance;
    }

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
            "Enemy distribution for round " + round + ": " +
                "Orc: " + orcCount +
                ", Goblin: " + goblinCount +
                ", Demon: " + demonCount
        );

        // Spawn enemies based on the calculated counts
        if (!spawnOrc(orcCount)) {
            return false;
        }
         if(!spawnGoblin(goblinCount)){
         return false;
         }
         if(!spawnDemon(demonCount)){
         return false;
         }
        return true;
    }

    @Override
    protected void processEntity(Entity entity, float v) {

    }

    private boolean initSpawnPoints() {
        for (int i = 0; i < bottomLayer.getWidth(); i++) {
            for (int j = 0; j < bottomLayer.getHeight(); j++) {
                TiledMapTile tile = bottomLayer.getCell(i, j).getTile();
                if (tile != null && tile.getProperties().containsKey("isSpawnPoint") &&
                    tile.getProperties().get("isSpawnPoint", Boolean.class)) {
                    spawnPoints.add(new Vector2(i, j));
                }
            }
        }
        return true;
    }

    private boolean spawnOrc(int count) {
        for (int i = 0; i < count; i++) {
            Vector2 spawnPointOnMap = getRandSpawnPointOnMap();
            Entity orc = EnemyFactory.createIdleEnemy(
                spawnPointOnMap.x,
                spawnPointOnMap.y
            );
            engine.addEntity(orc);
        }
        Gdx.app.debug(
            "[DEBUG] - EnemySpawnManager",
            "Orcs spawned " + count
        );
        return true;
    }

    private boolean spawnGoblin(int count) {
        for (int i = 0; i < count; i++) {
            Vector2 spawnPointOnMap = getRandSpawnPointOnMap();
            Entity goblin = EnemyFactory.createIdleEnemy(
                spawnPointOnMap.x,
                spawnPointOnMap.y
            );
            engine.addEntity(goblin);
        }
        Gdx.app.debug(
            "[DEBUG] - EnemySpawnManager",
            "Goblins spawned " + count
        );
        return true;
    }

    private boolean spawnDemon(int count) {
        for (int i = 0; i < count; i++) {
            Vector2 spawnPointOnMap = getRandSpawnPointOnMap();
            Entity demon = EnemyFactory.createIdleEnemy(
                spawnPointOnMap.x,
                spawnPointOnMap.y
            );
            engine.addEntity(demon);
        }
        Gdx.app.debug(
            "[DEBUG] - EnemySpawnManager",
            "Demons spawned " + count
        );
        return true;
    }

    private double calculateEnemyCount(int round) {
        float makeFunctionStartAtALowPoint = 4.55F;
        float exponentialIncrease = 2.5F;
        double x = round;
        return exponentialStretcher * Math.pow(exponentialIncrease, x * exponentialFlatOut) +
            difficultyVarianceDuringRound * Math.sin(x + makeFunctionStartAtALowPoint) + enemyStartingAmount;
    }

    private void calculateEnemyDistribution(int round) {

        if (round <= phaseOne) {
            enemyDistribution[0] = 1;
            enemyDistribution[1] = 0;
            enemyDistribution[2] = 0;
        }
        if (phaseOne < round && round <= phaseTwo) {
            enemyDistribution[0] = 0.9F;
            enemyDistribution[1] = 0.1F;
            enemyDistribution[2] = 0;
        }
        if (phaseTwo < round && round <= phaseThree) {
            enemyDistribution[0] = 0.7F;
            enemyDistribution[1] = 0.15F;
            enemyDistribution[2] = 0.15F;
        }
        if(round > phaseThree){
            enemyDistribution[0] = 0.6F;
            enemyDistribution[1] = 0.2F;
            enemyDistribution[2] = 0.2F;
        }

    }

    private Vector2 getRandSpawnPointOnMap() {
        if (spawnPoints == null || spawnPoints.isEmpty()) {
            throw new IllegalStateException("No spawn points available");
        }
        int randomIndex = (int) (Math.random() * spawnPoints.size());
        return spawnPoints.get(randomIndex);
    }
}
