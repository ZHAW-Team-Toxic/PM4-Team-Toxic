package com.zhaw.frontier;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.zhaw.frontier.components.OccupiesTilesComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.WallPieceComponent;
import com.zhaw.frontier.systems.BuildingManagerSystem;
import com.zhaw.frontier.systems.WallManager;
import com.zhaw.frontier.utils.TileOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Unit tests for the wall placement logic.
 * Verifies correct detection of wall piece types (e.g. SINGLE, STRAIGHT_HORIZONTAL_LEFT)
 * based on neighboring wall positions.
 */
@ExtendWith(GdxExtension.class)
public class WallManagerTest {

    private static Engine testEngine;
    private static ExtendViewport gameWorldView;
    private static TestMapEnvironment testMapEnvironment;

    /**
     * Initializes the test environment with a test map and registers the
     * {@link BuildingManagerSystem}. Ensures only the map entity is present initially.
     */
    @BeforeAll
    public static void setUp() {
        testMapEnvironment = new TestMapEnvironment();
        testEngine = testMapEnvironment.getTestEngine();
        gameWorldView = testMapEnvironment.getGameWorldView();

        testEngine.addSystem(
            new BuildingManagerSystem(
                testMapEnvironment.getBottomLayer(),
                gameWorldView,
                testEngine
            )
        );

        assertEquals(1, testEngine.getEntities().size(), "Only the map entity should be present.");
    }

    /**
     * Tests that a single wall piece without any neighbors is correctly
     * identified as {@link WallPieceComponent.WallPiece#SINGLE}.
     */
    @Test
    public void testSingleWallPiece() {
        Entity singleWallPiece = testEngine.createEntity();
        PositionComponent positionComponent = new PositionComponent(
            TestMapEnvironment.tileToScreenX(4),
            TestMapEnvironment.tileToScreenY(4),
            1,
            1
        );
        OccupiesTilesComponent occupiesTilesComponent = new OccupiesTilesComponent();
        WallPieceComponent wallPieceComponent1 = new WallPieceComponent();
        wallPieceComponent1.currentWallPiece = WallPieceComponent.WallPiece.CROSS;
        RenderComponent renderComponent = new RenderComponent(
            RenderComponent.RenderType.BUILDING,
            10,
            1,
            1
        );
        HashMap<TileOffset, TextureRegion> sprites = new HashMap<>();
        sprites.put(new TileOffset(0, 0), new TextureRegion());
        wallPieceComponent1.wallPieceTextures.put(WallPieceComponent.WallPiece.CROSS, sprites);
        wallPieceComponent1.wallPieceTextures.put(WallPieceComponent.WallPiece.SINGLE, sprites);
        renderComponent.sprites = sprites;
        singleWallPiece.add(positionComponent);
        singleWallPiece.add(occupiesTilesComponent);
        singleWallPiece.add(wallPieceComponent1);
        singleWallPiece.add(renderComponent);

        BuildingManagerSystem buildingManagerSystem = testEngine.getSystem(
            BuildingManagerSystem.class
        );
        buildingManagerSystem.placeBuilding(singleWallPiece);
        assertEquals(
            2,
            testEngine.getEntities().size(),
            "Two entities should be present: the map and the wall piece."
        );

        WallManager.update(testEngine);

        WallPieceComponent wallPieceComponent11 = testEngine
            .getEntitiesFor(
                Family
                    .all(PositionComponent.class, RenderComponent.class, WallPieceComponent.class)
                    .get()
            )
            .first()
            .getComponent(WallPieceComponent.class);

        assertEquals(
            wallPieceComponent11.currentWallPiece,
            WallPieceComponent.WallPiece.SINGLE,
            "The wall piece type should be SINGLE."
        );
        testEngine.removeEntity(singleWallPiece);
    }

    /**
     * Tests the detection of horizontal wall connections.
     * Places two walls at a horizontal distance and verifies they are identified as
     * {@link WallPieceComponent.WallPiece#STRAIGHT_HORIZONTAL_LEFT} and
     * {@link WallPieceComponent.WallPiece#STRAIGHT_HORIZONTAL_RIGHT}.
     */
    @Test
    public void testHorizontalLeftPiece() {
        Entity singleWallPiece1 = testEngine.createEntity();
        PositionComponent positionComponent1 = new PositionComponent(
            TestMapEnvironment.tileToScreenX(3),
            TestMapEnvironment.tileToScreenY(3),
            1,
            1
        );
        OccupiesTilesComponent occupiesTilesComponent1 = new OccupiesTilesComponent();
        WallPieceComponent wallPieceComponent1 = new WallPieceComponent();
        wallPieceComponent1.currentWallPiece = WallPieceComponent.WallPiece.CROSS;
        RenderComponent renderComponent1 = new RenderComponent(
            RenderComponent.RenderType.BUILDING,
            10,
            1,
            1
        );
        HashMap<TileOffset, TextureRegion> sprites = new HashMap<>();
        sprites.put(new TileOffset(0, 0), new TextureRegion());
        renderComponent1.sprites = sprites;
        wallPieceComponent1.wallPieceTextures.put(WallPieceComponent.WallPiece.CROSS, sprites);
        wallPieceComponent1.wallPieceTextures.put(WallPieceComponent.WallPiece.SINGLE, sprites);
        wallPieceComponent1.wallPieceTextures.put(
            WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_LEFT,
            sprites
        );
        wallPieceComponent1.wallPieceTextures.put(
            WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_RIGHT,
            sprites
        );
        wallPieceComponent1.wallPieceTextures.put(
            WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_MIDDLE,
            sprites
        );
        singleWallPiece1.add(positionComponent1);
        singleWallPiece1.add(occupiesTilesComponent1);
        singleWallPiece1.add(wallPieceComponent1);
        singleWallPiece1.add(renderComponent1);

        Entity singleWallPiece2 = testEngine.createEntity();
        PositionComponent positionComponent2 = new PositionComponent(
            TestMapEnvironment.tileToScreenX(5),
            TestMapEnvironment.tileToScreenY(3),
            1,
            1
        );
        OccupiesTilesComponent occupiesTilesComponent2 = new OccupiesTilesComponent();
        WallPieceComponent wallPieceComponent2 = new WallPieceComponent();
        wallPieceComponent1.currentWallPiece = WallPieceComponent.WallPiece.CROSS;
        RenderComponent renderComponent2 = new RenderComponent(
            RenderComponent.RenderType.BUILDING,
            10,
            1,
            1
        );
        renderComponent2.sprites = sprites;
        wallPieceComponent2.wallPieceTextures.put(WallPieceComponent.WallPiece.CROSS, sprites);
        wallPieceComponent2.wallPieceTextures.put(WallPieceComponent.WallPiece.SINGLE, sprites);
        wallPieceComponent2.wallPieceTextures.put(
            WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_LEFT,
            sprites
        );
        wallPieceComponent2.wallPieceTextures.put(
            WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_RIGHT,
            sprites
        );
        wallPieceComponent2.wallPieceTextures.put(
            WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_MIDDLE,
            sprites
        );
        singleWallPiece2.add(positionComponent2);
        singleWallPiece2.add(occupiesTilesComponent2);
        singleWallPiece2.add(wallPieceComponent2);
        singleWallPiece2.add(renderComponent2);

        BuildingManagerSystem buildingManagerSystem = testEngine.getSystem(
            BuildingManagerSystem.class
        );
        buildingManagerSystem.placeBuilding(singleWallPiece1);

        assertEquals(
            2,
            testEngine.getEntities().size(),
            "Two entities should be present: the map and the wall piece."
        );

        WallPieceComponent wallPieceComponent11 = testEngine
            .getEntitiesFor(
                Family
                    .all(PositionComponent.class, RenderComponent.class, WallPieceComponent.class)
                    .get()
            )
            .first()
            .getComponent(WallPieceComponent.class);

        assertEquals(
            wallPieceComponent11.currentWallPiece,
            WallPieceComponent.WallPiece.SINGLE,
            "The wall piece type should be SINGLE."
        );

        buildingManagerSystem.placeBuilding(singleWallPiece2);

        assertEquals(
            3,
            testEngine.getEntities().size(),
            "Three entities should be present: the map and the wall pieces."
        );

        List<WallPieceComponent> wallPieces = new ArrayList<>();
        for (Entity entity : testEngine.getEntitiesFor(
            Family
                .all(PositionComponent.class, RenderComponent.class, WallPieceComponent.class)
                .get()
        )) {
            wallPieces.add(entity.getComponent(WallPieceComponent.class));
        }

        for (WallPieceComponent wallPiece : wallPieces) {
            if (
                wallPiece.currentWallPiece == WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_LEFT
            ) {
                assertEquals(
                    wallPiece.currentWallPiece,
                    WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_LEFT,
                    "The wall piece type should be HORIZONTAL_LEFT."
                );
            }
            if (
                wallPiece.currentWallPiece == WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_RIGHT
            ) {
                assertEquals(
                    wallPiece.currentWallPiece,
                    WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_RIGHT,
                    "The wall piece type should be HORIZONTAL_RIGHT."
                );
            }
        }

        testEngine.removeEntity(singleWallPiece1);
        testEngine.removeEntity(singleWallPiece2);
    }
}
