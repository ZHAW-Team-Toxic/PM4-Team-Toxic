package com.zhaw.frontier.components.map;

/**
 * This class holds the properties of the tiles in the Tiled map.
 * //TODO maybe solve this differently
 */
public enum TiledPropertiesEnum {
    IS_BUILDABLE("isBuildable"),
    IS_TRAVERSABLE("isTraversable"),
    IS_SPAWN_POINT("isSpawnPoint"),

    WOOD("wood"),
    STONE("stone"),
    IRON("iron");

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
