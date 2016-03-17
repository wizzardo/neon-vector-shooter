package com.wizzardo.neon.nodes;

/**
 * Created by wizzardo on 16.03.16.
 */
public class Particle extends NodeSized {

    private boolean affectedByGravity;

    public Particle(String name) {
        super(name);
    }

    public boolean isAffectedByGravity() {
        return affectedByGravity;
    }

    public void setAffectedByGravity(boolean affectedByGravity) {
        this.affectedByGravity = affectedByGravity;
    }

    @Override
    public Particle clone() {
        return (Particle) super.clone();
    }
}
