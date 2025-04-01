package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.MultiTileAnimationComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.mappers.HQMapper;
import com.zhaw.frontier.mappers.MapLayerMapper;
import com.zhaw.frontier.mappers.ResourceBuildingMapper;
import com.zhaw.frontier.mappers.TowerMapper;
import com.zhaw.frontier.mappers.WallMapper;

public class RenderSystem extends EntitySystem {

    private final Viewport viewport;
    private final Engine engine;
    private final OrthogonalTiledMapRenderer renderer;

    private final MapLayerMapper mapLayerMapper = new MapLayerMapper();
    private final HQMapper hqMapper = new HQMapper();
    private final TowerMapper towerMapper = new TowerMapper();
    private final WallMapper wallMapper = new WallMapper();
    private final ResourceBuildingMapper resourceBuildingMapper = new ResourceBuildingMapper();

    public RenderSystem(Viewport viewport, Engine engine, OrthogonalTiledMapRenderer renderer) {
        this.viewport = viewport;
        this.engine = engine;
        this.renderer = renderer;
    }

    @Override
    public void update(float deltaTime) {
        ScreenUtils.clear(0, 0, 0, 1);

        viewport.apply();
        renderer.setView((OrthographicCamera) viewport.getCamera());

        renderer.getBatch().begin();

        Entity mapEntity = engine.getEntitiesFor(mapLayerMapper.mapLayerFamily).first();
        TiledMapTileLayer bottomLayer = mapLayerMapper.bottomLayerMapper.get(mapEntity).bottomLayer;
        TiledMapTileLayer decorationLayer = mapLayerMapper.decorationLayerMapper.get(mapEntity).decorationLayer;
        TiledMapTileLayer resourceLayer = mapLayerMapper.resourceLayerMapper.get(mapEntity).resourceLayer;

        renderer.renderTileLayer(bottomLayer);
        renderer.renderTileLayer(decorationLayer);
        renderer.renderTileLayer(resourceLayer);

        renderBuildings((SpriteBatch) renderer.getBatch());

        renderer.getBatch().end();
    }

    private void renderBuildings(SpriteBatch renderer) {
        Family buildingFamily = Family.all(PositionComponent.class, RenderComponent.class).get();
        for (Entity building : engine.getEntitiesFor(buildingFamily)) {
            if (building.getComponent(RenderComponent.class).renderType == RenderComponent.RenderType.BUILDING) {
                MultiTileAnimationComponent anim = building.getComponent(MultiTileAnimationComponent.class);
                if (anim != null) {
                    renderMultiTileBuilding(building, renderer);
                } else {
                    renderSingleTileBuilding(building, renderer);
                }
            } else {
                renderEnemies(building, renderer);
            }
        }
    }

    private void renderSingleTileBuilding(Entity building, SpriteBatch renderer) {
        PositionComponent pos = building.getComponent(PositionComponent.class);
        RenderComponent render = building.getComponent(RenderComponent.class);
        Vector2 pixel = calculatePixelCoordinate(
            (int) pos.currentPosition.x,
            (int) pos.currentPosition.y
        );

        renderer.draw(
            render.textureRegion,
            pixel.x,
            pixel.y,
            render.textureRegion.getRegionWidth(),
            render.textureRegion.getRegionHeight()
        );
    }

    private void renderMultiTileBuilding(Entity building, SpriteBatch renderer) {
        PositionComponent pos = building.getComponent(PositionComponent.class);
        MultiTileAnimationComponent anim = building.getComponent(MultiTileAnimationComponent.class);

        float stateTime = anim.stateTime;
        Vector2 basePixel = calculatePixelCoordinate(
            (int) pos.currentPosition.x,
            (int) pos.currentPosition.y
        );
        int tileSize = 16;

        renderer.draw(anim.get("topLeft").getKeyFrame(stateTime, true), basePixel.x, basePixel.y + tileSize);
        renderer.draw(anim.get("topRight").getKeyFrame(stateTime, true), basePixel.x + tileSize, basePixel.y + tileSize);
        renderer.draw(anim.get("bottomLeft").getKeyFrame(stateTime, true), basePixel.x, basePixel.y);
        renderer.draw(anim.get("bottomRight").getKeyFrame(stateTime, true), basePixel.x + tileSize, basePixel.y);
    }

    private void renderEnemies(Entity entity, SpriteBatch renderer) {
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        RenderComponent renderComponent = entity.getComponent(RenderComponent.class);
        Vector2 pixelCoordinate = calculatePixelCoordinateForEnemies(
            positionComponent.currentPosition.x,
            positionComponent.currentPosition.y
        );
        Gdx.app.debug("RenderSystem", "Rendering enemy at: " + pixelCoordinate);
        renderer.draw(
            renderComponent.textureRegion,
            pixelCoordinate.x,
            pixelCoordinate.y,
            renderComponent.textureRegion.getRegionWidth(),
            renderComponent.textureRegion.getRegionHeight()
        );
    }

    private Vector2 calculatePixelCoordinate(int x, int y) {
        Entity map = engine.getEntitiesFor(mapLayerMapper.mapLayerFamily).first();
        int tileX = x * mapLayerMapper.bottomLayerMapper.get(map).bottomLayer.getTileWidth();
        int tileY = y * mapLayerMapper.bottomLayerMapper.get(map).bottomLayer.getTileHeight();
        return new Vector2(tileX, tileY);
    }

    private Vector2 calculatePixelCoordinateForEnemies(float x, float y) {
        Entity map = engine.getEntitiesFor(mapLayerMapper.mapLayerFamily).first();
        float tileX = x * mapLayerMapper.bottomLayerMapper.get(map).bottomLayer.getTileWidth();
        float tileY = y * mapLayerMapper.bottomLayerMapper.get(map).bottomLayer.getTileHeight();
        return new Vector2(tileX, tileY);
    }
}
