package com.basics;


import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;



public class OpenGL_3 extends SimpleApplication {

    public static void main(String[] args) {
        OpenGL_3 app = new OpenGL_3();
        AppSettings aps = new AppSettings(true);
        aps.setRenderer(AppSettings.LWJGL_OPENGL3);
        app.setSettings(aps);
        app.start();
    }

    Geometry geom;     
   
              
    @Override
    public void simpleInitApp() {
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        geom = new Geometry("Box", b);
        geom.updateModelBound();

        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        geom.setMaterial(mat);
        geom.setLocalTranslation(0,2,1);
        rootNode.attachChild(geom);
        
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.8f, -0.6f, -0.08f).normalizeLocal());
        dl.setColor(new ColorRGBA(1,1,1,1));
        rootNode.addLight(dl);        
      
        
        flyCam.setMoveSpeed(30);
        viewPort.setBackgroundColor(ColorRGBA.Gray);   
        
    }

    
     
      
@Override
public void simpleUpdate(float tpf)
{
          
 }

}
