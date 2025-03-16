package com.zhaw.frontier.mappers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Family;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;

/**
 * Mapper for the towers. This class is used to map the towers.
 * Current components:
 * - PositionComponent
 * - RenderComponent
 */
public class TowerMapper {

    /**
     * Component mappers which map the position of the tower entity.
     */
    public ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    /**
     * Component mappers which map the render of the tower entity.
     */
    public ComponentMapper<RenderComponent> rm = ComponentMapper.getFor(RenderComponent.class);
    //todo: add more tower component mapper
    /**
     * Family which contains all components of the tower entity.
     */
    public Family towerFamily = Family.all(PositionComponent.class, RenderComponent.class).get();
}
