package com.zhaw.frontier.wrappers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Interface for abstracting the rendering behavior of a batch.
 * This is primarily intended to decouple rendering logic from the concrete {@link SpriteBatch}
 * implementation in libGDX, facilitating easier unit and integration testing.
 */
public interface SpriteBatchInterface {
    /**
     * Begins the rendering process.
     * Must be called before any draw operations. Typically, this sets up the rendering context.
     */
    void begin();

    /**
     * Ends the rendering process.
     * Must be called after all draw operations. This flushes the draw calls to the GPU.
     */
    void end();

    /**
     * Draws a texture at the specified coordinates using its native width and height.
     *
     * @param texture The {@link Texture} to be drawn.
     * @param x       The x-coordinate where the texture should be drawn.
     * @param y       The y-coordinate where the texture should be drawn.
     */
    void draw(Texture texture, float x, float y);

    /**
     * Draws a texture at the specified coordinates with a custom width and height.
     *
     * @param texture The {@link Texture} to be drawn.
     * @param x       The x-coordinate where the texture should be drawn.
     * @param y       The y-coordinate where the texture should be drawn.
     * @param width   The width to scale the texture to.
     * @param height  The height to scale the texture to.
     */
    void draw(Texture texture, float x, float y, float width, float height);

    /**
     * Disposes of resources associated with this batch implementation.
     * Should be called when the batch is no longer needed to free GPU resources.
     */
    void dispose();

    /**
     * Returns the underlying {@link SpriteBatch} instance, if available.
     * This allows low-level access when needed, but should be avoided in unit tests.
     *
     * @return The backing {@link SpriteBatch} instance, or {@code null} if not applicable.
     */
    SpriteBatch getBatch();

    /**
     * Sets the batch to be used for rendering.
     *
     * @param batch The batch to be used for rendering.
     */
    void setBatch(SpriteBatchInterface batch);
}
