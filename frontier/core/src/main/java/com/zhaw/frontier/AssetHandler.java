package com.zhaw.frontier;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.JsonValue;

public class AssetHandler {
    private AssetManager assetManager;

    public AssetHandler() {
        assetManager = new AssetManager();
    }

    public void loadAssets() {
        assetManager.load("ui/textures.atlas", TextureAtlas.class);
        assetManager.finishLoading();
    }

    public TextureRegion getSprite(String atlasName, String spriteName) {
        TextureAtlas atlas = assetManager.get(atlasName, TextureAtlas.class);
        return atlas.findRegion(spriteName);
    }

    public Texture getTexture(String assetName) {
        return assetManager.get(assetName, Texture.class);
    }

    public Sound getSound(String assetName) {
        return assetManager.get(assetName, Sound.class);
    }

    public Music getMusic(String assetName) {
        return assetManager.get(assetName, Music.class);
    }

    public Skin getSkin(String assetName) {
        return assetManager.get(assetName, Skin.class);
    }

    public JsonValue getJson(String assetName) {
        return assetManager.get(assetName, JsonValue.class);
    }

    public void dispose() {
        assetManager.dispose();
    }
}
