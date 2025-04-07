package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.zhaw.frontier.utils.TileOffset;
import java.util.HashMap;
import java.util.Map;

public class HQRoundAnimationComponent implements Component {

    public enum HQRoundAnimationType {
        SAND_CLOCK,
    }

    public Map<TileOffset, Array<TextureRegion>> frames = new HashMap<>();
    public int currentFrameIndex = 0;
}
