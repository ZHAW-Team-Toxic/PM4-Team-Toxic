package com.zhaw.frontier.mappers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Family;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.ResourceGeneratorComponent;

/**
 * Mapper for resource building entities.
 * <p>
 * This class provides component mappers for the common components used by resource building entities:
 * {@link PositionComponent}, {@link RenderComponent}, {@link HealthComponent}, and
 * {@link ResourceGeneratorComponent}. It also defines a {@link Family} that groups all entities
 * possessing these components.
 * </p>
 */
public class ResourceBuildingMapper {

    /**
     * Component mapper for the {@link PositionComponent}.
     * <p>
     * This mapper provides fast access to the position data of an entity.
     * </p>
     */
    public ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    /**
     * Component mapper for the {@link RenderComponent}.
     * <p>
     * This mapper provides fast access to the render data of an entity.
     * </p>
     */
    public ComponentMapper<RenderComponent> rm = ComponentMapper.getFor(RenderComponent.class);

    /**
     * Component mapper for the {@link HealthComponent}.
     * <p>
     * This mapper provides fast access to the health data of an entity.
     * </p>
     */
    public ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);

    /**
     * Component mapper for the {@link ResourceGeneratorComponent}.
     * <p>
     * This mapper provides fast access to the resource generation data of an entity.
     * </p>
     */
    public ComponentMapper<ResourceGeneratorComponent> rgm = ComponentMapper.getFor(
        ResourceGeneratorComponent.class
    );

    /**
     * Family of entities representing resource buildings.
     * <p>
     * This family includes all entities that have a {@link PositionComponent},
     * {@link RenderComponent}, {@link HealthComponent}, and {@link ResourceGeneratorComponent}.
     * </p>
     */
    public Family resouceBuildingFamily = Family
        .all(
            PositionComponent.class,
            RenderComponent.class,
            HealthComponent.class,
            ResourceGeneratorComponent.class
        )
        .get();
}
