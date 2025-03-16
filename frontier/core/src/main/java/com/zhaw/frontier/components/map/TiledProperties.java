package com.zhaw.frontier.components.map;

/**
 * This class holds the properties of the tiles in the Tiled map.
 * //TODO maybe solve this differently
 */
public class TiledProperties {

    public enum TiledTilePropertiesEnum{

        IS_BUILDABLE("isBuildable"),
        IS_TRAVERSABLE("isTraversable"),
        IS_SPAWN_POINT("isSpawnPoint");

        public final String property;

        TiledTilePropertiesEnum(String property) {
            this.property = property;
        }

        @Override
        public String toString() {
            return property;
        }
    }

}
