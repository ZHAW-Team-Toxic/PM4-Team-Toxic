package com.zhaw.frontier.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zhaw.frontier.FrontierGame;

/**
 * Shows a pause screen with options to resume, save, or save and exit the game.
 */
public class PauseScreen implements Screen {

    private FrontierGame frontierGame;
    private Stage stage;
    private Skin skin;

    public PauseScreen(FrontierGame frontierGame) {
        this.frontierGame = frontierGame;

        this.stage = new Stage(new ExtendViewport(1920, 1080,  new OrthographicCamera()));
        this.skin = frontierGame.getAssetManager().get("skins/skin.json", Skin.class);;
    }

    @Override
    public void show() {
        stage.clear();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton resumeButton = new TextButton("Resume", skin);
        TextButton saveButton = new TextButton("Save", skin);
        TextButton saveExitButton = new TextButton("Save & Exit", skin);

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resumeGame();
            }
        });

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveGame();
            }
        });

        saveExitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveGame();
                exitGame();
            }
        });

        table.add(resumeButton).fillX().pad(10);
        table.row();
        table.add(saveButton).fillX().pad(10);
        table.row();
        table.add(saveExitButton).fillX().pad(10);

        Gdx.input.setInputProcessor(new InputMultiplexer(stage));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            resumeGame();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void resumeGame() {
        frontierGame.switchScreen(new GameScreen(frontierGame));
    }

    private void saveGame() {
        Gdx.app.log("Save", "Saving...");
    }

    private void exitGame() {
        Gdx.app.exit();
    }
}
