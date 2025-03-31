package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/** Defines the position in the world for a {@link com.badlogic.ashley.core.Entity} */
public class PositionComponent implements Component {

    /**
     * The position of the entity.
     */
    public Vector2 currentPosition = new Vector2();

    /**
     * The previous position of the entity.
     */
    public Vector2 previousPosition = new Vector2();

    public Vector2 lookingDirection = new Vector2();
}
