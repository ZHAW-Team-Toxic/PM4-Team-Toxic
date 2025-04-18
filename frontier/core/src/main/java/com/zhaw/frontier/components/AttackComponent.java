package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

/**
 * Contains the attack data of an {@link com.badlogic.ashley.core.Entity}
 * This includes the attack damage, attack speed and attack range.
 */
public class AttackComponent implements Component {

    public float damage = 10f;
    public float attackRange = 1.5f; // in tiles
    public float attackInterval = 1f; // Seconds between attacks
    public float attackCooldown = 0f; // Internal timer

    public AttackComponent() {}

    public AttackComponent(float damage, float attackRange, float attackInterval) {
        this.damage = damage;
        this.attackRange = attackRange;
        this.attackInterval = attackInterval;
        this.attackCooldown = 0f;
    }
}
