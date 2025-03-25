package com.zhaw.frontier.mappers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Family;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.DecorationLayerComponent;
import com.zhaw.frontier.components.map.ResourceLayerComponent;

/**
 * Mapper for the map layers.
 * <p>
 * This class provides component mappers for the different layers of the map:
 * the bottom layer, the decoration layer, and the resource layer.
 * It also defines a {@link Family} that groups all entities possessing these map layer components.
 * </p>
 */
public class MapLayerMapper {

    /**
     * Component mapper for the {@link BottomLayerComponent}.
     * <p>
     * Provides quick access to the bottom layer of a map entity.
     * </p>
     */
    public ComponentMapper<BottomLayerComponent> bottomLayerMapper = ComponentMapper.getFor(
        BottomLayerComponent.class
    );

    /**
     * Component mapper for the {@link DecorationLayerComponent}.
     * <p>
     * Provides quick access to the decoration layer of a map entity.
     * </p>
     */
    public ComponentMapper<DecorationLayerComponent> decorationLayerMapper = ComponentMapper.getFor(
        DecorationLayerComponent.class
    );

    /**
     * Component mapper for the {@link ResourceLayerComponent}.
     * <p>
     * Provides quick access to the resource layer of a map entity.
     * </p>
     */
    public ComponentMapper<ResourceLayerComponent> resourceLayerMapper = ComponentMapper.getFor(
        ResourceLayerComponent.class
    );

    /**
     * Family of map layer entities.
     * <p>
     * This family includes all entities that have a {@link BottomLayerComponent},
     * a {@link DecorationLayerComponent}, and a {@link ResourceLayerComponent}.
     * </p>
     */
    public Family mapLayerFamily = Family
        .all(
            BottomLayerComponent.class,
            DecorationLayerComponent.class,
            ResourceLayerComponent.class
        )
        .get();
}
