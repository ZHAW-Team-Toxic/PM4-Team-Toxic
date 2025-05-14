package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhaw.frontier.components.AnimationQueueComponent;
import com.zhaw.frontier.components.BuildingAnimationComponent;
import com.zhaw.frontier.components.BuildingAnimationComponent.BuildingAnimationType;
import com.zhaw.frontier.components.EntityTypeComponent;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.components.OccupiesTilesComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.TeamComponent;
import com.zhaw.frontier.components.WallPieceComponent;
import com.zhaw.frontier.configs.AppProperties;
import com.zhaw.frontier.enums.Team;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.utils.TileOffset;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating wall entities.
 * <p>
 * This class is responsible for generating default wall entities.
 * A default wall entity is composed of a {@link PositionComponent},
 * a {@link HealthComponent}, and a {@link RenderComponent}. The render
 * component is set
 * to render the wall as a building using a placeholder texture that should be
 * replaced
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

    private static final EnumMap<
        BuildingAnimationType,
        HashMap<TileOffset, Animation<TextureRegion>>
    > sharedAnimations = new EnumMap<>(BuildingAnimationType.class);

    public static Entity createWoodWall(Engine engine, float x, float y) {
        Entity wall = createDefaultWall(engine, x, y);
        wall.add(new EntityTypeComponent(EntityTypeComponent.EntityType.WOOD_WALL));
        return wall;
    }

    public static Entity createStoneWall(Engine engine, float x, float y) {
        Entity wall = createDefaultWall(engine, x, y);
        wall.add(new EntityTypeComponent(EntityTypeComponent.EntityType.STONE_WALL));
        return wall;
    }

    public static Entity createIronWall(Engine engine, float x, float y) {
        Entity wall = createDefaultWall(engine, x, y);
        wall.add(new EntityTypeComponent(EntityTypeComponent.EntityType.IRON_WALL));
        return wall;
    }

    private static void initializeSharedWallAnimations() {
        if (sharedAnimations.isEmpty()) {
            TextureAtlas deathAtlas = AssetManagerInstance
                .getManager()
                .get("packed/textures.atlas", TextureAtlas.class);

            HashMap<TileOffset, Animation<TextureRegion>> deathAnimation = new HashMap<>();
            deathAnimation.put(
                new TileOffset(0, 0),
                new Animation<>(
                    AppProperties.DEATH_FRAME_DURATION,
                    deathAtlas.findRegions("death/explosion"),
                    Animation.PlayMode.LOOP
                )
            );
            sharedAnimations.put(
                BuildingAnimationComponent.BuildingAnimationType.DESTROYING,
                deathAnimation
            );
        }
    }

    /**
     * Creates a default wall entity with the necessary components.
     * <p>
     * The wall entity is initialized with a {@link PositionComponent} for spatial
     * positioning,
     * a {@link HealthComponent} for health management, and a
     * {@link RenderComponent} for rendering.
     * The {@code RenderComponent} uses a placeholder texture (created by
     * {@link #createPlaceHolder()})
     * with its render type set to {@link RenderComponent.RenderType#BUILDING}.
     * </p>
     *
     * @param engine the {@link Engine} used to create and manage the entity.
     * @return the newly created wall entity.
     */
    private static Entity createDefaultWall(Engine engine, float x, float y) {
        initWoodWallPiecesSprites();
        initStoneWallPiecesSprites();
        initIronWallPiecesSprites();
        initializeSharedWallAnimations();

        Entity wall = engine.createEntity();
        PositionComponent position = new PositionComponent(x, y, 1, 1);
        OccupiesTilesComponent occupiesTiles = new OccupiesTilesComponent();
        RenderComponent render = new RenderComponent(RenderComponent.RenderType.BUILDING, 10, 1, 1);
        render.sprites =
        new HashMap<>(woodWallPiecesCache.get(WallPieceComponent.WallPiece.SINGLE));

        HealthComponent health = new HealthComponent();
        BuildingAnimationComponent buildingAnimation = new BuildingAnimationComponent();
        buildingAnimation.animations = sharedAnimations;
        WallPieceComponent wallPiece = new WallPieceComponent();
        wallPiece.wallPieceTextures = woodWallPiecesCache;
        wallPiece.currentWallPiece = WallPieceComponent.WallPiece.SINGLE;

        wall.add(position);
        wall.add(occupiesTiles);
        wall.add(render);
        wall.add(health);
        wall.add(buildingAnimation);
        wall.add(wallPiece);
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
