package com.intermediate;

import com.jme3.export.Savable;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
 
public class ShootBulletControlCreate extends AbstractControl implements Savable, Cloneable
{

    ShootBulletRun shoBuRu;
    float timer;
    
    Node shotsGroup;
    Box box_bullet;
    Geometry geoo;
    Spatial spaa;

    int count;
    Geometry gm;

    
    
    
    ShootBulletControlCreate(Spatial zspaa, ShootBulletRun zshb)
    {
        
        this.shoBuRu = zshb;
        this.spaa = zspaa;
        
        box_bullet = new Box(Vector3f.ZERO, 1, 1, 1);
        count = 0;
        
        shotsGroup = new Node("bulletsNode_"+zspaa.getName());
        shoBuRu.getRootNode().attachChild(shotsGroup);

        geoo = new Geometry ("bullet_"+zspaa.getName(), box_bullet); 
        Material mat_bullet = new Material(shoBuRu.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat_bullet.setColor("Color", ColorRGBA.Red);
        geoo.setMaterial(mat_bullet);
        

        
    }
 

 
    @Override
    protected void controlUpdate(float arg0) {
    if (geoo != null || spaa != null) {
        
    timer += arg0*4f;
    
    if (timer>0.2f){
        
        gm = geoo.clone(false);
        gm.setName(geoo.getName()+count);
        gm.updateModelBound();
        shotsGroup.attachChild(gm);
        gm.addControl(new ShootBulletControlMove(spaa, gm, this));
        
        count+=1;
        if (count > 10000000) count = 0;
        timer = 0;
        
    }
                
    }
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}