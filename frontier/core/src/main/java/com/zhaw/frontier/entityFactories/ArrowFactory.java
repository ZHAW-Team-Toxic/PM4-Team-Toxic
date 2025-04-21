package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.ProjectileComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.TextureRotationComponent;
import com.zhaw.frontier.components.RenderComponent.RenderType;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.utils.TileOffset;

public class ArrowFactory {

    private static final float ARROW_SPEED = 12f;
    private static final float MAX_RANGE = 40f;

    public static Entity createArrow(
            Engine engine,
            Vector2 tower,
            Vector2 target,
            Vector2 targetVelocity) {
        Entity arrow = engine.createEntity();

        if (tower.dst(target) > MAX_RANGE) {
            Gdx.app.debug("[ArrowFactory]", "Target out of range");
        }

        TextureAtlas atlas = AssetManagerInstance
                .getManager()
                .get("packed/textures.atlas", TextureAtlas.class);
        var render = new RenderComponent();
        render.renderType = RenderType.NORMAL;
        render.sprites.put(new TileOffset(0, 0), atlas.findRegion("buildings/Arrow/Arrow"));

        arrow.add(new PositionComponent(tower.x, tower.y));

        var velocity = new VelocityComponent();
        Vector2 calcVelocity = calculateInterceptVelocity(
                tower,
                ARROW_SPEED,
                target,
                targetVelocity);
        if (calcVelocity == null) {
            Gdx.app.debug("[ArrowFactory]", "Velocity is null");
            return null;
        }
        velocity.velocity = calcVelocity;
        // todo add lifetime for arrows so they can be removed after they should hit the
        // target

        var rotation = new TextureRotationComponent();
        rotation.rotation = calcVelocity.angleDeg();

        arrow.add(rotation);
        arrow.add(new ProjectileComponent());
        arrow.add(velocity);
        arrow.add(render);
        return arrow;
    }

    /**
     * Calculates the velocity vector needed for a projectile to hit a moving
     * target.
     *
     * @param shooterPosition The position of the shooter
     * @param bulletSpeed     The speed of the bullet/arrow
     * @param targetPosition  The current position of the target
     * @param targetVelocity  The velocity of the target
     * @return The velocity vector the bullet should have, or null if no solution
     *         exists
     */
    public static Vector2 calculateInterceptVelocity(
            Vector2 shooterPosition,
            float bulletSpeed,
            Vector2 targetPosition,
            Vector2 targetVelocity) {
        // Convert to relative coordinates (shooter at origin)
        Vector2 relativePosition = new Vector2(targetPosition).sub(shooterPosition);

        // Calculate quadratic equation coefficients
        // a = |Tv|² - Bs²
        float a = targetVelocity.len2() - (bulletSpeed * bulletSpeed);

        // b = 2(Tp·Tv)
        float b = 2 * relativePosition.dot(targetVelocity);

        // c = |Tp|²
        float c = relativePosition.len2();

        // Check if interception is possible
        float discriminant = b * b - 4 * a * c;

        if (discriminant < 0) {
            // No real solutions, target cannot be intercepted
            return null;
        }

        // Calculate the two possible intercept times
        float t1 = (-b + (float) Math.sqrt(discriminant)) / (2 * a);
        float t2 = (-b - (float) Math.sqrt(discriminant)) / (2 * a);

        // Choose the smallest non-negative time
        float interceptTime;
        if (t1 >= 0 && t2 >= 0) {
            interceptTime = Math.min(t1, t2);
        } else if (t1 >= 0) {
            interceptTime = t1;
        } else if (t2 >= 0) {
            interceptTime = t2;
        } else {
            // Both times are negative, which means we can't intercept
            return null;
        }

        // Special case when a is close to zero (target speed ≈ bullet speed)
        if (Math.abs(a) < 0.0001f) {
            // Use alternative calculation when a ≈ 0
            interceptTime = -c / b;
            if (interceptTime < 0) {
                return null;
            }
        }

        // Calculate the interception point
        Vector2 interceptPosition = new Vector2(
                relativePosition.x + targetVelocity.x * interceptTime,
                relativePosition.y + targetVelocity.y * interceptTime);

        // Calculate and return the required bullet velocity vector
        return interceptPosition.scl(1.0f / interceptTime);
    }
}
