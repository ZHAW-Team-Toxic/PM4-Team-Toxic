package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.BuildingPositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.mappers.*;

/**
 * This class is responsible for rendering the map and all entities on it.
 * It uses the @MapLayerMapper and @TowerMapper to access the necessary components.
 * It uses the @BuildingManagerSystem to access the buildings on the map.
 * It uses the @OrthogonalTiledMapRenderer to render the map.
 */
public class RenderSystem extends EntitySystem {

    private final Viewport viewport;
    private final Engine engine;
    private final OrthogonalTiledMapRenderer renderer;
    private final MapGridRenderer mapGridSystem;

    private final MapLayerMapper mapLayerMapper = new MapLayerMapper();
    private final HQMapper hqMapper = new HQMapper();
    private final TowerMapper towerMapper = new TowerMapper();
    private final WallMapper wallMapper = new WallMapper();
    private final ResourceBuildingMapper resourceBuildingMapper = new ResourceBuildingMapper();

    /**
     *
     */
    public RenderSystem(Viewport viewport, Engine engine, OrthogonalTiledMapRenderer renderer) {
        this.viewport = viewport;
        this.engine = engine;
        this.renderer = renderer;

        TiledMapTileLayer sampleLayer = engine
            .getEntitiesFor(mapLayerMapper.mapLayerFamily)
            .first()
            .getComponent(BottomLayerComponent.class)
            .bottomLayer;
        this.mapGridSystem = new MapGridRenderer(sampleLayer, viewport);
    }

    /**
     *
     */
    @Override
    public void update(float deltaTime) {
        // Clear the screen (black)
        ScreenUtils.clear(0, 0, 0, 1);

        // Apply viewport and update camera
        viewport.apply();

        renderer.setView((OrthographicCamera) viewport.getCamera());

        //begin the batch
        renderer.getBatch().begin();

        // Render map from the first layer to the last layer
        Entity mapEntity = engine.getEntitiesFor(mapLayerMapper.mapLayerFamily).first();
        TiledMapTileLayer bottomLayer = mapLayerMapper.bottomLayerMapper.get(mapEntity).bottomLayer;
        TiledMapTileLayer decorationLayer = mapLayerMapper.decorationLayerMapper.get(mapEntity)
            .decorationLayer;
        TiledMapTileLayer resourceLayer = mapLayerMapper.resourceLayerMapper.get(mapEntity)
            .resourceLayer;

        renderer.renderTileLayer(bottomLayer);
        renderer.renderTileLayer(decorationLayer);
        renderer.renderTileLayer(resourceLayer);

        //Render Map grid todo doesnt work yet
        // mapGridSystem.update((SpriteBatch) renderer.getBatch());

        //Render all buildings
        HQRenderer();
        renderWalls();
        renderTower();
        renderResourceBuilding();

        // Render other entities
        //TODO: Implement rendering for enemies and other entities

        renderer.getBatch().end();
    }

    private void HQRenderer() {
        for (Entity hqEntity : engine.getEntitiesFor(hqMapper.HQFamily)) {
            BuildingPositionComponent hqPosition = hqMapper.pm.get(hqEntity);
            RenderComponent hqRender = hqMapper.rm.get(hqEntity);
            Vector2 pixelCoordinate = calculatePixelCoordinate(
                (int) hqPosition.position.x,
                (int) hqPosition.position.y
            );
            renderer.getBatch().draw(hqRender.sprite, pixelCoordinate.x, pixelCoordinate.y);
        }
    }

    private void renderTower() {
        for (Entity buildingEntity : engine.getEntitiesFor(towerMapper.towerFamily)) {
            BuildingPositionComponent buildingPosition = towerMapper.pm.get(buildingEntity);
            RenderComponent buildingRender = towerMapper.rm.get(buildingEntity);
            Vector2 pixelCoordinate = calculatePixelCoordinate(
                (int) buildingPosition.position.x,
                (int) buildingPosition.position.y
            );
            renderer.getBatch().draw(buildingRender.sprite, pixelCoordinate.x, pixelCoordinate.y);
        }
    }

    private void renderWalls() {
        for (Entity wallEntity : engine.getEntitiesFor(wallMapper.wallFamily)) {
            BuildingPositionComponent wallPosition = wallMapper.pm.get(wallEntity);
            RenderComponent wallRender = wallMapper.rm.get(wallEntity);
            Vector2 pixelCoordinate = calculatePixelCoordinate(
                (int) wallPosition.position.x,
                (int) wallPosition.position.y
            );
            renderer.getBatch().draw(wallRender.sprite, pixelCoordinate.x, pixelCoordinate.y);
        }
    }

    private void renderResourceBuilding() {
        for (Entity resourceBuildingEntity : engine.getEntitiesFor(
            resourceBuildingMapper.resouceBuildingFamily
        )) {
            BuildingPositionComponent resourceBuildingPosition = resourceBuildingMapper.pm.get(
                resourceBuildingEntity
            );
            RenderComponent resourceBuildingRender = resourceBuildingMapper.rm.get(
                resourceBuildingEntity
            );
            Vector2 pixelCoordinate = calculatePixelCoordinate(
                (int) resourceBuildingPosition.position.x,
                (int) resourceBuildingPosition.position.y
            );
            renderer
                .getBatch()
                .draw(resourceBuildingRender.sprite, pixelCoordinate.x, pixelCoordinate.y);
        }
    }

    //used to fix the sprite on the bottom left corner of the tile
    private Vector2 calculatePixelCoordinate(int x, int y) {
        Entity map = engine.getEntitiesFor(mapLayerMapper.mapLayerFamily).first();
        int tileX = x * mapLayerMapper.bottomLayerMapper.get(map).bottomLayer.getTileWidth();
        int tileY = y * mapLayerMapper.bottomLayerMapper.get(map).bottomLayer.getTileHeight();
        return new Vector2(tileX, tileY);
    }
}
