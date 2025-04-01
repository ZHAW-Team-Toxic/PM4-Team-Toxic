package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;
import java.util.Map;

/** contains multiple animations for rendering an animated {@link com.badlogic.ashley.core.Entity} */
public class AnimationComponent implements Component {

    public enum AnimationType {
        WALK_DOWN,
        WALK_UP,
        WALK_LEFT,
        WALK_RIGHT,
        ATTACK,
        DIE,
    }

    /** stores animations by type */
    public Map<AnimationType, Animation<TextureRegion>> animations = new HashMap<>();

    /** current animation being played */
    public AnimationType currentAnimation;

    /** time passed since the animation started */
    public float stateTime = 0f;

    /** returns the current animation, or null if none is set */
    public Animation<TextureRegion> getCurrentAnimation() {
        return animations.get(currentAnimation);
    }
}
