package com.zhaw.frontier.screens;

import static org.mockito.Mockito.*;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.GdxExtension;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxExtension.class)
class BaseUIScreenTest {

    private FrontierGame mockGame;
    private GameScreen mockGameScreen;
    private SpriteBatch mockBatch;
    private BaseUIScreen baseUIScreen;
    private SpriteBatchInterface mockSpriteBatchWrapper;
    private AssetManager mockAssetManager;

    @BeforeEach
    void setUp() {
        mockGame = mock(FrontierGame.class);
        mockGameScreen = mock(GameScreen.class);
        mockBatch = mock(SpriteBatch.class);
        mockSpriteBatchWrapper = mock(SpriteBatchInterface.class);
        mockAssetManager = new AssetManager();
        mockAssetManager.load("skins/skin.json", Skin.class);
        mockAssetManager.finishLoading();

        when(mockGame.getBatch()).thenReturn(mockSpriteBatchWrapper);
        when(mockSpriteBatchWrapper.getBatch()).thenReturn(mockBatch);
        when(mockGame.getAssetManager()).thenReturn(mockAssetManager);

        baseUIScreen = spy(new BaseUIScreen(mockGame, mockSpriteBatchWrapper, mockGameScreen));
    }

    @Test
    void testBaseUIPauseButton() {
        TextButton pauseButton = baseUIScreen.getStage().getRoot().findActor("pauseButton");

        InputEvent event = new InputEvent();
        event.setType(InputEvent.Type.touchDown);
        event.setListenerActor(pauseButton);
        event.setStage(pauseButton.getStage());

        pauseButton.fire(event);
        event.setType(InputEvent.Type.touchUp);

        verify(mockGame, never()).switchScreen(any());
        pauseButton.fire(event);
        verify(mockGame).switchScreen(any());
    }
}
