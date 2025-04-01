package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.zhaw.frontier.components.MultiTileAnimationComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;

import java.util.EnumMap;

/**
 * A factory class for creating Headquarters (HQ) entities.
 * <p>
 * This factory provides a method to create a default HQ entity which is initialized with
 * a {@link PositionComponent} and a {@link RenderComponent}. The {@code RenderComponent}
 * is configured to render the entity as a building.
 * </p>
 */
public class HQFactory {

    private static final EnumMap<MultiTileAnimationComponent.MultiTileAnimationType, MultiTileAnimationComponent>
        sharedAnimations = new EnumMap<>(MultiTileAnimationComponent.MultiTileAnimationType.class);

    public static Entity createDefaultHQ(Engine engine, AssetManager assetManager) {
        initAnimation(assetManager);

        Entity hq = engine.createEntity();
        hq.add(new PositionComponent());

        RenderComponent render = new RenderComponent();
        render.renderType = RenderComponent.RenderType.BUILDING;
        render.textureRegion = null;

        MultiTileAnimationComponent shared = sharedAnimations.get(
            MultiTileAnimationComponent.MultiTileAnimationType.FILLING
        );

        MultiTileAnimationComponent clone = new MultiTileAnimationComponent(
            cloneAnimation(shared.get("topLeft")),
            cloneAnimation(shared.get("topRight")),
            cloneAnimation(shared.get("bottomLeft")),
            cloneAnimation(shared.get("bottomRight"))
        );

        hq.add(render);
        hq.add(clone);

        return hq;
    }

    private static void initAnimation(AssetManager assetManager) {
        if (!sharedAnimations.isEmpty()) return;

        TextureAtlas atlas = assetManager.get(
            "packed/animationDemoThursday.atlas",
            TextureAtlas.class
        );

        Array<TextureAtlas.AtlasRegion> topLeftRegions = atlas.findRegions("hq_sandclock_topleft");
        Array<TextureAtlas.AtlasRegion> topRightRegions = atlas.findRegions("hq_sandclock_topright");
        Array<TextureAtlas.AtlasRegion> bottomLeftRegions = atlas.findRegions("hq_sandclock_bottomleft");
        Array<TextureAtlas.AtlasRegion> bottomRightRegions = atlas.findRegions("hq_sandclock_bottomright");

        MultiTileAnimationComponent fillingAnimation = new MultiTileAnimationComponent(
            new Animation<>(0.20f, topLeftRegions, Animation.PlayMode.LOOP),
            new Animation<>(0.20f, topRightRegions, Animation.PlayMode.LOOP),
            new Animation<>(0.20f, bottomLeftRegions, Animation.PlayMode.LOOP),
            new Animation<>(0.20f, bottomRightRegions, Animation.PlayMode.LOOP)
        );

        sharedAnimations.put(
            MultiTileAnimationComponent.MultiTileAnimationType.FILLING,
            fillingAnimation
        );
    }

    private static Animation<TextureRegion> cloneAnimation(Animation<TextureRegion> source) {
        return new Animation<>(source.getFrameDuration(), new Array<>(source.getKeyFrames()), source.getPlayMode());
    }
}
