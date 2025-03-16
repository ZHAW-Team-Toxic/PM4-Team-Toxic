package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/** contains animation frames for rendering an animated {@link com.badlogic.ashley.core.Entity} */
public class AnimationComponent implements Component {
    /** the animation to render */
    public Animation<TextureRegion> animation;
}
