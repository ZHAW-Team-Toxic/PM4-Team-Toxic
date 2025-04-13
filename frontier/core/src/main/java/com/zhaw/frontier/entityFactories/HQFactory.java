package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.RoundAnimationComponent;
import com.zhaw.frontier.utils.TileOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * A factory class for creating Headquarters (HQ) entities.
 */
public class HQFactory {

    private static final float SAND_CLOCK_FRAME_DURATION = 0.2f;
    private static final int HQ_TILE_SIZE = 4;

    private static final Map<
        Enum<?>,
        HashMap<TileOffset, Animation<TextureRegion>>
    > clockAnimationCache = new HashMap<>();

    /**
     * Creates a new Headquarters (HQ) entity with a sand clock animation.
     * @param engine the engine to which the entity will be added
     * @param assetManager the asset manager for loading assets
     * @return the created HQ entity
     */
    public static Entity createSandClockHQ(Engine engine, AssetManager assetManager) {
        Entity hq = createDefaultHQ(engine);
        initAnimationCache(assetManager);
        applyComponents(hq);
        return hq;
    }

    private static Entity createDefaultHQ(Engine engine) {
        Entity hq = engine.createEntity();
        hq.add(new PositionComponent());
        hq.add(new OccupiesTilesComponent());
        hq.add(new BuildingAnimationComponent());
        hq.add(new RoundAnimationComponent());
        hq.add(new AnimationQueueComponent());
        hq.add(new RenderComponent());
        return hq;
    }

    private static void initAnimationCache(AssetManager assetManager) {
        if (clockAnimationCache.isEmpty()) {
            TextureAtlas atlas = assetManager.get(
                "packed/buildings/buildingAtlas.atlas",
                TextureAtlas.class
            );

            HashMap<TileOffset, Animation<TextureRegion>> clockAnimation = new HashMap<>();

            // D5 – animated
            clockAnimation.put(
                new TileOffset(1, 1),
                new Animation<>(
                    SAND_CLOCK_FRAME_DURATION,
                    atlas.findRegions("hq_sandclock_D5"),
                    Animation.PlayMode.LOOP
                )
            );

            //D6 – animated
            clockAnimation.put(
                new TileOffset(2, 1),
                new Animation<>(
                    SAND_CLOCK_FRAME_DURATION,
                    atlas.findRegions("hq_sandclock_D6"),
                    Animation.PlayMode.LOOP
                )
            );

            // D9 – animated
            clockAnimation.put(
                new TileOffset(1, 2),
                new Animation<>(
                    SAND_CLOCK_FRAME_DURATION,
                    atlas.findRegions("hq_sandclock_D9"),
                    Animation.PlayMode.LOOP
                )
            );

            // All other tiles are static → use same region multiple times (or once)
            // Example: tile 00
            Array<TextureRegion> hq00Frames = new Array<>();
            hq00Frames.add(atlas.findRegion("hq_sandclock_00_S"));
            clockAnimation.put(
                new TileOffset(0, 0),
                new Animation<>(SAND_CLOCK_FRAME_DURATION, hq00Frames, Animation.PlayMode.LOOP)
            );

            // Repeat for all other static tiles
            // tile 01
            Array<TextureRegion> hq01Frames = new Array<>();
            hq01Frames.add(atlas.findRegion("hq_sandclock_10_S"));
            clockAnimation.put(
                new TileOffset(1, 0),
                new Animation<>(SAND_CLOCK_FRAME_DURATION, hq01Frames, Animation.PlayMode.LOOP)
            );

            // tile 02
            Array<TextureRegion> hq02Frames = new Array<>();
            hq02Frames.add(atlas.findRegion("hq_sandclock_20_S"));
            clockAnimation.put(
                new TileOffset(2, 0),
                new Animation<>(SAND_CLOCK_FRAME_DURATION, hq02Frames, Animation.PlayMode.LOOP)
            );

            // tile 03
            Array<TextureRegion> hq03Frames = new Array<>();
            hq03Frames.add(atlas.findRegion("hq_sandclock_30_S"));
            clockAnimation.put(
                new TileOffset(3, 0),
                new Animation<>(SAND_CLOCK_FRAME_DURATION, hq03Frames, Animation.PlayMode.LOOP)
            );

            // tile 04
            Array<TextureRegion> hq04Frames = new Array<>();
            hq04Frames.add(atlas.findRegion("hq_sandclock_01_S"));
            clockAnimation.put(
                new TileOffset(0, 1),
                new Animation<>(SAND_CLOCK_FRAME_DURATION, hq04Frames, Animation.PlayMode.LOOP)
            );

            // tile 07
            Array<TextureRegion> hq07Frames = new Array<>();
            hq07Frames.add(atlas.findRegion("hq_sandclock_31_S"));
            clockAnimation.put(
                new TileOffset(3, 1),
                new Animation<>(SAND_CLOCK_FRAME_DURATION, hq07Frames, Animation.PlayMode.LOOP)
            );

            // tile 08
            Array<TextureRegion> hq08Frames = new Array<>();
            hq08Frames.add(atlas.findRegion("hq_sandclock_02_S"));
            clockAnimation.put(
                new TileOffset(0, 2),
                new Animation<>(SAND_CLOCK_FRAME_DURATION, hq08Frames, Animation.PlayMode.LOOP)
            );

            // tile 10
            Array<TextureRegion> hq10Frames = new Array<>();
            hq10Frames.add(atlas.findRegion("hq_sandclock_22_S"));
            clockAnimation.put(
                new TileOffset(2, 2),
                new Animation<>(SAND_CLOCK_FRAME_DURATION, hq10Frames, Animation.PlayMode.LOOP)
            );

            // tile 11
            Array<TextureRegion> hq11Frames = new Array<>();
            hq11Frames.add(atlas.findRegion("hq_sandclock_32_S"));
            clockAnimation.put(
                new TileOffset(3, 2),
                new Animation<>(SAND_CLOCK_FRAME_DURATION, hq11Frames, Animation.PlayMode.LOOP)
            );

            // tile 12
            Array<TextureRegion> hq12Frames = new Array<>();
            hq12Frames.add(atlas.findRegion("hq_sandclock_03_S"));
            clockAnimation.put(
                new TileOffset(0, 3),
                new Animation<>(SAND_CLOCK_FRAME_DURATION, hq12Frames, Animation.PlayMode.LOOP)
            );

            // tile 13
            Array<TextureRegion> hq13Frames = new Array<>();
            hq13Frames.add(atlas.findRegion("hq_sandclock_13_S"));
            clockAnimation.put(
                new TileOffset(1, 3),
                new Animation<>(SAND_CLOCK_FRAME_DURATION, hq13Frames, Animation.PlayMode.LOOP)
            );

            // tile 14
            Array<TextureRegion> hq14Frames = new Array<>();
            hq14Frames.add(atlas.findRegion("hq_sandclock_23_S"));
            clockAnimation.put(
                new TileOffset(2, 3),
                new Animation<>(SAND_CLOCK_FRAME_DURATION, hq14Frames, Animation.PlayMode.LOOP)
            );

            // tile 15
            Array<TextureRegion> hq15Frames = new Array<>();
            hq15Frames.add(atlas.findRegion("hq_sandclock_33_S"));
            clockAnimation.put(
                new TileOffset(3, 3),
                new Animation<>(SAND_CLOCK_FRAME_DURATION, hq15Frames, Animation.PlayMode.LOOP)
            );

            clockAnimationCache.put(
                RoundAnimationComponent.HQRoundAnimationType.SAND_CLOCK,
                clockAnimation
            );
            // Add more animations as needed
        }
    }

    private static void applyComponents(Entity hq) {
        PositionComponent position = hq.getComponent(PositionComponent.class);
        position.widthInTiles = HQ_TILE_SIZE;
        position.heightInTiles = HQ_TILE_SIZE;

        RenderComponent render = hq.getComponent(RenderComponent.class);
        render.renderType = RenderComponent.RenderType.BUILDING;
        render.widthInTiles = HQ_TILE_SIZE;
        render.heightInTiles = HQ_TILE_SIZE;

        RoundAnimationComponent roundAnimation = hq.getComponent(RoundAnimationComponent.class);
        roundAnimation.currentFrameIndex = 0;

        HashMap<TileOffset, Animation<TextureRegion>> tileAnimations = clockAnimationCache.get(
            RoundAnimationComponent.HQRoundAnimationType.SAND_CLOCK
        );

        for (var entry : tileAnimations.entrySet()) {
            TileOffset offset = entry.getKey();
            Animation<TextureRegion> anim = entry.getValue();

            Object[] rawFrames = anim.getKeyFrames();
            Array<TextureRegion> frames = new Array<>();
            for (Object frame : rawFrames) {
                frames.add((TextureRegion) frame);
            }

            roundAnimation.frames.put(offset, frames);

            // Layer 0: base frame (static or first frame)
            TextureRegion baseFrame = frames.first();
            render.sprites.put(offset, new TextureRegion(baseFrame));
        }
        render.zIndex = 10;

        Gdx.app.debug(
            "HQFactory",
            "HQ created with animation frames: " + roundAnimation.frames.size()
        );
    }
}
