package com.zhaw.frontier;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.zhaw.frontier.screens.StartScreen;
import com.zhaw.frontier.wrappers.SpriteBatchWrapper;

public class FrontierGame extends Game {
    private Engine engine;
    private SpriteBatchWrapper batch;
    //TODO: Assetsmanager

    @Override
    public void create() {
        engine = new Engine();

        batch = new SpriteBatchWrapper();
        this.setScreen(new StartScreen(this));
    }

    public void switchScreen(Screen newScreen) {
        // Dispose of the current screen before switching
        if (getScreen() != null) {
            getScreen().dispose();
        }
        setScreen(newScreen);
    } 

    public Engine getEngine() {
        return engine;
    }

    public SpriteBatchWrapper getBatch() {
        return batch;
    }
}
