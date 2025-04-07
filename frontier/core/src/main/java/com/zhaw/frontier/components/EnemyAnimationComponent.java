package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.EnumMap;

/**
 * Animation component specifically for enemies, supporting a single active animation.
 */
public class EnemyAnimationComponent implements Component {

    public enum EnemyAnimationType {
        WALK_DOWN,
        WALK_UP,
        WALK_LEFT,
        WALK_RIGHT,
        ATTACK,
        IDLE,
        DIE,
    }

    /** All animations mapped by type */
    public EnumMap<EnemyAnimationType, Animation<TextureRegion>> animations = new EnumMap<>(
        EnemyAnimationType.class
    );

    /** Currently active animation */
    public EnemyAnimationType currentAnimation = null;

    /** Time passed for the current animation */
    public float stateTime = 0f;
}
