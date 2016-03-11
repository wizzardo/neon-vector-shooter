package com.wizzardo.neon;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by wizzardo on 11.03.16.
 */
public class SoundManager {

    private AudioNode music;
    private AudioNode[] shots;
    private AudioNode[] explosions;
    private AudioNode[] spawns;

    private AssetManager assetManager;

    public SoundManager(AssetManager assetManager) {
        this.assetManager = assetManager;
        shots = new AudioNode[4];
        explosions = new AudioNode[8];
        spawns = new AudioNode[8];

        loadSounds();
    }

    private void loadSounds() {
        music = new AudioNode(assetManager, "Sounds/Music.ogg");
        music.setPositional(false);
        music.setReverbEnabled(false);
        music.setLooping(true);

        for (int i = 0; i < shots.length; i++) {
            shots[i] = new AudioNode(assetManager, String.format("Sounds/shoot-%02d.wav", i + 1));
            shots[i].setPositional(false);
            shots[i].setReverbEnabled(false);
            shots[i].setLooping(false);
        }

        for (int i = 0; i < explosions.length; i++) {
            explosions[i] = new AudioNode(assetManager, String.format("Sounds/explosion-%02d.wav", i + 1));
            explosions[i].setPositional(false);
            explosions[i].setReverbEnabled(false);
            explosions[i].setLooping(false);
        }

        for (int i = 0; i < spawns.length; i++) {
            spawns[i] = new AudioNode(assetManager, String.format("Sounds/spawn-%02d.wav", i + 1));
            spawns[i].setPositional(false);
            spawns[i].setReverbEnabled(false);
            spawns[i].setLooping(false);
        }
    }

    public void startMusic() {
        music.play();
    }

    public void shoot() {
        shots[ThreadLocalRandom.current().nextInt(shots.length)].playInstance();
    }

    public void explosion() {
        explosions[ThreadLocalRandom.current().nextInt(explosions.length)].playInstance();
    }

    public void spawn() {
        spawns[ThreadLocalRandom.current().nextInt(spawns.length)].playInstance();
    }
}
