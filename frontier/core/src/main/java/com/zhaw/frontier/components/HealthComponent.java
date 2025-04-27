package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

/**
 * Contains the health of an {@link com.badlogic.ashley.core.Entity}
 */
public class HealthComponent implements Component {

    /**
     * $param Health The health of the entity.
     */
    public int maxHealth = 100;

    /**
     * $param Health The current health of the entity.
     */
    public int currentHealth = 100;
}
