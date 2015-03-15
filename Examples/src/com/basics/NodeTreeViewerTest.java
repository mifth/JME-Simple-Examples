package com.basics;

import com.jme3.app.SimpleApplication;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import java.io.File;
import java.io.IOException;


public class NodeTreeViewerTest extends SimpleApplication {

    public static void main(String[] args) {
        NodeTreeViewerTest app = new NodeTreeViewerTest();
        app.start();
    }

    
    BitmapText ch;
    
    
    
    
    
    
    @Override
    public void simpleInitApp() {

        Box a = new Box(Vector3f.ZERO, 1, 1, 1);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        
        for (int i=0; i<10; i++){
        
        Geometry geom = new Geometry("Box"+i, a);
        geom.setLocalTranslation(0, i*3, 0);
        geom.updateModelBound();
        geom.setMaterial(mat);
        Node ndParent = new Node("Parent" + i);
        ndParent.attachChild(geom);
        rootNode.attachChild(ndParent);
        
        }

        
 
    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    ch = new BitmapText(guiFont, false);
    ch.setSize(guiFont.getCharSet().getRenderedSize());
    ch.setText("Tree of the scene. Thanks to @perfecticus. Swing thing. :)"); // crosshairs
    ch.setColor(new ColorRGBA(1f,0.8f,0.1f,1f));
    ch.setLocalTranslation(settings.getWidth()*0.2f,settings.getHeight()*0.1f,0);
    guiNode.attachChild(ch);
    
    
    
  NodeTreeViewer tree = new NodeTreeViewer(rootNode);
        
        viewPort.setBackgroundColor(ColorRGBA.Gray);
        flyCam.setMoveSpeed(30);
    }

     
 
}
