package com.wizzardo.neon.nodes;

import com.jme3.math.Vector3f;
import com.jme3.scene.control.Control;

/**
 * Created by wizzardo on 17.03.16.
 */
public class BulletNode extends NodeSized {
    private BulletControl control;

    public BulletNode(String name) {
        super(name);
    }

    @Override
    public void applyGravity(Vector3f gravity, float distance) {
        control.applyGravity(gravity.multLocal(250f / distance * -0.8f));
    }

    @Override
    public void addControl(Control control) {
        super.addControl(control);
        if (control instanceof BulletControl) {
            this.control = (BulletControl) control;
        }
    }

}
