package com.zhaw.frontier;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxExtension.class)
public class ExampleTest {

    /**
     * This test fails if the headless backend isn't properly initialized or the @Extendwith
     * annotation is missing. If the annotation is missing you should see following error message:
     * Cannot invoke "com.badlogic.gdx.Files.internal(String)" because "com.badlogic.gdx.Gdx.files"
     * is null
     */
    @Test
    public void testHeadlessApplicationWorks() {
        assertDoesNotThrow(() -> Gdx.files.internal("testasset.txt"));
    }

    /**
     * This test fails if the gl mock does not work. If the mock does not work you should see
     * following error message: Unexpected exception thrown: java.lang.NullPointerException: Cannot
     * invoke "com.badlogic.gdx.graphics.GL20.glGenTexture()" because "com.badlogic.gdx.Gdx.gl" is
     * null
     */
    @Test
    public void testMockedGLWorks() {
        assertDoesNotThrow(() -> new Image(new Texture("testAssets/donkey.png")));
    }
}
