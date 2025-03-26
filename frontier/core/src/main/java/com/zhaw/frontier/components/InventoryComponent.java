package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.zhaw.frontier.components.map.TiledPropertiesEnum;
import java.util.HashMap;
import java.util.Map;

public class InventoryComponent implements Component {

    public Map<TiledPropertiesEnum, Integer> resources = new HashMap<>();

    public InventoryComponent() {
        for (TiledPropertiesEnum resourceType : TiledPropertiesEnum.values()) {
            resources.put(resourceType, 0);
        }
    }
}
