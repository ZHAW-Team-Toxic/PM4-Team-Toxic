package com.zhaw.frontier.savegame;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import com.zhaw.frontier.entityFactories.*;

import java.util.HashMap;
import java.util.Map;

public class SaveGameManager {

    private final Engine engine;
    private final Json json;

    public SaveGameManager(Engine engine) {
        this.engine = engine;

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

            // Saves the entity type. Entities without EntityTypeComponent or EntityType will be discarded.
            EntityTypeComponent entityComponent = entity.getComponent(EntityTypeComponent.class);
            if (entityComponent != null && entityComponent.type != null) {
                data.entityType = entityComponent.type.name();
            } else {
                continue;
            }

            // Saves the position.
            PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
            if (positionComponent != null) {
                data.x = positionComponent.basePosition.x;
                data.y = positionComponent.basePosition.y;
            }

            // Saves the health information.
            HealthComponent healthComponent = entity.getComponent(HealthComponent.class);
            if (healthComponent != null) {
                data.health = healthComponent.Health;
            }

            // Saves the attack information.
            AttackComponent attackComponent = entity.getComponent(AttackComponent.class);
            if (attackComponent != null) {
                data.damage = attackComponent.AttackDamage;
                data.range = attackComponent.AttackRange;
                data.speed = attackComponent.AttackSpeed;
            }

            // Saves the inventory
            InventoryComponent inventoryComponent = entity.getComponent(InventoryComponent.class);
            if (inventoryComponent != null) {
                data.inventory = new HashMap<>();
                for (Map.Entry<ResourceTypeEnum, Integer> entry : inventoryComponent.resources.entrySet()) {
                    data.inventory.put(entry.getKey().name(), entry.getValue());
                }
            }

            // Saves the production information
            /*
            ResourceProductionComponent prodComponent = entity.getComponent(ResourceProductionComponent.class);
            if (prodComponent != null) {
                data.productionRate = new HashMap<>();
                for (Map.Entry<ResourceTypeEnum, Integer> entry : prodComponent.productionRate.entrySet()) {
                    data.productionRate.put(entry.getKey().name(), entry.getValue());
                }
                data.countOfAdjacentResources = prodComponent.countOfAdjacentResources;
            }
             */

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

            EntityTypeComponent.EntityType entityType;
            try {
                entityType = EntityTypeComponent.EntityType.valueOf(data.entityType);
            } catch (Exception exeException) {
                Gdx.app.log(this.getClass().getSimpleName(), "Unknown entity type: " + data.entityType);
                continue;
            }

            Entity entity;
            switch (entityType) {
                case HQ -> {
                    if (data.x == null || data.y == null) continue;
                    entity = HQFactory.createSandClockHQ(engine, 1, 1);
                }
                case BALLISTA_TOWER -> {
                    if (data.x == null || data.y == null) continue;
                    entity = TowerFactory.createBallistaTower(engine, 1, 1);
                }
                case WOOD_WALL -> {
                    if (data.x == null || data.y == null) continue;
                    entity = WallFactory.createWoodWall(engine, 1, 1);
                }
                case STONE_WALL -> {
                    if (data.x == null || data.y == null) continue;
                    entity = WallFactory.createStoneWall(engine, 1, 1);
                }
                case IRON_WALL -> {
                    if (data.x == null || data.y == null) continue;
                    entity = WallFactory.createIronWall(engine, 1, 1);
                }
                case RESOURCE_BUILDING -> {
                    if (data.x == null || data.y == null) continue;
                    entity = ResourceBuildingFactory.woodResourceBuilding(engine, 1, 1);
                }
                default -> entity = engine.createEntity();
            }

            if (data.x != null && data.y != null) {
                PositionComponent pos = entity.getComponent(PositionComponent.class);
                if (pos != null) {
                    pos.basePosition.set(data.x, data.y);
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
