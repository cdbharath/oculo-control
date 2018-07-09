/*
autoCorr example

Adila Faruk, 2012.
 */
 
// import the library
import papaya.*;

// font
PFont titleFont = createFont("Helvetica", 14, true);
PFont smallFont = createFont("Helvetica", 12, true);

int ln = 100;
float[] sinx = new float[ln]; float[] x = new float[ln];
int[] lag = new int[ln];
float[] autoCorr = new float[ln];
float meanY, varianceY;

float minX, maxX;

void setup() {

  size(450, 500); 
  noLoop();
  for (int i=0; i<ln; i++) {  
    // x = rnorm(100) + sin(2*pi*1:100/10) 
    // http://en.wikipedia.org/wiki/File:Acf_new.svg
    // from a wikipedia search on 'autoCorr'
    lag[i] = i;
    sinx[i] = random(-1, 1) + sin(TWO_PI*i/10); // max = -2, min = 2
    x[i] = xLeft + 3*i;
  }
  // get the mean and variance
  meanY = Descriptive.mean(sinx);
  varianceY = Descriptive.var(sinx,false); // division by n
  // compute the autoCorr for each lag value
  for (int i=0; i<ln; i++) {
    autoCorr[i] = Correlation.auto(sinx, lag[i], meanY, varianceY);
  }
}

// plotting variables
float xLeft = 70; float xWidth = 300;
color steelblue = 0xff358cab;
float yPos1 = 200; float yPos2 = 380;

void draw() {
  background(255);
  stroke(100);
  
  // create a Visuals class to help with the drawing
  // This is all just for the plotting. nothing to do with the data
  Visuals v = new Visuals(this, xLeft-5, 100, xWidth+40, 200);
  fill(255);
  rect(xLeft-10,yPos1-100,320,200); 
  rect(xLeft-10,yPos2-60,320,120); // rectangle around the autoCorr plot.
  line(xLeft-5, yPos1, xLeft+xWidth+5, yPos1);
  line(xLeft-5, yPos2, xLeft+xWidth+5, yPos2);
  v.yLabels(yPos1-100, 200, -2, 2, 5);
  v.yLabels(yPos2-45, 90, -1, 1, 5);
  v.xLabels(xLeft, xWidth,0, 99, 5);
  
  // draw the raw data
  noFill(); stroke(steelblue);
  beginShape();
  for (int i=0; i<ln; i++) {
    vertex(x[i], yPos1-50*sinx[i]);
  }
  endShape();
  
  // autoCorr
  for (int i=0; i<ln; i++) {
   // autoCorr line
    line(x[i], yPos2 - 45*autoCorr[i], x[i], yPos2);
  }
  
 
  // labels
  fill(steelblue); textFont(titleFont); textAlign(LEFT,TOP);
  text("100 random numbers with a 'hidden' sine function (wavelength every 10 units) "+
       "and an autocorrelation (correlogram) of the series on the bottom.",xLeft-30, 20, width-2*xLeft+60, 70);
  fill(100);textFont(smallFont); textAlign(LEFT,BOTTOM);
  //raw data label:
  text("The data: y = random(-1, 1) + sin(2pi*i/10)", xLeft, 95); ;
   //raw data label:
  text("Autocorrelation plot for lag = 0,1,...,99", xLeft, yPos2+75); ;
}

void keyPressed(){
  if(keyPressed){
    redraw();
  }
}

