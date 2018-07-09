/**
 * papaya: A collection of utilities for Statistics and Matrix-related manipulations
 * http://adilapapaya.com/papayastatistics/, 1.1.0
 * Created by Adila Faruk, http://adilapapaya.com, May 2012, Last Updated April 2014
 *
 *
 * Copyright (C) 2014 Adila Faruk http://adilapapaya.com 
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 */
package papaya;
import java.util.Arrays;
import java.io.*;
import java.lang.Object;
import java.lang.String;

import processing.core.*;

/**
 * A simple class to plot x vs y data as a 
 * scatter plot. Default is to draw the points as ellipses,
 * but you can also use rectangles, and connect the points with
 * lines.
 * @author Adila Faruk
 */
public class ScatterPlot extends Visuals{
	public boolean asConnectedLines = false;
	public boolean asRectangles = false;
	public boolean asEllipses = true;
	public boolean[] XYLabels = {true,true}; 
	public boolean[] XYTicks = {true,true};
	
	/**
	 * Specifies whether to draw the bounding rectangle around the plot.
	 */
	public boolean drawRectangle = true;
	/**
	 * Specifies whether to draw the axes
	 */
	public boolean drawAxes = true;
	
	protected PApplet myParent;
	
	/**
	 * Specifies the space on the left between the plot area, and the
	 * bounding rectangle. That is, the left-most point of 
	 * the rectangle is given by plotLeft-leftBound. 
	 */
	public float leftBound = 40;
	/**
	 * Specifies the space on the right betweent the plot area, and the
	 * bounding rectangle. That is, the right-most point of 
	 * the rectangle is given by plotLeft+plotWidth+rightBound. 
	 */
	public float rightBound=20; 
	/**
	 * Specifies the space on the top, between the plot area and the bounding
	 * rectangle. That is, the top-most point of the rectangle is given by
	 * plotTop - topBound.
	 */
	public float topBound = 20;
	/**
	 * Specifies the space on the bottom, between the plot area and the bounding
	 * rectangle. That is, the bottom-most point of the rectangle is given by
	 * plotTop + plotHeight + bottomBound.
	 */
	public float bottomBound=20;
	//CENTER = 3; BOTTOM = 102; TOP = 101; BASELINE = 0; LEFT = 37; RIGHT = 39;	
	
	protected float minX;
	protected float minY;
	protected float maxX;
	protected float maxY; // plot min/max
	protected float minXDat;
	protected float minYDat;
	protected float maxXDat;
	protected float maxYDat; // data min/max
	boolean dataExtremesHaveBeenSet=false;
	
	public ScatterPlot(PApplet _theParent, float _plotLeft, float _plotTop, float _plotWidth, float _plotHeight){
		super(_theParent,_plotLeft,_plotTop,_plotWidth,_plotHeight);	
		myParent = _theParent;
		bgColor = super.bgColor;
	}
		
	/** Set the minimum/maximum values on the x and y axis to nice numbers.
	 * e.g. 60 instead of 63.183628, 90 instead of 87.1, etc. 
	 * It'll try do it automatically for you, if you don't set it here, 
	 * but no guarantees on the result being a nice number. For example, it might
	 * go from 61 to 89 instead of 60 to 90. 
	 * @param _minXDat the minimum x value to use. Set to more than the x data minimum.
	 * @param _maxXDat the maximum x value to use. Set to more than the x data maximum.
	 * @param _minYDat the minimum y value to use. Set to more than the y data minimum.
	 * @param _maxYDat the maximum y value to use. Set to more than the y data maximum.
	 */
	public void setDataExtremes(float _minXDat, float _maxXDat, float _minYDat, float _maxYDat){
		minXDat = _minXDat; maxXDat = _maxXDat; 
		minYDat = _minYDat; maxYDat = _maxYDat;
		dataExtremesHaveBeenSet = true;
	} 
	
	/**
	 * Draws a rectangle around the plot.
	 * <ul>
	 * <li>Left most point: plotLeft-leftBound</li>
	 * <li>Top most point: plotTop-topBound</li>
	 * <li>Width: plotWidth+leftBound+rightBound</li>
	 * <li>Height: plotHeight+topBound+bottomBound</li>
	 * </ul>
	 */
	public void drawRectangle(){
		drawRect(plotLeft-leftBound,plotTop-topBound,plotWidth+leftBound+rightBound,plotHeight+topBound+bottomBound,bgColor);
	}
	
	/**
	 * Parent function to plot scatter plot of the data.
	 * @param _xDat x Data array
	 * @param _yDat y Data array
	 * @param _sWeight stroke weight
	 * @param _sColor stroke color
	 */
	public void drawScatterPlot(float[] _xDat, float[] _yDat, float _sWeight, int _sColor){
		if(drawRectangle){
			drawRect(plotLeft-leftBound,plotTop-topBound,plotWidth+leftBound+rightBound,plotHeight+topBound+bottomBound,bgColor);
		}			
		if(drawAxes){
			drawAxes(50,.5f); 
		}
		
		// Check if the data extemes have been set.
		if(!dataExtremesHaveBeenSet){
			throw new IllegalStateException("Set the data extremes first.");
		}
		// map the data.
		float[] xDatMapped = Mat.map(_xDat,minXDat,maxXDat,plotLeft,plotLeft+plotWidth);
		float[] yDatMapped = Mat.map(_yDat,minYDat,maxYDat,plotTop+plotHeight,plotTop); 

				
		if(asConnectedLines){
			drawConnectedWithLines(xDatMapped,yDatMapped,_sWeight,_sColor);
		}
		if(asRectangles){
			drawAsRectangles(xDatMapped,yDatMapped,_sColor,10,6);
		}
		if(asEllipses){
			drawAsEllipses(xDatMapped,yDatMapped,_sColor,6);
		}
	}
	
	protected void drawScatterPlot(float[] _xDat, float[] _yDat){		
		drawScatterPlot(_xDat,_yDat,STROKEWEIGHT[0],GRAY[0]);
	}
	
	/**
	 * connects the dots
	 */
	protected void drawConnectedWithLines(float[] xDatMapped, float[] yDatMapped, float _sWeight, int _sColor){
		
		myParent.noFill();
		myParent.stroke(_sColor);
		myParent.strokeWeight(_sWeight);
		
		myParent.beginShape();
		for(int i=0; i<xDatMapped.length; i++){
			myParent.vertex(xDatMapped[i],yDatMapped[i]);
		}
		myParent.endShape();
	}
	
	/**
	 * draws the points as dots
	 */
	protected void drawAsEllipses(float[] xDatMapped, float[] yDatMapped, int _fillColor, float _diameter){
		
		myParent.fill(_fillColor);
		myParent.noStroke();
		for(int i=0; i<xDatMapped.length; i++){
			myParent.ellipse(xDatMapped[i],yDatMapped[i],_diameter,_diameter);
		}
	}
	
	/**
	 * draws the points as rectangles
	 */
	protected void drawAsRectangles(float[] xDatMapped, float[] yDatMapped, int _fillColor, int _rectWidth, int _rectHeight){
		
		myParent.fill(_fillColor);
		for(int i=0; i<xDatMapped.length; i++){
			myParent.rect(xDatMapped[i]-_rectWidth/2,yDatMapped[i]-_rectHeight/2,_rectWidth,_rectHeight);
		}
	}
}