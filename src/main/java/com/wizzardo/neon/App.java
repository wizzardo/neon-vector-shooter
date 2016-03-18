package com.wizzardo.neon;

import com.jme3.app.SimpleApplication;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.input.event.*;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.wizzardo.neon.grid.Grid;
import com.wizzardo.neon.nodes.enemy.*;
import com.wizzardo.neon.nodes.*;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by wizzardo on 08.03.16.
 */
public class App extends SimpleApplication {
    protected static final int SHOOT_COOL_DOWN = 83;
    private SoundManager soundManager;
    private ParticleManager particleManager;
    private PlayerNode player;
    private Node bulletNode;
    private long lastShot;
    private long spawnCooldownBlackHole;
    private long enemySpawnCooldown;
    private float enemySpawnChance = 80;
    private Node enemyNode;
    private Node blackHoleNode;
    private boolean gameOver = false;
    private Hud hud;
    private Grid grid;

    @Override
    public void simpleInitApp() {
        setupCamera();

////        turn off stats view (you can leave it on, if you want)
//        setDisplayStatView(false);
//        setDisplayFps(false);
        setupParticles();
        setupUserInput();
        setupBulletNode();
        setupEnemyNode();
        setupBlackHoleNode();
        setupSound();
        setupHud();
        addBloomFilter();

        setupGrid();
        setupPlayer();
    }

    private void setupParticles() {
        particleManager = new ParticleManager(guiNode, getSpatial("Laser", new ParticleNode("Laser")), getSpatial("Glow", new ParticleNode("Glow")), settings.getWidth(), settings.getHeight());
    }

    private void setupGrid() {
        Vector2f spacing = new Vector2f(60, 60);
        grid = new Grid(settings.getWidth(), settings.getHeight(), spacing, guiNode, assetManager);
    }

    private void setupHud() {
        hud = new Hud(assetManager, guiNode, settings.getWidth(), settings.getHeight());
        hud.reset();
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
//            spawnEnemies();
            handleCollisions();
            handleGravity(tpf);
        } else if (System.currentTimeMillis() - player.getDieTime() > 4000f && !gameOver) {
            // spawn player
            player.setLocalTranslation(settings.getWidth() / 2, settings.getHeight() / 2, 0);
            guiNode.attachChild(player);
            player.setAlive(true);
        }
        hud.update();
        grid.update(tpf);
    }

    private void handleGravity(float tpf) {
        for (int i = 0; i < blackHoleNode.getQuantity(); i++) {
            EnemyNode blackHole = (EnemyNode) blackHoleNode.getChild(i);
            if (!blackHole.getControl().isActive()) {
                continue;
            }
            int radius = 250;

            //check Player
            if (isNearby(player, blackHole, radius)) {
                applyGravity(blackHole, player, tpf);
            }
            //check Bullets
            for (int j = 0; j < bulletNode.getQuantity(); j++) {
                BulletNode child = (BulletNode) bulletNode.getChild(j);
                if (isNearby(child, blackHole, radius)) {
                    applyGravity(blackHole, child, tpf);
                }
            }
            //check Enemies
            for (int j = 0; j < enemyNode.getQuantity(); j++) {
                EnemyNode enemy = (EnemyNode) enemyNode.getChild(j);
                if (!enemy.getControl().isActive()) {
                    continue;
                }
                if (isNearby(enemy, blackHole, radius)) {
                    applyGravity(blackHole, enemy, tpf);
                }
            }

            // check Particles
            GNode<GNode<ParticleNode>> particleNode = particleManager.getParticleNode();
            for (int j = 0; j < particleNode.getQuantity(); j++) {
                GNode<ParticleNode> parent = particleNode.getChild(j);
                for (ParticleNode particle : parent.getChildrenArray()) {
                    if (particle.isAffectedByGravity()) {
                        applyGravity(blackHoleNode.getChild(i), particle, tpf);
                    }
                }
            }
        }
    }

    private void applyGravity(Spatial blackHole, NodeSized target, float tpf) {
        Vector3f difference = blackHole.getLocalTranslation().subtract(target.getLocalTranslation());
        float distance = difference.length();
        Vector3f gravity = difference.normalizeLocal().multLocal(tpf);
        target.applyGravity(gravity, distance);
    }

    private boolean isNearby(Spatial a, Spatial b, float distance) {
        Vector3f pos1 = a.getLocalTranslation();
        Vector3f pos2 = b.getLocalTranslation();
        return pos1.distanceSquared(pos2) <= distance * distance;
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
            EnemyNode enemy = (EnemyNode) enemyNode.getChild(i);
            for (int j = 0; j < bulletNode.getQuantity(); j++) {
                if (checkCollision(enemy, (NodeSized) bulletNode.getChild(j))) {
                    soundManager.explosion();
                    particleManager.enemyExplosion(enemy.getLocalTranslation());
                    hud.addPoints(enemy.getControl().getPoints());
                    enemyNode.detachChildAt(i);
                    bulletNode.detachChildAt(j);
                    i--;
                    break;
                }
            }
        }

        //is something colliding with a black hole?
        for (int i = 0; i < blackHoleNode.getQuantity(); i++) {
            EnemyNode blackHole = (EnemyNode) blackHoleNode.getChild(i);
            if (blackHole.getControl().isActive()) {
                //player
                if (checkCollision(player, blackHole)) {
                    killPlayer();
                }

                //enemies
                for (int j = 0; j < enemyNode.getQuantity(); j++) {
                    EnemyNode enemy = (EnemyNode) enemyNode.getChild(j);
                    if (checkCollision(enemy, blackHole)) {
                        particleManager.enemyExplosion(enemy.getLocalTranslation());
                        enemyNode.detachChildAt(j);
                        j--;
                    }
                }

                //bullets
                for (int j = 0; j < bulletNode.getQuantity(); j++) {
                    if (checkCollision((NodeSized) bulletNode.getChild(j), blackHole)) {
                        bulletNode.detachChildAt(j);
                        j--;
                        BlackHoleControl control = (BlackHoleControl) blackHole.getControl();
                        control.wasShot();
                        if (control.isDead()) {
                            particleManager.blackHoleExplosion(blackHole.getLocalTranslation(), control.getSpawnTime());
                            blackHoleNode.detachChild(blackHole);
                            soundManager.explosion();
                            break;
                        }
                    }
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
        particleManager.playerExplosion(player.getLocalTranslation());
        player.removeFromParent();
        player.getControl().reset();
        player.setAlive(false);
        player.setDieTime(System.currentTimeMillis());
        enemyNode.detachAllChildren();
        blackHoleNode.detachAllChildren();
        soundManager.explosion();

        if (!hud.removeLife()) {
            hud.endGame();
            gameOver = true;
        }
    }

    private void spawnEnemies() {
        long time = System.currentTimeMillis();
        if (time - enemySpawnCooldown >= 17) {
            enemySpawnCooldown = time;

            if (enemyNode.getQuantity() < 50) {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                if (random.nextInt((int) enemySpawnChance) == 0) {
                    createSeeker();
                }
                if (random.nextInt((int) enemySpawnChance) == 0) {
                    createWanderer();
                }
            }
            if (blackHoleNode.getQuantity() < 5) {
                if (time - spawnCooldownBlackHole > 10f) {
                    spawnCooldownBlackHole = time;
                    if (ThreadLocalRandom.current().nextInt(500) == 0) {
                        createBlackHole();
                    }
                }
            }
            //increase Spawn Time
            if (enemySpawnChance >= 1.1f) {
                enemySpawnChance -= 0.005f;
            }
        }
    }

    private void createBlackHole() {
        Spatial blackHole = getSpatial("Black Hole", new EnemyNode("Black Hole"));
        blackHole.addControl(new BlackHoleControl(particleManager, grid));

        blackHole.setLocalTranslation(getSpawnPosition());
        blackHoleNode.attachChild(blackHole);
        soundManager.spawn();
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

    private void setupBlackHoleNode() {
        blackHoleNode = new Node("blackHoles");
        guiNode.attachChild(blackHoleNode);
    }

    private void setupBulletNode() {
        bulletNode = new Node("bullets");
        guiNode.attachChild(bulletNode);
    }

    private void setupUserInput() {
        inputManager.setMouseCursor((JmeCursor) assetManager.loadAsset("Textures/Pointer.ico"));

        for (Joystick joystick : inputManager.getJoysticks()) {
            System.out.println("map joystick: " + joystick);
            for (JoystickButton button : joystick.getButtons()) {
                System.out.println(button.getButtonId() + " " + button.getName() + " " + button.getLogicalId());
            }
            for (JoystickAxis axis : joystick.getAxes()) {
                System.out.println(axis.getAxisId() + " " + axis.getName() + " " + axis.getLogicalId());
            }
        }

        inputManager.addMapping("left", new KeyTrigger(KeyInput.KEY_LEFT), new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("right", new KeyTrigger(KeyInput.KEY_RIGHT), new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("up", new KeyTrigger(KeyInput.KEY_UP), new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("down", new KeyTrigger(KeyInput.KEY_DOWN), new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("return", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener((ActionListener) (s, b, v) -> player.ifAlive(it -> it.getControl().left = b), "left");
        inputManager.addListener((ActionListener) (s, b, v) -> player.ifAlive(it -> it.getControl().right = b), "right");
        inputManager.addListener((ActionListener) (s, b, v) -> player.ifAlive(it -> it.getControl().down = b), "down");
        inputManager.addListener((ActionListener) (s, b, v) -> player.ifAlive(it -> it.getControl().up = b), "up");
        inputManager.addListener((ActionListener) (s, b, v) -> {
        }, "return");

        if (inputManager.getJoysticks().length > 0) {
            int joyId = inputManager.getJoysticks()[0].getJoyId();
            inputManager.addMapping("j-right", new JoyAxisTrigger(joyId, 0, false));
            inputManager.addListener((AnalogListener) (name, value, tpf) -> {
                if (player.isAlive())
                    player.getControl().x = value;
            }, "j-right");
            inputManager.addMapping("j-left", new JoyAxisTrigger(joyId, 0, true));
            inputManager.addListener((AnalogListener) (name, value, tpf) -> {
                if (player.isAlive())
                    player.getControl().x = -value;
            }, "j-left");
            inputManager.addMapping("j-up", new JoyAxisTrigger(joyId, 1, true));
            inputManager.addListener((AnalogListener) (name, value, tpf) -> {
                if (player.isAlive())
                    player.getControl().y = value;
            }, "j-up");
            inputManager.addMapping("j-down", new JoyAxisTrigger(joyId, 1, false));
            inputManager.addListener((AnalogListener) (name, value, tpf) -> {
                if (player.isAlive())
                    player.getControl().y = -value;
            }, "j-down");

            Vector3f aim = new Vector3f();
            Vector3f normalizedAim = new Vector3f();
            inputManager.addMapping("j-right-2", new JoyAxisTrigger(joyId, 3, false));
            inputManager.addListener((AnalogListener) (name, value, tpf) -> {
                aim.setX(value);
                if (player.isAlive())
                    shoot(normalizedAim.set(aim).normalizeLocal());
            }, "j-right-2");
            inputManager.addMapping("j-left-2", new JoyAxisTrigger(joyId, 3, true));
            inputManager.addListener((AnalogListener) (name, value, tpf) -> {
                aim.setX(-value);
                if (player.isAlive())
                    shoot(normalizedAim.set(aim).normalizeLocal());
            }, "j-left-2");
            inputManager.addMapping("j-up-2", new JoyAxisTrigger(joyId, 4, true));
            inputManager.addListener((AnalogListener) (name, value, tpf) -> {
                aim.setY(value);
                if (player.isAlive())
                    shoot(normalizedAim.set(aim).normalizeLocal());
            }, "j-up-2");
            inputManager.addMapping("j-down-2", new JoyAxisTrigger(joyId, 4, false));
            inputManager.addListener((AnalogListener) (name, value, tpf) -> {
                aim.setY(-value);
                if (player.isAlive())
                    shoot(normalizedAim.set(aim).normalizeLocal());
            }, "j-down-2");

        }

        inputManager.addMapping("mousePick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener((AnalogListener) (name, value, tpf) -> {
            if (player.isAlive()) {
                shoot(getAimDirection());
            }
        }, "mousePick");
    }

    private void shoot(Vector3f aim) {
        //shoot Bullet
        if (System.currentTimeMillis() - lastShot > SHOOT_COOL_DOWN) {
            lastShot = System.currentTimeMillis();

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

    private Spatial createBullet(Vector3f aim) {
        Spatial bullet = getSpatial("Bullet", new BulletNode("Bullet"));
        bullet.addControl(new BulletControl(aim, settings.getWidth(), settings.getHeight(), particleManager, grid));
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
        player.addControl(new PlayerControl(settings.getWidth(), settings.getHeight(), particleManager, grid));
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

    @Override
    public void setSettings(AppSettings settings) {
        settings.setUseJoysticks(true);
        super.setSettings(settings);
    }

    public static void main(String[] args) {
        new App().start();
    }
}
