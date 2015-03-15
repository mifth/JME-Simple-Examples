package com.basics;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class MoveNode2 extends SimpleApplication {

    public static void main(String[] args) {
        MoveNode2 app = new MoveNode2();
        app.start();
    }
    Vector3f vecmove = new Vector3f(25, 5, 0);
    Geometry geom;
    Vector3f vectry;
    float move;
    float vecdist2;
    float remainingDist;

    public void move(float tpf) {

        move = tpf * 10f; //speed

        remainingDist = geom.getLocalTranslation().distance(vecmove); //distance between 2 vectors
        if (remainingDist != 0) geom.setLocalTranslation(FastMath.interpolateLinear(move / remainingDist, geom.getLocalTranslation(), vecmove));

        System.out.println(remainingDist);
    }

    @Override
    public void simpleInitApp() {
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        geom = new Geometry("Box", b);
        geom.updateModelBound();

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
        geom.setLocalTranslation(0, 2, 1);
        rootNode.attachChild(geom);


        flyCam.setMoveSpeed(30);
        viewPort.setBackgroundColor(ColorRGBA.Gray);

    }

    @Override
    public void simpleUpdate(float tpf) {

        move(tpf);


    }
}
