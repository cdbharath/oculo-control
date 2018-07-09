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
 * Takes in a matrix and plots the data in each of the columns versus
 * each other. Each of the columns are normalized to their minimum and maximum values which
 * can be a little misleading. For example, say you are looking at columns specifying the weight
 * of different panda bears over 100 days. 
 * Panda bear A (Ling-Ling)'s weight varies from 650 to 970 pounds 
 * (she's an emotional eater), while panda bear B (Thomas)'s weight varies from 512 to 516 pounds 
 * (he's not a big fan of bamboo; also kind of metrosexual). The correlation plot will <b>not</b> show you 
 * this difference in magnitudes or ranges! 
 * There are many ways of addressing that, and all of them involve you writing your
 * own code. :) 
 * <p>If you absolutely insist on using this despite the warning above, then the (somewhat messy) hack
 * around it is to pad each column of your data with 2 additional rows containing the
 * minimum and maximum that you want to abide by. Just make sure it doesn't muck everything
 * else up though!
 */
public class CorrelationPlot extends Visuals{

	
	protected PApplet myParent;
	
	/**width and height of the individual plots */
	 private float w;
	 private float h;
	 private int numColumns;
	 
	//CENTER = 3; BOTTOM = 102; TOP = 101; BASELINE = 0; LEFT = 37; RIGHT = 39;	
	
	/** Setup the plot dimensions */
	public CorrelationPlot(PApplet _theParent, float _plotLeft, float _plotTop, float _plotWidth, float _plotHeight){
		super(_theParent,_plotLeft,_plotTop,_plotWidth,_plotHeight);	
		myParent = _theParent;
		bgColor = super.bgColor;
	}
	
	/**
	 * Plots the scatter plot of each of the columns of the input data matrix vs. the 
	 * other columns. Each of the columns are normalized to their minimum and maximum values which
	 * can be a little misleading. For example, say you are looking at columns specifying the weight
	 * of different panda bears over 100 days. 
	 * Panda bear A (Ling-Ling)'s weight varies from 450 to 670 pounds 
	 * (she's an emotional eater), while panda bear B (Thomas)'s weight varies from 512 to 516 pounds 
	 * (he's not a big fan of bamboo). The correlation plot will <b>not</b> show you this difference
	 * since both could follow the same sort of distribution. 
     *
	 * There are many ways of addressing that, and all of them involve you writing your
	 * own code. :) 
	 * <p>If you absolutely insist on using this despite the warning above, then the (very messy) hack
	 * around it is to pad each column of your data with 2 additional rows containing the
	 * minimum and maximum that you want to abide by. Just make sure it doesn't muck everything
	 * else up though!
	 * 
	 * @param data the data array with each column corresponding to a dataset
	 * @param pos 0 for top right plots, 1 for bottom left plots
	 * @param fColor the fill color for the dots on the plot.
	 */
	public void drawPlot(float[][] data, int pos, int fColor){
		if(pos!=0 && pos!=1) 
			throw new IllegalArgumentException("Set pos to 0 for top right plots or 1 for bottom left plots.");
		if(numColumns!=data[0].length) 
			throw new IllegalArgumentException("Number of categories doesn't match the number of columns in the matrix.");
		
		w = plotWidth/numColumns;
		h = plotHeight/numColumns;
		
		float x,y;
		
		for(int r=0; r<numColumns; r++){
			// get the data.
			float[] yDat = Mat.column(data,r);
			float minYDat = .9f*Descriptive.min(yDat);
			float maxYDat = 1.1f*Descriptive.max(yDat);
			
			for(int c = r+1; c<numColumns; c++){
				float[] xDat = Mat.column(data,c);
				float minXDat = .9f*Descriptive.min(xDat);
				float maxXDat = 1.1f*Descriptive.max(xDat);
				// Top Right
				if(pos==0){
					y = plotTop + r*h; x = plotLeft + c*w;
					float[] xDatMapped = Mat.map(xDat,minXDat,maxXDat,x,x+w);
					float[] yDatMapped = Mat.map(yDat,minYDat,maxYDat,y+h,y); 
					drawEllipses(xDatMapped,yDatMapped,fColor);
				}
				// BottomLeft
				else {
					x=plotLeft + r*w; y = plotTop + c*h;
					float[] xDatMapped = Mat.map(yDat,minYDat,maxYDat,x,x+w);
					float[] yDatMapped = Mat.map(xDat,minXDat,maxXDat,y+h,y); 
					drawEllipses(xDatMapped,yDatMapped,fColor);
				}
			}
		}
	}
	
	/**
	 * Draw the bordering lines and write the labels.
	 * @param categories the labels along the diagonal
	 * @param bgColor background rectangle color
	 */
	public void drawBorderAndLabels(String[] categories, int bgColor){	
		numColumns = categories.length;
		w = plotWidth/numColumns;
		h=plotHeight/numColumns;
		
		myParent.fill(bgColor);
		drawRect();
		myParent.strokeWeight(1); myParent.smooth(); myParent.noFill();
		for(int i=0; i<=categories.length; i++){
			myParent.line(plotLeft, plotTop + i*h, plotLeft + plotWidth, plotTop + i*h);
			myParent.line(plotLeft+i*w, plotTop, plotLeft+i*w, plotTop + plotHeight);
		}
		myParent.fill(50); myParent.textAlign(CENTER,CENTER);
		myParent.textSize(Math.round(w/4));
		for(int i=0; i<categories.length; i++){
			myParent.text(categories[i], plotTop + (i+.5f)*h, plotLeft + (i+.5f)*w);
		}
	}
		
	/** Draw the ellipses */
	protected void drawEllipses(float[] xDatMapped, float[] yDatMapped, int fillColor){
		
		myParent.fill(fillColor);
		myParent.noStroke();
		for(int i=0; i<xDatMapped.length; i++){
			myParent.ellipse(xDatMapped[i],yDatMapped[i],2,2);
		}
	}	

}