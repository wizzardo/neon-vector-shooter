package com.wizzardo.neon.enemy;

import com.jme3.scene.control.Control;
import com.wizzardo.neon.NodeSized;

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
}
