package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhaw.frontier.utils.TileOffset;
import java.util.HashMap;

/**
 * contains sprite data for rendering an {@link com.badlogic.ashley.core.Entity}
 */
public class RenderComponent implements Component {

    public enum RenderType {
        BUILDING,
        ENEMY,
    }

    public RenderType renderType;

    /**
     * Sprites relativ zur Basis-Position
     */
    public HashMap<TileOffset, TextureRegion> sprites = new HashMap<>();

    /**
     * Animationen relativ zur Basis-Position
     */
    public int zIndex = 0;

    /**
     * Gibt an, wie viele Tiles breit/hoch das Objekt ist
     */
    public int widthInTiles = 1;
    public int heightInTiles = 1;
}
