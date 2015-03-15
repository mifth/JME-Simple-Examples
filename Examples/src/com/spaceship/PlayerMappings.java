/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spaceship;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.scene.Node;




public class PlayerMappings {

    private SimpleApplication asm;
    private Node ship;
    private PlayerControl playerControl;
    private ShipWeaponControl weaponControl;  
    private ChaseCamera chase;
    
    public PlayerMappings (SimpleApplication asm, Node ship, 
            PlayerControl playerControl, ChaseCamera chase) {
        
        
        this.asm = asm;
        this.ship = ship;
        this.chase = chase;
        this.playerControl = playerControl;
        weaponControl = this.ship.getControl(ShipWeaponControl.class);
        setupKeys(this.asm.getInputManager());
        
    }
    
    
    
    private void setupKeys(InputManager inputManager){
     
       //Set up keys and listener to read it
        String[] mappings = new String[]{
            "MoveShip",
            "FireBullets",
            "FireRocket",
            "LockMouse",
            "SelectAim"
        };
        
        InputManager input = asm.getInputManager();
        
        input.addMapping("LockMouse", new KeyTrigger(KeyInput.KEY_SPACE));
        input.addMapping("SelectAim", new KeyTrigger(KeyInput.KEY_E));
        input.addMapping("MoveShip", new KeyTrigger(KeyInput.KEY_W));
        input.addMapping("FireBullets", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        input.addListener(anl, mappings);
        input.addListener(acl, mappings);
    }
    
    
    
    AnalogListener anl = new AnalogListener() {
      public void onAnalog(String name, float value, float tpf) {
 
      }    
    };
    
    ActionListener acl = new ActionListener() {
      public void onAction(String name, boolean isPressed, float tpf) {
        if (isPressed && name.equals("MoveShip")) {
            playerControl.makeMove(true);
        } else if (!isPressed && name.equals("MoveShip")) {
            playerControl.makeMove(false);
        }
        
        if (isPressed && name.equals("FireBullets")) {
            weaponControl.setFireBullets(true);
//          Bullet shipbullets = new Bullet(aim, bullet.clone(false));
        } else if (!isPressed && name.equals("FireBullets")) {
            weaponControl.setFireBullets(false);
        }  
       
        if (isPressed && name.equals("LockMouse")) {
            if (chase.isDragToRotate()) chase.setDragToRotate(false);
            else chase.setDragToRotate(true);
        }
        
        if (isPressed && name.equals("SelectAim")) {
            ship.getControl(AimingControl.class).setAim();
        }        
        
    } 
  };

    
    
}
