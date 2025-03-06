package com.zhaw.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.screens.GameScreen;
import com.zhaw.frontier.screens.HomeScreen;
import com.zhaw.frontier.wrappers.SpriteBatchWrapper;
import com.zhaw.tests.utils.StageUtils;

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

    @Test 
    public void menuButtonPressed(){
        gameScreen.show();
        TextButton button = StageUtils.findButtonByText(gameScreen.getStage(),"Menu");
        assertNotNull(button);
        assertFalse(button.isPressed());

        InputEvent touchDowEvent = new InputEvent();
        touchDowEvent.setType(InputEvent.Type.touchDown);
        touchDowEvent.setStage(gameScreen.getStage());
        touchDowEvent.setPointer(0);

        button.fire(touchDowEvent);
        assertTrue(button.isPressed());

        InputEvent touchUp = new InputEvent();
        touchUp.setType(InputEvent.Type.touchUp);
        touchUp.setStage(gameScreen.getStage());
        touchUp.setPointer(0);
        button.fire(touchUp);
        try {
            Thread.sleep(1000); // Sleep for 1000 milliseconds (1 second)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }        
        assertFalse(button.isPressed());
    }
}
