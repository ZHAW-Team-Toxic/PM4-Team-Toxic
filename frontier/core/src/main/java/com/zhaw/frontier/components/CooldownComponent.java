package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

/**
 * This component keeps track of the cooldown, as long as this component is
 * added to an entity it is not finished.
 */
public class CooldownComponent implements Component {

    public long start, duration;
}
