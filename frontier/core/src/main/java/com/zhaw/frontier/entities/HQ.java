package com.zhaw.frontier.entities;

import com.badlogic.ashley.core.Entity;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;

/**
 * HQ entity class. This class is used to create a HQ entity.
 * Current components:
 * - PositionComponent
 * - RenderComponent
 */
public class HQ extends Entity {

    /**
     * Constructor for the HQ entity.
     */
    public HQ() {
        add(new PositionComponent());
        add(new RenderComponent());
    }

    /**
     * Create a default HQ.
     * @return The default HQ.
     */
    public static HQ createDefaultHQ() {
        return new HQ();
    }
}
