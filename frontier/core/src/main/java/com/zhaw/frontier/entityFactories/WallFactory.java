package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhaw.frontier.components.AnimationQueueComponent;
import com.zhaw.frontier.components.BuildingAnimationComponent;
import com.zhaw.frontier.components.BuildingAnimationComponent.BuildingAnimationType;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.components.OccupiesTilesComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.TeamComponent;
import com.zhaw.frontier.components.WallComponent;
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
        Enum<?>,
        HashMap<TileOffset, Animation<TextureRegion>>
    > woodWallAnimationCache = new HashMap<>();
    private static final Map<
        Enum<?>,
        HashMap<TileOffset, Animation<TextureRegion>>
    > stoneWallAnimationCache = new HashMap<>();
    private static final Map<
        Enum<?>,
        HashMap<TileOffset, Animation<TextureRegion>>
    > ironWallAnimationCache = new HashMap<>();

    private static final EnumMap<
        BuildingAnimationType,
        HashMap<TileOffset, Animation<TextureRegion>>
    > sharedAnimations = new EnumMap<>(BuildingAnimationType.class);

    public Entity createWoodWall(Engine engine, float x, float y) {
        return createDefaultWall(engine, x, y);
    }

    public Entity createStoneWall(Engine engine, float x, float y) {
        return createDefaultWall(engine, x, y);
    }

    public Entity createIronWall(Engine engine, float x, float y) {
        return createDefaultWall(engine, x, y);
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
    public static Entity createDefaultWall(Engine engine, float x, float y) {
        Entity wall = engine.createEntity();

        RenderComponent render = new RenderComponent(RenderComponent.RenderType.BUILDING, 10, 1, 1);
        TextureRegion region = new TextureRegion(createPlaceHolder());
        render.sprites.put(new TileOffset(0, 0), region);

        initializeSharedWallAnimations();

        BuildingAnimationComponent buildingAnimationComponent = new BuildingAnimationComponent();
        buildingAnimationComponent.animations = sharedAnimations;

        wall.add(render);
        wall.add(new PositionComponent(x, y, 1, 1));
        wall.add(new OccupiesTilesComponent());
        wall.add(new HealthComponent());
        wall.add(buildingAnimationComponent);
        wall.add(new AnimationQueueComponent());
        wall.add(new WallComponent());
        wall.add(new TeamComponent(Team.PLAYER));

        return wall;
    }

    /**
     * Creates a placeholder texture for the wall.
     * <p>
     * This method generates a 12x12 texture using a {@link Pixmap} in
     * {@link Pixmap.Format#RGBA8888} format.
     * The pixmap is filled with a red color and then converted into a
     * {@link Texture}.
     * The generated texture serves as a temporary placeholder until an actual wall
     * texture is provided.
     * </p>
     *
     * @return a {@link Texture} generated from the pixmap.
     */
    private static Texture createPlaceHolder() {
        Pixmap pixmap = new Pixmap(
            AppProperties.TILE_SIZE,
            AppProperties.TILE_SIZE,
            Pixmap.Format.RGBA8888
        );
        pixmap.setColor(64 / 255f, 0, 0, 1); // red tone
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
