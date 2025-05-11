package com.zhaw.frontier.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.zhaw.frontier.components.AnimationQueueComponent;
import com.zhaw.frontier.components.DeathComponent;
import com.zhaw.frontier.components.EnemyAnimationComponent;
import com.zhaw.frontier.components.EnemyAnimationComponent.EnemyAnimationType;
import com.zhaw.frontier.utils.QueueAnimation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeathSystemTest {

    private Engine engine;
    private DeathSystem deathSystem;

    @BeforeEach
    void setup() {
        engine = new Engine();
        deathSystem = new DeathSystem();
        engine.addSystem(deathSystem);
    }

    @Test
    void testEntityIsRemovedAfterDeathTimerExpires() {
        Entity entity = new Entity();
        entity.add(new DeathComponent(0.05f));
        entity.add(new AnimationQueueComponent());
        entity.add(new EnemyAnimationComponent());

        engine.addEntity(entity);

        engine.update(0.03f);
        assertTrue(engine.getEntities().contains(entity, true));

        engine.update(0.03f);
        assertFalse(engine.getEntities().contains(entity, true));
    }

    @Test
    void testDeathAnimationIsQueuedWithExpectedValues() {
        Entity entity = new Entity();
        AnimationQueueComponent queue = new AnimationQueueComponent();
        entity.add(new DeathComponent(1.0f));
        entity.add(queue);
        entity.add(new EnemyAnimationComponent());

        engine.addEntity(entity);
        engine.update(0.016f);

        assertEquals(1, queue.queue.size());
        QueueAnimation anim = queue.queue.peek();
        assertEquals(EnemyAnimationType.DEATH, anim.animationType);
        assertFalse(anim.loop);
        assertTrue(anim.timeLeft > 0, "timeLeft should be positive");
    }

    @Test
    void testNoCrashIfAnimationQueueComponentMissing() {
        Entity entity = new Entity();
        entity.add(new DeathComponent(1.0f));
        entity.add(new EnemyAnimationComponent());

        engine.addEntity(entity);
        engine.update(0.016f);

        assertTrue(engine.getEntities().contains(entity, true));
    }

    @Test
    void testAnimationIsNotQueuedMultipleTimes() {
        Entity entity = new Entity();
        AnimationQueueComponent queue = new AnimationQueueComponent();
        entity.add(new DeathComponent(1.0f));
        entity.add(queue);
        entity.add(new EnemyAnimationComponent());

        engine.addEntity(entity);
        engine.update(0.016f);
        engine.update(0.016f);

        assertEquals(1, queue.queue.size());
        QueueAnimation anim = queue.queue.peek();
        assertEquals(EnemyAnimationType.DEATH, anim.animationType);
    }
}
