package com.zhaw.frontier.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.systems.RenderSystem;
import com.zhaw.frontier.wrappers.BatchInterface;

public class HomeScreen implements Screen {
    private FrontierGame frontierGame;
    private BatchInterface spriteBatchWrapper;
    private Texture texture;

    public HomeScreen(FrontierGame frontierGame) {
        this.frontierGame = frontierGame;
        this.spriteBatchWrapper = frontierGame.getSpriteBatchWrapper();
        this.texture = new Texture(Gdx.files.internal("sampl.png"));
    }

    @Override
    public void show() {
        var textureRegion = new TextureRegion(texture);
        frontierGame.getEngine().addSystem(new RenderSystem(spriteBatchWrapper, frontierGame.getExtendedViewport(), frontierGame.getEngine(), textureRegion));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        frontierGame.getExtendedViewport().apply();
        frontierGame.getEngine().update(delta);

        //Check if Enter key is pressed to switch to the GameScreen
        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            frontierGame.switchScreen(new GameScreen(frontierGame));
        }
    }

    @Override
    public void resize(int width, int height) {
        frontierGame.getExtendedViewport().update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        texture.dispose();
        frontierGame.getEngine().removeAllSystems();
    }
}
