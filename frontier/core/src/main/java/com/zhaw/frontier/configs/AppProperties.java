package com.zhaw.frontier.configs;

public class AppProperties {

    private AppProperties() {}

    public static final String APP_NAME = "Frontier";
    public static final int TILE_SIZE = 16;
    public static final float AVOID_RADIUS = 1f; // tiles
    public static final float AVOID_STRENGTH = 1f;

    public static final float ORC_ATTACK_DURATION = 0.1f;
    public static final float ORC_IDLE_DURATION = 0.1f;
    public static final float ORC_WALK_DURATION = 0.1f;

    public static final float DEATH_FRAME_DURATION = 0.1f;
}
