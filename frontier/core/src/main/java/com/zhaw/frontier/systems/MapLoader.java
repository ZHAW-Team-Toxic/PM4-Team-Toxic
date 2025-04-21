package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.DecorationLayerComponent;
import com.zhaw.frontier.components.map.ResourceLayerComponent;
import com.zhaw.frontier.entityFactories.MapFactory;
import com.zhaw.frontier.exceptions.MapLoadingException;
import java.nio.file.Path;
import lombok.Getter;

/**
 * The {@code MapLoader} class is responsible for loading a tiled map using an
 * {@link AssetManager} and initializing the corresponding map layer entities
 * in the Ashley engine.
 * <p>
 * It follows a singleton pattern to ensure only one instance of the map loader
 * is used.
 * </p>
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
     * Constructs a new {@code MapLoader}. This constructor is private to enforce
     * the singleton pattern.
     */
    public MapLoader() {
        // Default constructor
    }

    /**
     * Returns the singleton instance of {@code MapLoader}.
     *
     * @return the singleton instance.
     */
    public static MapLoader getInstance() {
        if (instance == null) {
            instance = new MapLoader();
        }
        return instance;
    }

    /**
     * Loads a tiled map from the specified path using the provided
     * {@link AssetManager}.
     * <p>
     * The {@link AssetManager} is configured with a {@link TmxMapLoader} to load
     * {@link TiledMap}
     * files. If any error occurs during loading, a {@link MapLoadingException} is
     * thrown.
     * </p>
     *
     * @param assetManager the {@link AssetManager} used to load assets.
     * @param mapPath      the {@link Path} to the map file.
     * @throws MapLoadingException if an error occurs during map loading.
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
     * Initializes the map layer entities in the given {@link Engine}.
     * <p>
     * This method retrieves the loaded {@link TiledMap} from the
     * {@link AssetManager} and creates a
     * map entity using the {@link MapFactory}. It then assigns the bottom,
     * decoration, and resource layers
     * to the corresponding components of the map entity before adding it to the
     * engine.
     * </p>
     *
     * @param engine the {@link Engine} that manages entities.
     */
    public void initMapLayerEntities(Engine engine) {
        Gdx.app.debug("MapLoader", "Initializing Map Layer Entities.");
        map = assetManager.get(mapPath.toString(), TiledMap.class);
        mapEntity = MapFactory.createDefaultMap(engine);
        mapEntity.getComponent(BottomLayerComponent.class).bottomLayer =
        (TiledMapTileLayer) map.getLayers().get(0);
        mapEntity.getComponent(DecorationLayerComponent.class).decorationLayer =
        (TiledMapTileLayer) map.getLayers().get(1);
        mapEntity.getComponent(ResourceLayerComponent.class).resourceLayer =
        (TiledMapTileLayer) map.getLayers().get(2);
        engine.addEntity(mapEntity);
        Gdx.app.debug(
            "MapLoader",
            "Layer " +
            MapLoader
                .getInstance()
                .getMapEntity()
                .getComponent(BottomLayerComponent.class)
                .bottomLayer.getName() +
            " loaded."
        );
        Gdx.app.debug(
            "MapLoader",
            "Layer " +
            MapLoader
                .getInstance()
                .getMapEntity()
                .getComponent(DecorationLayerComponent.class)
                .decorationLayer.getName() +
            " loaded."
        );
        Gdx.app.debug(
            "MapLoader",
            "Layer " +
            MapLoader
                .getInstance()
                .getMapEntity()
                .getComponent(ResourceLayerComponent.class)
                .resourceLayer.getName() +
            " loaded."
        );
    }
}
