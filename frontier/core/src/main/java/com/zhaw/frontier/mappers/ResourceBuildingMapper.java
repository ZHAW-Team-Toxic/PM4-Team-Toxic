package com.zhaw.frontier.mappers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Family;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.components.BuildingPositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.ResourceGeneratorComponent;

/**
 *
 */
public class ResourceBuildingMapper {

    /**
     * Component mappers which map the position of the tower entity.
     */
    public ComponentMapper<BuildingPositionComponent> pm = ComponentMapper.getFor(BuildingPositionComponent.class);
    /**
     * Component mappers which map the render of the tower entity.
     */
    public ComponentMapper<RenderComponent> rm = ComponentMapper.getFor(RenderComponent.class);

    public ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);

    public ComponentMapper<ResourceGeneratorComponent> rgm = ComponentMapper.getFor(ResourceGeneratorComponent.class);

    /**
     * Family which contains all components of the tower entity.
     */
    public Family resouceBuildingFamily = Family.all(BuildingPositionComponent.class, RenderComponent.class, HealthComponent.class, ResourceGeneratorComponent.class).get();
}
