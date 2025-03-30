package com.zhaw.frontier.components.map;

public enum MapLayerEnum {

    BOTTOM_LAYER("BottomLayer"),
    DECORATION_LAYER("DecorationLayer"),
    RESOURCE_LAYER("ResourceLayer");

    private final String layerName;

    /**
     * Constructor for the MapLayerEnum.
     * @param layerName the name of the layer
     */
    MapLayerEnum(String layerName) {
        this.layerName = layerName;
    }

    /**
     * Returns the name of the layer.
     * @return layerName the name of the layer
     */
    @Override
    public String toString() {
        return layerName;
    }
}
