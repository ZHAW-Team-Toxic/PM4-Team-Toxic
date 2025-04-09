package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.zhaw.frontier.components.RoundAnimationComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.utils.LayeredSprite;
import com.zhaw.frontier.utils.TileOffset;
import java.util.List;

public class RoundAnimationSystem {

    // Leerer Konstruktor – kann erweitert werden, falls du das System in Ashley als echtes System registrieren willst
    public static void updateFrameForRoundComponent(Entity entity) {
        RoundAnimationComponent anim = entity.getComponent(RoundAnimationComponent.class);
        RenderComponent render = entity.getComponent(RenderComponent.class);

        if (anim == null || anim.frames == null || anim.frames.isEmpty() || render == null) return;

        // Kleinste Frame-Anzahl ermitteln (manuell, ohne Streams)
        int minFrameCount = Integer.MAX_VALUE;
        for (Array<TextureRegion> frameArray : anim.frames.values()) {
            if (frameArray != null && frameArray.size < minFrameCount) {
                minFrameCount = frameArray.size;
            }
        }

        // Wenn keine Frames vorhanden, abbrechen
        if (minFrameCount <= 0 || minFrameCount == Integer.MAX_VALUE) return;

        // Frame-Index zyklisch erhöhen
        anim.currentFrameIndex = (anim.currentFrameIndex + 1) % minFrameCount;

        // Über animierte TileOffsets iterieren und Sprites aktualisieren
        for (var entry : anim.frames.entrySet()) {
            TileOffset offset = entry.getKey();
            Array<TextureRegion> frames = entry.getValue();

            if (frames == null || frames.size == 0) continue;

            // Sicherstellen, dass der Index gültig ist
            int clampedIndex = Math.min(anim.currentFrameIndex, frames.size - 1);
            TextureRegion newFrame = frames.get(clampedIndex);

            List<LayeredSprite> layers = render.sprites.get(offset);
            if (layers == null) continue;

            for (LayeredSprite layer : layers) {
                if (layer.zIndex == 10) {
                    layer.region.setRegion(newFrame);
                    break;
                }
            }
        }
    }
}
