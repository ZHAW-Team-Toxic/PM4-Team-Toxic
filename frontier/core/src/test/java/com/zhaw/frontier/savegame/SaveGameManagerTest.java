package com.zhaw.frontier.savegame;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import com.zhaw.frontier.entityFactories.HQFactory;
import com.zhaw.frontier.entityFactories.ResourceBuildingFactory;
import com.zhaw.frontier.entityFactories.TowerFactory;
import com.zhaw.frontier.entityFactories.WallFactory;
import com.zhaw.frontier.screens.LoadingScreen;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SaveGameManagerTest {

    private Engine engine;
    private SaveGameManager saveGameManager;

    @BeforeAll
    public void initGdx() {
        // Start dummy application context
        new HeadlessApplication(
            new ApplicationListener() {
                @Override
                public void create() {}

                @Override
                public void resize(int width, int height) {}

                @Override
                public void render() {}

                @Override
                public void pause() {}

                @Override
                public void resume() {}

                @Override
                public void dispose() {}
            },
            new HeadlessApplicationConfiguration()
        );

        // Mock GL20 (used internally by Texture)
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = Gdx.gl;

        // Mock Gdx.app for logging
        Gdx.app = mock(Application.class);

        // Mock graphics
        Gdx.graphics = mock(Graphics.class);

        // Mock FrontierGame
        SpriteBatchInterface dummyBatch = mock(SpriteBatchInterface.class);
        FrontierGame mockGame = mock(FrontierGame.class);
        when(mockGame.getBatch()).thenReturn(dummyBatch);

        // Use loadingScreen, to load assets
        LoadingScreen loadingScreen = new LoadingScreen(mockGame);
        loadingScreen.show();

        AssetManagerInstance.getManager().finishLoading();
    }

    @BeforeEach
    public void setUp() {
        engine = new Engine();
        saveGameManager = new SaveGameManager(engine);
    }

    @AfterEach
    public void cleanup() {
        engine.getEntities().forEach(engine::removeEntity);
    }

    @Test
    public void testLoadNonexistentFile() {
        FileHandle file = Gdx.files.external("frontier/saves/does-not-exist.json");
        if (file.exists()) {
            file.delete();
        }

        saveGameManager.loadGame("does-not-exist");

        ImmutableArray<Entity> entities = engine.getEntities();
        assertEquals(0, entities.size());
    }

    @Test
    public void testInventoryComponents() {
        Entity e = engine.createEntity();
        e.add(new EntityTypeComponent(EntityTypeComponent.EntityType.INVENTORY));
        e.add(new PositionComponent(0, 0, 1, 1));

        InventoryComponent inv = new InventoryComponent();
        inv.resources.put(ResourceTypeEnum.RESOURCE_TYPE_WOOD, 100);
        inv.resources.put(ResourceTypeEnum.RESOURCE_TYPE_IRON, 50);
        inv.resources.put(ResourceTypeEnum.RESOURCE_TYPE_STONE, 80);
        e.add(inv);

        engine.addEntity(e);
        saveAndReload("test-inventory");

        Entity loaded = getOnlyEntity();
        InventoryComponent loadedInv = loaded.getComponent(InventoryComponent.class);
        assertNotNull(loadedInv);
        assertEquals(100, loadedInv.resources.get(ResourceTypeEnum.RESOURCE_TYPE_WOOD));
        assertEquals(50, loadedInv.resources.get(ResourceTypeEnum.RESOURCE_TYPE_IRON));
        assertEquals(80, loadedInv.resources.get(ResourceTypeEnum.RESOURCE_TYPE_STONE));
    }

    @Test
    public void testUnknownEntityTypeIsIgnored() {
        String brokenJson =
            """
            {
              "entities": [
                {
                  "entityType": "UNKNOWN_THING",
                  "x": 0,
                  "y": 0
                }
              ]
            }
            """;

        FileHandle file = Gdx.files.external("frontier/saves/broken-entity.json");
        file.writeString(brokenJson, false);

        saveGameManager.loadGame("broken-entity");

        ImmutableArray<Entity> entities = engine.getEntities();
        assertEquals(0, entities.size(), "Entity with unknown type should be skipped");
    }

    @Test
    public void testTowerComponents() {
        Entity tower = TowerFactory.createBallistaTower(engine, 12, 34);

        HealthComponent health = tower.getComponent(HealthComponent.class);
        health.maxHealth = 100;
        health.currentHealth = 88;

        AttackComponent attack = tower.getComponent(AttackComponent.class);
        attack.AttackDamage = 25;
        attack.AttackRange = 6;
        attack.AttackSpeed = 1.1f;

        engine.addEntity(tower);

        saveAndReload("tower-full-roundtrip");

        Entity loaded = getOnlyEntity();

        PositionComponent loadedPos = loaded.getComponent(PositionComponent.class);
        assertNotNull(loadedPos);
        assertEquals(12f, loadedPos.basePosition.x, 0.001f);
        assertEquals(34f, loadedPos.basePosition.y, 0.001f);

        HealthComponent loadedHealth = loaded.getComponent(HealthComponent.class);
        assertNotNull(loadedHealth);
        assertEquals(100, loadedHealth.maxHealth);
        assertEquals(88, loadedHealth.currentHealth);

        AttackComponent loadedAttack = loaded.getComponent(AttackComponent.class);
        assertNotNull(loadedAttack);
        assertEquals(25, loadedAttack.AttackDamage);
        assertEquals(6, loadedAttack.AttackRange);
        assertEquals(1.1f, loadedAttack.AttackSpeed, 0.001f);

        EntityTypeComponent type = loaded.getComponent(EntityTypeComponent.class);
        assertNotNull(type);
        assertEquals(EntityTypeComponent.EntityType.BALLISTA_TOWER, type.type);
    }

    @Test
    public void testBallistaTower() {
        Entity entity = TowerFactory.createBallistaTower(engine, 2, 2);
        engine.addEntity(entity);
        saveAndReload("ballista-test");

        Entity loaded = getOnlyEntity();
        assertEquals(
            EntityTypeComponent.EntityType.BALLISTA_TOWER,
            loaded.getComponent(EntityTypeComponent.class).type
        );
    }

    @Test
    public void testSandClockHQ() {
        Entity e = HQFactory.createSandClockHQ(engine, 5, 5);
        engine.addEntity(e);
        saveAndReload("hq-test");

        Entity loaded = getOnlyEntity();
        assertEquals(
            EntityTypeComponent.EntityType.HQ,
            loaded.getComponent(EntityTypeComponent.class).type
        );
    }

    @Test
    public void testWoodWall() {
        Entity e = WallFactory.createWoodWall(engine, 1, 1);
        engine.addEntity(e);
        saveAndReload("wood-wall-test");

        Entity loaded = getOnlyEntity();
        assertEquals(
            EntityTypeComponent.EntityType.WOOD_WALL,
            loaded.getComponent(EntityTypeComponent.class).type
        );
    }

    @Test
    public void testStoneWall() {
        Entity e = WallFactory.createStoneWall(engine, 1, 2);
        engine.addEntity(e);
        saveAndReload("stone-wall-test");

        Entity loaded = getOnlyEntity();
        assertEquals(
            EntityTypeComponent.EntityType.STONE_WALL,
            loaded.getComponent(EntityTypeComponent.class).type
        );
    }

    @Test
    public void testIronWall() {
        Entity e = WallFactory.createIronWall(engine, 2, 2);
        engine.addEntity(e);
        saveAndReload("iron-wall-test");

        Entity loaded = getOnlyEntity();
        assertEquals(
            EntityTypeComponent.EntityType.IRON_WALL,
            loaded.getComponent(EntityTypeComponent.class).type
        );
    }

    @Test
    public void testWoodResourceBuilding() {
        Entity e = ResourceBuildingFactory.woodResourceBuilding(engine, 3, 3);
        engine.addEntity(e);
        saveAndReload("wood-resource-test");

        Entity loaded = getOnlyEntity();
        assertEquals(
            EntityTypeComponent.EntityType.RESOURCE_BUILDING,
            loaded.getComponent(EntityTypeComponent.class).type
        );
        ResourceProductionComponent prod = loaded.getComponent(ResourceProductionComponent.class);
        assertNotNull(prod);
        assertTrue(prod.productionRate.containsKey(ResourceTypeEnum.RESOURCE_TYPE_WOOD));
    }

    @Test
    public void testStoneResourceBuilding() {
        Entity e = ResourceBuildingFactory.stoneResourceBuilding(engine, 4, 4);
        engine.addEntity(e);
        saveAndReload("stone-resource-test");

        Entity loaded = getOnlyEntity();
        assertEquals(
            EntityTypeComponent.EntityType.RESOURCE_BUILDING,
            loaded.getComponent(EntityTypeComponent.class).type
        );
        ResourceProductionComponent prod = loaded.getComponent(ResourceProductionComponent.class);
        assertNotNull(prod);
        assertTrue(prod.productionRate.containsKey(ResourceTypeEnum.RESOURCE_TYPE_STONE));
    }

    @Test
    public void testIronResourceBuilding() {
        Entity e = ResourceBuildingFactory.ironResourceBuilding(engine, 6, 6);
        engine.addEntity(e);
        saveAndReload("iron-resource-test");

        Entity loaded = getOnlyEntity();
        assertEquals(
            EntityTypeComponent.EntityType.RESOURCE_BUILDING,
            loaded.getComponent(EntityTypeComponent.class).type
        );
        ResourceProductionComponent prod = loaded.getComponent(ResourceProductionComponent.class);
        assertNotNull(prod);
        assertTrue(prod.productionRate.containsKey(ResourceTypeEnum.RESOURCE_TYPE_IRON));
    }

    private void saveAndReload(String fileName) {
        saveGameManager.saveGame(fileName);
        engine.getEntities().forEach(engine::removeEntity);
        saveGameManager.loadGame(fileName);
    }

    private Entity getOnlyEntity() {
        var entities = engine.getEntities();
        assertEquals(1, entities.size());
        return entities.first();
    }
}
