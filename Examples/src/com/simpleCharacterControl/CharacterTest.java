package com.simpleCharacterControl;

import SimpleChaseCamera.SimpleCameraState;
import com.basics.*;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class CharacterTest extends SimpleApplication {

    public static void main(String[] args) {
        CharacterTest app = new CharacterTest();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        starting();
        flyCam.setEnabled(false);

        // set physics for the client
        BulletAppState bulletAppState = new BulletAppState(PhysicsSpace.BroadphaseType.DBVT); // DBVT is dynamic scale of World Size
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);  // use physics on a separete thread
        stateManager.attach(bulletAppState);

        Node scene = (Node) assetManager.loadModel("Models/test/testScene.j3o");

//        SimpleCameraState camera = new SimpleCameraState(this);
//        stateManager.attach(camera);
//            camera.getChState().setVerticalDownLimit(0.1f);


        // setup scene
        for (Spatial sp : scene.getChildren()) {
            Node nd = (Node) sp;

            // setup character
            if (nd.getName().equals("character")) {

                nd.setLocalTranslation(nd.getLocalTranslation().addLocal(new Vector3f(0, 20, 0)));
                Geometry geo = (Geometry) nd.getChild(0);
                CapsuleCollisionShape enShape = new CapsuleCollisionShape(0.5f, 1, 1);
                SimpleCharacterControl physContr = new SimpleCharacterControl(this, enShape.getHeight(), enShape, 40f);
                physContr.setPhysicsLocation(nd.getWorldTranslation());
                physContr.setPhysicsRotation(nd.getLocalRotation());
                physContr.setDamping(0.5f, 0.5f);
                physContr.setSleepingThresholds(0.7f, 0.7f);
                physContr.setFriction(1f);
                physContr.setAngularFactor(0f); // this is for better collisions


                nd.addControl(physContr);
                stateManager.getState(BulletAppState.class).getPhysicsSpace().add(physContr);
                physContr.setGravity(Vector3f.UNIT_Y.negate().mult(20));

                System.out.println(enShape.getHeight());
//                charControl.setJumpSpeed(8.0f);
                physContr.setMoveSpeed(4.5f);
                physContr.setMoveSlopeSpeed(0.3f);


//                camera.getChState().setSpatialToFollow(sp);
                ChaseCamera chCam = new ChaseCamera(cam, sp, inputManager);
                nd.addControl(physContr);

                CharacterController charController = new CharacterController(this, physContr);
                stateManager.attach(charController);

            } // setup other objects
            else {
                Geometry geo = (Geometry) nd.getChild(0);
                CollisionShape mshShape = CollisionShapeFactory.createMeshShape(nd);
                float mass = 0;

                if (nd.getName().indexOf("Box") == 0) {
                    mshShape = new BoxCollisionShape(new Vector3f(0.5f, 0.5f, 0.5f));
                    mass = 70;
                }

                mshShape.setScale(nd.getLocalScale());

                RigidBodyControl physContr = new RigidBodyControl(mshShape, mass);
                physContr.setPhysicsLocation(nd.getWorldTranslation());
                physContr.setPhysicsRotation(nd.getLocalRotation());
                physContr.setFriction(0.1f);

                if (nd.getName().indexOf("Box") == 0) {
                    physContr.setFriction(0.2f);
                    physContr.setSleepingThresholds(0.7f, 0.7f);
                    physContr.setDamping(1.0f, 1.0f);
                    physContr.setAngularFactor(0.3f); // this is for better collisions
                }

                nd.addControl(physContr);
                stateManager.getState(BulletAppState.class).getPhysicsSpace().add(physContr);
            }
        }

        Node root = (Node) viewPort.getScenes().get(0);
        root.attachChild(scene);

        DirectionalLight dl = new DirectionalLight();
        root.addLight(dl);
        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(1, 1, 1.5f, 1));
        root.addLight(al);
        stateManager.getState(BulletAppState.class).setDebugEnabled(true);

    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    public void starting() {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize());
        ch.setText("WASD, LeftClick - This is SimpleCharacterControl"); // crosshairs
        ch.setColor(new ColorRGBA(1f, 0.8f, 0.1f, 1f));
        ch.setLocalTranslation(settings.getWidth() * 0.3f, settings.getHeight() * 0.1f, 0);
        guiNode.attachChild(ch);

        viewPort.setBackgroundColor(ColorRGBA.Gray);
        flyCam.setMoveSpeed(30);
    }
}
