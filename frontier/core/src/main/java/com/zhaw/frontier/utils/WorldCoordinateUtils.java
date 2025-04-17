package com.zhaw.frontier.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.PositionComponent;

/**
 * Utility class for converting between screen, world, and tile coordinates.
 */
public class WorldCoordinateUtils {

    private WorldCoordinateUtils() {}

    /**
     * Calculates the world coordinate as tile indices based on screen coordinates.
     * <p>
     * Converts screen coordinates into world space using the given {@link Viewport},
     * then divides by tile size to get the tile indices.
     * </p>
     *
     * @param viewport    the viewport used for unprojecting the screen coordinates
     * @param sampleLayer the tile layer providing tile size information
     * @param screenX     the x coordinate of the screen click
     * @param screenY     the y coordinate of the screen click
     * @return a {@link Vector2} representing the tile coordinates in world space
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

    /**
     * Calculates the top-left tile position to center a multi-tile building on a clicked point.
     * <p>
     * Uses entity size (in tiles) and adjusts the click to center the building properly.
     * </p>
     *
     * @param viewport    the viewport for screen-to-world projection
     * @param sampleLayer the tile layer used for tile size
     * @param screenX     the x coordinate of the screen click
     * @param screenY     the y coordinate of the screen click
     * @param entity      the entity to center (must have a {@link PositionComponent})
     * @return a {@link Vector2} representing the tile position where the building should be placed
     */
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

        int tileX = (int) worldCoordinate.x;
        int tileY = (int) worldCoordinate.y;

        int offsetX = widthInTiles / 2;
        int offsetY = heightInTiles / 2;

        if (widthInTiles % 2 == 0) offsetX -= 1;
        if (heightInTiles % 2 == 0) offsetY -= 1;

        return new Vector2(tileX - offsetX, tileY - offsetY);
    }

    /**
     * Converts a tile coordinate to a pixel coordinate for placing buildings.
     * <p>
     * Useful for rendering buildings based on tile positions.
     * </p>
     *
     * @param x           the tile x position
     * @param y           the tile y position
     * @param sampleLayer the tile layer with tile size information
     * @return a {@link Vector2} representing the pixel coordinate
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
