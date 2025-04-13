package com.zhaw.frontier.screens;

import static org.mockito.Mockito.*;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.GdxExtension;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxExtension.class)
class GameScreenTest {

    private FrontierGame mockGame;
    private SpriteBatch mockBatch;
    private StartScreen startScreen;
    private SpriteBatchInterface mockSpriteBatchWrapper;
    private AssetManager mockAssetManager;
    private TextureAtlas mockAtlas;
    private BitmapFont mockFont;
    private Skin mockSkin;

    @BeforeEach
    void setUp() {
        mockGame = mock(FrontierGame.class);
        mockBatch = mock(SpriteBatch.class);
        mockSpriteBatchWrapper = mock(SpriteBatchInterface.class);
        mockAssetManager = mock(AssetManager.class);
        mockAtlas = mock(TextureAtlas.class);
        mockFont = mock(BitmapFont.class);  // Mock the BitmapFont
        mockSkin = mock(Skin.class);  // Mock the Skin

        // Mock the AssetManager to return the mock TextureAtlas
        when(mockAssetManager.get("packed/titlescreen/titlescreenAtlas.atlas", TextureAtlas.class))
            .thenReturn(mockAtlas);

        // Mock the findRegions method to return a valid Array
        TextureAtlas.AtlasRegion dummyRegion = mock(TextureAtlas.AtlasRegion.class);
        Array<TextureAtlas.AtlasRegion> knightRegions = new Array<>();
        knightRegions.add(dummyRegion);  // Add a dummy region to simulate the expected result
        when(mockAtlas.findRegions("Frontier_Knights")).thenReturn(knightRegions);

        // Set up the TextButtonStyle with a mock font
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = mockFont;  // Provide the mocked font to the style
        when(mockSkin.get(TextButton.TextButtonStyle.class)).thenReturn(buttonStyle);

        // Mock the get method for the skin
        when(mockAssetManager.get("skins/skin.json", Skin.class)).thenReturn(mockSkin);

        // Mock the game dependencies
        when(mockGame.getAssetManager()).thenReturn(mockAssetManager);
        when(mockGame.getBatch()).thenReturn(mockSpriteBatchWrapper);
        when(mockSpriteBatchWrapper.getBatch()).thenReturn(mockBatch);

        // Create the StartScreen instance
        startScreen = spy(new StartScreen(mockGame));
    }

    @Test
    void testStartButtonFiresScreenSwitch() {
        TextButton startButton = (TextButton) startScreen.getTable().getChildren().first();

        InputEvent event = new InputEvent();
        event.setType(InputEvent.Type.touchDown);
        event.setListenerActor(startButton);
        event.setStage(startScreen.getStage());

        startButton.fire(event);
        event.setType(InputEvent.Type.touchUp);
        startButton.fire(event);

        verify(mockGame).switchScreen(any());
    }

    @Test
    void testFireballFramesLoadedCorrectly() {
        for (int i = 1; i <= 8; i++) {
            verify(mockAtlas).findRegion("Fireball", i);
        }
    }

    @Test
    void testKnightFramesLoadedCorrectly() {
        for (int i = 1; i <= 6; i++) {
            verify(mockAtlas).findRegion("Frontier_Knights", i);
        }
    }

    @Test
    void testAllStaticAssetsLoaded() {
        verify(mockAtlas).findRegion("Frontier_Tower");
        verify(mockAtlas).findRegion("Frontier_Logo");
        verify(mockAtlas).findRegion("Frontier_Enemies");
        verify(mockAtlas).findRegion("Frontier_Sky_Background");
        verify(mockAtlas).findRegion("Frontier_Ground_Background");
    }

    @AfterEach
    void tearDown() {
        startScreen.dispose();
        mockAssetManager.dispose();
    }
}
