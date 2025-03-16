package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.BorderComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.components.ZoomComponent;
import com.zhaw.frontier.entities.Camera;
import com.zhaw.frontier.mappers.CameraMapper;
import com.zhaw.frontier.subsystems.RTSInputAdapter;
import lombok.Getter;

/**
 * System for controlling the camera. This system is responsible for updating the camera and the input adapter.
 * The camera is updated based on input events processed via the @RTSInputAdapter.
 */
public class CameraControlSystem extends IteratingSystem {

    private final Viewport viewport;
    private final Engine engine;
    private final OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    @Getter
    private final RTSInputAdapter inputAdapter;

    private final CameraMapper cameraMapper = new CameraMapper();

    /**
     * Constructor. Initializes the camera control system.
     * @param viewport The viewport to be used.
     * @param engine The engine to be used.
     * @param renderer The renderer to be used.
     */
    public CameraControlSystem(
        ExtendViewport viewport,
        Engine engine,
        OrthogonalTiledMapRenderer renderer
    ) {
        super(Family.all(ZoomComponent.class).get());
        this.viewport = viewport;
        this.engine = engine;
        this.renderer = renderer;
        setUpGameCamera(viewport);
        inputAdapter = new RTSInputAdapter(viewport);
    }

    /**
     * Updates the camera and the input adapter.
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        // Update each camera entity based on input events processed via the InputProcessor
        super.update(deltaTime);
        renderer.setView(camera);
        viewport.apply();
        inputAdapter.update();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        //todo probably needed for changing settings of camera during runtime
    }

    private void setUpGameCamera(Viewport viewport) {
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false);
        this.camera.zoom = 40.0f;
        //TODO needs to be done through components in the future
        this.camera.update();
        viewport.setCamera(camera);

        Camera cameraEntity = new Camera();
        cameraEntity.add(new ZoomComponent()).add(new VelocityComponent()).add(new PositionComponent()).add(
            new BorderComponent());
        engine.addEntity(cameraEntity);
    }
}
