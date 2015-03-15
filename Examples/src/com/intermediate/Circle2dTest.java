package com.intermediate;

/*You can get transforms from *.blend files and use your models for it.
 * Blender could be used as a World Editor or scene composer.
 * Names of JME objects and blend objects should be like:
 * JME names – Box, Sphere
 * blend names – Box, Box.000, Box.001, Box.002…. Sphere, Sphere.000, Sphere.001…
 */
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.math.*;
import com.jme3.system.AppSettings;
import java.awt.Color;

import java.util.Random;


public class Circle2dTest extends SimpleApplication {

    public static void main(String[] args) {

        AppSettings settings = new AppSettings(true);
        settings.setTitle("Circle");
        settings.setResolution(800, 600);
        Circle2dTest app = new Circle2dTest();
        app.setSettings(settings);
        app.setPauseOnLostFocus(false);
        app.setShowSettings(false);

        app.start();
    }

    @Override
    public void simpleInitApp() {
        Random randomGenerator = new Random();

        for (float i = 0; i < settings.getWidth(); i+=settings.getWidth()/10) {
            for (float j = 0; j < settings.getHeight(); j+=settings.getHeight()/10) {
                int borderAngle = randomGenerator.nextInt(360);
                int innerAngle = randomGenerator.nextInt(360);
                Color randomBorderColor = new Color(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255));
                Color randomInnerColor = new Color(randomGenerator.nextInt(255), randomGenerator.nextInt(255), randomGenerator.nextInt(255));
                Circle2d circle = new Circle2d(assetManager, 1, randomGenerator.nextInt(20), randomBorderColor, borderAngle, randomInnerColor, innerAngle);
                circle.setLocalTranslation(0, 0, 0);
                guiNode.attachChild(circle);
                circle.rotate(90, 0,0);
                circle.setLocalTranslation(new Vector3f(i , j, 0));
                circle.scale(70);
            }
        }
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize());
        ch.setText("Circle2d Mesh Example"); // crosshairs
        ch.setColor(new ColorRGBA(1f, 0.8f, 0.1f, 1f));
        ch.setLocalTranslation(settings.getWidth() * 0.3f, settings.getHeight() * 0.1f, 0);
        guiNode.attachChild(ch);

        flyCam.setEnabled(false);
        viewPort.setBackgroundColor(ColorRGBA.Gray);

    }

    @Override
    public void simpleUpdate(float tpf) {
//        circle.generateImage();
    }
}