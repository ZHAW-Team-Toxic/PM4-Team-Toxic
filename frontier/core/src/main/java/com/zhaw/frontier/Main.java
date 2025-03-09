package com.zhaw.frontier;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    // Adjust this speed to control camera movement
    private final float CAMERA_SPEED = 200f;

    @Override
    public void create() {
        // Load the Tiled map
        map = new TmxMapLoader().load("test_1_path_enemy_two_towers.tmx");
        // Create a map renderer for the loaded map
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        // Set up the camera with a viewport (adjust dimensions as needed)
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        camera.update();

        // Set up an input processor to handle left mouse clicks
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    // Log the raw screen coordinates
                    Gdx.app.log("Input", "Left click at screen coordinates (" + screenX + ", " + screenY + ")");

                    // Convert the screen coordinates to world coordinates
                    Vector3 worldCoords = new Vector3(screenX, screenY, 0);
                    camera.unproject(worldCoords);
                    Gdx.app.log("Input", "Converted to world coordinates (" + worldCoords.x + ", " + worldCoords.y + ")");

                    // Calculate tile coordinates based on the first encountered tile layer's dimensions
                    // (Assumes all tile layers have the same tile width and height)
                    TiledMapTileLayer sampleLayer = null;
                    for (int i = 0; i < map.getLayers().getCount(); i++) {
                        if (map.getLayers().get(i) instanceof TiledMapTileLayer) {
                            sampleLayer = (TiledMapTileLayer) map.getLayers().get(i);
                            break;
                        }
                    }
                    if (sampleLayer == null) {
                        Gdx.app.log("Tile Info", "No TiledMapTileLayer found.");
                        return true;
                    }

                    int tileX = (int) (worldCoords.x / sampleLayer.getTileWidth());
                    int tileY = (int) (worldCoords.y / sampleLayer.getTileHeight());
                    Gdx.app.log("Tile Info", "Tile coordinates (" + tileX + ", " + tileY + ")");

                    // Iterate layers in reverse order to prioritize the top-most layer with a valid tile
                    TiledMapTileLayer.Cell cellFound = null;
                    String layerName = "";
                    for (int i = map.getLayers().getCount() - 1; i >= 0; i--) {
                        if (map.getLayers().get(i) instanceof TiledMapTileLayer) {
                            TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(i);
                            TiledMapTileLayer.Cell cell = layer.getCell(tileX, tileY);
                            if (cell != null && cell.getTile() != null) {
                                cellFound = cell;
                                layerName = layer.getName();
                                break;
                            }
                        }
                    }

                    if (cellFound != null) {
                        Gdx.app.log("Tile Info", "Clicked tile found on layer: " + layerName + " with Tile ID: " + cellFound.getTile().getId());
                    } else {
                        Gdx.app.log("Tile Info", "No tile found at the clicked position on any layer.");
                    }
                }
                return true;
            }
        });

    }

    @Override
    public void render() {
        // Clear the screen
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Update camera movement with WASD keys
        float delta = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.position.y += CAMERA_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.position.y -= CAMERA_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.position.x -= CAMERA_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.position.x += CAMERA_SPEED * delta;
        }
        camera.update();

        // Set the view for the map renderer and render the map
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    @Override
    public void dispose() {
        // Dispose of assets
        map.dispose();
        mapRenderer.dispose();
    }
}
