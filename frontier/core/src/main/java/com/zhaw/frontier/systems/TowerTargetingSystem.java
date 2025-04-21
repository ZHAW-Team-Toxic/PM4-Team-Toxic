package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.zhaw.frontier.components.AttackComponent;
import com.zhaw.frontier.components.CircleCollisionComponent;
import com.zhaw.frontier.components.CurrentTargetComponent;
import com.zhaw.frontier.components.EnemyComponent;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.TowerAnimationComponent;
import com.zhaw.frontier.components.TowerComponent;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.entityFactories.ArrowFactory;

public class TowerTargetingSystem extends IntervalIteratingSystem {

    private ImmutableArray<Entity> enemies;
    private final ComponentMapper<PositionComponent> positionComponentMapper = ComponentMapper.getFor(
            PositionComponent.class);

    private final ComponentMapper<VelocityComponent> velocityComponentMapper = ComponentMapper.getFor(
            VelocityComponent.class);
    private final ComponentMapper<AttackComponent> attackComponentMapper = ComponentMapper.getFor(
            AttackComponent.class);

    private final ComponentMapper<CurrentTargetComponent> targetComponentMapper = ComponentMapper.getFor(
            CurrentTargetComponent.class);

    private final ComponentMapper<TowerAnimationComponent  > towerAnimationComponentComponentMapper= ComponentMapper.getFor(
        TowerAnimationComponent.class);
    public TowerTargetingSystem() {
        super(Family.all(TowerAnimationComponent.class, TowerComponent.class, AttackComponent.class).get(),
                0.5f);
        Gdx.app.debug("TowerTargetingSystem", "initialized");
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        Gdx.app.debug("TowerTargetingSystem", "adding listener to engine");

        // Setup the family
        Family enemyFamily = Family.all(
                CircleCollisionComponent.class,
                PositionComponent.class,
                VelocityComponent.class,
                HealthComponent.class, EnemyComponent.class).get();

        this.enemies = engine.getEntitiesFor(enemyFamily);
    }

    @Override
    protected void processEntity(Entity tower) {
        var currentTarget = targetComponentMapper.get(tower);

        // remove already killed enemies
        if (currentTarget != null && !enemies.contains(currentTarget.target, false)) {
            tower.remove(CurrentTargetComponent.class);
            currentTarget = null;
        }

        if (currentTarget == null) {
            findNewTarget(tower);
        } else {
            shootAtTarget(tower, currentTarget.target);
        }
    }

    private void shootAtTarget(Entity tower, Entity enemy) {
        if (inRange(tower, enemy)) {
            var enemyPosition = positionComponentMapper.get(enemy).basePosition;
            var enemyVelocity = velocityComponentMapper.get(enemy).velocity;
            var towerPosition = positionComponentMapper.get(tower).basePosition;
            // todo add stats from attackcomponent
            // todo set direction
            var animation = towerAnimationComponentComponentMapper.get(tower);
            var arrow = ArrowFactory.createArrow(getEngine(), towerPosition, enemyPosition, enemyVelocity);
            if(arrow != null) {
                var arrowVelocity = velocityComponentMapper.get(arrow).velocity;
                animation.degrees = (int) arrowVelocity.angleDeg();
                getEngine().addEntity(arrow);
            }
        } else {
            tower.remove(CurrentTargetComponent.class);
        }

    }

    private void findNewTarget(Entity tower) {
        for (var enemy : enemies) {
            if (inRange(tower, enemy)) {
                var target = new CurrentTargetComponent();
                target.target = enemy;
                tower.add(target);
                return;
            }
        }
    }

    private boolean inRange(Entity tower, Entity enemy) {
        var enemyPosition = positionComponentMapper.get(enemy);
        var towerPosition = positionComponentMapper.get(tower);
        var towerAttackComponent = attackComponentMapper.get(tower);

        return enemyPosition.basePosition.dst(towerPosition.basePosition) <= towerAttackComponent.AttackRange;
    }
}
