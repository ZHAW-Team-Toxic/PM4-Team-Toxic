package com.zhaw.frontier.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.savegame.SaveGameManager;
import com.zhaw.frontier.ui.PauseScreenUI;
import com.zhaw.frontier.utils.AssetManagerInstance;

/**
 * Shows a pause screen with options to resume, save, or save and exit the game.
 */
public class PauseScreen extends ScreenAdapter {

    private final GameScreen gameScreen;
    private final FrontierGame frontierGame;
    private final SaveGameManager saveGameManager;
    private Stage stage;
    private Skin skin;

    public PauseScreen(FrontierGame frontierGame, GameScreen gameScreen) {
        this.frontierGame = frontierGame;
        this.gameScreen = gameScreen;

        this.saveGameManager = new SaveGameManager(gameScreen.getEngine());
    }

    @Override
    public void show() {
        this.stage = new Stage(new ExtendViewport(1920, 1080, new OrthographicCamera()));
        this.skin = AssetManagerInstance.getManager().get("skins/skin.json", Skin.class);
        stage.clear();

        new PauseScreenUI(stage, skin, this::resumeGame, this::saveGame, this::exitGame);

        Gdx.input.setInputProcessor(stage);
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
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void resumeGame() {
        frontierGame.switchScreen(gameScreen);
    }

    private void saveGame() {
        Gdx.app.log("Save", "Saving...");

        saveGameManager.saveGame("saveFile.json");
    }

    private void exitGame() {
        Gdx.app.exit();
    }
}
