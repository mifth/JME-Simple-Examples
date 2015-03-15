package com.petomancer;


import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.util.TangentBinormalGenerator;
import java.util.logging.Level;
import java.util.logging.Logger;
import jme3tools.optimize.GeometryBatchFactory;

public class DungeonRenderer
{
   public static final float STAGE_SIZE = 128f;
   public static final Quad IDENTITY_QUAD = new Quad(1, 1);

   public static void main(String[] args)
   {
      Logger.getLogger("").setLevel(Level.SEVERE);
      
      final SimpleApplication scene = new SimpleApplication()
      {
         @Override
         public void simpleInitApp()
         {
            getFlyByCamera().setMoveSpeed(10);
            getViewPort().setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
            
            DirectionalLight dl = new DirectionalLight();
            dl.setDirection(new Vector3f(0.69305897f, -0.51373863f, 0.5057093f).normalizeLocal());
            dl.setColor(new ColorRGBA(0.8f, 0.8f, 0.8f, 0.8f));
            getRootNode().addLight(dl);
            
            AmbientLight amb = new AmbientLight();
            amb.setColor(new ColorRGBA(3.7f, 3.5f, 3.9f, 1.0f));
            getRootNode().addLight(amb);
            
            onInit(this);
         }
      };
      scene.start();
   }

   public static void onInit(SimpleApplication scene)
   {
      //code and coke map import.
      Map map = new Map(System.currentTimeMillis(), (int) STAGE_SIZE, (int) STAGE_SIZE, 30, 300, 6, 6, 10, 10, 5000, true)
      {
      };
      map.generate();
      setPlayerStartPosition(map, scene);


      Material emptyTile = null;//assetManager.loadMaterial("Materials/Lava.j3m");
      Material fullTile = scene.getAssetManager().loadMaterial("Materials/CobbleStoneGround.j3m");
      Material wall = scene.getAssetManager().loadMaterial("Materials/CobbleStoneGround.j3m");

      scene.getRootNode().attachChild(createFloor(map, fullTile, false, emptyTile, true, wall, 0.0f, 1));
   }

   public static void setPlayerStartPosition(Map map, SimpleApplication scene)
   {
      Vector3f playerStartLocation = Vector3f.POSITIVE_INFINITY;
      for (int i = 0; i < map.getWidth(); i++)
      {
         for (int j = 0; j < map.getHeight(); j++)
         {
            if (!(map.getTile(i, j) == 0) && (i <= playerStartLocation.x) && (j <= playerStartLocation.z)) playerStartLocation = new Vector3f(i + 0.5f, 0, j + 0.5f);
         }
      }
      playerStartLocation.addLocal(0,5,0);
      scene.getCamera().setLocation(playerStartLocation);
      Vector3f direction = playerStartLocation.clone();
      direction.y -= 5;
      scene.getCamera().lookAt(direction, Vector3f.UNIT_Y);
   }
   
    /**Lower left corner is at (0,0,0) and top right corner is at (x,y,z)*/
   public static Box createLowerLeftBox(float x, float y, float z)
   {
      return new Box(new Vector3f(x / 2f, y / 2f, z / 2f), x / 2f, y / 2f, z / 2f);
   }
   
    public static Geometry createHorizontalQuad(float x, float y, float z, float width, float length)
   {
      Quad quad = IDENTITY_QUAD;
      if (width != 1 && length != 1) quad = new Quad(width, length);

      Geometry tile = new Geometry("Quad", quad);
      tile.rotate(FastMath.HALF_PI, 0, FastMath.PI);
      tile.move(x + width, y, z);
      return tile;
   }

   public static Node createFloor(Map floor, Material fullTile, boolean displayEmptyTiles, Material emptyTile, boolean createWalls, Material wallMaterial, float wallWidth, float wallHeight)
   {
      Node ground = new Node("Ground");
      Box wall = createLowerLeftBox(1f, wallHeight, wallWidth);
      
      for (int x = 0; x < floor.getWidth(); x++)
      {
         for (int y = 0; y < floor.getHeight(); y++)
         {
            if (displayEmptyTiles || (!displayEmptyTiles && !(floor.getTile(x,y)==0)))//if ! empty
            {
               Geometry tile = createHorizontalQuad(x, 0, y, 1, 1);
               //tile.setShadowMode(ShadowMode.Receive);
               if (floor.getTile(x, y)==0) tile.setMaterial(emptyTile);
               else tile.setMaterial(fullTile);
               
               ground.attachChild(tile);
            }
            
            if (createWalls)
            {
               //Left wall
               if (!(floor.getTile(x, y)==0) && ((y == 0) || (floor.getTile(x,y - 1)==0)))
               {
                  Geometry leftWall = new Geometry("Left Wall (" + x + "," + y + ")", wall);
                  leftWall.setMaterial(wallMaterial);
                  leftWall.move(x, 0, y - wallWidth);
                  leftWall.setShadowMode(ShadowMode.Cast);
                  ground.attachChild(leftWall);
               }

               //South wall
               if (!(floor.getTile(x, y)==0) && ((x == 0) || (floor.getTile(x - 1,y)==0)))
               {
                  Geometry southWall = new Geometry("South Wall (" + x + "," + y + ")", wall);
                  southWall.setMaterial(wallMaterial);
                  southWall.rotate(0, FastMath.HALF_PI, 0);
                  southWall.move(x - wallWidth, 0, y + 1);
                  southWall.setShadowMode(ShadowMode.Cast);
                  ground.attachChild(southWall);
               }

               //Right wall
               if (!(floor.getTile(x, y)==0) && ((y + 1 >= floor.getHeight()) || (floor.getTile(x, y + 1)==0)))
               {
                  Geometry rightWall = new Geometry("Right Wall (" + x + "," + y + ")", wall);
                  rightWall.setMaterial(wallMaterial);
                  rightWall.move(x, 0, y + 1);
                  rightWall.setShadowMode(ShadowMode.Cast);
                  ground.attachChild(rightWall);
               }

               //Top wall
               if (!(floor.getTile(x, y)==0) && ((x + 1 >= floor.getWidth()) || (floor.getTile(x+1, y)==0)))
               {
                  Geometry topWall = new Geometry("Top Wall (" + x + "," + y + ")", wall);
                  topWall.setMaterial(wallMaterial);
                  topWall.rotate(0, FastMath.HALF_PI, 0);
                  topWall.move(x + 1, 0, y + 1);
                  topWall.setShadowMode(ShadowMode.Cast);
                  ground.attachChild(topWall);
               }
            }//if
         }//for
      }//for
      TangentBinormalGenerator.generate(ground);
      ground = (Node) GeometryBatchFactory.optimize(ground);
//      TangentBinormalGenerator.generate(ground);
      
      return ground;
   }//renderFloor
}