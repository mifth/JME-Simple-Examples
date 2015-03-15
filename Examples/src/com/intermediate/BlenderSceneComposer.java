package com.intermediate;



/*You can get transforms from *.blend files and use your models for it. 
 * Blender could be used as a World Editor or scene composer.
 * Names of JME objects and blend objects should be like:
 * JME names - Box, Sphere
 * blend names - Box, Box.000, Box.001, Box.002.... Sphere, Sphere.000, Sphere.001...
*/ 


import com.jme3.app.SimpleApplication;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.*;
import com.jme3.scene.Node;
import com.jme3.scene.shape.*;


public class BlenderSceneComposer extends SimpleApplication {

    Geometry geom_a;
    Geometry geom_b;
    Node ndmd;
    Material mat_sphr;
    Material mat_box;

      
    public static void main(String[] args) {
        BlenderSceneComposer app = new BlenderSceneComposer();
        app.start();
    }

     public void Models () {
        
         //Create an empty node for models 
         ndmd = new Node("Models");
         
        // Create a box Geometry
        Box box_a = new Box(Vector3f.ZERO, 1, 1, 1);
        geom_a = new Geometry("Box", box_a);
        geom_a.updateModelBound();
        
        mat_box = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_box.setColor("Color", ColorRGBA.Blue);
        geom_a.setMaterial(mat_box);
        ndmd.attachChild(geom_a);
        
        // Create a sphere Geometry
        Sphere sphr_a = new Sphere(10, 10, 1);
        geom_b = new Geometry("Sphere", sphr_a);
        geom_b.updateModelBound();
        
        mat_sphr = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_sphr.setColor("Color", ColorRGBA.Red);
        geom_b.setMaterial(mat_sphr);
        ndmd.attachChild(geom_b);
      
        }
     
     
    
    @Override
    public void simpleInitApp() {
        
        Models();
        
        
        // Load a blender file. 
        DesktopAssetManager dsk = (DesktopAssetManager) assetManager;        
        ModelKey bk = new ModelKey("Models/blender_test_scene/blender_test_scene.blend");
        Node nd =  (Node) dsk.loadModel(bk); 
        
        //Create empty Scene Node
        Node ndscene = new Node("Scene");
        
        
        // Attach boxes with names and transformations of the blend file to a Scene
         for (int j=0; j<ndmd.getChildren().size();j++){
            String strmd = ndmd.getChild(j).getName();
                
            for (int i=0; i<nd.getChildren().size(); i++) {
                      
               String strndscene = nd.getChild(i).getName();
             if (strmd.length() < strndscene.length())  strndscene = strndscene.substring(0, strmd.length());
               
         
            if (strndscene.equals(strmd) == true){
                Geometry ndGet = (Geometry) ndmd.getChild(j).clone(false);
                ndGet.setName(nd.getChild(i).getName());
                ndGet.setLocalTransform(nd.getChild(i).getWorldTransform());
                ndscene.attachChild(ndGet);   
                 
         }    
         }
         }
           
        rootNode.attachChild(ndscene);

        // Clear Cache
        nd.detachAllChildren();
        nd.removeFromParent();
        dsk.clearCache();
  
        
        mat_sphr.setColor("Color", ColorRGBA.Yellow); //check if material is shared
        
        
        
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize());
        ch.setText("Transformations are loaded from blender_test_scene.blend"); // crosshairs
        ch.setColor(new ColorRGBA(1f,0.8f,0.1f,1f));
        ch.setLocalTranslation(settings.getWidth()*0.3f,settings.getHeight()*0.1f,0);
        guiNode.attachChild(ch);
        
        
        
        flyCam.setMoveSpeed(30);
        viewPort.setBackgroundColor(ColorRGBA.Gray);

}


    }




