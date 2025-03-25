package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

import java.util.Map;

public class InventorySystem extends EntitySystem {
    private Engine engine;

    public Map<ResourceType, Integer> playerResources;

    public InventorySystem(Engine engine){
        this.playerResources = Map.of(
            ResourceType.GOLD, 0,
            ResourceType.WOOD, 0,
            ResourceType.STONE, 0
        );

        this.engine = engine;
    }

    public void onTurnEnd() {
        // todo get buildings from engine
        //   -> get their production per turn
        //   -> add production values to hash map for each resourceType
    }
}
