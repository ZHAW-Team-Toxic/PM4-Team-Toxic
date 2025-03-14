package com.zhaw.frontier.wrappers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Concrete implementation of {@link SpriteBatchInterface} that wraps around LibGDX's {@link SpriteBatch}.
 * <p>
 * This wrapper allows the rendering logic to be decoupled from the {@code SpriteBatch} implementation,
 * enabling easier unit and integration testing by mocking or substituting this implementation.
 */
public class FrontierSpriteBatch implements SpriteBatchInterface {

    private SpriteBatch batch;

    /**
     * Constructs a new {@code FrontierSpriteBatch} with an internally managed {@link SpriteBatch}.
     */
    public FrontierSpriteBatch() {
        this.batch = new SpriteBatch();
    }

    /**
     * Begins the rendering process.
     * <p>
     * This must be called before any {@code draw()} calls.
     * Internally delegates to {@link SpriteBatch#begin()}.
     */
    @Override
    public void begin() {
        batch.begin();
    }

    /**
     * Ends the rendering process.
     * <p>
     * This must be called after all {@code draw()} calls.
     * Internally delegates to {@link SpriteBatch#end()}.
     */
    @Override
    public void end() {
        batch.end();
    }

    /**
     * Draws a texture at the specified position using its original width and height.
     * <p>
     * Internally delegates to {@link SpriteBatch#draw(Texture, float, float)}.
     *
     * @param texture The texture to be rendered.
     * @param x       The x-coordinate of the texture's bottom-left corner.
     * @param y       The y-coordinate of the texture's bottom-left corner.
     */
    @Override
    public void draw(Texture texture, float x, float y) {
        batch.draw(texture, x, y);
    }

    /**
     * Draws a texture at the specified position with the specified width and height.
     * <p>
     * Internally delegates to {@link SpriteBatch#draw(Texture, float, float, float, float)}.
     *
     * @param texture The texture to be rendered.
     * @param x       The x-coordinate of the texture's bottom-left corner.
     * @param y       The y-coordinate of the texture's bottom-left corner.
     * @param width   The width to scale the texture to.
     * @param height  The height to scale the texture to.
     */
    @Override
    public void draw(Texture texture, float x, float y, float width, float height) {
        batch.draw(texture, x, y, width, height);
    }

    /**
     * Disposes of the internal {@link SpriteBatch}, releasing any GPU resources.
     * <p>
     * Should be called when this batch is no longer needed to prevent memory/resource leaks.
     */
    @Override
    public void dispose() {
        batch.dispose();
    }

    /**
     * Returns the internal {@link SpriteBatch} instance.
     * <p>
     * This method allows access to the underlying batch for advanced use cases,
     * but should generally be avoided in test scenarios to maintain abstraction.
     *
     * @return The internal {@link SpriteBatch} instance.
     */
    @Override
    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void setBatch(SpriteBatchInterface batch) {
        this.batch = ((FrontierSpriteBatch) batch).getBatch();
    }
}
