/*
 Sketch demonstrating how to plot box plots using the BoxPlot class.
 Author: Adila Faruk
 */

// import the library
import papaya.*;
//import papaya.BoxPlot;
// --------------------- Sketch-wide variables ----------------------
BoxPlot[] boxPlot = new BoxPlot[2];
PFont titleFont, mediumFont, smallFont;

float[] breaks; 
int[] tension;
int minBreaks, maxBreaks;
float[][] fiveNumSummary;

// boxplot labels
String[] xLabels = new String[] {
  "L","M","H"
};


// -----------------------------------
void setup() {
  size(630, 500);
  smooth();
  noLoop();

  titleFont = createFont("Helvetica", 22, true);
  smallFont = createFont("Helvetica", 11, true);
  mediumFont = createFont("Helvetica", 14, true);
  // Read in the data
  // This data set gives the number of warp breaks per loom, 
  // where a loom corresponds to a fixed length of yarn.
  // breaks: number of breaks in wool
  // tension: tension (L,M,H).
  String lines[] = loadStrings("warpbreaks.txt");

  int ln = lines.length-1;
  breaks = new float[ln];
  tension = new int[ln]; 

  for (int i=0; i<ln; i++) {
    String[] pieces = splitTokens(lines[i+1], "\t") ;
    breaks[i] =Float.parseFloat( pieces[1]);
    if (pieces[3].equals("L")) {
      tension[i] = 0;
    }
    else if (pieces[3].equals("M")) {
      tension[i] = 1;
    }
    else if (pieces[3].equals("H")) {
      tension[i] = 2;
    }
  }
  
  // get the min and max number of breaks, rounded to the nearest 10
  minBreaks = floor( min(breaks)/10)*10-5; maxBreaks = ceil( max(breaks)/10)*10+5;
  // get the five number summaries for each data set.
  // We have 3 data sets for A, and 3 for B
  float[][] woolA = new float[3][5];
  float[][] woolB = new float[3][5];
  for (int i=0; i<3; i++) {
    // Get the five number summary for each data set
    woolA[i] = Descriptive.tukeyFiveNum(subset(breaks, 9*i, 9));
    woolB[i] = Descriptive.tukeyFiveNum(subset(breaks, 9*(i+3), 9));
  }
  // setup the box plot
  for(int i=0; i<2; i++){  
    // create the boxPlot  
    boxPlot[i] = new BoxPlot(this, plotLeft + 1.5*i*plotWidth, plotTop, plotWidth, plotHeight);
    // adjust the boundaries a little
    boxPlot[i].rightBound = 10; boxPlot[i].leftBound = 35;
  }
  boxPlot[0].setData(woolA, minBreaks, maxBreaks);
  boxPlot[1].setData(woolB, minBreaks, maxBreaks);
}

// -----------------------
// drawing
// plot parameters
float plotLeft = 70, plotTop = 120;
float plotWidth = 200, plotHeight = 300;
color boxColor = color(255);//color(40, 100, 200, 100);
String[] AB = {"A", "B"};

void draw() {
  background(color(255));
  // draw the box plots
  for(int i=0; i<2; i++){
    boxPlot[i].drawBoxPlot(xLabels,2,boxColor);
    fill(100); textAlign(CENTER,TOP);
    textFont(smallFont,13);
    text("Wool "+AB[i], plotLeft + (.5+1.5*i)*plotWidth, plotTop + plotHeight + boxPlot[i].bottomBound + 10);
  }
  
  // labeling ------------------------------------------------
  textAlign(LEFT, BOTTOM);
  fill(120);
  textFont(titleFont);
  text("Number of Warp Breaks per Loom of Wool", 40, 50);
  float textHeight = 1.4*textAscent(); 
  //fill(40, 100,150);
  textFont(smallFont, 13);
  textAlign(LEFT, TOP);
  text("Wool Types 'A' and 'B', under Low (L), Medium (M), and High (H) Tension", 40, 52);
 
}
