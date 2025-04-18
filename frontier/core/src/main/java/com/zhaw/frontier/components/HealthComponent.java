package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

/**
 * Contains the health of an {@link com.badlogic.ashley.core.Entity}
 */
public class HealthComponent implements Component {

    public int currentHealth = 100;
    public int maxHealth = 100;
    public boolean isDead = false;
}
