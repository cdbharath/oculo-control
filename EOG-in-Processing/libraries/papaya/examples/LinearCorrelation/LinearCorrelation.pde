import papaya.*;

// Simple linear correlation example studying the fascinating relationship
// between tree volume and tree height

// --------------------- Sketch-wide variables ----------------------
PFont titleFont, smallFont;


float r; // correlation coefficient
float pValCorr; // correlation coefficient p-value
// Margin of error for the regression line
float slope, intercept;
String summary;

float[] treeGirth, treeHt, treeVol;
int minHt,maxHt,minVol,maxVol;

// ------------------------ Initialisation --------------------------

// Initialise the data
void setup()
{
  size(800, 400);
  smooth();
  noLoop();

  titleFont = createFont("Helvetica", 22, true);
  smallFont = createFont("Helvetica", 11, true);
  textFont(smallFont);

  //Read in the data
  //  A data frame with 31 observations on 3 variables.
  // Girth; Tree diameter in inches
  // Height: Tree height in ft.
  // Column: Volume of timber in cubic ft.
  String lines[] = loadStrings("Trees.txt");

  int ln = lines.length-1; 
  treeGirth = new float[ln];
  treeHt = new float[ln]; 
  treeVol = new float[ln];
  for (int i=0; i<ln; i++) {
    String[] pieces = splitTokens(lines[i+1], " ") ;
    treeGirth[i] =Float.parseFloat( pieces[1]);
    treeHt[i] = Float.parseFloat( pieces[2] );
    treeVol[i] = Float.parseFloat( pieces[3] );
  }
 
  // construct the linear model.
  float[] coeff = Linear.bestFit(treeHt,treeVol);
  slope = coeff[0]; intercept = coeff[1];
  r = Correlation.linear(treeHt,treeVol,false); // linear correlation coefficient
  pValCorr = Correlation.Significance.linear(r,ln); // the p-value of r

  // std error of the residual, slope and intercept.
  float[] residuals = Linear.residuals(treeHt,treeVol,slope,intercept);
  // df = n-2 since neither slope nor intercept was specified.
  float residualErr = Linear.StdErr.residual(residuals,ln-2); 
  float slopeErr = Linear.StdErr.slope(treeHt,residualErr);
  float interceptErr = Linear.StdErr.intercept(treeHt,residualErr);
 
 
 // create a string containing the relevant statistics.
 summary = "Correlation Coefficient, r = "+nfc(r, 2)+"\np-value: "+nfc(pValCorr,4);
 summary = summary +",\nStandard Error in the residual: "+nfc(residualErr,1) +       
           "\nSlope: +/-"+nfc(slopeErr,3) +", Intercept: +/-"+nfc(interceptErr,3); 
}

// ------------------ Processing draw --------------------

// Draws the graph in the sketch.
void draw()
{
  background(255);  
  
  // draw the scatter plot of the data --------------------------
  // (1): Setup your plot area. 
  LinearModelPlot lmPlot = new LinearModelPlot(this,60f,60f,width-120f,height-120f);
  // (1): Once you've done this, you can set the plot parameters
  // such as whether you want to draw the bounding rectangle,
  // the background color, whether you want to draw the axes, etc.
  // All variables have been left as "public" so you can edit them 
  // directly. Not exactly the best programming practice, but I figured
  // better you behind the wheel rather than me. :)
  
  // (2): Set the minimum/maximum values on the x and y axis to nice numbers.
  // e.g. 60 instead of 63, 90 instead of 87, etc. 
  minHt = floor(min(treeHt)/10)*10; maxHt = ceil(max(treeHt)/10)*10;
  minVol = floor(min(treeVol)/10-1)*10; maxVol = ceil(max(treeVol)/10+1)*10;
  lmPlot.setDataExtremes(minHt,maxHt,minVol,maxVol);
  lmPlot.bgColor=255;
  // Draw the raw data
  lmPlot.drawXY(treeHt,treeVol,color(50,80,200,150));
  // Draw the best fit linear line.
  float meanHt = Descriptive.mean(treeHt), meanVol = Descriptive.mean(treeVol);
  lmPlot.drawBestFitLine(new float[]{slope,intercept}, new float[]{meanHt,meanVol},.15);
  
  // write the x and y labels. Setting the first two elements to <0 
  // makes it default to writing the labels from the 
  // plot left to the plot right, and plot top to the bottom.
  lmPlot.xLabels(-1f, -1f, minHt, maxHt, 6);
  lmPlot.yLabels(-1f, -1f, minVol, maxVol, 5);

  // labeling
  textAlign(LEFT,TOP);
  fill(120);
  textFont(titleFont);
  text("Tree Volume (cubic ft) vs. Tree Height (ft)", 70, 50);
  float textHeight = 1.4*textAscent(); 
  fill(200, 100, 100);
  textFont(smallFont);
  text(summary, 72, 50+textHeight);
}

void mousePressed() {
  if (mousePressed) {
    println(mouseX+","+mouseY);
  }
}

