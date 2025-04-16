package com.zhaw.frontier.savegame;

import com.zhaw.frontier.components.WallPieceComponent.WallPiece;
import java.util.HashMap;

public class EntityData {

    public String entityType;
    public Float x, y;
    public Integer maxHealth;
    public Integer currentHealth;
    public Float damage, range, speed, cooldown;
    public HashMap<String, Integer> inventory = new HashMap<>();
    public String resourceType;
    public Integer countOfAdjacentResources;
    public WallPiece wallPieceType;
}
