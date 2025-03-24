package com.zhaw.frontier.systems;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Utility class for building-related operations.
 * <p>
 * Provides helper methods for converting screen coordinates to world coordinates,
 * taking into account the tile dimensions defined by a {@link TiledMapTileLayer}.
 * </p>
 */
public class BuildingUtils {

    /**
     * Calculates the world coordinate as tile indices based on screen coordinates.
     * <p>
     * This method converts the given screen coordinates into world coordinates using the provided
     * {@link Viewport}. The resulting world coordinates are then divided by the tile width and height
     * of the specified {@link TiledMapTileLayer} to determine the corresponding tile indices.
     * </p>
     *
     * @param viewport    the {@link Viewport} used to unproject the screen coordinates.
     * @param sampleLayer the {@link TiledMapTileLayer} that provides the tile dimensions.
     * @param screenX     the x-coordinate on the screen.
     * @param screenY     the y-coordinate on the screen.
     * @return a {@link Vector2} representing the tile indices corresponding to the world coordinate.
     */
    static Vector2 calculateWorldCoordinate(
        Viewport viewport,
        TiledMapTileLayer sampleLayer,
        float screenX,
        float screenY
    ) {
        int HAS_TO_BE_ZERO_FOR_2D_GAMES = 0;
        Vector3 screenCoordinates = new Vector3(screenX, screenY, HAS_TO_BE_ZERO_FOR_2D_GAMES);
        Vector3 worldCoordinates = viewport.unproject(screenCoordinates);
        return new Vector2(
            worldCoordinates.x / sampleLayer.getTileWidth(),
            worldCoordinates.y / sampleLayer.getTileHeight()
        );
    }
}
