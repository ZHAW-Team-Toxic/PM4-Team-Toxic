package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;

/** contains sprite data for rendering an {@link com.badlogic.ashley.core.Entity} */
public class RenderComponent implements Component {

    /**
     * What type of render this is
     */
    public enum RenderType {
        BUILDING,
        ENEMY,
    }

    /**
     * the sprite to render
     */
    public Sprite sprite;

    /**
     * the type of render
     */
    public RenderType renderType;
}
