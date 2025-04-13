package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

public class EntityTypeComponent implements Component {

    public EntityTypeComponent(EntityType type) {
        this.type = type;
    }

    public enum EntityType { INVENTORY, TOWER, WALL, WOOD_RESOURCE_BUILDING, IDLE_ENEMY, PATROL_ENEMY }
    public EntityType type;
}

