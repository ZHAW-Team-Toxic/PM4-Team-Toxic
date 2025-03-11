package com.zhaw.frontier;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.zhaw.frontier.screens.GameScreen;
import com.zhaw.frontier.screens.GreenScreen;
import com.badlogic.gdx.files.FileHandle;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

public class ScreenshotTestApp extends ApplicationAdapter {
    private FrameBuffer fbo;
    private SpriteBatch batch;
    private Screen screen;
    private int state = 0;
    private CountDownLatch latch;
    private boolean testFailed = false;

    public boolean result = false;

    public ScreenshotTestApp(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, 800, 600, false);
        Gdx.app.postRunnable(this::switchToNextScreen);
    }

    private void switchToNextScreen() {
        FrontierGame frontierGame = new FrontierGame();
        frontierGame.create();
        if (state == 0) {
            screen = frontierGame.getScreen();
        } else if (state == 1) {
            frontierGame.switchScreen(new GameScreen(frontierGame));
            screen = frontierGame.getScreen();
        } else {
            result = !testFailed;
            Gdx.app.exit();
            latch.countDown();
            return;
        }
    }

    private long lastSwitchTime = 0;

    @Override
    public void render() {
        if (screen == null) return;
        screen.render(Gdx.graphics.getDeltaTime());

        if (TimeUtils.timeSinceMillis(lastSwitchTime) > 1000 && state < 2) {
            String screenshotPath = "test_output/screen_" + state + ".png";
            String baselinePath = "test_baselines/expected/screen_" + state + "_expected.png";

            takeScreenshot(screenshotPath);
            if (!compareWithBaseline(screenshotPath, baselinePath)) {
                testFailed = true;
            }

            state++;
            lastSwitchTime = TimeUtils.millis();
            switchToNextScreen();
        }
    }

    private void takeScreenshot(String path) {
        fbo.begin();
        screen.render(Gdx.graphics.getDeltaTime());
        fbo.end();

        byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, fbo.getWidth(), fbo.getHeight(), false);

        // Create a new Pixmap with the same dimensions and RGBA8888 format
        Pixmap pixmap = new Pixmap(fbo.getWidth(), fbo.getHeight(), Pixmap.Format.RGBA8888);
        
        // Copy pixel data into the Pixmap's pixel buffer
        pixmap.getPixels().clear(); // ensure buffer is cleared
        pixmap.getPixels().put(pixels);
        pixmap.getPixels().position(0);
        
        // Flip vertically (LibGDX framebuffer is flipped)
        Pixmap flipped = flipPixmapVertically(pixmap);

        FileHandle file = Gdx.files.local(path);
        PixmapIO.writePNG(file, flipped);
        pixmap.dispose();
    }

    private static Pixmap flipPixmapVertically(Pixmap original) {
        Pixmap flipped = new Pixmap(original.getWidth(), original.getHeight(), original.getFormat());
        ByteBuffer pixels = original.getPixels();
    
        int bytesPerPixel = 4;
        int width = original.getWidth();
        int height = original.getHeight();
    
        byte[] line = new byte[width * bytesPerPixel];
    
        for (int y = 0; y < height; y++) {
            pixels.position((height - y - 1) * width * bytesPerPixel);
            pixels.get(line);
            flipped.getPixels().position(y * width * bytesPerPixel);
            flipped.getPixels().put(line);
        }
    
        flipped.getPixels().position(0);
        return flipped;
    }

    private boolean compareWithBaseline(String actualPath, String expectedPath) {
        Pixmap actual = new Pixmap(Gdx.files.local(actualPath));
        Pixmap expected = new Pixmap(Gdx.files.internal(expectedPath));
    
        if (actual.getWidth() != expected.getWidth() || actual.getHeight() != expected.getHeight()) {
            System.out.println("Image size mismatch");
            return false;
        }
    
        int maxMismatches = 100;
        int mismatches = 0;
        int tolerance = 20; // per channel, max difference allowed
    
        for (int y = 0; y < actual.getHeight(); y++) {
            for (int x = 0; x < actual.getWidth(); x++) {
                int pixelA = actual.getPixel(x, y);
                int pixelB = expected.getPixel(x, y);
    
                if (!pixelsMatch(pixelA, pixelB, tolerance)) {
                    mismatches++;
                    if (mismatches > maxMismatches) {
                        System.out.printf("Too many mismatches (> %d), aborting comparison\n", maxMismatches);
                        actual.dispose();
                        expected.dispose();
                        return false;
                    }
                }
            }
        }
    
        actual.dispose();
        expected.dispose();
        return true;
    }
    
    private boolean pixelsMatch(int p1, int p2, int tolerance) {
        int r1 = (p1 & 0xff000000) >>> 24;
        int g1 = (p1 & 0x00ff0000) >>> 16;
        int b1 = (p1 & 0x0000ff00) >>> 8;
    
        int r2 = (p2 & 0xff000000) >>> 24;
        int g2 = (p2 & 0x00ff0000) >>> 16;
        int b2 = (p2 & 0x0000ff00) >>> 8;

        
    
        return Math.abs(r1 - r2) <= tolerance &&
               Math.abs(g1 - g2) <= tolerance &&
               Math.abs(b1 - b2) <= tolerance;
    }

    @Override
    public void dispose() {
        batch.dispose();
        fbo.dispose();
        if (screen != null) screen.dispose();
    }
}