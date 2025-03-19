package com.zhaw.frontier.components.map;

/**
 * This class holds the properties of the tiles in the Tiled map.
 * //TODO maybe solve this differently
 */
public enum TiledPropertiesEnum {
    IS_BUILDABLE("isBuildable"),
    IS_TRAVERSABLE("isTraversable"),
    IS_SPAWN_POINT("isSpawnPoint");

    private final String property;

    TiledPropertiesEnum(String property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return property;
    }
}
