package com.zhaw.frontier.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ResourceUITest {

    private SpriteBatchInterface mockSpriteBatchWrapper;
    private SpriteBatch mockBatch;
    private AssetManager assetManager;
    private Skin skin;
    private Stage stage;
    private ResourceUI resourceUI;

    @BeforeEach
    public void setUp() {
        // Mocks erstellen
        mockSpriteBatchWrapper = mock(SpriteBatchInterface.class);
        mockBatch = mock(SpriteBatch.class);
        when(mockSpriteBatchWrapper.getBatch()).thenReturn(mockBatch);

        // AssetManager vorbereiten
        assetManager = new AssetManager();
        assetManager.load("skins/skin.json", Skin.class);
        assetManager.finishLoading();
        skin = assetManager.get("skins/skin.json", Skin.class);

        // Stage vorbereiten
        stage = new Stage(new ScreenViewport(), mockBatch);

        // Testobjekt initialisieren
        resourceUI = new ResourceUI(skin, stage);
    }

    @Test
    public void testUpdateResourcesSetsCorrectLabelText() {
        // Arrange
        int wood = 10, woodIncome = 2;
        int stone = 20, stoneIncome = 3;
        int gold = 30, goldIncome = 4;

        // Act
        resourceUI.updateResources(wood, woodIncome, stone, stoneIncome, gold, goldIncome);

        // Assert
        assertEquals("Holz: 10 + 2", resourceUI.getWoodLabel().getText().toString());
        assertEquals("Stein: 20 + 3", resourceUI.getStoneLabel().getText().toString());
        assertEquals("Gold: 30 + 4", resourceUI.getGoldLabel().getText().toString());
    }
}
