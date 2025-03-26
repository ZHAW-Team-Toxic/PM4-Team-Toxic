package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

public class ResourceCollectionRangeComponent implements Component {

    public int range = 1;
    public int tilesInRange = 0;
    public boolean isCalculated = false;
}
