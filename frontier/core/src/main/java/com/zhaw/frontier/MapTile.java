package com.zhaw.frontier;

import com.badlogic.gdx.math.Vector2;
import lombok.*;

public class MapTile {

    private final Vector2 position;
    private final boolean isWalkable;
    private final boolean isSpawn;
    @Setter @Getter
    private boolean isOccupied;
    @Setter @Getter
    private boolean isBuildable;

    private AssetWrapper asset;

    public MapTile(Vector2 position, boolean isWalkable, boolean isSpawn, AssetWrapper asset) {
        this.position = position;
        this.isWalkable = isWalkable;
        this.isSpawn = isSpawn;
        this.asset = asset;
    }

    public MapTile(Vector2 position, boolean isWalkable, boolean isOccupied, boolean isSpawn, boolean isBuildable, AssetWrapper asset) {
        this.position = position;
        this.isWalkable = isWalkable;
        this.isOccupied = isOccupied;
        this.isSpawn = isSpawn;
        this.isBuildable = isBuildable;
        this.asset = asset;
    }


}
