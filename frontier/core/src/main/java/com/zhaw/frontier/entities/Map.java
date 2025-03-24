package com.zhaw.frontier.entities;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.DecorationLayerComponent;
import com.zhaw.frontier.components.map.ResourceLayerComponent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Map {

    /**
     *
     * @param engine
     * @return
     */
    public static Entity createDefaultMap(Engine engine) {
        List<Component> components = new ArrayList<>();
        components.add(new BottomLayerComponent());
        components.add(new ResourceLayerComponent());
        components.add(new DecorationLayerComponent());

        return EntityFactory.buildEntity(engine, components);
    }
}
