package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

/**
 * Tiis detects collision with a collision component. The damage will be applied
 * to the target on collision.
 */

public class ProjectileComponent implements Component {

    public int damage = 10;
}
