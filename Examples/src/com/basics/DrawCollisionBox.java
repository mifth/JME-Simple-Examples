package com.basics;


import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;



public class DrawCollisionBox extends SimpleApplication {

    public static void main(String[] args) {
        DrawCollisionBox app = new DrawCollisionBox();
        app.start();
    }

    Geometry geom;     
    Geometry geom2;     
    Geometry bx;
    Geometry bx2;
    WireBox wbx;

    
    @Override
    public void simpleInitApp() {

        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        
        Material mat_box = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_box.setColor("Color", ColorRGBA.Blue);
        
        Material mat_wire = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_wire.setColor("Color", ColorRGBA.Cyan);
        mat_wire.getAdditionalRenderState().setWireframe(true);
        
        Mesh sphr = new Sphere(10, 10, 1f);
        Mesh cyl = new Cylinder(10,10,1,1,true);
        Mesh bxWire = new Box(1,1,1);
        geom = new Geometry("Sphere", sphr);
        geom.scale(2,1,1);  //check if scale works with bx correctly
      
        wbx = new WireBox();
        wbx.fromBoundingBox((BoundingBox) geom.getWorldBound());
        
        bx = new Geometry("TheMesh", wbx);
        bx.setMaterial(mat_box);
        rootNode.attachChild(bx);
        
        geom.setMaterial(mat);
        rootNode.attachChild(geom);

        geom2 = new Geometry("Boxxx", cyl);
        geom2.setMaterial(mat);
        geom2.setLocalTranslation(5, 0, 0);
        geom2.setLocalScale(2, 1, 1);
        rootNode.attachChild(geom2);
        
        bx2 = new Geometry("TheMesh", sphr);
        bx2.setMaterial(mat_wire);
        rootNode.attachChild(bx2);
        
        
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.63f, -0.463f, -0.623f).normalizeLocal());
        dl.setColor(new ColorRGBA(1,1,1,1));
        rootNode.addLight(dl);        
      
        viewPort.setBackgroundColor(ColorRGBA.Gray);
        flyCam.setMoveSpeed(30);
        
    }
     
      
@Override
public void simpleUpdate(float tpf)
{    
    //float angle =0;
   float  angle = tpf*2f;
  //  angle %= FastMath.TWO_PI;
     
    
    geom.move(0, angle*0.2f, 0);
    geom.rotate(0, angle, 0);
    
    geom2.move(0, angle*0.2f, 0);
    geom2.rotate(0, angle, 0);
 
    
    bx.setLocalTransform(geom.getWorldTransform().setScale(1,1,1));
    bx2.setLocalTransform(geom2.getWorldTransform());

 }

}
