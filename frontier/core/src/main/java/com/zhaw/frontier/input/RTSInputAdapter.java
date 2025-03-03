package com.zhaw.frontier.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class RTSInputAdapter extends InputAdapter {
    private final ExtendViewport viewport;
    private final Camera camera;
    // Used to store the world coordinates of the touch points
    private final Vector3 lastTouch = new Vector3();
    private final Vector3 currentTouch = new Vector3();
    // The target position the camera should move toward
    private final Vector3 cameraTarget;
    // Adjust this factor (0 - 1) for smoothing speed (lower is slower/smoother)
    private final float lerpFactor = 0.2f;

    public RTSInputAdapter(ExtendViewport viewport) {
        this.viewport = viewport;
        this.camera = viewport.getCamera();
        // Initialize the target to the camera's starting position
        this.cameraTarget = new Vector3(camera.position);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            // Convert screen coordinates to world coordinates
            viewport.unproject(lastTouch.set(screenX, screenY, 0));
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Ensure the right mouse button is pressed
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            // Convert the current screen coordinates to world coordinates
            viewport.unproject(currentTouch.set(screenX, screenY, 0));
            // Calculate the delta (difference) between the last and current touch positions
            float deltaX = lastTouch.x - currentTouch.x;
            float deltaY = lastTouch.y - currentTouch.y;
            // Update the target position by adding the delta
            cameraTarget.add(deltaX, deltaY, 0);
            // Update the last touch position for the next frame
            lastTouch.set(currentTouch);
            return true;
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        float zoomSpeed = 0.1f;
        var camera = (OrthographicCamera) viewport.getCamera();
        camera.zoom += amountY * zoomSpeed;
        camera.zoom = MathUtils.clamp(camera.zoom, 0.5f, 2.0f);
        camera.update();
        return true;
    }

    // Call this method in your render loop to smoothly move the camera
    public void update() {
        // Smoothly interpolate the camera's position towards the target position
        camera.position.lerp(cameraTarget, lerpFactor);
        camera.update();
    }
}
