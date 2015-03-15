/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spaceship;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import java.io.IOException;

/**
 *
 * @author mifth
 */
public class PlayerControl extends AbstractControl implements Savable, Cloneable {

    private float angle;
    private Node ship;
    private ShipPhysicsControl shipControl;
    private BulletAppState bulletAppState;
    private AssetManager assetManager;
    private SimpleApplication asm;
    private ShipWeaponControl weaponControl;
    private boolean doMove = false;
    private boolean doRotate = true;
    private float rotateSpeed;
    private Camera cam;

    public PlayerControl(Camera cam, Node ship, BulletAppState bulletAppState,
            AssetManager assetManager, SimpleApplication asm) {

        this.cam = cam;
        this.ship = ship;
        this.bulletAppState = bulletAppState;
        this.assetManager = assetManager;
        this.asm = asm;

        setShip();

        shipControl.setMoveSpeed(60f);
        rotateSpeed = 150f;

        weaponControl = new ShipWeaponControl(asm, ship);
        ship.addControl(weaponControl);

    }

    private void setShip() {

        Box b = new Box(Vector3f.ZERO, 0.5f, 0.5f, 1);
        Geometry geomShip = new Geometry("Box", b);
        geomShip.setUserData("Type", "Player");

        ship.attachChild(geomShip);
        ship.setUserData("Type", "Player");

        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        ship.setMaterial(mat);

        CollisionShape colShape = new BoxCollisionShape(new Vector3f(1.0f, 1.0f, 1.0f));
        colShape.setMargin(0.05f);
        shipControl = new ShipPhysicsControl(colShape, 1, bulletAppState);

        shipControl.setDamping(0.75f, 0.999f);
        shipControl.setFriction(0.2f);
        shipControl.setAngularFactor(0.1f);

        ship.addControl(shipControl);
        bulletAppState.getPhysicsSpace().add(shipControl);
        shipControl.setEnabled(true);

//        shipControl.setGravity(new Vector3f(0, 0, 0));        
    }

    void makeMove(boolean boo) {
        if (boo) {
            doMove = true;
        } else {
            doMove = false;
        }
    }

    void makeRotate(boolean boo) {
        if (boo) {
            doRotate = true;
        } else {
            doRotate = false;
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (doMove) {
            shipControl.setFlyDirection(cam.getDirection().normalizeLocal());
        }

        if (doRotate) {

//            System.out.println(shipControl.getAngularVelocity());
            angle = cam.getRotation().mult(Vector3f.UNIT_Z).normalizeLocal().angleBetween(shipControl.getPhysicsRotation().mult(Vector3f.UNIT_Z).normalizeLocal());
//            System.out.println(angle + " ANGLE");

            shipControl.setViewDirection(cam.getRotation().clone());


            if (angle >= 0.0001f) {
                shipControl.setRotateSpeed(rotateSpeed * angle);
            } else {
                shipControl.setViewDirection(null);
                if (!shipControl.getAngularVelocity().equals(Vector3f.ZERO)) {
                    shipControl.setAngularVelocity(new Vector3f(0, 0, 0));
                }
            }

//            shipControl.setAngle(angle);

        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    public Control cloneForSpatial(Spatial spatial) {
//        PlayerControl control = new PlayerControl();
//        //TODO: copy parameters to new Control
//        control.setSpatial(spatial);
//        return control;
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }
}
