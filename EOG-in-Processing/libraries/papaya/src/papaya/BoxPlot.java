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
 *
 */
package papaya;

import java.util.Arrays;
import java.io.*;
import java.lang.Object;
import java.lang.String;

import processing.core.*;
import processing.core.PApplet;
import processing.core.PFont;

/**
 * BoxPlot class
 * 
 */

public class BoxPlot extends Visuals{
	
	private float[][] fiveNumArr;
	private int numDataSets;
	private float minVal,maxVal;
	private PApplet myParent;
	

	/**
	 * Specifies whether to draw the bounding rectangle around the plot.
	 */
	public boolean drawRectangle = true;
	/**
	 * Specifies whether to draw the axes
	 */
	public boolean drawAxes = true;	
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

	public BoxPlot(PApplet _theParent, float _plotLeft, float _plotTop, float _plotWidth, float _plotHeight){
		super(_theParent,_plotLeft,_plotTop,_plotWidth,_plotHeight);	
		myParent = _theParent;
		//bgColor = super.bgColor;
	}
	/**
	 * Sets the data to be plotted. 
	 * @param _fiveNumArr array of five number box plot summaries
	 * with each row corresponding to a given data set. See {@link Descriptive#tukeyFiveNum} for more info.
	 * 
	 * @param _minVal the minimum value to show in the plot
	 * @param _maxVal the maximum value to show in the plot
	 */
	public void setData(float[][] _fiveNumArr, float _minVal, float _maxVal){
		fiveNumArr = _fiveNumArr;		
		numDataSets = fiveNumArr.length;
		minVal = _minVal; 
		maxVal = _maxVal;
	}
	/**
	 * 
	 * @return the tukey five number array
	 */
	public float[][] getData(){
		return fiveNumArr;
	}
	
	/**
	 * Plot the box plots.
	 * @param _labels labels for the box plot
	 * @param _sWeight the stroke weight to use for the lines.
	 * @param _fColor the fill color for the boxplots
	 */
	public void drawBoxPlot(String[] _labels, float _sWeight, int _fColor){		
		
		if(drawRectangle){
			drawRect(plotLeft-leftBound,plotTop-topBound,plotWidth+leftBound+rightBound,plotHeight+topBound+bottomBound,bgColor);
		}	
		
		// Draw the box plots.
		float wEach = plotWidth/(numDataSets+1);
		float wBox = wEach/4;
		float xPos;
		if(drawAxes){
			// map the minimum and maximum values
			float[] minmax = Mat.map(new float[]{minVal,maxVal},minVal,maxVal,plotTop+plotHeight,plotTop);
			vertLine(minmax[0],minmax[1], plotLeft, 50,.5f); 
			horizLine(plotLeft + .5f*wEach, plotLeft + (numDataSets+.5f)*wEach, plotTop + plotHeight, 50,.5f);
			xLabels(plotLeft + wEach, numDataSets*wEach, _labels);
			xTicks(plotLeft + wEach, numDataSets*wEach, numDataSets,5);
			yLabels(plotTop, plotHeight, minVal,maxVal,5);
			yTicks(plotTop, plotHeight, 5 ,10);
		}
		
		for(int i=0; i<numDataSets; i++){
			xPos = plotLeft + (i+1)*wEach;
			drawEachBoxPlot(fiveNumArr[i], xPos,wBox,_sWeight, _fColor );
		}
		
	}
	
	// draws a single box plot.
	
	private void drawEachBoxPlot(float[] _fiveNum, float _xPos, float _wBox,float _sWeight, int _fColor){
		float[] mapped = Mat.map(_fiveNum,minVal,maxVal, plotTop + plotHeight, plotTop );
		myParent.pushStyle();
		
		myParent.strokeWeight(_sWeight); 
		myParent.stroke(GRAY[1]);
		myParent.fill(_fColor);
		myParent.line(_xPos, mapped[4],_xPos,mapped[3]);
		myParent.line(_xPos, mapped[0],_xPos,mapped[1]);
		myParent.line(_xPos-_wBox, mapped[0], _xPos + _wBox, mapped[0]);
		myParent.line(_xPos-_wBox, mapped[4], _xPos + _wBox, mapped[4]);
		myParent.rectMode(0);
		myParent.noStroke();
		myParent.rect(_xPos - _wBox, mapped[1], 2*_wBox, (mapped[3]-mapped[1]) );
		myParent.strokeWeight(3); myParent.stroke(50);
		myParent.line(_xPos-_wBox, mapped[2], _xPos + _wBox, mapped[2]);
		
		myParent.popStyle();
	}

}