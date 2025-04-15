package com.zhaw.frontier;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.zhaw.frontier.configs.AppConfig;
import com.zhaw.frontier.screens.LoadingScreen;
import com.zhaw.frontier.utils.AppConfigLoader;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.wrappers.FrontierSpriteBatch;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;

public class FrontierGame extends Game {

    private SpriteBatchInterface batch;
    private AppConfig appConfig;

    @Override
    public void create() {
        batch = new FrontierSpriteBatch();

        this.appConfig = AppConfigLoader.ReadAppConfig();
        this.setScreen(new LoadingScreen(this));
    }

    @Override
    public void dispose() {
        AssetManagerInstance.getManager().dispose();
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

    public AppConfig getAppConfig() {
        return appConfig;
    }
}
