package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraComponent implements Component {
    public OrthographicCamera camera;
    public float moveSpeed = 10.0f; // pixels per second
    public float zoomSpeed = 0.05f;  // zoom change per input event

    public CameraComponent(OrthographicCamera camera) {
        this.camera = camera;
    }
}
