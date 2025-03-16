package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * System for drawing the grid of the map. This class is used to draw the grid of the map.
 * The grid is drawn as a set of lines.
 */
public class MapGridSystem extends EntitySystem {

    private final int mapWidthInTiles;
    private final int mapHeightInTiles;
    private final float tileWidth;
    private final float tileHeight;
    private final Camera camera;

    private final ShapeRenderer shapeRenderer;

    /**
     * Constructor for the MapGridSystem.
     * @param mapWidthInTiles The width of the map in tiles. This is the number of tiles in the x direction.
     * @param mapHeightInTiles The height of the map in tiles. This is the number of tiles in the y direction.
     * @param tileWidth The width of a tile in pixels.
     * @param tileHeight The height of a tile in pixels.
     * @param camera The camera to use for rendering.
     */
    public MapGridSystem(
        int mapWidthInTiles,
        int mapHeightInTiles,
        float tileWidth,
        float tileHeight,
        Camera camera
    ) {
        this.mapWidthInTiles = mapWidthInTiles;
        this.mapHeightInTiles = mapHeightInTiles;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.camera = camera;

        this.shapeRenderer = new ShapeRenderer();
    }

    /**
     * Draw the grid of the map.
     * This method draws the grid of the map as a set of lines.
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(1, 1, 1, 0.5f); // semi-transparent white

        // Draw vertical lines
        for (int x = 0; x <= mapWidthInTiles; x++) {
            float worldX = x * tileWidth;
            shapeRenderer.line(worldX, 0, worldX, mapHeightInTiles * tileHeight);
        }

        // Draw horizontal lines
        for (int y = 0; y <= mapHeightInTiles; y++) {
            float worldY = y * tileHeight;
            shapeRenderer.line(0, worldY, mapWidthInTiles * tileWidth, worldY);
        }
        shapeRenderer.end();
    }
}
