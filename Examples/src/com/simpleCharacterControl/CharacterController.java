/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simpleCharacterControl;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;

public class CharacterController extends AbstractAppState implements ActionListener {

    private Application app;
    private String[] mappings;
    boolean jump = false, forward = false, backward = false,
            leftRotate = false, rightRotate = false;
    private SimpleCharacterControl charControl;

    public CharacterController(Application app, SimpleCharacterControl charControl) {
        this.app = app;
        this.charControl = charControl;
        setupKeys();
    }

    private void setupKeys() {
        InputManager inputManager = app.getInputManager();

        inputManager.addMapping("Rotate Left",
                new KeyTrigger(KeyInput.KEY_A),
                new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Rotate Right",
                new KeyTrigger(KeyInput.KEY_D),
                new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Walk Forward",
                new KeyTrigger(KeyInput.KEY_W),
                new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Walk Backward",
                new KeyTrigger(KeyInput.KEY_S),
                new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("Jump",
                new KeyTrigger(KeyInput.KEY_SPACE));

//        inputManager.addListener(this, "Strafe Left", "Strafe Right");
        inputManager.addListener(this, "Rotate Left", "Rotate Right");
        inputManager.addListener(this, "Walk Forward", "Walk Backward");
        inputManager.addListener(this, "Jump");

        inputManager = null;
    }

    public void addListener() {
        app.getInputManager().addListener(this, mappings);
    }

    public void removeListener() {
        app.getInputManager().removeListener(this);
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Rotate Left")) {
            if (value) {
                leftRotate = true;
            } else {
                leftRotate = false;
            }
        } else if (binding.equals("Rotate Right")) {
            if (value) {
                rightRotate = true;
            } else {
                rightRotate = false;
            }
        } else if (binding.equals("Walk Forward")) {
            if (value) {
                forward = true;
            } else {
                forward = false;
//                doStop = true;
            }
        } else if (binding.equals("Walk Backward")) {
            if (value) {
                backward = true;
            } else {
                backward = false;
            }
        } else if (binding.equals("Jump")) {
            if (value) {
                jump = true;
            } else {
                jump = false;
            }
        }
    }

    @Override
    public void update(float tpf) {

        if (forward ^ backward) {
//            float yCoord = charControl.getRigidBody().getLinearVelocity().getY();
            Vector3f charLocation = charControl.getPhysicsLocation();
            Vector3f walkDir = charLocation.subtract(app.getCamera().getLocation().clone().setY(charLocation.getY())).normalizeLocal();
            if (backward) {
                walkDir.negateLocal();
            }

            charControl.setWalkDirection(walkDir);
            charControl.setMove(true);
        } else {
            charControl.setMove(false);
        }

        if (jump) {
            charControl.setJump();
            jump = false;
        }
        Vector3f camdir = app.getCamera().getDirection().clone();
        Quaternion newRot = new Quaternion();
        newRot.lookAt(camdir.setY(0), Vector3f.UNIT_Y);
        charControl.setRotationInUpdate(newRot);

    }

    @Override
    public void cleanup() {
        super.cleanup();
    }
}
