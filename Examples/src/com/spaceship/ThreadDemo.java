/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spaceship;





/**
 *
 * @author mifth
 */
public class ThreadDemo {
   public static void main(String args[]) {
        ThreadTest t = new ThreadTest(); // create a new thread

      try {
         for(int i = 500; i > 0; i--) {
           System.out.println("Main Thread: " + i);
           Thread.sleep(1000);
         }
      } catch (InterruptedException e) {
         System.out.println("Main thread interrupted.");
      }
      System.out.println("Main thread exiting.");
   }
}
