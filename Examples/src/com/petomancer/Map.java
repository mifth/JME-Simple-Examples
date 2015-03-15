package com.petomancer;

import java.util.Random;

/**
 * A seed generated dungeon map consisting of a set of rooms and doors
 * 
 * @author kevin
 */
public class Map
{
   /** The tile data for the map, each room will be placed into the tile map */
   private int[][][] data;
   /** The rooms that have been generated for the map - keyed on ID (not index!) */
   private Room[] rooms;
   /** The highest index of any room placed */
   private int highestRoomID;

   /** The minimum size of any room that will be generated */
   private int minRoomWidth;
   private int minRoomHeight;
   /** The maximum size of any room that will be genereated */
   private int maxRoomWidth;
   private int maxRoomHeight;  
   /** The width of the map in tiles */
   private int width ;
   /** The height of the map in tiles */
   private int height ;
   /** The maximum number of rooms that can be generated */
   private int maxRooms = 300;
   /** The number of times we can fail at placing a room before we give up */
   private int maxFails;
   private long seed;
   private int minRooms;
   private boolean createDoorways;


   /**
    * Create a new map
    * 
    * @param width The width in tiles of the map
    * @param height The height in tiles of the map
    */
   public Map(long seed, int width, int height, int minRooms, int maxRooms, int minRoomWidth, int minRoomHeight, int maxRoomWidth, int maxRoomHeight, int maxFails, boolean createDoorways)
   {
      this.seed = seed;
      this.width = width;
      this.height = height;
      this.minRooms = minRooms;
      this.maxRooms = maxRooms;
      this.minRoomWidth = minRoomWidth;
      this.minRoomHeight = minRoomHeight;
      this.maxRoomWidth = maxRoomWidth;
      this.maxRoomHeight = maxRoomHeight;
      this.maxFails = maxFails;
      this.createDoorways = createDoorways;
      
      data = new int[width][height][3];
      rooms = new Room[maxRooms];
   }

   /**
    * Get the width of the map in tiles
    * 
    * @return The width of the map in tiles
    */
   public int getWidth()
   {
      return width;
   }

   /**
    * Get the height of the map in tiles
    * 
    * @return The height of the map in tiles
    */
   public int getHeight()
   {
      return height;
   }

   /**
    * Get the highest room ID that has been generated 
    * 
    * @return The highest room ID that has been generated
    */
   public int getHighestRoomID()
   {
      return highestRoomID;
   }

   /**
    * Set the tile at a given location
    * 
    * @param x The x coordinate at which to set the tile
    * @param y The y coordinate at which to set the tile
    * @param l The layer to set the tile on
    * @param tile The tile to set or 0 to clear
    */
   public void setTile(int x, int y, int l, int tile)
   {
      data[x][y][l] = tile;
   }

   /**
    * Get the tile at a given location
    * 
    * @param x The x coordinate at which to get the tile
    * @param y The y coordinate at which to get the tile
    * @param l The layer at which to get the tile
    * @return The tile at the given location
    */
   public int getTile(int x, int y, int l)
   {
      if ((x < 0) || (y < 0) || (x >= width) || (y >= height))
      {
         return 0;
      }

      return data[x][y][l];
   }
   
   public int getTile(int x, int y)
   {
      return data[x][y][0];
   }

   /**
    * Generate a map
    * 
    * @param seed The seed for the random number generator
    * @param minRooms The minimum number of rooms to accept in a generation
    */
   public void generate()
   {
      // create a random number generate
      // that we'll use for everything so we can
      // always generate the exact same map again
      Random random = new Random(seed);
      int totalRooms = 0;

      while (totalRooms < minRooms)
      {
         // initialise the generation data structures
         data = new int[width][height][3];
         rooms = new Room[maxRooms];
         highestRoomID = 0;
         totalRooms = 0;
         int failCount = 0;

         // keep placing random sized rooms and random locaitons until we've
         // generated enough or we've failed too many times.
         while ((failCount < maxFails) && (highestRoomID < maxRooms - 1))
         {
            // random size and position
            int roomWidth = minRoomWidth + random.nextInt(maxRoomWidth - minRoomWidth);
            int roomHeight = minRoomHeight + random.nextInt(maxRoomHeight - minRoomHeight);

            int roomX = random.nextInt(width - roomWidth);
            int roomY = random.nextInt(height - roomHeight);

            // ensure that rooms at only position in even tiles, this is
            // to line rooms up better and give more chance for doors
            roomX = (roomX / 2) * 2;
            roomY = (roomY / 2) * 2;
            roomWidth = (roomWidth / 2) * 2;
            roomHeight = (roomHeight / 2) * 2;

            // Check there isn't already a room placed at the specified
            // size and location.
            boolean clear = true;
            for (int x = roomX; x < roomX + roomWidth; x++)
            {
               for (int y = roomY; y < roomY + roomHeight; y++)
               {
                  if (getTile(x, y, 0) != 0)
                  {
                     clear = false;
                     break;
                  }
               }
            }

            if (clear)
            {
               // if there isn't a room at the given location, create a new one
               // and fill the map in for it's ID. Store the room away.
               highestRoomID++;
               totalRooms++;
               Room room = new Room(highestRoomID, roomX, roomY, roomWidth, roomHeight);
               room.place(this);
               rooms[highestRoomID] = room;
            }
            else
            {
               failCount++;
            }
         }
         
         if (!createDoorways)
         {
            return ;
         }

         // places doors to link adjacent rooms
         for (int i = 0; i < highestRoomID; i++)
         {
            rooms[i + 1].placeDoors(this, random);
         }

         // prune islands - this is important since we want to be 
         // able to explore the dungeon fully starting in any room - this 
         // means that all rooms must be linked together some how. So, we 
         // go through each room "filling" is with a new ID, this causes any rooms
         // linked to it, and rooms linked to those and so on, to be filled with the
         // same ID. If we find any rooms without an "island ID" then they are completely
         // separated.
         int islandID = 1;
         int bestCount = 0;
         int bestIsland = 0;

         for (int i = 0; i < highestRoomID; i++)
         {
            Room room = rooms[i + 1];
            if (room.getIslandID() == -1)
            {
               int count = room.flood(islandID);
               if (count > bestCount)
               {
                  bestCount = count;
                  bestIsland = islandID;
               }
               islandID++;
            }
         }

         // finally remove any rooms that don't form part of the biggest
         // island, leaving us with one big dungeon area to explore
         for (int i = 0; i < highestRoomID; i++)
         {
            Room room = rooms[i + 1];
            if (room.getIslandID() != bestIsland)
            {
               room.clear(this);
               rooms[i + 1] = null;
               totalRooms--;
            }
         }
      }
   }

   /**
    * Get a room by it's ID
    * 
    * @param id The ID of the room to retrieve
    * @return The room if it exists, or null
    */
   public Room getRoomByID(int id)
   {
      return rooms[id];
   }
}