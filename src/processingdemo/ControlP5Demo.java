package processingdemo;

import com.firebase.client.*;
import controlP5.*;
import processing.core.PApplet;
import cc.arduino.*;


/**
 *
 * @author guilherme
 */

public class ControlP5Demo extends PApplet {

    HouseSettings settings = new HouseSettings();
    Sphinx recog;
    LeapRecognizer leapRecognizer;
    Boolean isRecognizing = false;
    ControlP5 cp5;
    Arduino arduino = null; 
 
    String stringRecognized = ""; // variable for sphinx results
    int lastevent = 0;
    
    //private Slider sliderLightIntensity;
    private Slider sliderFanSpeed;
    private Slider sliderRed, sliderGreen, sliderBlue;
    private CheckBox cbTurnOnOff;
    private Textlabel statusLabel;
    
    int ledPin = 13;
    int ledRed = 7;
    int ledGreen = 6;
    int ledBlue = 5;
    int motorPin = 10;
    float fanSpeedValue = 0;
    float distanceValue = 0;
    
    
    private void setupArduino(){
        //println(Arduino.list());
        String arduinoDevice = "/dev/tty.usbserial-A4004G9A";
        arduino = new Arduino(this,arduinoDevice,57600);
        
        //Setup Arduino Pins
        arduino.pinMode(ledPin, Arduino.OUTPUT);
        arduino.pinMode(ledRed, Arduino.OUTPUT);
        arduino.pinMode(ledGreen, Arduino.OUTPUT);
        arduino.pinMode(ledBlue, Arduino.OUTPUT);
        arduino.pinMode(motorPin, Arduino.OUTPUT);
       
        
    }
    
    public void LeapEvent(String data){
        println("LeapEvent: " + data);
        if(data.equals("clockwise")){
            setFanSpeed(254);
            delay(100);
            setLightState(true);
            delay(100);
        }else if(data.equals("counterclockwise")){
            setFanSpeed(1);
            delay(100);
            setLightState(false);
            delay(100);
        }
    }
    
    public void SphinxEvent(Sphinx _l) {
        int now = millis();

        stringRecognized = _l.readString(); // returns the recognized string

          // echo to screen 
      //  System.out.print("["+now+"] sphinx heard: "+s);
        System.out.print("["+now+"] "+stringRecognized);

        // intra-utterance timing
        System.out.println("  ("+(now-lastevent)+" since last utterance)");
        lastevent=now;
        
        if(stringRecognized.indexOf("light") >= 0){
            toggleLight();
        }
        
        if(stringRecognized.indexOf("blue") >= 0){
            setLightBlueChannel(254);
            setLightRedChannel(1);
            setLightGreenChannel(1);
        }
        
        if(stringRecognized.indexOf("red") >= 0){
            setLightBlueChannel(1);
            setLightRedChannel(254);
            setLightGreenChannel(1);
        }
        
        if(stringRecognized.indexOf("green") >= 0){
            setLightBlueChannel(1);
            setLightRedChannel(1);
            setLightGreenChannel(254);
        }
            
        // check for stop command
        if((stringRecognized.indexOf("quit") >= 0) || (stringRecognized.indexOf("exit") >= 0) || ((stringRecognized.indexOf("stop") >= 0) && (stringRecognized.indexOf("dystopian") < 0))) {
          println("Exit");
        }
        
        settings.push();
      }

    

    private void setupScreen() {
        
        size(700, 400);
        smooth();
        cp5 = new ControlP5(this);

        this.sliderFanSpeed = cp5.addSlider("fanSlider", 0, 255, 20, 100, 128, 20);
        this.sliderFanSpeed.setCaptionLabel("Fan Speed");
        
        this.sliderFanSpeed.addCallback(new CallbackListener() {

            @Override
            public void controlEvent(CallbackEvent ce) {
                if(ce.getAction() == Slider.ACTION_RELEASED || ce.getAction() == Slider.ACTION_LEAVE){
                    float value = sliderFanSpeed.getValue();
                    setFanSpeed(value);
                }
            }
        });
      
        sliderRed = cp5.addSlider("slider-red", 0, 255, 300, 100, 40, 40);
		cp5.removeProperty(sliderRed);
        sliderRed.setId(0);
        sliderRed.setBroadcast(false);
        sliderRed.moveTo(this);
        sliderRed.setMoveable(false);
        sliderRed.setColorBackground(0xff660000);
        sliderRed.setColorForeground(0xffaa0000);
        sliderRed.setColorActive(0xffff0000);
        sliderRed.getCaptionLabel().setVisible(false);
        sliderRed.setDecimalPrecision(0);
        sliderRed.setMin(1);
                
        sliderRed.addCallback(new CallbackListener() {

            @Override
            public void controlEvent(CallbackEvent ce) {
                if(ce.getAction() == ControlP5.ACTION_RELEASED || ce.getAction() == ControlP5.ACTION_RELEASEDOUTSIDE){
                    float value = sliderRed.getValue();
                    setLightRedChannel(value);
                }
            }
        });
        
        sliderGreen = cp5.addSlider("slider-green", 0, 255, 300, 150, 40, 40);
        sliderGreen.setId(1);
        sliderGreen.setBroadcast(false);
        sliderGreen.moveTo(this);
        sliderGreen.setMoveable(false);
        sliderGreen.setColorBackground(0xff006600);
        sliderGreen.setColorForeground(0xff00aa00);
        sliderGreen.setColorActive(0xff00ff00);
        sliderGreen.getCaptionLabel().setVisible(false);
        sliderGreen.setDecimalPrecision(0);
        sliderGreen.setMin(1);
        sliderGreen.addCallback(new CallbackListener() {
            @Override
            public void controlEvent(CallbackEvent ce) {
                if(ce.getAction() == ControlP5.ACTION_RELEASED || ce.getAction() == ControlP5.ACTION_RELEASEDOUTSIDE){
                    float value = sliderGreen.getValue();
                    setLightGreenChannel(value);
                }
            }
        });
        
        sliderBlue = cp5.addSlider("slider-blue", 0, 255, 300, 200, 40, 40);
        sliderBlue.setId(2);
        sliderBlue.setBroadcast(false);
        sliderBlue.moveTo(this);
        sliderBlue.setMoveable(false);
        sliderBlue.setColorBackground(0xff000066);
        sliderBlue.setColorForeground(0xff0000aa);
        sliderBlue.setColorActive(0xff0000ff);
        sliderBlue.getCaptionLabel().setVisible(false);
        sliderBlue.setDecimalPrecision(0);
        sliderBlue.setMin(1);
        sliderBlue.addCallback(new CallbackListener() {
            @Override
            public void controlEvent(CallbackEvent ce) {
                if(ce.getAction() == ControlP5.ACTION_RELEASED || ce.getAction() == ControlP5.ACTION_RELEASEDOUTSIDE){
                    float value = sliderBlue.getValue();
                    setLightBlueChannel(value);
                }
            }
        });
        
        this.cbTurnOnOff = cp5.addCheckBox("checkBox")
                .setPosition(400, 100)
                .setColorForeground(color(120))
                .setColorActive(color(255))
                .setColorLabel(color(255))
                .setSize(40, 40)
                .setItemsPerRow(1)
                .setSpacingColumn(30)
                .setSpacingRow(20)
                .addItem("Light On", 0);
        
        this.cbTurnOnOff.getItem(0).addCallback(new CallbackListener() {
            @Override
            public void controlEvent(CallbackEvent ce) {
                if(ce.getAction() == ControlP5.ACTION_PRESSED){
                    boolean state = cbTurnOnOff.getState(0);
                    setLightState(state);
                }
                
            }
        });

      
        
        this.statusLabel = cp5.addTextlabel("statusLabel")
                    .setText("")
                    .setPosition(10,380)
                    .setColor(255)
                    .setFont(createFont("Arial",12))
                    ;

    }
    
    private void setupVoiceRecognition(){
        this.recog = new Sphinx(this, "/colors.config.xml");
    }
    
    private void setupLeapRecognition(){
        //this.leapRecognizer = new LeapRecognizer(this);
    }
    
    private void setStatusMessage(String msg){
        this.statusLabel.setText(msg);
    }

    private void setupEvents() {

        settings.getRef().addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snap) {
                if ("house_settings".equals(snap.getName())) {
                    setStatusMessage("Dados recebidos do servidor...");
                    
                    Long fanSpeed = (Long) snap.child("fan_speed").getValue();
                    Long r = (Long) snap.child("light_r").getValue();
                    Long g = (Long) snap.child("light_g").getValue();
                    Long b = (Long) snap.child("light_b").getValue();
                    
                    Boolean lightState = (Boolean) snap.child("light_state").getValue();
                    
                    setLightColor(r, g, b);
                    setFanSpeed(fanSpeed);
                    setLightState(lightState);
                    
                    
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
        
        
        
    }

    @Override
    public void setup() {
        this.setupArduino();
        this.setupScreen();
        this.setupEvents();
        this.setupVoiceRecognition();
        this.setupLeapRecognition();
    }
    
    public void setLightColor(Long r, Long g, Long b){
        setLightRedChannel((float)r);
        setLightGreenChannel((float)g);
        setLightBlueChannel((float)b);
    }
    
    public void setLightRedChannel(float value){
        if((float) getLightRed() != value){
            sliderRed.setValue(value);
            settings.setLightRed((long)value);
            arduino.analogWrite(ledRed, (int) (255-value));
        
            delay(10);
        }
    }
    
    public void setLightGreenChannel(float value){
        if((float) getLightGreen() != value){
            sliderGreen.setValue(value);
            settings.setLightGreen((long)value);
            arduino.analogWrite(ledGreen, (int) (255-value));
            delay(10);
        }
    }
    
    public void setLightBlueChannel(float value){
        if((float)getLightBlue() != value){
            sliderBlue.setValue(value);
            settings.setLightBlue((long) value);
            arduino.analogWrite(ledBlue, (int) (255-value));
            delay(10);
        }
    }
    
    public int getLightRed(){
        return (int) sliderRed.getValue();
    }
    
    public int getLightGreen(){
        return (int) sliderGreen.getValue();
    }
    
    public int getLightBlue(){
        return (int) sliderBlue.getValue();
    }
   
    
    public void setLightState(boolean state){
        this.cbTurnOnOff.getItem(0).setState(state);
        if(state){
            arduino.digitalWrite(ledPin, Arduino.HIGH);
        }else{
            arduino.digitalWrite(ledPin, Arduino.LOW);
        }
        settings.setLightState(state);
    }
    
    public boolean getLightState(){
        boolean currentState = this.cbTurnOnOff.getItem(0).getState();
        return currentState;
    }
    
    public void toggleLight(){
        setLightState(!getLightState());
    }
    
    public void setFanSpeed(float value){
        sliderFanSpeed.setValue(value);
        settings.setFanSpeed((long)value);
        int fanSpeed = (int) map(value,0,255,0,255);
        arduino.analogWrite(motorPin, fanSpeed);
    }
    
    public float getFanSpeed(){
        return sliderFanSpeed.getValue();
    }
    
    
    @Override
    public void draw() {
        background(0);
        
    }
    
}
