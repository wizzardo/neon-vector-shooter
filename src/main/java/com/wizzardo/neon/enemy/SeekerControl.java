package com.wizzardo.neon.enemy;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.ui.Picture;

/**
 * Created by wizzardo on 10.03.16.
 */
public class SeekerControl extends AbstractControl {
    private Spatial player;
    private Vector3f velocity;
    private long spawnTime;

    public SeekerControl(Spatial player) {
        this.player = player;
        velocity = new Vector3f(0, 0, 0);
        spawnTime = System.currentTimeMillis();
    }

    @Override
    protected void controlUpdate(float tpf) {
        if ((Boolean) spatial.getUserData("active")) {
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
        } else {
            // handle the "active"-status
            long dif = System.currentTimeMillis() - spawnTime;
            if (dif >= 1000f) {
                spatial.setUserData("active", true);
            }

            ColorRGBA color = new ColorRGBA(1, 1, 1, dif / 1000f);
            Node spatialNode = (Node) spatial;
            Picture pic = (Picture) spatialNode.getChild("Seeker");
            pic.getMaterial().setColor("Color", color);
        }
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
    }

}
