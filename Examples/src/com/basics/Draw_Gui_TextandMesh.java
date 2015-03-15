package com.basics;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;


public class Draw_Gui_TextandMesh extends SimpleApplication {

    public static void main(String[] args) {
        Draw_Gui_TextandMesh app = new Draw_Gui_TextandMesh();
        app.start();
    }

    Geometry geom, geom2;
    BitmapText ch;
    
    
    
    
    
    
    @Override
    public void simpleInitApp() {

        Box a = new Box(Vector3f.ZERO, 1, 1, 1);
        geom = new Geometry("Box", a);
        geom.setLocalTranslation(0, -1, 0);
        geom.updateModelBound();
                
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);

        rootNode.attachChild(geom);

        
//    guiNode.detachAllChildren();    
    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    ch = new BitmapText(guiFont, false);
    ch.setSize(guiFont.getCharSet().getRenderedSize());
    ch.setText(geom.getName()); // crosshairs
    ch.setColor(new ColorRGBA(1f,0.8f,0.3f,0.8f));
    guiNode.attachChild(ch);
    geom2 = geom.clone();
    geom2.scale(10f);
    guiNode.attachChild(geom2);
    
    
    
    
        
        viewPort.setBackgroundColor(ColorRGBA.Gray);
        flyCam.setMoveSpeed(30);
    }

    
    
@Override
public void simpleUpdate(float tpf)
{
          
      geom2.setLocalTranslation(cam.getScreenCoordinates(geom.getLocalTranslation().add(0, 2.1f, 0)));
      ch.setLocalTranslation(cam.getScreenCoordinates(geom.getLocalTranslation().add(0, 2, 0)));
       
      
 }
 
}
