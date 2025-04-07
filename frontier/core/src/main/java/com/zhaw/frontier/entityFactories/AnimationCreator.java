package com.zhaw.frontier.entityFactories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import java.util.EnumMap;

/**
 * Utility class to create animations for entities using AnimationType enums.
 * Supports both enemy and building animations, or any other custom type.
 */
public class AnimationCreator {

    /**
     * Creates a looping animation from an atlas using the given base region name.
     *
     * @param atlas      TextureAtlas to search regions in
     * @param baseName   base name of the region series
     * @param frameCount number of frames (e.g. 4 means baseName_1 to baseName_4)
     * @return Animation<TextureRegion>
     */
    public static Animation<TextureRegion> create(
        TextureAtlas atlas,
        String baseName,
        int frameCount,
        float frameDuration
    ) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= frameCount; i++) {
            TextureRegion region = atlas.findRegion(baseName);
            if (region != null) {
                frames.add(region);
            } else {
                System.err.println("[AnimationCreator] Missing region: " + baseName + "_" + i);
            }
        }
        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    /**
     * Creates a single-frame animation for static visuals.
     */
    public static Animation<TextureRegion> createStatic(
        TextureAtlas atlas,
        String regionName,
        float frameDuration
    ) {
        Array<TextureRegion> frames = new Array<>();
        TextureRegion region = atlas.findRegion(regionName);
        if (region != null) {
            frames.add(region);
        } else {
            System.err.println("[AnimationCreator] Missing region: " + regionName);
        }
        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    /**
     * Bulk-create animations for a given enum type (e.g. WALK_UP, WALK_DOWN, etc.).
     * Assumes atlas uses naming convention like: prefix_enumName_1, _2, ...
     */
    public static <T extends Enum<T>> EnumMap<T, Animation<TextureRegion>> createAll(
        TextureAtlas atlas,
        Class<T> enumClass,
        String prefix,
        int frameCount,
        float frameDuration
    ) {
        EnumMap<T, Animation<TextureRegion>> animations = new EnumMap<>(enumClass);
        for (T type : enumClass.getEnumConstants()) {
            String name = prefix + "_" + type.name().toLowerCase();
            animations.put(type, create(atlas, name, frameCount, frameDuration));
        }
        return animations;
    }

    /**
     * Creates static animation variants for all enum types, assuming atlas region exists.
     */
    public static <T extends Enum<T>> EnumMap<T, Animation<TextureRegion>> createAllStatic(
        TextureAtlas atlas,
        Class<T> enumClass,
        String prefix,
        float frameDuration
    ) {
        EnumMap<T, Animation<TextureRegion>> animations = new EnumMap<>(enumClass);
        for (T type : enumClass.getEnumConstants()) {
            String name = prefix + "_" + type.name().toLowerCase();
            animations.put(type, createStatic(atlas, name, frameDuration));
        }
        return animations;
    }
}
