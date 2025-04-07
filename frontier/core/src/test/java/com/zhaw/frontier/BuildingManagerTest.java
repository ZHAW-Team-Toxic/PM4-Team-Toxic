package com.zhaw.frontier;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.zhaw.frontier.components.OccupiesTilesComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.ResourceGeneratorComponent;
import com.zhaw.frontier.components.ResourceProductionComponent;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import com.zhaw.frontier.entityFactories.ResourceBuildingFactory;
import com.zhaw.frontier.entityFactories.TowerFactory;
import com.zhaw.frontier.systems.BuildingManagerSystem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Integration tests for verifying the functionality of the {@link BuildingManagerSystem}.
 *
 * <p>This test class covers various building placement scenarios:
 * <ul>
 *     <li>Valid placement on buildable tiles</li>
 *     <li>Rejection of placement on non-buildable tiles</li>
 *     <li>Rejection on resource tiles</li>
 *     <li>Prevention of overlapping placements</li>
 *     <li>Special validation logic for resource buildings</li>
 * </ul>
 * </p>
 *
 * <p>
 * The test map and game environment are initialized using {@link TestMapEnvironment}, which sets up
 * a fixed 9x9 tile TMX map with predefined tile properties (buildable, resource, blocked, etc.).
 * </p>
 *
 * @see BuildingManagerSystem
 * @see TowerFactory
 * @see ResourceBuildingFactory
 * @see TestMapEnvironment
 */
@ExtendWith(GdxExtension.class)
public class BuildingManagerTest {

    private static Engine testEngine;
    private static ExtendViewport gameWorldView;
    private static TestMapEnvironment testMapEnvironment;

    /**
     * Sets up the test environment by initializing the test map, engine, and viewport.
     *
     * <p>Uses {@link TestMapEnvironment} to prepare the map and provide tile layer access.
     * Adds the {@link BuildingManagerSystem} to the test engine.
     * </p>
     */
    @BeforeAll
    public static void setUp() {
        testMapEnvironment = new TestMapEnvironment();
        testEngine = testMapEnvironment.getTestEngine();
        gameWorldView = testMapEnvironment.getGameWorldView();

        addSystemsUnderTestHere();
    }

    /**
     * Adds the system under test to the engine: {@link BuildingManagerSystem}.
     */
    private static void addSystemsUnderTestHere() {
        testEngine.addSystem(
            new BuildingManagerSystem(
                testMapEnvironment.getBottomLayer(),
                gameWorldView,
                testEngine
            )
        );
    }

    private Entity createMockedTower(int height, int width) {
        Entity tower = testEngine.createEntity();
        PositionComponent positionComponent = new PositionComponent();
        positionComponent.heightInTiles = height;
        positionComponent.widthInTiles = width;
        OccupiesTilesComponent occupiesTilesComponent = new OccupiesTilesComponent();
        tower.add(occupiesTilesComponent);
        tower.add(positionComponent);
        return tower;
    }

    private Entity createMockedResourceBuilding(int height, int width, ResourceTypeEnum type) {
        Entity resourceBuilding = testEngine.createEntity();
        PositionComponent positionComponent = new PositionComponent();
        positionComponent.heightInTiles = height;
        positionComponent.widthInTiles = width;
        OccupiesTilesComponent occupiesTilesComponent = new OccupiesTilesComponent();
        resourceBuilding.add(occupiesTilesComponent);
        ResourceProductionComponent resourceProductionComponent = new ResourceProductionComponent();
        resourceProductionComponent.productionRate.put(type, 1);
        ResourceGeneratorComponent resourceGeneratorComponent = new ResourceGeneratorComponent();
        resourceBuilding.add(positionComponent);
        resourceBuilding.add(resourceProductionComponent);
        resourceBuilding.add(resourceGeneratorComponent);
        return resourceBuilding;
    }

    private Entity createMockedHQ(int height, int width) {
        Entity hq = testEngine.createEntity();
        PositionComponent positionComponent = new PositionComponent();
        positionComponent.heightInTiles = height;
        positionComponent.widthInTiles = width;
        OccupiesTilesComponent occupiesTilesComponent = new OccupiesTilesComponent();
        hq.add(positionComponent);
        hq.add(occupiesTilesComponent);
        return hq;
    }

    /**
     * Tests that a tower can be placed on a buildable tile.
     */
    @Test
    public void testBuildingPlacementOnBuildableTile1x1() {
        Entity tower = createMockedTower(1, 1);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(5);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(5);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(bms.placeBuilding(tower), "Building should be placed on buildable tile.");
        testEngine.removeEntity(tower);
    }

    @Test
    public void testBuildingPlacementOnBuildableTile2x2() {
        Entity tower = createMockedTower(2, 2);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(3);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(3);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(bms.placeBuilding(tower), "Building should be placed on buildable tile.");
        testEngine.removeEntity(tower);
    }

    /**
     * Tests that a building is not placed on a non-buildable tile (e.g. water).
     */
    @Test
    public void testBuildingNotPlacedOnNonBuildableTile1x1() {
        Entity tower = createMockedTower(1, 1);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(2);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(2);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(
            bms.placeBuilding(tower),
            "Building should not be placed on non-buildable tile."
        );
        testEngine.removeEntity(tower);
    }

    @Test
    public void testBuildingNotPlacedOnNonBuildableTile2x2() {
        Entity tower = createMockedTower(2, 2);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(2);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(2);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(
            bms.placeBuilding(tower),
            "Building should not be placed on non-buildable tile."
        );
        testEngine.removeEntity(tower);
    }

    /**
     * Tests that a building is not placed on a resource tile (e.g. stone/wood/iron).
     */
    @Test
    public void testBuildingNotPlacedOnResourceTile1x1() {
        Entity tower = createMockedTower(1, 1);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(0);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(bms.placeBuilding(tower), "Building should not be placed on resource tile.");
        testEngine.removeEntity(tower);
    }

    @Test
    public void testBuildingNotPlacedOnResourceTile2x2() {
        Entity tower = createMockedTower(2, 2);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(0);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(bms.placeBuilding(tower), "Building should not be placed on resource tile.");
        testEngine.removeEntity(tower);
    }

    /**
     * Tests that a building cannot be placed on an already occupied tile.
     */
    @Test
    public void testBuildingNotPlacedOnOccupiedTile1x1VS1x1() {
        Entity tower = createMockedTower(1, 1);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(bms.placeBuilding(tower), "Building should be placed on buildable tile.");

        Entity tower2 = createMockedTower(1, 1);
        PositionComponent bp2 = tower2.getComponent(PositionComponent.class);
        bp2.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp2.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        assertFalse(bms.placeBuilding(tower2), "Building should not be placed on occupied tile.");
        testEngine.removeEntity(tower);
    }

    @Test
    public void testBuildingNotPlacedOnOccupiedTile1x1VS2x2() {
        Entity tower = createMockedTower(1, 1);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(bms.placeBuilding(tower), "Building should be placed on buildable tile.");

        Entity tower2 = createMockedTower(2, 2);
        PositionComponent bp2 = tower2.getComponent(PositionComponent.class);
        bp2.basePosition.x = TestMapEnvironment.tileToScreenX(3);
        bp2.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        assertFalse(bms.placeBuilding(tower2), "Building should not be placed on occupied tile.");
        testEngine.removeEntity(tower);
    }

    /**
     * Tests that a valid resource-producing building is placed correctly when adjacent resource tiles exist.
     * The resource building is from type "stone".
     */
    @Test
    public void testResourceBuildingPlacementWorks1x1() {
        Entity resourceBuilding = createMockedResourceBuilding(
            1,
            1,
            ResourceTypeEnum.RESOURCE_TYPE_STONE
        );
        PositionComponent bp = resourceBuilding.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(2);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(
            bms.placeBuilding(resourceBuilding),
            "Building should be placed on buildable tile."
        );
        testEngine.removeEntity(resourceBuilding);
    }

    @Test
    public void testResourceBuildingPlacementWorks2x2() {
        Entity resourceBuilding = createMockedResourceBuilding(
            2,
            2,
            ResourceTypeEnum.RESOURCE_TYPE_STONE
        );
        PositionComponent bp = resourceBuilding.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(2);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(3);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);

        bms.placeBuilding(resourceBuilding);

        ResourceProductionComponent resourceProductionComponent = resourceBuilding.getComponent(
            ResourceProductionComponent.class
        );

        assertEquals(
            1,
            resourceProductionComponent.countOfAdjacentResources,
            "Building should have adjacent resources."
        );

        OccupiesTilesComponent occupiesTilesComponent = resourceBuilding.getComponent(
            OccupiesTilesComponent.class
        );
        assertEquals(4, occupiesTilesComponent.occupiedTiles.size(), "Tiles should be occupied.");
        testEngine.removeEntity(resourceBuilding);
    }

    /**
     * Tests that a resource-producing building is rejected if no matching adjacent resource tiles exist.
     * The resource building is from type "wood".
     */
    @Test
    public void checkResourceBuildingNotPlaced1x1() {
        Entity resourceBuilding = createMockedResourceBuilding(
            1,
            1,
            ResourceTypeEnum.RESOURCE_TYPE_WOOD
        );
        PositionComponent bp = resourceBuilding.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(
            bms.placeBuilding(resourceBuilding),
            "Building should not be placed without adjacent resource tiles."
        );
        testEngine.removeEntity(resourceBuilding);
    }

    @Test
    public void checkResourceBuildingNotPlaced2x2() {
        Entity resourceBuilding = createMockedResourceBuilding(
            2,
            2,
            ResourceTypeEnum.RESOURCE_TYPE_WOOD
        );
        PositionComponent bp = resourceBuilding.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(
            bms.placeBuilding(resourceBuilding),
            "Building should not be placed without adjacent resource tiles."
        );
        testEngine.removeEntity(resourceBuilding);
    }

    @Test
    public void checkMultiTIleBuildingPlacement2x2() {
        Entity hq = createMockedHQ(2, 2);
        PositionComponent bp = hq.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);
        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(bms.placeBuilding(hq), "Building should be placed on buildable tile.");
        OccupiesTilesComponent occupiesTilesComponent = hq.getComponent(
            OccupiesTilesComponent.class
        );
        assertEquals(4, occupiesTilesComponent.occupiedTiles.size(), "Tiles should be occupied.");
        testEngine.removeEntity(hq);
    }

    @Test
    public void checkMultiTIleBuildingPlacement3x3() {
        Entity hq = createMockedHQ(3, 3);
        PositionComponent bp = hq.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);
        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(bms.placeBuilding(hq), "Building should be placed on buildable tile.");
        OccupiesTilesComponent occupiesTilesComponent = hq.getComponent(
            OccupiesTilesComponent.class
        );
        assertEquals(9, occupiesTilesComponent.occupiedTiles.size(), "Tiles should be occupied.");
        testEngine.removeEntity(hq);
    }

    /**
     * Cleans up the test environment by removing all entities and disposing of map resources.
     */
    @AfterAll
    public static void tearDown() {
        testEngine.removeAllEntities();
        testMapEnvironment.dispose();
    }
}
