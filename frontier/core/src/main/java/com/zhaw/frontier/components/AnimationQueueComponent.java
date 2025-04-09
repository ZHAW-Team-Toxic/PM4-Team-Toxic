package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.zhaw.frontier.utils.QueueAnimation;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a component that manages a queue of animations for an entity.
 */
public class AnimationQueueComponent implements Component {

    /**
     * The queue of animations to be processed.
     */
    public Queue<QueueAnimation> queue = new LinkedList<>();
}
