package com.zhaw.frontier.wrappers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;

public class GameBatch implements BatchInterface {
    private BatchInterface batch;
    /**
     * Begins the rendering process for the batch. 
     * This method must be called before drawing any textures.
     */
    public void begin() {
        batch.begin();
    }

    /**
     * Ends the rendering process for the batch.
     * This method should be called after all drawing operations are complete.
     */
    public void end() {
        batch.end();
    }

    /**
     * Draws a texture at the specified position.
     *
     * @param texture The texture to be drawn.
     * @param x The x-coordinate of the texture.
     * @param y The y-coordinate of the texture.
     */
    public void draw(Texture texture, float x, float y) {
        batch.draw(texture, x, y);
    }

    /**
     * Draws a texture at the specified position with a specified width and height.
     *
     * @param texture The texture to be drawn.
     * @param x The x-coordinate of the texture.
     * @param y The y-coordinate of the texture.
     * @param width The width of the texture.
     * @param height The height of the texture.
     */
    public void draw(Texture texture, float x, float y, float width, float height) {
        batch.draw(texture, x, y, width, height);
    }

    /**
     * Disposes of the internal {@link SpriteBatch} to free up resources.
     * This method should be called when the wrapper is no longer needed.
     */
    @Override
    public void dispose() {
        batch.dispose();
    }
    
    /**
     * Gets the underlying {@link SpriteBatch} instance.
     *
     * @return The internal {@code SpriteBatch}.
     */
    public SpriteBatch getBatch() {
        return batch.getBatch();
    }

    public void draw(TextureRegion background, int x, int y, int width, int height) {
        batch.draw(background, x, y, width, height);
    }

    public void setProjectionMatrix(Matrix4 combined) {
        batch.setProjectionMatrix(combined);
    }
}
