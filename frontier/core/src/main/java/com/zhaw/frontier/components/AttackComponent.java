package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

/**
 * Component that defines the attack properties of an {@link com.badlogic.ashley.core.Entity}.
 *
 * <p>This includes:</p>
 * <ul>
 *   <li>Attack damage (how much damage is dealt per hit)</li>
 *   <li>Attack range (in tiles)</li>
 *   <li>Attack interval (delay between attacks, in milliseconds)</li>
 *   <li>Internal cooldown timer for managing when the next attack can occur</li>
 * </ul>
 */
public class AttackComponent implements Component {

    /** The amount of damage dealt by each attack. */
    public float damage = 10f;

    /** The attack range in tiles (i.e., how far the entity can hit). */
    public float attackRange = 1.5f;

    /** The time in milliseconds between consecutive attacks. */
    public float attackInterval = 1000f;

    /** Internal cooldown timer used to track when the next attack is allowed. */
    public float attackCooldown = 0f;

    /**
     * Constructs an {@code AttackComponent} with default values.
     */
    public AttackComponent() {}

    /**
     * Constructs an {@code AttackComponent} with custom values.
     *
     * @param damage         the amount of damage per attack
     * @param attackRange    the range of the attack in tiles
     * @param attackInterval the interval between attacks in milliseconds
     */
    public AttackComponent(float damage, float attackRange, float attackInterval) {
        this.damage = damage;
        this.attackRange = attackRange;
        this.attackInterval = attackInterval;
        this.attackCooldown = 0f;
    }
}
