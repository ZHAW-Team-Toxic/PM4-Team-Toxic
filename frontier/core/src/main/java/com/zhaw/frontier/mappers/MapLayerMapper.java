package com.zhaw.frontier.mappers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Family;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.DecorationLayerComponent;
import com.zhaw.frontier.components.map.ResourceLayerComponent;

/**
 * Mapper for the map layers. This class is used to map the different layers of the map.
 * The layers are the bottom layer, the decoration layer and the resource layer.
 */
public class MapLayerMapper {

    /**
     * Component mappers which map the bottom layer of the map.
     */
    public ComponentMapper<BottomLayerComponent> bottomLayerMapper = ComponentMapper.getFor(
        BottomLayerComponent.class
    );
    /**
     * Component mappers which map the decoration layer of the map.
     */
    public ComponentMapper<DecorationLayerComponent> decorationLayerMapper = ComponentMapper.getFor(
        DecorationLayerComponent.class
    );
    /**
     * Component mappers which map the resource layer of the map.
     */
    public ComponentMapper<ResourceLayerComponent> resourceLayerMapper = ComponentMapper.getFor(
        ResourceLayerComponent.class
    );
    /**
     * Family which contains all components of the map layers.
     */
    public Family mapLayerFamily = Family
        .all(
            BottomLayerComponent.class,
            DecorationLayerComponent.class,
            ResourceLayerComponent.class
        )
        .get();
}
