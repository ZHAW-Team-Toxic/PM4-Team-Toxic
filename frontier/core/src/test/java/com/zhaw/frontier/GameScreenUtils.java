package com.zhaw.frontier;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zhaw.frontier.screens.GameScreen;

public class GameScreenUtils {

    private GameScreenUtils() {}

    public static void setScreenViewport(GameScreen screen, String fieldName, ScreenViewport ui) {
        setPrivateField(screen, fieldName, ui);
    }

    public static void setStage(GameScreen screen, String fieldName, Stage stage) {
        setPrivateField(screen, fieldName, stage);
    }

    private static void setPrivateField(Object obj, String fieldName, Object value) {
        try {
            var field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }
}
