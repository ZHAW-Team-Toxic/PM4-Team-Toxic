package com.zhaw.frontier.mappers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Family;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;

/**
 * Mapper for the Headquarters (HQ) entity.
 * <p>
 * This class provides easy access to the common components used by HQ entities,
 * namely the {@link PositionComponent} and the {@link RenderComponent}. It also
 * defines a {@link Family} that groups all entities that contain these components.
 * </p>
 */
public class HQMapper {

    /**
     * Component mapper for the {@link PositionComponent}.
     * <p>
     * This mapper provides quick access to the position data of an entity.
     * </p>
     */
    public ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    /**
     * Component mapper for the {@link RenderComponent}.
     * <p>
     * This mapper provides quick access to the render data of an entity.
     * </p>
     */
    public ComponentMapper<RenderComponent> rm = ComponentMapper.getFor(RenderComponent.class);

    /**
     * Family of entities containing both {@link PositionComponent} and {@link RenderComponent}.
     * <p>
     * This family can be used to iterate over all HQ entities that have a position and render data.
     * </p>
     */
    public Family HQFamily = Family.all(PositionComponent.class, RenderComponent.class).get();
}
