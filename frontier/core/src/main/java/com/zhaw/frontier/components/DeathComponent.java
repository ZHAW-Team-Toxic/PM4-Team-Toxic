package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

public class DeathComponent implements Component {

    public float timeUntilRemoval;

    public DeathComponent(float timeUntilRemoval) {
        this.timeUntilRemoval = timeUntilRemoval;
    }
}
