package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

public class ConditionalAnimationComponent implements Component {

    public Enum<?> animationType; // supports Enemy or Building
    public float timeLeft;
    public boolean loop;
}
