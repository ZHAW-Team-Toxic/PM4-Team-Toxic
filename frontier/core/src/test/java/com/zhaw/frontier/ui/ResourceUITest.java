package com.zhaw.frontier.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zhaw.frontier.configs.AppProperties;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testklasse für die ResourceUI.
 * Überprüft, ob Ressourcenwerte korrekt im UI angezeigt werden.
 */
public class ResourceUITest {

    private SpriteBatchInterface mockSpriteBatchWrapper;
    private SpriteBatch mockBatch;
    private AssetManager assetManager;
    private Skin skin;
    private Stage stage;
    private ResourceUI resourceUI;

    /**
     * Initialisiert Skin, Stage und ResourceUI vor jedem Test.
     */
    @BeforeEach
    public void setUp() {
        // Mocks erstellen
        mockSpriteBatchWrapper = mock(SpriteBatchInterface.class);
        mockBatch = mock(SpriteBatch.class);
        when(mockSpriteBatchWrapper.getBatch()).thenReturn(mockBatch);

        // AssetManager vorbereiten
        assetManager = new AssetManager();
        assetManager.load(AppProperties.SKIN_PATH, Skin.class);
        assetManager.finishLoading();
        skin = assetManager.get(AppProperties.SKIN_PATH, Skin.class);

        // Stage vorbereiten
        stage = new Stage(new ScreenViewport(), mockBatch);

        // ResourceUI initialisieren
        resourceUI = new ResourceUI(skin, stage);
    }

    /**
     * Testet, ob die Labels nach updateResources(...) korrekte Werte anzeigen.
     */
    @Test
    public void testUpdateResourcesSetsCorrectLabelText() {
        // Arrange
        int wood = 10, woodIncome = 2;
        int stone = 20, stoneIncome = 3;
        int iron = 30, ironIncome = 4;

        // Act
        resourceUI.updateResources(wood, woodIncome, stone, stoneIncome, iron, ironIncome);

        // Assert
        assertEquals(
            AppProperties.RESOURCE_UI_WOOD_LABEL_TEXT + ": 10 + 2",
            resourceUI.getWoodLabel().getText().toString()
        );
        assertEquals(
            AppProperties.RESOURCE_UI_STONE_LABEL_TEXT + ": 20 + 3",
            resourceUI.getStoneLabel().getText().toString()
        );
        assertEquals(
            AppProperties.RESOURCE_UI_IRON_LABEL_TEXT + ": 30 + 4",
            resourceUI.getIronLabel().getText().toString()
        );
    }

    /**
     * Testet die Anzeige von Ressourcen, wenn alle Werte 0 sind.
     */
    @Test
    public void testUpdateResourcesWithZeroValues() {
        // Act
        resourceUI.updateResources(0, 0, 0, 0, 0, 0);

        // Assert
        assertEquals(
            AppProperties.RESOURCE_UI_WOOD_LABEL_TEXT + ": 0 + 0",
            resourceUI.getWoodLabel().getText().toString()
        );
        assertEquals(
            AppProperties.RESOURCE_UI_STONE_LABEL_TEXT + ": 0 + 0",
            resourceUI.getStoneLabel().getText().toString()
        );
        assertEquals(
            AppProperties.RESOURCE_UI_IRON_LABEL_TEXT + ": 0 + 0",
            resourceUI.getIronLabel().getText().toString()
        );
    }
}
