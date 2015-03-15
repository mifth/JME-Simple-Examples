package com.basics;

import com.jme3.app.SimpleApplication;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import java.io.File;
import java.io.IOException;


public class WriteToJ3o extends SimpleApplication {

    public static void main(String[] args) {
        WriteToJ3o app = new WriteToJ3o();
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
        rootNode.attachChild(geom);
        
        }

        
 
    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    ch = new BitmapText(guiFont, false);
    ch.setSize(guiFont.getCharSet().getRenderedSize());
    ch.setText("The Scene is Written to writej3o.j3o file, see it in your project folder!!!"); // crosshairs
    ch.setColor(new ColorRGBA(1f,0.8f,0.1f,1f));
    ch.setLocalTranslation(settings.getWidth()*0.2f,settings.getHeight()*0.1f,0);
    
    
    
    String str = new String("assets/Models/writej3o.j3o");
    
        // convert to / for windows
        if (File.separatorChar == '\\'){
            str = str.replace('\\', '/');
        }
        if(!str.endsWith("/")){
            str += "/";
        }
        
    File MaFile = new File(str);
    MaFile.setWritable(true);
    MaFile.canWrite();
    MaFile.canRead();
    
    
        try {
            BinaryExporter.getInstance().save(rootNode, MaFile);
            guiNode.attachChild(ch);
        } catch (IOException ex) {
            System.out.println("Baddddd");
           
        }
        
        viewPort.setBackgroundColor(ColorRGBA.Gray);
        flyCam.setMoveSpeed(30);
    }

     
 
}
