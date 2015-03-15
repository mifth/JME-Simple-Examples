package com.basics;


import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;



public class AppstateTest extends SimpleApplication {

    public static void main(String[] args) {
        AppstateTest app = new AppstateTest();
        AppSettings aps = new AppSettings(true);
        app.setSettings(aps);
        app.start();
    }

   
   
              
    @Override
    public void simpleInitApp() {
        
        NewAppState aps = new NewAppState();

       boolean bl1 = stateManager.attach(aps);
//       boolean bl2 = stateManager.detach(stateManager.getState(NewAppState.class));
        
    }

    
     
      
@Override
public void simpleUpdate(float tpf)
{
          
 }

}
