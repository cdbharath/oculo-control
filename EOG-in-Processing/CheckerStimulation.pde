/*-----------------------------------------------------------------Written by Surya ---------------------------------------------------------------*/
// this is a second applet generated from the main applet.


//import java.awt.Frame; //  Used for creating a child applet.
//import java.lang.Thread.*;

secondApplet s;

// Interface class between main and child applets
public class CheckerStimulation extends Frame {

  public CheckerStimulation() {
    setBounds(0, 0, 600, 600);
    s = new secondApplet();
    add(s);
    s.init();
    show();
  }

  void highlight(int num) {
    s.highlightPattern(num);
  }
}


// Child applet
public class secondApplet extends PApplet {
  //  variables to design checker stimulation
  color c1  = color(255, 255, 255);      // color 
  color c2  = color(0, 0, 0);      // complement color (or) color2
  color backgr = #C8CECD;
  color highlightColor = #2CD60B ;

  String image1 = "data/pattern1.png";
  String image2 = "data/pattern2.png";
  int w, h;  // width and height of checker patterns

  PImage stim1, stim2;
  boolean [] flag;
  int [] stimx ;
  int [] stimy ;

  

  

  public void setup() {
    size((int)(displayWidth*0.9), (int)(displayHeight*0.9));

    // Generation of Checker patterns
    gCheckers(width/3, height/3, c1, c2); // 3x3 checker
    w = height/4; // width of checker
    h = height/4; // height of checker
    frameRate(60);
    stim1 = loadImage(image1);
    stim2 = loadImage(image2);
    background(backgr);

    // positioning the stimulus blocks
    stimx = new int[4];
    stimy = new int[4];
    stimx[0] = (width-w)/2 ;      
    stimy[0] = 0;
    stimx[1] = 0 ;                
    stimy[1] =(height-h)/2;
    stimx[2] = (width-w)/2 ;      
    stimy[2] =(height-h);
    stimx[3] = (width-w) ;        
    stimy[3] =(height-h)/2;

    flag = new boolean[4];
    for (int i=0; i<4; i++)flag[i] = true;
  }

  public void draw() {
    if (frameCount % 4 == 0)invertPattern(0);   // 15 Hz
    if (frameCount % 5 == 0)invertPattern(1);   // 12 Hz
    if (frameCount % 6 == 0)invertPattern(2);   // 10 Hz
    if (frameCount % 7 == 0)invertPattern(3);   // 8.57 Hz
  }

  void invertPattern(int patternNum) {
    if (flag[patternNum])image(stim2, stimx[patternNum], stimy[patternNum], w, h);
    else image(stim1, stimx[patternNum], stimy[patternNum], w, h);
    flag[patternNum] ^= true;
  }

  // Generate checker patterns
  void gCheckers(int rows, int cols, color c1, color c2) {

    for (int i = 0; i < 8; i ++) {
      for (int j = 0; j < 8; j ++) {
        if ((i + j + 1) % 2 == 0) {
          fill(c1);
        } else {
          fill(c2);
        }
        rect(i * rows, j * cols, (i + 1) * rows, (j + 1) * cols);
      }
    }
    saveFrame(image1);
    for (int i = 0; i < 8; i ++) {
      for (int j = 0; j < 8; j ++) {
        if ((i + j + 1) % 2 == 0) {
          fill(c2);
        } else {
          fill(c1);
        }
        rect(i * rows, j * cols, (i + 1) * rows, (j + 1) * cols);
      }
    }
    saveFrame(image2);
  }

  void highlightPattern(int patternNum) {
    //println(synchMode);
    if (synchMode) {
      stroke(highlightColor);
      strokeWeight(15);
    } else {
        stroke(255);
        strokeWeight(0);
    }
    if (patternNum !=-1)rect(stimx[patternNum], stimy[patternNum], w, h);
    else background(backgr);
  }
}

