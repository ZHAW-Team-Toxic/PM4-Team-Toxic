package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.zhaw.frontier.utils.QueueAnimation;

import java.util.LinkedList;
import java.util.Queue;

public class AnimationQueueComponent implements Component {

    public Queue<QueueAnimation> queue = new LinkedList<>();
}
