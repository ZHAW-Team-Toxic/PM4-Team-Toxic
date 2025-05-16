package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * If an entity with RangeVisibleComponent the range will be drawn as a circle.
 */
public class RangeComponent implements Component {
    public Sprite rangeTexture;
    public int range = 256;
}
