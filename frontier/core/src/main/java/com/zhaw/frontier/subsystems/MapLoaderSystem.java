package com.zhaw.frontier.subsystems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.DecorationLayerComponent;
import com.zhaw.frontier.components.map.ResourceLayerComponent;
import com.zhaw.frontier.exceptions.MapLoadingException;
import java.nio.file.Path;
import lombok.Getter;

/**
 * System for loading the map. Creates entities for each layer of the map.
 * The map is loaded from a file.
 */
public class MapLoaderSystem {

    private final Path mapPath;
    private final Engine engine;

    @Getter
    private TiledMap map;

    @Getter
    private com.zhaw.frontier.entities.Map mapEntity;

    /**
     * Constructor. Initializes the map loader system.
     * Loads the map from the given path and creates entities for each layer.
     * @param mapPath The path to the map file.
     * @param engine The engine to be used.
     * @throws MapLoadingException If an error occurs while loading the map.
     */
    public MapLoaderSystem(Path mapPath, Engine engine) throws MapLoadingException {
        this.mapPath = mapPath;
        this.engine = engine;
        loadMap();
        initMapLayerEntities();
    }

    private void loadMap() throws MapLoadingException {
        try {
            map = new TmxMapLoader().load(mapPath.toString());
        } catch (Exception e) {
            throw new MapLoadingException("Error loading map: " + mapPath.toString());
        }
    }

    private void initMapLayerEntities() {
        // Create entities for each layer
        mapEntity = new com.zhaw.frontier.entities.Map();
        mapEntity
            .add(new BottomLayerComponent((TiledMapTileLayer) map.getLayers().get(0)))
            .add(new DecorationLayerComponent((TiledMapTileLayer) map.getLayers().get(1)))
            .add(new ResourceLayerComponent((TiledMapTileLayer) map.getLayers().get(2)));
        engine.addEntity(mapEntity);
    }
}
