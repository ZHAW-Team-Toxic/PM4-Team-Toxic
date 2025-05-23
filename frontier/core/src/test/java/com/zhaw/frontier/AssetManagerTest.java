package com.zhaw.frontier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.zhaw.frontier.configs.AppProperties;
import com.zhaw.frontier.utils.AssetManagerInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxExtension.class)
public class AssetManagerTest {

    private static AssetManager assetManager;

    @BeforeAll
    static void setup() {
        assetManager = AssetManagerInstance.getManager();

        // Synchronously load required assets for the test
        assetManager.load(AppProperties.SKIN_PATH, Skin.class);
        assetManager.load(AppProperties.TEXTURE_ATLAS_PATH, TextureAtlas.class);
        assetManager.load("skins/skin.atlas", TextureAtlas.class);
        assetManager.finishLoading(); // Make sure everything is loaded
    }

    @Test
    void testBuildingMenuAssets() {
        assertTrue(
            assetManager.isLoaded(AppProperties.SKIN_PATH, Skin.class),
            "Skin asset should be loaded"
        );
        Skin skin = assetManager.get(AppProperties.SKIN_PATH, Skin.class);
        assertNotNull(
            skin.getDrawable("build_menu_96_32"),
            "Drawable 'build_menu_96_32' should exist in skin"
        );
        assertNotNull(skin.getDrawable("selected"), "Drawable 'selected' should exist in skin");
    }

    @Test
    void testBasicUiAssets() {
        TextureAtlas atlas = assetManager.get("skins/skin.atlas", TextureAtlas.class);

        assertNotNull(atlas.findRegion("BrokenPickaxe"), "Atlas should contain 'BrokenPickaxe'");
        assertNotNull(atlas.findRegion("Pickaxe"), "Atlas should contain 'Pickaxe'");
        assertNotNull(atlas.findRegion("Campfire"), "Atlas should contain 'Campfire'");
        assertNotNull(atlas.findRegion("Gears"), "Atlas should contain 'Gears'");
    }

    @Test
    void testSkinContainsArchivoBlackFont() {
        Skin skin = assetManager.get(AppProperties.SKIN_PATH, Skin.class);
        assertNotNull(skin.getFont("ArchivoBlack"), "Skin should contain font 'ArchivoBlack'");
    }

    @Test
    void testTextureAtlasIsLoaded() {
        assertTrue(
            assetManager.isLoaded(AppProperties.TEXTURE_ATLAS_PATH, TextureAtlas.class),
            "Texture atlas should be loaded"
        );

        TextureAtlas atlas = assetManager.get(AppProperties.TEXTURE_ATLAS_PATH, TextureAtlas.class);
        assertNotNull(atlas.findRegion("demo/donkey"), "Texture region 'demo/donkey' should exist");
    }

    @Test
    void testAllEssentialAssetsAreLoaded() {
        assertTrue(assetManager.getProgress() == 1f, "All assets should be fully loaded");
    }
}
