package com.basics;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.math.*;


public class template extends SimpleApplication {

    public static void main(String[] args) {
        template app = new template();
        app.start();
    }

    
    
    @Override
    public void simpleInitApp() {

 starting();
                
       
    }

    
    
    @Override
public void simpleUpdate(float tpf)
{
          
    
 }
 
    public void starting () {
         guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize());
        ch.setText("Write Your Annotation"); // crosshairs
        ch.setColor(new ColorRGBA(1f,0.8f,0.1f,1f));
        ch.setLocalTranslation(settings.getWidth()*0.3f,settings.getHeight()*0.1f,0);
        guiNode.attachChild(ch);
        
        viewPort.setBackgroundColor(ColorRGBA.Gray);
        flyCam.setMoveSpeed(30);
    }
 
}
