package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

/**
 * Marker component that designates an {@link com.badlogic.ashley.core.Entity} as a Headquarters (HQ).
 *
 * <p>This component is typically used to identify the main base or command center for a team or player.</p>
 *
 * <p>It contains no data and serves as a tag for systems that need to detect or process HQ-related logic.</p>
 */
public class HQComponent implements Component {}
