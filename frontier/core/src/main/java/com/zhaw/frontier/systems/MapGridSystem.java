package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * System for drawing the grid of the map. This class is used to draw the grid of the map.
 * The grid is drawn as a set of lines.
 * TODO When moving fast with the rts-controls, the grid moves around a bit.
 */
public class MapGridSystem extends EntitySystem {

    private final int mapWidthInTiles;
    private final int mapHeightInTiles;
    private final float tileWidth;
    private final float tileHeight;
    private final Viewport viewport;

    private final ShapeRenderer shapeRenderer;

    /**
     * Constructor for the MapGridSystem.
     * @param mapWidthInTiles The width of the map in tiles. This is the number of tiles in the x direction.
     * @param mapHeightInTiles The height of the map in tiles. This is the number of tiles in the y direction.
     * @param tileWidth The width of a tile in pixels.
     * @param tileHeight The height of a tile in pixels.
     * @param viewport The viewport to be used.
     */
    public MapGridSystem(
        int mapWidthInTiles,
        int mapHeightInTiles,
        float tileWidth,
        float tileHeight,
        Viewport viewport
    ) {
        this.mapWidthInTiles = mapWidthInTiles;
        this.mapHeightInTiles = mapHeightInTiles;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.viewport = viewport;

        this.shapeRenderer = new ShapeRenderer();
    }

    /**
     * Draw the grid of the map.
     * This method draws the grid of the map as a set of lines.
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        // Ensure the camera is updated

        viewport.apply();
        Camera camera = viewport.getCamera();
        camera.update();

        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(1, 1, 1, 0.5f); // semi-transparent white

        // Draw vertical lines with rounding to avoid sub-pixel artifacts
        for (int x = 0; x <= mapWidthInTiles; x++) {
            float worldX = Math.round(x * tileWidth);
            shapeRenderer.line(worldX, 0, worldX, Math.round(mapHeightInTiles * tileHeight));
        }

        // Draw horizontal lines with rounding
        for (int y = 0; y <= mapHeightInTiles; y++) {
            float worldY = Math.round(y * tileHeight);
            shapeRenderer.line(0, worldY, Math.round(mapWidthInTiles * tileWidth), worldY);
        }

        shapeRenderer.end();
    }
}
