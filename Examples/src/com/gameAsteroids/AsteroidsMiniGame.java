package com.gameAsteroids;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;
import java.util.List;

/**
 * My version of asteroids.
 * @author Mark Schrijver
 */
public class AsteroidsMiniGame extends SimpleApplication {

    private static final String INPUT_ROTATE_LEFT = "ROTATE_LEFT";
    private static final String INPUT_ROTATE_RIGHT = "ROTATE_RIGHT";
    private static final String INPUT_THRUST_FORWARD = "THRUST_FORWARD";
    private static final String INPUT_FIRE = "FIRE";
    private static final float INVULNERABILITY_TIMOUT = 1f;
    private static final int MAX_SPLIT_COUNT = 3;
    private static final int MOVEMENT_ASTEROID_SPEED = 5;
    private static final int MOVEMENT_BULLET_SPEED = 30;
    private static final int MOVEMENT_INCREASE_SPEED = 20;
    private static final int MOVEMENT_MAX_BULLET_DISTANCE = 3;
    private static final int MOVEMENT_MAX_SPEED = 30;
    private static final int MOVEMENT_ROTATION_SPEED = 10;
    private Node ship = null;
    private Geometry asteroid = null;
    private Geometry bullet = null;
    private Node bulletNode = null;
    private List<Node> asteroids = new ArrayList<Node>();
    private AppActionListener actionListener = new AppActionListener();
    private int level = 3;
    private float untouchableCounter = 0;
    private Vector3f vel;
    private Vector3f accel;
    private int roidCounter = 0;

    public static void main(String[] args) {
        FastMath.rand.setSeed(System.currentTimeMillis());
        AsteroidsMiniGame app = new AsteroidsMiniGame();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        setupCamera();
        setupLighting();
        setupKeys();
        setupShip();
        setupAsteroid();
        setupBullet();
        createLevel();
    }

    @Override
    public void simpleUpdate(float tpf) {
        moveShip(tpf);
        moveAsteroids(tpf);
        moveBullet(tpf);
        testHit();
        testColliding(tpf);
        testVictory();
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private void moveShip(float tpf) {
        vel.addLocal(accel.mult(tpf));
        ship.setLocalTranslation(ship.getLocalTranslation().add(vel.mult(tpf)));
        wrap(ship);
        accel = new Vector3f(0, 0, 0);
    }

    private void moveAsteroids(float tpf) {
        for (Node roid : asteroids) {
            Vector3f direction = roid.getLocalRotation().getRotationColumn(2).normalize();
            roid.setLocalTranslation(roid.getLocalTranslation().add(direction.mult(MOVEMENT_ASTEROID_SPEED * tpf)));
            wrap(roid);
        }
    }

    private void moveBullet(float tpf) {
        if (bulletNode != null) {
            Vector3f direction = bulletNode.getLocalRotation().getRotationColumn(2).normalize();
            bulletNode.setLocalTranslation(bulletNode.getLocalTranslation().add(direction.multLocal(MOVEMENT_BULLET_SPEED * tpf)));

            Float distanceTraveled = bulletNode.getUserData("distance_traveled");
            if (distanceTraveled > MOVEMENT_MAX_BULLET_DISTANCE) {
                rootNode.detachChild(bulletNode);
                bulletNode = null;
            } else {
                wrap(bulletNode);
            }
        }
    }

    private void wrap(Spatial wrappable) {
        Vector3f screenCoordinates = cam.getScreenCoordinates(wrappable.getWorldTranslation());

        if (screenCoordinates.x < 0 || screenCoordinates.x > cam.getWidth()) {
            wrappable.setLocalTranslation(-wrappable.getLocalTranslation().x, wrappable.getLocalTranslation().y, wrappable.getLocalTranslation().z);
        } else if (screenCoordinates.y < 0 || screenCoordinates.y > cam.getHeight()) {
            wrappable.setLocalTranslation(wrappable.getLocalTranslation().x, wrappable.getLocalTranslation().y, -wrappable.getLocalTranslation().z);
        }
    }

    private void testHit() {
        if (bulletNode != null) {
            for (Node roid : asteroids) {
//                System.out.println("testing " + roid.getName() + " bounds: " + roid.getWorldBound());
//                System.out.println("   to Bullet bounds: " + bulletNode.getWorldBound());
                if (roid.getWorldBound().intersects(bulletNode.getWorldBound())) {
                    System.out.println("bullet hit roid " + roid.getName());
                    rootNode.detachChild(roid);
                    rootNode.detachChild(bulletNode);
                    asteroids.remove(roid);
                    bulletNode = null;
                    if ((Integer) roid.getUserData("roid_level") < MAX_SPLIT_COUNT) {
                        float rotation1 = roid.getLocalRotation().toAngles(null)[1] + FastMath.QUARTER_PI;
                        float rotation2 = roid.getLocalRotation().toAngles(null)[1] - FastMath.QUARTER_PI;
                        rootNode.attachChild(createAsteroidNode(0.75f / (Integer) roid.getUserData("roid_level"), rotation1, roid));
                        rootNode.attachChild(createAsteroidNode(0.75f / (Integer) roid.getUserData("roid_level"), rotation2, roid));
                    }
                    break;
                }
            }
        }
    }

    private void testColliding(float tpf) {
        if (untouchableCounter > INVULNERABILITY_TIMOUT) {
            for (Node roid : asteroids) {
//            System.out.println("testing " + roid.getName() + " bounds: " + roid.getWorldBound());
//            System.out.println("   to Ship bounds: " + ship.getWorldBound());
                if (roid.getWorldBound().intersects(ship.getWorldBound())) {
                    System.out.println("BOOM !! You're dead.");
                    stop();
                }
            }
        } else {
            untouchableCounter += tpf;
        }
    }

    private void testVictory() {
        if (asteroids.isEmpty()) {
            System.out.println("You won!!!");
            stop();
        }
    }

    private void setupCamera() {
        this.getFlyByCamera().setEnabled(false);
        cam.setLocation(new Vector3f(0, 30, 0));
        cam.lookAt(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
    }

    private void setupLighting() {
        DirectionalLight sun1 = new DirectionalLight();
        sun1.setColor(ColorRGBA.White);
        sun1.setDirection(new Vector3f(-1.13f, -1.13f, 1.13f).normalizeLocal());
        rootNode.addLight(sun1);

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.Red);
        rootNode.addLight(al);
    }

    private void setupKeys() {
        inputManager.addMapping(INPUT_ROTATE_LEFT, new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(INPUT_ROTATE_RIGHT, new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(INPUT_THRUST_FORWARD, new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(INPUT_FIRE, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, INPUT_ROTATE_LEFT,
                INPUT_ROTATE_RIGHT, INPUT_THRUST_FORWARD, INPUT_FIRE);
    }

    private void setupShip() {
        Cylinder n = new Cylinder(30, 30, 0.25f, 1f);
        Geometry nose = new Geometry("Nose", n);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Ambient", ColorRGBA.Gray);
        mat.setColor("Diffuse", ColorRGBA.Blue);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 1);
        nose.setMaterial(mat);

        Sphere b = new Sphere(30, 30, 1f, true, false);
        Geometry body = new Geometry("Body", b);
        body.setMaterial(mat);
        body.setLocalTranslation(0, -0.5f, 0);
        ship = new Node("Ship");
        ship.attachChild(nose);
        ship.attachChild(body);

        ship.getChild("Nose").setLocalTranslation(0, 0, 1);
        ship.getChild("Body").setLocalTranslation(0, 0, 0);

        System.out.println("setting radius to: 1.0");
        ship.setUserData("radius", new Float(1f));
        rootNode.attachChild(ship);

        vel = new Vector3f(0, 0, 0);
        accel = new Vector3f(0, 0, 0);
    }

    private void setupAsteroid() {
        Sphere b = new Sphere(30, 30, 1f, true, false);
        asteroid = new Geometry("Asteroid", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Ambient", ColorRGBA.Gray);
        mat.setColor("Diffuse", ColorRGBA.Brown);
        mat.setColor("Specular", ColorRGBA.LightGray);
        mat.setFloat("Shininess", 1);
        asteroid.setMaterial(mat);
    }

    private void setupBullet() {
        Sphere b = new Sphere(30, 30, 0.2f, true, false);
        bullet = new Geometry("Bullet", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Ambient", ColorRGBA.LightGray);
        mat.setColor("Diffuse", ColorRGBA.Cyan);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 1);
        bullet.setMaterial(mat);
    }

    private void createLevel() {
        for (int i = 0; i < level; i++) {
            float direction = FastMath.rand.nextFloat() * roidCounter;
            rootNode.attachChild(createAsteroidNode(1f, direction, ship));
        }
    }

    private Node createAsteroidNode(float scale, float direction, Node baseNode) {
        int roidLevel = 0;
        try {
            roidLevel = (Integer) baseNode.getUserData("roid_level");
        } catch (NullPointerException e) {
            //ignore this one. 
        }
        Geometry r = asteroid.clone();
        r.setName("roid_geo_" + roidCounter);
        Node roid = new Node();
        roid.attachChild(r);
        roid.setName("roid_node_" + roidCounter);
        roid.setLocalTranslation(baseNode.getLocalTranslation().x, baseNode.getLocalTranslation().y, baseNode.getLocalTranslation().z);
        roid.rotate(0, direction, 0);
        roid.scale(scale);
        System.out.println("scaling to: " + scale);
        roid.setUserData("roid_level", roidLevel + 1);
        asteroids.add(roid);
        roidCounter++;
        return roid;
    }

    private class AppActionListener implements AnalogListener {

        public void onAnalog(String name, float value, float tpf) {
            if (name.equals(INPUT_ROTATE_LEFT)) {
                ship.rotate(0, tpf * MOVEMENT_ROTATION_SPEED, 0);
            } else if (name.equals(INPUT_ROTATE_RIGHT)) {
                ship.rotate(0, -tpf * MOVEMENT_ROTATION_SPEED, 0);
            } else if (name.equals(INPUT_THRUST_FORWARD)) {
                if (ship.getLocalRotation().getRotationColumn(2).normalize().length() < MOVEMENT_MAX_SPEED) {
                    accel = ship.getLocalRotation().getRotationColumn(2).normalize().mult(MOVEMENT_INCREASE_SPEED);
                }
            } else if (name.equals(INPUT_FIRE)) {
                shoot();
            }
        }

        private void shoot() {
            if (bulletNode == null) {
                Geometry b = bullet.clone();
                bulletNode = ship.clone(false);
                bulletNode.detachAllChildren();
                bulletNode.attachChild(b);
                bulletNode.setUserData("distance_traveled", new Float(0));
//                bulletNode.setModelBound(new BoundingSphere(0.2f, bullet.getLocalTranslation()));
                rootNode.attachChild(bulletNode);
            }
        }
    }
}
