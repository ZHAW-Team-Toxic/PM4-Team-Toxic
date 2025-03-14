package com.zhaw.frontier;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.zhaw.frontier.screens.StartScreen;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;
import com.zhaw.frontier.wrappers.FrontierSpriteBatch;

public class FrontierGame extends Game {
    private SpriteBatchInterface batch;
    // TODO: add AssetManager

    @Override
    public void create() {
        batch = new FrontierSpriteBatch();
        this.setScreen(new StartScreen(this));
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
}
