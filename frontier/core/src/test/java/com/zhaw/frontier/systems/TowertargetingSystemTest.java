package com.zhaw.frontier.systems;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.GdxExtension;
import com.zhaw.frontier.components.AttackComponent;
import com.zhaw.frontier.components.CircleCollisionComponent;
import com.zhaw.frontier.components.CurrentTargetComponent;
import com.zhaw.frontier.components.EnemyComponent;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.TowerAnimationComponent;
import com.zhaw.frontier.components.TowerComponent;
import com.zhaw.frontier.components.VelocityComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * TowertargetingSystem
 */
@ExtendWith(GdxExtension.class)
public class TowertargetingSystemTest {

    private TowerTargetingSystem towertargetingSystem;
    private Engine engine;
    private final float RANGE = 10;

    @BeforeEach
    public void setup() {
        engine = new Engine();
        towertargetingSystem = new TowerTargetingSystem();
        engine.addSystem(towertargetingSystem);
    }

    private Entity createEnemy(int x, int y) {
        var entity = new Entity();

        var hit = new CircleCollisionComponent();
        var pos = new PositionComponent();
        var vel = new VelocityComponent();
        var health = new HealthComponent();
        var enemy = new EnemyComponent();
        pos.basePosition = new Vector2(x, y);

        entity.add(hit);
        entity.add(pos);
        entity.add(vel);
        entity.add(health);
        entity.add(enemy);
        return entity;
    }

    private Entity createTower(int x, int y) {
        var entity = new Entity();
        var anim = new TowerAnimationComponent();
        var towerComponent = new TowerComponent();
        var attackComponent = new AttackComponent();
        attackComponent.AttackRange = RANGE;
        var position = new PositionComponent();
        position.basePosition = new Vector2(x, y);

        entity.add(anim);
        entity.add(towerComponent);
        entity.add(attackComponent);
        entity.add(position);
        return entity;
    }

    @Test
    public void testTargetWhenInRange() {
        var tower = createTower(0, 0);
        var enemy = createEnemy(0, (int) RANGE / 2);
        engine.addEntity(tower);
        engine.addEntity(enemy);
        engine.update(.1f);
        var target = tower.getComponent(CurrentTargetComponent.class);
        assertNotNull(target);
    }

    @Test
    public void testNoTargetWhenOutOfRange() {
        var tower = createTower(0, 0);
        var enemy = createEnemy(0, (int) RANGE + 1);
        engine.addEntity(tower);
        engine.addEntity(enemy);
        engine.update(.1f);
        var target = tower.getComponent(CurrentTargetComponent.class);
        assertNull(target);
    }
}
