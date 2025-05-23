package com.zhaw.frontier.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.configs.AppProperties;
import com.zhaw.frontier.systems.MapLoader;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;
import java.nio.file.Path;

/**
 * Displays a loading screen while assets are loaded by the AssetLoader.
 * Switches to the GameScreen once all assets are loaded.
 */
public class LoadingScreen extends ScreenAdapter {

    private FrontierGame game;
    private AssetManager assetManager;
    private SpriteBatchInterface batch;
    private BitmapFont font;
    private final MapLoader mapLoaderSystem;

    public LoadingScreen(FrontierGame game) {
        this.game = game;
        this.assetManager = AssetManagerInstance.getManager();
        this.batch = game.getBatch();
        this.font = new BitmapFont();
        this.mapLoaderSystem = new MapLoader();
    }

    @Override
    public void show() {
        // Add new assets here
        assetManager.load(AppProperties.TEXTURE_ATLAS_PATH, TextureAtlas.class);
        assetManager.load("libgdx.png", Texture.class);
        assetManager.load(AppProperties.SKIN_PATH, Skin.class);
        try {
            mapLoaderSystem.loadMap(assetManager, Path.of("TMX/frontier_testmap.tmx"));
        } catch (Exception e) {
            Gdx.app.error("[ERROR] - LoadingScreen", "Error loading map");
        }
    }

    @Override
    public void render(float delta) {
        if (assetManager.update()) {
            game.switchScreen(new StartScreen(game));
        }

        float progress = assetManager.getProgress();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(
            batch.getBatch(),
            "Loading: " + (int) (progress * 100) + "%",
            (float) Gdx.graphics.getWidth() / 2 - 30,
            (float) Gdx.graphics.getHeight() / 2
        );
        batch.end();
    }
}
