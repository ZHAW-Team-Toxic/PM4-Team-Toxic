package com.zhaw.frontier.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.wrappers.SpriteBatchWrapper;

public class LoadingScreen extends ScreenAdapter {
    private FrontierGame game;
    private AssetManager assetManager;
    private SpriteBatchWrapper batch;
    private BitmapFont font;

    public LoadingScreen(FrontierGame game) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        this.batch = game.getBatch();
        this.font = new BitmapFont();
    }

    @Override
    public void show() {
        // Add new assets here
        assetManager.load("libgdx.png", Texture.class);
    }

    @Override
    public void render(float delta) {
        if (assetManager.update()) {
            game.switchScreen(new GameScreen(game));
        }

        float progress = assetManager.getProgress();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(batch.getBatch(), "Loading: " + (int) (progress * 100) + "%",
            (float) Gdx.graphics.getWidth() / 2 - 30, (float) Gdx.graphics.getHeight() / 2);
        batch.end();
    }
}
