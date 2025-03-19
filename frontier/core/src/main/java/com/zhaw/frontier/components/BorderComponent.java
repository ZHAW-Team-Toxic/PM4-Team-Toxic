package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

/**
 * Contains a border for entities.
 */
public class BorderComponent implements Component {

    /**
     * The border of the entity in the x direction.
     */
    public float borderX = 0.0f;
    /**
     * The border of the entity in the y direction.
     */
    public float borderY = 0.0f;
}
