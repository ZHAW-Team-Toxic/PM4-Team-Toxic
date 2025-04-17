package com.zhaw.frontier;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.zhaw.frontier.components.OccupiesTilesComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.WallPieceComponent;
import com.zhaw.frontier.systems.BuildingManagerSystem;
import com.zhaw.frontier.systems.WallManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(GdxExtension.class)
public class WallMangerTest {


    private static Engine testEngine;
    private static ExtendViewport gameWorldView;
    private static TestMapEnvironment testMapEnvironment;

    @BeforeAll
    public static void setUp() {
        testMapEnvironment = new TestMapEnvironment();
        testEngine = testMapEnvironment.getTestEngine();
        gameWorldView = testMapEnvironment.getGameWorldView();

        testEngine.addSystem(new BuildingManagerSystem(
            testMapEnvironment.getBottomLayer(),
            gameWorldView,
            testEngine
        ));

        assertEquals(1, testEngine.getEntities().size(), "Only the map entity should be present.");
    }


    @Test
    public void testSingleWallPiece() {
        Entity singleWallPiece = testEngine.createEntity();
        PositionComponent positionComponent = new PositionComponent(TestMapEnvironment.tileToScreenX(4),
            TestMapEnvironment.tileToScreenY(4), 2, 2);
        OccupiesTilesComponent occupiesTilesComponent = new OccupiesTilesComponent();
        WallPieceComponent wallPieceComponent1 = new WallPieceComponent();
        wallPieceComponent1.currentWallPiece = WallPieceComponent.WallPiece.CROSS;
        RenderComponent renderComponent = new RenderComponent(RenderComponent.RenderType.BUILDING, 10, 2, 2);
        singleWallPiece.add(positionComponent);
        singleWallPiece.add(occupiesTilesComponent);
        singleWallPiece.add(wallPieceComponent1);
        singleWallPiece.add(renderComponent);

        BuildingManagerSystem buildingManagerSystem = testEngine.getSystem(BuildingManagerSystem.class);
        buildingManagerSystem.placeBuilding(singleWallPiece);
        assertEquals(
            2,
            testEngine.getEntities().size(),
            "Two entities should be present: the map and the wall piece."
        );

        WallManager.update(testEngine);

        WallPieceComponent wallPieceComponent11 = testEngine
            .getEntitiesFor(Family.all(PositionComponent.class, RenderComponent.class, WallPieceComponent.class).get())
            .first()
            .getComponent(WallPieceComponent.class);

        assertEquals(
            wallPieceComponent11.currentWallPiece,
            WallPieceComponent.WallPiece.SINGLE,
            "The wall piece type should be SINGLE."
        );
    }

    @Test
    public void testHorizontalLeftPiece() {
        Entity singleWallPiece1 = testEngine.createEntity();
        PositionComponent positionComponent1 = new PositionComponent(TestMapEnvironment.tileToScreenX(3),
            TestMapEnvironment.tileToScreenY(3), 2, 2);
        OccupiesTilesComponent occupiesTilesComponent1 = new OccupiesTilesComponent();
        WallPieceComponent wallPieceComponent1 = new WallPieceComponent();
        wallPieceComponent1.currentWallPiece = WallPieceComponent.WallPiece.CROSS;
        RenderComponent renderComponent1 = new RenderComponent(RenderComponent.RenderType.BUILDING, 10, 2, 2);
        singleWallPiece1.add(positionComponent1);
        singleWallPiece1.add(occupiesTilesComponent1);
        singleWallPiece1.add(wallPieceComponent1);
        singleWallPiece1.add(renderComponent1);

        Entity singleWallPiece2 = testEngine.createEntity();
        PositionComponent positionComponent2 = new PositionComponent(TestMapEnvironment.tileToScreenX(5),
            TestMapEnvironment.tileToScreenY(3), 2, 2);
        OccupiesTilesComponent occupiesTilesComponent2 = new OccupiesTilesComponent();
        WallPieceComponent wallPieceComponent2 = new WallPieceComponent();
        wallPieceComponent1.currentWallPiece = WallPieceComponent.WallPiece.CROSS;
        RenderComponent renderComponent2 = new RenderComponent(RenderComponent.RenderType.BUILDING, 10, 2, 2);
        singleWallPiece2.add(positionComponent2);
        singleWallPiece2.add(occupiesTilesComponent2);
        singleWallPiece2.add(wallPieceComponent2);
        singleWallPiece2.add(renderComponent2);

        BuildingManagerSystem buildingManagerSystem = testEngine.getSystem(BuildingManagerSystem.class);
        buildingManagerSystem.placeBuilding(singleWallPiece1);

        assertEquals(
            2,
            testEngine.getEntities().size(),
            "Two entities should be present: the map and the wall piece."
        );

        WallManager.update(testEngine);

        WallPieceComponent wallPieceComponent11 = testEngine
            .getEntitiesFor(Family.all(PositionComponent.class, RenderComponent.class, WallPieceComponent.class).get())
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


        WallManager.update(testEngine);


        List<WallPieceComponent> wallPieces = new ArrayList<>();
        for (Entity entity : testEngine.getEntitiesFor(Family.all(PositionComponent.class, RenderComponent.class, WallPieceComponent.class).get())) {
            wallPieces.add(entity.getComponent(WallPieceComponent.class));
        }

        for (WallPieceComponent wallPiece : wallPieces) {
            if (wallPiece.currentWallPiece == WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_LEFT) {
                assertEquals(
                    wallPiece.currentWallPiece,
                    WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_LEFT,
                    "The wall piece type should be HORIZONTAL_LEFT."
                );
            }
            if (wallPiece.currentWallPiece == WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_RIGHT) {
                assertEquals(
                    wallPiece.currentWallPiece,
                    WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_RIGHT,
                    "The wall piece type should be HORIZONTAL_RIGHT."
                );
            }
        }

    }
}
