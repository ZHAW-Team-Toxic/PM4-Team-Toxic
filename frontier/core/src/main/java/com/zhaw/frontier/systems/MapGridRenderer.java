package com.zhaw.frontier.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MapGridRenderer {

    private final int mapWidthInTiles;
    private final int mapHeightInTiles;
    private final float tileWidth;
    private final float tileHeight;

    private final Camera camera;
    private final Texture pixelTexture;

    // You can adjust the line thickness as needed
    private final float lineThickness = 50f;

    public MapGridRenderer(TiledMapTileLayer sampleLayer, Viewport viewport) {
        this.mapWidthInTiles = sampleLayer.getWidth();
        this.mapHeightInTiles = sampleLayer.getHeight();
        this.tileWidth = sampleLayer.getTileWidth();
        this.tileHeight = sampleLayer.getTileHeight();

        this.camera = viewport.getCamera();

        // Create a 1x1 white pixel texture
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        this.pixelTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void update(SpriteBatch spriteBatch) {
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        // Enable blending for transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);

        spriteBatch.begin();
        // Set the color to semi-transparent white
        spriteBatch.setColor(1, 1, 1, 0.5f);

        // Draw vertical grid lines
        for (int x = 0; x <= mapWidthInTiles; x++) {
            float worldX = Math.round(x * tileWidth);
            spriteBatch.draw(pixelTexture,
                worldX, // x position
                0,      // y position
                lineThickness, // width (line thickness)
                Math.round(mapHeightInTiles * tileHeight)); // height of the grid area
        }

        // Draw horizontal grid lines
        for (int y = 0; y <= mapHeightInTiles; y++) {
            float worldY = Math.round(y * tileHeight);
            spriteBatch.draw(pixelTexture,
                0, // x position
                worldY, // y position
                Math.round(mapWidthInTiles * tileWidth), // width of the grid area
                lineThickness); // height (line thickness)
        }
        spriteBatch.end();

        // Optionally disable blending after drawing
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Reset batch color to opaque white if needed
        spriteBatch.setColor(1, 1, 1, 1);
    }

    // Dispose the texture when no longer needed
    public void dispose() {
        pixelTexture.dispose();
    }
}
