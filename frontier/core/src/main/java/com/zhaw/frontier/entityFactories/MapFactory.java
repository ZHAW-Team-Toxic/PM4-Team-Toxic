package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.DecorationLayerComponent;
import com.zhaw.frontier.components.map.ResourceLayerComponent;

/**
 * A factory class responsible for creating and initializing map entities.
 * <p>
 * This factory provides a method to create a default map entity, which is initialized
 * with the necessary map layer components: {@link BottomLayerComponent}, {@link ResourceLayerComponent},
 * and {@link DecorationLayerComponent}.
 * </p>
 */
public class MapFactory {

    /**
     * Creates a default map entity with the required map layer components.
     * <p>
     * The created entity includes:
     * <ul>
     *   <li>{@link BottomLayerComponent} for the bottom layer of the map,</li>
     *   <li>{@link ResourceLayerComponent} for the resource layer of the map,</li>
     *   <li>{@link DecorationLayerComponent} for the decoration layer of the map.</li>
     * </ul>
     * </p>
     *
     * @param engine the {@link Engine} used to create and manage the entity.
     * @return the newly created map entity.
     */
    public static Entity createDefaultMap(Engine engine) {
        Entity map = engine.createEntity();
        map.add(new BottomLayerComponent());
        map.add(new ResourceLayerComponent());
        map.add(new DecorationLayerComponent());
        return map;
    }
}
