package com.zhaw.frontier;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.zhaw.frontier.configs.AppConfig;
import com.zhaw.frontier.screens.LoadingScreen;
import com.zhaw.frontier.utils.AppConfigLoader;
import com.zhaw.frontier.wrappers.FrontierSpriteBatch;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;

public class FrontierGame extends Game {

    private SpriteBatchInterface batch;
    private AssetManager assetManager;
    private AppConfig appConfig;

    @Override
    public void create() {
        batch = new FrontierSpriteBatch();
        assetManager = new AssetManager();

        this.appConfig = AppConfigLoader.ReadAppConfig();
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
        // just a test to get the coverage to analyze this
        setScreen(newScreen);
    }

    public SpriteBatchInterface getBatch() {
        return batch;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }
}
