package com.robotfight;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;

/**
 * A simple demo game for a realtime 3rd person enviroment. The player controls
 * a robot and has to fight a computer controlled robot with laser beams.
 *
 * @author Ryu Battosai Kajiya
 */
public class Main extends SimpleApplication implements AnalogListener, ActionListener {

// general variables
    private BloomFilter bf;
    private float maxBlur = 2.7f;
    private Material mat;
    private final int floorsize = 40;
    private final float turnSpeed = 25;
    private final float lasermaxlifetime = 0.25f;
    private final float attackRange = 50f;
    private final float laserdamage = 5f;
    private boolean gameFinished = false;
    private float gameFinishCountDown = 5f;
    // player
    private Node player;
    private final float playerMoveSpeed = 15;
    private float playerLaserLifetime = 0;
    private Geometry playerLaserBeam;
    // enemy
    private Node enemy;
    private final float enemyMoveSpeed = 8;
    private float enemyLaserLifetime = 0;
    private Geometry enemyLaserBeam;
    private boolean attacking = false;
    private final float maxIdleTime = 2.5f;
    private final float maxActionTime = 5f;
    private float enemyLaserCooldown = 0f;
    private final float enemyMaxLaserCooldown = 0.5f;
    private ArrayList<EnemyPlan> plans = new ArrayList<EnemyPlan>();
    private ArrayList<EnemyPlan> updatePlans = new ArrayList<EnemyPlan>();
    // some variables for enemy "AI"
    private boolean front = true;
    private boolean move = true;
    private boolean left = true;
    private boolean turn = true;
    private Plane forward = new Plane();
    private Plane sidewards = new Plane();
    Vector3f[] viewAxe = new Vector3f[3];
    private Node helperNode = new Node("ViewHelper");
    // debugging stuff
    private Geometry helper;
    private final boolean addDebugObjects = false;
    private final boolean allDebugMessages = false;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        flyCam.setMoveSpeed(20);
        rootNode.attachChild(helperNode);

        initializeFloor();

        initializeBloom();

        initializePlayer();

        initializeEnemy();

        // Disable the default flyby cam
        flyCam.setEnabled(false);
        //create the camera Node
        CameraNode camNode = new CameraNode("Camera Node", cam);
        //This mode means that camera copies the movements of the target:
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        //Attach the camNode to the target:
        player.attachChild(camNode);
        //Move camNode, e.g. behind and above the target:
        camNode.setLocalTranslation(new Vector3f(0, 6, -18));
        //Rotate the camNode to look at the target:
        camNode.lookAt(player.getLocalTranslation(), Vector3f.UNIT_Y);
        camNode.setLocalTranslation(new Vector3f(0, 12, -22));

        registerInput();

        if (addDebugObjects) {
            Geometry playfield = new Geometry("debuggrid", new Grid(floorsize, floorsize, floorsize / 10));
            Material plafieldmat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            plafieldmat.getAdditionalRenderState().setWireframe(true);
            plafieldmat.setColor("Color", ColorRGBA.Yellow);
            playfield.setMaterial(plafieldmat);
            playfield.center().move(0, 0.2f, 0);
            rootNode.attachChild(playfield);
        }
    }

    private void registerInput() {
        inputManager.addMapping("moveForward", new KeyTrigger(KeyInput.KEY_UP), new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("moveBackward", new KeyTrigger(KeyInput.KEY_DOWN), new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("moveRight", new KeyTrigger(KeyInput.KEY_RIGHT), new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("moveLeft", new KeyTrigger(KeyInput.KEY_LEFT), new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "moveForward", "moveBackward", "moveRight", "moveLeft");
        inputManager.addListener(this, "Shoot");
    }

    private void initializeEnemy() {
        // create a robot for the enemy
        enemy = createRobot("enemy");
        enemy.setUserData("health", 100f);

        // add simple healthbar
        BillboardControl billboard = new BillboardControl();
        Geometry healthbar = new Geometry("healthbar", new Quad(4f, 0.2f));
        Material mathb = mat.clone();
        mathb.setColor("Color", ColorRGBA.Red);
        healthbar.setMaterial(mathb);
        enemy.attachChild(healthbar);
        healthbar.center();
        healthbar.move(0, 7, 2);
        healthbar.addControl(billboard);

        // put enemy in a corner
        enemy.move(floorsize * 2 - 5, 0, floorsize * 2 - 5);

        // let enemy look at center
        enemy.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        // add enemy to scene
        rootNode.attachChild(enemy);
    }

    private void initializePlayer() {
        // create a robot for the player
        player = createRobot("player");
        player.setUserData("health", 100f);

        // add simple healthbar
        BillboardControl billboard = new BillboardControl();
        Geometry healthbar = new Geometry("healthbar", new Quad(4f, 0.2f));
        Material mathb = mat.clone();
        mathb.setColor("Color", ColorRGBA.Red);
        healthbar.setMaterial(mathb);
        player.attachChild(healthbar);
        healthbar.center();
        healthbar.move(4, 1, -4);
        healthbar.addControl(billboard);

        // put player in center
        player.move(-2, 0, 0);
        player.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        // add player to scene
        rootNode.attachChild(player);
    }

    private Node createRobot(String name) {
        Node res = new Node(name);

        // create the feet
        Box leftFoot = new Box(0.5f, 0.2f, 1.5f);
        Box rightFoot = new Box(0.5f, 0.2f, 1.5f);
        Geometry geomlf = new Geometry("leftFoot", leftFoot);
        Geometry geomrf = new Geometry("rightFoot", rightFoot);
        Material matf = mat.clone();
        matf.setColor("Color", ColorRGBA.DarkGray);
        geomlf.setMaterial(matf);
        geomrf.setMaterial(matf);
        geomlf.center().move(new Vector3f(1, 0.21f, 0));
        geomrf.center().move(new Vector3f(-1, 0.21f, 0));

        res.attachChild(geomlf);
        res.attachChild(geomrf);

        // create the body
        Cylinder body = new Cylinder(8, 16, 1, 3);
        Geometry bodygeom = new Geometry("body", body);
        Material matb = mat.clone();
        matb.setColor("Color", ColorRGBA.Gray);
        bodygeom.setMaterial(matb);
        bodygeom.center().move(new Vector3f(0, 1.92f, 0));
        bodygeom.rotate(FastMath.DEG_TO_RAD * 90, 0, 0);
        res.attachChild(bodygeom);

        // create the head
        Sphere head = new Sphere(16, 32, 1.5f);
        Geometry headgeom = new Geometry("head", head);
        Material math = mat.clone();
        math.setColor("Color", ColorRGBA.Gray);
        headgeom.setMaterial(math);
        headgeom.center().move(new Vector3f(0, 4.42f, 0));
        res.attachChild(headgeom);

        // create a "nose" to see where robot is heading
        Box nose = new Box(0.2f, 0.2f, 0.2f);
        Geometry nosegeom = new Geometry("head", nose);
        Material matn = mat.clone();
        matn.setColor("Color", ColorRGBA.Orange);
        matn.setColor("GlowColor", ColorRGBA.Orange);
        nosegeom.setMaterial(matn);
        nosegeom.center().move(new Vector3f(0, 4.42f, 1.5f));
        res.attachChild(nosegeom);

        return res;
    }

    private void initializeBloom() {
        // add a glow effect
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        bf = new BloomFilter(BloomFilter.GlowMode.Objects);
        bf.setExposurePower(18f);
        bf.setBloomIntensity(1.0f);
        bf.setBlurScale(maxBlur);
        //bf.setExposureCutOff(1f);
        bf.setDownSamplingFactor(2.2f);
        fpp.addFilter(bf);
        viewPort.addProcessor(fpp);
    }

    private void initializeFloor() {
        // create a floor
        float gridsize = floorsize / 10;
        Box bx = new Box(gridsize / 2, 0.02f, 0.02f);
        Material matx = mat.clone();
        matx.setColor("Color", ColorRGBA.Cyan);
        matx.setColor("GlowColor", ColorRGBA.Blue);
        Box by = new Box(0.02f, 0.02f, gridsize / 2);
        Material maty = mat.clone();
        maty.setColor("Color", ColorRGBA.Cyan);
        maty.setColor("GlowColor", ColorRGBA.Blue);

        Box floor = new Box(gridsize / 2 - 0.01f, 0.01f, gridsize / 2 - 0.01f);
        Material matfloor = mat.clone();
        matfloor.setColor("Color", ColorRGBA.LightGray);

        for (int x = (int) -gridsize * 5; x <= gridsize * 5; x++) {
            for (int y = (int) -gridsize * 5; y <= gridsize * 5; y++) {
                Geometry geomx = new Geometry("Grid", bx);
                geomx.setMaterial(matx);
                geomx.center().move(new Vector3f(x * gridsize, 0, y * gridsize + gridsize / 2));

                rootNode.attachChild(geomx);

                if (y == (int) -gridsize * 5) {
                    Geometry geomxend = geomx.clone();
                    geomxend.center().move(new Vector3f(x * gridsize, 0, y * gridsize - gridsize / 2));
                    rootNode.attachChild(geomxend);
                }

                Geometry geomy = new Geometry("Grid", by);
                geomy.setMaterial(maty);
                geomy.center().move(new Vector3f(x * gridsize + gridsize / 2, 0, y * gridsize));

                rootNode.attachChild(geomy);

                if (x == (int) -gridsize * 5) {
                    Geometry geomyend = geomy.clone();
                    geomyend.center().move(new Vector3f(x * gridsize - gridsize / 2, 0, y * gridsize));
                    rootNode.attachChild(geomyend);
                }

                Geometry geomfloor = new Geometry("Floor", floor);
                geomfloor.setMaterial(matfloor);
                geomfloor.center().move(new Vector3f(x * gridsize, 0, y * gridsize));

                rootNode.attachChild(geomfloor);
            }
        }
    }

    public void onAnalog(String name, float value, float tpf) {
        if (gameFinished) {
            return;
        }
        Vector3f oldPos = player.getLocalTranslation().clone();
        if (name.equals("moveForward")) {
            player.move(player.getLocalRotation().mult(new Vector3f(0, 0, playerMoveSpeed * tpf)));
            if (player.getLocalTranslation().x > floorsize * 2 || player.getLocalTranslation().z > floorsize * 2
                    || player.getLocalTranslation().x < -floorsize * 2 || player.getLocalTranslation().z < -floorsize * 2) {
                player.setLocalTranslation(oldPos);
            }
        }
        if (name.equals("moveBackward")) {
            player.move(player.getLocalRotation().mult(new Vector3f(0, 0, -playerMoveSpeed * tpf)));
            if (player.getLocalTranslation().x > floorsize * 2 || player.getLocalTranslation().z > floorsize * 2
                    || player.getLocalTranslation().x < -floorsize * 2 || player.getLocalTranslation().z < -floorsize * 2) {
                player.setLocalTranslation(oldPos);
            }
        }
        if (name.equals("moveRight")) {
            player.rotate(0, -(FastMath.DEG_TO_RAD * tpf) * turnSpeed, 0);
        }
        if (name.equals("moveLeft")) {
            player.rotate(0, (FastMath.DEG_TO_RAD * tpf) * turnSpeed, 0);
        }
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (gameFinished) {
            return;
        }
        if (name.equals("Shoot") && isPressed) {

            if (playerLaserBeam == null) {
                Vector2f click2d = inputManager.getCursorPosition();
                Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
                Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
                Ray ray = new Ray(click3d, dir);
                Plane ground = new Plane(Vector3f.UNIT_Y, 0);
                Vector3f groundpoint = new Vector3f();
                ray.intersectsWherePlane(ground, groundpoint);

                if (addDebugObjects) {
                    Sphere help = new Sphere(8, 8, 0.2f);
                    helper = new Geometry("helper", help);
                    Material mathelper = mat.clone();
                    mathelper.setColor("Color", ColorRGBA.Red);
                    mathelper.setColor("GlowColor", ColorRGBA.Magenta);
                    helper.setMaterial(mathelper);
                    helper.center().move(groundpoint);
                    rootNode.attachChild(helper);
                    System.out.println("Clicked: x=" + groundpoint.x + "; y=" + groundpoint.z);
                }

                float laserlength = attackRange;

                CollisionResults collsions = new CollisionResults();
                click3d = new Vector3f(player.getLocalTranslation());
                dir = groundpoint.subtract(click3d).normalizeLocal();
                click3d.y = 3;
                ray = new Ray(click3d, dir);
                ray.setLimit(attackRange);
                enemy.collideWith(ray, collsions);
                if (collsions.size() > 0) {
                    float enemyhealth = (Float) enemy.getUserData("health");
                    enemy.setUserData("health", enemyhealth - laserdamage);
                    laserlength = player.getLocalTranslation().distance(enemy.getLocalTranslation());
                }

                Cylinder laser = new Cylinder(4, 8, 0.02f, laserlength);
                playerLaserBeam = new Geometry("laserbeam", laser);
                Material matlaser = mat.clone();
                matlaser.setColor("Color", ColorRGBA.Orange);
                matlaser.setColor("GlowColor", ColorRGBA.Red);
                playerLaserBeam.setMaterial(matlaser);
                // attach laserbeam to player so it moves with player
                player.attachChild(playerLaserBeam);
                // center laserbeam on players origin
                playerLaserBeam.center();
                // make the laserbeam point towards clicked spot
                playerLaserBeam.lookAt(groundpoint, Vector3f.UNIT_Z);
                // move laserbeam up so it does not shoot on ground level, but from player model
                playerLaserBeam.move(new Vector3f(0, 3, 0));
                // move laserbeam forward because cylinder is created with center at player origin
                playerLaserBeam.move(playerLaserBeam.getLocalRotation().mult(new Vector3f(0, 0, laserlength / 2)));
                playerLaserLifetime = lasermaxlifetime;

            }
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        // get player and enemy health
        float enemyHealth = (Float) enemy.getUserData("health");
        float playerHealth = (Float) player.getUserData("health");

        // check if game is over
        if (gameFinished) {
            if (gameFinishCountDown <= 0) {
                this.stop();
                return;
            }
            gameFinishCountDown -= tpf;
            return;
        }

        checkGameState(enemyHealth, playerHealth);

        updateLasers(tpf);

        updateEnemy(tpf);

        updateHealthBars(enemyHealth, playerHealth);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private void checkGameState(float enemyHealth, float playerHealth) {
        // check if we won or lost the game
        if (enemyHealth <= 0) {
            BitmapText hudText = new BitmapText(guiFont, false);
            hudText.setSize(guiFont.getCharSet().getRenderedSize()); // font size
            hudText.setColor(ColorRGBA.Red); // font color
            hudText.setText("You WON !"); // the text
            hudText.setLocalTranslation(settings.getWidth() / 2 - hudText.getLineWidth() / 2, settings.getHeight() / 2 - hudText.getLineHeight() / 2, 0); // position
            guiNode.attachChild(hudText);
            gameFinished = true;
        } else if (playerHealth <= 0) {
            BitmapText hudText = new BitmapText(guiFont, false);
            hudText.setSize(guiFont.getCharSet().getRenderedSize()); // font size
            hudText.setColor(ColorRGBA.Red); // font color
            hudText.setText("You LOST !"); // the text
            hudText.setLocalTranslation(settings.getWidth() / 2 - hudText.getLineWidth() / 2, settings.getHeight() / 2 - hudText.getLineHeight() / 2, 0); // position
            guiNode.attachChild(hudText);
            gameFinished = true;
        }
    }

    private void updateLasers(float tpf) {
        // check if player is firing
        if (playerLaserLifetime <= 0 && playerLaserBeam != null) {
            playerLaserLifetime = 0;
            playerLaserBeam.removeFromParent();
            playerLaserBeam = null;
            if (helper != null) {
                helper.removeFromParent();
                helper = null;
            }
        } else if (playerLaserLifetime > 0 && playerLaserBeam != null) {
            playerLaserLifetime -= tpf;
        }

        // check if enemy is firing
        if (enemyLaserLifetime <= 0 && enemyLaserBeam != null) {
            enemyLaserLifetime = 0;
            enemyLaserBeam.removeFromParent();
            enemyLaserBeam = null;
        } else if (enemyLaserLifetime > 0 && enemyLaserBeam != null) {
            enemyLaserLifetime -= tpf;
        } else if (enemyLaserCooldown > 0) {
            enemyLaserCooldown -= tpf;
        }
    }

    private void updateHealthBars(float enemyHealth, float playerHealth) {
        // update health bars
        ((Quad) ((Geometry) enemy.getChild("healthbar")).getMesh()).updateGeometry(enemyHealth / 100 * 4, 0.2f);
        ((Quad) ((Geometry) player.getChild("healthbar")).getMesh()).updateGeometry(playerHealth / 100 * 4, 0.2f);
    }

    private void updateEnemy(float tpf) {
        // some basic "AI" for the enemy

        // check if player is in attack range
        if (enemy.getLocalTranslation().distance(player.getLocalTranslation()) < attackRange) {
            // show hostile enemy
            ((Geometry) enemy.getChild("head")).getMaterial().setColor("GlowColor", ColorRGBA.Red);

            // get angle from enemy view to player
            Quaternion currentRot = enemy.getLocalRotation();
            currentRot.toAxes(viewAxe);
            forward.setOriginNormal(enemy.getLocalTranslation(), viewAxe[2]);
            sidewards.setOriginNormal(enemy.getLocalTranslation(), viewAxe[0]);

            if (forward.whichSide(player.getLocalTranslation()) == Plane.Side.Positive) {
                if (addDebugObjects && allDebugMessages) {
                    System.out.println("Player is in front of enemy.");
                }
                front = true;
                move = true;
            } else if (forward.whichSide(player.getLocalTranslation()) == Plane.Side.None) {
                if (addDebugObjects && allDebugMessages) {
                    System.out.println("Player is next to enemy.");
                }
                move = false;
            } else {
                if (addDebugObjects && allDebugMessages) {
                    System.out.println("Player is behind of enemy.");
                }
                front = false;
                move = true;
            }

            if (sidewards.whichSide(player.getLocalTranslation()) == Plane.Side.Positive) {
                if (addDebugObjects && allDebugMessages) {
                    System.out.println("Player is left of enemy.");
                }
                left = true;
                turn = true;
            } else if (sidewards.whichSide(player.getLocalTranslation()) == Plane.Side.None) {
                if (addDebugObjects && allDebugMessages) {
                    System.out.println("Player is next to enemy.");
                }
                turn = false;
            } else {
                if (addDebugObjects && allDebugMessages) {
                    System.out.println("Player right of enemy.");
                }
                left = false;
                turn = true;
            }

            // if just entered attack range clear plans and start attack plans
            if (!attacking) {
                plans.clear();
            }

            if (!isEnemyMoving() && move) {
                // make enemy move toward player
                EnemyPlan newPlan = new EnemyPlan(maxActionTime, EnemyPlanType.MOVE, front);
                if (addDebugObjects) {
                    System.out.println("Plan: " + newPlan.type + ", Duration: " + newPlan.duration + ", Direction: " + newPlan.direction + " added.");
                }
                plans.add(newPlan);
            }

            if (!isEnemyTurning() && turn) {
                // check angle from enemy facing to player position
                helperNode.setLocalTranslation(enemy.getLocalTranslation());
                helperNode.lookAt(player.getLocalTranslation(), Vector3f.UNIT_Y);
                Quaternion delta = enemy.getLocalRotation().inverse().multLocal(helperNode.getLocalRotation());

                if (addDebugObjects && allDebugMessages) {
                    System.out.println("Deltaquaternion angle towards player: " + delta.toAngles(null)[1] * FastMath.RAD_TO_DEG);
                }

                // make enemy turn toward player
                EnemyPlan newPlan = new EnemyPlan(FastMath.abs(delta.toAngles(null)[1] / (tpf * turnSpeed)), EnemyPlanType.TURN, left);
                if (addDebugObjects) {
                    System.out.println("Plan: " + newPlan.type + ", Duration: " + newPlan.duration + ", Direction: " + newPlan.direction + " added.");
                }
                plans.add(newPlan);
            }

            // if player is in front of enemy shoot!
            if (front && enemyLaserCooldown <= 0 && enemyLaserBeam == null) {
                float laserlength = attackRange;

                CollisionResults collsions = new CollisionResults();
                Vector3f origin = new Vector3f(enemy.getLocalTranslation());
                Vector3f dir = player.getLocalTranslation().subtract(origin).normalizeLocal();
                origin.y = 3;
                Ray ray = new Ray(origin, dir);
                ray.setLimit(attackRange);
                enemy.collideWith(ray, collsions);
                if (collsions.size() > 0) {
                    float playerhealth = (Float) player.getUserData("health");
                    player.setUserData("health", playerhealth - laserdamage);
                    laserlength = enemy.getLocalTranslation().distance(player.getLocalTranslation());
                }

                Cylinder laser = new Cylinder(4, 8, 0.02f, laserlength);
                enemyLaserBeam = new Geometry("laserbeam", laser);
                Material matlaser = mat.clone();
                matlaser.setColor("Color", ColorRGBA.Green);
                matlaser.setColor("GlowColor", ColorRGBA.Cyan);
                enemyLaserBeam.setMaterial(matlaser);
                // attach laserbeam to player so it moves with player
                enemy.attachChild(enemyLaserBeam);
                // center laserbeam on players origin
                enemyLaserBeam.center();
                // make the laserbeam point towards clicked spot
                enemyLaserBeam.lookAt(player.getLocalTranslation(), Vector3f.UNIT_Z);
                // move laserbeam up so it does not shoot on ground level, but from player model
                enemyLaserBeam.move(new Vector3f(0, 3, 0));
                // move laserbeam forward because cylinder is created with center at player origin
                enemyLaserBeam.move(enemyLaserBeam.getLocalRotation().mult(new Vector3f(0, 0, laserlength / 2)));
                enemyLaserLifetime = lasermaxlifetime;
                enemyLaserCooldown = enemyMaxLaserCooldown;
            }

            updateEnemyPlans(tpf);

            // trigger attack mode
            attacking = true;
        } else {
            // show peacefull enemy
            ((Geometry) enemy.getChild("head")).getMaterial().setColor("GlowColor", ColorRGBA.Green);

            Quaternion currentRot = enemy.getLocalRotation();
            currentRot.toAxes(viewAxe);
            forward.setOriginNormal(enemy.getLocalTranslation(), viewAxe[2]);
            sidewards.setOriginNormal(enemy.getLocalTranslation(), viewAxe[0]);

            if (forward.whichSide(Vector3f.ZERO) == Plane.Side.Positive) {
                if (addDebugObjects && allDebugMessages) {
                    System.out.println("Origin is in front of enemy.");
                }
                front = true;
            } else if (forward.whichSide(Vector3f.ZERO) == Plane.Side.None) {
                if (addDebugObjects && allDebugMessages) {
                    System.out.println("Origin is next to enemy.");
                }
                move = false;
            } else {
                if (addDebugObjects && allDebugMessages) {
                    System.out.println("Origin is behind of enemy.");
                }
                front = false;
            }

            if (sidewards.whichSide(Vector3f.ZERO) == Plane.Side.Positive) {
                if (addDebugObjects && allDebugMessages) {
                    System.out.println("Origin is left of enemy.");
                }
                left = true;
            } else if (sidewards.whichSide(Vector3f.ZERO) == Plane.Side.None) {
                if (addDebugObjects && allDebugMessages) {
                    System.out.println("Origin is next to enemy.");
                }
                turn = false;
            } else {
                if (addDebugObjects && allDebugMessages) {
                    System.out.println("Origin right of enemy.");
                }
                left = false;
            }

            // check if enemy is allready doing something (enemy can do 2 actions at a time)
            if (plans.size() < 2) {
                // if enemy is close to border he is eager to move
                if (!isEnemyMoving() && (enemy.getLocalTranslation().x > floorsize * 1.5f || enemy.getLocalTranslation().z > floorsize * 1.5f
                        || enemy.getLocalTranslation().x < -floorsize * 1.5f || enemy.getLocalTranslation().z < -floorsize * 1.5f)) {
                    EnemyPlan newPlan = new EnemyPlan(maxActionTime, EnemyPlanType.MOVE, front);
                    if (addDebugObjects) {
                        System.out.println("Plan: " + newPlan.type + ", Duration: " + newPlan.duration + ", Direction: " + newPlan.direction + " added.");
                    }
                    plans.add(newPlan);
                } else {
                    // else pick a plan at random
                    switch (FastMath.rand.nextInt(EnemyPlanType.values().length)) {
                        case (0): {
                            // check if enemy is allready moving
                            if (isEnemyMoving()) {
                                break;
                            }
                            // decide to move a bit
                            EnemyPlan newPlan = new EnemyPlan(FastMath.rand.nextFloat() * maxActionTime, EnemyPlanType.MOVE, FastMath.rand.nextBoolean());
                            if (addDebugObjects) {
                                System.out.println("Plan: " + newPlan.type + ", Duration: " + newPlan.duration + ", Direction: " + newPlan.direction + " added.");
                            }
                            plans.add(newPlan);
                            break;
                        }
                        case (1): {
                            // check if enemy is allready turning
                            if (isEnemyTurning()) {
                                break;
                            }
                            // decide to turn a bit
                            EnemyPlan newPlan = new EnemyPlan(FastMath.rand.nextFloat() * maxActionTime, EnemyPlanType.TURN, FastMath.rand.nextBoolean());
                            if (addDebugObjects) {
                                System.out.println("Plan: " + newPlan.type + ", Duration: " + newPlan.duration + ", Direction: " + newPlan.direction + " added.");
                            }
                            plans.add(newPlan);
                            break;
                        }
                        case (2): {
                            // decide to wait a bit
                            EnemyPlan newPlan = new EnemyPlan(FastMath.rand.nextFloat() * maxIdleTime, EnemyPlanType.IDLE, FastMath.rand.nextBoolean());
                            if (addDebugObjects) {
                                System.out.println("Plan: " + newPlan.type + ", Duration: " + newPlan.duration + ", Direction: " + newPlan.direction + " added.");
                            }
                            plans.add(newPlan);
                            break;
                        }
                    }
                }
            }

            updateEnemyPlans(tpf);

            attacking = false;
        }
    }

    private boolean isEnemyMoving() {
        for (EnemyPlan plan : plans) {
            if (plan.type == EnemyPlanType.MOVE) {
                return true;
            }
        }
        return false;
    }

    private boolean isEnemyTurning() {
        for (EnemyPlan plan : plans) {
            if (plan.type == EnemyPlanType.TURN) {
                return true;
            }
        }
        return false;
    }

    private void updateEnemyPlans(float tpf) {
        // process plans
        updatePlans.clear();
        updatePlans.addAll(plans);
        for (EnemyPlan plan : updatePlans) {
            switch (plan.getType()) {
                case IDLE: {
                    plan.update(tpf);
                    break;
                }
                case MOVE: {
                    plan.update(tpf);
                    Vector3f oldPos = enemy.getLocalTranslation().clone();
                    if (plan.direction) {
                        enemy.move(enemy.getLocalRotation().mult(new Vector3f(0, 0, enemyMoveSpeed * tpf)));
                    } else {
                        enemy.move(enemy.getLocalRotation().mult(new Vector3f(0, 0, -enemyMoveSpeed * tpf)));
                    }
                    if (enemy.getLocalTranslation().x > floorsize * 2 || enemy.getLocalTranslation().z > floorsize * 2
                            || enemy.getLocalTranslation().x < -floorsize * 2 || enemy.getLocalTranslation().z < -floorsize * 2) {
                        enemy.setLocalTranslation(oldPos);
                        // if enemy hit border make him move backwards
                        plans.remove(plan);
                        plans.add(new EnemyPlan(plan.getDuration(), plan.getType(), !plan.getDirection()));
                    }
                    break;
                }
                case TURN: {
                    plan.update(tpf);
                    if (plan.direction) {
                        // turn left
                        enemy.rotate(0, (FastMath.DEG_TO_RAD * tpf) * turnSpeed, 0);
                    } else {
                        // turn right
                        enemy.rotate(0, -(FastMath.DEG_TO_RAD * tpf) * turnSpeed, 0);
                    }
                    break;
                }
            }
            if (plan.isFinished()) {
                plans.remove(plan);
            }
        }
    }

    private enum EnemyPlanType {

        IDLE,
        TURN,
        MOVE;
    }

    private class EnemyPlan {

        private float duration;
        private EnemyPlanType type;
        private boolean direction;

        public EnemyPlan(float duration, EnemyPlanType type, boolean direction) {
            this.duration = duration;
            this.type = type;
            this.direction = direction;
        }

        public float getDuration() {
            return this.duration;
        }

        public EnemyPlanType getType() {
            return this.type;
        }

        public void update(float tpf) {
            this.duration -= tpf;
        }

        public boolean isFinished() {
            return this.duration <= 0;
        }

        public boolean getDirection() {
            return this.direction;
        }
    }
}