package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.entityFactories.CameraFactory;
import com.zhaw.frontier.input.RTSInputAdapter;
import com.zhaw.frontier.mappers.CameraMapper;
import lombok.Getter;

/**
 * System for controlling the camera.
 * <p>
 * This system is responsible for updating the camera and the input adapter. The camera is updated based on input events
 * processed via the {@link RTSInputAdapter}. In future versions, the camera may be controlled through ECS components.
 * </p>
 */
public class CameraControlSystem extends IteratingSystem {

    private final Viewport viewport;
    private final Engine engine;
    private final OrthogonalTiledMapRenderer renderer;

    @Getter
    private OrthographicCamera camera;

    @Getter
    private final RTSInputAdapter inputAdapter;

    private final CameraMapper cameraMapper = new CameraMapper();

    /**
     * Constructs a new CameraControlSystem.
     *
     * @param viewport the {@link ExtendViewport} to be used for rendering.
     * @param engine   the {@link Engine} used for managing entities and systems.
     * @param renderer the {@link OrthogonalTiledMapRenderer} used for rendering tile maps.
     */
    public CameraControlSystem(
        ExtendViewport viewport,
        Engine engine,
        OrthogonalTiledMapRenderer renderer
    ) {
        super(Family.all().get());
        this.viewport = viewport;
        this.engine = engine;
        this.renderer = renderer;
        setUpGameCamera(viewport);
        inputAdapter = new RTSInputAdapter(viewport);
    }

    /**
     * Updates the camera and input adapter each frame.
     * <p>
     * This method calls the super update method to process any entities (currently none), sets the renderer view to the camera,
     * applies the viewport, and updates the input adapter.
     * </p>
     *
     * @param deltaTime the time elapsed since the last frame, in seconds.
     */
    @Override
    public void update(float deltaTime) {
        // Process entities (if any) before performing camera update operations.
        super.update(deltaTime);
        renderer.setView(camera);
        viewport.apply();
        inputAdapter.update();
    }

    /**
     * Processes each camera entity.
     * <p>
     * Currently, this method is not used because the camera is not updated per entity.
     * In the future, the {@link RTSInputAdapter} might utilize a camera component to update individual camera entities.
     * </p>
     *
     * @param entity    the current entity being processed.
     * @param deltaTime the time elapsed since the last frame, in seconds.
     */
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // TODO: RTSInputAdapter should use camera component for processing individual entities.
    }

    /**
     * Sets up the game camera.
     * <p>
     * This method initializes a new {@link OrthographicCamera}, sets its position and zoom,
     * updates the camera, and assigns it to the provided viewport. Additionally, it creates a camera entity using the
     * {@link CameraFactory} and adds it to the engine.
     * </p>
     *
     * @param viewport the {@link Viewport} that will use the configured camera.
     */
    private void setUpGameCamera(Viewport viewport) {
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        // Set the camera's initial position (example: centered at 32*16, 32*16)
        camera.position.set(32 * 16, 32 * 16, 0);
        // Set the initial zoom level of the camera.
        this.camera.zoom = 40.0f;
        // TODO: Configure camera properties through components in the future.
        this.camera.update();
        viewport.setCamera(camera);
        // Create a camera entity and add it to the engine.
        Entity cameraEntity = CameraFactory.createCamera(engine);
        engine.addEntity(cameraEntity);
    }
}
