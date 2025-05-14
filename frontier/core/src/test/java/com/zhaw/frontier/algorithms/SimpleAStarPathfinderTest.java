package com.zhaw.frontier.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.algorithm.SimpleAStarPathfinder;
import com.zhaw.frontier.components.map.TiledPropertiesEnum;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SimpleAStarPathfinderTest {

    private TiledMapTileLayer mockLayerWithTraversability(
        int width,
        int height,
        boolean[][] traversable
    ) {
        TiledMapTileLayer layer = mock(TiledMapTileLayer.class);
        when(layer.getWidth()).thenReturn(width);
        when(layer.getHeight()).thenReturn(height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TiledMapTileLayer.Cell cell = mock(TiledMapTileLayer.Cell.class);
                TiledMapTile tile = mock(TiledMapTile.class);
                MapProperties props = new MapProperties();
                props.put(TiledPropertiesEnum.IS_TRAVERSABLE.toString(), traversable[x][y]);
                when(tile.getProperties()).thenReturn(props);
                when(cell.getTile()).thenReturn(tile);
                when(layer.getCell(x, y)).thenReturn(cell);
            }
        }

        return layer;
    }

    @Test
    void testPathExists() {
        boolean[][] map = new boolean[5][5];
        for (int x = 0; x < 5; x++) for (int y = 0; y < 5; y++) map[x][y] = true;

        TiledMapTileLayer layer = mockLayerWithTraversability(5, 5, map);
        var pathfinder = new SimpleAStarPathfinder(List.of(layer), null);
        var path = pathfinder.findPath(new Vector2(0, 0), new Vector2(4, 4));

        assertFalse(path.isEmpty());
        assertEquals(new Vector2(4, 4), path.peek());
    }

    @Test
    void testGoalBlockedReturnsClosestReachable() {
        boolean[][] map = new boolean[5][5];
        for (int x = 0; x < 5; x++) for (int y = 0; y < 5; y++) map[x][y] = true;

        map[4][4] = false; // Goal is not walkable

        TiledMapTileLayer layer = mockLayerWithTraversability(5, 5, map);
        var pathfinder = new SimpleAStarPathfinder(List.of(layer), null);
        var path = pathfinder.findPath(new Vector2(0, 0), new Vector2(4, 4));

        assertFalse(path.isEmpty());
        assertNotEquals(new Vector2(4, 4), path.peek()); // Should be closest reachable, not goal
    }

    @Test
    void testStartEqualsGoalReturnsEmptyPath() {
        boolean[][] map = new boolean[3][3];
        for (int x = 0; x < 3; x++) for (int y = 0; y < 3; y++) map[x][y] = true;

        TiledMapTileLayer layer = mockLayerWithTraversability(3, 3, map);
        var pathfinder = new SimpleAStarPathfinder(List.of(layer), null);
        var path = pathfinder.findPath(new Vector2(1, 1), new Vector2(1, 1));

        assertTrue(path.isEmpty()); // Already at goal
    }

    @Test
    void testFullyBlockedMapReturnsEmpty() {
        boolean[][] map = new boolean[3][3];
        for (int x = 0; x < 3; x++) for (int y = 0; y < 3; y++) map[x][y] = false; // Everything blocked

        TiledMapTileLayer layer = mockLayerWithTraversability(3, 3, map);
        var pathfinder = new SimpleAStarPathfinder(List.of(layer), null);
        var path = pathfinder.findPath(new Vector2(0, 0), new Vector2(2, 2));

        assertTrue(path.isEmpty());
    }
}
