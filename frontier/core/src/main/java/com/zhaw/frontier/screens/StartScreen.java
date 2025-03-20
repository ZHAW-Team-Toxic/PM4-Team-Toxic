package com.zhaw.frontier.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;

public class StartScreen extends ScreenAdapter {

    private FrontierGame frontierGame;
    private SpriteBatchInterface spriteBatchWrapper;
    private ExtendViewport background;
    private ScreenViewport menu;
    private Stage stage;
    private Skin skin;

    public StartScreen(FrontierGame frontierGame) {
        this.frontierGame = frontierGame;
        this.spriteBatchWrapper = frontierGame.getBatch();
        background = new ExtendViewport(16, 9);
        background.getCamera().position.set(8, 4.5f, 0);

        // create
        menu = new ScreenViewport();
        stage = new Stage(menu, spriteBatchWrapper.getBatch());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("skins/skin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton startButton = new TextButton("Start", skin);
        TextButton loadButton = new TextButton("Load", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        startButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    frontierGame.switchScreen(new LevelSelectionScreen(frontierGame));
                }
            }
        );

        loadButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Loading...");
                }
            }
        );

        exitButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.exit();
                }
            }
        );

        table.add(startButton).pad(10).row();
        table.add(loadButton).pad(10).row();
        table.add(exitButton).pad(10).row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1); // Red, Green, Blue, Alpha (1,0,0,1) = Red
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public Stage getStage() {
        return stage;
    }

    public Table getTable() {
        return (Table) stage.getActors().first();
    }
}
