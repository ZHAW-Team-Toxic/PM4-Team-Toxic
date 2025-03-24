package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

/**
 * Contains the resource generation rate of an {@link com.badlogic.ashley.core.Entity}
 */
public class ResourceGeneratorComponent implements Component {

    /**
     * The resource generation rate of the entity.
     */
    public int ResourceGenerationRate = 0;
}
