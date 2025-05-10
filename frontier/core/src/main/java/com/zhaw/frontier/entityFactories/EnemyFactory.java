package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.behaviours.IdleBehaviourComponent;
import com.zhaw.frontier.components.behaviours.PathfindingBehaviourComponent;
import com.zhaw.frontier.components.behaviours.PatrolBehaviourComponent;
import com.zhaw.frontier.configs.AppProperties;
import com.zhaw.frontier.enums.EnemyType;
import com.zhaw.frontier.enums.Team;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.utils.TileOffset;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating different types of enemy {@link Entity} objects.
 * <p>
 * This class provides static methods to create enemies with different behavior components:
 * <ul>
 *     <li>{@link #createEnemy} - patrol behavior</li>
 *     <li>{@link #createIdleEnemy} - idle behavior</li>
 *     <li>{@link #createPathfindingEnemy} - actively seeks out targets</li>
 * </ul>
 * <p>
 * It also handles loading and caching shared animations for all enemy types.
 */
public class EnemyFactory {

    /** Cached shared animations for all enemy types, reused between entities to save memory. */
    private static final Map<
        EnemyType,
        EnumMap<EnemyAnimationComponent.EnemyAnimationType, Animation<TextureRegion>>
    > sharedAnimations = new HashMap<>();

    /**
     * Creates a pathfinding enemy with the given type and position.
     * This enemy uses {@link PathfindingBehaviourComponent} and targets entities with {@link WallPieceComponent}.
     *
     * @param enemyType the type of enemy (e.g., ORC)
     * @param x         the x-coordinate in tiles
     * @param y         the y-coordinate in tiles
     * @return the fully initialized enemy entity
     */
    public static Entity createPathfindingEnemy(EnemyType enemyType, float x, float y) {
        Entity enemy = createBaseEnemy(enemyType, x, y);
        enemy.add(new PathfindingBehaviourComponent(enemyType.getSpeed()));
        enemy.add(new TargetTypeComponent(HQComponent.class));
        return enemy;
    }

    /**
     * Creates an enemy with patrol behavior.
     *
     * @param enemyType the type of enemy
     * @param x         the x-coordinate
     * @param y         the y-coordinate
     * @return the patrol-enabled enemy entity
     */
    public static Entity createEnemy(EnemyType enemyType, float x, float y) {
        Entity enemy = createBaseEnemy(enemyType, x, y);
        enemy.add(new PatrolBehaviourComponent(enemyType.getSpeed()));
        return enemy;
    }

    /**
     * Creates an idle enemy that does not move or seek targets.
     *
     * @param enemyType the type of enemy
     * @param x         the x-coordinate
     * @param y         the y-coordinate
     * @return the idle enemy entity
     */
    public static Entity createIdleEnemy(EnemyType enemyType, float x, float y) {
        Entity enemy = createBaseEnemy(enemyType, x, y);
        enemy.add(new IdleBehaviourComponent());
        return enemy;
    }

    /**
     * Initializes enemy animations for each {@link EnemyType}, if not already done.
     * Loads frames from the shared texture atlas and sets them up in a reusable cache.
     */
    private static void initializeEnemyAnimations() {
        if (sharedAnimations.isEmpty()) {
            TextureAtlas enemyAtlas = AssetManagerInstance
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
                        AppProperties.ORC_WALK_DURATION,
                        enemyAtlas.findRegions("walk_down_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );

                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.WALK_LEFT,
                    new Animation<>(
                        AppProperties.ORC_WALK_DURATION,
                        enemyAtlas.findRegions("walk_left_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );

                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.WALK_RIGHT,
                    new Animation<>(
                        AppProperties.ORC_WALK_DURATION,
                        enemyAtlas.findRegions("walk_right_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );

                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.WALK_UP,
                    new Animation<>(
                        AppProperties.ORC_WALK_DURATION,
                        enemyAtlas.findRegions("walk_up_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );

                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.IDLE_DOWN,
                    new Animation<>(
                        AppProperties.ORC_IDLE_DURATION,
                        enemyAtlas.findRegions("idle_down_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );

                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.IDLE_LEFT,
                    new Animation<>(
                        AppProperties.ORC_IDLE_DURATION,
                        enemyAtlas.findRegions("idle_left_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );

                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.IDLE_RIGHT,
                    new Animation<>(
                        AppProperties.ORC_IDLE_DURATION,
                        enemyAtlas.findRegions("idle_right_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );

                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.IDLE_UP,
                    new Animation<>(
                        AppProperties.ORC_IDLE_DURATION,
                        enemyAtlas.findRegions("idle_up_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );

                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.ATTACK_DOWN,
                    new Animation<>(
                        AppProperties.ORC_ATTACK_DURATION,
                        enemyAtlas.findRegions("attack_down_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );

                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.ATTACK_LEFT,
                    new Animation<>(
                        AppProperties.ORC_ATTACK_DURATION,
                        enemyAtlas.findRegions("attack_left_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );

                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.ATTACK_RIGHT,
                    new Animation<>(
                        AppProperties.ORC_ATTACK_DURATION,
                        enemyAtlas.findRegions("attack_right_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );

                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.ATTACK_UP,
                    new Animation<>(
                        AppProperties.ORC_ATTACK_DURATION,
                        enemyAtlas.findRegions("attack_up_" + prefix),
                        Animation.PlayMode.LOOP
                    )
                );

                animations.put(
                    EnemyAnimationComponent.EnemyAnimationType.DEATH,
                    new Animation<>(
                        AppProperties.DEATH_FRAME_DURATION,
                        enemyAtlas.findRegions("death/explosion"),
                        Animation.PlayMode.LOOP
                    )
                );

                sharedAnimations.put(type, animations);
            }
        }
    }

    /**
     * Creates a base enemy entity with common components including health, rendering, collision, and animations.
     *
     * @param enemyType the enemy type, which defines stats and animation set
     * @param x         the spawn x-position
     * @param y         the spawn y-position
     * @return the base enemy entity
     */
    private static Entity createBaseEnemy(EnemyType enemyType, float x, float y) {
        Entity enemy = new Entity();

        // Basic stats
        PositionComponent position = new PositionComponent(x, y, 1, 1);
        VelocityComponent velocity = new VelocityComponent();
        HealthComponent health = new HealthComponent();
        health.maxHealth = enemyType.getHealth();
        health.currentHealth = enemyType.getHealth();
        enemy.add(health);

        // Rendering
        RenderComponent render = new RenderComponent(RenderComponent.RenderType.ENEMY, 10, 1, 1);

        // Animations
        initializeEnemyAnimations();
        EnemyAnimationComponent enemyAnimation = new EnemyAnimationComponent();
        enemyAnimation.animations = sharedAnimations.get(enemyType);

        // Set initial idle frame
        Animation<TextureRegion> initialAnimation = sharedAnimations
            .get(enemyType)
            .get(EnemyAnimationComponent.EnemyAnimationType.IDLE_DOWN);

        TextureRegion firstFrame = initialAnimation.getKeyFrame(0f);
        if (firstFrame != null) {
            TileOffset offset = new TileOffset(0, 0);
            render.sprites.put(offset, new TextureRegion(firstFrame));
        } else {
            Gdx.app.error("EnemyFactory", "No first frame found for WALK_DOWN");
        }

        AnimationQueueComponent queue = new AnimationQueueComponent();
        var collision = new CircleCollisionComponent();
        collision.collisionObject = new Circle(x, y, 1);

        // Add components to entity
        enemy.add(position);
        enemy.add(velocity);
        enemy.add(render);
        enemy.add(enemyAnimation);
        enemy.add(collision);
        enemy.add(queue);
        enemy.add(new EnemyComponent());
        enemy.add(new TeamComponent(Team.ENEMY));
        enemy.add(new AttackComponent());

        return enemy;
    }
}
