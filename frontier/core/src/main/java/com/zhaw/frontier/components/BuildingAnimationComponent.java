package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhaw.frontier.utils.TileOffset;
import java.util.*;

/**
 * Animation component for buildings.
 * Supports multiple parallel animations.
 */
public class BuildingAnimationComponent implements Component {

    /**
     * Enum representing the different types of building animations.
     * Each type corresponds to a specific animation state.
     */
    public enum BuildingAnimationType {
        BUILDING,
        REPAIRING,
        PRODUCING,
        DESTROYING,
        GETTING_ATTACKED,
        SPARKS,
        SMOKE,
    }

    /** All available animations by type */
    public EnumMap<
        BuildingAnimationType,
        HashMap<TileOffset, Animation<TextureRegion>>
    > animations = new EnumMap<>(BuildingAnimationType.class);

    /** Currently active animation types (overlays) */
    public EnumSet<BuildingAnimationType> activeAnimations = EnumSet.noneOf(
        BuildingAnimationType.class
    );

    /** Individual state time per animation type */
    public EnumMap<BuildingAnimationType, Float> stateTimes = new EnumMap<>(
        BuildingAnimationType.class
    );
}
