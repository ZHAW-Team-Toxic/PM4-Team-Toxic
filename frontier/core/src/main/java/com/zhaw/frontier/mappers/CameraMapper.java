package com.zhaw.frontier.mappers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Family;
import com.zhaw.frontier.components.CameraComponent;

/**
 * Mapper for the camera. This class is used to map the camera entity.
 *
 */
public class CameraMapper {

    /**
     * .
     */
    public ComponentMapper<CameraComponent> cameraComponentMapper = ComponentMapper.getFor(CameraComponent.class);

    /**
     * Family which contains all components of the camera entity.
     */
    public Family cameraFamily = Family.all(CameraComponent.class).get();
}
