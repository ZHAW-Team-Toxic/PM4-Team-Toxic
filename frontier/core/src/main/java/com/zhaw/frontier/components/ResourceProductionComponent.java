package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import com.zhaw.frontier.components.map.TiledPropertiesEnum;
import java.util.HashMap;
import java.util.Map;

public class ResourceProductionComponent implements Component {

    public Map<ResourceTypeEnum, Integer> productionRate = new HashMap<>();

    public int countOfAdjacentResources = 0;
}
