package com.zhaw.frontier.wrappers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

/**
 * A wrapper class for {@link SpriteBatch} that simplifies batch rendering operations in a
 * LibGDX-based game.
 *
 * <p>This class provides methods to begin and end a batch, as well as drawing textures at specific
 * coordinates. It implements {@link Disposable} to ensure proper resource cleanup.
 */
public class SpriteBatchWrapper implements Disposable {

    private SpriteBatch batch;

    /**
     * Constructs a new {@code SpriteBatchWrapper} and initializes an internal {@link SpriteBatch}.
     */
    public SpriteBatchWrapper() {
        this.batch = new SpriteBatch();
    }

    /**
     * Begins the rendering process for the batch. This method must be called before drawing any
     * textures.
     */
    public void begin() {
        batch.begin();
    }

    /**
     * Ends the rendering process for the batch. This method should be called after all drawing
     * operations are complete.
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
     * Disposes of the internal {@link SpriteBatch} to free up resources. This method should be
     * called when the wrapper is no longer needed.
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
        return batch;
    }
}
