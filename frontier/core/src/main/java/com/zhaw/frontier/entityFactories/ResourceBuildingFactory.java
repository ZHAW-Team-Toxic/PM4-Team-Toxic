package com.zhaw.frontier.entityFactories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import com.zhaw.frontier.utils.LayeredSprite;
import com.zhaw.frontier.utils.TileOffset;
import java.util.List;

/**
 * A factory class responsible for creating Resource Building entities.
 * <p>
 * This factory creates a default resource building that is initialized with the necessary components:
 * <ul>
 *   <li>{@link PositionComponent} - Holds the position data.</li>
 *   <li>{@link HealthComponent} - Manages the health of the resource building.</li>
 *   <li>{@link ResourceGeneratorComponent} - Handles resource generation logic.</li>
 *   <li>{@link RenderComponent} - Manages rendering; its {@code renderType} is set to
 *       {@link RenderComponent.RenderType#BUILDING} and it uses a placeholder texture.</li>
 * </ul>
 * </p>
 */
public class ResourceBuildingFactory {

    /**
     * Creates a default resource building entity with the required components.
     * <p>
     * The entity is configured with a {@link PositionComponent}, {@link HealthComponent},
     * {@link ResourceGeneratorComponent}, and a {@link RenderComponent} using a placeholder texture.
     * The placeholder texture is generated using a 12x12 pixmap and should be replaced with an
     * actual resource building texture in production.
     * </p>
     *
     * @param engine the {@link Engine} used to create and manage the entity.
     * @return the newly created resource building entity.
     */
    public static Entity createDefaultResourceBuilding(Engine engine) {
        Entity resourceBuilding = engine.createEntity();
        resourceBuilding.add(new PositionComponent());
        resourceBuilding.add(new OccupiesTilesComponent());
        resourceBuilding.add(new HealthComponent());
        resourceBuilding.add(new ResourceGeneratorComponent());
        resourceBuilding.add(new ResourceProductionComponent());
        resourceBuilding.add(new RenderComponent());
        resourceBuilding.add(new BuildingAnimationComponent());
        return resourceBuilding;
    }

    public static Entity woodResourceBuilding(Engine engine) {
        Entity resourceBuilding = engine.createEntity();
        PositionComponent positionComponent = new PositionComponent();
        positionComponent.heightInTiles = 1;
        positionComponent.widthInTiles = 1;
        resourceBuilding.add(positionComponent);
        resourceBuilding.add(new OccupiesTilesComponent());
        resourceBuilding.add(new HealthComponent());
        resourceBuilding.add(new ResourceGeneratorComponent());

        ResourceProductionComponent resourceProductionComponent = new ResourceProductionComponent();
        resourceProductionComponent.productionRate.put(ResourceTypeEnum.RESOURCE_TYPE_WOOD, 1);
        resourceBuilding.add(resourceProductionComponent);

        RenderComponent renderComponent = new RenderComponent();
        renderComponent.renderType = RenderComponent.RenderType.BUILDING;
        Texture texture = createPlaceHolder();
        renderComponent.sprites.put(
            new TileOffset(0, 0),
            (List<LayeredSprite>) new Sprite(texture)
        );
        renderComponent.widthInTiles = 1;
        renderComponent.heightInTiles = 1;

        resourceBuilding.add(renderComponent);
        resourceBuilding.add(new BuildingAnimationComponent());
        return resourceBuilding;
    }

    public static Entity stoneResourceBuilding(Engine engine) {
        Entity resourceBuilding = engine.createEntity();
        PositionComponent positionComponent = new PositionComponent();
        positionComponent.heightInTiles = 1;
        positionComponent.widthInTiles = 1;
        resourceBuilding.add(positionComponent);
        resourceBuilding.add(new OccupiesTilesComponent());
        resourceBuilding.add(new HealthComponent());
        resourceBuilding.add(new ResourceGeneratorComponent());

        ResourceProductionComponent resourceProductionComponent = new ResourceProductionComponent();
        resourceProductionComponent.productionRate.put(ResourceTypeEnum.RESOURCE_TYPE_STONE, 1);
        resourceBuilding.add(resourceProductionComponent);

        RenderComponent renderComponent = new RenderComponent();
        renderComponent.renderType = RenderComponent.RenderType.BUILDING;
        Texture texture = createPlaceHolder();
        renderComponent.sprites.put(
            new TileOffset(0, 0),
            (List<LayeredSprite>) new Sprite(texture)
        );

        resourceBuilding.add(renderComponent);
        resourceBuilding.add(new BuildingAnimationComponent());
        return resourceBuilding;
    }

    public static Entity ironResourceBuilding(Engine engine) {
        Entity resourceBuilding = engine.createEntity();
        resourceBuilding.add(new PositionComponent());
        PositionComponent positionComponent = new PositionComponent();
        positionComponent.heightInTiles = 1;
        positionComponent.widthInTiles = 1;
        resourceBuilding.add(positionComponent);
        resourceBuilding.add(new OccupiesTilesComponent());
        resourceBuilding.add(new HealthComponent());
        resourceBuilding.add(new ResourceGeneratorComponent());

        ResourceProductionComponent resourceProductionComponent = new ResourceProductionComponent();
        resourceProductionComponent.productionRate.put(ResourceTypeEnum.RESOURCE_TYPE_IRON, 1);
        resourceBuilding.add(resourceProductionComponent);

        RenderComponent renderComponent = new RenderComponent();
        renderComponent.renderType = RenderComponent.RenderType.BUILDING;
        Texture texture = createPlaceHolder();
        renderComponent.sprites.put(
            new TileOffset(0, 0),
            (List<LayeredSprite>) new Sprite(texture)
        );
        renderComponent.widthInTiles = 1;
        renderComponent.heightInTiles = 1;

        resourceBuilding.add(renderComponent);
        resourceBuilding.add(new BuildingAnimationComponent());
        return resourceBuilding;
    }

    private static Texture createPlaceHolder() {
        // Create a Pixmap with dimensions 12x12 using the RGBA8888 format.
        Pixmap pixmap = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
        // Set the drawing color to a dark green (the values here can be adjusted as needed).
        pixmap.setColor(0, 64, 0, 1);
        // Fill the entire pixmap with the specified color.
        pixmap.fill();
        // Create a texture from the pixmap.
        Texture texture = new Texture(pixmap);
        // Dispose of the pixmap since it is no longer needed.
        pixmap.dispose();
        return texture;
    }
}
