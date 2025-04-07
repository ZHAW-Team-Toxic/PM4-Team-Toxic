package com.zhaw.frontier.utils;

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
        Entity entity
    ) {
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        int widthInTiles = positionComponent.widthInTiles;
        int heightInTiles = positionComponent.heightInTiles;

        Vector2 worldCoordinate = calculateWorldCoordinate(viewport, sampleLayer, screenX, screenY);

        // Round to integer tile position
        int tileX = (int) worldCoordinate.x;
        int tileY = (int) worldCoordinate.y;

        // Offset so that the building is centered on the click
        int offsetX = widthInTiles / 2;
        int offsetY = heightInTiles / 2;

        // Adjust for even-sized buildings: place top-left of the "center"
        if (widthInTiles % 2 == 0) offsetX -= 1;
        if (heightInTiles % 2 == 0) offsetY -= 1;

        return new Vector2(tileX - offsetX, tileY - offsetY);
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
    public static Vector2 calculatePixelCoordinateForBuildings(
        float x,
        float y,
        TiledMapTileLayer sampleLayer
    ) {
        int pixelX = (int) x * sampleLayer.getTileWidth();
        int pixelY = (int) y * sampleLayer.getTileHeight();
        return new Vector2(pixelX, pixelY);
    }
}
