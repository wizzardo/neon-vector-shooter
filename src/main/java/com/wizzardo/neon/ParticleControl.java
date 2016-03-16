package com.wizzardo.neon;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.ui.Picture;

/**
 * Created by wizzardo on 14.03.16.
 */
public class ParticleControl extends AbstractControl {
    private int screenWidth, screenHeight;
    private Vector3f velocity;
    private float lifespan;
    private long spawnTime;
    private ColorRGBA color;

    public ParticleControl(Vector3f velocity, float lifespan, ColorRGBA color, int screenWidth, int screenHeight) {
        this.velocity = velocity;
        this.lifespan = lifespan;
        this.color = color;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        spawnTime = System.currentTimeMillis();
    }

    @Override
    protected void controlUpdate(float tpf) {
        // movement
        spatial.move(velocity.mult(tpf * 3f));
        velocity.multLocal(1 - 3f * tpf);
        if (Math.abs(velocity.x) + Math.abs(velocity.y) < 0.001f) {
            velocity = Vector3f.ZERO;
        }
        Vector3f loc = spatial.getLocalTranslation();
        if (loc.x < 0) {
            velocity.x = Math.abs(velocity.x);
        } else if (loc.x > screenWidth) {
            velocity.x = -Math.abs(velocity.x);
        }
        if (loc.z < 0) {
            velocity.y = Math.abs(velocity.y);
        } else if (loc.y > screenHeight) {
            velocity.y = -Math.abs(velocity.y);
        }
        // rotation
        if (velocity != Vector3f.ZERO) {
            spatial.rotateUpTo(velocity.normalize());
            spatial.rotate(0, 0, FastMath.PI / 2f);
        }
        // scaling and alpha
        float speed = velocity.length();
        long difTime = System.currentTimeMillis() - spawnTime;
        float percentLife = 1 - difTime / lifespan;
        float alpha = min(1.5f, min(percentLife * 2, speed));
        alpha *= alpha;
        setAlpha(alpha);
        spatial.setLocalScale(0.3f + min(min(1.5f, 0.02f * speed + 0.1f), alpha));
        spatial.scale(0.65f);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    private float min(float a, float b) {
        return a < b ? a : b;
    }

    private void setAlpha(float alpha) {
        color.set(color.r, color.g, color.b, alpha);
        Node spatialNode = (Node) spatial;
        Picture pic = (Picture) spatialNode.getChild("texture");
        pic.getMaterial().setColor("Color", color);
    }

    public void applyGravity(Vector3f gravity, float distance) {
        Vector3f additionalVelocity = gravity.multLocal(1000f / (distance * distance + 10000f));
        velocity.addLocal(additionalVelocity);

        if (distance < 400) {
            additionalVelocity.set(gravity.y, -gravity.x, 0).multLocal(3f / (distance + 100));
            velocity.addLocal(additionalVelocity);
        }
    }
}