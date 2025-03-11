package com.zhaw.tests.utils;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.zhaw.frontier.wrappers.BatchInterface;

public class MockSpriteBatch implements BatchInterface {
    private List<String> logs = new ArrayList<>();

    public MockSpriteBatch() {
    }
    
     /**
     * Begins the rendering process for the batch. 
     * This method must be called before drawing any textures.
     */
    public void begin() {
        logs.add("begin()");
    }

    /**
     * Ends the rendering process for the batch.
     * This method should be called after all drawing operations are complete.
     */
    public void end() {
        logs.add("end()");
    }

    /**
     * Draws a texture at the specified position.
     *
     * @param texture The texture to be drawn.
     * @param x The x-coordinate of the texture.
     * @param y The y-coordinate of the texture.
     */
    public void draw(Texture texture, float x, float y) {
        logs.add(String.format("draw(%s, %s, %s)", texture.toString(), x, y));
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
        logs.add(String.format("draw(%s, %s, %s, %s, %s)", texture.toString(), x, y, width, height));
    }

    public void draw(TextureRegion background, int x, int y, int width, int height) {
        logs.add(String.format("draw(%s, %s, %s, %s, %s)", background.toString(), x, y, width, height));
    }

    /**
     * Disposes of the internal {@link SpriteBatch} to free up resources.
     * This method should be called when the wrapper is no longer needed.
     */
    @Override
    public void dispose() {
        logs.add("dispose()");
    }

    @Override
    public void setProjectionMatrix(Matrix4 matrix) {
        logs.add(String.format("setProjectionMatrix(%s)", matrix.toString()));
    }

    @Override
    public SpriteBatch getBatch() {
        return mock(SpriteBatch.class);
    }
}
