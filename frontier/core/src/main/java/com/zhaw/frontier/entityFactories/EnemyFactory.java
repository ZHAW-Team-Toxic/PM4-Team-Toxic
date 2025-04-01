package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.behaviours.IdleBehaviourComponent;
import com.zhaw.frontier.components.behaviours.PatrolBehaviourComponent;
import java.util.EnumMap;

/**
 * Factory class responsible for creating enemy entities with their
 * initial components and visual representation.
 */
public class EnemyFactory {

    private static boolean animationsInitialized = false;
    private static final EnumMap<
        AnimationComponent.AnimationType,
        Animation<TextureRegion>
    > sharedAnimations = new EnumMap<>(AnimationComponent.AnimationType.class);

    public static void initializeAnimations(AssetManager assetManager) {
        if (animationsInitialized) return;

        TextureAtlas atlas = assetManager.get(
            "packed/animationsDemoOrc2.atlas",
            TextureAtlas.class
        );

        sharedAnimations.put(
            AnimationComponent.AnimationType.WALK_DOWN,
            new Animation<>(0.20f, atlas.findRegions("walk_down_orc"), Animation.PlayMode.LOOP)
        );
        sharedAnimations.put(
            AnimationComponent.AnimationType.WALK_LEFT,
            new Animation<>(0.20f, atlas.findRegions("walk_left_orc"), Animation.PlayMode.LOOP)
        );
        sharedAnimations.put(
            AnimationComponent.AnimationType.WALK_RIGHT,
            new Animation<>(0.20f, atlas.findRegions("walk_right_orc"), Animation.PlayMode.LOOP)
        );
        sharedAnimations.put(
            AnimationComponent.AnimationType.WALK_UP,
            new Animation<>(0.20f, atlas.findRegions("walk_up_orc"), Animation.PlayMode.LOOP)
        );

        animationsInitialized = true;
        Gdx.app.debug("EnemyFactory", "Animations initialized and cached.");
    }

    public static Entity createPatrolEnemy(float x, float y, AssetManager assetManager) {
        Entity enemy = createBaseEnemy(x, y, assetManager);
        enemy.add(new PatrolBehaviourComponent(5f));
        return enemy;
    }

    public static Entity createIdleEnemy(float x, float y, AssetManager assetManager) {
        Entity enemy = createBaseEnemy(x, y, assetManager);
        enemy.add(new IdleBehaviourComponent());
        return enemy;
    }

    private static Entity createBaseEnemy(float x, float y, AssetManager assetManager) {
        initializeAnimations(assetManager);

        Entity enemy = new Entity();

        PositionComponent position = new PositionComponent();
        position.currentPosition.set(x, y);

        VelocityComponent velocity = new VelocityComponent();

        RenderComponent renderComponent = new RenderComponent();
        renderComponent.renderType = RenderComponent.RenderType.ENEMY;

        AnimationComponent animationComponent = new AnimationComponent();
        for (var entry : sharedAnimations.entrySet()) {
            animationComponent.animations.put(entry.getKey(), entry.getValue());
        }
        animationComponent.stateTime = 0f;

        renderComponent.textureRegion =
        sharedAnimations.get(AnimationComponent.AnimationType.WALK_LEFT).getKeyFrame(0);

        enemy.add(renderComponent);
        enemy.add(position);
        enemy.add(velocity);
        enemy.add(animationComponent);
        enemy.add(new EnemyComponent());

        return enemy;
    }
}
