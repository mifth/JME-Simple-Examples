/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spaceship;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.export.Savable;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;

/**
 *
 * @author mifth
 */
public class ShipWeaponControl extends  AbstractControl implements Savable, Cloneable, PhysicsCollisionListener  {

    
    private Geometry bullet;
    private Vector3f aim;
    private boolean fire;
    float bulletTimer;
    private Node bullets;
    private Node shipNode;
    private SimpleApplication asm;
    private CollisionShape shape;
    private BulletAppState bulletApp;
    
    public ShipWeaponControl(SimpleApplication asm, Node ship) {
        
        shipNode = ship;
        this.asm = asm;
        
         shape = new BoxCollisionShape(new Vector3f(0.15f, 0.15f, 0.5f));
         bullets = new Node("bullets");
         bulletTimer = 0f;
         fire = false;
        
        // Setup Bullet
        Box b = new Box(Vector3f.ZERO, 0.15f, 0.15f, 0.5f);
        bullet = new Geometry("Box", b);
        Material mat_bullet = new Material(asm.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat_bullet.setColor("Color", ColorRGBA.Red);
        bullet.setMaterial(mat_bullet);
        
        this.asm.getRootNode().attachChild(bullets);
                // add ourselves as collision listener
        
        bulletApp = asm.getStateManager().getState(BulletAppState.class);
        
        bulletApp.getPhysicsSpace().addCollisionListener(this);
        
    }
    
    boolean getFireBullets() {
        return fire;
    }

    void setFireBullets(boolean fireSet) {
        fire = fireSet;
    }    
    
    @Override
    protected void controlUpdate(float tpf) {

      if (fire) {
        bulletTimer += tpf;
        
        if (bulletTimer > 0.07f) {
            Geometry newBullet = bullet.clone(false);
            newBullet.setLocalRotation(shipNode.getControl(RigidBodyControl.class).getPhysicsRotation().clone());
            newBullet.setLocalTranslation(shipNode.getControl(RigidBodyControl.class).getPhysicsLocation().clone());
            newBullet.addControl(new BulletControl(newBullet.getLocalTranslation(), newBullet, bulletApp, shape, asm));
            asm.getRootNode().attachChild(newBullet);
            bulletTimer = 0f;
      } 
     } else if (!fire) {
         bulletTimer = 0f;
     }   
    } 

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void collision(PhysicsCollisionEvent event) {

        
//        if ((event.getNodeA() != null && event.getNodeB() != null)){
//        Spatial A = event.getNodeA();
//        Spatial B = event.getNodeB();
////        System.out.println(A + " and " + B);
////                System.out.println(event.getLocalPointA());
//        Geometry geoBullet;
//        
//        // Destroy Bullets if they collide with Asteroids
//        if((A.getUserData("Type").equals("Bullet") ||
//           B.getUserData("Type").equals("Bullet")) &&
//               (A.getUserData("Type").equals("Asteroid") ||
//           B.getUserData("Type").equals("Asteroid"))) {
//            
//            if(A.getUserData("Type").equals("Bullet")) geoBullet = (Geometry) A;
//            else geoBullet = (Geometry) B;
//            
//            if(A.getUserData("Type").equals("Asteroid") ||
//            B.getUserData("Type").equals("Asteroid")) {
//               geoBullet.getControl(Bullet.class).destroy(); 
//            }
//          }
//        geoBullet = null;
//        A = null;
//        B = null;
//        }
        
      }  

}
