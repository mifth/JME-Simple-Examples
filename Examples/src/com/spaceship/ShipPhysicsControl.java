/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spaceship;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 *
 * @author mifth
 */
public class ShipPhysicsControl extends RigidBodyControl {

    private float rotateSpeed, moveSpeed;

    private Quaternion viewDir;
    private Vector3f moveDir;
    
    public ShipPhysicsControl(CollisionShape shape, float mass, BulletAppState aps) {
        super(shape, mass);
        
        moveSpeed = 1f;
        rotateSpeed = 1f;
//        angle = 1f;
        
        aps.getPhysicsSpace().addTickListener(physics);        
    }


//    public void setAngle(float angle) {
//        this.angle = angle;
//    }
    
    public void setMoveSpeed(float value) {
        moveSpeed = value;
    }
    
    public void setRotateSpeed(float value) {
        rotateSpeed = value;
    }    

    public void setFlyDirection(Vector3f direction) {
        moveDir = direction;
    }

    public void setViewDirection(Quaternion viewDirection) {
        viewDir = viewDirection;
    }    
    
    PhysicsTickListener physics = new PhysicsTickListener() {

        public void prePhysicsTick(PhysicsSpace space, float f) {
            
//   float angle = cam.getRotation().mult(Vector3f.UNIT_Z).normalizeLocal().angleBetween(getPhysicsRotation().clone().mult(Vector3f.UNIT_Z).normalizeLocal());
//    System.out.println(angle);
    
    // Ship Movement
    if (moveDir != null) {
        applyCentralForce(moveDir.mult(moveSpeed));
//        setLinearVelocity(moveDir.mult(moveSpeed));  // another approach of movement        
        moveDir = null;
    }
 
        // Ship Rotation
        if (viewDir != null) {
            Vector3f dirSpatial = getPhysicsRotation().mult(Vector3f.UNIT_Z);
            Vector3f dirCam = viewDir.mult(Vector3f.UNIT_Z);
            Vector3f cross = dirSpatial.cross(dirCam);

            Vector3f dirSpatial1 = getPhysicsRotation().mult(Vector3f.UNIT_Y);
            Vector3f dirCam1 = viewDir.mult(Vector3f.UNIT_Y);
            Vector3f cross1 = dirSpatial1.cross(dirCam1);

            Vector3f dirSpatial2 = getPhysicsRotation().mult(Vector3f.UNIT_X);
            Vector3f dirCam2 = viewDir.mult(Vector3f.UNIT_X);
            Vector3f cross2 = dirSpatial2.cross(dirCam2);

            applyTorque(getAngularVelocity().negate().multLocal(10f));
            applyTorque(cross.addLocal(cross1).addLocal(cross2).normalizeLocal().multLocal(rotateSpeed));
            
//            setAngularVelocity(cross.addLocal(cross1).addLocal(cross2).normalizeLocal().multLocal(rotateSpeed*0.1f));

            viewDir = null;
        }
 

        }

        public void physicsTick(PhysicsSpace space, float f) {

        }
    };

    
}
