package com.spaceship;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import jme3tools.optimize.GeometryBatchFactory;

public class SpaceShip extends SimpleApplication {

    public static void main(String[] args) {
        SpaceShip app = new SpaceShip();
        AppSettings aps = new AppSettings(true);
        aps.setVSync(true);
//        aps.setFrameRate(60);
//        aps.setResolution(800, 600);
        app.setSettings(aps);
        app.start();
    }
    private Geometry geom;
    private Node instNodes;
    private BulletAppState bulletAppState;
    private Node ship;
    private ChaseCamera chaseCam;

    @Override
    public void simpleInitApp() {

//        settings.setVSync(true);
//        settings.setFrameRate(60);

        flyCam.setEnabled(false);
//        flyCam.setMoveSpeed(30);
        viewPort.setBackgroundColor(ColorRGBA.Gray);
        instNodes = new Node();

        setPhysics();
        UI();
        setAsteroids();
        setPlayer();
        setEnemies();

        setLight();
        setCam();
        PlayerMappings mappings = new PlayerMappings(this, ship, (PlayerControl) ship.getControl(PlayerControl.class), chaseCam);
    }

    void setPhysics() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, 0, 0));
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);        
    }

    void UI() {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize());
        ch.setText("W, E, Space, LeftMouse, MiddleMouse, RightMouse"); // crosshairs
        ch.setColor(new ColorRGBA(1f, 0.8f, 0.1f, 1f));
        ch.setLocalTranslation(settings.getWidth() * 0.3f, settings.getHeight() * 0.1f, 0);
        guiNode.attachChild(ch);
    }

    void setCam() {
        chaseCam = new ChaseCamera(cam, ship, inputManager);
        chaseCam.setDragToRotate(true);
        chaseCam.setTrailingEnabled(false);

        chaseCam.setInvertVerticalAxis(true);

        chaseCam.setMinVerticalRotation(-FastMath.PI * 0.45f);
        chaseCam.setMaxVerticalRotation(FastMath.PI * 0.45f);

        chaseCam.setMinDistance(10f);
        chaseCam.setMaxDistance(20f);

        chaseCam.setRotationSpeed(0.3f);
//        chaseCam.setDownRotateOnCloseViewOnly(false);   
//        chaseCam.setHideCursorOnRotate(false);

        chaseCam.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        chaseCam.setEnabled(true);
    }

    void setLight() {
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.8f, -0.6f, -0.08f).normalizeLocal());
        dl.setColor(new ColorRGBA(0.9f, 0.9f, 0.7f, 1));
        rootNode.addLight(dl);

        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(1.5f, 1.7f, 2.7f, 1));
        rootNode.addLight(al);
    }

    void setPlayer() {
        ship = new Node("Player");
        ship.addControl(new PlayerControl(cam, ship, bulletAppState, assetManager, this));
        ship.addControl(new AimingControl(this, ship));
        rootNode.attachChild(ship);
    }

    void setEnemies() {
        Node enemyNode = new Node("enemies");
        EnemyManager enMan = new EnemyManager(enemyNode, bulletAppState, assetManager);
        for (int i = 0; i < 50; i++) {
            Node enemy = enMan.createEnemy();
            ship.getControl(AimingControl.class).addToSelection(enemy); // add to aiming
        }
        rootNode.attachChild(enemyNode);
    }

    void setAsteroids() {

        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        geom = new Geometry("Box", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        geom.setMaterial(mat);

        int numClones = 500;

        for (int i = 0; i < numClones; i++) {

            Geometry gm = geom.clone(false);
            gm.setName("instance" + i);
            gm.setLocalTranslation((float) Math.random() * 70.0f, (float) Math.random() * 70.0f - 15.0f, (float) Math.random() * 50.0f + 50.0f);
            gm.rotate(0, (float) Math.random() * (float) Math.PI, 0);

            instNodes.attachChild(gm);

        }

        instNodes = (Node) GeometryBatchFactory.optimize(instNodes);  // fps optimization
        rootNode.attachChild(instNodes);
        instNodes.setUserData("Type", "Asteroid");

        // setting physics
        for (Spatial s : instNodes.getChildren()) {
            Geometry geo = (Geometry) s;
            geo.setUserData("Type", "Asteroid");
            CollisionShape colShape = new MeshCollisionShape(geo.getMesh());
            colShape.setMargin(0.005f);
            RigidBodyControl rigControl = new RigidBodyControl(colShape, 0);

            geo.addControl(rigControl);

            bulletAppState.getPhysicsSpace().add(rigControl);
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        System.nanoTime();
        chaseCam.setLookAtOffset(cam.getUp().normalizeLocal().multLocal(4f));

    }

    public void onAction(String name, boolean isPressed, float tpf) {
    }
}
