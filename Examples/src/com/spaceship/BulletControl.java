/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spaceship;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.export.Savable;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import java.util.List;

/**
 *
 * @author mifth
 */
public class BulletControl extends AbstractControl implements Savable, Cloneable {

    private Geometry bullet;
    private Vector3f vecMove, bornPlace, contactPoint;
    private BulletAppState state;
    private boolean work;
    private float bulletLength;
    private float hit;
    private Spatial sp;
    private SimpleApplication asm;
    
    public BulletControl(Vector3f bornPlace, Geometry bullet, BulletAppState state, CollisionShape shape, SimpleApplication asm) {

        this.bullet = bullet;
        this.bullet.setUserData("Type", "Bullet");
        this.state = state;
        this.bornPlace = bornPlace.clone();
        this.asm = asm;
        
        hit = 1000f;
        
        vecMove = bullet.getLocalRotation().mult(Vector3f.UNIT_Z).normalizeLocal().mult(7f);        
        bulletLength = 100f;
        work = true;
        
//        // testRay
//        Geometry geoRay = new Geometry("line", new Line(bullet.getLocalTranslation().clone(), bullet.getLocalTranslation().add(bullet.getLocalRotation().mult(Vector3f.UNIT_Z).normalizeLocal().mult(bulletLength))));
//        Material mat_bullet = new Material(asm.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//        mat_bullet.setColor("Color", ColorRGBA.Red);
//        geoRay.setMaterial(mat_bullet);
//        asm.getRootNode().attachChild(geoRay);
        
        List<PhysicsRayTestResult> rayTest = this.state.getPhysicsSpace().rayTest(this.bornPlace.add(bullet.getLocalRotation().mult(Vector3f.UNIT_Z).normalizeLocal()), this.bornPlace.add(bullet.getLocalRotation().mult(Vector3f.UNIT_Z).normalizeLocal().mult(bulletLength)));
        if (rayTest.size() > 0) {
            for (Object obj : rayTest) {
            PhysicsRayTestResult getObject = (PhysicsRayTestResult) obj;
            float fl = getObject.getHitFraction();
            PhysicsCollisionObject collisionObject = getObject.getCollisionObject();
            Spatial spThis = (Spatial) collisionObject.getUserObject();
            
            if (fl < hit && !spThis.getUserData("Type").equals("Player")) {
                hit = fl;
                sp = spThis;
            } 
            
            }

            System.out.println(rayTest.size());
            
          if (!sp.getUserData("Type").equals("Bullet") && !sp.getUserData("Type").equals("Player") 
                  && !sp.getUserData("Type").equals("Shit")) { 
              
//            float hit = getObject.getHitFraction();            
            System.out.println(hit);
        Vector3f vecHit = this.bornPlace.add(bullet.getLocalRotation().clone().mult(Vector3f.UNIT_Z).normalizeLocal().mult(bulletLength * hit));    
        
        
        contactPoint = vecHit;    
              
             }
           }        
        
    }

    protected void destroy() {

        work = false;
        bullet.removeFromParent();
        bullet.removeControl(this);
        bullet = null;
        
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(work) {    
            float distance = bornPlace.distance(bullet.getLocalTranslation());
            
            if(contactPoint != null) {
                System.out.println("eeyyyyy");
            float contactPointDistance = bornPlace.distance(contactPoint);
            
             if (distance >= contactPointDistance) {
                 Node nd = new Node("expl");
                 nd.addControl(new ExplosionControl(contactPoint, nd, asm.getAssetManager()));
                 asm.getRootNode().attachChild(nd);
                 destroy();
                 return;                
             }
            }
            
            if(distance >= bulletLength) {
                destroy();
                return;
            }
            
             bullet.move(vecMove);                            
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
