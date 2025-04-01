package com.zhaw.frontier.components.behaviours;

import com.badlogic.ashley.core.Component;

public class PatrolBehaviourComponent implements Component {

    public float leftBound;
    public float rightBound;
    public float speed;
    public boolean movingRight = true;
    public boolean initialized = false;

    public PatrolBehaviourComponent(float speed) {
        this.speed = speed;
    }
}
