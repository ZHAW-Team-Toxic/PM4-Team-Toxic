package com.zhaw.frontier.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.zhaw.frontier.FrontierGame;

/**
 * LoadingScreen
 */
public class LoadingScreen extends ScreenAdapter {
    private AssetManager assetManager;
    private FrontierGame game;

    public LoadingScreen(FrontierGame game) {
        this.game = game;
        this.assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        // add assets which need to be loaded
        assetManager.load("libgdx.png", Texture.class);
    }

    @Override
    public void render(float delta) {
        if (assetManager.update()) {
            // after loading switch
            game.switchScreen(new GameScreen(game));
        }

        // display loading information
        float progress = assetManager.getProgress();
        Gdx.app.log("loading progress", Float.toString(progress));

    }
}
