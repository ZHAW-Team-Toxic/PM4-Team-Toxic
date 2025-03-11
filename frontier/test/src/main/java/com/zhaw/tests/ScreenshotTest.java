package com.zhaw.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.zhaw.frontier.ScreenshotTestApp;

@ExtendWith(GdxExtension.class)
public class ScreenshotTest {

    @Test
    public void runScreenshotVisualTest() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        ScreenshotTestApp app = new ScreenshotTestApp(latch);

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Screenshot Test");
        config.setWindowedMode(800, 600);
        config.setInitialBackgroundColor(com.badlogic.gdx.graphics.Color.BLACK);

        new Lwjgl3Application(app, config);
        latch.await();

        assertTrue(app.result);
    }
}
