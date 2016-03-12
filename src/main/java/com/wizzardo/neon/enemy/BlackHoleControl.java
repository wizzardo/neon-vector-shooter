package com.wizzardo.neon.enemy;

import com.jme3.math.Vector3f;

/**
 * Created by wizzardo on 13.03.16.
 */
public class BlackHoleControl extends EnemyControl {
    private int hitpoints;

    public BlackHoleControl() {
        hitpoints = 10;
    }

    @Override
    protected void onUpdate(float tpf) {
    }

    public void wasShot() {
        hitpoints--;
    }

    public boolean isDead() {
        return hitpoints <= 0;
    }
}