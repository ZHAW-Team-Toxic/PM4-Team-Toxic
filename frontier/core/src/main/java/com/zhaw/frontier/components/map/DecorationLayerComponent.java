package com.zhaw.frontier.components.map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import java.util.Iterator;
import java.util.List;

/**
 * Component for the decoration layer of the map.
 */
public class DecorationLayerComponent implements Component {

    /**
     * The decoration layer of the map.
     */
    public TiledMapTileLayer decorationLayer;
    /**
     * The name of the decoration layer.
     */
    public String decorationLayerName;
    /**
     * The properties of the decoration layer.
     */
    public List<String> properties;

    /**
     * Constructor for the decoration layer component. Initializes the decoration layer.
     * Loads the properties of the decoration layer.
     * @param decorationLayer The decoration layer of the map.
     */
    public DecorationLayerComponent(TiledMapTileLayer decorationLayer) {
        this.decorationLayer = decorationLayer;
        this.decorationLayerName = decorationLayer.getName();
        Iterator<String> propertyKeys = decorationLayer.getProperties().getKeys();
        while (propertyKeys.hasNext()) {
            properties.add(propertyKeys.next());
        }
    }
}
