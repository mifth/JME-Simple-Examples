package com.basics;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;


public class RotateNode2 extends SimpleApplication {

    public static void main(String[] args) {
        RotateNode2 app = new RotateNode2();
        app.start();
    }

    
      Vector3f vecmove = new Vector3f(10, 10, 0);
      float angla;
      Geometry geom;    
      Vector3f vectry3;


       public void rotateObj (float tpf) {      
       
       angla += tpf * 0.1f; //speed
       angla %= 1f;
           
      Quaternion vectry = geom.getLocalRotation();
      Quaternion qRot = new Quaternion();
      qRot.lookAt(vecmove.clone().setY(geom.getWorldTranslation().y), Vector3f.UNIT_Y);
      
      vectry.slerp(qRot, angla);
      geom.setLocalRotation(vectry);
    //  geom.rotate(0,0.1f,0);
      
    System.out.println(vecmove);      
    viewPort.setBackgroundColor(ColorRGBA.Gray);   
    
         }
      
          
    @Override
    public void simpleInitApp() {
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        geom = new Geometry("Box", b);
        geom.scale(1, 1, 2);
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
