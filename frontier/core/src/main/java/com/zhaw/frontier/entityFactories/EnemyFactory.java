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

    private static final EnumMap<
        EnemyAnimationComponent.EnemyAnimationType,
        Animation<TextureRegion>
    > sharedAnimations = new EnumMap<>(EnemyAnimationComponent.EnemyAnimationType.class);

    public static Entity createPatrolEnemy(float x, float y, AssetManager assetManager) {
        Entity enemy = createBaseEnemy(x, y, assetManager);
        enemy.add(new PatrolBehaviourComponent(10f));
        return enemy;
    }

    public static Entity createIdleEnemy(float x, float y, AssetManager assetManager) {
        Entity enemy = createBaseEnemy(x, y, assetManager);
        enemy.add(new IdleBehaviourComponent());
        return enemy;
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
        render.sprites = new HashMap<>();

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
            Gdx.app.error("EnemyFactory", "❌ Kein erstes Frame gefunden für WALK_DOWN");
        }

        Gdx.app.debug("EnemyFactory", "Animations: " + enemyAnimation.animations.size());

        // Komponenten hinzufügen
        enemy.add(position);
        enemy.add(velocity);
        enemy.add(render);
        enemy.add(enemyAnimation);
        enemy.add(new EnemyComponent());

        return enemy;
    }

    public static void initializeAnimations(AssetManager assetManager) {
        if (sharedAnimations.isEmpty()) {
            TextureAtlas atlas = assetManager.get(
                "packed/enemies/enemieAtlas.atlas",
                TextureAtlas.class
            );

            sharedAnimations.put(
                EnemyAnimationComponent.EnemyAnimationType.WALK_DOWN,
                new Animation<>(0.20f, atlas.findRegions("walk_down_orc"), Animation.PlayMode.LOOP)
            );
            sharedAnimations.put(
                EnemyAnimationComponent.EnemyAnimationType.WALK_LEFT,
                new Animation<>(0.20f, atlas.findRegions("walk_left_orc"), Animation.PlayMode.LOOP)
            );
            sharedAnimations.put(
                EnemyAnimationComponent.EnemyAnimationType.WALK_RIGHT,
                new Animation<>(0.20f, atlas.findRegions("walk_right_orc"), Animation.PlayMode.LOOP)
            );
            sharedAnimations.put(
                EnemyAnimationComponent.EnemyAnimationType.WALK_UP,
                new Animation<>(0.20f, atlas.findRegions("walk_up_orc"), Animation.PlayMode.LOOP)
            );
        }
    }
}
