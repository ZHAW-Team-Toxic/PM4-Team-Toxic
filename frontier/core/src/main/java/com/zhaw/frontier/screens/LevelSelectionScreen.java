package com.zhaw.frontier.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.configs.AppProperties;
import com.zhaw.frontier.entityFactories.CursorFactory;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;

public class LevelSelectionScreen extends ScreenAdapter {

    private FrontierGame frontierGame;
    private SpriteBatchInterface spriteBatchWrapper;
    private ExtendViewport background;
    private ScreenViewport menu;
    private Stage stage;
    private Skin skin;

    public LevelSelectionScreen(FrontierGame frontierGame) {
        this.frontierGame = frontierGame;
        this.spriteBatchWrapper = frontierGame.getBatch();
        Gdx.graphics.setCursor(CursorFactory.createDefaultCursor());
        background = new ExtendViewport(16, 9);
        background.getCamera().position.set(8, 4.5f, 0);

        // create
        menu = new ScreenViewport();
        stage = new Stage(menu, spriteBatchWrapper.getBatch());
        Gdx.input.setInputProcessor(stage);

        skin = AssetManagerInstance.getManager().get(AppProperties.SKIN_PATH, Skin.class);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label levelSelectionLabel = new Label("Level Selection", skin);

        table.add(levelSelectionLabel).pad(10).row();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1); // Red, Green, Blue, Alpha (1,1,1,1) = White
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleSwitch();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void handleSwitch() {
        // Temporary to switch screen when ENTER is pressed.
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            frontierGame.switchScreen(new LoadingScreen(frontierGame));
        }
    }
}
