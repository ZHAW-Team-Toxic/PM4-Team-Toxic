package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

/**
 * Component that specifies what type of entities can be targeted by this entity.
 *
 * <p>Used by systems such as attack, pathfinding, or AI to determine valid targets
 * based on the presence of a specific component on other entities.</p>
 *
 * <p>For example, an entity with this component set to {@code HQComponent.class}
 * will only target entities that have an {@code HQComponent}.</p>
 */
public class TargetTypeComponent implements Component {

    /** The class type of the component that valid target entities must have. */
    public Class<? extends Component> targetComponentType;

    /**
     * Constructs a {@code TargetTypeComponent} with the given target component type.
     *
     * @param targetType the type of component that defines valid targets
     */
    public TargetTypeComponent(Class<? extends Component> targetType) {
        this.targetComponentType = targetType;
    }
}
