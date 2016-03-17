package com.wizzardo.neon.grid;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * Created by wizzardo on 17.03.16.
 */
public class AdditionalLineControl extends AbstractControl {
    private PointMass end11, end12, end21, end22;

    public AdditionalLineControl(PointMass end11, PointMass end12, PointMass end21, PointMass end22) {
        this.end11 = end11;
        this.end12 = end12;
        this.end21 = end21;
        this.end22 = end22;
    }

    @Override
    protected void controlUpdate(float tpf) {
        //movement
        spatial.setLocalTranslation(position1());

        //scale
        spatial.setLocalScale(position2().distance(position1()));

        //rotation
        spatial.lookAt(position2(), new Vector3f(1, 0, 0));

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    private Vector3f position1() {
        return new Vector3f().interpolateLocal(end11.getPosition(), end12.getPosition(), 0.5f);
    }

    private Vector3f position2() {
        return new Vector3f().interpolateLocal(end21.getPosition(), end22.getPosition(), 0.5f);
    }

}
