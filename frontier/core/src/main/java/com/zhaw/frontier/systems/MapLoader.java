package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.DecorationLayerComponent;
import com.zhaw.frontier.components.map.ResourceLayerComponent;
import com.zhaw.frontier.entities.Map;
import com.zhaw.frontier.exceptions.MapLoadingException;
import lombok.Getter;

import java.nio.file.Path;

/**
 *
 */
public class MapLoader {

    private static MapLoader instance;
    private static AssetManager assetManager;
    private static Path mapPath;

    @Getter
    private TiledMap map;
    @Getter
    private Entity mapEntity;

    /**
     *
     */
    public MapLoader() {}

    /**
     *
     */
    public static MapLoader getInstance() {
        if (instance == null) {
            instance = new MapLoader();
        }
        return instance;
    }

    /**
     *
     */
    public void loadMap(AssetManager assetManager, Path mapPath) throws MapLoadingException {
        MapLoader.assetManager = assetManager;
        MapLoader.mapPath = mapPath;
        assetManager.setLoader(TiledMap.class, new TmxMapLoader());
        try {
            MapLoader.assetManager.load(mapPath.toString(), TiledMap.class);
        } catch (Exception e) {
            throw new MapLoadingException("Error loading map");
        }
    }

    /**
     *
     */
    public void initMapLayerEntities(Engine engine) {
        map = assetManager.get(mapPath.toString(), TiledMap.class);
        mapEntity = Map.createDefaultMap(engine);
        mapEntity.getComponent(BottomLayerComponent.class).bottomLayer = (TiledMapTileLayer) map.getLayers().get(0);
        mapEntity.getComponent(DecorationLayerComponent.class).decorationLayer = (TiledMapTileLayer) map.getLayers().get(1);
        mapEntity.getComponent(ResourceLayerComponent.class).resourceLayer = (TiledMapTileLayer) map.getLayers().get(2);
        engine.addEntity(mapEntity);
    }
}
