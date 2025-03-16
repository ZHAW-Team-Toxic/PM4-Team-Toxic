package com.zhaw.frontier.components.map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Component for the bottom layer of the map.
 */
public class BottomLayerComponent implements Component {
    /**
     * The bottom layer of the map.
     */
    public TiledMapTileLayer bottomLayer;
    /**
     * Constructor for the bottom layer component.
     * @param bottomLayer The bottom layer of the map.
     */
    public BottomLayerComponent(TiledMapTileLayer bottomLayer) {
        this.bottomLayer = bottomLayer;
    }
}
