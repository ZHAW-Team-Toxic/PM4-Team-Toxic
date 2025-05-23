package com.zhaw.frontier;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.zhaw.frontier.configs.AppProperties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test class for the texture packer.
 */
@ExtendWith(GdxExtension.class)
public class TexturePackerTest {

    /**
     * Test if the texture atlas file exists.
     */
    @Test
    void testLoadTextureAtlas() {
        FileHandle fileHandle = Gdx.files.internal(AppProperties.TEXTURE_ATLAS_PATH);
        assertTrue(fileHandle.exists());
    }

    /**
     * Test if the texture atlas file contains the donkey texture.
     */
    @Test
    void testLoadDonkey() {
        AssetManager assetManager = new AssetManager();
        assetManager.load(AppProperties.TEXTURE_ATLAS_PATH, TextureAtlas.class);
        assetManager.finishLoading();

        TextureAtlas atlas = assetManager.get(AppProperties.TEXTURE_ATLAS_PATH, TextureAtlas.class);
        Sprite donkey = new Sprite(atlas.findRegion("demo/donkey"));
        assertNotNull(donkey.getTexture());
    }
}
