package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.utils.TileOffset;
import java.util.HashMap;
import java.util.Map;

public class WallFactory {

    private static final Map<
        WallPieceComponent.WallPiece,
        HashMap<TileOffset, TextureRegion>
    > woodWallPiecesCache = new HashMap<>();
    private static final Map<
        WallPieceComponent.WallPiece,
        HashMap<TileOffset, TextureRegion>
    > stoneWallPiecesCache = new HashMap<>();
    private static final Map<
        WallPieceComponent.WallPiece,
        HashMap<TileOffset, TextureRegion>
    > ironWallPiecesCache = new HashMap<>();

    public static Entity createWoodWall(Engine engine, float x, float y) {
        return createDefaultWall(engine, x, y);
    }

    public static Entity createStoneWall(Engine engine, float x, float y) {
        return createDefaultWall(engine, x, y);
    }

    public static Entity createIronWall(Engine engine, float x, float y) {
        return createDefaultWall(engine, x, y);
    }

    private static Entity createDefaultWall(Engine engine, float x, float y) {
        initWoodWallPiecesSprites();
        initStoneWallPiecesSprites();
        initIronWallPiecesSprites();

        Entity wall = engine.createEntity();
        PositionComponent position = new PositionComponent(x, y, 1, 1);
        OccupiesTilesComponent occupiesTiles = new OccupiesTilesComponent();
        RenderComponent render = new RenderComponent(RenderComponent.RenderType.BUILDING, 10, 1, 1);
        render.sprites =
        new HashMap<>(woodWallPiecesCache.get(WallPieceComponent.WallPiece.SINGLE));

        HealthComponent health = new HealthComponent();
        health.Health = 100;
        //todo current heatlh
        BuildingAnimationComponent buildingAnimation = new BuildingAnimationComponent();
        //todo probably not needed
        WallPieceComponent wallPiece = new WallPieceComponent();
        wallPiece.wallPieceTextures = woodWallPiecesCache;
        wallPiece.currentWallPiece = WallPieceComponent.WallPiece.SINGLE;

        wall.add(position);
        wall.add(occupiesTiles);
        wall.add(render);
        wall.add(health);
        wall.add(buildingAnimation);
        wall.add(wallPiece);

        return wall;
    }

    /**
     * Initializes sprite mappings for wood wall pieces.
     */
    private static void initWoodWallPiecesSprites() {
        if (!woodWallPiecesCache.isEmpty()) return;

        TextureAtlas atlas = AssetManagerInstance
            .getManager()
            .get("packed/textures.atlas", TextureAtlas.class);

        HashMap<TileOffset, TextureRegion> singlePiece = new HashMap<>();
        singlePiece.put(new TileOffset(0, 0), atlas.findRegion("wall_wood_single"));
        woodWallPiecesCache.put(WallPieceComponent.WallPiece.SINGLE, singlePiece);

        HashMap<TileOffset, TextureRegion> straightHorizontalLeftPiece = new HashMap<>();
        straightHorizontalLeftPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_wood_horizontal_left")
        );
        woodWallPiecesCache.put(
            WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_LEFT,
            straightHorizontalLeftPiece
        );

        HashMap<TileOffset, TextureRegion> straightHorizontalRightPiece = new HashMap<>();
        straightHorizontalRightPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_wood_horizontal_right")
        );
        woodWallPiecesCache.put(
            WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_RIGHT,
            straightHorizontalRightPiece
        );

        HashMap<TileOffset, TextureRegion> straightHorizontalMiddlePiece = new HashMap<>();
        straightHorizontalMiddlePiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_wood_horizontal_middle")
        );
        woodWallPiecesCache.put(
            WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_MIDDLE,
            straightHorizontalMiddlePiece
        );

        HashMap<TileOffset, TextureRegion> straightVerticalTopPiece = new HashMap<>();
        straightVerticalTopPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_wood_vertical_top")
        );
        woodWallPiecesCache.put(
            WallPieceComponent.WallPiece.STRAIGHT_VERTICAL_UP,
            straightVerticalTopPiece
        );

        HashMap<TileOffset, TextureRegion> straightVerticalBottomPiece = new HashMap<>();
        straightVerticalBottomPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_wood_vertical_bottom")
        );
        woodWallPiecesCache.put(
            WallPieceComponent.WallPiece.STRAIGHT_VERTICAL_DOWN,
            straightVerticalBottomPiece
        );

        HashMap<TileOffset, TextureRegion> straightVerticalMiddlePiece = new HashMap<>();
        straightVerticalMiddlePiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_wood_vertical_middle")
        );
        woodWallPiecesCache.put(
            WallPieceComponent.WallPiece.STRAIGHT_VERTICAL_MIDDLE,
            straightVerticalMiddlePiece
        );

        HashMap<TileOffset, TextureRegion> cornerTopLeftPiece = new HashMap<>();
        cornerTopLeftPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_wood_corner_topleft"));
        woodWallPiecesCache.put(WallPieceComponent.WallPiece.CORNER_TOP_LEFT, cornerTopLeftPiece);

        HashMap<TileOffset, TextureRegion> cornerTopRightPiece = new HashMap<>();
        cornerTopRightPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_wood_corner_topright")
        );
        woodWallPiecesCache.put(WallPieceComponent.WallPiece.CORNER_TOP_RIGHT, cornerTopRightPiece);

        HashMap<TileOffset, TextureRegion> cornerBottomLeftPiece = new HashMap<>();
        cornerBottomLeftPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_wood_corner_bottomleft")
        );
        woodWallPiecesCache.put(
            WallPieceComponent.WallPiece.CORNER_BOTTOM_LEFT,
            cornerBottomLeftPiece
        );

        HashMap<TileOffset, TextureRegion> cornerBottomRightPiece = new HashMap<>();
        cornerBottomRightPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_wood_corner_bottomright")
        );
        woodWallPiecesCache.put(
            WallPieceComponent.WallPiece.CORNER_BOTTOM_RIGHT,
            cornerBottomRightPiece
        );

        HashMap<TileOffset, TextureRegion> tTopPiece = new HashMap<>();
        tTopPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_wood_T_top"));
        woodWallPiecesCache.put(WallPieceComponent.WallPiece.T_TOP, tTopPiece);

        HashMap<TileOffset, TextureRegion> tBottomPiece = new HashMap<>();
        tBottomPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_wood_T_bottom"));
        woodWallPiecesCache.put(WallPieceComponent.WallPiece.T_BOTTOM, tBottomPiece);

        HashMap<TileOffset, TextureRegion> tLeftPiece = new HashMap<>();
        tLeftPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_wood_T_left"));
        woodWallPiecesCache.put(WallPieceComponent.WallPiece.T_LEFT, tLeftPiece);

        HashMap<TileOffset, TextureRegion> tRightPiece = new HashMap<>();
        tRightPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_wood_T_right"));
        woodWallPiecesCache.put(WallPieceComponent.WallPiece.T_RIGHT, tRightPiece);

        HashMap<TileOffset, TextureRegion> crossPiece = new HashMap<>();
        crossPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_wood_cross"));
        woodWallPiecesCache.put(WallPieceComponent.WallPiece.CROSS, crossPiece);
    }

    private static void initStoneWallPiecesSprites() {
        if (!stoneWallPiecesCache.isEmpty()) return;

        TextureAtlas atlas = AssetManagerInstance
            .getManager()
            .get("packed/textures.atlas", TextureAtlas.class);
    }

    private static void initIronWallPiecesSprites() {
        if (!ironWallPiecesCache.isEmpty()) return;

        TextureAtlas atlas = AssetManagerInstance
            .getManager()
            .get("packed/textures.atlas", TextureAtlas.class);
    }
}
