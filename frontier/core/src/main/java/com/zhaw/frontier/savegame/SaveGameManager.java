package com.zhaw.frontier.savegame;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.zhaw.frontier.components.AttackComponent;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.entityFactories.TowerFactory;

import java.util.ArrayList;
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

    public void loadGame(String filePath) {
        FileHandle file = Gdx.files.local(filePath.endsWith(".json") ? filePath : filePath + ".json");

        if (!file.exists()) {
            Gdx.app.log("LoadGame", "No save file found at: " + file.file().getAbsolutePath());
            return;
        }

        GameState gameState = json.fromJson(GameState.class, file.readString());

        for (Map<String, Object> data : gameState.entities) {
            String type = (String) data.get("type");

            Entity entity;

            switch (type) {
                case "TOWER":
                case "BUILDING":
                    entity = TowerFactory.createDefaultTower(engine);
                    break;
                case "ENEMY":
                case "OBJECT":
                case "UNKNOWN":
                default:
                    entity = engine.createEntity();
                    entity.add(new PositionComponent());
                    break;
            }

            if (data.containsKey("x") && data.containsKey("y")) {
                PositionComponent pos = entity.getComponent(PositionComponent.class);
                if (pos != null) {
                    pos.position.x = ((Number) data.get("x")).floatValue();
                    pos.position.y = ((Number) data.get("y")).floatValue();
                }
            }

            if (data.containsKey("health")) {
                HealthComponent health = entity.getComponent(HealthComponent.class);
                if (health != null) {
                    health.Health = ((Number) data.get("health")).intValue();
                }
            }

            if (data.containsKey("damage") || data.containsKey("range") || data.containsKey("speed")) {
                AttackComponent attack = entity.getComponent(AttackComponent.class);
                if (attack != null) {
                    if (data.containsKey("damage")) {
                        attack.AttackDamage = ((Number) data.get("damage")).floatValue();
                    }
                    if (data.containsKey("range")) {
                        attack.AttackRange = ((Number) data.get("range")).floatValue();
                    }
                    if (data.containsKey("speed")) {
                        attack.AttackSpeed = ((Number) data.get("speed")).floatValue();
                    }
                }
            }

            if (data.containsKey("resource")) {
                RenderComponent render = entity.getComponent(RenderComponent.class);
                if (render != null) {
                    Gdx.app.log("LoadGame", "Note: Resource info available, but reloading textures is not implemented.");
                }
            }

            engine.addEntity(entity);
        }

        Gdx.app.log("LoadGame", "Loaded " + gameState.entities.size() + " entities from " + file.file().getAbsolutePath());
    }

    public void saveGame(String filePath) {
        GameState gameState = new GameState();

        ImmutableArray<Entity> allEntities = engine.getEntitiesFor(Family.all().get());

        for (Entity entity : allEntities) {
            Map<String, Object> entityData = new HashMap<>();

            PositionComponent pos = entity.getComponent(PositionComponent.class);
            if (pos != null) {
                entityData.put("x", pos.position.x);
                entityData.put("y", pos.position.y);
            }

            HealthComponent health = entity.getComponent(HealthComponent.class);
            if (health != null) {
                entityData.put("health", health.Health);
            }

            AttackComponent attack = entity.getComponent(AttackComponent.class);
            if (attack != null) {
                entityData.put("damage", attack.AttackDamage);
                entityData.put("range", attack.AttackRange);
                entityData.put("speed", attack.AttackSpeed);
            }

            RenderComponent render = entity.getComponent(RenderComponent.class);
            if (render != null) {
                if (render.sprite != null && render.sprite.getTexture() != null) {
                    entityData.put("resource", render.sprite.getTexture().toString());
                }
                if (render.renderType != null) {
                    entityData.put("renderType", render.renderType.name());
                }
            }

            entityData.put("type", inferEntityType(entity));

            gameState.entities.add(entityData);
        }

        FileHandle file = Gdx.files.local(filePath.endsWith(".json") ? filePath : filePath + ".json");
        file.writeString(json.toJson(gameState), false);

        Gdx.app.log("SaveGame", "Saved " + gameState.entities.size() + " entities to " + file.file().getAbsolutePath());
    }

    private String inferEntityType(Entity entity) {
        RenderComponent render = entity.getComponent(RenderComponent.class);
        if (render != null && render.renderType != null) {
            return render.renderType.name();
        }
        if (entity.getComponent(AttackComponent.class) != null) {
            return "TOWER";
        }
        if (entity.getComponent(HealthComponent.class) != null && entity.getComponent(PositionComponent.class) != null) {
            return "OBJECT";
        }
        return "UNKNOWN";
    }
}

