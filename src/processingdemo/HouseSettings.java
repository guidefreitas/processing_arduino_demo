/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package processingdemo;

import com.firebase.client.*;

/**
 *
 * @author guilherme
 */
public class HouseSettings {
    private static final String FIREBASE_URL = "https://domotic.firebaseio.com";
    private final Firebase ref;
    
    public HouseSettings(){
        ref = new Firebase(FIREBASE_URL).child("house_settings");
    }
    
    public Firebase getRef(){
        return ref;
    }
    
    
    public void setFanSpeed(Long fanSpeed){
        assert(fanSpeed > 0);
        assert(fanSpeed <= 255);
        ref.child("fan_speed").setValue(fanSpeed);
    }
    
    public void setLightState(Boolean state){
        ref.child("light_state").setValue(state);
    }
    
    public void setLightRed(Long value){
        assert(value>0 || value<=255);
        ref.child("light_r").setValue(value);
    }
    
    public void setLightGreen(Long value){
        assert(value>0 || value<=255);
        ref.child("light_g").setValue(value);
    }
    
    public void setLightBlue(Long value){
        assert(value>0 || value<=255);
        ref.child("light_b").setValue(value);
    }
    
    public void push(){
        ref.push();
    }
}
