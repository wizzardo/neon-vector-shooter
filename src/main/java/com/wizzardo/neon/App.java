package com.wizzardo.neon;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.wizzardo.neon.enemy.EnemyNode;
import com.wizzardo.neon.enemy.SeekerControl;
import com.wizzardo.neon.enemy.WandererControl;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by wizzardo on 08.03.16.
 */
public class App extends SimpleApplication {
    protected static final int SHOOT_COOL_DOWN = 83;
    private SoundManager soundManager;
    private PlayerNode player;
    private Node bulletNode;
    private long lastShot;

    private long enemySpawnCooldown;
    private float enemySpawnChance = 80;
    private Node enemyNode;
    private boolean gameOver = false;

    @Override
    public void simpleInitApp() {
        setupCamera();

////        turn off stats view (you can leave it on, if you want)
//        setDisplayStatView(false);
//        setDisplayFps(false);
        setupPlayer();

        setupUserInput();
        setupBulletNode();
        setupEnemyNode();
        setupSound();
        addBloomFilter();
    }

    private void setupSound() {
        soundManager = new SoundManager(assetManager);
        soundManager.startMusic();
    }

    private void addBloomFilter() {
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter();
        bloom.setBloomIntensity(2f);
        bloom.setExposurePower(2);
        bloom.setExposureCutOff(0f);
        bloom.setBlurScale(1.5f);
        fpp.addFilter(bloom);
        guiViewPort.addProcessor(fpp);
        guiViewPort.setClearColor(true);
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (player.isAlive()) {
            spawnEnemies();
            handleCollisions();
        } else if (System.currentTimeMillis() - player.getDieTime() > 4000f && !gameOver) {
            // spawn player
            player.setLocalTranslation(settings.getWidth() / 2, settings.getHeight() / 2, 0);
            guiNode.attachChild(player);
            player.setAlive(true);
        }
    }

    private void handleCollisions() {
        // should the player die?
        for (int i = 0; i < enemyNode.getQuantity(); i++) {
            if (((EnemyNode) enemyNode.getChild(i)).getControl().isActive()) {
                if (checkCollision(player, (NodeSized) enemyNode.getChild(i))) {
                    killPlayer();
                }
            }
        }

        //should an enemy die?
        for (int i = 0; i < enemyNode.getQuantity(); i++) {
            for (int j = 0; j < bulletNode.getQuantity(); j++) {
                if (checkCollision(((NodeSized) enemyNode.getChild(i)), (NodeSized) bulletNode.getChild(j))) {
                    enemyNode.detachChildAt(i);
                    bulletNode.detachChildAt(j);
                    soundManager.explosion();
                    i--;
                    break;
                }
            }
        }
    }

    private boolean checkCollision(NodeSized a, NodeSized b) {
        float distance = a.getLocalTranslation().distance(b.getLocalTranslation());
        float maxDistance = a.getRadius() + b.getRadius();
        return distance <= maxDistance;
    }

    private void killPlayer() {
        player.removeFromParent();
        player.getControl().reset();
        player.setAlive(false);
        player.setDieTime(System.currentTimeMillis());
        enemyNode.detachAllChildren();
        soundManager.explosion();
    }

    private void spawnEnemies() {
        if (System.currentTimeMillis() - enemySpawnCooldown >= 17) {
            enemySpawnCooldown = System.currentTimeMillis();

            if (enemyNode.getQuantity() < 50) {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                if (random.nextInt((int) enemySpawnChance) == 0) {
                    createSeeker();
                }
                if (random.nextInt((int) enemySpawnChance) == 0) {
                    createWanderer();
                }
            }
            //increase Spawn Time
            if (enemySpawnChance >= 1.1f) {
                enemySpawnChance -= 0.005f;
            }
        }
    }

    private void createSeeker() {
        Spatial seeker = getSpatial("Seeker", new EnemyNode("Seeker"));
        seeker.addControl(new SeekerControl(player));
        spawn(seeker);
    }

    private void createWanderer() {
        Spatial wanderer = getSpatial("Wanderer", new EnemyNode("Wanderer"));
        wanderer.addControl(new WandererControl(settings.getWidth(), settings.getHeight()));
        spawn(wanderer);
    }

    private void spawn(Spatial spatial) {
        spatial.setLocalTranslation(getSpawnPosition());
        enemyNode.attachChild(spatial);
        soundManager.spawn();
    }

    private Vector3f getSpawnPosition() {
        Vector3f pos;
        do {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            pos = new Vector3f(random.nextInt(settings.getWidth()), random.nextInt(settings.getHeight()), 0);
        } while (pos.distanceSquared(player.getLocalTranslation()) < 8000);
        return pos;
    }

    private void setupEnemyNode() {
        enemyNode = new Node("enemies");
        guiNode.attachChild(enemyNode);
    }

    private void setupBulletNode() {
        bulletNode = new Node("bullets");
        guiNode.attachChild(bulletNode);
    }

    private void setupUserInput() {
        inputManager.addMapping("left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("up", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("down", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("return", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener((ActionListener) (s, b, v) -> player.ifAlive(it -> it.getControl().left = b), "left");
        inputManager.addListener((ActionListener) (s, b, v) -> player.ifAlive(it -> it.getControl().right = b), "right");
        inputManager.addListener((ActionListener) (s, b, v) -> player.ifAlive(it -> it.getControl().down = b), "down");
        inputManager.addListener((ActionListener) (s, b, v) -> player.ifAlive(it -> it.getControl().up = b), "up");
        inputManager.addListener((ActionListener) (s, b, v) -> {
        }, "return");

        inputManager.addMapping("mousePick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener((AnalogListener) (name, value, tpf) -> {
            if (player.isAlive()) {
                //shoot Bullet
                if (System.currentTimeMillis() - lastShot > SHOOT_COOL_DOWN) {
                    lastShot = System.currentTimeMillis();

                    Vector3f aim = getAimDirection();
                    Vector3f offset = new Vector3f(aim.y / 3, -aim.x / 3, 0);

//                    init bullet 1
                    Spatial bullet = createBullet(aim);
                    Vector3f finalOffset = aim.add(offset).mult(30);
                    Vector3f trans = player.getLocalTranslation().add(finalOffset);
                    bullet.setLocalTranslation(trans);
                    bulletNode.attachChild(bullet);

//                    init bullet 2
                    Spatial bullet2 = createBullet(aim);
                    finalOffset = aim.add(offset.negate()).mult(30);
                    trans = player.getLocalTranslation().add(finalOffset);
                    bullet2.setLocalTranslation(trans);
                    bulletNode.attachChild(bullet2);

                    soundManager.shoot();
                }
            }
        }, "mousePick");
    }

    private Spatial createBullet(Vector3f aim) {
        Spatial bullet = getSpatial("Bullet");
        bullet.addControl(new BulletControl(aim, settings.getWidth(), settings.getHeight()));
        return bullet;
    }

    private Vector3f getAimDirection() {
        Vector2f mouse = inputManager.getCursorPosition();
        Vector3f playerPos = player.getLocalTranslation();
        Vector3f dif = new Vector3f(mouse.x - playerPos.x, mouse.y - playerPos.y, 0);
        return dif.normalizeLocal();
    }

    private void setupPlayer() {
        //        setup the player
        player = getSpatial("Player", new PlayerNode("Player"));
        player.move(settings.getWidth() / 2, settings.getHeight() / 2, 0);
        player.addControl(new PlayerControl(settings.getWidth(), settings.getHeight()));
        guiNode.attachChild(player);
    }

    private void setupCamera() {
        //        setup camera for 2D games
        cam.setParallelProjection(true);
        cam.setLocation(new Vector3f(0, 0, 0.5f));
        getFlyByCamera().setEnabled(false);
    }

    private NodeSized getSpatial(String name) {
        return getSpatial(name, new NodeSized(name));
    }

    private <T extends NodeSized> T getSpatial(String name, T node) {
//        load picture
        Picture pic = new Picture("texture");
        Texture2D tex = (Texture2D) assetManager.loadTexture("Textures/" + name + ".png");
        pic.setTexture(assetManager, tex, true);

//        adjust picture
        float width = tex.getImage().getWidth();
        float height = tex.getImage().getHeight();
        pic.setWidth(width);
        pic.setHeight(height);
        pic.move(-width / 2f, -height / 2f, 0);

//        add a material to the picture
        Material picMat = new Material(assetManager, "Common/MatDefs/Gui/Gui.j3md");
        picMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.AlphaAdditive);
        node.setMaterial(picMat);

//        set the radius of the spatial
//        (using width only as a simple approximation)
        node.setRadius(width / 2);

//        attach the picture to the node and return it
        node.attachChild(pic);
        return node;
    }

    public static float getAngleFromVector(Vector3f vec) {
        Vector2f vec2 = new Vector2f(vec.x, vec.y);
        return vec2.getAngle();
    }

    public static Vector3f getVectorFromAngle(float angle) {
        return new Vector3f(FastMath.cos(angle), FastMath.sin(angle), 0);
    }

    public static void main(String[] args) {
        new App().start();
    }
}
