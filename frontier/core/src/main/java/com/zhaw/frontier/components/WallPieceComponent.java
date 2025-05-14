package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhaw.frontier.utils.TileOffset;
import java.util.HashMap;
import java.util.Map;

public class WallPieceComponent implements Component {

    public enum WallPiece {
        CORNER_TOP_LEFT,
        CORNER_TOP_RIGHT,
        CORNER_BOTTOM_LEFT,
        CORNER_BOTTOM_RIGHT,
        T_TOP,
        T_BOTTOM,
        T_LEFT,
        T_RIGHT,
        CROSS,
        STRAIGHT_HORIZONTAL_LEFT,
        STRAIGHT_HORIZONTAL_MIDDLE,
        STRAIGHT_HORIZONTAL_RIGHT,
        STRAIGHT_VERTICAL_MIDDLE,
        STRAIGHT_VERTICAL_UP,
        STRAIGHT_VERTICAL_DOWN,
        SINGLE,
        DESTROYED
    }

    public WallPiece currentWallPiece = WallPiece.SINGLE;

    public Map<WallPiece, HashMap<TileOffset, TextureRegion>> wallPieceTextures = new HashMap<>();
}
