/* 
 
 Sphinx wrapper for use in Processing.  
 Works with Sphinx-4 1.0beta5
 
 build from Hellow World example in Sphinx-4 Application Programmers Guide:
 http://cmusphinx.sourceforge.net/sphinx4/doc/ProgrammersGuide.html#helloworld
 
 with reference to Listen.java from Chatter project by Mie Sorensen et al.:
 http://www.lilwondermat.com/downloads/Chatter.zip
 
 rtwomey@u.washington.edu
 
 */
package processingdemo;

// processing stuff
import processing.core.*;

// sphinx stuff
import edu.cmu.sphinx.frontend.util.Utterance;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;

// java stuff
import java.io.IOException;
import java.net.URL;
import java.lang.reflect.*;


public class Sphinx implements Runnable {
  Recognizer recognizer;
  Microphone microphone;
  PApplet parent;
  Method SphinxEventMethod;

  Thread t;
  String resultText;
  String config;
  String path;
  Recognizer.State READY = Recognizer.State.READY;

  public Sphinx(PApplet _p, String _c) {
    this.parent = _p;
    this.config = _c;
    resultText = "";
    init();

    parent.registerDispose(this);

    t = new Thread(this);
    t.start();
  }

  private void init() {
    path = parent.dataPath("");
    //System.out.println("Microphone off test");
    try {
      SphinxEventMethod = parent.getClass().getMethod("SphinxEvent", new Class[] { 
        Sphinx.class
      }
      );

      // Initialize the voice recognition      
      System.out.println("Initializing Sphinx-4:");      
      System.out.println("  data directory " + path);
      System.out.println("  config file " + config);
      URL url = new URL("file:///" + path + config);

      System.out.print("loading configuration...");
      ConfigurationManager cm = new ConfigurationManager(url);
      System.out.println("loaded");

      recognizer = (Recognizer) cm.lookup("recognizer");
      System.out.print("allocating recognizer (note this may take some time)... ");
      try {
        recognizer.allocate();
      } 
      catch (Exception e) {
        e.printStackTrace();
      };
      System.out.println("allocated");

      microphone = (Microphone) cm.lookup("microphone");
      // start recording / recognizing
      System.out.print("microphone recording... ");
      if (!microphone.startRecording()) {
        System.out.println("Cannot start microphone.");
        recognizer.deallocate();
      }
      System.out.println("started");
    } 
    catch (IOException ioe) {
      ioe.printStackTrace();
    } 
    catch (PropertyException pe) {
      pe.printStackTrace();
    } 
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void run() {
    // Sphinx thread, main loop
    System.out.println("main recognition loop started");
    while (true) {
      try {        
        // ask the recognizer to recognize text in the recording
        if(recognizer.getState()==Recognizer.State.READY) {
          Result result = recognizer.recognize(); 
          // got a result
          if (result != null) {
            resultText = result.getBestFinalResultNoFiller();
            if(resultText.length()>0) {
              //              System.out.println("["+result.getStartFrame()+","+result.getEndFrame()+"]");
//              System.out.println(result.getTimedBestResult(false, true));
              makeEvent();
            }
          }
        }
      }
      catch (Exception e) { 
        System.out.println("exception Occured ");  
        e.printStackTrace(); 
        System.exit(1);
      }
    }
  }

  public String readString() {
    return resultText;
  }

  public void makeEvent() {
    if (SphinxEventMethod != null) {
      try {
        SphinxEventMethod.invoke(parent, new Object[] { 
          this
        }
        );
      } 
      catch (Exception e) {
        e.printStackTrace();
        SphinxEventMethod = null;
      }
    }
  }

  public void dispose() {
    microphone.stopRecording();
    while(recognizer.getState()==Recognizer.State.RECOGNIZING) {
      // wait till finished recognizing
    };
    recognizer.deallocate();
  }
}
