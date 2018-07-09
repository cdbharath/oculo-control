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
 * Convenient class for drawing multiple scatter plots.
 * @author Adila Faruk
 */
public class SubPlot extends Visuals {

	/**
	 * Specifies whether to draw the bounding rectangle around the plot.
	 */
	public boolean drawRectangle = false;
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
	

	private PApplet myParent;

	
	//CENTER = 3; BOTTOM = 102; TOP = 101; BASELINE = 0; LEFT = 37; RIGHT = 39;	


	boolean dataExtremesHaveBeenSet=false;
	public float[][] xLefts; public float[][] yTops;
	public float xW; public float  yH;
	public float ySpacing = 80; 
	public float xSpacing = 100;
	public ScatterPlot[][] s;
	

	/**
	   Creates numX*numY plots, each of with xWidth, and height yHeight.
	   subplot(1,1) has its top left corner situated at xLeft, yTop.
	   subplot(1,2) has its top left corner situated at xLeft, yTop + yHeight + yInterval.
	   ...
	   ...
	   subplot(1,numY) has its top left corner situated at xLeft, yTop + (numY-1)*(yHeight+ySpacing)

	 */
	public SubPlot(PApplet theParent, float xLeft, float yTop, float xWidth, float yHeight, int numX, int numY) {
		super(theParent,xLeft,yTop,xWidth,yHeight);
		myParent = theParent;
		xLefts = new float[numX][numY]; 
		yTops = new float[numX][numY]; 
		s = new ScatterPlot[numX][numY];

		// get the x and y spacing.
		yH = (yHeight - (numY-1)*ySpacing)/numY;
		xW = (xWidth-(numX-1)*xSpacing)/numX; 

		for (int i=0; i<numX; i++) {
			for (int j=0; j<numY; j++) {
				xLefts[i][j] = xLeft + i*(xW+xSpacing);
				yTops[i][j] = yTop + j*(yH+ySpacing);
				s[i][j] = new ScatterPlot(theParent,xLefts[i][j], yTops[i][j],xW,yH);
				s[i][j].drawRectangle();
				s[i][j].drawRectangle=false;
			}
		}
	}
	
    /** Sets the data extremes for subplot(xnum,ynum) by calling {@link ScatterPlot#setDataExtremes} */
	public void setDataExtremes(float minXDat, float maxXDat, float minYDat, float maxYDat,int xnum, int ynum){
		s[xnum][ynum].setDataExtremes(minXDat,maxXDat,minYDat,maxYDat);
	}
	/** 
	 * Draws the scatterplot for subplot(xnum,ynum) by calling {@link ScatterPlot#drawScatterPlot}. For
	 * more control, use s[xnum][ynum].drawScatterPlot directly.
	 */
	public void drawScatterPlot(float[] xDat, float[] yDat, int fColor, int xnum, int ynum){
		s[xnum][ynum].drawScatterPlot(xDat,yDat,.5f,fColor);
	}
	/** 
	 * Writes the title for subplot(xnum,ynum). For more control over the placement, color, size,
	 * I highly recommend you do this yourself. :)
	 */
	public void writeTitle(PFont pFont, String title, int xnum, int ynum){
		myParent.fill(GRAY[0]);	
		s[xnum][ynum].writeLabels(pFont, title, xLefts[xnum][ynum]+5, yTops[xnum][ynum]);
	}
	/** 
	 * Writes the title for subplot(xnum,ynum). For more control over the placement, color, size,
	 * I highly recommend you do this yourself. :)
	 */
	public void writeTitle( String title, int xnum, int ynum){
		s[xnum][ynum].writeLabels(title,xLefts[xnum][ynum]+5, yTops[xnum][ynum]);
	}
	
	/**
	 * Draws a legend going downwards with the first element situated at (x,y).
	 * 100% similar to {@link Visuals#legendVert}.
     *
	 * @param colors the legend colors
	 * @param labels labels that go with each color
	 */
	public void legendVert(float x, float y, int[] colors, String[] labels){
		legendVert(x,y,colors,labels);
	}
	
	/**
	 * Draws a legend going to the right with the first element situated at (x,y). 
	 * Useful for situations where you are plotting more than one dataset.
	 * @param where "top" if you want to place the legend at the top, otherwise, it 
	 * will default to placing it at the bottom of the plots.
	 * @param colors the legend colors
	 * @param labels labels that go with each color
	 */
	public void legendHoriz(String where, int[] colors, String[] labels){
		float yPos;
		if(where.equals("top")){
			yPos = plotTop-40;
		}
		else{
			yPos = super.plotTop+super.plotHeight+40;
		}
		legendHoriz(xLefts[0][0]-s[0][0].leftBound, yPos,colors,labels);
	}

	/** 
	 * Writes the y labels for subplot(xnum,ynum). For more control, call the
	 * underlying scatterplot directly using s[xnum][ynum].yLabels.
	 */
	public void yLabels(int _numDiv, int xnum, int ynum){
		s[xnum][ynum].yLabels(yTops[xnum][ynum], yH, s[xnum][ynum].minYDat, s[xnum][ynum].maxYDat,  _numDiv);
	}
	
	/** 
	 * Writes the x labels for subplot(xnum,ynum). For more control, call the
	 * underlying scatterplot directly using s[xnum][ynum].xLabels.
	 */
	public void xLabels(int _numDiv, int xnum, int ynum){
		s[xnum][ynum].xLabels(xLefts[xnum][ynum], xW, s[xnum][ynum].minXDat, s[xnum][ynum].maxXDat,  _numDiv);
	}
	
}

