package com.wizzardo.neon;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;

/**
 * Created by wizzardo on 08.03.16.
 */
public class App extends SimpleApplication implements ActionListener {
    private Spatial player;

    @Override
    public void simpleInitApp() {
        setupCamera();

////        turn off stats view (you can leave it on, if you want)
//        setDisplayStatView(false);
//        setDisplayFps(false);
        setupPlayer();

        setupUserUnput();
    }

    private void setupUserUnput() {
        inputManager.addMapping("left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("up", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("down", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("return", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "left");
        inputManager.addListener(this, "right");
        inputManager.addListener(this, "up");
        inputManager.addListener(this, "down");
        inputManager.addListener(this, "return");
    }

    @Override
    public void onAction(String name, boolean isPressed, float v) {
        if (player.getUserData("alive")) {
            if (name.equals("up")) {
                player.getControl(PlayerControl.class).up = isPressed;
            } else if (name.equals("down")) {
                player.getControl(PlayerControl.class).down = isPressed;
            } else if (name.equals("left")) {
                player.getControl(PlayerControl.class).left = isPressed;
            } else if (name.equals("right")) {
                player.getControl(PlayerControl.class).right = isPressed;
            }
        }
    }

    private void setupPlayer() {
        //        setup the player
        player = getSpatial("Player");
        player.setUserData("alive", true);
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

    private Spatial getSpatial(String name) {
        Node node = new Node(name);
//        load picture
        Picture pic = new Picture(name);
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
        node.setUserData("radius", width / 2);

//        attach the picture to the node and return it
        node.attachChild(pic);
        return node;
    }

    public static void main(String[] args) {
        new App().start();
    }
}
