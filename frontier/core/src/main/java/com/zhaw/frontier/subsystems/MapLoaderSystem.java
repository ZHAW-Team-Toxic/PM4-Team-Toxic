package com.zhaw.frontier.subsystems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.DecorationLayerComponent;
import com.zhaw.frontier.components.map.ResourceLayerComponent;
import com.zhaw.frontier.entities.Map;
import com.zhaw.frontier.exceptions.MapLoadingException;
import java.nio.file.Path;
import lombok.Getter;

/**
 * The {@code MapLoaderSystem} is responsible for loading a TiledMap from a file and
 * creating entities for each layer of the map. It converts map layers into entities
 * with their respective components for further processing in the game engine.
 * <p>
 * This system follows the singleton pattern to ensure only one instance is used
 * throughout the application.
 * </p>
 *
 * @see TiledMap
 * @see AssetManager
 * @see Engine
 * @see BottomLayerComponent
 * @see DecorationLayerComponent
 * @see ResourceLayerComponent
 * @see Map
 * @see MapLoadingException
 */
public class MapLoaderSystem {

    private static MapLoaderSystem instance;
    private static AssetManager assetManager;
    private static Path mapPath;

    @Getter
    private TiledMap map;

    @Getter
    private Map mapEntity;

    /**
     * Constructs a new {@code MapLoaderSystem}. This constructor initializes the system.
     * <p>
     * Note: The actual loading of the map and initialization of the layer entities is
     * performed by {@link #loadMap(AssetManager, Path)} and {@link #initMapLayerEntities(Engine)}.
     * </p>
     *
     * @throws MapLoadingException if an error occurs while loading the map.
     */
    public MapLoaderSystem() {}

    /**
     * Returns the singleton instance of {@code MapLoaderSystem}. If no instance exists,
     * a new one is created.
     *
     * @return the single instance of {@code MapLoaderSystem}.
     */
    public static MapLoaderSystem getInstance() {
        if (instance == null) {
            instance = new MapLoaderSystem();
        }
        return instance;
    }

    /**
     * Loads a {@code TiledMap} from the specified file path using the provided {@code AssetManager}.
     * <p>
     * The method sets the loader for the {@code TiledMap} to a {@code TmxMapLoader} and attempts
     * to load the map. If any error occurs during the loading process, a {@link MapLoadingException}
     * is thrown.
     * </p>
     *
     * @param assetManager the {@code AssetManager} used for loading the map asset.
     * @param mapPath      the file system path to the map file.
     * @throws MapLoadingException if the map cannot be loaded due to an error.
     */
    public void loadMap(AssetManager assetManager, Path mapPath) throws MapLoadingException {
        MapLoaderSystem.assetManager = assetManager;
        MapLoaderSystem.mapPath = mapPath;
        assetManager.setLoader(TiledMap.class, new TmxMapLoader());
        try {
            MapLoaderSystem.assetManager.load(mapPath.toString(), TiledMap.class);
        } catch (Exception e) {
            throw new MapLoadingException("Error loading map");
        }
    }

    /**
     * Initializes the map layer entities by retrieving the loaded {@code TiledMap} and creating
     * the corresponding map entity with layer components.
     * <p>
     * This method extracts individual layers from the {@code TiledMap} and assigns them to the
     * map entity as follows:
     * <ul>
     *   <li>Bottom layer via {@link BottomLayerComponent}</li>
     *   <li>Decoration layer via {@link DecorationLayerComponent}</li>
     *   <li>Resource layer via {@link ResourceLayerComponent}</li>
     * </ul>
     * Finally, the map entity is added to the provided game {@code Engine}.
     * </p>
     *
     * @param engine the game engine to which the map entity will be added.
     */
    public void initMapLayerEntities(Engine engine) {
        // Retrieve the loaded map from the AssetManager.
        map = assetManager.get(mapPath.toString(), TiledMap.class);

        // Create a new map entity and add layer components.
        mapEntity = new com.zhaw.frontier.entities.Map();
        mapEntity
            .add(new BottomLayerComponent((TiledMapTileLayer) map.getLayers().get(0)))
            .add(new DecorationLayerComponent((TiledMapTileLayer) map.getLayers().get(1)))
            .add(new ResourceLayerComponent((TiledMapTileLayer) map.getLayers().get(2)));

        // Add the entity to the game engine.
        engine.addEntity(mapEntity);
    }
}
