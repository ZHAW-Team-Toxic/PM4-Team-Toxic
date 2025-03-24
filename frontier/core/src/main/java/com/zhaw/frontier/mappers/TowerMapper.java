package com.zhaw.frontier.mappers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Family;
import com.zhaw.frontier.components.AttackComponent;
import com.zhaw.frontier.components.BuildingPositionComponent;
import com.zhaw.frontier.components.HealthComponent;
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
    public ComponentMapper<BuildingPositionComponent> pm = ComponentMapper.getFor(
        BuildingPositionComponent.class
    );

    /**
     * Component mappers which map the render of the tower entity.
     */
    public ComponentMapper<RenderComponent> rm = ComponentMapper.getFor(RenderComponent.class);

    /**
     *
     */
    public ComponentMapper<AttackComponent> am = ComponentMapper.getFor(AttackComponent.class);

    /**
     *
     */
    public ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    /**
     * Family which contains all components of the tower entity.
     */
    public Family towerFamily = Family
        .all(
            BuildingPositionComponent.class,
            RenderComponent.class,
            AttackComponent.class,
            HealthComponent.class
        )
        .get();
}
