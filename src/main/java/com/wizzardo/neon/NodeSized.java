package com.wizzardo.neon;

import com.jme3.scene.Node;

/**
 * Created by wizzardo on 09.03.16.
 */
public class NodeSized extends Node {
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
}
