package com.zhaw.frontier.configs;

/**
 * A static configuration class containing application-wide constants used throughout the game.
 * <p>
 * These properties include rendering values, movement and animation durations,
 * and pathfinding or avoidance tuning parameters.
 * </p>
 */
public class AppProperties {

    /** Private constructor to prevent instantiation. */
    private AppProperties() {}

    /** The name of the application. */
    public static final String APP_NAME = "Frontier";

    /** The size of one tile in pixels. */
    public static final int TILE_SIZE = 16;

    /** The radius (in tiles) within which units should avoid one another. */
    public static final float AVOID_RADIUS = 0.6f;

    /** The strength of the avoidance force applied between units. */
    public static final float AVOID_STRENGTH = 1f;

    /** Duration of the orc attack animation frame (in seconds). */
    public static final float ORC_ATTACK_DURATION = 0.1f;

    /** Duration of the orc idle animation frame (in seconds). */
    public static final float ORC_IDLE_DURATION = 0.1f;

    /** Duration of the orc walk animation frame (in seconds). */
    public static final float ORC_WALK_DURATION = 0.1f;

    /** Duration of each frame in the death animation (in seconds). */
    public static final float DEATH_FRAME_DURATION = 0.1f;

    /**
     * Distance threshold (in tiles) for stopping at a waypoint during path following.
     * If the entity is within this distance from the waypoint, it is considered to have arrived.
     */
    public static final float PATH_FOLLOWER_STOP_TRESHOLD = 0.1f;

    public static final float WORLD_HEIGHT = 64 * 16;
    public static final float WORLD_WIDTH = 64 * 16;

    public static final int WOOD_TOWER_PRICE = 10;

    public static final int WOOD_RESOURCE_BUILDING_PRICE = 2;
    public static final int STONE_RESOURCE_BUILDING_PRICE = 2;
    public static final int IRON_RESOURCE_BUILDING_PRICE = 2;

    public static final int WOOD_WALL_PRICE = 1;
    public static final int STONE_WALL_PRICE = 1;
    public static final int IRON_WALL_PRICE = 1;

    public static final int DEFAULT_PRODUCTION_RATE_HQ = 2;

    public static final int DEFAULT_PRODUCTION_RATE_RESOURCE_BUILDING = 1;

    public static final String RESOURCE_UI_WOOD_LABEL_TEXT = "Wood";
    public static final String RESOURCE_UI_STONE_LABEL_TEXT = "Stone";
    public static final String RESOURCE_UI_IRON_LABEL_TEXT = "Iron";

    public static final String TEXTURE_ATLAS_PATH = "packed/textures.atlas";
    public static final String SKIN_PATH = "skins/skin.json";
}
