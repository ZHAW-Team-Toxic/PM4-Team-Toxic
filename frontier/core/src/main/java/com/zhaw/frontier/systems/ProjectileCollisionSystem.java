package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.zhaw.frontier.components.*;

public class ProjectileCollisionSystem extends IntervalIteratingSystem {

    private ImmutableArray<Entity> collisionObjects;
    private final ComponentMapper<PositionComponent> positionComponentMapper =
        ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<ProjectileComponent> projectileComponentMapper =
        ComponentMapper.getFor(ProjectileComponent.class);
    private final ComponentMapper<CircleCollisionComponent> collisionComponentMapper =
        ComponentMapper.getFor(CircleCollisionComponent.class);
    private final ComponentMapper<HealthComponent> healthComponentMapper = ComponentMapper.getFor(
        HealthComponent.class
    );

    public ProjectileCollisionSystem() {
        super(Family.all(ProjectileComponent.class, PositionComponent.class).get(), 0.1f);
        Gdx.app.debug("ProjectileCollisionSystem", "initialized");
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        Gdx.app.debug("ProjectileCollisionSystem", "adding listener to engine");

        // Setup the family
        Family collisionFamily = Family
            .all(CircleCollisionComponent.class, PositionComponent.class, HealthComponent.class)
            .get();

        this.collisionObjects = engine.getEntitiesFor(collisionFamily);
    }

    @Override
    protected void processEntity(Entity arrowEntity) {
        var arrowPosition = positionComponentMapper.get(arrowEntity).basePosition;
        var arrowDamage = projectileComponentMapper.get(arrowEntity).damage;

        for (var enemyEntity : collisionObjects) {
            var enemyPosition = positionComponentMapper.get(enemyEntity).basePosition;
            var enemyCollider = collisionComponentMapper.get(enemyEntity).collisionObject;
            enemyCollider.setPosition(enemyPosition);

            if (enemyCollider.contains(arrowPosition)) {
                Gdx.app.debug("PrjectileCollisionSystem", "Collision detected");
                var enemyHealthComponent = healthComponentMapper.get(enemyEntity);
                enemyHealthComponent.Health -= arrowDamage;
                getEngine().removeEntity(arrowEntity);
            }
        }
    }
}
