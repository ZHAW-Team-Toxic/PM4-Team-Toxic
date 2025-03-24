package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

public class CameraComponent implements Component {

    public Vector3 position = new Vector3();
    public Vector3 border = new Vector3();
    public float zoom = 1.0f;
    public Vector3 target = new Vector3();
}
