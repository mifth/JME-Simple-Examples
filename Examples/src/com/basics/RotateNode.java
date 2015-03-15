package com.basics;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;


public class RotateNode extends SimpleApplication {

    public static void main(String[] args) {
        RotateNode app = new RotateNode();
        app.start();
    }

    
    
      Vector3f vecmove = new Vector3f(25, 5, 0);
      float angla;
      Geometry geom;    
      Quaternion vectry;   
      Quaternion vectry2; 
      Vector3f vectry3;
      float vecdist2;
      Quaternion PITCH045;
      Quaternion quat;
float xxx = FastMath.DEG_TO_RAD*180f;

Quaternion qqq;



       public void rotateObj (float tpf) {      
       
 
      vectry = geom.getWorldRotation();
      qqq = new Quaternion().fromAngles(0, xxx, 0);
   
      if (angla < xxx) {
          
           angla += tpf * 1.5f; //speed
      
       geom.setLocalRotation(new Quaternion().fromAngles(0, angla, 0)); 
          
      }
      else geom.setLocalRotation(qqq);
       
   
    System.out.println(angla);      
    viewPort.setBackgroundColor(ColorRGBA.Gray);   
    
         }
      
       
          
    @Override
    public void simpleInitApp() {
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        geom = new Geometry("Box", b);
        geom.updateModelBound();

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);

        rootNode.attachChild(geom);
        
       
        
        flyCam.setMoveSpeed(30);
        

        
    }

    
    
      
@Override
public void simpleUpdate(float tpf)
{
          
          rotateObj(tpf);
       
      
 }

}
