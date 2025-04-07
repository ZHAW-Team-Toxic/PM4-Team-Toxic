package com.zhaw.frontier;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.zhaw.frontier.components.OccupiesTilesComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.entityFactories.HQFactory;
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

    /**
     * Tests that a tower can be placed on a buildable tile.
     */
    @Test
    public void testBuildingPlacementOnBuildableTile() {
        Entity tower = TowerFactory.createDefaultTower(testEngine);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(5);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(5);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(bms.placeBuilding(tower), "Building should be placed on buildable tile.");
    }

    /**
     * Tests that a building is not placed on a non-buildable tile (e.g. water).
     */
    @Test
    public void testBuildingNotPlacedOnNonBuildableTile() {
        Entity tower = TowerFactory.createDefaultTower(testEngine);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(2);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(2);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(
            bms.placeBuilding(tower),
            "Building should not be placed on non-buildable tile."
        );
    }

    /**
     * Tests that a building is not placed on a resource tile (e.g. stone/wood/iron).
     */
    @Test
    public void testBuildingNotPlacedOnResourceTile() {
        Entity tower = TowerFactory.createDefaultTower(testEngine);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(0);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(bms.placeBuilding(tower), "Building should not be placed on resource tile.");
    }

    /**
     * Tests that a building cannot be placed on an already occupied tile.
     */
    @Test
    public void testBuildingNotPlacedOnOccupiedTile() {
        Entity tower = TowerFactory.createDefaultTower(testEngine);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(bms.placeBuilding(tower), "Building should be placed on buildable tile.");

        Entity tower2 = TowerFactory.createDefaultTower(testEngine);
        PositionComponent bp2 = tower2.getComponent(PositionComponent.class);
        bp2.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp2.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        assertFalse(bms.placeBuilding(tower2), "Building should not be placed on occupied tile.");
    }

    /**
     * Tests that a valid resource-producing building is placed correctly when adjacent resource tiles exist.
     */
    @Test
    public void testResourceBuildingPlacementWorks() {
        Entity resourceBuilding = ResourceBuildingFactory.stoneResourceBuilding(testEngine);
        PositionComponent bp = resourceBuilding.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(2);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(
            bms.placeBuilding(resourceBuilding),
            "Building should be placed on buildable tile."
        );
    }

    /**
     * Tests that a resource-producing building is rejected if no matching adjacent resource tiles exist.
     */
    @Test
    public void checkResourceBuildingNotPlaced() {
        Entity resourceBuilding = ResourceBuildingFactory.stoneResourceBuilding(testEngine);
        PositionComponent bp = resourceBuilding.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(
            bms.placeBuilding(resourceBuilding),
            "Building should not be placed without adjacent resource tiles."
        );
    }

    @Test
    public void checkMultiTIleBuildingPlacement() {
        Entity hq = HQFactory.createSandClockHQ(testEngine, null);
        PositionComponent bp = hq.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(3);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(3);
        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(bms.placeBuilding(hq), "Building should be placed on buildable tile.");
        OccupiesTilesComponent occupiesTilesComponent = hq.getComponent(
            OccupiesTilesComponent.class
        );
        assertEquals(16, occupiesTilesComponent.occupiedTiles.size(), "Tiles should be occupied.");
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
