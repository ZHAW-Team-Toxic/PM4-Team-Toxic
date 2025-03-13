package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * AnimationComponent
 */
public class AnimationComponent implements Component {
    public Animation<TextureRegion> animation;
}
