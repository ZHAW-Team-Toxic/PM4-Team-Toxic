package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.EnumMap;

/**
 * Animation component specifically for enemies, supporting a single active animation.
 */
public class EnemyAnimationComponent implements Component {

    /**
     * Enum representing the different types of enemy animations.
     * Each type corresponds to a specific animation state.
     */
    public enum EnemyAnimationType {
        WALK_DOWN,
        WALK_UP,
        WALK_LEFT,
        WALK_RIGHT,
        IDLE_DOWN,
        IDLE_UP,
        IDLE_LEFT,
        IDLE_RIGHT,
        ATTACK_DOWN,
        ATTACK_UP,
        ATTACK_LEFT,
        ATTACK_RIGHT,
        DEATH,
    }

    /** All animations mapped by type */
    public EnumMap<EnemyAnimationType, Animation<TextureRegion>> animations = new EnumMap<>(
        EnemyAnimationType.class
    );

    /** Currently active animation */
    public EnemyAnimationType currentAnimation = EnemyAnimationType.IDLE_DOWN;

    /** Time passed for the current animation */
    public float stateTime = 0f;

    /** The index of the last frame displayed */
    public int lastFrameIndex = -1;
}
