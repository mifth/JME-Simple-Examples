package com.intermediate;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 *
 * @author bobo
 */
public class Circle2d extends Node {
    private float radius = 5;
    private int angle = 360;
    private int borderAngle = 360;
    private Color color = Color.BLACK;
    private Color fillColor = null;
    private AssetManager assetManager;
    private final Texture texture = new Texture2D();
    private float borderWidth;
    private int heightResolution = 64;
    private int widthResolution = 64;
    private Material material;
    private Geometry geometry;

    /**
     *
     * @param assetManager
     * @param radius radius of the circle
     * @param borderWidth width of the border
     * @param color  fill color
     * @param borderAngle and of border displayed
     * @param fillColor filled color
     * @param angle and gle of the filled color
     */
    public Circle2d(AssetManager assetManager, float radius, float borderWidth, Color color, int borderAngle, Color fillColor, int angle) {
        this.assetManager = assetManager;
        this.radius = radius;
        this.color = color;
        this.fillColor = fillColor;
        this.borderWidth = borderWidth;
        this.borderAngle = borderAngle;
        this.angle = angle;
        material = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        initSpatial();
        updateSpatial();
    }

    private void initSpatial() {
        generateImage();
        Quad q = new Quad(10, 10);
        geometry = new Geometry("circle", q);
        geometry.rotate(-3.1416f / 2, 0, 0);
        geometry.setMaterial(material);
        this.attachChild(geometry);
    }

    private void updateSpatial() {
        geometry.setLocalScale(radius / 10f, radius / 10f, radius / 10f);
    }

    public void generateImage() {
        BufferedImage image = new BufferedImage(widthResolution, heightResolution, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (fillColor != null) {
            g.setColor(fillColor);
            g.fillArc((int) borderWidth / 2, (int) borderWidth / 2, (int) (widthResolution - 1 - borderWidth), (int) (heightResolution - borderWidth), 180+(angle/2), -angle);
        }
        g.setStroke(new BasicStroke(borderWidth));
        g.setColor(color);
        g.drawArc((int) borderWidth / 2, (int) borderWidth / 2, (int) (widthResolution - 1 - borderWidth), (int) (heightResolution - borderWidth), 180 +(borderAngle/2), -borderAngle);

        AWTLoader awtLoader = new AWTLoader();
        texture.setImage(awtLoader.load(image, false));
        material.setTexture("ColorMap", texture);
    }

    public void setRadius(float radius) {
        this.radius = radius;
        updateSpatial();
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public void setResolution(int resolution) {
        this.heightResolution = resolution;
        this.widthResolution = resolution;
    }

}