package com.zhaw.tests.screens;

import static org.mockito.Mockito.*;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.screens.StartScreen;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;
import com.zhaw.tests.GdxExtension;
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
    private Skin mockSkin;

    @BeforeEach
    void setUp() {
        mockGame = mock(FrontierGame.class);
        mockBatch = mock(SpriteBatch.class);
        mockSpriteBatchWrapper = mock(SpriteBatchInterface.class);
        mockSkin = mock(Skin.class);
        mockAssetManager = mock(AssetManager.class);

        when(mockGame.getBatch()).thenReturn(mockSpriteBatchWrapper);
        when(mockSpriteBatchWrapper.getBatch()).thenReturn(mockBatch);
        when(mockGame.getAssetManager()).thenReturn(mockAssetManager);
        when(mockAssetManager.get("skins/skin.json", Skin.class)).thenReturn(mockSkin);

        startScreen = spy(new StartScreen(mockGame));
    }

    @Test
    void testStartScreenButtonActsProperly() {
        TextButton startButton = (TextButton) startScreen.getTable().getChildren().first();

        InputEvent event = new InputEvent();
        event.setType(InputEvent.Type.touchDown);
        event.setListenerActor(startButton);
        event.setStage(startScreen.getStage());

        startButton.fire(event);
        event.setType(InputEvent.Type.touchUp);

        verify(mockGame, never()).switchScreen(any());
        startButton.fire(event);
        verify(mockGame).switchScreen(any());
    }
}
