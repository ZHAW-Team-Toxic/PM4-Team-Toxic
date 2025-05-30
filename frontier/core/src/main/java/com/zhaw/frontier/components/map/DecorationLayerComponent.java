package com.zhaw.frontier.components.map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Component for the decoration layer of the map.
 */
public class DecorationLayerComponent implements Component {

    /**
     * The decoration layer of the map.
     */
    public TiledMapTileLayer decorationLayer;
}
