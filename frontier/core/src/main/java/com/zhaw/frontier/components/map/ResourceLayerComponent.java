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

}
