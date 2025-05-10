package com.zhaw.frontier.enums;

/**
 * Enum representing different types of enemies in the game.
 * Each enemy type has a name, health, attack damage, and speed.
 */
public enum EnemyType {
    ORC("orc", 100, 10, 2f),
    DEMON("demon", 200, 20, 1.5f),
    GOBLIN("goblin", 70, 15, 3.5f);

    private final String typeName;
    private final int health;
    private final float attackDamage;
    private final float speed;

    /**
     * Constructor for EnemyType enum.
     *
     * @param typeName The name of the enemy type.
     * @param health The health of the enemy.
     * @param attackDamage The attack damage of the enemy.
     * @param speed The speed of the enemy.
     */
    EnemyType(String typeName, int health, float attackDamage, float speed) {
        this.typeName = typeName;
        this.health = health;
        this.attackDamage = attackDamage;
        this.speed = speed;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getHealth() {
        return health;
    }

    public float getAttackDamage() {
        return attackDamage;
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return typeName;
    }
}
