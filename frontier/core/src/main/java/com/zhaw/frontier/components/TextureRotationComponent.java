package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

/**
 * Keeps track of what direction a component is looking at so the correct
 * texture can be drawn.
 */

public class TextureRotationComponent implements Component {

    public float rotation;
}
