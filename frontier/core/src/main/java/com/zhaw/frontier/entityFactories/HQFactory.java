package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;

/**
 * A factory class for creating Headquarters (HQ) entities.
 * <p>
 * This factory provides a method to create a default HQ entity which is initialized with
 * a {@link PositionComponent} and a {@link RenderComponent}. The {@code RenderComponent}
 * is configured to render the entity as a building.
 * </p>
 */
public class HQFactory {

    /**
     * Creates a default HQ entity with the required components.
     * <p>
     * The created entity is given a {@link PositionComponent} to hold its position data
     * and a {@link RenderComponent} with its {@code renderType} set to
     * {@link RenderComponent.RenderType#BUILDING}. This entity is intended to represent a headquarters.
     * </p>
     *
     * @param engine the Ashley {@link Engine} used to create the entity.
     * @return the newly created HQ entity.
     */
    public static Entity createDefaultHQ(Engine engine) {
        Entity hq = engine.createEntity();
        hq.add(new PositionComponent());
        RenderComponent render = new RenderComponent();
        render.renderType = RenderComponent.RenderType.BUILDING;
        hq.add(render);

        return hq;
    }
}
