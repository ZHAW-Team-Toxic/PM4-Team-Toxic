package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.TowerAnimationComponent;
import com.zhaw.frontier.utils.TileOffset;
import java.util.Set;

public class StateDirectionalTextureSystem extends IteratingSystem {

    public StateDirectionalTextureSystem() {
        super(Family.all(RenderComponent.class, TowerAnimationComponent.class).get());
        Gdx.app.debug("TowerDirectionTextureSystem", "Initialized Tower Direction System.");
    }

    float time;

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var direction = entity.getComponent(TowerAnimationComponent.class);
        var renderComponent = entity.getComponent(RenderComponent.class);

        var possible = direction.animationTextures.keySet();
        var nearest = clampToNearest(direction.degrees, possible);

        renderComponent.sprites.clear();
        renderComponent.sprites.put(new TileOffset(0, 0), direction.animationTextures.get(nearest));
    }

    public static int clampToNearest(int degrees, Set<Integer> allowedDegrees) {
        if (allowedDegrees == null || allowedDegrees.isEmpty()) {
            throw new IllegalArgumentException(
                    "The set of allowed degrees cannot be null or empty.");
        }

        int nearest = allowedDegrees.iterator().next();
        int minDifference = Math.abs(degrees - nearest);

        for (int allowedDegree : allowedDegrees) {
            int difference = Math.abs(degrees - allowedDegree);
            if (difference < minDifference) {
                minDifference = difference;
                nearest = allowedDegree;
            }
        }

        return nearest;
    }
}
