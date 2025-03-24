package com.zhaw.frontier.entities;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.zhaw.frontier.components.CameraComponent;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Camera {
    /**
     *
     * @param engine
     * @return
     */
    public static Entity createCamera(Engine engine) {
        List<Component> components = new ArrayList<>();
        components.add(new CameraComponent());

        return EntityFactory.buildEntity(engine, components);
    }
}
