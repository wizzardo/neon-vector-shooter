package com.wizzardo.neon;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by wizzardo on 14.03.16.
 */
public class ParticleManager {
    private int screenWidth, screenHeight;
    private Node guiNode;
    private Particle standardParticle;
    private Particle glowParticle;

    private Node particleNode;
    private Random rand;

    public ParticleManager(Node guiNode, Particle standardParticle, Particle glowParticle, int screenWidth, int screenHeight) {
        this.guiNode = guiNode;
        this.standardParticle = standardParticle;
        this.glowParticle = glowParticle;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        particleNode = new Node("particles");
        guiNode.attachChild(particleNode);

        rand = new Random();
    }

    public Node getParticleNode() {
        return particleNode;
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

            Particle particle = standardParticle.clone();
            particle.setLocalTranslation(position);
            particle.setAffectedByGravity(true);
            ColorRGBA color = new ColorRGBA();
            color.interpolateLocal(color1, color2, rand.nextFloat() * 0.5f);
            particle.addControl(new ParticleControl(velocity, 3100, color, screenWidth, screenHeight));
            particleNode.attachChild(particle);
        }
    }

    public void bulletExplosion(Vector3f position) {
        for (int i = 0; i < 30; i++) {
            Vector3f velocity = getRandomVelocity(175);

            Particle particle = standardParticle.clone();
            particle.setLocalTranslation(position);
            particle.setAffectedByGravity(true);
            ColorRGBA color = new ColorRGBA(0.676f, 0.844f, 0.898f, 1);
            particle.addControl(new ParticleControl(velocity, 1000, color, screenWidth, screenHeight));
            particleNode.attachChild(particle);
        }
    }

    public void playerExplosion(Vector3f position) {
        ColorRGBA color1 = ColorRGBA.White;
        ColorRGBA color2 = ColorRGBA.Yellow;

        for (int i = 0; i < 1200; i++) {
            Vector3f velocity = getRandomVelocity(1000);

            Particle particle = standardParticle.clone();
            particle.setLocalTranslation(position);
            particle.setAffectedByGravity(true);
            ColorRGBA color = new ColorRGBA();
            color.interpolateLocal(color1, color2, rand.nextFloat());
            particle.addControl(new ParticleControl(velocity, 2800, color, screenWidth, screenHeight));
            particleNode.attachChild(particle);
        }
    }

    public void blackHoleExplosion(Vector3f position, long spawnTime) {
        float hue = ((System.currentTimeMillis() - spawnTime) * 0.003f) % 6f;
        int numParticles = 150;
        ColorRGBA color = hsvToColor(hue, 0.25f, 1);
        float startOffset = rand.nextFloat() * FastMath.PI * 2 / numParticles;

        for (int i = 0; i < numParticles; i++) {
            float alpha = FastMath.PI * 2 * i / numParticles + startOffset;
            Vector3f velocity = App.getVectorFromAngle(alpha).multLocal(rand.nextFloat() * 200 + 300);
            Vector3f pos = position.add(velocity.mult(0.1f));

            Particle particle = standardParticle.clone();
            particle.setLocalTranslation(pos);
            particle.addControl(new ParticleControl(velocity, 1000, color, screenWidth, screenHeight));
            particle.setAffectedByGravity(false);
            particleNode.attachChild(particle);
        }
    }

    public void sprayParticle(Vector3f position, Vector3f sprayVel) {
        Particle particle = standardParticle.clone();
        particle.setLocalTranslation(position);
        ColorRGBA color = new ColorRGBA(0.8f, 0.4f, 0.8f, 1f);
        particle.addControl(new ParticleControl(sprayVel, 3500, color, screenWidth, screenHeight));
        particle.setAffectedByGravity(true);
        particleNode.attachChild(particle);
    }

    public void makeExhaustFire(Vector3f position, float rotation, long spawnTime) {
        ColorRGBA midColor = new ColorRGBA(1f, 0.73f, 0.12f, 0.7f);
        ColorRGBA sideColor = new ColorRGBA(0.78f, 0.15f, 0.04f, 0.7f);

        Vector3f direction = App.getVectorFromAngle(rotation);

        float t = (System.currentTimeMillis() - spawnTime) / 1000f;
        Vector3f baseVel = direction.mult(-45f);
        Vector3f perpVel = new Vector3f(baseVel.y, -baseVel.x, 0).multLocal(2f * FastMath.sin(t * 10f));

        Vector3f pos = position.add(App.getVectorFromAngle(rotation).multLocal(-25f));

        //middle stream
        Random random = ThreadLocalRandom.current();
        Vector3f randVec = App.getVectorFromAngle(random.nextFloat() * FastMath.PI * 2);
        Vector3f velMid = baseVel.add(randVec.mult(7.5f));
        Particle particleMid = standardParticle.clone();
        particleMid.setLocalTranslation(pos);
        particleMid.addControl(new ParticleControl(velMid, 800, midColor, screenWidth, screenHeight));
        particleMid.setAffectedByGravity(true);
        particleNode.attachChild(particleMid);

        Particle particleMidGlow = glowParticle.clone();
        particleMidGlow.setLocalTranslation(pos);
        particleMidGlow.addControl(new ParticleControl(velMid, 800, midColor, screenWidth, screenHeight));
        particleMidGlow.setAffectedByGravity(true);
        particleNode.attachChild(particleMidGlow);

        //side streams
        Vector3f randVec1 = App.getVectorFromAngle(random.nextFloat() * FastMath.PI * 2);
        Vector3f randVec2 = App.getVectorFromAngle(random.nextFloat() * FastMath.PI * 2);
        Vector3f velSide1 = baseVel.add(randVec1.mult(2.4f)).addLocal(perpVel);
        Vector3f velSide2 = baseVel.add(randVec2.mult(2.4f)).subtractLocal(perpVel);

        Particle particleSide1 = standardParticle.clone();
        particleSide1.setLocalTranslation(pos);
        particleSide1.addControl(new ParticleControl(velSide1, 800, sideColor, screenWidth, screenHeight));
        particleSide1.setAffectedByGravity(true);
        particleNode.attachChild(particleSide1);

        Particle particleSide2 = standardParticle.clone();
        particleSide2.setLocalTranslation(pos);
        particleSide2.addControl(new ParticleControl(velSide2, 800, sideColor, screenWidth, screenHeight));
        particleSide2.setAffectedByGravity(true);
        particleNode.attachChild(particleSide2);

        Particle particleSide1Glow = glowParticle.clone();
        particleSide1Glow.setLocalTranslation(pos);
        particleSide1Glow.addControl(new ParticleControl(velSide1, 800, sideColor, screenWidth, screenHeight));
        particleSide1Glow.setAffectedByGravity(true);
        particleNode.attachChild(particleSide1Glow);

        Particle particleSide2Glow = glowParticle.clone();
        particleSide2Glow.setLocalTranslation(pos);
        particleSide2Glow.addControl(new ParticleControl(velSide2, 800, sideColor, screenWidth, screenHeight));
        particleSide2Glow.setAffectedByGravity(true);
        particleNode.attachChild(particleSide2Glow);
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