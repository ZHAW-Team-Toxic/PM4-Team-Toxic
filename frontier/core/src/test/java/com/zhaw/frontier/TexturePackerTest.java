package com.zhaw.frontier;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
        FileHandle fileHandle = Gdx.files.internal("packed/textures.atlas");
        assertTrue(fileHandle.exists());
    }

    /**
     * Test if the texture atlas file contains the donkey texture.
     */
    @Test
    void testLoadDonkey() {
        AssetManager assetManager = new AssetManager();
        assetManager.load("packed/textures.atlas", TextureAtlas.class);
        assetManager.finishLoading();

        TextureAtlas atlas = assetManager.get("packed/textures.atlas", TextureAtlas.class);
        Sprite donkey = new Sprite(atlas.findRegion("demo/donkey"));
        assertNotNull(donkey.getTexture());
    }
}
