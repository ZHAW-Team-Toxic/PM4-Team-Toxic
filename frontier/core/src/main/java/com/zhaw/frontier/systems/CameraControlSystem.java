package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.zhaw.frontier.components.CameraComponent;

public class CameraControlSystem extends IteratingSystem implements InputProcessor {

    private ComponentMapper<CameraComponent> cm = ComponentMapper.getFor(CameraComponent.class);
    // Optionally, if you have UI actions in the same system:
    // private ComponentMapper<UIActionComponent> um = ComponentMapper.getFor(UIActionComponent.class);

    public CameraControlSystem() {
        // Process entities that have a CameraComponent (e.g., your camera entity)
        super(Family.all(CameraComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        // Update each camera entity based on input events processed via the InputProcessor
        super.update(deltaTime);
        // You might also update your UI actions or handle global state here.
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        CameraComponent cameraComp = cm.get(entity);
        OrthographicCamera camera = cameraComp.camera;

        // Example: You might update the camera's position and zoom if input state variables (set by keyDown/keyUp) are toggled.
        // For this example, assume we update the camera immediately in the input callbacks.
        // Always call update() on the camera when its properties change.
        camera.update();
    }

    @Override
    public boolean keyDown(int keycode) {
        // Use arrow keys (or WASD) for panning the camera
        // and page up/down (or +/- keys) for zooming in/out.
        for (Entity entity : getEngine().getEntitiesFor(Family.all(CameraComponent.class).get())) {
            CameraComponent cameraComp = cm.get(entity);
            OrthographicCamera camera = cameraComp.camera;

            switch (keycode) {
                case Input.Keys.RIGHT:
                    camera.position.x += cameraComp.moveSpeed * Gdx.graphics.getDeltaTime();
                    break;
                case Input.Keys.LEFT:
                    camera.position.x -= cameraComp.moveSpeed * Gdx.graphics.getDeltaTime();
                    break;
                case Input.Keys.UP:
                    camera.position.y += cameraComp.moveSpeed * Gdx.graphics.getDeltaTime();
                    break;
                case Input.Keys.DOWN:
                    camera.position.y -= cameraComp.moveSpeed * Gdx.graphics.getDeltaTime();
                    break;
                case Input.Keys.PLUS:  // Or any key you choose for zoom in
                    camera.zoom = Math.max(0.1f, camera.zoom - cameraComp.zoomSpeed);
                    break;
                case Input.Keys.MINUS: // Or any key you choose for zoom out
                    camera.zoom += cameraComp.zoomSpeed;
                    break;
                // For UI actions, you can set flags here if using a UIActionComponent.
                case Input.Keys.SPACE:
                    // For example, space might toggle a UI action like placing a tower.
                    // Retrieve a UIActionComponent from a dedicated UI entity and toggle its flag.
                    // entity.getComponent(UIActionComponent.class).placeTower = true;
                    Gdx.app.log("CameraControlSystem", "Space pressed: trigger UI action");
                    break;
                default:
                    break;
            }
            camera.update();
        }
        return true;
    }

    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}

