package com.zhaw.frontier;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
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
        camera.setToOrtho(false, 400, 400);
        camera.update();
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
