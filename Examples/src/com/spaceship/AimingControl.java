/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spaceship;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author mifth
 */
public class AimingControl extends AbstractControl {

    private SimpleApplication app;
    private AssetManager asm;
    private List<Spatial> list;
    private float timer;
    private Camera cam;
    private Node player;
    private Spatial aim;
    private BitmapText ch;
    private Vector2f centerCam;
    private float distanceToRemove;

    public AimingControl(SimpleApplication app, Node player) {

        this.app = app;
        asm = this.app.getAssetManager();
        cam = app.getCamera();
        this.player = player;

        list = new LinkedList<Spatial>();
        timer = 0f;
        centerCam = new Vector2f(cam.getWidth() / 2, cam.getHeight() / 2);
        distanceToRemove = 300f;

        BitmapFont guiFont = asm.loadFont("Interface/Fonts/Default.fnt");
        ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize());
        ch.setColor(new ColorRGBA(1f, 0.35f, 0.1f, 0.8f));

    }

    public void addToSelection(Spatial sp) {
        list.add(sp);
    }

    public void removeFromSelection(Spatial sp) {
        list.remove(sp);
    }

    public void setAim() {

        Spatial aimPossible = null;

//        Vector3f aimScreenPos = cam.getScreenCoordinates(aim.getWorldTranslation());
//        float aimWorldDistance = player.getWorldTranslation().distance(aim.getWorldTranslation());        
//        float aimScreenDistance = centerCam.distance(new Vector2f(aimScreenPos.getX(), aimScreenPos.getY()));                     

        for (Spatial sp : list) {

            Vector3f spScreenPos = cam.getScreenCoordinates(sp.getWorldTranslation());
            float spWorldDistance = player.getWorldTranslation().distance(sp.getWorldTranslation());
            float spScreenDistance = centerCam.distance(new Vector2f(spScreenPos.getX(), spScreenPos.getY()));

            // Possible AIM Searching
            if (aim == null
                    && spScreenPos.getZ() < 1f
                    && spScreenDistance <= (cam.getHeight() * 0.5) * 0.6f
                    && spScreenPos.getZ() < distanceToRemove) {

                if (aimPossible == null) {
                    aimPossible = sp;
                } else if (aimPossible != null) {
                    float aimPosibleWorldDistance = player.getWorldTranslation().distance(aimPossible.getWorldTranslation());
                    if (spWorldDistance < aimPosibleWorldDistance) {
                        aimPossible = sp;
                    }
                }

            } else if (aim != null
                    && spScreenPos.getZ() < 1f
                    && spScreenDistance <= (cam.getHeight() * 0.5) * 0.6f
                    && !sp.equals(aim)
                    && spScreenPos.getZ() < distanceToRemove) {
                float aimPosibleWorldDistance = player.getWorldTranslation().distance(aim.getWorldTranslation());
                if (spWorldDistance < aimPosibleWorldDistance) {
                    aimPossible = sp;
                }
            }
        }

        // set Aim
        if (aimPossible != null) {
            aim = aimPossible;
        }
    }

    public Spatial getAim() {
        return aim;
    }

    @Override
    protected void controlUpdate(float tpf) {
//        timer += tpf * 3f;
//
//        if (timer > 5f) {
//            timer = 0;
//        }

        if (aim != null) {
            Vector3f screenPos = cam.getScreenCoordinates(aim.getWorldTranslation());
            float worldDistance = player.getWorldTranslation().distance(aim.getWorldTranslation());
            float screenDistance = centerCam.distance(new Vector2f(screenPos.getX(), screenPos.getY()));

//            System.out.println(screenPos.getZ());
            if (screenDistance <= (cam.getHeight() * 0.5) * 0.6f
                    && screenPos.getZ() < 1f) {
                ch.setText("Selected: " + aim.getName());
                ch.setLocalTranslation(cam.getScreenCoordinates(aim.getLocalTranslation()));
                app.getGuiNode().attachChild(ch);

            } else {
                app.getGuiNode().detachChild(ch);
            }

            if (worldDistance > distanceToRemove) {
                aim = null;
            }
        } else if (aim == null) {
            app.getGuiNode().detachChild(ch);
        }


    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }
}
