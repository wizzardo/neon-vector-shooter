package com.wizzardo.neon.enemy;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * Created by wizzardo on 10.03.16.
 */
public class SeekerControl extends EnemyControl {
    private Spatial player;

    public SeekerControl(Spatial player) {
        this.player = player;
    }

    @Override
    protected void onUpdate(float tpf) {
        //translate the seeker
        Vector3f playerDirection = player.getLocalTranslation().subtract(spatial.getLocalTranslation());
        playerDirection.normalizeLocal();
        playerDirection.multLocal(1000f);
        velocity.addLocal(playerDirection);
        velocity.multLocal(0.8f);
        spatial.move(velocity.mult(tpf * 0.1f));

        // rotate the seeker
        if (velocity != Vector3f.ZERO) {
            spatial.rotateUpTo(velocity.normalize());
            spatial.rotate(0, 0, FastMath.PI / 2f);
        }
    }

    @Override
    public int getPoints() {
        return 2;
    }
}
