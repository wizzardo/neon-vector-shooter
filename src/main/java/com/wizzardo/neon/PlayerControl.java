package com.wizzardo.neon;

import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * Created by wizzardo on 08.03.16.
 */
public class PlayerControl extends AbstractControl {

    private int screenWidth, screenHeight;
    private float speed = 800f;
    private float lastRotation;
    public boolean up, down, left, right;

    public PlayerControl(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    @Override
    protected void controlUpdate(float tpf) {
//        move the player in a certain direction
//        if he is not out of the screen
        if (up) {
            if (spatial.getLocalTranslation().y < screenHeight - (Float) spatial.getUserData("radius")) {
                spatial.move(0, tpf * speed, 0);
            }
            spatial.rotate(0, 0, -lastRotation + FastMath.PI / 2);
            lastRotation = FastMath.PI / 2;
        } else if (down) {
            if (spatial.getLocalTranslation().y > (Float) spatial.getUserData("radius")) {
                spatial.move(0, tpf * -speed, 0);
            }
            spatial.rotate(0, 0, -lastRotation + FastMath.PI * 1.5f);
            lastRotation = FastMath.PI * 1.5f;
        } else if (left) {
            if (spatial.getLocalTranslation().x > (Float) spatial.getUserData("radius")) {
                spatial.move(tpf * -speed, 0, 0);
            }
            spatial.rotate(0, 0, -lastRotation + FastMath.PI);
            lastRotation = FastMath.PI;
        } else if (right) {
            if (spatial.getLocalTranslation().x < screenWidth - (Float) spatial.getUserData("radius")) {
                spatial.move(tpf * speed, 0, 0);
            }
            spatial.rotate(0, 0, -lastRotation + 0);
            lastRotation = 0;
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
