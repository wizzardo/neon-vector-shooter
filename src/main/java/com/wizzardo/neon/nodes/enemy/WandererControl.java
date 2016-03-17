package com.wizzardo.neon.nodes.enemy;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.wizzardo.neon.App;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by wizzardo on 10.03.16.
 */
public class WandererControl extends EnemyControl {
    private int screenWidth, screenHeight;

    private float directionAngle;

    public WandererControl(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        directionAngle = ThreadLocalRandom.current().nextFloat() * FastMath.PI * 2f;
    }

    @Override
    protected void onUpdate(float tpf) {
        // translate the wanderer
        // change the directionAngle a bit
        directionAngle += (ThreadLocalRandom.current().nextFloat() * 20f - 10f) * tpf;
        Vector3f directionVector = App.getVectorFromAngle(directionAngle);
        directionVector.multLocal(1000f);
        velocity.addLocal(directionVector);

        // decrease the velocity a bit and move the wanderer
        velocity.multLocal(0.8f);
        spatial.move(velocity.mult(tpf * 0.1f));

        // make the wanderer bounce off the screen borders
        Vector3f loc = spatial.getLocalTranslation();
        if (loc.x > screenWidth || loc.y > screenHeight || loc.x < 0 || loc.y < 0) {
            Vector3f newDirectionVector = new Vector3f(screenWidth / 2, screenHeight / 2, 0).subtract(loc);
            directionAngle = App.getAngleFromVector(newDirectionVector);
        }

        // rotate the wanderer
        spatial.rotate(0, 0, tpf * 2);
    }
}
