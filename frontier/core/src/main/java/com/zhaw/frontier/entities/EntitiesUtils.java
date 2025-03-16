package com.zhaw.frontier.entities;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import java.util.List;

/**
 * This class provides utility methods for entities.
 */
public class EntitiesUtils {
    /**
     * Builds an entity with the given components.
     * @param entity The entity to build.
     * @param components The components to add to the entity.
     * @return The entity with the given components.
     */
    public static Entity buildEntity(Entity entity, List<Component> components) {
        for (Component component : components) {
            entity.add(component);
        }
        return entity;
    }
}
