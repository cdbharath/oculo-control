/*Simple example showing the ScatterPlot functions
 */
// import the library
import papaya.*;

// define a "Stats" variable


// create a few different types of data points
int ln = 100;
float[] yDat = new float[ln]; 
float[] xDat = new float[ln];

// font
PFont font = createFont("Helvetica", 12, true);

void setup() {
  size(500, 200);
  smooth();
  noLoop();
}

// plot parameters
float plotLeft = 60, plotTop = 90;
float plotWidth = 400;
color meanColor = color(100, 100, 200);
color medianColor = color(200, 100, 100);

void draw() {
  background(color(255));
  textFont(font, 12); 
  fill(50); 
  smooth();
  textAlign(LEFT, TOP);
  text("Randomly Generated Data. Press any key to Regenerate.", 20, 10);

  for (int i=0; i<ln; i++) {
    xDat[i] = i+1; 
    yDat[i] =  i+1 + random(-50, 50); // yData is unmapped
  }

  // define the scatter plot dimensions
  ScatterPlot scatter = new ScatterPlot(this, plotLeft, plotTop, plotWidth, 50f);
  // some setup stuff before we do the drawing. 
  // you can also set these to false now, and then
  // call the drawRectangle and drawAxes functions directly
  // after you're done plotting the data if you want
  // more control over the parameters.
  scatter.drawRectangle=true; // draw the bounding rectangle
  scatter.drawAxes=true; // draw the axes
  
  scatter.setDataExtremes(-10f, ln+10f, -100f, ln+50f);
  scatter.asConnectedLines = true; 
  scatter.asEllipses = false;
  scatter.drawScatterPlot(xDat, yDat, 1, color(200, 100, 0, 100));
  textFont(font,12);
  scatter.writeTitle("y = x + random(-50,50)",font);

 // x and y labels. 
 // The first two variables (left,width), (top,height)
 // are set to negative values to invoke the default  
 // (plotLeft,plotWidth), (plotTop,plotHeight) 
  scatter.xLabels(-1f, -1f, -10f, ln+10f, 5);
  scatter.yLabels(-1f, -1f, -100f, ln+50f, 5);
}

void keyPressed() {
  if (keyPressed) {
    // don't redraw when the Apple/Command key is pressed though.
    if (keyCode!=157) {   
      redraw();
    }
  }
}

