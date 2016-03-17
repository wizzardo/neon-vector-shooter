package com.wizzardo.neon.nodes;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.wizzardo.neon.ParticleManager;

/**
 * Created by wizzardo on 08.03.16.
 */
public class PlayerControl extends AbstractControl {

    private int screenWidth, screenHeight;
    private float speed = 800f;
    private float lastRotation;
    private PlayerNode player;
    private ParticleManager particleManager;
    private long spawnTime;
    public boolean up, down, left, right;


    public PlayerControl(int width, int height, ParticleManager particleManager) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.particleManager = particleManager;
        spawnTime = System.currentTimeMillis();
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        player = (PlayerNode) spatial;
    }

    @Override
    protected void controlUpdate(float tpf) {
//        move the player in a certain direction
//        if he is not out of the screen
        PlayerNode player = this.player;
        if (up) {
            if (player.getLocalTranslation().y < screenHeight - player.getRadius()) {
                player.move(0, tpf * speed, 0);
            }
            player.rotate(0, 0, -lastRotation + FastMath.PI / 2);
            lastRotation = FastMath.PI / 2;
        } else if (down) {
            if (player.getLocalTranslation().y > player.getRadius()) {
                player.move(0, tpf * -speed, 0);
            }
            player.rotate(0, 0, -lastRotation + FastMath.PI * 1.5f);
            lastRotation = FastMath.PI * 1.5f;
        } else if (left) {
            if (player.getLocalTranslation().x > player.getRadius()) {
                player.move(tpf * -speed, 0, 0);
            }
            player.rotate(0, 0, -lastRotation + FastMath.PI);
            lastRotation = FastMath.PI;
        } else if (right) {
            if (player.getLocalTranslation().x < screenWidth - player.getRadius()) {
                player.move(tpf * speed, 0, 0);
            }
            player.rotate(0, 0, -lastRotation + 0);
            lastRotation = 0;
        }
        if (up || down || left || right) {
            particleManager.makeExhaustFire(spatial.getLocalTranslation(), lastRotation, spawnTime);
        }
    }

    @Override
    protected void controlRender(RenderManager renderManager, ViewPort viewPort) {
    }

    public void reset() {
        up = false;
        down = false;
        left = false;
        right = false;
    }
}
