package com.wizzardo.neon.enemy;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.ui.Picture;

/**
 * Created by wizzardo on 12.03.16.
 */
public class EnemyControl extends AbstractControl {
    protected Vector3f velocity;
    protected long spawnTime;
    protected boolean active = false;

    public EnemyControl() {
        velocity = new Vector3f(0, 0, 0);
        spawnTime = System.currentTimeMillis();
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (active) {
            onUpdate(tpf);
        } else {
            // handle the "active"-status
            long dif = System.currentTimeMillis() - spawnTime;
            if (dif >= 1000f) {
                active = true;
            }

            ColorRGBA color = new ColorRGBA(1, 1, 1, dif / 1000f);
            Node spatialNode = (Node) spatial;
            Picture pic = (Picture) spatialNode.getChild("texture");
            pic.getMaterial().setColor("Color", color);
        }
    }

    protected void onUpdate(float tpf) {

    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {

    }

    public boolean isActive() {
        return active;
    }
}
