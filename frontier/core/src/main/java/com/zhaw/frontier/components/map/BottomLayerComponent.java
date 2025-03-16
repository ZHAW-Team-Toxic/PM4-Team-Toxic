package com.zhaw.frontier.components.map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import java.util.Iterator;
import java.util.List;

/**
 * Component for the bottom layer of the map.
 */
public class BottomLayerComponent implements Component {

    /**
     * The bottom layer of the map.
     */
    public TiledMapTileLayer bottomLayer;
    /**
     * The name of the bottom layer.
     */
    public String bottomLayerName;

    /**
     * Constructor for the bottom layer component. Initializes the bottom layer.
     * Loads the properties of the bottom layer.
     * @param bottomLayer The bottom layer of the map.
     */
    public BottomLayerComponent(TiledMapTileLayer bottomLayer) {
        this.bottomLayer = bottomLayer;
        this.bottomLayerName = bottomLayer.getName();
    }
}
