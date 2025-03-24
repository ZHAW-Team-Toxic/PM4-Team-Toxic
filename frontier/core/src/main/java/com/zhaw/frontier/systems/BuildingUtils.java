package com.zhaw.frontier.systems;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 *
 */
public class BuildingUtils {
    /**
     *
     * @param viewport
     * @param sampleLayer
     * @param screenX
     * @param screenY
     * @return
     */
    static Vector2 calculateWorldCoordinate(Viewport viewport, TiledMapTileLayer sampleLayer, float screenX, float screenY) {
        int HAS_TO_BE_ZERO_FOR_2D_GAMES = 0;
        Vector3 screenCoordinates = new Vector3(screenX, screenY, HAS_TO_BE_ZERO_FOR_2D_GAMES);
        Vector3 worldCoordinates = viewport.unproject(screenCoordinates);
        return new Vector2(
            worldCoordinates.x / sampleLayer.getTileWidth(),
            worldCoordinates.y / sampleLayer.getTileHeight()
        );
    }
}
