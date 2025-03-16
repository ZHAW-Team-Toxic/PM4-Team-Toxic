package com.zhaw.frontier.mappers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Family;
import com.zhaw.frontier.components.BorderComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.components.ZoomComponent;

/**
 * Mapper for the camera. This class is used to map the camera entity.
 *
 */
public class CameraMapper {

    /**
     * Component mappers which mao the position of the camera entity.
     */
    public ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(
        PositionComponent.class
    );
    /**
     * Component mappers which mao the border of the camera entity.
     */
    public ComponentMapper<BorderComponent> borderMapper = ComponentMapper.getFor(
        BorderComponent.class
    );
    /**
     * Component mappers which mao the velocity of the camera entity.
     */
    public ComponentMapper<VelocityComponent> velocityMapper = ComponentMapper.getFor(
        VelocityComponent.class
    );
    /**
     * Component mappers which mao the zoom of the camera entity.
     */
    public ComponentMapper<ZoomComponent> zoomMapper = ComponentMapper.getFor(ZoomComponent.class);
    /**
     * Family which contains all components of the camera entity.
     */
    public Family cameraFamily = Family
        .all(
            PositionComponent.class,
            BorderComponent.class,
            VelocityComponent.class,
            ZoomComponent.class
        )
        .get();
}
