package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

/**
 * Contains the camera data of an {@link com.badlogic.ashley.core.Entity}
 * This includes the position, border, zoom and target of the camera.
 */
public class CameraComponent implements Component {

    /**
     * The position of the camera.
     */
    public Vector3 position = new Vector3();
    /**
     * The border of the camera.
     */
    public Vector3 border = new Vector3();
    /**
     * The zoom of the camera.
     */
    public float zoom = 1.0f;
    /**
     * The target of the camera.
     */
    public Vector3 target = new Vector3();
}
