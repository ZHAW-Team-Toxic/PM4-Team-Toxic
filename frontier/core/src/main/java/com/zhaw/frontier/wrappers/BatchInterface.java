package com.zhaw.frontier.wrappers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;

public interface BatchInterface {
    void begin();
    void end();
    void draw(Texture texture, float x, float y);
    void draw(Texture texture, float x, float y, float width, float height);
    void draw(TextureRegion region, int x, int y, int width, int height);
    void setProjectionMatrix(Matrix4 matrix);
    SpriteBatch getBatch();
    void dispose();
}
