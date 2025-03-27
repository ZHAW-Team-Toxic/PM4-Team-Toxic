package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.zhaw.frontier.components.CameraComponent;

/**
 * A factory class responsible for creating and initializing an Entity
 * that represents the game camera.
 */
public class CameraFactory {

    /**
     * Creates a new camera entity with a {@link CameraComponent} and adds it to the engine.
     *
     * @param engine the {@link Engine} to which the new camera entity will be added.
     * @return the newly created camera entity.
     */
    public static Entity createCamera(Engine engine) {
        Entity camera = engine.createEntity();
        camera.add(new CameraComponent());

        return camera;
    }
}
