package com.zhaw.frontier;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Objects;

public class Main extends ApplicationAdapter {
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private final float CAMERA_SPEED = 200f;

    // For highlighting the building tile under the mouse
    private int selectedTileX = -1, selectedTileY = -1;
    // A simple white pixel texture for drawing the overlay
    private Texture whitePixel;

    @Override
    public void create() {
        // Load the Tiled map
        map = new TmxMapLoader().load("test_1_path_enemy_two_towers.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        // Set up the camera (adjust viewport dimensions as needed)
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        camera.update();

        // Create a 1x1 white texture for the highlight overlay
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();

        // Set up an input processor that handles both mouse movement and left-clicks
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                // Convert the mouse position from screen to world coordinates
                Vector3 worldCoords = new Vector3(screenX, screenY, 0);
                camera.unproject(worldCoords);
                Gdx.app.log("Mouse", "Mouse moved to world coordinates (" + worldCoords.x + ", " + worldCoords.y + ")");

                // Use a sample tile layer to get tile dimensions.
                // (Assumes that all tile layers share the same tile width and height.)
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

                // Check only the building layer (assumed to be named "Buildings")
                boolean foundBuilding = false;
                TiledMapTileLayer buildingLayer = null;
                for (int i = 0; i < map.getLayers().getCount(); i++) {
                    if (map.getLayers().get(i) instanceof TiledMapTileLayer) {
                        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(i);
                        if(Objects.nonNull(layer)){
                            Gdx.app.log("Tile Info", "Checking building layer for tile presence: + " + layer.getName());
                        }

                        if ("TowerLayer".equals(layer.getName())) {
                            buildingLayer = layer;
                            TiledMapTileLayer.Cell cell = buildingLayer.getCell(tileX, tileY);
                            if (cell != null && cell.getTile() != null) {
                                foundBuilding = true;
                                Gdx.app.log("Tile Info", "" + foundBuilding);
                            }
                            break;
                        }
                    }
                }

                if (foundBuilding) {
                    selectedTileX = tileX;
                    selectedTileY = tileY;
                    Gdx.app.log("Tile Info", selectedTileX + ", " + selectedTileY);
                } else {
                    selectedTileX = -1;
                    selectedTileY = -1;
                }
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    Vector3 worldCoords = new Vector3(screenX, screenY, 0);
                    camera.unproject(worldCoords);
                    Gdx.app.log("Input", "Left click at world coordinates (" + worldCoords.x + ", " + worldCoords.y + ")");
                }
                return true;
            }
        });
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Update camera movement via WASD keys
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

        // Render the map
        mapRenderer.setView(camera);
        mapRenderer.render();

        // Draw a white, semi-transparent overlay on the selected building tile
        if (selectedTileX >= 0 && selectedTileY >= 0) {
            // Use a sample layer for tile dimensions (assuming all layers have the same dimensions)
            TiledMapTileLayer sampleLayer = null;
            for (int i = 0; i < map.getLayers().getCount(); i++) {
                if (map.getLayers().get(i) instanceof TiledMapTileLayer) {
                    sampleLayer = (TiledMapTileLayer) map.getLayers().get(i);
                    break;
                }
            }
            if (sampleLayer != null) {
                float tileWidth = sampleLayer.getTileWidth();
                float tileHeight = sampleLayer.getTileHeight();
                float worldX = selectedTileX * tileWidth;
                float worldY = selectedTileY * tileHeight;

                // Use the map renderer's batch to draw the overlay
                mapRenderer.getBatch().begin();
                // Set the batch color to white with a 50% alpha (making it "a bit white")
                mapRenderer.getBatch().setColor(1, 1, 1, 0.5f);
                mapRenderer.getBatch().draw(whitePixel, worldX, worldY, tileWidth, tileHeight);
                // Reset the batch color to opaque white
                mapRenderer.getBatch().setColor(1, 1, 1, 1);
                mapRenderer.getBatch().end();
            }
        }
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        whitePixel.dispose();
    }
}
