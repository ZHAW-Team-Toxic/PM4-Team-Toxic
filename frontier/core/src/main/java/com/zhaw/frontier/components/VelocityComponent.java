package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Contains velocity data for moving an {@link com.badlogic.ashley.core.Entity}
 */
public class VelocityComponent implements Component {
    /**
     * The velocity of the entity.
     */
    public Vector2 velocity = new Vector2();
}
