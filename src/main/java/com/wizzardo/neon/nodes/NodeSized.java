package com.wizzardo.neon.nodes;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.wizzardo.neon.Gravitable;

/**
 * Created by wizzardo on 09.03.16.
 */
public class NodeSized extends Node implements Gravitable {
    private float radius;

    public NodeSized(String name) {
        super(name);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public void applyGravity(Vector3f gravity, float distance) {
    }
}
