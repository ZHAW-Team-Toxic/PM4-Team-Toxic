package com.zhaw.frontier.utils;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Represents a renderable tilemap layer with a specific z-index.
 * <p>
 * Used for layered rendering of TiledMap layers.
 * </p>
 */
public class MapLayerRenderEntry {

    /** The layer name (as defined in the TMX map). */
    public String name;

    /** The z-index for sorting render order (lower = behind, higher = in front). */
    public int zIndex;

    /** The actual TiledMapTileLayer instance. */
    public TiledMapTileLayer layer;

    /**
     * Constructs a new render entry for a tile layer.
     *
     * @param name   the name of the layer
     * @param zIndex the rendering order index
     * @param layer  the tile layer
     */
    public MapLayerRenderEntry(String name, int zIndex, TiledMapTileLayer layer) {
        this.name = name;
        this.zIndex = zIndex;
        this.layer = layer;
    }
}
