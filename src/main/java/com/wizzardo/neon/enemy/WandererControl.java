package com.wizzardo.neon.enemy;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.ui.Picture;
import com.wizzardo.neon.App;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by wizzardo on 10.03.16.
 */
public class WandererControl extends AbstractControl {
    private int screenWidth, screenHeight;

    private Vector3f velocity;
    private float directionAngle;
    private long spawnTime;

    public WandererControl(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        velocity = new Vector3f();
        directionAngle = ThreadLocalRandom.current().nextFloat() * FastMath.PI * 2f;
        spawnTime = System.currentTimeMillis();
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (spatial.getUserData("active")) {
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
        } else {
            // handle the "active"-status
            long dif = System.currentTimeMillis() - spawnTime;
            if (dif >= 1000f) {
                spatial.setUserData("active", true);
            }

            ColorRGBA color = new ColorRGBA(1, 1, 1, dif / 1000f);
            Node spatialNode = (Node) spatial;
            Picture pic = (Picture) spatialNode.getChild("Wanderer");
            pic.getMaterial().setColor("Color", color);
        }
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
    }

}
