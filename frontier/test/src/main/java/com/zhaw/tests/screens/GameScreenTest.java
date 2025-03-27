package com.zhaw.tests.screens;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.exceptions.MapLoadingException;
import com.zhaw.frontier.screens.GameScreen;
import com.zhaw.frontier.screens.PauseScreen;
import com.zhaw.frontier.systems.MapLoader;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;
import com.zhaw.tests.GdxExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Path;

import static org.mockito.Mockito.*;

@ExtendWith(GdxExtension.class)
class GameScreenTest {

    private FrontierGame mockGame;

    @BeforeEach
    void setup() throws MapLoadingException {
        mockGame = mock(FrontierGame.class);

        SpriteBatch mockSpriteBatch = mock(SpriteBatch.class);
        SpriteBatchInterface mockBatch = mock(SpriteBatchInterface.class);

        when(mockSpriteBatch.getColor()).thenReturn(new Color(1, 1, 1, 1));

        when(mockBatch.getBatch()).thenReturn(mockSpriteBatch);
        when(mockGame.getBatch()).thenReturn(mockBatch);

        AssetManager assetManager = new AssetManager();
        when(mockGame.getAssetManager()).thenReturn(assetManager);

        Path mapPath = Path.of("TMX/frontier_testmap.tmx");
        MapLoader.getInstance().loadMap(assetManager, mapPath);
        assetManager.finishLoading();
    }

    @Test
    void pressingEsc_switchesToPauseScreen() {
        Input mockInput = mock(Input.class);
        Gdx.input = mockInput;
        when(mockInput.isKeyJustPressed(Input.Keys.ESCAPE)).thenReturn(true);

        GameScreen screen = spy(new GameScreen(mockGame));
        screen.render(1 / 60f);

        verify(mockGame).switchScreen(any(PauseScreen.class));
    }
}
