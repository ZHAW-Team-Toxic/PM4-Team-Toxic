package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.DecorationLayerComponent;
import com.zhaw.frontier.components.map.ResourceLayerComponent;
import com.zhaw.frontier.entities.Map;
import com.zhaw.frontier.mappers.MapLayerMapper;
import com.zhaw.frontier.mappers.TowerMapper;
import com.zhaw.frontier.subsystems.BuildingManagerSystem;
import com.zhaw.frontier.subsystems.MapLoaderSystem;

/**
 * This class is responsible for rendering the map and all entities on it.
 * It uses the @MapLayerMapper and @TowerMapper to access the necessary components.
 * It uses the @BuildingManagerSystem to access the buildings on the map.
 * It uses the @OrthogonalTiledMapRenderer to render the map.
 */
public class RenderSystem extends EntitySystem {

    private final SpriteBatch batch;
    private final Viewport viewport;
    private final Engine engine;
    private final OrthogonalTiledMapRenderer renderer;
    private final BuildingManagerSystem buildingManagerSystem;

    private final MapLayerMapper mapLayerMapper = new MapLayerMapper();
    private final TowerMapper towerMapper = new TowerMapper();

    /**
     * Constructor for the RenderSystem.
     * Initializes the system with the necessary components and systems.
     * @param batch The sprite batch to be used for rendering.
     * @param viewport The viewport to be used.
     * @param engine The engine to be used.
     * @param renderer The renderer to be used.
     * @param mapLoaderSystem The map loader system to be used.
     * @param buildingManagerSystem The building manager system to be used.
     */
    public RenderSystem(
        SpriteBatch batch,
        Viewport viewport,
        Engine engine,
        OrthogonalTiledMapRenderer renderer,
        MapLoaderSystem mapLoaderSystem,
        BuildingManagerSystem buildingManagerSystem
    ) {
        this.batch = batch;
        this.viewport = viewport;
        this.engine = engine;
        this.renderer = renderer;
        this.renderer.setMap(mapLoaderSystem.getMap());
        this.buildingManagerSystem = buildingManagerSystem;
    }

    /**
     * Renders the map and all entities on it.
     * It first clears the screen, then applies the viewport and updates the camera.
     * It renders in following order:
     * - Map layers
     * - Building entities
     * - Other entities (not implemented yet)
     * @param deltaTime The time passed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        // Clear the screen (black)
        ScreenUtils.clear(0, 0, 0, 1);

        // Apply viewport and update camera
        viewport.apply();

        // Render map layers
        for (Entity entity : engine.getEntitiesFor(mapLayerMapper.mapLayerFamily)) {
            BottomLayerComponent bottomLayer = mapLayerMapper.bottomLayerMapper.get(entity);
            DecorationLayerComponent decorationLayer = mapLayerMapper.decorationLayerMapper.get(
                entity
            );
            ResourceLayerComponent resourceLayer = mapLayerMapper.resourceLayerMapper.get(entity);
            renderer.getBatch().begin();
            renderer.renderTileLayer(bottomLayer.bottomLayer);
            renderer.renderTileLayer(decorationLayer.decorationLayer);
            renderer.renderTileLayer(resourceLayer.resourceLayer);
            renderer.getBatch().end();
        }

        //Here necessary to apply the viewport again, because the renderer changes the camera
        viewport.apply();

        // Render building entities
        renderer.getBatch().begin();
        for (Entity buildingEntity : buildingManagerSystem.getBuildingEntities()) {
            PositionComponent buildingPosition = towerMapper.pm.get(buildingEntity);
            RenderComponent buildingRender = towerMapper.rm.get(buildingEntity);
            Vector2 pixelCoordinate = calculatePixelCoordinate(
                (int) buildingPosition.position.x,
                (int) buildingPosition.position.y
            );
            renderer.getBatch().draw(buildingRender.sprite, pixelCoordinate.x, pixelCoordinate.y);
        }
        renderer.getBatch().end();

        //Here necessary to apply the viewport again, because the renderer changes the camera
        viewport.apply();
        // Render other entities
        //TODO: Implement rendering for enemies and other entities

    }

    private Vector2 calculatePixelCoordinate(int x, int y) {
        Map map = buildingManagerSystem.getMap();
        int tilex = x * mapLayerMapper.bottomLayerMapper.get(map).bottomLayer.getTileWidth();
        int tiley = y * mapLayerMapper.bottomLayerMapper.get(map).bottomLayer.getTileHeight();
        return new Vector2(tilex, tiley);
    }
}
