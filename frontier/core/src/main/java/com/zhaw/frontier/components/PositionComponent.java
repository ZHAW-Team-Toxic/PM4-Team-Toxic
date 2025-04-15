package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/** Defines the position in the world for a {@link com.badlogic.ashley.core.Entity} */
public class PositionComponent implements Component {

    /**
     * The position of the entity.
     */
    public Vector2 basePosition = new Vector2();

    /**
     * The previous position of the entity.
     */
    public Vector2 previousPosition = new Vector2();

    /**
     * The looking direction of the entity.
     */
    public Vector2 lookingDirection = new Vector2();

    /**
     * The tile offset of the entity.
     */
    public int widthInTiles;
    /**
     * The tile offset of the entity.
     */
    public int heightInTiles;

    public PositionComponent() {}

    public PositionComponent(float x, float y) {
        this.basePosition.x = x;
        this.basePosition.y = y;
    }

    public PositionComponent(float x, float y, int widthInTiles, int heightInTiles) {
        this.basePosition.x = x;
        this.basePosition.y = y;
        this.widthInTiles = widthInTiles;
        this.heightInTiles = heightInTiles;
    }
}
