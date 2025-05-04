package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Circle;

/**
 * This class enables collision detection for within the radius of the
 * collisionObject
 */
public class CircleCollisionComponent implements Component {

    public Circle collisionObject;
}
