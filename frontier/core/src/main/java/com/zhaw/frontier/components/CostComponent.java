package com.zhaw.frontier.components;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.zhaw.frontier.components.map.ResourceTypeEnum;

public class CostComponent implements Component {

    /**
     */
    public Map<ResourceTypeEnum, Integer> resouceCosts = new HashMap<>();

    /**
     */
    public CostComponent() {
        resouceCosts.put(ResourceTypeEnum.RESOURCE_TYPE_WOOD, 0);
        resouceCosts.put(ResourceTypeEnum.RESOURCE_TYPE_STONE, 0);
        resouceCosts.put(ResourceTypeEnum.RESOURCE_TYPE_IRON, 0);
    }

}
