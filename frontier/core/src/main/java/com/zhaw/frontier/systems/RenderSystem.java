package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.mappers.HQMapper;
import com.zhaw.frontier.mappers.MapLayerMapper;
import com.zhaw.frontier.mappers.ResourceBuildingMapper;
import com.zhaw.frontier.mappers.TowerMapper;
import com.zhaw.frontier.mappers.WallMapper;

/**
 * System responsible for rendering the map and game entities.
 * <p>
 * This system renders the tiled map layers (bottom, decoration, and resource layers)
 * using an {@link OrthogonalTiledMapRenderer} and draws game entities (currently buildings)
 * with a {@link SpriteBatch}. Building entities are rendered based on their {@link PositionComponent}
 * and {@link RenderComponent} data.
 * </p>
 */
public class RenderSystem extends EntitySystem {

    private final Viewport viewport;
    private final Engine engine;
    private final OrthogonalTiledMapRenderer renderer;
    private final HealthBarManager healthBarManager;

    private final MapLayerMapper mapLayerMapper = new MapLayerMapper();
    private final HQMapper hqMapper = new HQMapper();
    private final TowerMapper towerMapper = new TowerMapper();
    private final WallMapper wallMapper = new WallMapper();
    private final ResourceBuildingMapper resourceBuildingMapper = new ResourceBuildingMapper();

    /**
     * Constructs a new RenderSystem.
     *
     * @param viewport the {@link Viewport} used for rendering.
     * @param engine   the {@link Engine} that manages game entities.
     * @param renderer the {@link OrthogonalTiledMapRenderer} used for rendering the tiled map.
     */
    public RenderSystem(Viewport viewport, Engine engine, OrthogonalTiledMapRenderer renderer) {
        this.viewport = viewport;
        this.engine = engine;
        this.renderer = renderer;
        this.healthBarManager = new HealthBarManager(engine);
    }

    /**
     * Updates the render system.
     * <p>
     * This method clears the screen, applies the viewport, updates the camera view,
     * renders the map layers, and then renders all building entities.
     * </p>
     *
     * @param deltaTime the time elapsed since the last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        // Clear the screen with black.
        ScreenUtils.clear(0, 0, 0, 1);

        // Apply viewport and update camera.
        viewport.apply();
        renderer.setView((OrthographicCamera) viewport.getCamera());

        // Begin the sprite batch.
        renderer.getBatch().begin();

        // Retrieve the map entity and extract its layers.
        Entity mapEntity = engine.getEntitiesFor(mapLayerMapper.mapLayerFamily).first();
        TiledMapTileLayer bottomLayer = mapLayerMapper.bottomLayerMapper.get(mapEntity).bottomLayer;
        TiledMapTileLayer decorationLayer = mapLayerMapper.decorationLayerMapper.get(mapEntity)
            .decorationLayer;
        TiledMapTileLayer resourceLayer = mapLayerMapper.resourceLayerMapper.get(mapEntity)
            .resourceLayer;

        // Render the tiled map layers.
        renderer.renderTileLayer(bottomLayer);
        renderer.renderTileLayer(decorationLayer);
        renderer.renderTileLayer(resourceLayer);

        // Render all building entities.
        renderBuilding((SpriteBatch) renderer.getBatch());

        // TODO: Implement rendering for enemies and other entities.

        // End the sprite batch.
        renderer.getBatch().end();
    }

    /**
     * Renders building entities using the provided sprite batch.
     * <p>
     * This method iterates over all entities that have both a {@link PositionComponent} and a
     * {@link RenderComponent}. Only entities whose {@code renderType} is set to
     * {@link RenderComponent.RenderType#BUILDING} are rendered. The method calculates pixel coordinates
     * for each building and draws its sprite at that position.
     * </p>
     *
     * @param renderer the {@link SpriteBatch} used for drawing sprites.
     */
    private void renderBuilding(SpriteBatch renderer) {
        Family buildingFamily = Family.all(PositionComponent.class, RenderComponent.class).get();
        for (Entity building : engine.getEntitiesFor(buildingFamily)) {
            if (
                building.getComponent(RenderComponent.class).renderType ==
                    RenderComponent.RenderType.BUILDING ||
                building.getComponent(RenderComponent.class).renderType ==
                    RenderComponent.RenderType.ENEMY
            ) {
                PositionComponent positionComponent = building.getComponent(
                    PositionComponent.class
                );
                RenderComponent renderComponent = building.getComponent(RenderComponent.class);
                Vector2 pixelCoordinate = calculatePixelCoordinate(
                    (int) positionComponent.position.x,
                    (int) positionComponent.position.y
                );
                renderComponent.sprite.setPosition(pixelCoordinate.x, pixelCoordinate.y);
                renderComponent.sprite.draw(renderer);
                healthBarManager.drawHealthBar(renderer, building);
            }
        }
    }

    /**
     * Calculates the pixel coordinate corresponding to a given tile coordinate.
     * <p>
     * This method converts tile indices (x, y) to pixel coordinates by multiplying them with the tile width and height
     * from the bottom layer of the map.
     * </p>
     *
     * @param x the tile x-coordinate.
     * @param y the tile y-coordinate.
     * @return a {@link Vector2} representing the pixel coordinates.
     */
    private Vector2 calculatePixelCoordinate(int x, int y) {
        Entity map = engine.getEntitiesFor(mapLayerMapper.mapLayerFamily).first();
        int tileX = x * mapLayerMapper.bottomLayerMapper.get(map).bottomLayer.getTileWidth();
        int tileY = y * mapLayerMapper.bottomLayerMapper.get(map).bottomLayer.getTileHeight();
        return new Vector2(tileX, tileY);
    }
}
