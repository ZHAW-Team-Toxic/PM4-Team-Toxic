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
import com.zhaw.frontier.utils.LayeredSprite;
import com.zhaw.frontier.utils.TileOffset;
import java.util.*;

public class EnemyFactory {

    public static final float ORC_ATTACK_DURATION = 0.1f;
    public static final float ORC_IDLE_DURATION = 0.1f;
    public static final float ORC_WALK_DURATION = 0.1f;

    private static final EnumMap<
        EnemyAnimationComponent.EnemyAnimationType,
        Animation<TextureRegion>
    > sharedAnimations = new EnumMap<>(EnemyAnimationComponent.EnemyAnimationType.class);

    /**
     * Creates a new enemy entity with patrol behavior.
     * @param x The x-coordinate of the enemy's position.
     * @param y The y-coordinate of the enemy's position.
     * @param assetManager The asset manager for loading assets.
     * @return The created enemy entity.
     */
    public static Entity createPatrolEnemy(float x, float y, AssetManager assetManager) {
        Entity enemy = createBaseEnemy(x, y, assetManager);
        enemy.add(new PatrolBehaviourComponent(10f));
        return enemy;
    }

    /**
     * Creates a new enemy entity with idle behavior.
     * @param x The x-coordinate of the enemy's position.
     * @param y The y-coordinate of the enemy's position.
     * @param assetManager The asset manager for loading assets.
     * @return The created enemy entity.
     */
    public static Entity createIdleEnemy(float x, float y, AssetManager assetManager) {
        Entity enemy = createBaseEnemy(x, y, assetManager);
        enemy.add(new IdleBehaviourComponent());
        return enemy;
    }

    private static void initializeAnimations(AssetManager assetManager) {
        if (sharedAnimations.isEmpty()) {
            TextureAtlas atlas = assetManager.get(
                "packed/enemies/enemyAtlas.atlas",
                TextureAtlas.class
            );

            sharedAnimations.put(
                EnemyAnimationComponent.EnemyAnimationType.WALK_DOWN,
                new Animation<>(
                    ORC_WALK_DURATION,
                    atlas.findRegions("walk_down_orc"),
                    Animation.PlayMode.LOOP
                )
            );
            sharedAnimations.put(
                EnemyAnimationComponent.EnemyAnimationType.WALK_LEFT,
                new Animation<>(
                    ORC_WALK_DURATION,
                    atlas.findRegions("walk_left_orc"),
                    Animation.PlayMode.LOOP
                )
            );
            sharedAnimations.put(
                EnemyAnimationComponent.EnemyAnimationType.WALK_RIGHT,
                new Animation<>(
                    ORC_WALK_DURATION,
                    atlas.findRegions("walk_right_orc"),
                    Animation.PlayMode.LOOP
                )
            );
            sharedAnimations.put(
                EnemyAnimationComponent.EnemyAnimationType.WALK_UP,
                new Animation<>(
                    ORC_WALK_DURATION,
                    atlas.findRegions("walk_up_orc"),
                    Animation.PlayMode.LOOP
                )
            );
            sharedAnimations.put(
                EnemyAnimationComponent.EnemyAnimationType.IDLE_DOWN,
                new Animation<>(
                    ORC_IDLE_DURATION,
                    atlas.findRegions("idle_down_orc"),
                    Animation.PlayMode.LOOP
                )
            );
            sharedAnimations.put(
                EnemyAnimationComponent.EnemyAnimationType.IDLE_LEFT,
                new Animation<>(
                    ORC_IDLE_DURATION,
                    atlas.findRegions("idle_left_orc"),
                    Animation.PlayMode.LOOP
                )
            );
            sharedAnimations.put(
                EnemyAnimationComponent.EnemyAnimationType.IDLE_RIGHT,
                new Animation<>(
                    ORC_IDLE_DURATION,
                    atlas.findRegions("idle_right_orc"),
                    Animation.PlayMode.LOOP
                )
            );
            sharedAnimations.put(
                EnemyAnimationComponent.EnemyAnimationType.IDLE_UP,
                new Animation<>(
                    ORC_IDLE_DURATION,
                    atlas.findRegions("idle_up_orc"),
                    Animation.PlayMode.LOOP
                )
            );
            sharedAnimations.put(
                EnemyAnimationComponent.EnemyAnimationType.ATTACK_DOWN,
                new Animation<>(
                    ORC_ATTACK_DURATION,
                    atlas.findRegions("attack_down_orc"),
                    Animation.PlayMode.LOOP
                )
            );
            sharedAnimations.put(
                EnemyAnimationComponent.EnemyAnimationType.ATTACK_LEFT,
                new Animation<>(
                    ORC_ATTACK_DURATION,
                    atlas.findRegions("attack_left_orc"),
                    Animation.PlayMode.LOOP
                )
            );
            sharedAnimations.put(
                EnemyAnimationComponent.EnemyAnimationType.ATTACK_RIGHT,
                new Animation<>(
                    ORC_ATTACK_DURATION,
                    atlas.findRegions("attack_right_orc"),
                    Animation.PlayMode.LOOP
                )
            );
            sharedAnimations.put(
                EnemyAnimationComponent.EnemyAnimationType.ATTACK_UP,
                new Animation<>(
                    ORC_ATTACK_DURATION,
                    atlas.findRegions("attack_up_orc"),
                    Animation.PlayMode.LOOP
                )
            );
        }
    }

    private static Entity createBaseEnemy(float x, float y, AssetManager assetManager) {
        Entity enemy = new Entity();

        // Position & Bewegung
        PositionComponent position = new PositionComponent();
        position.basePosition.set(x, y);
        position.widthInTiles = 1;
        position.heightInTiles = 1;

        VelocityComponent velocity = new VelocityComponent();

        // Render-Komponente mit LayeredSprites
        RenderComponent render = new RenderComponent();
        render.renderType = RenderComponent.RenderType.ENEMY;
        render.widthInTiles = 1;
        render.heightInTiles = 1;

        // Animationen
        initializeAnimations(assetManager);
        EnemyAnimationComponent enemyAnimation = new EnemyAnimationComponent();
        enemyAnimation.animations = sharedAnimations;

        // Initiales Frame setzen (z.B. WALK_DOWN → Frame 0)
        Animation<TextureRegion> initialAnimation = sharedAnimations.get(
            EnemyAnimationComponent.EnemyAnimationType.WALK_DOWN
        );
        TextureRegion firstFrame = initialAnimation.getKeyFrame(0f);

        if (firstFrame != null) {
            TileOffset offset = new TileOffset(0, 0);
            List<LayeredSprite> layers = new ArrayList<>();
            layers.add(new LayeredSprite(firstFrame, 0)); // Basis-Layer
            render.sprites.put(offset, layers);
        } else {
            Gdx.app.error("EnemyFactory", "Kein erstes Frame gefunden für WALK_DOWN");
        }

        AnimationQueueComponent queue = new AnimationQueueComponent();
        enemy.add(queue);

        // Komponenten hinzufügen
        enemy.add(position);
        enemy.add(velocity);
        enemy.add(render);
        enemy.add(enemyAnimation);
        enemy.add(new EnemyComponent());

        return enemy;
    }
}
