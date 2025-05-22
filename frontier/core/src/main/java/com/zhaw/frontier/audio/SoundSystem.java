package com.zhaw.frontier.audio;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Handles game sound effects and background music using libGDX audio system.
 *
 * - Loads and plays background music on loop
 * - Provides method to trigger click sounds
 */

public class SoundSystem extends EntitySystem {

    public SoundSystem() {
        Gdx.app.debug("SoundSystem", "initialized");
    }

    Sound clickSound;
    private Music backgroundMusic;

    /**
     * Called when the system is added to the Ashley Engine.
     * Initializes audio assets and starts background music.
     */
    @Override
    public void addedToEngine(Engine engine) {
        // Lade Sounds
        clickSound = Gdx.audio.newSound(Gdx.files.internal("audio/click.mp3"));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/background.mp3"));

        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.3f);
        backgroundMusic.play();

        Gdx.app.debug("SoundSystem", "ADDED to engine: " + this);
    }

    /**
     * Plays a UI click sound effect.
     */
    public void playClick() {
        clickSound.play(1.0f);
    }

    /**
     * Called when the system is removed from the Ashley Engine.
     * Disposes all loaded audio resources.
     */
    @Override
    public void removedFromEngine(Engine engine) {
        clickSound.dispose();
        backgroundMusic.stop();
        backgroundMusic.dispose();
        Gdx.app.debug("SoundSystem", "REMOVED from engine: " + this);
    }
}
