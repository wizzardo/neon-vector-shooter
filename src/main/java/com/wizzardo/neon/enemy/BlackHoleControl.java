package com.wizzardo.neon.enemy;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.wizzardo.neon.App;
import com.wizzardo.neon.ParticleManager;

import java.util.Random;

/**
 * Created by wizzardo on 13.03.16.
 */
public class BlackHoleControl extends EnemyControl {
    private ParticleManager particleManager;
    private int hitpoints;
    private long lastSprayTime;
    private float sprayAngle;
    private Random rand;

    public BlackHoleControl(ParticleManager particleManager) {
        this.particleManager = particleManager;
        hitpoints = 10;
        rand = new Random();
    }

    @Override
    protected void onUpdate(float tpf) {
        long sprayDif = System.currentTimeMillis() - lastSprayTime;
        if ((System.currentTimeMillis() / 250) % 2 == 0 && sprayDif > 20) {
            lastSprayTime = System.currentTimeMillis();

            Vector3f sprayVel = App.getVectorFromAngle(sprayAngle).multLocal(rand.nextFloat() * 3 + 6);
            Vector3f randVec = App.getVectorFromAngle(rand.nextFloat() * FastMath.PI * 2);
            randVec.multLocal(4 + rand.nextFloat() * 4);
            Vector3f position = spatial.getLocalTranslation().add(sprayVel.mult(2f)).addLocal(randVec);

            particleManager.sprayParticle(position, sprayVel.multLocal(30f));
        }
        sprayAngle -= FastMath.PI * tpf / 10f;
    }

    public void wasShot() {
        hitpoints--;
    }

    public boolean isDead() {
        return hitpoints <= 0;
    }

    @Override
    public int getPoints() {
        return 10;
    }
}