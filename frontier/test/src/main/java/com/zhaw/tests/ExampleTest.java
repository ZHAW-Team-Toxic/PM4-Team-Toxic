package com.zhaw.tests;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.zhaw.frontier.Main;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(GdxExtension.class)
public class ExampleTest {

    /**
     * This test fails if the headless backend isn't properly initialized or
     * the @Extendwith annotation is missing.
     * If the annotation is missing you should see following error message:
     * Cannot invoke "com.badlogic.gdx.Files.internal(String)" because
     * "com.badlogic.gdx.Gdx.files" is null
     */
    @Test
    public void testHeadlessApplicationWorks() {
        assertDoesNotThrow(() -> Gdx.files.internal("testasset.txt"));
    }

    /**
     * This test fails if the gl mock does not work.
     * If the mock does not work you should see following error message:
     * Unexpected exception thrown: java.lang.NullPointerException: Cannot invoke
     * "com.badlogic.gdx.graphics.GL20.glGenTexture()" because
     * "com.badlogic.gdx.Gdx.gl" is null
     */
    @Test
    public void testMockedGLWorks() {
        assertDoesNotThrow(() -> new Image(new Texture("donkey.png")));
    }

    @Test
    public void testFail() {
        fail();
    }

}
