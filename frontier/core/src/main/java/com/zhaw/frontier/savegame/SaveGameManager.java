package com.zhaw.frontier.savegame;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import com.zhaw.frontier.entityFactories.EnemyFactory;
import com.zhaw.frontier.entityFactories.ResourceBuildingFactory;
import com.zhaw.frontier.entityFactories.TowerFactory;
import com.zhaw.frontier.entityFactories.WallFactory;

import java.util.HashMap;
import java.util.Map;

public class SaveGameManager {

    private final Engine engine;
    private final AssetManager assetManager;
    private final Json json;

    public SaveGameManager(Engine engine, AssetManager assetManager) {
        this.engine = engine;
        this.assetManager = assetManager;

        json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);
        json.setTypeName(null);
        json.setIgnoreUnknownFields(true);
    }

    public void saveGame(String filePath) {
        GameState gameState = new GameState();
        ImmutableArray<Entity> allEntities = engine.getEntitiesFor(Family.all().get());

        for (Entity entity : allEntities) {
            EntityData data = new EntityData();

            EntityTypeComponent type = entity.getComponent(EntityTypeComponent.class);
            if (type != null) {
                data.entityType = type.type.name();
            } else {
                continue;
            }

            PositionComponent pos = entity.getComponent(PositionComponent.class);
            if (pos != null) {
                data.x = pos.position.x;
                data.y = pos.position.y;
            }

            HealthComponent health = entity.getComponent(HealthComponent.class);
            if (health != null) {
                data.health = health.Health;
            }

            AttackComponent attack = entity.getComponent(AttackComponent.class);
            if (attack != null) {
                data.damage = attack.AttackDamage;
                data.range = attack.AttackRange;
                data.speed = attack.AttackSpeed;
            }

            InventoryComponent inventory = entity.getComponent(InventoryComponent.class);
            if (inventory != null) {
                data.inventory = new HashMap<>();
                for (Map.Entry<ResourceTypeEnum, Integer> entry : inventory.resources.entrySet()) {
                    data.inventory.put(entry.getKey().name(), entry.getValue());
                }
            }

            gameState.entities.add(data);
        }

        FileHandle saveDir = Gdx.files.external("frontier/saves/");
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        FileHandle file = Gdx.files.external("frontier/saves/" + (filePath.endsWith(".json") ? filePath : filePath + ".json"));

        file.writeString(json.toJson(gameState), false);

        Gdx.app.log(this.getClass().getSimpleName(), "Saved " + gameState.entities.size() + " entities to " + file.file().getAbsolutePath());
    }

    public void loadGame(String filePath) {
        FileHandle file = Gdx.files.external("frontier/saves/" + (filePath.endsWith(".json") ? filePath : filePath + ".json"));

        if (!file.exists()) {
            Gdx.app.log(this.getClass().getSimpleName(), "No save file found at: " + file.file().getAbsolutePath());
            return;
        }

        GameState gameState = json.fromJson(GameState.class, file.readString());

        for (EntityData data : gameState.entities) {
            Entity entity;

            EntityTypeComponent.EntityType entityType;
            try {
                entityType = EntityTypeComponent.EntityType.valueOf(data.entityType);
            } catch (Exception exeException) {
                Gdx.app.log(this.getClass().getSimpleName(), "Unknown entity type: " + data.entityType);
                continue;
            }

            entity = switch (entityType) {
                case WALL -> WallFactory.createDefaultWall(engine);
                case TOWER -> TowerFactory.createDefaultTower(engine);
                case WOOD_RESOURCE_BUILDING -> ResourceBuildingFactory.woodResourceBuilding(engine);
                case IDLE_ENEMY -> EnemyFactory.createIdleEnemy(data.x, data.y, assetManager);
                case PATROL_ENEMY -> EnemyFactory.createPatrolEnemy(data.x, data.y, assetManager);
                default -> engine.createEntity();
            };

            if (data.x != null && data.y != null) {
                PositionComponent pos = entity.getComponent(PositionComponent.class);
                if (pos != null) {
                    pos.position.set(data.x, data.y);
                }
            }

            if (data.health != null) {
                HealthComponent health = entity.getComponent(HealthComponent.class);
                if (health != null) {
                    health.Health = data.health;
                }
            }

            if (data.damage != null || data.range != null || data.speed != null) {
                AttackComponent attack = entity.getComponent(AttackComponent.class);
                if (attack != null) {
                    if (data.damage != null) attack.AttackDamage = data.damage;
                    if (data.range != null) attack.AttackRange = data.range;
                    if (data.speed != null) attack.AttackSpeed = data.speed;
                }
            }

            if (entityType == EntityTypeComponent.EntityType.INVENTORY && data.inventory != null) {
                InventoryComponent inventory = new InventoryComponent();
                for (Map.Entry<String, Integer> entry : data.inventory.entrySet()) {
                    try {
                        ResourceTypeEnum type = ResourceTypeEnum.valueOf(entry.getKey());
                        inventory.resources.put(type, entry.getValue());
                    } catch (IllegalArgumentException e) {
                        Gdx.app.log(this.getClass().getSimpleName(), "Unknown resource type in inventory: " + entry.getKey());
                    }
                }
                entity.add(inventory);
            }

            engine.addEntity(entity);
        }

        Gdx.app.log(this.getClass().getSimpleName(), "Loaded " + gameState.entities.size() + " entities from " + file.file().getAbsolutePath());
    }
}
