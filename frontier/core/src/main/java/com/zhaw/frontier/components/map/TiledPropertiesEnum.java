package com.zhaw.frontier.components.map;

/**
 * This class holds the properties of the tiles in the Tiled map.
 * //TODO maybe solve this differently
 */
public enum TiledPropertiesEnum {
    BOTTOM_LAYER("BottomLayer"),
    DECORATION_LAYER("DecorationLayer"),
    RESOURCE_LAYER("ResourceLayer"),

    IS_BUILDABLE("isBuildable"),
    IS_TRAVERSABLE("isTraversable"),
    IS_SPAWN_POINT("isSpawnPoint"),

    RESOURCE_TYPE_WOOD("wood"),
    RESOURCE_TYPE_STONE("stone"),
    RESOURCE_TYPE_IRON("iron");

    private final String property;

    /**
     * Constructor for the TiledPropertiesEnum.
     * @param property the property of the tile
     */
    TiledPropertiesEnum(String property) {
        this.property = property;
    }

    /**
     * Returns the property.
     * @return property the property of the tile
     */
    @Override
    public String toString() {
        return property;
    }
}
