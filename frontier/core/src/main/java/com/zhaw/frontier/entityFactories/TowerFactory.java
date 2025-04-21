package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.utils.TileOffset;

/**
 * A factory class responsible for creating Tower entities.
 * <p>
 * This factory provides a method to create a default Tower entity which is
 * initialized with
 * the necessary components:
 * <ul>
 * <li>{@link PositionComponent} for position data,</li>
 * <li>{@link HealthComponent} for health data,</li>
 * <li>{@link AttackComponent} for attack capabilities, and</li>
 * <li>{@link RenderComponent} for rendering. The {@code renderType} is set to
 * {@link RenderComponent.RenderType#BUILDING} and a placeholder texture is
 * used.</li>
 * </ul>
 * </p>
 */
public class TowerFactory {

    public static Entity createBallistaTower(Engine engine, float x, float y) {
        Entity tower = createDefaultTower(engine, x, y);
        tower.add(new EntityTypeComponent(EntityTypeComponent.EntityType.BALLISTA_TOWER));
        return tower;
    }

    /**
     * Creates a default Tower entity with the required components.
     * <p>
     * The tower entity is initialized with a {@link PositionComponent},
     * {@link HealthComponent},
     * {@link AttackComponent}, and a {@link RenderComponent}. The
     * {@code RenderComponent} is set up
     * with a placeholder texture. This texture should be replaced with the actual
     * tower texture in the future.
     * </p>
     *
     * @param engine the {@link Engine} used to create and manage the entity.
     * @return the newly created Tower entity.
     */
    public static Entity createDefaultTower(Engine engine, float x, float y) { // todo add tower damage to this
        TextureAtlas atlas = AssetManagerInstance
            .getManager()
            .get("packed/textures.atlas", TextureAtlas.class);
        Entity tower = engine.createEntity();
        tower.add(new PositionComponent());
        HealthComponent healthComponent = new HealthComponent();
        healthComponent.maxHealth = 100;
        healthComponent.currentHealth = 60;
        tower.add(healthComponent);
        tower.add(new AttackComponent());

        // Placeholder texture

        var renderComponent = new RenderComponent(RenderComponent.RenderType.TOWER, 10, 1, 1);

        TowerAnimationComponent directionTextures = new TowerAnimationComponent();
        directionTextures.animationTextures.put(
            45,
            atlas.findRegion("buildings/Tower/Wood_Tower1")
        );
        directionTextures.animationTextures.put(0, atlas.findRegion("buildings/Tower/Wood_Tower2"));
        directionTextures.animationTextures.put(
            315,
            atlas.findRegion("buildings/Tower/Wood_Tower3")
        );
        directionTextures.animationTextures.put(
            270,
            atlas.findRegion("buildings/Tower/Wood_Tower4")
        );
        directionTextures.animationTextures.put(
            225,
            atlas.findRegion("buildings/Tower/Wood_Tower5")
        );
        directionTextures.animationTextures.put(
            180,
            atlas.findRegion("buildings/Tower/Wood_Tower6")
        );
        directionTextures.animationTextures.put(
            135,
            atlas.findRegion("buildings/Tower/Wood_Tower7")
        );
        directionTextures.animationTextures.put(
            90,
            atlas.findRegion("buildings/Tower/Wood_Tower8")
        );
        renderComponent.sprites.put(
            new TileOffset(0, 0),
            directionTextures.animationTextures.get(0)
        );

        var attack = new AttackComponent();
        attack.AttackSpeed = 1;
        attack.AttackDamage = 20;
        attack.AttackRange = 10;

        tower.add(renderComponent);
        tower.add(directionTextures);
        tower.add(attack);
        tower.add(new PositionComponent(x, y, 1, 1));
        tower.add(new OccupiesTilesComponent());
        tower.add(new HealthComponent());
        tower.add(new TowerComponent());
        tower.add(new BuildingAnimationComponent());
        return tower;
    }
}
