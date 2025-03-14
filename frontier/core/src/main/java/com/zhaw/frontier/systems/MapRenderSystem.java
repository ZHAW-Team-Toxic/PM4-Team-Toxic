package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.zhaw.frontier.components.map.*;

import java.nio.file.Path;

public class MapRenderSystem extends EntitySystem {

    public static final ComponentMapper<MapLayerComponent> mapLayer = ComponentMapper.getFor(MapLayerComponent.class);

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private Engine engine;
    private OrthographicCamera camera;

    public MapRenderSystem(Path mapPath, OrthographicCamera camera, Engine engine) {
        loadMap(mapPath);
        initMapLayerEntities();
        this.camera = camera;
        this.renderer = new OrthogonalTiledMapRenderer(map);
        this.engine = engine;
        this.renderer.setView(this.camera);
    }

    @Override
    public void update(float deltaTime) {
        // Render each layer
        camera.update();
        for (Entity entity : engine.getEntitiesFor(Family.all(MapLayerComponent.class).get())) {
            MapLayerComponent mapLayer = MapRenderSystem.mapLayer.get(entity);
            TiledMapTileLayer layer = mapLayer.mapLayer;
            renderer.renderTileLayer(layer);
        }
    }

    private void loadMap(Path mapPath) {
        map = new TmxMapLoader().load(mapPath.toString());
        renderer.setMap(map);
    }

    private void initMapLayerEntities() {
        // Create entities for each layer
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(i);
            engine.addEntity(new Entity().add(new MapLayerComponent(layer)));
        }
    }


}
