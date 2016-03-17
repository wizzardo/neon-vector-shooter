package com.wizzardo.neon.grid;

import com.jme3.math.Vector3f;

/**
 * Created by wizzardo on 17.03.16.
 */
public class PointMass {
    private Vector3f position;
    private Vector3f velocity = new Vector3f();
    private float inverseMass;

    private Vector3f acceleration = new Vector3f();
    private float damping = 0.98f;

    public PointMass(Vector3f position, float inverseMass) {
        this.position = position;
        this.inverseMass = inverseMass;
    }

    public void applyForce(Vector3f force) {
        acceleration.addLocal(force.mult(inverseMass));
    }

    public void increaseDamping(float factor) {
        damping *= factor;
    }

    public void update(float tpf) {
        velocity.addLocal(acceleration.mult(1f));
        position.addLocal(velocity.mult(0.6f));
        acceleration = new Vector3f();

        if (velocity.lengthSquared() < 0.0001f) {
            velocity = new Vector3f();
        }

        velocity.multLocal(damping);
        damping = 0.98f;
        damping = 0.8f;

        position.z *= 0.9f;
        if (position.z < 0.01) {
            position.z = 0;
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getVelocity() {
        return velocity;
    }
}