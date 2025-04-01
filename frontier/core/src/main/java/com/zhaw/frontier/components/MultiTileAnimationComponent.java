package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.List;

public class MultiTileAnimationComponent implements Component{
    public Animation<TextureRegion> topLeft;
    public Animation<TextureRegion> topRight;
    public Animation<TextureRegion> bottomLeft;
    public Animation<TextureRegion> bottomRight;

    /** current animation being played */
    public AnimationComponent.AnimationType currentAnimation;

    /** time passed since the animation started */
    public float stateTime = 0f;

    public enum MultiTileAnimationType {
        FILLING,
    }

    public MultiTileAnimationComponent(
        Animation<TextureRegion> topLeft,
        Animation<TextureRegion> topRight,
        Animation<TextureRegion> bottomLeft,
        Animation<TextureRegion> bottomRight
    ) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    public Animation<TextureRegion> get(String quadrant) {
        return switch (quadrant) {
            case "topLeft" -> topLeft;
            case "topRight" -> topRight;
            case "bottomLeft" -> bottomLeft;
            case "bottomRight" -> bottomRight;
            default -> null;
        };
    }

    /** returns the current animation, or null if none is set */
    public List<Animation<TextureRegion>> getCurrentAnimation() {
        return new ArrayList<>(List.of(topLeft, topRight, bottomLeft, bottomRight));
    }
}
