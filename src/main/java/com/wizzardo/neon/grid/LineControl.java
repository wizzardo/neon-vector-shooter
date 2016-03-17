package com.wizzardo.neon.grid;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * Created by wizzardo on 17.03.16.
 */
public class LineControl extends AbstractControl {
    private PointMass end1, end2;

    public LineControl(PointMass end1, PointMass end2) {
        this.end1 = end1;
        this.end2 = end2;
    }

    @Override
    protected void controlUpdate(float tpf) {
        //movement
        spatial.setLocalTranslation(end1.getPosition());

        //scale
        spatial.setLocalScale(end2.getPosition().distance(end1.getPosition()));

        //rotation
        spatial.lookAt(end2.getPosition(), new Vector3f(1, 0, 0));

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
