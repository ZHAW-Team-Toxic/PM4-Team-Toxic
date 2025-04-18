package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

public class EntityTypeComponent implements Component {

    public EntityTypeComponent(EntityType type) {
        this.type = type;
    }

    public enum EntityType {
        INVENTORY,
        HQ,
        BALLISTA_TOWER,
        WOOD_WALL,
        IRON_WALL,
        STONE_WALL,
        RESOURCE_BUILDING,
    }

    public EntityType type;
}
