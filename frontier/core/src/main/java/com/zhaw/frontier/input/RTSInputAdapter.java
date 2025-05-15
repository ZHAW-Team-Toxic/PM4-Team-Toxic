package com.zhaw.frontier.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * An input adapter for real-time strategy (RTS) style camera controls.
 * <p>
 * This adapter processes touch and scroll input to allow for camera panning and zooming.
 * It smooths out camera movements using linear interpolation (lerp).
 * </p>
 */
public class RTSInputAdapter extends InputAdapter {

    private final ExtendViewport viewport;
    private final OrthographicCamera camera;
    private final Vector3 lastTouch = new Vector3();
    private final Vector3 currentTouch = new Vector3();
    private final Vector3 cameraTarget;
    /**
     * The interpolation factor for camera position smoothing.
     * A lower value results in slower, smoother transitions.
     */
    private final float lerpFactor = 0.2f;

    /**
     * Constructs a new RTSInputAdapter with the specified viewport.
     *
     * @param viewport the {@link ExtendViewport} used for coordinate conversion and camera management.
     */
    public RTSInputAdapter(ExtendViewport viewport) {
        this.viewport = viewport;
        this.camera = (OrthographicCamera) viewport.getCamera() ;
        // Initialize the camera target to the camera's starting position.
        this.cameraTarget = new Vector3(camera.position);
    }

    /**
     * Called when a touch/click is first detected.
     * <p>
     * If the left mouse button is pressed, the screen coordinates are converted
     * to world coordinates and stored as the last touch position.
     * </p>
     *
     * @param screenX the x-coordinate on the screen.
     * @param screenY the y-coordinate on the screen.
     * @param pointer the pointer for the event.
     * @param button  the mouse button that was pressed.
     * @return true if the event was handled, false otherwise.
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            // Convert screen coordinates to world coordinates.
            viewport.unproject(lastTouch.set(screenX, screenY, 0));
            return true;
        }
        return false;
    }

    /**
     * Called when a touch/click is dragged across the screen.
     * <p>
     * If the left mouse button is held, the current touch position is converted to world coordinates.
     * The difference between the last and current positions is used to update the camera target,
     * effectively panning the camera.
     * </p>
     *
     * @param screenX the current x-coordinate on the screen.
     * @param screenY the current y-coordinate on the screen.
     * @param pointer the pointer for the event.
     * @return true if the event was handled, false otherwise.
     */
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Ensure the left mouse button is still pressed.
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            // Convert the current screen coordinates to world coordinates.
            viewport.unproject(currentTouch.set(screenX, screenY, 0));
            // Calculate the delta between the last and current touch positions.
            float deltaX = lastTouch.x - currentTouch.x;
            float deltaY = lastTouch.y - currentTouch.y;
            // Update the camera target by adding the delta.

            cameraTarget.add(deltaX, deltaY, 0);
            this.clampCamera(camera, cameraTarget);
            lastTouch.set(currentTouch);
            return true;
        }
        return false;
    }

    /**
     * Called when the user scrolls the mouse wheel.
     * <p>
     * This method adjusts the camera's zoom level by interpolating toward a target zoom value.
     * The zoom is clamped to a specified range to prevent excessive zooming.
     * </p>
     *
     * @param amountX the horizontal scroll amount.
     * @param amountY the vertical scroll amount.
     * @return true if the event was handled, false otherwise.
     */
    @Override
    public boolean scrolled(float amountX, float amountY) {
        float zoomSpeed = 9.0f;
        float smoothingFactor = 0.2f;
        OrthographicCamera camera = (OrthographicCamera) viewport.getCamera();

        // Calculate the target zoom level.
        float targetZoom = camera.zoom + amountY * zoomSpeed;
        // Clamp the target zoom to a defined range.
        targetZoom = MathUtils.clamp(targetZoom, 30f, 60f);
        // Interpolate the camera's zoom toward the target zoom.
        camera.zoom = MathUtils.lerp(camera.zoom, targetZoom, smoothingFactor);
        this.clampCamera(camera, cameraTarget);
        this.clampCamera(camera, camera.position);
        camera.update();
        return true;
    }

    private void clampCamera(OrthographicCamera camera, Vector3 target) {
        float width = camera.viewportWidth * camera.zoom;
        float height = camera.viewportHeight * camera.zoom;
        float w = width * Math.abs(camera.up.y) + height * Math.abs(camera.up.x);
        float h = height * Math.abs(camera.up.y) + width * Math.abs(camera.up.x);

        float minX = w / 2;
        float minY = h / 2;
        float mapWidth = 32 * 32;
        float mapHeight = 32 * 32;
        float maxX = mapWidth - (w / 2);
        float maxY = mapHeight - (h / 2);
        target.x = MathUtils.clamp(cameraTarget.x, minX, maxX);
        target.y = MathUtils.clamp(cameraTarget.y, minY, maxY);
    }

    /**
     * Updates the camera's position smoothly towards the target position.
     * <p>
     * This method should be called within the render loop to ensure smooth camera movement.
     * </p>
     */
    public void update() {
        // Smoothly interpolate the camera's position towards the target position.
        camera.position.lerp(cameraTarget, lerpFactor);
        camera.update();
    }
}
