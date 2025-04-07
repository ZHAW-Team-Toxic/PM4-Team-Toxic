package com.zhaw.frontier.utils;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class LayerRenderEntry {

    public String name;
    public int zIndex;
    public TiledMapTileLayer layer;

    public LayerRenderEntry(String name, int zIndex, TiledMapTileLayer layer) {
        this.name = name;
        this.zIndex = zIndex;
        this.layer = layer;
    }
}
