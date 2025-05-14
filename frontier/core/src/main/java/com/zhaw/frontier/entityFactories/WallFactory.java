package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.enums.Team;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.utils.TileOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating wall entities.
 * <p>
 * This class is responsible for generating default wall entities.
 * A default wall entity is composed of a {@link PositionComponent},
 * a {@link HealthComponent}, and a {@link RenderComponent}. The render component is set
 * to render the wall as a building using a placeholder texture that should be replaced
 * with the actual wall texture in the future.
 * </p>
 */
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
        initWoodWallPiecesSprites();
        Entity wall = createDefaultWall(engine, x, y);
        wall.add(new EntityTypeComponent(EntityTypeComponent.EntityType.WOOD_WALL));
        RenderComponent render = new RenderComponent(RenderComponent.RenderType.BUILDING, 10, 1, 1);

        render.sprites =
        new HashMap<>(woodWallPiecesCache.get(WallPieceComponent.WallPiece.SINGLE));
        wall.add(render);

        WallPieceComponent wallPiece = new WallPieceComponent();
        wallPiece.wallPieceTextures = woodWallPiecesCache;
        wallPiece.currentWallPiece = WallPieceComponent.WallPiece.SINGLE;
        wall.add(wallPiece);
        return wall;
    }

    public static Entity createStoneWall(Engine engine, float x, float y) {
        initStoneWallPiecesSprites();
        Entity wall = createDefaultWall(engine, x, y);
        wall.add(new EntityTypeComponent(EntityTypeComponent.EntityType.STONE_WALL));
        RenderComponent render = new RenderComponent(RenderComponent.RenderType.BUILDING, 10, 1, 1);

        render.sprites =
        new HashMap<>(stoneWallPiecesCache.get(WallPieceComponent.WallPiece.SINGLE));
        wall.add(render);

        WallPieceComponent wallPiece = new WallPieceComponent();
        wallPiece.wallPieceTextures = stoneWallPiecesCache;
        wallPiece.currentWallPiece = WallPieceComponent.WallPiece.SINGLE;
        wall.add(wallPiece);
        return wall;
    }

    public static Entity createIronWall(Engine engine, float x, float y) {
        initIronWallPiecesSprites();
        Entity wall = createDefaultWall(engine, x, y);
        wall.add(new EntityTypeComponent(EntityTypeComponent.EntityType.IRON_WALL));
        RenderComponent render = new RenderComponent(RenderComponent.RenderType.BUILDING, 10, 1, 1);

        render.sprites =
        new HashMap<>(ironWallPiecesCache.get(WallPieceComponent.WallPiece.SINGLE));
        wall.add(render);

        WallPieceComponent wallPiece = new WallPieceComponent();
        wallPiece.wallPieceTextures = ironWallPiecesCache;
        wallPiece.currentWallPiece = WallPieceComponent.WallPiece.SINGLE;
        wall.add(wallPiece);
        return wall;
    }

    private static Entity createDefaultWall(Engine engine, float x, float y) {
        Entity wall = engine.createEntity();
        PositionComponent position = new PositionComponent(x, y, 1, 1);
        OccupiesTilesComponent occupiesTiles = new OccupiesTilesComponent();

        BuildingAnimationComponent buildingAnimation = new BuildingAnimationComponent();

        wall.add(position);
        wall.add(occupiesTiles);
        wall.add(new HealthComponent());
        wall.add(buildingAnimation);

        wall.add(new AnimationQueueComponent());
        wall.add(new TeamComponent(Team.PLAYER));
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

        HashMap<TileOffset, TextureRegion> wallPiece = new HashMap<>();
        wallPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_wood_destroyed"));
        woodWallPiecesCache.put(WallPieceComponent.WallPiece.DESTROYED, wallPiece);
    }

    private static void initStoneWallPiecesSprites() {
        if (!stoneWallPiecesCache.isEmpty()) return;

        TextureAtlas atlas = AssetManagerInstance
            .getManager()
            .get("packed/textures.atlas", TextureAtlas.class);

        HashMap<TileOffset, TextureRegion> singlePiece = new HashMap<>();
        singlePiece.put(new TileOffset(0, 0), atlas.findRegion("wall_stone_single"));
        stoneWallPiecesCache.put(WallPieceComponent.WallPiece.SINGLE, singlePiece);

        HashMap<TileOffset, TextureRegion> straightHorizontalLeftPiece = new HashMap<>();
        straightHorizontalLeftPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_stone_horizontal_left")
        );
        stoneWallPiecesCache.put(
            WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_LEFT,
            straightHorizontalLeftPiece
        );

        HashMap<TileOffset, TextureRegion> straightHorizontalRightPiece = new HashMap<>();
        straightHorizontalRightPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_stone_horizontal_right")
        );
        stoneWallPiecesCache.put(
            WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_RIGHT,
            straightHorizontalRightPiece
        );

        HashMap<TileOffset, TextureRegion> straightHorizontalMiddlePiece = new HashMap<>();
        straightHorizontalMiddlePiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_stone_horizontal_middle")
        );
        stoneWallPiecesCache.put(
            WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_MIDDLE,
            straightHorizontalMiddlePiece
        );

        HashMap<TileOffset, TextureRegion> straightVerticalTopPiece = new HashMap<>();
        straightVerticalTopPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_stone_vertical_top")
        );
        stoneWallPiecesCache.put(
            WallPieceComponent.WallPiece.STRAIGHT_VERTICAL_UP,
            straightVerticalTopPiece
        );

        HashMap<TileOffset, TextureRegion> straightVerticalBottomPiece = new HashMap<>();
        straightVerticalBottomPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_stone_vertical_bottom")
        );
        stoneWallPiecesCache.put(
            WallPieceComponent.WallPiece.STRAIGHT_VERTICAL_DOWN,
            straightVerticalBottomPiece
        );

        HashMap<TileOffset, TextureRegion> straightVerticalMiddlePiece = new HashMap<>();
        straightVerticalMiddlePiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_stone_vertical_middle")
        );
        stoneWallPiecesCache.put(
            WallPieceComponent.WallPiece.STRAIGHT_VERTICAL_MIDDLE,
            straightVerticalMiddlePiece
        );

        HashMap<TileOffset, TextureRegion> cornerTopLeftPiece = new HashMap<>();
        cornerTopLeftPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_stone_corner_topleft"));
        stoneWallPiecesCache.put(WallPieceComponent.WallPiece.CORNER_TOP_LEFT, cornerTopLeftPiece);

        HashMap<TileOffset, TextureRegion> cornerTopRightPiece = new HashMap<>();
        cornerTopRightPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_stone_corner_topright")
        );
        stoneWallPiecesCache.put(
            WallPieceComponent.WallPiece.CORNER_TOP_RIGHT,
            cornerTopRightPiece
        );

        HashMap<TileOffset, TextureRegion> cornerBottomLeftPiece = new HashMap<>();
        cornerBottomLeftPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_stone_corner_bottomleft")
        );
        stoneWallPiecesCache.put(
            WallPieceComponent.WallPiece.CORNER_BOTTOM_LEFT,
            cornerBottomLeftPiece
        );

        HashMap<TileOffset, TextureRegion> cornerBottomRightPiece = new HashMap<>();
        cornerBottomRightPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_stone_corner_bottomright")
        );
        stoneWallPiecesCache.put(
            WallPieceComponent.WallPiece.CORNER_BOTTOM_RIGHT,
            cornerBottomRightPiece
        );

        HashMap<TileOffset, TextureRegion> tTopPiece = new HashMap<>();
        tTopPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_stone_T_top"));
        stoneWallPiecesCache.put(WallPieceComponent.WallPiece.T_TOP, tTopPiece);

        HashMap<TileOffset, TextureRegion> tBottomPiece = new HashMap<>();
        tBottomPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_stone_T_bottom"));
        stoneWallPiecesCache.put(WallPieceComponent.WallPiece.T_BOTTOM, tBottomPiece);

        HashMap<TileOffset, TextureRegion> tLeftPiece = new HashMap<>();
        tLeftPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_stone_T_left"));
        stoneWallPiecesCache.put(WallPieceComponent.WallPiece.T_LEFT, tLeftPiece);

        HashMap<TileOffset, TextureRegion> tRightPiece = new HashMap<>();
        tRightPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_stone_T_right"));
        stoneWallPiecesCache.put(WallPieceComponent.WallPiece.T_RIGHT, tRightPiece);

        HashMap<TileOffset, TextureRegion> crossPiece = new HashMap<>();
        crossPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_stone_cross"));
        stoneWallPiecesCache.put(WallPieceComponent.WallPiece.CROSS, crossPiece);

        HashMap<TileOffset, TextureRegion> wallPiece = new HashMap<>();
        wallPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_stone_destroyed"));
        stoneWallPiecesCache.put(WallPieceComponent.WallPiece.DESTROYED, wallPiece);
    }

    private static void initIronWallPiecesSprites() {
        if (!ironWallPiecesCache.isEmpty()) return;

        TextureAtlas atlas = AssetManagerInstance
            .getManager()
            .get("packed/textures.atlas", TextureAtlas.class);

        HashMap<TileOffset, TextureRegion> singlePiece = new HashMap<>();
        singlePiece.put(new TileOffset(0, 0), atlas.findRegion("wall_iron_single"));
        ironWallPiecesCache.put(WallPieceComponent.WallPiece.SINGLE, singlePiece);

        HashMap<TileOffset, TextureRegion> straightHorizontalLeftPiece = new HashMap<>();
        straightHorizontalLeftPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_iron_horizontal_left")
        );
        ironWallPiecesCache.put(
            WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_LEFT,
            straightHorizontalLeftPiece
        );

        HashMap<TileOffset, TextureRegion> straightHorizontalRightPiece = new HashMap<>();
        straightHorizontalRightPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_iron_horizontal_right")
        );
        ironWallPiecesCache.put(
            WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_RIGHT,
            straightHorizontalRightPiece
        );

        HashMap<TileOffset, TextureRegion> straightHorizontalMiddlePiece = new HashMap<>();
        straightHorizontalMiddlePiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_iron_horizontal_middle")
        );
        ironWallPiecesCache.put(
            WallPieceComponent.WallPiece.STRAIGHT_HORIZONTAL_MIDDLE,
            straightHorizontalMiddlePiece
        );

        HashMap<TileOffset, TextureRegion> straightVerticalTopPiece = new HashMap<>();
        straightVerticalTopPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_iron_vertical_top")
        );
        ironWallPiecesCache.put(
            WallPieceComponent.WallPiece.STRAIGHT_VERTICAL_UP,
            straightVerticalTopPiece
        );

        HashMap<TileOffset, TextureRegion> straightVerticalBottomPiece = new HashMap<>();
        straightVerticalBottomPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_iron_vertical_bottom")
        );
        ironWallPiecesCache.put(
            WallPieceComponent.WallPiece.STRAIGHT_VERTICAL_DOWN,
            straightVerticalBottomPiece
        );

        HashMap<TileOffset, TextureRegion> straightVerticalMiddlePiece = new HashMap<>();
        straightVerticalMiddlePiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_iron_vertical_middle")
        );
        ironWallPiecesCache.put(
            WallPieceComponent.WallPiece.STRAIGHT_VERTICAL_MIDDLE,
            straightVerticalMiddlePiece
        );

        HashMap<TileOffset, TextureRegion> cornerTopLeftPiece = new HashMap<>();
        cornerTopLeftPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_iron_corner_topleft"));
        ironWallPiecesCache.put(WallPieceComponent.WallPiece.CORNER_TOP_LEFT, cornerTopLeftPiece);

        HashMap<TileOffset, TextureRegion> cornerTopRightPiece = new HashMap<>();
        cornerTopRightPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_iron_corner_topright")
        );
        ironWallPiecesCache.put(WallPieceComponent.WallPiece.CORNER_TOP_RIGHT, cornerTopRightPiece);

        HashMap<TileOffset, TextureRegion> cornerBottomLeftPiece = new HashMap<>();
        cornerBottomLeftPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_iron_corner_bottomleft")
        );
        ironWallPiecesCache.put(
            WallPieceComponent.WallPiece.CORNER_BOTTOM_LEFT,
            cornerBottomLeftPiece
        );

        HashMap<TileOffset, TextureRegion> cornerBottomRightPiece = new HashMap<>();
        cornerBottomRightPiece.put(
            new TileOffset(0, 0),
            atlas.findRegion("wall_iron_corner_bottomright")
        );
        ironWallPiecesCache.put(
            WallPieceComponent.WallPiece.CORNER_BOTTOM_RIGHT,
            cornerBottomRightPiece
        );

        HashMap<TileOffset, TextureRegion> tTopPiece = new HashMap<>();
        tTopPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_iron_T_top"));
        ironWallPiecesCache.put(WallPieceComponent.WallPiece.T_TOP, tTopPiece);

        HashMap<TileOffset, TextureRegion> tBottomPiece = new HashMap<>();
        tBottomPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_iron_T_bottom"));
        ironWallPiecesCache.put(WallPieceComponent.WallPiece.T_BOTTOM, tBottomPiece);

        HashMap<TileOffset, TextureRegion> tLeftPiece = new HashMap<>();
        tLeftPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_iron_T_left"));
        ironWallPiecesCache.put(WallPieceComponent.WallPiece.T_LEFT, tLeftPiece);

        HashMap<TileOffset, TextureRegion> tRightPiece = new HashMap<>();
        tRightPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_iron_T_right"));
        ironWallPiecesCache.put(WallPieceComponent.WallPiece.T_RIGHT, tRightPiece);

        HashMap<TileOffset, TextureRegion> crossPiece = new HashMap<>();
        crossPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_iron_cross"));
        ironWallPiecesCache.put(WallPieceComponent.WallPiece.CROSS, crossPiece);

        HashMap<TileOffset, TextureRegion> wallPiece = new HashMap<>();
        wallPiece.put(new TileOffset(0, 0), atlas.findRegion("wall_iron_destroyed"));
        ironWallPiecesCache.put(WallPieceComponent.WallPiece.DESTROYED, wallPiece);
    }
}
