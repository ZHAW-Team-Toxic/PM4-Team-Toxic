package com.zhaw.frontier;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zhaw.frontier.screens.HomeScreen;
import com.zhaw.frontier.wrappers.SpriteBatchWrapper;

public class FrontierGame extends Game {
    private Engine engine;
    private ExtendViewport extendedViewport;
    private ScreenViewport screenViewport;
    private SpriteBatchWrapper spriteBatchWrapper;

    @Override
    public void create() {
        engine = new Engine();
        // create view with world coordinates
        extendedViewport = new ExtendViewport(8, 8);
        extendedViewport.getCamera().position.set(8, 4, 0);

        screenViewport = new ScreenViewport();
        spriteBatchWrapper = spriteBatchWrapper == null ? new SpriteBatchWrapper() : spriteBatchWrapper;
        this.setScreen(new HomeScreen(this));
    }
    
    public void switchScreen(Screen newScreen) {
        // Dispose of the current screen before switching
        if (getScreen() != null) {
            getScreen().dispose();
        }
        setScreen(newScreen);
    }

    public Engine getEngine() {
        return this.engine;
    }

    public ExtendViewport getExtendedViewport() {
        return extendedViewport;
    }

    public ScreenViewport getScreenViewport() {
        return screenViewport;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void setExtendedViewport(ExtendViewport extendedViewport) {
        this.extendedViewport = extendedViewport;
    }

    public void setScreenViewport(ScreenViewport screenViewport) {
        this.screenViewport = screenViewport;
    }

    public SpriteBatchWrapper getSpriteBatchWrapper() {
        return spriteBatchWrapper;
    }

    public void setSpriteBatchWrapper(SpriteBatchWrapper spriteBatchWrapper) {
        this.spriteBatchWrapper = spriteBatchWrapper;
    }
}
