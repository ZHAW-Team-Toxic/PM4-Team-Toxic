package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.utils.TileOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * A factory class responsible for creating Tower entities.
 * <p>
 * This factory provides a method to create a default Tower entity which is initialized with
 * the necessary components:
 * <ul>
 *   <li>{@link PositionComponent} for position data,</li>
 *   <li>{@link HealthComponent} for health data,</li>
 *   <li>{@link AttackComponent} for attack capabilities, and</li>
 *   <li>{@link RenderComponent} for rendering. The {@code renderType} is set to
 *       {@link RenderComponent.RenderType#BUILDING} and a placeholder texture is used.</li>
 * </ul>
 * </p>
 */
public class TowerFactory {

    private static final Map<
        Enum<?>,
        HashMap<TileOffset, Animation<TextureRegion>>
    > ballistaTowerAnimationCache = new HashMap<>();
    private static final Map<
        Enum<?>,
        HashMap<TileOffset, Animation<TextureRegion>>
    > cannonTowerAnimationCache = new HashMap<>();

    public static Entity createBallistaTower(Engine engine, AssetManager assetManager) {
        return createDefaultTower(engine, assetManager);
    }

    /**
     * Creates a default Tower entity with the required components.
     * <p>
     * The tower entity is initialized with a {@link PositionComponent}, {@link HealthComponent},
     * {@link AttackComponent}, and a {@link RenderComponent}. The {@code RenderComponent} is set up
     * with a placeholder texture. This texture should be replaced with the actual tower texture in the future.
     * </p>
     *
     * @param engine the {@link Engine} used to create and manage the entity.
     * @return the newly created Tower entity.
     */
    public static Entity createDefaultTower(Engine engine, AssetManager assetManager) {
        Entity tower = engine.createEntity();

        PositionComponent positionComponent = new PositionComponent();
        positionComponent.heightInTiles = 1;
        positionComponent.widthInTiles = 1;
        tower.add(positionComponent);
        tower.add(new OccupiesTilesComponent());
        tower.add(new HealthComponent());
        tower.add(new AttackComponent());

        // Placeholder texture
        Texture texture = createPlaceHolder();
        TextureRegion region = new TextureRegion(texture);

        RenderComponent renderComponent = new RenderComponent();
        renderComponent.renderType = RenderComponent.RenderType.BUILDING;
        renderComponent.heightInTiles = 1;
        renderComponent.widthInTiles = 1;
        renderComponent.sprites.put(new TileOffset(0, 0), region);
        renderComponent.zIndex = 10;

        tower.add(renderComponent);
        tower.add(new BuildingAnimationComponent());

        return tower;
    }

    /**
     * Creates a placeholder texture used as a visual representation for the tower.
     * <p>
     * The placeholder is a 12x12 black square and can easily be swapped
     * with a real texture or sprite later.
     * </p>
     *
     * @return the generated {@link Texture} placeholder.
     */
    private static Texture createPlaceHolder() {
        Pixmap pixmap = new Pixmap(12, 12, Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 1); // black
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
