package com.zhaw.frontier.utils;

/**
 * Represents a queued animation that is managed by the animation queue system.
 * <p>
 * Can be used for both enemy and building animations. Includes timing and loop control.
 * </p>
 */
public class QueueAnimation {

    /** The type of animation to play (can be an enemy or building animation enum). */
    public Enum<?> animationType;

    /** The remaining duration this animation should play (in seconds). */
    public float timeLeft;

    /** Whether this animation should loop indefinitely. */
    public boolean loop;
}
