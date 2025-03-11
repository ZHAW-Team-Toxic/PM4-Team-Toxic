package com.zhaw.tests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.badlogic.gdx.graphics.Texture;
import com.zhaw.frontier.Main;
import com.zhaw.frontier.wrappers.BatchInterface;
import com.zhaw.frontier.wrappers.GameBatch;

@ExtendWith(GdxExtension.class)
public class MainTest {
    private BatchInterface spriteBatchWrapperMock;
    private Main main;

    @BeforeEach
    void setUp() {
        spriteBatchWrapperMock = mock(GameBatch.class);
        main = new Main();
        main.setSpriteBatchWrapper(spriteBatchWrapperMock);
        main.create();
    }

    @Test
    void testBegin() {
        main.render();
        verify(spriteBatchWrapperMock, times(1)).begin();
    }

    @Test
    void testEnd() {
        main.render();
        verify(spriteBatchWrapperMock, times(1)).end();
    }

    @Test
    void testDrawTextureAtPosition() {
        main.render();
        verify(spriteBatchWrapperMock, atLeastOnce()).draw(any(Texture.class), eq(140.0f), eq(210.0f));
    }


    @Test
    void testDispose() {
        main.dispose();
        verify(spriteBatchWrapperMock, times(1)).dispose();
    }
}
