package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

/**
 * Contains zoom data for the camera.
 */
public class ZoomComponent implements Component {

    /**
     * The zoom level of the camera.
     */
    public float zoom = 40.0f;
    /**
     * The speed at which the camera zooms in and out.
     */
    public float zoomSpeed = 2.0f;
}
