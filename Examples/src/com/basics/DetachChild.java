package com.basics;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.*;
import com.jme3.scene.shape.*;

public class DetachChild extends SimpleApplication {

    Geometry geom_a;
    Geometry geom_b;
    Node red = new Node();

    Vector3f maLocation = new Vector3f(5, 0, 0);  
    float amount = 0;
    float scale = 1;
    
    public static void main(String[] args) {
        DetachChild app = new DetachChild();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        Box box_a = new Box(Vector3f.ZERO, 1, 1, 1);
        geom_a = new Geometry("Box_A", box_a);
        geom_a.updateModelBound();
        Material mat_a = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_a.setColor("Color", ColorRGBA.Blue);
        geom_a.setMaterial(mat_a);
        rootNode.attachChild(geom_a);
        
        Box box_b = new Box(Vector3f.ZERO, 1, 1, 1);
        geom_b = new Geometry("Box_B", box_b);
        geom_b.updateModelBound();
        Material mat_b = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_b.setColor("Color", ColorRGBA.Red);
        geom_b.setMaterial(mat_b);        
        
        Node nd[] = new Node [10];
        for (int i=0; i<nd.length; i++) {
        

            
        Node ndd = new Node("nddd"+i);  
        ndd.attachChild(geom_b.clone(false));
        ndd.setLocalTranslation((i+3)*5, 0, 0);
        System.out.println(ndd.getName());
        red.attachChild(ndd);    
        }
        
        rootNode.attachChild(red);
        
        
        cam.setLocation(new Vector3f(36.0f, 0.9578f, 60.314f));
        flyCam.setMoveSpeed(30);
        viewPort.setBackgroundColor(ColorRGBA.Gray);

}


    @Override
    public void simpleUpdate(float tpf) {
 
       amount += tpf*scale;
       geom_a.setLocalTranslation(maLocation.mult(amount));   
        
          rootNode.updateGeometricState();

        CollisionResults results = new CollisionResults();

        BoundingVolume bv = geom_a.getWorldBound();
        red.collideWith(bv, results);
   
        if (results.size() > 0) {
        Spatial closest = results.getCollision(0).getGeometry();
        if(closest != null) closest.removeFromParent();
             
       

      
        }
    }


}

