package com.zhaw.frontier.mappers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Family;
import com.zhaw.frontier.components.CameraComponent;

/**
 * Mapper for the camera component.
 * <p>
 * This class provides easy access to the {@link CameraComponent} for entities and
 * defines a {@link Family} that consists of all entities having a {@link CameraComponent}.
 * </p>
 */
public class CameraMapper {

    /**
     * The component mapper for the {@link CameraComponent}.
     * <p>
     * This mapper allows for quick retrieval of the {@link CameraComponent} from an entity.
     * </p>
     */
    public ComponentMapper<CameraComponent> cameraComponentMapper = ComponentMapper.getFor(
        CameraComponent.class
    );

    /**
     * The family of entities containing the {@link CameraComponent}.
     * <p>
     * This family can be used to iterate over all entities that possess a {@link CameraComponent}.
     * </p>
     */
    public Family cameraFamily = Family.all(CameraComponent.class).get();
}
