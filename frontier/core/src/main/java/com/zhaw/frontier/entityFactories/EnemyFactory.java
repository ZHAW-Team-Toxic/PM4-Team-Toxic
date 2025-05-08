package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.behaviours.IdleBehaviourComponent;
import com.zhaw.frontier.components.behaviours.PatrolBehaviourComponent;
import com.zhaw.frontier.enums.EnemyType;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.utils.TileOffset;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating enemy entities in the game.
 * This class provides methods to create different types of enemies with
 * specific behaviors.
 * Current enemies are:
 * - Orcs:
 * - Patrol
 * - Idle
 */
public class EnemyFactory {

    private static final float ORC_ATTACK_DURATION = 0.1f;
    private static final float ORC_IDLE_DURATION = 0.1f;
    private static final float ORC_WALK_DURATION = 0.1f;

    private static final Map<
        EnemyType,
        EnumMap<EnemyAnimationComponent.EnemyAnimationType, Animation<TextureRegion>>
    > sharedAnimations = new HashMap<>();

    /**
     * Creates a new enemy entity with the specified type and position.
     *
     * @param enemyType The type of the enemy to create.
     * @param x The x-coordinate of the enemy's position.
     * @param y The y-coordinate of the enemy's position.
     * @return The created enemy entity.
     */
    public static Entity createEnemy(EnemyType enemyType, float x, float y) {
        Entity enemy = createBaseEnemy(enemyType, x, y);
        enemy.add(new PatrolBehaviourComponent(enemyType.getSpeed()));

        return enemy;
    }

    /**
     * Creates a new idle enemy entity with the specified type and position.
     *
     * @param enemyType The type of the enemy to create.
     * @param x The x-coordinate of the enemy's position.
     * @param y The y-coordinate of the enemy's position.
     * @return The created idle enemy entity.
     */
    public static Entity createIdleEnemy(EnemyType enemyType, float x, float y) {
        Entity enemy = createBaseEnemy(enemyType, x, y);
        enemy.add(new IdleBehaviourComponent());

        return enemy;
    }

    private static void initializeEnemyAnimations() {
        if (sharedAnimations.isEmpty()) {
            TextureAtlas atlas = AssetManagerInstance
                .getManager()
                .get("packed/textures.atlas", TextureAtlas.class);

            for (EnemyType type : EnemyType.values()) {
                String prefix = type.getTypeName();

                EnumMap<
                    EnemyAnimationComponent.EnemyAnimationType,
                    Animation<TextureRegion>
                > animations = new EnumMap<>(EnemyAnimationComponent.EnemyAnimationType.class);

                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.WALK_DOWN,
                    new Animation<>(
                        ORC_WALK_DURATION,
                        atlas.findRegions("walk_down_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );
                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.WALK_LEFT,
                    new Animation<>(
                        ORC_WALK_DURATION,
                        atlas.findRegions("walk_left_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );
                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.WALK_RIGHT,
                    new Animation<>(
                        ORC_WALK_DURATION,
                        atlas.findRegions("walk_right_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );
                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.WALK_UP,
                    new Animation<>(
                        ORC_WALK_DURATION,
                        atlas.findRegions("walk_up_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );
                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.IDLE_DOWN,
                    new Animation<>(
                        ORC_IDLE_DURATION,
                        atlas.findRegions("idle_down_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );
                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.IDLE_LEFT,
                    new Animation<>(
                        ORC_IDLE_DURATION,
                        atlas.findRegions("idle_left_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );
                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.IDLE_RIGHT,
                    new Animation<>(
                        ORC_IDLE_DURATION,
                        atlas.findRegions("idle_right_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );
                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.IDLE_UP,
                    new Animation<>(
                        ORC_IDLE_DURATION,
                        atlas.findRegions("idle_up_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );
                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.ATTACK_DOWN,
                    new Animation<>(
                        ORC_ATTACK_DURATION,
                        atlas.findRegions("attack_down_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );
                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.ATTACK_LEFT,
                    new Animation<>(
                        ORC_ATTACK_DURATION,
                        atlas.findRegions("attack_left_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );
                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.ATTACK_RIGHT,
                    new Animation<>(
                        ORC_ATTACK_DURATION,
                        atlas.findRegions("attack_right_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );
                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.ATTACK_UP,
                    new Animation<>(
                        ORC_ATTACK_DURATION,
                        atlas.findRegions("attack_up_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );

                sharedAnimations.put(type, animations);
            }
        }
    }

    private static Entity createBaseEnemy(EnemyType enemyType, float x, float y) {
        Entity enemy = new Entity();

        // Position & Bewegung
        PositionComponent position = new PositionComponent(x, y, 1, 1);

        VelocityComponent velocity = new VelocityComponent();

        HealthComponent health = new HealthComponent();
        health.maxHealth = enemyType.getHealth();
        health.currentHealth = enemyType.getHealth();
        enemy.add(health);

        // Render-Komponente mit LayeredSprites
        RenderComponent render = new RenderComponent(RenderComponent.RenderType.ENEMY, 10, 1, 1);

        // Animationen
        initializeEnemyAnimations();
        EnemyAnimationComponent enemyAnimation = new EnemyAnimationComponent();
        enemyAnimation.animations = sharedAnimations.get(enemyType);

        // Initiales Frame setzen (z.B. WALK_DOWN → Frame 0)
        Animation<TextureRegion> initialAnimation = sharedAnimations
            .get(enemyType)
            .get(EnemyAnimationComponent.EnemyAnimationType.IDLE_DOWN);

        TextureRegion firstFrame = initialAnimation.getKeyFrame(0f);

        if (firstFrame != null) {
            TileOffset offset = new TileOffset(0, 0); // Basis-Layer
            render.sprites.put(offset, new TextureRegion(firstFrame));
        } else {
            Gdx.app.error("EnemyFactory", "No first frame found for WALK_DOWN");
        }

        AnimationQueueComponent queue = new AnimationQueueComponent();
        enemy.add(queue);

        // Collision
        var collision = new CircleCollisionComponent();
        collision.collisionObject = new Circle(x, y, 1);

        // Komponenten hinzufügen
        enemy.add(position);
        enemy.add(health);
        enemy.add(velocity);
        enemy.add(render);
        enemy.add(enemyAnimation);
        enemy.add(collision);
        enemy.add(new EnemyComponent());

        return enemy;
    }
}
