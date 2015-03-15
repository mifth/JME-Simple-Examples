package com.basics;


import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;


public class GenerateFieldOfInstances extends SimpleApplication {

    public static void main(String[] args) {
        GenerateFieldOfInstances app = new GenerateFieldOfInstances();
        app.start();
    }

    Geometry geom;     
    Node instNodes = new Node();  
              
    @Override
    public void simpleInitApp() {
        
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        geom = new Geometry("Box", b);
        geom.updateModelBound();

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        geom.setMaterial(mat);
        geom.setLocalTranslation(0,2,1);
        
        int numClones = 500;
        
        for (int i=0; i<numClones; i++) {
            
            Geometry gm = geom.clone(false);
            gm.setName("instance"+i);
            gm.setLocalTranslation((float) Math.random() * 100.0f - 50f,(float) Math.random() * 10.0f,(float)Math.random() * 100.0f - 50f);
            gm.rotate(0, (float) Math.random() * (float)Math.PI, 0);
            instNodes.attachChild(gm);
            
        }
        
        rootNode.attachChild(instNodes);
        
        mat.setColor("Color", ColorRGBA.Blue);  //Check if material is shared by all objects
        
        
        flyCam.setMoveSpeed(30);
        viewPort.setBackgroundColor(ColorRGBA.Gray);
        
    }

    
     
      
@Override
public void simpleUpdate(float tpf)
{
          
 }

}
