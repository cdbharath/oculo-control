/**
 * Plots <code>x</code> vs <code>y</code> data and the best-fit linear
 * line for that data.
 * @author Adila Faruk
 */
public class LinearModelPlot extends Visuals {
  private PApplet myParent;
  boolean drawRectangle = true, drawAxes = true;

  public float leftBound = 40, rightBound=20; 
  public float topBound = 20, bottomBound=20;

  float minX, minY, maxX, maxY; // plot min/max
  float minXDat, minYDat, maxXDat, maxYDat; // data min/max
  boolean dataExtremesHaveBeenSet = false;
  
  // best fit line
  int sColor  = color(200, 100, 100); // stroke color for the best fit line
  float sWeight = 1; // stroke weight for the best fit line
  boolean writeLineEquation = true; 
    	
  /**Constructor */
  public LinearModelPlot(PApplet _theParent, float _plotLeft, float _plotTop, float _plotWidth, float _plotHeight) {
    super(_theParent, _plotLeft, _plotTop, _plotWidth, _plotHeight);	
    myParent = _theParent;
    bgColor = super.bgColor;
  }

  /* Set the minimum/maximum values on the x and y axis to nice numbers. 
  E.g. 60 instead of 63.183628, 90 instead of 87.1, etc. */ 
  public void setDataExtremes(float _minXDat, float _maxXDat, float _minYDat, float _maxYDat) {
    minXDat = _minXDat; 
    maxXDat = _maxXDat; 
    minYDat = _minYDat; 
    maxYDat = _maxYDat;
    dataExtremesHaveBeenSet = true;
  } 

  /*  Plots the scatter plot of the data. 
   x,y the x-y dataset of interest.
   _fColor the color of the ellipses corresponding to each x-y pair 
  */
  public void drawXY( float[] x, float[] y, int _fColor) {		
    if (drawRectangle) {
      drawRect(plotLeft-leftBound, plotTop-topBound, plotWidth+leftBound+rightBound, plotHeight+topBound+bottomBound, bgColor);
    }
    if (drawAxes) {
      drawAxes(50, .5f);
    }
    // Check if the data extemes have been set.
    if (!dataExtremesHaveBeenSet) {
      throw new IllegalStateException("Set the data extremes first.");
    }

    // map the data.
    float[] xDatMapped = Mat.map(x, minXDat, maxXDat, plotLeft, plotLeft+plotWidth);
    float[] yDatMapped = Mat.map(y, minYDat, maxYDat, plotTop+plotHeight, plotTop); 
    minX = Descriptive.min(xDatMapped); 
    maxX = Descriptive.max(xDatMapped);
    minY = Descriptive.min(yDatMapped); 
    maxY = Descriptive.max(yDatMapped);
    myParent.fill(_fColor);
    myParent.noStroke(); 
    myParent.smooth();
    for (int i=0; i<xDatMapped.length; i++) {
      myParent.ellipse(xDatMapped[i], yDatMapped[i], 4, 4);
    }
  }

        /*
       	 * function that plots the best fit linear line going from <code>x1,y1</code> to <code>x2,y2</code>. 
       	 * 
       	 * The multiplier <code>c</code> ( 0 <= c<= 0.5 ) is chosen such that
       	 * the points (x1,y1) and (x2,y2) are in the rectangle with corners
       	 * 
       	 * (meanX-c*rangeX/2, meanY+c*rangeY/2) 	(meanX+c*rangeX/2, meanY+c*rangeY/2)
       	 *
       	 * (meanX-c*rangeX/2, meanY-c*rangeY/2)		(meanX+c*rangeX/2, meanY-c*rangeY/2)
       	 * 
       	 * where 
         * rangeX = _maxX - _minX
         * and
         * rangeY = _maxY - _minY.
       	 * This is done in the most inefficient way possible in the universe... :)
       	 * @param coeff, the slope and intercept where coeff[0] = slope, coeff[1] = intercept
         * @param meanVals = float[]{mean(x),mean(y)}}
       	 * @param multiplier corresponds to <code>c</code> above and is the multiplier to use when plotting 
       	 * the line. A smaller c value results in a longer line. The default it set to .15, and will be used if a 
       	 * value of c<= 0  or c >=.50 is entered.
       	 */
  public void drawBestFitLine(float[] coeff, float[] meanVals, float multiplier) {
    // Check if the data extemes have been set.
    if (!dataExtremesHaveBeenSet) {
      throw new IllegalStateException("Set the data extremes first.");
    }
    
    float meanX = meanVals[0]; 
    float meanY = meanVals[1];
    float x1, y1, x2, y2; 
    float slope = coeff[0]; 
    float intercept = coeff[1];
    float mult;		
    if (multiplier<0 || multiplier>=.5) {
      mult = .15f;
    }
    else mult = 1-multiplier;

    x1 = (meanX+mult*(minXDat-meanX)); 
    y1= slope*(x1)+intercept;
    x2 = (meanX+mult*(maxXDat-meanX)); 
    y2= slope*(x2)+intercept;
    if (slope!=0) {
      // if the intercept b is less than the minimum y value
      // update the left-most point
      if (y1<meanY+mult*(minYDat-meanY)) {
        y1 = meanY+mult*(minYDat-meanY);
        x1 = (y1-intercept)/slope;
      }
      // Rightmost point: 
      // if the y point exceeds the maximum value, 
      // update the x and y coordinates
      if (y2>meanY+mult*(maxYDat-meanY)) {
        y2 = meanY+mult*(maxYDat-meanY);
        x2 = (y2-intercept)/slope;
      }
    }

    // map to the plot parameters
    float[]  xFit = Mat.map(new float[] {x1, x2}, minXDat, maxXDat, minX, maxX);
    float[]  yFit = Mat.map(new float[] {y1, y2}, minYDat, maxYDat, maxY, minY);
    // draw the line		
    line(xFit[0], yFit[0], xFit[1], yFit[1], sColor, sWeight);

    // draw an ellipse at the mean value.
    meanX = Mat.map(meanX, minXDat, maxXDat, minX, maxX);
    meanY = Mat.map(meanY, minYDat, maxYDat, maxY, minY);
    myParent.fill(sColor);
    myParent.noStroke();
    myParent.ellipse(meanX, meanY, 6, 6);

    if (writeLineEquation) {
      if (yFit[1]<yFit[0]) myParent.textAlign(CENTER, BOTTOM);
      else myParent.textAlign(CENTER, TOP);
      String label = "y = "+myParent.nfc(slope, 2)+"x + ( "+myParent.nfc(intercept, 2)+" )";
      writeLabels(label, xFit[1], yFit[1]);
    }
  }
}

