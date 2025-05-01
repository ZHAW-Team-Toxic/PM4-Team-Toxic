package com.zhaw.frontier.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.ui.WinScreenUI;
import com.zhaw.frontier.utils.AssetManagerInstance;

public class WinScreen extends ScreenAdapter {
    private final FrontierGame frontierGame;

    private Stage stage;

    public WinScreen(FrontierGame frontierGame) {
        this.frontierGame = frontierGame;
    }

    @Override
    public void show() {
        this.stage = new Stage(new ExtendViewport(1920, 1080, new OrthographicCamera()));
        Skin skin = AssetManagerInstance.getManager().get("skins/skin.json", Skin.class);

        stage.clear();

        new WinScreenUI(stage, skin, 0, this::openStartScreen);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0.2f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void openStartScreen() {
        frontierGame.setScreen(new StartScreen(frontierGame));
    }
}
