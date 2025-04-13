package com.zhaw.frontier.screens;

import static org.mockito.Mockito.*;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
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
class StartScreenTest {

    private FrontierGame mockGame;
    private SpriteBatch mockBatch;
    private StartScreen startScreen;
    private SpriteBatchInterface mockSpriteBatchWrapper;
    private AssetManager mockAssetManager;

    @BeforeEach
    void setUp() {
        mockGame = mock(FrontierGame.class);
        mockBatch = mock(SpriteBatch.class);
        mockSpriteBatchWrapper = mock(SpriteBatchInterface.class);
        mockAssetManager = new AssetManager();
        mockAssetManager.load("skins/skin.json", Skin.class);
        mockAssetManager.load("unpacked/titlescreen/Frontier_Sky_Background.png", Texture.class);
        mockAssetManager.load("unpacked/titlescreen/Frontier_Ground_Background.png", Texture.class);
        mockAssetManager.load("unpacked/titlescreen/Frontier_Tower.png", Texture.class);
        mockAssetManager.load("unpacked/titlescreen/Frontier_Knights_1.png", Texture.class);
        mockAssetManager.load("unpacked/titlescreen/Frontier_Knights_2.png", Texture.class);
        mockAssetManager.load("unpacked/titlescreen/Frontier_Knights_3.png", Texture.class);
        mockAssetManager.load("unpacked/titlescreen/Frontier_Knights_4.png", Texture.class);
        mockAssetManager.load("unpacked/titlescreen/Frontier_Knights_5.png", Texture.class);
        mockAssetManager.load("unpacked/titlescreen/Frontier_Knights_6.png", Texture.class);
        mockAssetManager.load("unpacked/titlescreen/Frontier_Enemies.png", Texture.class);
        mockAssetManager.load("unpacked/titlescreen/fireball_1.png", Texture.class);
        mockAssetManager.load("unpacked/titlescreen/fireball_2.png", Texture.class);
        mockAssetManager.load("unpacked/titlescreen/fireball_3.png", Texture.class);
        mockAssetManager.load("unpacked/titlescreen/fireball_4.png", Texture.class);
        mockAssetManager.load("unpacked/titlescreen/fireball_5.png", Texture.class);
        mockAssetManager.load("unpacked/titlescreen/fireball_6.png", Texture.class);
        mockAssetManager.load("unpacked/titlescreen/fireball_7.png", Texture.class);
        mockAssetManager.load("unpacked/titlescreen/fireball_8.png", Texture.class);
        mockAssetManager.load("unpacked/titlescreen/Frontier_Logo.png", Texture.class);
        mockAssetManager.finishLoading();

        when(mockGame.getBatch()).thenReturn(mockSpriteBatchWrapper);
        when(mockSpriteBatchWrapper.getBatch()).thenReturn(mockBatch);
        when(mockGame.getAssetManager()).thenReturn(mockAssetManager);

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
