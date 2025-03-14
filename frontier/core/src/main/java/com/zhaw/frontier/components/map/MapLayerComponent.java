package com.zhaw.frontier.components.map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class MapLayerComponent implements Component {

    public TiledMapTileLayer mapLayer;
    public String layerName;

    public MapLayerComponent(TiledMapTileLayer mapLayer) {
        this.mapLayer = mapLayer;
        this.layerName = mapLayer.getName();

    }
}
