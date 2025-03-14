package com.zhaw.frontier;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.zhaw.frontier.screens.LoadingScreen;
import com.zhaw.frontier.wrappers.FrontierSpriteBatch;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;

public class FrontierGame extends Game {

    private SpriteBatchInterface batch;
    private AssetManager assetManager;

    @Override
    public void create() {
        batch = new FrontierSpriteBatch();
        assetManager = new AssetManager();

        this.setScreen(new LoadingScreen(this));
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

    public SpriteBatchInterface getBatch() {
        return batch;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}
