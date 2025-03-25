package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

/**
 * Contains the attack data of an {@link com.badlogic.ashley.core.Entity}
 * This includes the attack damage, attack speed and attack range.
 */
public class AttackComponent implements Component {

    /**
     * The attack damage of the entity.
     */
    public float AttackDamage = 0;

    /**
     * The attack speed of the entity.
     */
    public float AttackSpeed = 0;

    /**
     * The attack range of the entity.
     */
    public float AttackRange = 0;
}
