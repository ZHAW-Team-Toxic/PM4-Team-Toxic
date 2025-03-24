package com.zhaw.frontier.mappers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Family;
import com.zhaw.frontier.components.BuildingPositionComponent;
import com.zhaw.frontier.components.RenderComponent;

public class HQMapper {

    /**
     * Component mappers which map the position of the tower entity.
     */
    public ComponentMapper<BuildingPositionComponent> pm = ComponentMapper.getFor(BuildingPositionComponent.class);
    /**
     * Component mappers which map the render of the tower entity.
     */
    public ComponentMapper<RenderComponent> rm = ComponentMapper.getFor(RenderComponent.class);

    /**
     * Family which contains all components of the tower entity.
     */
    public Family HQFamily = Family.all(BuildingPositionComponent.class, RenderComponent.class).get();
}
