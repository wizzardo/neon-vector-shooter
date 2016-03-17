package com.wizzardo.neon;

import com.jme3.math.Vector3f;

/**
 * Created by wizzardo on 16.03.16.
 */
public interface Gravitable {

    void applyGravity(Vector3f gravity, float distance);
}
