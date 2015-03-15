package com.petomancer;

/**
 * A Door links two rooms in the dungeon
 * 
 * @author kevin
 */
public class Door
{
   /** The first room the door links to */
   private Room roomA;
   /** The x coordinate of the position in room A of the door */
   private int ax;
   /** The y coordinate of the position in room A of the door */
   private int ay;
   /** The second  room the door links to */
   private Room roomB;
   /** The x coordinate of the position in room B of the door */
   private int bx;
   /** The y coordinate of the position in room B of the door */
   private int by;

   /**
    * Create a new door
    * 
    * @param ax The x coordinate of the position in room A of the door
    * @param ay The y coordinate of the position in room A of the door
    * @param roomA The first room the door links to
    * @param bx The x coordinate of the position in room A of the door
    * @param by The y coordinate of the position in room A of the door
    * @param roomB The second room the door links to
    */
   public Door(int ax, int ay, Room roomA, int bx, int by, Room roomB)
   {
      this.ax = ax;
      this.ay = ay;
      this.roomA = roomA;
      this.bx = bx;
      this.by = by;
      this.roomB = roomB;
   }

   /**
    * Check if this door is placed at the given location. This checks both
    * linked rooms.
    * 
    * @param x The x position to check 
    * @param y The y position to check
    * @return True if this door contains the given position
    */
   public boolean contains(int x, int y)
   {
      return ((ax == x) && (ay == y)) || ((bx == x) && (by == y));
   }

   /**
    * Get the x coordinate of the position of door in room A
    * 
    * @return The x coordinate
    */
   public int getAX()
   {
      return ax;
   }

   /**
    * Get the y coordinate of the position of door in room A
    * 
    * @return The y coordinate
    */
   public int getAY()
   {
      return ay;
   }

   /**
    * Get the x coordinate of the position of door in room B
    * 
    * @return The x coordinate
    */
   public int getBX()
   {
      return bx;
   }

   /**
    * Get the y coordinate of the position of door in room B
    * 
    * @return The y coordinate
    */
   public int getBY()
   {
      return by;
   }

   /**
    * The first room that is linked by this door
    * 
    * @return The first room that is linked
    */
   public Room getRoomA()
   {
      return roomA;
   }

   /**
    * The second room that is linked by this door
    * 
    * @return The second room that is linked 
    */
   public Room getRoomB()
   {
      return roomB;
   }
}
