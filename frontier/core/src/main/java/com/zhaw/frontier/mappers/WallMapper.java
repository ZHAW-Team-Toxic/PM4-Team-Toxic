package com.zhaw.frontier.mappers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Family;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;

/**
 * Mapper for wall entities.
 * <p>
 * This class provides component mappers for the common components used by wall
 * entities:
 * <ul>
 * <li>{@link PositionComponent} for position data,</li>
 * <li>{@link RenderComponent} for rendering data,</li>
 * <li>{@link HealthComponent} for health data.</li>
 * </ul>
 * It also defines a {@link Family} that groups all wall entities that contain
 * these components.
 * </p>
 */
public class WallMapper {

    /**
     * Component mapper for the {@link PositionComponent}.
     * <p>
     * Provides quick access to the position data of wall entities.
     * </p>
     */
    public ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    /**
     * Component mapper for the {@link RenderComponent}.
     * <p>
     * Provides quick access to the render data of wall entities.
     * </p>
     */
    public ComponentMapper<RenderComponent> rm = ComponentMapper.getFor(RenderComponent.class);

    /**
     * Component mapper for the {@link HealthComponent}.
     * <p>
     * Provides quick access to the health data of wall entities.
     * </p>
     */
    public ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);

    /**
     * Family of wall entities.
     * <p>
     * This family includes all entities that have a {@link PositionComponent},
     * {@link RenderComponent}, and {@link HealthComponent}.
     * </p>
     */
    public Family wallFamily = Family
        .all(PositionComponent.class, RenderComponent.class, HealthComponent.class)
        .get();
}
