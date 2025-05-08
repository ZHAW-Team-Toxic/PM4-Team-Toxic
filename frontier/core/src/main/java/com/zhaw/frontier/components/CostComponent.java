package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import java.util.HashMap;
import java.util.Map;

/**
 * This component defines how much resources a user must spend to build a
 * building.
 */

public class CostComponent implements Component {

    public Map<ResourceTypeEnum, Integer> resouceCosts = new HashMap<>();

    public CostComponent() {
        resouceCosts.put(ResourceTypeEnum.RESOURCE_TYPE_WOOD, 0);
        resouceCosts.put(ResourceTypeEnum.RESOURCE_TYPE_STONE, 0);
        resouceCosts.put(ResourceTypeEnum.RESOURCE_TYPE_IRON, 0);
    }
}
