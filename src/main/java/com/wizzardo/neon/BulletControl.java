package com.wizzardo.neon;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * Created by wizzardo on 08.03.16.
 */
public class BulletControl extends AbstractControl {
    private ParticleManager particleManager;
    private int screenWidth, screenHeight;
    private float speed = 1100f;
    private float rotation;
    public Vector3f direction;

    public BulletControl(Vector3f direction, int screenWidth, int screenHeight, ParticleManager particleManager) {
        this.direction = direction;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.particleManager = particleManager;
    }

    @Override
    protected void controlUpdate(float tpf) {
//        movement
        spatial.move(direction.mult(speed * tpf));

//        rotation
        float actualRotation = App.getAngleFromVector(direction);
        if (actualRotation != rotation) {
            spatial.rotate(0, 0, actualRotation - rotation);
            rotation = actualRotation;
        }

//        check boundaries
        Vector3f loc = spatial.getLocalTranslation();
        if (loc.x > screenWidth || loc.y > screenHeight || loc.x < 0 || loc.y < 0) {
            particleManager.bulletExplosion(loc);
            spatial.removeFromParent();
        }
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
    }

    public void applyGravity(Vector3f gravity) {
        direction.addLocal(gravity);
    }
}
