package com.zhaw.frontier;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.zhaw.frontier.screens.StartScreen;
import com.zhaw.frontier.wrappers.SpriteBatchWrapper;

public class FrontierGame extends Game {
    private SpriteBatchWrapper batch;
    private AssetManager assetManager;

    @Override
    public void create() {
        batch = new SpriteBatchWrapper();
        assetManager = new AssetManager();
        assetManager.load("libgdx.png", Texture.class);

        this.setScreen(new StartScreen(this));
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }

    public void switchScreen(Screen newScreen) {
        // Dispose of the current screen before switching
        if (getScreen() != null) {
            getScreen().dispose();
        }
        setScreen(newScreen);
    }

    public SpriteBatchWrapper getBatch() {
        return batch;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}
