package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import java.util.LinkedList;
import java.util.Queue;

public class AnimationQueueComponent implements Component {

    public Queue<ConditionalAnimationComponent> queue = new LinkedList<>();
}
