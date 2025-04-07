package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.zhaw.frontier.utils.LayeredSprite;
import com.zhaw.frontier.utils.TileOffset;
import java.util.HashMap;
import java.util.List;

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
    public HashMap<TileOffset, List<LayeredSprite>> sprites = new HashMap<>();

    /**
     * Gibt an, wie viele Tiles breit/hoch das Objekt ist
     */
    public int widthInTiles;
    public int heightInTiles;
}
