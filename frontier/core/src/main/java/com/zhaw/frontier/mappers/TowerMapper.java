package com.zhaw.frontier.mappers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Family;
import com.zhaw.frontier.components.AttackComponent;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;

/**
 * Mapper for tower entities.
 * <p>
 * This class provides component mappers for the common components used by tower entities:
 * <ul>
 *   <li>{@link PositionComponent} for position data,</li>
 *   <li>{@link RenderComponent} for rendering data,</li>
 *   <li>{@link AttackComponent} for attack capabilities,</li>
 *   <li>{@link HealthComponent} for health data.</li>
 * </ul>
 * It also defines a {@link Family} that groups all tower entities with these components.
 * </p>
 */
public class TowerMapper {

    /**
     * Component mapper for the {@link PositionComponent}.
     * <p>
     * Provides quick access to the position data of tower entities.
     * </p>
     */
    public ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    /**
     * Component mapper for the {@link RenderComponent}.
     * <p>
     * Provides quick access to the render data of tower entities.
     * </p>
     */
    public ComponentMapper<RenderComponent> rm = ComponentMapper.getFor(RenderComponent.class);

    /**
     * Component mapper for the {@link AttackComponent}.
     * <p>
     * Provides quick access to the attack data of tower entities.
     * </p>
     */
    public ComponentMapper<AttackComponent> am = ComponentMapper.getFor(AttackComponent.class);

    /**
     * Component mapper for the {@link HealthComponent}.
     * <p>
     * Provides quick access to the health data of tower entities.
     * </p>
     */
    public ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);

    /**
     * Family of tower entities.
     * <p>
     * This family includes all entities that have the following components:
     * {@link PositionComponent}, {@link RenderComponent}, {@link AttackComponent}, and {@link HealthComponent}.
     * </p>
     */
    public Family towerFamily = Family
        .all(
            PositionComponent.class,
            RenderComponent.class,
            AttackComponent.class,
            HealthComponent.class
        )
        .get();
}
