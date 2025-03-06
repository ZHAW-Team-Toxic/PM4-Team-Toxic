package com.zhaw.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.screens.GameScreen;
import com.zhaw.frontier.screens.HomeScreen;
import com.zhaw.frontier.wrappers.SpriteBatchWrapper;

@ExtendWith(GdxExtension.class)
public class UiTest {
    private FrontierGame mockGame;
    private HomeScreen homeScreen;
    private GameScreen gameScreen;
    private SpriteBatchWrapper spriteBatchWrapper;

    @BeforeEach
    public void setup() {
        spriteBatchWrapper = mock(SpriteBatchWrapper.class);
        when(spriteBatchWrapper.getBatch()).thenReturn(mock(SpriteBatch.class));
        mockGame = new FrontierGame();
        mockGame.setSpriteBatchWrapper(spriteBatchWrapper);
        mockGame.create();
        homeScreen = new HomeScreen(mockGame);
        gameScreen = new GameScreen(mockGame);
    }


    @Test
    public void testEnterKeySwitchScreenHomeScreen() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        com.badlogic.gdx.Gdx.input = mock(com.badlogic.gdx.Input.class);
        when(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)).thenReturn(true);

        homeScreen.render(0f); // Simulate one frame of rendering

        Screen newScreen = mockGame.getScreen();

        assertNotNull(newScreen);
        assertTrue(newScreen instanceof GameScreen);
    }

    @Test
    public void testEnterKeySwitchScreenGameScreen() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        gameScreen.show();
        com.badlogic.gdx.Gdx.input = mock(com.badlogic.gdx.Input.class);
        when(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)).thenReturn(true);
        
        Screen newScreen = mockGame.getScreen();
        assertNotNull(newScreen);
        assertTrue(newScreen instanceof HomeScreen);
        homeScreen.render(0);
        newScreen = mockGame.getScreen();

        assertNotNull(newScreen);
        assertTrue(newScreen instanceof GameScreen);

        gameScreen.render(0f); // Simulate one frame of rendering
        newScreen = mockGame.getScreen();

        assertNotNull(newScreen);
        assertTrue(newScreen instanceof HomeScreen);
    }
}
