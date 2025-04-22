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

/**
 * Handles saving and loading of the game state to and from JSON files.
 * Uses the Ashley engine to serialize and recreate entities with their components.
 */
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

    /***
     * Saves the current game state, including all entities and their relevant components,
     * to a JSON file at the specified file path.
     * @param filePath Name of the file to which the data is saved.
     */
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
                data.currentHealth = healthComponent.currentHealth;
                data.maxHealth = healthComponent.maxHealth;
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
                for (Map.Entry<
                    ResourceTypeEnum,
                    Integer
                > entry : inventoryComponent.resources.entrySet()) {
                    data.inventory.put(entry.getKey().name(), entry.getValue());
                }
            }

            // Saves the production information
            ResourceProductionComponent prodComponent = entity.getComponent(
                ResourceProductionComponent.class
            );
            if (prodComponent != null) {
                data.countOfAdjacentResources = prodComponent.countOfAdjacentResources;
                if (
                    prodComponent.productionRate != null && !prodComponent.productionRate.isEmpty()
                ) {
                    ResourceTypeEnum resourceType = prodComponent.productionRate
                        .keySet()
                        .iterator()
                        .next();
                    if (resourceType != null) {
                        data.resourceType = resourceType.name();
                    }
                }
            }

            WallPieceComponent wallPieceType = entity.getComponent(WallPieceComponent.class);
            if (wallPieceType != null) {
                data.wallPieceType = wallPieceType.currentWallPiece;
                Gdx.app.log(
                    this.getClass().getSimpleName(),
                    "Wall piece type: " + wallPieceType.currentWallPiece
                );
            }

            gameState.entities.add(data);
        }

        FileHandle saveDir = Gdx.files.external("frontier/saves/");
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        FileHandle file = Gdx.files.external(
            "frontier/saves/" + (filePath.endsWith(".json") ? filePath : filePath + ".json")
        );

        file.writeString(json.toJson(gameState), false);

        Gdx.app.log(
            this.getClass().getSimpleName(),
            "Saved " + gameState.entities.size() + " entities to " + file.file().getAbsolutePath()
        );
    }

    /**
     * Loads the game state from a JSON file at the specified file path.
     * Entities are recreated with their components if the data is valid.
     *
     * @param filePath Name of the file from which the data is loaded.
     */
    public void loadGame(String filePath) {
        FileHandle file = Gdx.files.external(
            "frontier/saves/" + (filePath.endsWith(".json") ? filePath : filePath + ".json")
        );

        if (!file.exists()) {
            Gdx.app.log(
                this.getClass().getSimpleName(),
                "No save file found at: " + file.file().getAbsolutePath()
            );
            return;
        }

        GameState gameState = json.fromJson(GameState.class, file.readString());

        for (EntityData data : gameState.entities) {
            EntityTypeComponent.EntityType entityType;
            try {
                entityType = EntityTypeComponent.EntityType.valueOf(data.entityType);
            } catch (Exception exeException) {
                Gdx.app.log(
                    this.getClass().getSimpleName(),
                    "Unknown entity type: " + data.entityType
                );
                continue;
            }

            Entity entity;
            switch (entityType) {
                case HQ -> {
                    if (data.x == null || data.y == null) continue;
                    entity = HQFactory.createSandClockHQ(engine, data.x, data.y);
                }
                case BALLISTA_TOWER -> {
                    if (data.x == null || data.y == null) continue;
                    entity = TowerFactory.createBallistaTower(engine, data.x, data.y);
                }
                case WOOD_WALL -> {
                    if (data.x == null || data.y == null) continue;
                    entity = WallFactory.createWoodWall(engine, data.x, data.y);
                }
                case STONE_WALL -> {
                    if (data.x == null || data.y == null) continue;
                    entity = WallFactory.createStoneWall(engine, data.x, data.y);
                }
                case IRON_WALL -> {
                    if (data.x == null || data.y == null) continue;
                    entity = WallFactory.createIronWall(engine, data.x, data.y);
                }
                case RESOURCE_BUILDING -> {
                    if (data.x == null || data.y == null) continue;
                    if (data.resourceType == null) continue;

                    ResourceTypeEnum resourceType;
                    try {
                        resourceType = ResourceTypeEnum.valueOf(data.resourceType);
                    } catch (Exception exeException) {
                        Gdx.app.log(
                            this.getClass().getSimpleName(),
                            "Unknown resource type: " + data.resourceType
                        );
                        continue;
                    }

                    entity =
                    switch (resourceType) {
                        case RESOURCE_TYPE_WOOD -> ResourceBuildingFactory.woodResourceBuilding(
                            engine,
                            data.x,
                            data.y
                        );
                        case RESOURCE_TYPE_STONE -> ResourceBuildingFactory.stoneResourceBuilding(
                            engine,
                            data.x,
                            data.y
                        );
                        case RESOURCE_TYPE_IRON -> ResourceBuildingFactory.ironResourceBuilding(
                            engine,
                            data.x,
                            data.y
                        );
                    };
                }
                default -> entity = engine.createEntity();
            }

            if (data.x != null && data.y != null) {
                PositionComponent pos = entity.getComponent(PositionComponent.class);
                if (pos != null) {
                    pos.basePosition.set(data.x, data.y);
                }
            }

            if (data.maxHealth != null) {
                HealthComponent health = entity.getComponent(HealthComponent.class);
                if (health != null) {
                    health.maxHealth = data.maxHealth;
                    health.currentHealth = data.maxHealth;
                }
            }

            if (data.currentHealth != null) {
                HealthComponent health = entity.getComponent(HealthComponent.class);
                if (health != null) {
                    health.currentHealth = data.currentHealth;
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
                        Gdx.app.log(
                            this.getClass().getSimpleName(),
                            "Unknown resource type in inventory: " + entry.getKey()
                        );
                    }
                }
                entity.add(inventory);
            }

            ResourceProductionComponent prodComponent = entity.getComponent(
                ResourceProductionComponent.class
            );
            if (prodComponent != null && data.countOfAdjacentResources != null) {
                prodComponent.countOfAdjacentResources = data.countOfAdjacentResources;
            }

            WallPieceComponent wallPieceType = entity.getComponent(WallPieceComponent.class);
            RenderComponent renderComponent = entity.getComponent(RenderComponent.class);

            if (wallPieceType != null && data.wallPieceType != null) {
                wallPieceType.currentWallPiece = data.wallPieceType;
                renderComponent.sprites =
                wallPieceType.wallPieceTextures.get(wallPieceType.currentWallPiece);
            }

            engine.addEntity(entity);
        }

        Gdx.app.log(
            this.getClass().getSimpleName(),
            "Loaded " +
            gameState.entities.size() +
            " entities from " +
            file.file().getAbsolutePath()
        );
    }
}
