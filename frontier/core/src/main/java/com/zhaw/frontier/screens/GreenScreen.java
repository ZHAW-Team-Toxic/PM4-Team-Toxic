package com.zhaw.frontier.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

public class GreenScreen implements Screen {
    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 1, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
    public void show() {}
    public void resize(int width, int height) {}
    public void pause() {}
    public void resume() {}
    public void hide() {}
    public void dispose() {}
}