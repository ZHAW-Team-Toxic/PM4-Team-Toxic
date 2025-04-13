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
import com.zhaw.frontier.systems.MapLoader;
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
        this.assetManager = game.getAssetManager();
        this.batch = game.getBatch();
        this.font = new BitmapFont();
        this.mapLoaderSystem = new MapLoader();
    }

    @Override
    public void show() {
        // Add new assets here
        assetManager.load("unpacked/Frontier Sky Background.png", Texture.class);
        assetManager.load("unpacked/Frontier Ground Background.png", Texture.class);
        assetManager.load("unpacked/Frontier Tower.png", Texture.class);
        assetManager.load("unpacked/Frontier Knights 1.png", Texture.class);
        assetManager.load("unpacked/Frontier Knights 2.png", Texture.class);
        assetManager.load("unpacked/Frontier Knights 3.png", Texture.class);
        assetManager.load("unpacked/Frontier Knights 4.png", Texture.class);
        assetManager.load("unpacked/Frontier Knights 5.png", Texture.class);
        assetManager.load("unpacked/Frontier Knights 6.png", Texture.class);
        assetManager.load("unpacked/fireball_1.png", Texture.class);
        assetManager.load("unpacked/fireball_2.png", Texture.class);
        assetManager.load("unpacked/fireball_3.png", Texture.class);
        assetManager.load("unpacked/fireball_4.png", Texture.class);
        assetManager.load("unpacked/fireball_5.png", Texture.class);
        assetManager.load("unpacked/fireball_6.png", Texture.class);
        assetManager.load("unpacked/fireball_7.png", Texture.class);
        assetManager.load("unpacked/fireball_8.png", Texture.class);
        assetManager.load("unpacked/Frontier Enemies.png", Texture.class);
        assetManager.load("unpacked/Frontier Logo.png", Texture.class);

        assetManager.load("packed/textures.atlas", TextureAtlas.class);
        assetManager.load("libgdx.png", Texture.class);
        assetManager.load("skins/skin.json", Skin.class);

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
