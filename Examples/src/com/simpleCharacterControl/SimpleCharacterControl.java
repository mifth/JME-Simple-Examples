/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simpleCharacterControl;

import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.util.List;

public class SimpleCharacterControl extends RigidBodyControl implements PhysicsTickListener {

    private Application app;
    private boolean doMove, doJump, hasJumped = false;
    private Vector3f walkDirection = Vector3f.ZERO;
    private Vector3f additiveJumpSpeed = Vector3f.ZERO;
    private Quaternion newRotation;
    private int stopTimer, jumpTimer, maxStopTimer, maxJumpTimer;
    private boolean hasMoved = false;
    private float angleNormals = 0;
    private PhysicsRayTestResult physicsClosestTets;

    private float jumpSpeedY, moveSpeed, moveSpeedMultiplier, moveSlopeSpeed, slopeLimitAngle, stopDamping, centerToBottomHeight;
    private float frictionWalk, frictionStop, mainWalkInterpolation, otherWalkInterpolation;

    public SimpleCharacterControl(Application app, float centerToBottomHeight, CollisionShape colShape, float mass) {
        super(colShape, mass);
        
        this.app = app;

        jumpSpeedY = 40f;
        moveSpeed = 0.5f;
        moveSpeedMultiplier = 1;
        moveSlopeSpeed = 0.3f;
        slopeLimitAngle = FastMath.DEG_TO_RAD * 45f;
        stopDamping = 0.8f;
        
        stopTimer = 0;
        jumpTimer = 0;
        maxStopTimer = 30;
        maxJumpTimer = 20;
        
        frictionWalk = 0.1f;
        frictionStop = 7f;
        mainWalkInterpolation = 0.7f;
        otherWalkInterpolation = 0.9f;
        
        this.centerToBottomHeight = centerToBottomHeight;

        this.app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().addTickListener(this);
    }

    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        if (spatial != null && newRotation != null) {
            spatial.setLocalRotation(newRotation);
//            newRotation = null;
        }
    }

//    @Override
//    protected void controlRender(RenderManager rm, ViewPort vp) {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    public void prePhysicsTick(PhysicsSpace space, float tpf) {

        if (physicsClosestTets != null) {
            angleNormals = physicsClosestTets.getHitNormalLocal().normalizeLocal().angleBetween(Vector3f.UNIT_Y);

        }

        if (angleNormals < slopeLimitAngle && physicsClosestTets != null && (!doMove && !doJump && !hasJumped)) {
            this.setFriction(frictionStop);
        } else {
            this.setFriction(frictionWalk);
        }

        if (doMove) {

            Vector3f moveCharVec = walkDirection.mult(moveSpeed * moveSpeedMultiplier);
            moveCharVec.setY(this.getLinearVelocity().getY());

            if ((angleNormals < slopeLimitAngle && physicsClosestTets != null) || !this.isActive()) {

                this.setLinearVelocity(moveCharVec.interpolate(this.getLinearVelocity(), mainWalkInterpolation));


            } else if (angleNormals > slopeLimitAngle && angleNormals < FastMath.DEG_TO_RAD * 80f && physicsClosestTets != null) {
                this.applyCentralForce((walkDirection.mult(moveSlopeSpeed).setY(0f)));
//                this.setLinearVelocity(moveCharVec.interpolate(this.getLinearVelocity(), 0.99f));
            } else {
//                physSp.applyCentralForce((walkDirection.mult(moveSlopeSpeed).setY(0f)));
//                this.setLinearVelocity(moveCharVec.setY(this.getLinearVelocity().getY()));
                this.setLinearVelocity(moveCharVec.interpolate(this.getLinearVelocity(), otherWalkInterpolation));
            }
            hasMoved = true;
            hasJumped = false;
            stopTimer = 0;
            jumpTimer = 0;

        }

        if (jumpTimer > 0) {
            if (jumpTimer > maxJumpTimer) {
                jumpTimer = 0;
            } else {
                jumpTimer++;
            }
        }

        if (doJump && !hasJumped && (physicsClosestTets != null || !this.isActive())) {
            if ((angleNormals < slopeLimitAngle)) {
//                this.clearForces();
                this.setLinearVelocity(this.getLinearVelocity().add(Vector3f.UNIT_Y.clone().multLocal(jumpSpeedY).addLocal(additiveJumpSpeed)));
//                physSp.applyImpulse(Vector3f.UNIT_Y.mult(jumpSpeed), Vector3f.ZERO);
                hasJumped = true;
                jumpTimer = 1;
            }
        }


        // Stop the char
        if ((hasMoved || hasJumped) && physicsClosestTets != null && angleNormals < slopeLimitAngle && !doMove) {

            if (hasJumped && hasMoved) {
                hasJumped = false;
                jumpTimer = 0;
            }

            if (stopTimer < maxStopTimer && jumpTimer == 0) {
//                this.setLinearDamping(1f);
//                this.setFriction(10f);
                this.setLinearVelocity(this.getLinearVelocity().multLocal(new Vector3f(stopDamping, 1, stopDamping)));
                stopTimer += 1;
            } else {
                if (jumpTimer == 0) {
//                    this.setLinearDamping(0.5f);
//                    this.setFriction(0.3f);
                    stopTimer = 0;
                    hasMoved = false;
                    hasJumped = false;
                    jumpTimer = 0;
                }

            }
        }
//        else {
//            this.setLinearDamping(0.5f);
//            this.setFriction(0.3f);
//        }

        if (doJump) {
            doJump = false; // set it after damping
        }
    }

    public void physicsTick(PhysicsSpace space, float tpf) {
        physicsClosestTets = null;
        angleNormals = 0f;
        float closestFraction = centerToBottomHeight * 10f;

        if (this.isActive()) {
            List<PhysicsRayTestResult> results = space.rayTest(this.getPhysicsLocation().add(Vector3f.UNIT_Y.mult(-0.8f * centerToBottomHeight)),
                    this.getPhysicsLocation().add(Vector3f.UNIT_Y.mult(-1.3f * centerToBottomHeight)));
            for (PhysicsRayTestResult physicsRayTestResult : results) {

                if (physicsRayTestResult.getHitFraction() < closestFraction && !physicsRayTestResult.getCollisionObject().getUserObject().equals(spatial)
                        && physicsRayTestResult.getCollisionObject() instanceof GhostControl == false) {
                    physicsClosestTets = physicsRayTestResult;
                    closestFraction = physicsRayTestResult.getHitFraction();
                }
            }
        }
    }

    // DESTROY METHOD
    public void destroy() {
        physicsClosestTets = null;
        walkDirection = null;
        app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().removeTickListener(this);
//        spatial.removeControl(this);
        app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().remove(this);
        
        app = null;
        spatial.removeControl(this);
//        this = null;

    }

    public Vector3f getWalkDirection() {
        return walkDirection;
    }

    public void setWalkDirection(Vector3f walkDirection) {
        this.walkDirection = walkDirection;
    }

    public void setRotationInUpdate(Quaternion newRotation) {
        this.newRotation = newRotation;
    }

    public boolean isDoMove() {
        return doMove;
    }

    public void setMove(boolean doMove) {
        this.doMove = doMove;
    }

    public void setJump() {
        this.doJump = true;
    }

    public float getJumpSpeed() {
        return jumpSpeedY;
    }

    public void setJumpSpeed(float jumpSpeed) {
        this.jumpSpeedY = jumpSpeed;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public float getMoveSlopeSpeed() {
        return moveSlopeSpeed;
    }

    public void setMoveSlopeSpeed(float moveSlopeSpeed) {
        this.moveSlopeSpeed = moveSlopeSpeed;
    }

    public float getSlopeLimitAngle() {
        return slopeLimitAngle;
    }

    public void setSlopeLimitAngle(float slopeLimitAngle) {
        this.slopeLimitAngle = slopeLimitAngle;
    }

    public float getStopDamping() {
        return stopDamping;
    }

    public void setStopDamping(float stopDamping) {
        this.stopDamping = stopDamping;
    }

    public Vector3f getAdditiveJumpSpeed() {
        return additiveJumpSpeed;
    }

    public void setAdditiveJumpSpeed(Vector3f additiveJumpSpeed) {
        this.additiveJumpSpeed = additiveJumpSpeed;
    }

    public float getMoveSpeedMultiplier() {
        return moveSpeedMultiplier;
    }

    public void setMoveSpeedMultiplier(float moveSpeedMultiplier) {
        this.moveSpeedMultiplier = moveSpeedMultiplier;
    }

    public float getFrictionWalk() {
        return frictionWalk;
    }

    public void setFrictionWalk(float frictionWalk) {
        this.frictionWalk = frictionWalk;
    }

    public float getFrictionStop() {
        return frictionStop;
    }

    public void setFrictionStop(float frictionStop) {
        this.frictionStop = frictionStop;
    }

    public float getMainWalkInterpolation() {
        return mainWalkInterpolation;
    }

    public void setMainWalkInterpolation(float mainWalkInterpolation) {
        this.mainWalkInterpolation = mainWalkInterpolation;
    }

    public float getOtherWalkInterpolation() {
        return otherWalkInterpolation;
    }

    public void setOtherWalkInterpolation(float otherWalkInterpolation) {
        this.otherWalkInterpolation = otherWalkInterpolation;
    }

    public int getMaxStopTimer() {
        return maxStopTimer;
    }

    public void setMaxStopTimer(int maxStopTimer) {
        this.maxStopTimer = maxStopTimer;
    }

    public int getMaxJumpTimer() {
        return maxJumpTimer;
    }

    public void setMaxJumpTimer(int maxJumpTimer) {
        this.maxJumpTimer = maxJumpTimer;
    }
    
}
