/*-----------------------------------------------------------------Written by Surya ---------------------------------------------------------------*/
// this is a second applet generated from the main applet.
// this is to display the response of eye ball movement
// Eye ball movement is captured by processing EOG data

import java.awt.Frame; //  Used for creating a child applet.
import java.lang.Thread.*;

thirdApplet t;


public class GuestureDetection extends Frame {

  public GuestureDetection() {
    setBounds(0, 0, 600, 600);
    t = new thirdApplet();
    add(t);
    t.init();
    show();
  }

  public void movement (int move) {
    t.movement(move);
  }
}

public class thirdApplet extends PApplet {
  int ellipseDiameter = 50;
  int triangleSide = 50;
  int triangleFromCentre = 100;
  color red = color(255, 0, 0);
  color white = color(255, 255, 255);
  color selectColor = white;
  color upColor = white;
  color downColor = white;
  color leftColor = white;
  color rightColor = white;
  int wait = 200;

  public void setup() {
    size(600, 600);
    background(255);
  }

  public void draw() {
    updateDisplay();
  }

  /* mov = 1 -> up
   = 3 -> down
   = 2 -> left
   = 4 -> right
   = 0 -> select
   */
 public void movement (int move) {
    switch(move) {

    case 1 :  
      thread("up");
      if(controlBot)serial_botControl.write('w');
      break;

    case 3 :  
      thread("down");
      if(controlBot)serial_botControl.write('x');
      break;

    case 2 :  
      thread("left");
       if(controlBot)serial_botControl.write('a');
      break;

    case 4 :  
      thread("right");
      if(controlBot)serial_botControl.write('d');
      break;

    case 0 :  
      thread("select");
      if(controlBot)serial_botControl.write('s');   // stop
      break;
    }
  }
  void up() {
    upColor = red;
    delay(wait);
    upColor = white;
  }

  void down() {
    downColor = red;
    delay(wait);
    downColor = white;
  }

  void left() {
    leftColor = red;
    delay(wait);
    leftColor = white;
  }

  void right() {
    rightColor = red;
    delay(wait);
    rightColor = white;
  }

  void select() {
    selectColor = red;
    delay(wait);
    selectColor = white;
  }

  void delay(int delay)
  {
    int time = millis();
    while (millis () - time <= delay);
  }


  void updateDisplay() {
    stroke(0, 0, 0);

    fill(selectColor);
    displaySelect();

    fill(upColor);
    displayUp();

    fill(downColor);
    displayDown();

    fill(leftColor);
    displayLeft();

    fill(rightColor);
    displayRight();
  }


  void displaySelect() {
    ellipse(width/2, height/2, ellipseDiameter, ellipseDiameter);  // centre
  }

  void displayUp() {
    triangle(width/2-triangleSide/2, height/2-triangleFromCentre, // up
    width/2+triangleSide/2, height/2-triangleFromCentre, 
    width/2, height/2-(1.732/2*triangleSide)-triangleFromCentre);
  }

  void displayDown() {
    triangle(width/2-triangleSide/2, height/2+triangleFromCentre, // down
    width/2+triangleSide/2, height/2+triangleFromCentre, 
    width/2, height/2+(1.732/2*triangleSide)+triangleFromCentre);
  }

  void displayLeft() {
    triangle(width/2-triangleFromCentre, height/2-triangleSide/2, // left
    width/2-triangleFromCentre, height/2+triangleSide/2, 
    width/2-(1.732/2*triangleSide)-triangleFromCentre, height/2);
  }

  void displayRight() {
    triangle(width/2+triangleFromCentre, height/2-triangleSide/2, // right
    width/2+triangleFromCentre, height/2+triangleSide/2, 
    width/2+(1.732/2*triangleSide)+triangleFromCentre, height/2);
  }
}

