package com.intermediate;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;


public class ShootBulletRun extends SimpleApplication {


    
    public static void main(String[] args) {
        ShootBulletRun app = new ShootBulletRun();
        
        //set vSinc on to get stable 60 fps
        AppSettings aps = new AppSettings(true);
        aps.setVSync(true);
        app.setSettings(aps);
        app.start();
    }

ShootBulletControlMove shb; 
//ShootBulletControl sbc;
Spatial charShoot;
Spatial enemy1;
Spatial enemy2;
Material mat_box;
Box box_char = new Box(Vector3f.ZERO, 1, 1, 1);
boolean spaBoo = false;
Node enemyNode = new Node();
int h1 = 100;
int h2 = 100;

 public void shootObj() {
    
        // Create a blue box Geometry
        
        charShoot = new Geometry("Box", box_char);
        charShoot.scale(1,1,2);
        
        mat_box = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_box.setColor("Color", ColorRGBA.Blue);
        charShoot.setMaterial(mat_box);
        charShoot.updateModelBound();
        
        rootNode.attachChild(charShoot);     
        
        //Create enemies
        enemy1 = charShoot.clone(false);
        enemy1.setLocalTranslation(15f,0,0);
        enemy1.setUserData("health", h1);
        enemyNode.attachChild(enemy1);     
        
        enemy2 = enemy1.clone(false);
        enemy2.setLocalTranslation(-15f,0,0);
        enemy2.setUserData("health", h2);
        enemyNode.attachChild(enemy2);     
        
        rootNode.attachChild(enemyNode);

    }    

    @Override
    public void simpleInitApp() {

      shootObj();
      starting();
                   
    }
    

    @Override
public void simpleUpdate(float tpf)
{
      
    charShoot.rotate(0,2f*tpf,0);  
    
    System.out.println(charShoot.getUserData("qwerty"));    
    if (spaBoo == false){
        charShoot.addControl(new ShootBulletControlCreate(charShoot,this));
        spaBoo=true;
    }
    
 }
 
    public void starting () {
         guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize());
        ch.setText("Shooting Cube!"); // crosshairs
        ch.setColor(new ColorRGBA(1f,0.8f,0.1f,1f));
        ch.setLocalTranslation(settings.getWidth()*0.3f,settings.getHeight()*0.1f,0);
        guiNode.attachChild(ch);
        
        viewPort.setBackgroundColor(ColorRGBA.Gray);
        flyCam.setMoveSpeed(30);
        cam.setLocation(new Vector3f(0f,3f,30f));
        
    }

    
}
