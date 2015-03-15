package com.basics;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;


public class LookAtNode extends SimpleApplication {

    public static void main(String[] args) {
        LookAtNode app = new LookAtNode();
        app.start();
    }

    Geometry geom;
    Quaternion rot=new Quaternion();
 
    
        public void move (float tpf) {
                
        rot = geom.getLocalRotation();
        rot.lookAt(cam.getDirection().multLocal(1f, 0f, 1f), Vector3f.UNIT_Y);
        geom.setLocalRotation(geom.getLocalRotation());
          
}
    
    
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
     
        
        Box b = new Box(Vector3f.ZERO, 2.0f, 0.01f, 2.0f);
        Geometry plane = new Geometry("Box", b);
        plane.setLocalTranslation(0, -2, 0);
        plane.updateModelBound();
        
        Material matpl = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matpl.setColor("Color", ColorRGBA.Red);
        plane.setMaterial(matpl);

        rootNode.attachChild(plane);
               
        
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize());
        ch.setText("Box is Looking At You"); // crosshairs
        ch.setColor(new ColorRGBA(1f,0.8f,0.1f,1f));
        ch.setLocalTranslation(settings.getWidth()*0.3f,settings.getHeight()*0.1f,0);
        guiNode.attachChild(ch);        
        
        
        flyCam.setMoveSpeed(30);
        viewPort.setBackgroundColor(ColorRGBA.Gray);        
        
    }

    
    
@Override
public void simpleUpdate(float tpf)
{
          
          move(tpf);
       
      
 }
 
}
