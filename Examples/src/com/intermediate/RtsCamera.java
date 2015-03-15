package com.intermediate;


import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;


public class RtsCamera extends SimpleApplication {

  public static void main(String[] args) {
    RtsCamera app = new RtsCamera();
    app.start();
  }
  
  Node cubechar;
  Node shootables;
  Geometry mark;
  
  Boolean shoot = false;
  Vector3f vectry;   
  Vector3f vectry2; 
  Vector3f vecmove;
  float vecdist2;
  float   move;
  float remainingDist;

  @Override
  public void simpleInitApp() {
    initCrossHairs(); // a "+" in the middle of the screen to help aiming
    initKeys();       // load custom key mappings
    initMark();       // a red sphere to mark the hit

    /** create four colored boxes and a floor to shoot at: */
    shootables = new Node("Shootables");
    rootNode.attachChild(shootables);
    shootables.attachChild(makeFloor());
   
    
    cubechar = new Node();
    cubechar.attachChild(makeCube("Character", 0, 0.5f, 0));
    rootNode.attachChild(cubechar);
    cubechar.setLocalTranslation(0,-3.8f,0);
    
    final RtsCameraControl rtsCam = new RtsCameraControl(cam, rootNode);
    rtsCam.registerWithInput(inputManager);
    rtsCam.setCenter(new Vector3f(20,0.5f,20));
        
    viewPort.setBackgroundColor(ColorRGBA.Gray);   
    
  }

  /** Declaring the "Shoot" action and mapping to its triggers. */
  public void initKeys() {
    inputManager.addMapping("Shoot", new KeyTrigger(KeyInput.KEY_SPACE), // trigger 1: spacebar
      new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2: left-button click
    inputManager.addListener(actionListener, "Shoot");
  }
  /** Defining the "Shoot" action: Determine what was hit and how to respond. */
  public ActionListener actionListener = new ActionListener() {

    public void onAction(String name, boolean keyPressed, float tpf) {
      if (name.equals("Shoot") && !keyPressed) {
        // 1. Reset results list.
        CollisionResults results = new CollisionResults();
        // 2. Aim the ray from cam loc to cam direction.
        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
        // 3. Collect intersections between Ray and Shootables in results list.
        shootables.collideWith(ray, results);
        // 4. Print the results
        System.out.println("----- Collisions? " + results.size() + "-----");
        for (int i = 0; i < results.size(); i++) {
          // For each hit, we know distance, impact point, name of geometry.
          float dist = results.getCollision(i).getDistance();
          Vector3f pt = results.getCollision(i).getContactPoint();
          String hit = results.getCollision(i).getGeometry().getName();
          System.out.println("* Collision #" + i);
          System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
        }
        
        if (results.size() > 0) {
          // The closest collision point is what was truly hit:
          CollisionResult closest = results.getClosestCollision();
          // Let's interact - we mark the hit with a red dot.
          mark.setLocalTranslation(closest.getContactPoint().setY(mark.getLocalTranslation().y));
         // rootNode.attachChild(mark);
          
       shoot = true;
       
        } 
          
      }
       
    }
  };

  /** A cube object for target practice */
  protected Geometry makeCube(String name, float x, float y, float z) {
    Box box = new Box(new Vector3f(x, y, z), 1, 1, 1);
    Geometry cube = new Geometry(name, box);
    cube.setLocalScale(0.5f,0.5f,1.5f);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.randomColor());
    cube.setMaterial(mat1);
    return cube;
  }

  /** A floor to show that the "shot" can go through several objects. */
  protected Geometry makeFloor() {
    Box box = new Box(new Vector3f(0, -4, -5), 15, .2f, 15);
    Geometry floor = new Geometry("the Floor", box);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.Brown);
    floor.setMaterial(mat1);
    return floor;
  }

  /** A red ball that marks the last spot that was "hit" by the "shot". */
  protected void initMark() {
    Sphere sphere = new Sphere(30, 30, 0.2f);
    mark = new Geometry("BOOM!", sphere);
    Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mark_mat.setColor("Color", ColorRGBA.Red);
    mark.setMaterial(mark_mat);
    mark.setLocalTranslation(0, -3.8f, 0);
    rootNode.attachChild(mark);
  }

  /** A centred plus sign to help the player aim. */
  protected void initCrossHairs() {
    
    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    BitmapText ch = new BitmapText(guiFont, false);
    ch.setSize(guiFont.getCharSet().getRenderedSize()*1.5f);
    ch.setText("+"); // crosshairs
    ch.setLocalTranslation( // center
      settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
      settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
    guiNode.attachChild(ch);
    
        BitmapText ch2 = new BitmapText(guiFont, false);
        ch2.setSize(guiFont.getCharSet().getRenderedSize());
        ch2.setText("WASD, QE, ZX, RF - Camera Controls");
        ch2.setColor(new ColorRGBA(1f,0.8f,0.1f,1f));
        ch2.setLocalTranslation(settings.getWidth()*0.3f,settings.getHeight()*0.1f,0);
        guiNode.attachChild(ch2);        
    
    
  }

        
        @Override
public void simpleUpdate(float tpf)
{
    
      move = tpf*5.0f;
      vectry = cubechar.getWorldTranslation();
      vecmove = mark.getWorldTranslation(); 
      
       remainingDist = cubechar.getLocalTranslation().distance(vecmove);

       if (remainingDist > 0.05f && !cubechar.getWorldTranslation().equals(vecmove)){
       vectry2 = vectry.interpolate(vecmove, move/remainingDist);
       cubechar.lookAt(vecmove, Vector3f.UNIT_Y);
       cubechar.setLocalTranslation(vectry2);
       }
      
      else {
           
          if (remainingDist <= 0.02f && !cubechar.getWorldTranslation().equals(vecmove)) cubechar.setLocalTranslation(vecmove);
       }
       

}

}
