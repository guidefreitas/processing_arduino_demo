/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package processingdemo;

import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Listener;
import java.lang.reflect.Method;
import processing.core.PApplet;

/**
 *
 * @author guilherme
 */
public class LeapRecognizer extends Listener implements Runnable {
    Method LeapEventMethod;
    PApplet parent;
    Controller leapController;
    
    public LeapRecognizer(PApplet _p){
        this.parent = _p;
        this.init();
    }
    
    private void init(){
        try{
            leapController = new Controller();
            leapController.addListener(this);
            LeapEventMethod = parent.getClass().getMethod("LeapEvent", 
                    new Class[] { String.class });
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    @Override
    public void onInit(Controller controller) {
        System.out.println("LeapRecognizer - onInit");
    }
    
    @Override
    public void onConnect(Controller controller) {
        System.out.println("LeapRecognizer - onConnect");
        //controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
        //controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        //controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
    }
    
    @Override
    public void onDisconnect(Controller controller) {
        System.out.println("LeapRecognizer - onDisconnect");
    }
    
    @Override
    public void onExit(Controller controller) {
        System.out.println("LeapRecognizer - onExit");
    }
    
    
    @Override
    public void onFrame(Controller controller) {
        Frame frame = controller.frame();
        GestureList gestures = frame.gestures();
        for (int i = 0; i < gestures.count(); i++) {
            Gesture gesture = gestures.get(i);
            switch (gesture.type()) {
                case TYPE_CIRCLE:
                    System.out.println("LeapRecognizer - circleGesture");
                    CircleGesture circle = new CircleGesture(gesture);
                    String clockwiseness;
                    if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI/4) {
                        // Clockwise if angle is less than 90 degrees
                        clockwiseness = "clockwise";
                    } else {
                        clockwiseness = "counterclockwise";
                    }
                    makeEvent(clockwiseness);
                    break;
            }
        }
    }
    
    @Override
    public void run() {
        //makeEvent();
    }
    
    public void makeEvent(String data) {
    if (LeapEventMethod != null) {
      try {
        LeapEventMethod.invoke(parent, new Object[] { 
          data
        }
        );
      } 
      catch (Exception e) {
        e.printStackTrace();
        LeapEventMethod = null;
      }
    }
  }
    
}
