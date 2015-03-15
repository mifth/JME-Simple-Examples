package com.basics;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;


public class MoveNode extends SimpleApplication {

    public static void main(String[] args) {
        MoveNode app = new MoveNode();
        app.start();
    }

    
    
          
      Vector3f vecmove = new Vector3f(25, 5, 0);
      Geometry geom;     
      Vector3f vectry;   
      Vector3f vectry2;
      float   move;
      float remainingDist;
      float vecdist2;
      
      

      public void move (float tpf) {      
     
      move = tpf*7.0f;
      vectry = geom.getLocalTranslation();
  
      remainingDist = vectry.distance(vecmove);
       
       System.out.println(remainingDist);
              
       if (move < remainingDist && remainingDist != 0) {
           
           vectry2 = vectry.interpolate(vecmove, move / remainingDist);
           geom.setLocalTranslation(vectry2);
       }
       else geom.setLocalTranslation(vecmove);
        
         }
      
      
    
    @Override
    public void simpleInitApp() {
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        geom = new Geometry("Box", b);
        geom.updateModelBound();

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
        geom.setLocalTranslation(0,2,1);
        rootNode.attachChild(geom);
        
        


        
        
        flyCam.setMoveSpeed(30);
        viewPort.setBackgroundColor(ColorRGBA.Gray);   
        

        
    }

    
     
      
@Override
public void simpleUpdate(float tpf)
{
          
          move(tpf);
       
      
 }

}
