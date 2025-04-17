package com.zhaw.frontier.screens;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.GdxExtension;
import com.zhaw.frontier.utils.AssetManagerInstance;
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
    private BitmapFont mockFont;
    private TextureAtlas atlas;

    @BeforeEach
    void setUp() {
        mockGame = mock(FrontierGame.class);
        mockBatch = mock(SpriteBatch.class);
        mockSpriteBatchWrapper = mock(SpriteBatchInterface.class);
        mockFont = mock(BitmapFont.class);

        // --- Assets vorbereiten ---
        // Wichtig: vor .get() muss .load() und finishLoading()
        AssetManagerInstance.getManager().load("skins/skin.json", Skin.class);
        AssetManagerInstance
            .getManager()
            .load("packed/titlescreen/titlescreenAtlas.atlas", TextureAtlas.class);
        AssetManagerInstance.getManager().finishLoading(); // Blockiert bis alles geladen ist

        atlas =
        AssetManagerInstance
            .getManager()
            .get("packed/titlescreen/titlescreenAtlas.atlas", TextureAtlas.class);

        Skin mockSkin = mock(Skin.class);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = mockFont;
        when(mockSkin.get(TextButton.TextButtonStyle.class)).thenReturn(buttonStyle);

        // Mock-Rückgaben setzen
        when(mockGame.getBatch()).thenReturn(mockSpriteBatchWrapper);
        when(mockSpriteBatchWrapper.getBatch()).thenReturn(mockBatch);

        // StartScreen erzeugen
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
        Array<TextureAtlas.AtlasRegion> fireballFrames = atlas.findRegions("Fireball");
        assertEquals(8, fireballFrames.size);
    }

    @Test
    void testKnightFramesLoadedCorrectly() {
        Array<TextureAtlas.AtlasRegion> knightFrames = atlas.findRegions("Frontier_Knights");
        assertEquals(6, knightFrames.size);
    }
}
