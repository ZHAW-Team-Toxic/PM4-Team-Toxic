package com.zhaw.frontier.audio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.ashley.core.Engine;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the SoundSystem class.
 */
public class SoundSystemTest {

    @Test
    public void testPlayClick() {
        Engine engine = new Engine();
        SoundSystem soundSystem = new SoundSystem();

        engine.addSystem(soundSystem);

        assertNotNull(engine.getSystem(SoundSystem.class));
    }

    @Test
    public void playClick_callsPlayOnSound() {
        TestableSoundSystem soundSystem = new TestableSoundSystem();
        MockSound mockSound = new MockSound();

        soundSystem.setClickSound(mockSound);
        soundSystem.playClick();

        assertTrue(mockSound.played, "Expected click sound to be played");
    }
}

// → MockSound-Klasse, um zu prüfen, ob `play()` aufgerufen wird
class MockSound implements com.badlogic.gdx.audio.Sound {

    public boolean played = false; // → Wird true, sobald eine der play()-Methoden aufgerufen wird

    @Override
    public long play() {
        played = true;
        return 1;
    } // → überschreibt play() – merkt sich Aufruf

    @Override
    public long play(float volume) {
        played = true;
        return 1;
    } // → wird ebenfalls für andere Fälle aufgerufen

    @Override
    public long play(float volume, float pitch, float pan) {
        played = true;
        return 1;
    }

    // → Ab hier: Nicht nötig für deinen Test, aber Pflichtimplementierungen aus dem Interface

    @Override
    public long loop() {
        return 0;
    } // → Muss existieren, wird aber nicht genutzt

    @Override
    public long loop(float volume) {
        return 0;
    }

    @Override
    public long loop(float volume, float pitch, float pan) {
        return 0;
    }

    @Override
    public void stop() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {}

    @Override
    public void stop(long soundId) {}

    @Override
    public void pause(long soundId) {}

    @Override
    public void resume(long soundId) {}

    @Override
    public void setLooping(long soundId, boolean looping) {}

    @Override
    public void setPitch(long soundId, float pitch) {}

    @Override
    public void setVolume(long soundId, float volume) {}

    @Override
    public void setPan(long soundId, float pan, float volume) {}
}

class TestableSoundSystem extends SoundSystem {

    public void setClickSound(com.badlogic.gdx.audio.Sound sound) {
        this.clickSound = sound;
    }
}
