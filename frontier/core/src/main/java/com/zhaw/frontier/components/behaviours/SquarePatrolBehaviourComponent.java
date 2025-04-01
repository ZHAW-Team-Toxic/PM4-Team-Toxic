package com.zhaw.frontier.components.behaviours;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class SquarePatrolBehaviourComponent implements Component {
    public boolean initialized = false;
    public Vector2 origin = new Vector2();
    public float patrolSize = 30f;
    public float speed;
    public int direction = 0;

    public SquarePatrolBehaviourComponent(float speed) {
        this.speed = speed;
    }   
}
