package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.zhaw.frontier.components.HQRoundAnimationComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.utils.LayeredSprite;
import com.zhaw.frontier.utils.TileOffset;
import java.util.List;

public class RoundAnimationSystem {

    // Leerer Konstruktor – kann erweitert werden, falls du das System in Ashley als echtes System registrieren willst
    public RoundAnimationSystem() {}

    /**
     * Diese Methode wird manuell aufgerufen (z. B. bei Rundenwechsel),
     * um die animierten HQ-Frames auf das nächste Bild zu schalten.
     */
    public static void updateFrameForRoundComponent(Entity entity) {
        // Komponenten vom Entity holen
        HQRoundAnimationComponent anim = entity.getComponent(HQRoundAnimationComponent.class);
        RenderComponent render = entity.getComponent(RenderComponent.class);

        // Wenn keine Animation oder Render-Komponente vorhanden ist: nichts tun
        if (anim == null || anim.frames.isEmpty() || render == null) return;

        // Referenz-Framearray holen, um die maximale Frameanzahl zu kennen
        Array<TextureRegion> anyFrameArray = anim.frames.values().iterator().next();
        if (anyFrameArray == null || anyFrameArray.isEmpty()) return;

        // Frame-Zähler erhöhen und ggf. auf 0 zurücksetzen (Loop)
        anim.currentFrameIndex++;
        if (anim.currentFrameIndex >= anyFrameArray.size) {
            anim.currentFrameIndex = 0;
        }

        // Über alle animierten Tiles iterieren
        for (var entry : anim.frames.entrySet()) {
            TileOffset offset = entry.getKey(); // Tile-Position (z. B. (1,1))
            Array<TextureRegion> frames = entry.getValue(); // Animationsframes für dieses Tile

            if (frames.isEmpty()) continue;

            // Index begrenzen (nur als Fallback-Sicherheit)
            int clampedIndex = Math.min(anim.currentFrameIndex, frames.size - 1);
            TextureRegion newFrame = frames.get(clampedIndex); // Das neue Bild für dieses Tile

            // LayeredSprite-Liste an dieser Tile-Position holen
            List<LayeredSprite> layers = render.sprites.get(offset);
            if (layers == null) continue;

            // Gesuchten Layer (z. B. der animierte Overlay mit zIndex 10) aktualisieren
            for (LayeredSprite layer : layers) {
                if (layer.zIndex == 10) {
                    // Das Bild im Layer auf das neue Frame setzen (per Referenz, d.h. kein Neuanlegen nötig)
                    layer.region.setRegion(newFrame);
                    break; // Nur der erste passende Layer wird aktualisiert
                }
            }
        }
    }
}
