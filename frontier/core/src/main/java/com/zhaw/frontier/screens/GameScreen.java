package com.zhaw.frontier.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.wrappers.SpriteBatchWrapper;

public class GameScreen implements Screen {
    private FrontierGame frontierGame;
    private SpriteBatchWrapper spriteBatchWrapper;

    public GameScreen(FrontierGame frontierGame) {
        this.frontierGame = frontierGame;
        this.spriteBatchWrapper = frontierGame.getBatch();
    }

    @Override
    public void show() {
        
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 1, 1); // (0,0,1,1) = Blue
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        
        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            frontierGame.switchScreen(new StartScreen(frontierGame));
        }
    }


    @Override
    public void resize(int width, int height) {
       
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

    }
    
}
