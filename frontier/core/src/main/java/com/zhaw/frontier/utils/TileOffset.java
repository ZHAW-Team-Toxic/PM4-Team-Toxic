package com.zhaw.frontier.utils;

public record TileOffset(int x, int y) {
    /**
     * Creates a new TileOffset with the given x and y values.
     *
     * @param x the x value
     * @param y the y value
     */
    public TileOffset(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
