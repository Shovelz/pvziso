package com.pvz.game.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;

public class SoundManager {
    private static SoundManager instance;
    private final HashMap<String, Sound> sounds = new HashMap<>();
    private float volume = 0.5f;
    private boolean muted = false;

    // Libgdx doesn't provide a callback when a sound is finished, and I don't want to estimate the sound duration, as then bugs can be introduced with that
    // So I can't pause sounds when pausing the game and resume them. When pausing the game all sounds that are playing continue playing. I can prevent new
    // sounds from being made though

    private SoundManager() {
        // Preload sounds here
//        load("zombie_groan", "sounds/zombie_groan.wav");
//        load("shot", "sounds/shot.wav");
        load("game_over", "sounds/scream.ogg");
        load("zombie_groan1", "sounds/groan1.ogg");
        load("zombie_groan2", "sounds/groan2.ogg");
        load("zombie_groan3", "sounds/groan3.ogg");
        load("zombie_groan4", "sounds/groan4.ogg");
        load("zombie_groan5", "sounds/groan5.ogg");
        load("zombie_groan6", "sounds/groan6.ogg");
        load("cherrybomb", "sounds/cherrybomb.ogg");
        load("plant", "sounds/plant.ogg");
        load("tap", "sounds/tap.ogg");
        load("pause", "sounds/pause.ogg");
        load("seed_lift", "sounds/seedlift.ogg");
        load("splat1", "sounds/splat1.ogg");
        load("splat2", "sounds/splat2.ogg");
        load("splat3", "sounds/splat3.ogg");
        load("shovel", "sounds/shovel.ogg");
        load("points", "sounds/points.ogg");
        load("win_music", "sounds/winmusic.ogg");
        load("lose_music", "sounds/losemusic.ogg");
        load("buzzer", "sounds/buzzer.ogg");
        load("readysetplant", "sounds/readysetplant.ogg");
        load("pea_shoot1", "sounds/throw.ogg");
        load("pea_shoot2", "sounds/throw2.ogg");
        load("mower", "sounds/lawnmower.ogg");
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void load(String key, String path) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
        sounds.put(key, sound);
    }

    public void play(String key) {
        if (!muted && sounds.containsKey(key)) {
            sounds.get(key).play(volume);
        }
    }

    public void setVolume(float vol) {
        volume = vol;
    }

    public void mute(boolean mute) {
        muted = mute;
    }

    public void dispose() {
        for (Sound s : sounds.values()) {
            s.dispose();
        }
        sounds.clear();
    }
}
