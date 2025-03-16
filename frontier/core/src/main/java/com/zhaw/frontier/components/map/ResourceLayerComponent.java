package com.zhaw.frontier.components.map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Component for the resource layer of the map.
 */
public class ResourceLayerComponent implements Component {

    /**
     * The resource layer of the map.
     */
    public TiledMapTileLayer resourceLayer;
    /**
     * The name of the resource layer.
     */
    public String resourceLayerName;

    /**
     * Constructor for the resource layer component. Initializes the resource layer.
     * Loads the properties of the resource layer.
     * @param resourceLayer The resource layer of the map.
     */
    public ResourceLayerComponent(TiledMapTileLayer resourceLayer) {
        this.resourceLayer = resourceLayer;
        this.resourceLayerName = resourceLayer.getName();
    }
}
