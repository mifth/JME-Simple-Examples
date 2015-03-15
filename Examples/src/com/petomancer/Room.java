package com.petomancer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * A single room defined in a generate map. It specifies both it's size, location and
 * the other rooms which it links to
 * 
 * @author kevin
 */
public class Room
{
   /** The x coordinate of the position of this room */
   private int x;
   /** The y coordinate of the position of this room */
   private int y;
   /** The width of the room in tiles */
   private int width;
   /** The height of the room in tiles */
   private int height;
   /** The ID given for this room */
   private int id;
   /** The other rooms that this room links to */
   private ArrayList<Room> connections = new ArrayList<Room>();
   /** A mapping from a connected room to the door that links us to that room, used to check for duplicate doors */
   private HashMap<Room, Door> doors = new HashMap<Room, Door>();
   /** The island ID that has been assigned or -1 */
   private int islandID = -1;

   /**
    * Create a new room
    * 
    * @param id The ID to give the room
    * @param x The x coordinate of the position of this room
    * @param y The y coordinate of the position of this room
    * @param width The width in tiles of the room
    * @param height The height in tiles of the room
    */
   public Room(int id, int x, int y, int width, int height)
   {
      this.id = id;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   /**
    * Get the number of doors in the room
    * 
    * @return The number of doors in the room 
    */
   public int getDoorCount()
   {
      return connections.size();
   }

   /**
    * Get a door defined in this room
    * 
    * @param index The index of the door to retrieve
    * @return The door requested
    */
   public Door getDoor(int index)
   {
      Room room = connections.get(index);
      return doors.get(room);
   }

   /**
    * Place this room onto the tile map. Each tile included in this room
    * will be set to the ID of this room
    * 
    * @param map The map to place the room into
    */
   public void place(Map map)
   {
      for (int xp = 0; xp < width; xp++)
      {
         for (int yp = 0; yp < height; yp++)
         {
            map.setTile(x + xp, y + yp, 0, id);
         }
      }
   }
   
   public void place(int[][][] map)
   {
      for (int xp = 0; xp < width; xp++)
      {
         for (int yp = 0; yp < height; yp++)
         {
            map[x + xp][y + yp][0] = id ;
         }
      }
   }

   /**
    * Check if there is a door at the given location
    * 
    * @param x The x coordinate of the position to check
    * @param y The y coordinate of the position to check
    * @return True if there is a door at the given position
    */
   public boolean isDoor(int x, int y)
   {
      for (int i = 0; i < connections.size(); i++)
      {
         Room room = connections.get(i);
         if (doors.get(room).contains(x, y))
         {
            return true;
         }
      }

      return false;
   }

   /**
    * Consider where it's possible to place a door a given location. If it is
    * add it to the list
    * 
    * @param possibles The map from target room ID to the list of possible doors to the room
    * @param map The map the doors will exist on
    * @param x The x location in the target room to consider
    * @param y The y location in the target room to consider
    * @param ox The x location in the current room the door would start from
    * @param oy The y location in the current room the door would start from
    */
   private void considerDoor(HashMap<Integer, ArrayList<int[]>> possibles, Map map, int x, int y, int ox, int oy)
   {
      // if theres a room at the target location
      int targetRoomID = map.getTile(x, y, 0);
      if (targetRoomID != 0)
      {
         Room targetRoom = map.getRoomByID(targetRoomID);
         // if the room doesn't already have a door at the given location
         if (targetRoom.isDoor(x, y))
         {
            return;
         }
         // and this room doesn't already have a door at the location
         if (isDoor(ox, oy))
         {
            return;
         }

         // record the location as a possible door
         ArrayList<int[]> doors = possibles.get(targetRoomID);
         if (doors == null)
         {
            doors = new ArrayList<int[]>();
            possibles.put(targetRoomID, doors);
         }
         doors.add(new int[]
                 {
                    x, y, ox, oy
                 });
      }
   }

   public void placeDoors(Map map, Random random)
   {
      HashMap<Integer, ArrayList<int[]>> possibles = new HashMap<Integer, ArrayList<int[]>>();

      // first record all the possible locations in the room
      // that could be doors and which room they link to
      // check top and bottom
      for (int xp = 0; xp < width; xp++)
      {
         considerDoor(possibles, map, x + xp, y - 1, x + xp, y);
         considerDoor(possibles, map, x + xp, y + height, x + xp, y + height - 1);
      }
      // check sides
      for (int yp = 0; yp < height; yp++)
      {
         considerDoor(possibles, map, x - 1, y + yp, x, y + yp);
         considerDoor(possibles, map, x + width, y + yp, x + width - 1, y + yp);
      }

      // for each room that we could potentially link to
      Iterator<Integer> targets = possibles.keySet().iterator();
      while (targets.hasNext())
      {
         // get the target room
         Integer target = targets.next();
         Room targetRoom = map.getRoomByID(target);

         // if we don't already have a connection to the target room
         if (!connections.contains(targetRoom))
         {
            // pick one of the possible locations for a door randomly
            ArrayList<int[]> options = possibles.get(target);
            int option = random.nextInt(options.size());
            int[] pos = options.get(option);

            int tx = pos[0];
            int ty = pos[1];
            int ox = pos[2];
            int oy = pos[3];

            // and link up the two rooms up
            Door door = new Door(tx, ty, targetRoom, ox, oy, this);
            connections.add(targetRoom);
            targetRoom.connections.add(this);
            doors.put(targetRoom, door);
            targetRoom.doors.put(this, door);
         }
      }
   }

   /**
    * Clear the area in the map that this room occupies, this is 
    * to undo @see Room.place
    * 
    * @param map The map to clear this room from
    */
   public void clear(Map map)
   {
      for (int xp = 0; xp < width; xp++)
      {
         for (int yp = 0; yp < height; yp++)
         {
            map.setTile(x + xp, y + yp, 0, 0);
         }
      }
   }

   /**
    * Get the island ID that has been assigned to this room
    * 
    * @return The island ID that has been assigned to this room
    */
   public int getIslandID()
   {
      return islandID;
   }

   /**
    * Floor fill starting at this room identifying any rooms
    * that are connected directly, or indirectly to this one.
    * 
    * @param id The island ID to use for the fill
    * @return The number of rooms that have been found linked together
    */
   public int flood(int id)
   {
      islandID = id;
      int count = 1;

      for (int i = 0; i < connections.size(); i++)
      {
         Room target = connections.get(i);
         if (target.getIslandID() == -1)
         {
            count += target.flood(id);
         }
      }

      return count;
   }
}
