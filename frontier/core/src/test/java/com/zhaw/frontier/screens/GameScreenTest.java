package com.zhaw.frontier.screens;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.GdxExtension;
import com.zhaw.frontier.exceptions.MapLoadingException;
import com.zhaw.frontier.systems.MapLoader;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxExtension.class)
public class GameScreenTest {

    private FrontierGame mockGame;
    private GameScreen gameScreen;

    @BeforeEach
    void setup() throws MapLoadingException {
        mockGame = mock(FrontierGame.class);

        SpriteBatch mockSpriteBatch = mock(SpriteBatch.class);
        SpriteBatchInterface mockBatch = mock(SpriteBatchInterface.class);
        when(mockBatch.getBatch()).thenReturn(mockSpriteBatch);
        when(mockGame.getBatch()).thenReturn(mockBatch);

        AssetManager assetManager = new AssetManager();
        when(mockGame.getAssetManager()).thenReturn(assetManager);

        Path mapPath = Path.of("TMX/frontier_testmap.tmx");
        MapLoader.getInstance().loadMap(assetManager, mapPath);
        assetManager.finishLoading();
        gameScreen = new GameScreen(mockGame);
    }

    @Test
    void pressingEsc_switchesToPauseScreen() {
        Input mockInput = mock(Input.class);
        Gdx.input = mockInput;
        when(mockInput.isKeyJustPressed(Input.Keys.ESCAPE)).thenReturn(true);
        gameScreen.render(0.016f);
        verify(mockGame).switchScreen(any(PauseScreen.class));
    }
}
