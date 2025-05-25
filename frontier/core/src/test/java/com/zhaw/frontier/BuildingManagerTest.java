package com.zhaw.frontier;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.zhaw.frontier.components.InventoryComponent;
import com.zhaw.frontier.components.NonRemovalObjectComponent;
import com.zhaw.frontier.components.OccupiesTilesComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.ResourceGeneratorComponent;
import com.zhaw.frontier.components.ResourceProductionComponent;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import com.zhaw.frontier.systems.ErrorSystem;
import com.zhaw.frontier.systems.building.BuildingManagerSystem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Integration tests for verifying the building placement system handled by
 * {@link BuildingManagerSystem}.
 *
 * <p>
 * Tests cover:
 * <ul>
 * <li>Valid placements on buildable tiles</li>
 * <li>Invalid placements (non-buildable, overlapping, resource tiles)</li>
 * <li>Special rules for resource-producing buildings</li>
 * </ul>
 * </p>
 */
@ExtendWith(GdxExtension.class)
public class BuildingManagerTest {

    private static Engine testEngine;
    private static ExtendViewport gameWorldView;
    private static TestMapEnvironment testMapEnvironment;
    private static InventoryComponent inventory;

    /**
     * Sets up the test environment by initializing the test map, engine, and
     * viewport.
     *
     * <p>
     * Uses {@link TestMapEnvironment} to prepare the map and provide tile layer
     * access.
     * Adds the {@link BuildingManagerSystem} to the test engine.
     * </p>
     */
    @BeforeAll
    public static void setUp() {
        testMapEnvironment = new TestMapEnvironment();
        testEngine = testMapEnvironment.getTestEngine();
        gameWorldView = testMapEnvironment.getGameWorldView();
        inventory = new InventoryComponent();
        inventory.resources.put(ResourceTypeEnum.RESOURCE_TYPE_WOOD, 10);
        ErrorSystem.init(null, null);

        addSystemsUnderTestHere();

        assertEquals(1, testEngine.getEntities().size(), "Only the map entity should be present.");
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
     * Verifies that a 1x1 tower can be placed on a buildable tile.
     */
    @Test
    public void testBuildingPlacementOnBuildableTile1x1() {
        Entity tower = createMockedTower(1, 1);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(5);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(5);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(
            bms.placeBuilding(tower, inventory),
            "Building should be placed on buildable tile."
        );
        testEngine.removeEntity(tower);
    }

    /**
     * Verifies that a 2x2 tower can be placed on a buildable area.
     */
    @Test
    public void testBuildingPlacementOnBuildableTile2x2() {
        Entity tower = createMockedTower(2, 2);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(3);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(3);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(
            bms.placeBuilding(tower, inventory),
            "Building should be placed on buildable tile."
        );
        testEngine.removeEntity(tower);
    }

    /**
     * Ensures that placement on water or non-buildable terrain is rejected for a
     * 1x1 tower building.
     */
    @Test
    public void testBuildingNotPlacedOnNonBuildableTile1x1() {
        Entity tower = createMockedTower(1, 1);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(2);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(2);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(
            bms.placeBuilding(tower, inventory),
            "Building should not be placed on non-buildable tile."
        );
        testEngine.removeEntity(tower);
    }

    /**
     * Ensures that placement on water or non-buildable terrain is rejected for a
     * 2x2 tower building.
     */
    @Test
    public void testBuildingNotPlacedOnNonBuildableTile2x2() {
        Entity tower = createMockedTower(2, 2);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(2);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(2);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(
            bms.placeBuilding(tower, inventory),
            "Building should not be placed on non-buildable tile."
        );
        testEngine.removeEntity(tower);
    }

    /**
     * Ensures that resource tiles (e.g., stone, wood) block placement of
     * non-resource buildings for 1x1.
     */
    @Test
    public void testBuildingNotPlacedOnResourceTile1x1() {
        Entity tower = createMockedTower(1, 1);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(0);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(
            bms.placeBuilding(tower, inventory),
            "Building should not be placed on resource tile."
        );
        testEngine.removeEntity(tower);
    }

    /**
     * Ensures that resource tiles (e.g., stone, wood) block placement of
     * non-resource buildings for a 2x2.
     */
    @Test
    public void testBuildingNotPlacedOnResourceTile2x2() {
        Entity tower = createMockedTower(2, 2);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(0);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(
            bms.placeBuilding(tower, inventory),
            "Building should not be placed on resource tile."
        );
        testEngine.removeEntity(tower);
    }

    /**
     * Verifies that overlapping building placement is prevented.
     */
    @Test
    public void testBuildingNotPlacedOnOccupiedTile1x1VS1x1() {
        Entity tower = createMockedTower(1, 1);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(
            bms.placeBuilding(tower, inventory),
            "Building should be placed on buildable tile."
        );

        Entity tower2 = createMockedTower(1, 1);
        PositionComponent bp2 = tower2.getComponent(PositionComponent.class);
        bp2.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp2.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        assertFalse(
            bms.placeBuilding(tower2, inventory),
            "Building should not be placed on occupied tile."
        );
        testEngine.removeEntity(tower);
    }

    /**
     * Verifies that overlapping building placement is prevented.
     * Checks here if the occupying of tiles works correctly.
     */
    @Test
    public void testBuildingNotPlacedOnOccupiedTile1x1VS2x2() {
        Entity tower = createMockedTower(1, 1);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(
            bms.placeBuilding(tower, inventory),
            "Building should be placed on buildable tile."
        );

        Entity tower2 = createMockedTower(2, 2);
        PositionComponent bp2 = tower2.getComponent(PositionComponent.class);
        bp2.basePosition.x = TestMapEnvironment.tileToScreenX(3);
        bp2.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        assertFalse(
            bms.placeBuilding(tower2, inventory),
            "Building should not be placed on occupied tile."
        );
        testEngine.removeEntity(tower);
    }

    /**
     * Tests that a valid resource-producing building is placed correctly when
     * adjacent resource tiles exist.
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
            bms.placeBuilding(resourceBuilding, inventory),
            "Building should be placed on buildable tile."
        );
        testEngine.removeEntity(resourceBuilding);
    }

    /**
     * Tests that a valid resource-producing building is placed correctly when
     * adjacent resource tiles exist.
     * The resource building is from type "stone".
     */
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

        bms.placeBuilding(resourceBuilding, inventory);

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
     * Tests that a resource-producing building is rejected if no matching adjacent
     * resource tiles exist.
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
            bms.placeBuilding(resourceBuilding, inventory),
            "Building should not be placed without adjacent resource tiles."
        );
        testEngine.removeEntity(resourceBuilding);
    }

    /**
     * Tests that a resource-producing building is rejected if no matching adjacent
     * resource tiles exist.
     * The resource building is from type "wood" and it's a 2x2 building.
     */
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
            bms.placeBuilding(resourceBuilding, inventory),
            "Building should not be placed without adjacent resource tiles."
        );
        testEngine.removeEntity(resourceBuilding);
    }

    /**
     * Tests if a multiple building can be placed on a buildable tile.
     * The building is a 2x2 building.
     */
    @Test
    public void checkMultiTIleBuildingPlacement2x2() {
        Entity hq = createMockedHQ(2, 2);
        PositionComponent bp = hq.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);
        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(
            bms.placeBuilding(hq, inventory),
            "Building should be placed on buildable tile."
        );
        OccupiesTilesComponent occupiesTilesComponent = hq.getComponent(
            OccupiesTilesComponent.class
        );
        assertEquals(4, occupiesTilesComponent.occupiedTiles.size(), "Tiles should be occupied.");
        testEngine.removeEntity(hq);
    }

    /**
     * Tests if a multiple building can be placed on a buildable tile.
     * The building is a 3x3 building.
     */
    @Test
    public void checkMultiTIleBuildingPlacement3x3() {
        Entity hq = createMockedHQ(3, 3);
        PositionComponent bp = hq.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);
        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(
            bms.placeBuilding(hq, inventory),
            "Building should be placed on buildable tile."
        );
        OccupiesTilesComponent occupiesTilesComponent = hq.getComponent(
            OccupiesTilesComponent.class
        );
        assertEquals(9, occupiesTilesComponent.occupiedTiles.size(), "Tiles should be occupied.");
        testEngine.removeEntity(hq);
    }

    /**
     * Tests if a building can be removed from a different location than the one it
     * was placed.
     * The building is a 1x1 building.
     */
    @Test
    public void place1x1BuildingRemoveDifferentLocation() {
        Entity hq = createMockedHQ(1, 1);
        PositionComponent bp = hq.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);
        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(
            bms.placeBuilding(hq, inventory),
            "Building should be placed on buildable tile."
        );
        OccupiesTilesComponent occupiesTilesComponent = hq.getComponent(
            OccupiesTilesComponent.class
        );
        assertEquals(1, occupiesTilesComponent.occupiedTiles.size(), "Tiles should be occupied.");

        Vector2 screenCoordinate = new Vector2(
            TestMapEnvironment.tileToScreenX(5),
            TestMapEnvironment.tileToScreenY(4)
        );
        assertFalse(
            bms.removeBuilding(screenCoordinate.x, screenCoordinate.y, inventory),
            "Building should not be removed."
        );
        testEngine.removeEntity(hq);
    }

    /**
     * Tests if a building can be removed from the same location it was placed.
     * The building is a 2x2 building.
     */
    @Test
    public void place2x2BuildingRemoveItWithAnchorTile() {
        Entity hq = createMockedHQ(2, 2);
        PositionComponent bp = hq.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);
        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(
            bms.placeBuilding(hq, inventory),
            "Building should be placed on buildable tile."
        );
        OccupiesTilesComponent occupiesTilesComponent = hq.getComponent(
            OccupiesTilesComponent.class
        );
        assertEquals(4, occupiesTilesComponent.occupiedTiles.size(), "Tiles should be occupied.");

        Vector2 screenCoordinate = new Vector2(
            TestMapEnvironment.tileToScreenX(4),
            TestMapEnvironment.tileToScreenY(4)
        );
        assertTrue(
            bms.removeBuilding(screenCoordinate.x, screenCoordinate.y, inventory),
            "Building should be removed."
        );
        testEngine.removeEntity(hq);
    }

    /**
     * Tests if a building can be removed from a different location than the one it
     * was placed.
     * The building is a 2x2 building.
     */
    @Test
    public void place2x2BuildingRemoveItWithoutAnchorTIle() {
        Entity hq = createMockedHQ(2, 2);
        PositionComponent bp = hq.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);
        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(
            bms.placeBuilding(hq, inventory),
            "Building should be placed on buildable tile."
        );
        OccupiesTilesComponent occupiesTilesComponent = hq.getComponent(
            OccupiesTilesComponent.class
        );
        assertEquals(4, occupiesTilesComponent.occupiedTiles.size(), "Tiles should be occupied.");

        Vector2 screenCoordinate = new Vector2(
            TestMapEnvironment.tileToScreenX(5),
            TestMapEnvironment.tileToScreenY(4)
        );
        assertTrue(
            bms.removeBuilding(screenCoordinate.x, screenCoordinate.y, inventory),
            "Building should be removed."
        );
        testEngine.removeEntity(hq);
    }

    /**
     * Tests if a building can be placed again after it was removed.
     * The building is a 1x1 building.
     */
    @Test
    public void testRebuildAfterRemoval() {
        Entity tower = createMockedTower(1, 1);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(5);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(5);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(
            bms.placeBuilding(tower, inventory),
            "Initial building placement should succeed."
        );

        bp.basePosition.x = TestMapEnvironment.tileToScreenX(5);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(5);

        assertTrue(
            bms.removeBuilding(bp.basePosition.x, bp.basePosition.y, inventory),
            "Building should be removed."
        );

        Entity tower2 = createMockedTower(1, 1);
        PositionComponent bp2 = tower2.getComponent(PositionComponent.class);
        bp2.basePosition.set(bp.basePosition);

        assertTrue(
            bms.placeBuilding(tower2, inventory),
            "Building should be placeable again after removal."
        );
        testEngine.removeEntity(tower2);
    }

    /**
     * Tests if a building can be placed on a tile that is partially occupied by
     * another building.
     * The building is a 2x2 building.
     */
    @Test
    public void testPartialPlacementOnInvalidTilesFails() {
        Entity tower = createMockedTower(2, 2);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(1); // eine Ecke im Wasser
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(2);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(
            bms.placeBuilding(tower, inventory),
            "Building should not be placed if part is on non-buildable tiles."
        );
        testEngine.removeEntity(tower);
    }

    /**
     * Tests if a resource building can be placed if the resource is only diagonally
     * adjacent.
     * The building is a 1x1 building.
     */
    @Test
    public void testDiagonalResourceShouldBeCounted() {
        Entity resourceBuilding = createMockedResourceBuilding(
            1,
            1,
            ResourceTypeEnum.RESOURCE_TYPE_STONE
        );
        PositionComponent bp = resourceBuilding.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(1); // diagonal zu (0,4) mit Stein
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(3);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(
            bms.placeBuilding(resourceBuilding, inventory),
            "Diagonal adjacent resource should be counted."
        );
        testEngine.removeEntity(resourceBuilding);
    }

    /**
     * Tests if the same entity can be placed and added to the engine.
     * The building is a 1x1 building.
     */
    @Test
    public void testDoublePlacementFails() {
        Entity tower = createMockedTower(1, 1);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(bms.placeBuilding(tower, inventory), "First placement should succeed.");
        assertFalse(
            bms.placeBuilding(tower, inventory),
            "Second placement of same entity should fail."
        );
        testEngine.removeEntity(tower);
    }

    /**
     * Tests if a building placement fails when it goes out of map bounds.
     * The building is a 2x2 building.
     */
    @Test
    public void testBuildingPlacementOutOfBoundsFails() {
        Entity tower = createMockedTower(2, 2);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        // Rechte untere Ecke der Karte ist (8,8), also führt 8,8 + 1 Tile drüber hinaus
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(8);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(8);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(
            bms.placeBuilding(tower, inventory),
            "Placement beyond map bounds should fail."
        );
        testEngine.removeEntity(tower);
    }

    @Test
    public void testNonRemovableBuildingCannotBeRemoved() {
        // Arrange
        Entity hq = createMockedHQ(1, 1);
        PositionComponent bp = hq.getComponent(PositionComponent.class);
        bp.basePosition.x = TestMapEnvironment.tileToScreenX(4);
        bp.basePosition.y = TestMapEnvironment.tileToScreenY(4);

        // Add NonRemovableObjectComponent
        hq.add(new NonRemovalObjectComponent());

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(
            bms.placeBuilding(hq, inventory),
            "Building should be placed on buildable tile."
        );

        // Act
        Vector2 screenCoordinate = new Vector2(
            TestMapEnvironment.tileToScreenX(4),
            TestMapEnvironment.tileToScreenY(4)
        );
        boolean removed = bms.removeBuilding(screenCoordinate.x, screenCoordinate.y, inventory);

        // Assert
        assertFalse(removed, "Building should NOT be removed if it has NonRemovableObjectComponent.");

        testEngine.removeEntity(hq);
    }

    /**
     * Cleans up the test environment by removing all entities and disposing of map
     * resources.
     */
    @AfterAll
    public static void tearDown() {
        testEngine.removeAllEntities();
        testMapEnvironment.dispose();
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
}
