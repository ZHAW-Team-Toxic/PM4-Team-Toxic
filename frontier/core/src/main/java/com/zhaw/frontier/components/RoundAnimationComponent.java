package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.zhaw.frontier.utils.TileOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * Animation component for round-based animations.
 */
public class RoundAnimationComponent implements Component {

    /**
     * Enum representing the different types of round animations.
     * Each type corresponds to a specific animation state.
     */
    public enum HQRoundAnimationType {
        SAND_CLOCK,
    }

    /**
     * All available animations by type.
     * Each animation is represented as a map of tile offsets to texture regions.
     */
    public Map<TileOffset, Array<TextureRegion>> frames = new HashMap<>();

    /**
     * The current animation frame index. Used for optimizing the animation process.
     * (If a frame does not change it should not be redrawn)
     */
    public int currentFrameIndex = 0;
}
