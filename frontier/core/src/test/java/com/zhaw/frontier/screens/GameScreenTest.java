package com.zhaw.frontier.screens;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.GameScreenUtils;
import com.zhaw.frontier.GdxExtension;
import com.zhaw.frontier.exceptions.MapLoadingException;
import com.zhaw.frontier.systems.MapLoader;
import com.zhaw.frontier.utils.AssetManagerInstance;
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

        AssetManagerInstance.getManager().load("packed/textures.atlas", TextureAtlas.class);
        AssetManagerInstance.getManager().load("libgdx.png", Texture.class);
        AssetManagerInstance.getManager().load("skins/skin.json", Skin.class);

        Path mapPath = Path.of("TMX/frontier_testmap.tmx");
        MapLoader.getInstance().loadMap(AssetManagerInstance.getManager(), mapPath);

        AssetManagerInstance.getManager().finishLoading();

        gameScreen = new GameScreen(mockGame);

        Stage stage = mock(Stage.class);
        ScreenViewport screenViewport = mock(ScreenViewport.class);
        GameScreenUtils.setStage(gameScreen, "stage", stage);
        GameScreenUtils.setScreenViewport(gameScreen, "gameUi", screenViewport);
    }

    @Test
    void pressingEsc_switchesToPauseScreen() {
        Input mockInput = mock(Input.class);
        Gdx.input = mockInput;
        when(mockInput.isKeyJustPressed(Input.Keys.ESCAPE)).thenReturn(true);
        gameScreen.handleInput();
        verify(mockGame).switchScreen(any(PauseScreen.class));
    }
}
