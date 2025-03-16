package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class CameraComponent implements Component {

    public OrthographicCamera camera;
    public float positionX = 0.0f;
    public float positionY = 0.0f;
    public float zoom = 80.0f;
    public float zoomSpeed = 0.1f;
    public Vector2 velocity = new Vector2();
    public float borderX = 0.0f;
    public float borderY = 0.0f;

    public CameraComponent(OrthographicCamera camera) {
        this.camera = camera;
    }
}
