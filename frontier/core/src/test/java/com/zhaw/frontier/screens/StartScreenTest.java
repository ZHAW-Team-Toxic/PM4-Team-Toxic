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
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.GdxExtension;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxExtension.class)
class StartScreenTest {

    private FrontierGame mockGame;
    private SpriteBatch mockBatch;
    private StartScreen startScreen;
    private SpriteBatchInterface mockSpriteBatchWrapper;
    private AssetManager mockAssetManager;
    private TextureAtlas mockAtlas;

    @BeforeEach
    void setUp() {
        mockGame = mock(FrontierGame.class);
        mockBatch = mock(SpriteBatch.class);
        mockSpriteBatchWrapper = mock(SpriteBatchInterface.class);
        mockAssetManager = mock(AssetManager.class);
        mockAtlas = mock(TextureAtlas.class);

        // Setup SpriteBatch & AssetManager
        when(mockGame.getBatch()).thenReturn(mockSpriteBatchWrapper);
        when(mockSpriteBatchWrapper.getBatch()).thenReturn(mockBatch);
        when(mockGame.getAssetManager()).thenReturn(mockAssetManager);

        // Dummy TextureRegion für alle Regionen
        TextureAtlas.AtlasRegion dummyRegion = mock(TextureAtlas.AtlasRegion.class);
        when(mockAtlas.findRegion(anyString())).thenReturn(dummyRegion);
        when(mockAtlas.findRegion(anyString(), anyInt())).thenReturn(dummyRegion);

        // Rückgabe des Atlas
        when(mockAssetManager.get("packed/titlescreen/titlescreenAtlas.atlas", TextureAtlas.class))
            .thenReturn(mockAtlas);

        // Dummy Skin & Styles
        Skin mockSkin = new Skin();
        BitmapFont dummyFont = new BitmapFont();
        BitmapFont archivoBlackFont = new BitmapFont();
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = dummyFont;
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = dummyFont;

        mockSkin.add("default", buttonStyle);
        mockSkin.add("default", labelStyle);
        mockSkin.add("default-font", dummyFont);
        mockSkin.add("ArchivoBlack", archivoBlackFont, BitmapFont.class);
        when(mockAssetManager.get("skins/skin.json", Skin.class)).thenReturn(mockSkin);

        // Starte Screen
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
}
