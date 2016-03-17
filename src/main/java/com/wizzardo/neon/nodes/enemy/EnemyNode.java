package com.wizzardo.neon.nodes.enemy;

import com.jme3.math.Vector3f;
import com.jme3.scene.control.Control;
import com.wizzardo.neon.nodes.NodeSized;

/**
 * Created by wizzardo on 12.03.16.
 */
public class EnemyNode extends NodeSized {

    private EnemyControl control;

    public EnemyNode(String name) {
        super(name);
    }

    @Override
    public void addControl(Control control) {
        super.addControl(control);
        if (control instanceof EnemyControl)
            this.control = (EnemyControl) control;
    }

    public EnemyControl getControl() {
        return control;
    }

    @Override
    public void applyGravity(Vector3f gravity, float distance) {
        control.applyGravity(gravity.multLocal(150000));
    }
}
