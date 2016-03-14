package com.wizzardo.neon;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.Random;

/**
 * Created by wizzardo on 14.03.16.
 */
public class ParticleManager {
    private Node guiNode;
    private Spatial standardParticle;
    private Spatial glowParticle;

    private Node particleNode;
    private Random rand;

    public ParticleManager(Node guiNode, Spatial standardParticle, Spatial glowParticle) {
        this.guiNode = guiNode;
        this.standardParticle = standardParticle;
        this.glowParticle = glowParticle;

        particleNode = new Node("particles");
        guiNode.attachChild(particleNode);

        rand = new Random();
    }

    public void enemyExplosion(Vector3f position) {
        // init colors
        float hue1 = rand.nextFloat() * 6;
        float hue2 = (rand.nextFloat() * 2) % 6f;
        ColorRGBA color1 = hsvToColor(hue1, 0.5f, 1f);
        ColorRGBA color2 = hsvToColor(hue2, 0.5f, 1f);

        // create 120 particles
        for (int i = 0; i < 120; i++) {
            Vector3f velocity = getRandomVelocity(250);

            Spatial particle = standardParticle.clone();
            particle.setLocalTranslation(position);
            ColorRGBA color = new ColorRGBA();
            color.interpolateLocal(color1, color2, rand.nextFloat() * 0.5f);
            particle.addControl(new ParticleControl(velocity, 3100, color));
            particleNode.attachChild(particle);
        }
    }

    public ColorRGBA hsvToColor(float h, float s, float v) {
        if (h == 0 && s == 0) {
            return new ColorRGBA(v, v, v, 1);
        }

        float c = s * v;
        float x = c * (1 - Math.abs(h % 2 - 1));
        float m = v - c;

        if (h < 1) {
            return new ColorRGBA(c + m, x + m, m, 1);
        } else if (h < 2) {
            return new ColorRGBA(x + m, c + m, m, 1);
        } else if (h < 3) {
            return new ColorRGBA(m, c + m, x + m, 1);
        } else if (h < 4) {
            return new ColorRGBA(m, x + m, c + m, 1);
        } else if (h < 5) {
            return new ColorRGBA(x + m, m, c + m, 1);
        } else {
            return new ColorRGBA(c + m, m, x + m, 1);
        }
    }

    private Vector3f getRandomVelocity(float max) {
        // generate Vector3f with random direction
        Vector3f velocity = new Vector3f(rand.nextFloat() - 0.5f, rand.nextFloat() - 0.5f, 0).normalizeLocal();

        // apply semi-random particle speed
        float random = rand.nextFloat() * 5 + 1;
        float particleSpeed = max * (1f - 0.6f / random);
        velocity.multLocal(particleSpeed);
        return velocity;
    }
}