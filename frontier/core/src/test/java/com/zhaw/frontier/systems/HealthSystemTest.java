package com.zhaw.frontier.systems;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.zhaw.frontier.GdxExtension;
import com.zhaw.frontier.components.DeathComponent;
import com.zhaw.frontier.components.HealthComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxExtension.class)
public class HealthSystemTest {

    private Engine engine;
    private HealthSystem system;

    @BeforeEach
    void setup() {
        engine = new Engine();
        system = new HealthSystem();
        engine.addSystem(system);
    }

    private Entity createEntityWithHealth(int current, int max) {
        Entity e = new Entity();
        HealthComponent hc = new HealthComponent();
        hc.currentHealth = current;
        hc.maxHealth = max;
        e.add(hc);
        return e;
    }

    @Test
    void testEntityWithPositiveHealthDoesNotDie() {
        Entity entity = createEntityWithHealth(5, 10);
        engine.addEntity(entity);

        engine.update(0.5f);

        assertFalse(entity.getComponent(HealthComponent.class).isDead);
        assertNull(entity.getComponent(DeathComponent.class));
    }

    @Test
    void testEntityWithZeroHealthIsMarkedDeadAndGetsDeathComponent() {
        Entity entity = createEntityWithHealth(0, 10);
        engine.addEntity(entity);

        engine.update(0.5f);

        assertTrue(entity.getComponent(HealthComponent.class).isDead);
        assertNotNull(entity.getComponent(DeathComponent.class));
    }

    @Test
    void testEntityWithNegativeHealthIsMarkedDead() {
        Entity entity = createEntityWithHealth(-5, 10);
        engine.addEntity(entity);

        engine.update(0.5f);

        assertTrue(entity.getComponent(HealthComponent.class).isDead);
        assertNotNull(entity.getComponent(DeathComponent.class));
    }

    @Test
    void testEntityWithZeroHealthOnlyGetsDeathOnce() {
        Entity entity = createEntityWithHealth(0, 10);
        engine.addEntity(entity);

        engine.update(0.5f);
        DeathComponent deathComponent = entity.getComponent(DeathComponent.class);
        engine.update(0.5f);

        assertSame(deathComponent, entity.getComponent(DeathComponent.class));
    }
}
