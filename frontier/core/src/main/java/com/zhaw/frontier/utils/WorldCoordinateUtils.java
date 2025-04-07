package com.zhaw.frontier.utils;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.PositionComponent;

public class WorldCoordinateUtils {

    private WorldCoordinateUtils() {}

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
    public static Vector2 calculateWorldCoordinate(
        Viewport viewport,
        TiledMapTileLayer sampleLayer,
        float screenX,
        float screenY
    ) {
        int is2dGame = 0;
        Vector3 screenCoordinates = new Vector3(screenX, screenY, is2dGame);
        Vector3 worldCoordinates = viewport.unproject(screenCoordinates);
        return new Vector2(
            worldCoordinates.x / sampleLayer.getTileWidth(),
            worldCoordinates.y / sampleLayer.getTileHeight()
        );
    }

    public static Vector2 centerClickWithBuilding(
        Viewport viewport,
        TiledMapTileLayer sampleLayer,
        float screenX,
        float screenY,
        Entity entity,
        Engine engine
    ) {
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        int widthInTiles = positionComponent.widthInTiles;
        int heightInTiles = positionComponent.heightInTiles;

        if (widthInTiles == 1 && heightInTiles == 1) {
            return calculateWorldCoordinate(viewport, sampleLayer, screenX, screenY);
        }
        if (widthInTiles == 1 && heightInTiles == 2) {
            Vector2 worldCoordinate = calculateWorldCoordinate(
                viewport,
                sampleLayer,
                screenX,
                screenY
            );
            worldCoordinate.x = (int) worldCoordinate.x;
            worldCoordinate.y = (int) worldCoordinate.y - 1;
            return worldCoordinate;
        }
        if (widthInTiles == 2 && heightInTiles == 2) {
            Vector2 worldCoordinate = calculateWorldCoordinate(
                viewport,
                sampleLayer,
                screenX,
                screenY
            );
            worldCoordinate.x = (int) worldCoordinate.x;
            worldCoordinate.y = (int) worldCoordinate.y;
            return worldCoordinate;
        }
        if (widthInTiles == 3 && heightInTiles == 3) {
            Vector2 worldCoordinate = calculateWorldCoordinate(
                viewport,
                sampleLayer,
                screenX,
                screenY
            );
            worldCoordinate.x = (int) worldCoordinate.x - 1;
            worldCoordinate.y = (int) worldCoordinate.y - 1;
            Vector2 pixelCoordinate = calculatePixelCoordinateForEnemies(
                (int) worldCoordinate.x,
                (int) worldCoordinate.y,
                sampleLayer
            );
            return new Vector2(pixelCoordinate.x, pixelCoordinate.y);
        }
        if (widthInTiles == 4 && heightInTiles == 4) {
            Vector2 worldCoordinate = calculateWorldCoordinate(
                viewport,
                sampleLayer,
                screenX,
                screenY
            );
            worldCoordinate.x = (int) worldCoordinate.x - 1;
            worldCoordinate.y = (int) worldCoordinate.y - 1;
            return worldCoordinate;
        }
        if (widthInTiles == 5 && heightInTiles == 5) {
            Vector2 worldCoordinate = calculateWorldCoordinate(
                viewport,
                sampleLayer,
                screenX,
                screenY
            );
            worldCoordinate.x = (int) worldCoordinate.x - 4;
            worldCoordinate.y = (int) worldCoordinate.y - 4;
            return worldCoordinate;
        }
        return new Vector2(0, 0);
    }

    /**
     * Calculates the pixel coordinate corresponding to a given tile coordinate.
     * <p>
     * This method converts tile indices (x, y) to pixel coordinates by multiplying them with the tile width and height
     * from the bottom layer of the map.
     * </p>
     *
     * @param x the tile x-coordinate.
     * @param y the tile y-coordinate.
     * @return a {@link Vector2} representing the pixel coordinates.
     */
    public static Vector2 calculatePixelCoordinateForEnemies(
        int x,
        int y,
        TiledMapTileLayer sampleLayer
    ) {
        float pixelX = x * sampleLayer.getTileWidth();
        float pixelY = y * sampleLayer.getTileHeight();
        return new Vector2(pixelX, pixelY);
    }

    public static Vector2 calculatePixelCoordinateForEnemies(
        float x,
        float y,
        TiledMapTileLayer sampleLayer
    ) {
        float pixelX = x * sampleLayer.getTileWidth();
        float pixelY = y * sampleLayer.getTileHeight();
        return new Vector2(pixelX, pixelY);
    }
}
