package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

/**
 * CurrentTargetComponent tracks a target which is currently being hot at by a
 * tower.
 */

public class CurrentTargetComponent implements Component {

    public Entity target;
}
