package com.intermediate;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

public class SelectObject extends SimpleApplication implements ActionListener {

    public static void main(String[] args) {
        SelectObject app = new SelectObject();
        app.start();
    }
//    Geometry geom;
    Node nd_selection;
    Node scene;
    Node sub_scene;

    @Override
    public void simpleInitApp() {

        initCrossHairs();

        scene = new Node("scene");
        sub_scene = new Node("sub_scene");
        nd_selection = new Node("nd_selection");

        // create boxes
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        for (int i = 0; i < 10; i++) {
            
            Geometry geom = new Geometry("Geom" + i, b);
            Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            geom.setMaterial(mat);
            geom.setLocalTranslation(0 + i*3, 2, 1);
            scene.attachChild(geom);
        }


        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.8f, -0.6f, -0.08f).normalizeLocal());
        dl.setColor(new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
        rootNode.addLight(dl);

        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(0.7f, 0.9f, 1.5f, 1.0f));
        rootNode.addLight(al);





        AmbientLight al_selection = new AmbientLight();
        al_selection.setColor(new ColorRGBA(1.75f, 1.2f, -0.95f, 1.0f));
        nd_selection.addLight(al_selection);

        rootNode.attachChild(scene);
        scene.attachChild(sub_scene);
        sub_scene.attachChild(nd_selection);

        inputManager.addMapping("FIRE", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "FIRE");


        flyCam.setMoveSpeed(30);
        viewPort.setBackgroundColor(ColorRGBA.Gray);

    }

    @Override
    public void onAction(String name, boolean isPressed, float arg) {
        if (name.equals("FIRE") && isPressed) {
            CollisionResults crs = new CollisionResults();
            scene.collideWith(new Ray(cam.getLocation(), cam.getDirection()), crs);


            if (crs.getClosestCollision() != null) {
                System.out.println("Hit at " + crs.getClosestCollision().getContactPoint());
                
                Geometry geom = crs.getClosestCollision().getGeometry();

                if (!geom.hasAncestor(nd_selection)) {
                    System.out.println("Geom is attached to nd_ambien node");
                    nd_selection.attachChild(geom);
                }
            } else {
                
                for (Object obj : nd_selection.getChildren()) {
                    if (obj instanceof Node || obj instanceof Geometry) {
                        scene.attachChild((Spatial)obj);
                    }
                }

            }

        }
    }

    protected void initCrossHairs() {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
                settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
                settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);

        BitmapText ch2 = new BitmapText(guiFont, false);
        ch2.setSize(guiFont.getCharSet().getRenderedSize());
        ch2.setText("Click to select");
        ch2.setColor(new ColorRGBA(1f, 0.8f, 0.1f, 1f));
        ch2.setLocalTranslation(settings.getWidth() * 0.3f, settings.getHeight() * 0.1f, 0);
        guiNode.attachChild(ch2);

    }

    @Override
    public void simpleUpdate(float tpf) {
    }
}
