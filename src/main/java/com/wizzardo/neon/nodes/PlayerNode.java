package com.wizzardo.neon.nodes;

import com.jme3.math.Vector3f;
import com.jme3.scene.control.Control;

import java.util.function.Consumer;

/**
 * Created by wizzardo on 09.03.16.
 */
public class PlayerNode extends NodeSized {
    private boolean alive = true;
    private PlayerControl control;
    private long dieTime;

    public PlayerNode(String name) {
        super(name);
    }

    public boolean isAlive() {
        return alive;
    }

    public void ifAlive(Consumer<? super PlayerNode> consumer) {
        if (alive)
            consumer.accept(this);
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public long getDieTime() {
        return dieTime;
    }

    public void setDieTime(long dieTime) {
        this.dieTime = dieTime;
    }

    @Override
    public void addControl(Control control) {
        super.addControl(control);
        if (control instanceof PlayerControl) {
            this.control = (PlayerControl) control;
        }
    }

    public PlayerControl getControl() {
        return control;
    }

    @Override
    public void applyGravity(Vector3f gravity, float distance) {
        move(gravity.multLocal(250f / distance * 80f));
    }
}
