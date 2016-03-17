package com.wizzardo.neon.nodes;

import com.jme3.math.Vector3f;
import com.jme3.scene.control.Control;

/**
 * Created by wizzardo on 16.03.16.
 */
public class ParticleNode extends NodeSized {

    private boolean affectedByGravity;
    private ParticleControl control;

    public ParticleNode(String name) {
        super(name);
    }

    public boolean isAffectedByGravity() {
        return affectedByGravity;
    }

    public void setAffectedByGravity(boolean affectedByGravity) {
        this.affectedByGravity = affectedByGravity;
    }

    @Override
    public ParticleNode clone() {
        return (ParticleNode) super.clone();
    }

    @Override
    public void addControl(Control control) {
        super.addControl(control);
        if (control instanceof ParticleControl) {
            this.control = (ParticleControl) control;
        }
    }

    @Override
    public void applyGravity(Vector3f gravity, float distance) {
        control.applyGravity(gravity.multLocal(15000), distance);
    }
}
