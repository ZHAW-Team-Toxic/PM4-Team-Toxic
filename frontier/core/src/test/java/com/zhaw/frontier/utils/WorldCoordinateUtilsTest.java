package com.zhaw.frontier.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.junit.jupiter.api.Test;

public class WorldCoordinateUtilsTest {

    @Test
    void testCalculateWorldCoordinate() {
        Viewport viewport = mock(Viewport.class);
        TiledMapTileLayer layer = mock(TiledMapTileLayer.class);

        when(layer.getTileWidth()).thenReturn(32);
        when(layer.getTileHeight()).thenReturn(32);

        // Simulate unprojection: screen (64, 96) -> world (64, 96)
        when(viewport.unproject(any(Vector3.class)))
            .thenAnswer(invocation -> {
                Vector3 input = invocation.getArgument(0);
                return new Vector3(input.x, input.y, 0);
            });

        Vector2 result = WorldCoordinateUtils.calculateWorldCoordinate(viewport, layer, 64, 96);

        assertTrue(
            result.epsilonEquals(new Vector2(2f, 3f), 0.001f),
            "Tile indices should be (2, 3)"
        );
    }

    @Test
    void testCalculateWorldCoordinateWithActualCameraSetup() {
        // Simulate your game camera setup
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.position.set(32 * 16, 32 * 16, 0);
        camera.zoom = 40.0f;
        camera.update();

        Viewport viewport = new FitViewport(800, 600, camera);
        viewport.update(800, 600);

        // Mock tile layer with 32x32 tiles
        TiledMapTileLayer layer = mock(TiledMapTileLayer.class);
        when(layer.getTileWidth()).thenReturn(32);
        when(layer.getTileHeight()).thenReturn(32);

        // Screen coordinates — for example, somewhere near the center of the screen
        float screenX = 400; // center X of screen
        float screenY = 300; // center Y of screen

        Vector2 tileCoords = WorldCoordinateUtils.calculateWorldCoordinate(
            viewport,
            layer,
            screenX,
            screenY
        );

        // Get expected world coordinate using same camera
        Vector3 world = viewport.unproject(new Vector3(screenX, screenY, 0));
        Vector2 expected = new Vector2(world.x / 32f, world.y / 32f);

        assertEquals(expected.x, tileCoords.x, 0.001f);
        assertEquals(expected.y, tileCoords.y, 0.001f);
    }
}
