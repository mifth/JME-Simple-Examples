/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpleChaseCamera;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

/**
 *
 * @author mifth
 */
public class SimpleCameraState extends AbstractAppState {

    private SimpleChaseCamera chState;
    private Application app;

    public SimpleCameraState(Application app) {
        this.app = app;
        chState = new SimpleChaseCamera(app, app.getInputManager());
    }
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        if(chState != null) {
            chState.update();
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    public SimpleChaseCamera getChaseCamera() {
        return chState;
    }
    
}
