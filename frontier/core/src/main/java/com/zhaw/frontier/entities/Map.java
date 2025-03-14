package com.zhaw.frontier.entities;

import com.badlogic.ashley.core.Entity;
import com.zhaw.frontier.components.map.MapLayerComponent;

public class Map extends Entity {

    public Map() {}

    public Map addComponent(MapLayerComponent component) {
        add(component);
        return this;
    }
}
