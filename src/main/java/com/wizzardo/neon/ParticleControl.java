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
    private Vector3f velocity;
    private float lifespan;
    private long spawnTime;
    private ColorRGBA color;

    public ParticleControl(Vector3f velocity, float lifespan, ColorRGBA color) {
        this.velocity = velocity;
        this.lifespan = lifespan;
        this.color = color;
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
        // rotation
        if (velocity != Vector3f.ZERO) {
            spatial.rotateUpTo(velocity.normalize());
            spatial.rotate(0, 0, FastMath.PI / 2f);
        }
        // scaling and alpha
        float speed = velocity.length();
        long difTime = System.currentTimeMillis() - spawnTime;
        float percentLife = 1 - difTime / lifespan;
        float alpha = lesserValue(1.5f, lesserValue(percentLife * 2, speed));
        alpha *= alpha;
        setAlpha(alpha);
        spatial.setLocalScale(0.3f + lesserValue(lesserValue(1.5f, 0.02f * speed + 0.1f), alpha));
        spatial.scale(0.65f);
        // is particle expired?
        if (difTime > lifespan) {
            spatial.removeFromParent();
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    private float lesserValue(float a, float b) {
        return a < b ? a : b;
    }

    private void setAlpha(float alpha) {
        color.set(color.r, color.g, color.b, alpha);
        Node spatialNode = (Node) spatial;
        Picture pic = (Picture) spatialNode.getChild("texture");
        pic.getMaterial().setColor("Color", color);
    }
}