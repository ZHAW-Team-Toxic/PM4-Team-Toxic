package com.zhaw.frontier.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.GdxExtension;
import com.zhaw.frontier.components.AttackComponent;
import com.zhaw.frontier.components.CooldownComponent;
import com.zhaw.frontier.components.DeathComponent;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.TeamComponent;
import com.zhaw.frontier.components.behaviours.PathfindingBehaviourComponent;
import com.zhaw.frontier.enums.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxExtension.class)
public class EnemyAttackSystemTest {

    private Engine engine;
    private EnemyAttackSystem system;
    private HealthSystem healthSystem;

    @BeforeEach
    void setup() {
        engine = new Engine();
        system = new EnemyAttackSystem();
        healthSystem = new HealthSystem();
        engine.addSystem(system);
        engine.addSystem(healthSystem);
    }

    private Entity createAttacker(Vector2 position, float range, float damage) {
        Entity attacker = new Entity();
        attacker.add(new PositionComponent(position.x, position.y, 1, 1));
        attacker.add(new AttackComponent(damage, range, 500));
        attacker.add(new HealthComponent());
        attacker.add(new TeamComponent(Team.ENEMY));
        return attacker;
    }

    private Entity createTarget(Vector2 position, int health, Team team) {
        Entity target = new Entity();
        target.add(new PositionComponent(position.x, position.y, 1, 1));
        HealthComponent hc = new HealthComponent();
        hc.maxHealth = hc.currentHealth = health;
        target.add(hc);
        target.add(new TeamComponent(team));
        return target;
    }

    @Test
    void testAttackerDealsDamageInRange() {
        Entity attacker = createAttacker(new Vector2(1, 1), 2f, 5f);
        Entity target = createTarget(new Vector2(2, 1), 10, Team.PLAYER);

        engine.addEntity(attacker);
        engine.addEntity(target);

        engine.update(0.016f);

        HealthComponent hc = target.getComponent(HealthComponent.class);
        assertEquals(5f, hc.currentHealth, 0.001f);
    }

    @Test
    void testAttackerOnCooldownDoesNotAttack() {
        Entity attacker = createAttacker(new Vector2(1, 1), 2f, 5f);
        Entity target = createTarget(new Vector2(2, 1), 10, Team.PLAYER);

        attacker.add(new CooldownComponent());

        engine.addEntity(attacker);
        engine.addEntity(target);

        engine.update(0.016f);

        HealthComponent hc = target.getComponent(HealthComponent.class);
        assertEquals(10f, hc.currentHealth, 0.001f);
    }

    @Test
    void testTargetDiesAndPathResets() {
        Entity attacker = createAttacker(new Vector2(1, 1), 2f, 10f);
        Entity target = createTarget(new Vector2(2, 1), 10, Team.PLAYER);

        PathfindingBehaviourComponent path = new PathfindingBehaviourComponent(1f);
        path.destination = new Vector2(2, 1);
        path.targetEntity = target;
        attacker.add(path);

        engine.addEntity(attacker);
        engine.addEntity(target);

        for (int i = 0; i < 40; i++) engine.update(0.016f); // Ensure health system runs

        assertTrue(target.getComponent(HealthComponent.class).isDead);
        assertNotNull(target.getComponent(DeathComponent.class));
        assertNull(path.targetEntity);
        assertNull(path.destination);
    }

    @Test
    void testAttackerIgnoresFriendlyTarget() {
        Entity attacker = createAttacker(new Vector2(1, 1), 2f, 5f);
        Entity target = createTarget(new Vector2(2, 1), 10, Team.ENEMY);

        engine.addEntity(attacker);
        engine.addEntity(target);

        engine.update(0.016f);

        assertEquals(10f, target.getComponent(HealthComponent.class).currentHealth);
    }
}
