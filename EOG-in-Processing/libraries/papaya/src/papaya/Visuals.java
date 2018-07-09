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
 * Visuals is the parent class behind most of the other plotting classes.
 * 
 * <p>
 * It:
 * </br>
 * 1. Contains some useful functions for the visual representation
 *    of your data (e.g. setting up your font, writing labels, mapping 
 *    arrays to the plot right-left-top-bottom parameters, xTicks, yTicks,
 *    etc.) that should help you reduce the amount of code you end up
 *    writing. 
 *</br>    
 * 2. Is extended by the available "Plotting" classes used to quickly give a 
 *    visual snapshot of your data.  
 * </p>   
 * 
 */

public class Visuals implements PapayaConstants{

	/*
	 * Processing applet
	 */
	protected PApplet myParent;
	
	PFont plotFont = new PFont();


	// protected variables -------------------------------------------
	// can be set only using get/set methods
	// or in the constructor

	protected float plotLeft=10;
	protected float plotWidth=180;

	protected float plotTop=10;
	protected float plotHeight=180;

	/**
	 * Specifies the plot background color
	 */
	public int bgColor = GRAY[4];

	//CENTER = 3; BOTTOM = 102; TOP = 101; BASELINE = 0; LEFT = 37; RIGHT = 39;

	 /** The parent class behind the other plotting classes.
	 * 
	 * <p>
	 * Visuals contains some useful functions for the (elementary!) visual representation
	 * of your data (e.g. setting up your font, writing labels, mapping 
	 * arrays to the plot right-left-top-bottom parameters, xTicks, yTicks,legends,
	 * etc). Nothing very fancy like writing x-labels at an angle or computing your
	 * tax returns for you though so don't get your hopes up. Sorry. 
	 * 
	 * 
	 * @param _theParent the Processing papplet. Just "this" in your sketch.
	 * @param _plotLeft the plot left
	 * @param _plotTop the plot top (in Processing coordinates!)
	 * @param _plotWidth the plot width
	 * @param _plotHeight the plot height.
	 * <p>The bottom of the plot area is then given by <code>_plotTop + _plotHeight</code>
	 * while the right is <code>_plotLeft + _plotWidth</code>.</p>
	 */
	public Visuals(PApplet _theParent, float _plotLeft, float _plotTop, float _plotWidth, float _plotHeight){
		myParent = _theParent;
		plotLeft = _plotLeft;
		plotWidth = _plotWidth;		
		plotTop = _plotTop;
		plotHeight = _plotHeight;	
		plotFont = myParent.createFont(FONTNAME,12f,true);
	}
    
	/**
	 * Maps the x-data from the range min(data) to max(data) to 
	 * the plotLeft and plotLeft+plotWidth. That is, 
	 * <code>mappedData[i] = plotLeft + (data[i] - min) / (max-min) * plotWidth</code>.
	 * This is not done directly though, but rather, the 
	 * {@link Mat#map(float[] data, float low1, float high1, float low2, float high2)} 
	 * function is used.
	 * @param data the x data to map.
	 * @return the mapped data. 
	 */
	public float[] mapXData(float[] data){		
		float min = Descriptive.min(data);
		float max = Descriptive.max(data);
		return Mat.map( data,min,max,plotLeft,plotLeft + plotWidth);
	}
	/**
	 * Maps the x-data from the range minXDat to maxXDat to 
	 * the plotLeft and plotRight. That is, 
	 * <code>mappedData[i] = plotLeft + (data[i] - minXDat) / (maxXDat-minXDat) * plotWidth</code>.
	 * This is not done directly though, but rather, the 
	 * {@link Mat#map(float[] data, float low1, float high1, float low2, float high2)} 
	 * function is used.
	 * <p>
	 * Note: No internal checks are done to make sure that the min/max values are in the
	 * data range.
	 * </p>
	 * 
	 * @param data the x data to map.
	 * @param minXDat the minimum value to use in the data mapping.
	 * @param maxXDat the maximum value to use in the data mapping. 
	 * @return the mapped data. 
	 */
	public float[] mapXData(float[] data, float minXDat, float maxXDat){		
		return Mat.map( data,minXDat,maxXDat,plotLeft,plotLeft + plotWidth);
	}

	/**
	 * Maps the y-data from the range min(data) to max(data) to 
	 * the plotBottom and plotTop. That is, 
	 * <code>mappedData[i] = plotBottom + (data[i] - min) / (max-min) * (-plotHeight)</code>.
	 * (The minus sign above accounts for the difference in Processing's coordinate system
	 * and what we associate with Top/Bottom).
	 * The computation above is not done directly though, but rather, the 
	 * {@link Mat#map(float[] data, float low1, float high1, float low2, float high2)} 
	 * function is used. 
	 * @param data the y data to map.
	 * @return the mapped data. 
	 */
	public float[] mapYData(float[] data){
		float min = Descriptive.min(data);
		float max = Descriptive.max(data);
		return Mat.map( data, min, max, plotTop + plotHeight, plotTop);
	}
	
	/**
	 * Maps the y-data from the range minYDat to maxYDat to 
	 * the plotBottom and plotTop. That is, 
	 * <code>mappedData[i] = plotBottom + (data[i] - minYDat) / (maxYDat-minYDat) * (-plotHeight)</code>.
	 * (The minus sign above accounts for the difference in Processing's coordinate system
	 * and what we associate with Top/Bottom).
	 * The computation above is not done directly though, but rather, the 
	 * {@link Mat#map(float[] data, float low1, float high1, float low2, float high2)} 
	 * function is used. 
	 * <p>
	 * Note: No internal checks are done to make sure that the min/max values are in the
	 * data range.
	 * </p>
	 * 
	 * @param data the x data to map.
	 * @param minYDat the minimum value to use in the data mapping.
	 * @param maxYDat the maximum value to use in the data mapping. 
	 * @return the mapped data. 
	 */
	public float[] mapYData(float[] data, float minYDat, float maxYDat){		
		return Mat.map( data,minYDat,maxYDat,plotTop + plotHeight, plotTop);
	}


	/**
	 * Draws the plot axes using a line with color _sColor, and weight _sWeight
	 */
	public void drawAxes(int _sColor, float _sWeight){
		horizLine(plotLeft,plotLeft+plotWidth,plotTop+plotHeight,_sColor,_sWeight);
		vertLine(plotTop,plotTop+plotHeight,plotLeft,_sColor,_sWeight);
	}

	/** 
	 * Draw the plot background rectangle using the default plot
	 * dimensions and background color.
	 *
	 * <li>Left most point: plotLeft</li>
	 * <li>Top most point: plotTop</li>
	 * <li>Width: plotWidth</li>
	 * <li>Height: plotHeight</li>
	 * </ul>
	 */
	public void drawRect(){
		drawRect(plotLeft,plotTop,plotWidth,plotHeight,bgColor);
	}

	/**
	 * Draws the plot background rectangle using the input dimensions and
	 * background color.
	 * @param _left the left-most point of the rectangle
	 * @param _top the top-most point of the rectangle
	 * @param _width the width of the rectangle
	 * @param _height the height of the rectangle
	 * @param _rectColor the rectangle color.
	 */
	public void drawRect(float _left,float _top,float _width,float _height, int _rectColor){
		myParent.stroke(150);
		myParent.strokeWeight(.5f); 		
		myParent.fill(_rectColor);
		myParent.pushStyle();
		myParent.rectMode(CORNER); 
		myParent.rect(_left,_top,_width,_height);
		myParent.popStyle();
	}
	
	/**
	 * Draws a legend going downwards with the first element situated at (x,y). 
	 * Useful for situations where you are plotting more than one dataset.
	 * 
	 * @param colors the legend colors
	 * @param labels labels that go with each color
	 */
	public void legendVert(float x, float y, int[] colors, String[] labels){
		int num = labels.length;
		if(num != colors.length) throw new IllegalArgumentException("The label and color arrays have different lengths");
	    
		myParent.textAlign(LEFT,TOP);
		
		for(int i=0; i<num; i++){
	    	myParent.fill(colors[i]);myParent.noStroke();
	    	myParent.rect(x, y+15*i, 10, 10);
	    	myParent.fill(100);
	    	writeLabels(labels[i],x+20, y+15*i);
	    }	    
	}
	
	/**
	 * Draws a legend going to the right with the first element situated at (x,y). 
	 * Useful for situations where you are plotting more than one dataset.
	 * 
	 * @param colors the legend colors
	 * @param labels labels that go with each color
	 */
	public void legendHoriz(float x, float y, int[] colors, String[] labels){
		int num = labels.length;
		if(num != colors.length) throw new IllegalArgumentException("The label and color arrays have different lengths");  
		myParent.textAlign(LEFT,TOP);	
		float x2 = x;
		for(int i=0; i<num; i++){			
	    	myParent.fill(colors[i]);myParent.noStroke();
	    	myParent.rect(x2, y, 10, 10);
	    	myParent.fill(100);
	    	writeLabels(labels[i],x2+15, y);
	    	x2 = x2+ myParent.textWidth(labels[i])+40;
	    }	    
	}

	
	/**
	 * Draws the x-axis tickmarks.
	 * @param _left left most tick position. Set to negative to make it default
	 * to the plotLeft)
	 * @param _width (_left + _width) = right most tick position. Set to negative
	 * @param _numDiv the number of divisions. The number of tick marks drawn = _numDiv+1.
	 * @param _tickHeight the height of the tick mark.
	 */
	public void xTicks(float _left, float _width, int _numDiv, int _tickHeight){
		if( _left<0) _left = plotLeft;
		if( _width<0) _width = plotWidth;
		
		myParent.noFill(); myParent.smooth();
		myParent.stroke(0); 
		myParent.strokeWeight(STROKEWEIGHT[0]); 
		float bottom1 = plotTop + plotHeight +.5f*_tickHeight;
		float bottom2 = bottom1 - _tickHeight;
		for(int i=1; i<_numDiv; i++){
			float linePos = _left + i*(_width-2*STROKEWEIGHT[1])/_numDiv + STROKEWEIGHT[1];
			myParent.line(linePos, bottom2, linePos, bottom2);
		}	
		// left-most and right-most ticks
		myParent.line(_left, bottom2, _left, bottom2);
		myParent.line(_left+_width, bottom1, _left+_width, bottom2);
	}
	
	/**
	 * Write the x-axis labels.
	 * @param _left left most label position. Set to negative to make it default
	 * to the plotLeft)
	 * @param _width (_left + _width) = right most label position. Set to negative
	 * to make it default to the plotWidth
	 * @param _xDatMin minimum data value to show in the label
	 * @param _xDatMax maximum data value to show in the label
	 * @param _numDiv the number of divisions.
	 */
	
	public void xLabels(float _left, float _width, float _xDatMin, float _xDatMax, int _numDiv){
		if( _left<0) _left = plotLeft;
		if( _width<0) _width = plotWidth;		
		int numDecimals=setNumDecimals(_xDatMin,_xDatMax,_numDiv); 
		setupFont(FONTNAME, CENTER, TOP, TEXTSIZE[2],GRAY[0]);
		float wEach = _width/(float)_numDiv; 
		float wEachVal = (_xDatMax - _xDatMin)/_numDiv;
		for (int i=1;i<_numDiv;i++) { // labels for the ratings
			float linePos = _left + i*wEach;
			float lineVal = _xDatMin+i*wEachVal;
			String val = myParent.nfc(lineVal,numDecimals);
			writeLabels(plotFont, val ,linePos, plotTop+plotHeight +5);
		}
		// the left and right labels
		myParent.textAlign(LEFT,TOP);
		writeLabels(plotFont, myParent.nfc(_xDatMin,numDecimals) ,_left, plotTop+plotHeight + 5);
		myParent.textAlign(RIGHT,TOP);
		writeLabels(plotFont, myParent.nfc(_xDatMax,numDecimals) ,_left+_width, plotTop+plotHeight + 5);
	}
	/**
	 * Write the x-axis labels.
	 * @param _left left most label position. Set to negative to make it default
	 * to the plotLeft)
	 * @param _width (_left + _width) = right most label position. Set to negative
	 * to make it default to the plotWidth
	 * @param _xLabels the String array of labels. 
	 */
	public void xLabels(float _left, float _width, String[] _xLabels){
		int numDiv = _xLabels.length-1;
		if( _left<0) _left = plotLeft;
		if( _width<0) _width = plotWidth;
		
		float wEach = _width/(numDiv);
		setupFont(FONTNAME, CENTER, TOP, TEXTSIZE[2],GRAY[0]);
		for (int i=1;i<numDiv; i++) { // labels for the ratings
			float linePos = _left + i*wEach;
			writeLabels(plotFont, _xLabels[i] ,linePos, plotTop+plotHeight + 5);
		}
		// the left and right labels
		myParent.textAlign(LEFT,TOP);
		writeLabels(plotFont, _xLabels[0] ,_left, plotTop+plotHeight + 5);
		myParent.textAlign(RIGHT,TOP);
		writeLabels(plotFont, _xLabels[numDiv] ,_left+_width, plotTop+plotHeight + 5);
	}
	
	/**
	 * Draws the y-axis tickmarks.
	* @param _top top most tick position in Processing's coordinate system. 
	 * Set to negative to make it default to the plotTop)
	 * @param _height  (top + height) = bottom most label position in Processing's coordinate system.
	 * Set to negative to make it default to the plotHeight.
	 * @param _numDiv the number of divisions. The number of tick marks drawn = _numDiv+1.
	 * @param _tickWidth the width of the tick mark.
	 */
	public void yTicks(float _top, float _height, int _numDiv, int _tickWidth ){
		if(_top<0) _top = plotTop;
		if( _height<0) _height = plotHeight;

		myParent.noFill(); myParent.smooth();
		myParent.stroke(0); 
		myParent.strokeWeight(STROKEWEIGHT[0]); 
		float left1 = plotLeft - .5f*_tickWidth;
		float left2 = left1 + _tickWidth;
		for(int i=1; i<_numDiv; i++){
			float linePos = _top + _height - i*(_height-2*STROKEWEIGHT[1])/_numDiv + STROKEWEIGHT[1];
			myParent.line(left1, linePos, left2, linePos);
		}	
		// the top and bottom ticks.
		myParent.line(left1, _top+_height, left2, _top+_height);		
		myParent.line(left1, _top, left2, _top);
	}
	

	/**
	 * Write the y-axis labels. 
	 * @param _top top most label position in Processing's coordinate system. 
	 * Set to negative to make it default to the plotTop)
	 * @param _height  (top + height) = bottom most label positionin Processing's coordinate system.
	 * Set to negative to make it default to the plotHeight.
	 * @param _yDatMin minimum data value to show in the label
	 * @param _yDatMax maximum data value to show in the label
	 * @param _numDiv the number of divisions.
	 */
	public void yLabels(float _top, float _height, float _yDatMin, float _yDatMax, int _numDiv){
		if(_top<0) _top = plotTop;
		if( _height<0) _height = plotHeight;
		int numDecimals=setNumDecimals(_yDatMin,_yDatMax, _numDiv); 
		setupFont(FONTNAME, RIGHT, CENTER, TEXTSIZE[2],GRAY[0]);
		for (int i=1;i<_numDiv;i++) { // labels for the ratings
			float linePos = _top+_height - i*_height/_numDiv;
			float lineVal = myParent.map(linePos, _top+_height, _top, _yDatMin, _yDatMax);
			String val = myParent.nfc(lineVal,numDecimals);
			writeLabels(plotFont, val, plotLeft-10, linePos);
		}
		// the top and bottom labels
		myParent.textAlign(RIGHT,BOTTOM);
		writeLabels(plotFont, myParent.nfc(_yDatMin,numDecimals) ,plotLeft-10, _top+_height);
		myParent.textAlign(RIGHT,TOP);
		writeLabels(plotFont, myParent.nfc(_yDatMax,numDecimals) ,plotLeft-10, _top);
	}
    
	/**
	 * Write the y-axis labels.
	 * @param _top top most label position in Processing's coordinate system. 
	 * Set to negative to make it default to the plotTop)
	 * @param _height  (top + height) = bottom most label positionin Processing's coordinate system.
	 * Set to negative to make it default to the plotHeight.
	 * @param _yLabels the String array of labels. 
	 */
	public void yLabels(float _top, float _height, String[] _yLabels){
		int numDiv = _yLabels.length-1;
		if(_top<0) _top = plotTop;
		if( _height<0) _height = plotHeight;
		setupFont(FONTNAME, CENTER, TOP, TEXTSIZE[2],GRAY[0]);
		float hEach = _height/numDiv; float bottom = _top + _height;
		for (int i=1;i<numDiv; i++) { // labels for the ratings
			float linePos = bottom - i*hEach;
			writeLabels(plotFont, _yLabels[i] ,linePos, plotTop+plotHeight +5);
		}
		// the top and bottom labels
		myParent.textAlign(RIGHT,BOTTOM);
		writeLabels(plotFont, _yLabels[0],plotLeft-10, _top+_height);
		myParent.textAlign(RIGHT,TOP);
		writeLabels(plotFont, _yLabels[numDiv], plotLeft-10, _top);
	}
	
	/** Draws horizontal y-lines on the plot
	 * @param _numDiv number of divisions. The number of lines drawn = numDiv+1
	 */
	public void YLines(int _numDiv) {
		float yTop =plotTop; 
		float yBottom = plotTop+plotHeight;
		myParent.noFill();
		myParent.stroke(GRAY[2]); 
		myParent.strokeWeight(STROKEWEIGHT[0]); 
		
		float hEach = plotHeight/_numDiv;
		// top and bottom lines are left out
		for (int i=1;i<_numDiv;i++) { 
			float linePos = yBottom - i*hEach;
			myParent.line(plotLeft, linePos, plotLeft+plotWidth, linePos);
		}
	}

	/**
	 * draws a horizonal line with color _sColor, and weight _sWeight
	 */
	public void horizLine(float _xFrom, float _xTo, float _y, int _sColor, float _sWeight){
		myParent.stroke(_sColor); 
		myParent.strokeWeight(_sWeight);
		myParent.line(_xFrom,_y,_xTo,_y);
	}
	/**
	 * draws a vertical line with color _sColor, and weight _sWeight
	 */
	public void vertLine(float _yFrom, float _yTo, float _x, int _sColor, float _sWeight){
		myParent.stroke(_sColor); 
		myParent.strokeWeight(_sWeight);
		myParent.line(_x,_yFrom,_x,_yTo);
	}
	
	/**
	 * Draws a straight line going from (x[0],y[0]) to (x[1],y[1]) 
	 * with color _sColor, and weight _sWeight
	 */
	public void line(float _x1, float _y1, float _x2, float _y2, int _sColor, float _sWeight){
		myParent.stroke(_sColor); 
		myParent.strokeWeight(_sWeight);
		myParent.line(_x1,_y1,_x2,_y2);
	}
	/**
	 * titles the plot given the String _title and the PFont
	 */
	public void writeTitle(String _title, PFont _plotFont){
		myParent.textAlign(CENTER,CENTER);
		myParent.fill(50);
		writeLabels(_plotFont,_title,plotLeft + .5f*plotWidth, plotTop -30 );
	}

	public void writeLabels(PFont _plotFont, String[] _text, float[] _xPos, float[] _yPos){
		myParent.textFont(_plotFont);
		myParent.smooth();
		for(int i=0; i<_text.length; i++){
			myParent.text(_text[i],_xPos[i],_yPos[i]);
		}
	}

	public void writeLabels(PFont _plotFont, String _text, float _xPos, float _yPos){
		if (_plotFont==null){
			_plotFont = plotFont;
		}
		myParent.smooth();
		myParent.textFont(_plotFont);
		myParent.text(_text,_xPos,_yPos);
	}	

	public void writeLabels(PFont _plotFont, float _text, float _xPos, float _yPos){
		myParent.smooth();
		myParent.textFont(_plotFont);
		myParent.text(_text,_xPos,_yPos);
	}	
	public void writeLabels(float _text, float _xPos, float _yPos){
		writeLabels(plotFont,_text,_xPos,_yPos);
	}
	public void writeLabels(String _text, float _xPos, float _yPos){
		writeLabels(plotFont,_text,_xPos,_yPos);
	}


	/**
	 * function for setting the font up for writing the labels
	 */
	public void setupFont(String _fontName, int _xAlign, int _yAlign, int _tSize ){
		myParent.smooth();
		myParent.noStroke();
		myParent.textAlign(_xAlign,_yAlign);

		if(_fontName.equals("")){
			_fontName = FONTNAME;
		}

		plotFont = myParent.createFont(_fontName, _tSize,true);
		myParent.textFont(plotFont);
	}

	/**
	 * overload setupFont function that takes in font color
	 */
	public void setupFont(String _fontName, int _xAlign, int _yAlign, int _tSize, int _fontColor ){
		myParent.fill(_fontColor);
		setupFont(_fontName, _xAlign, _yAlign,  _tSize);
	}


	public void setBackgroundColor(int _backgroundColor){
		bgColor = _backgroundColor;
	}
	
	/* set the number of decimal digits to display. Useful for writing labels on the fly.*/
	protected int setNumDecimals(float minVal, float maxVal, int numDiv){
		float interval = (maxVal-minVal)/numDiv;
		int numDecimals = -1;
		//integer?
		if(Math.round(interval)==interval) return numDecimals;		
		else return 1;
	}

	public float getLeft(){
		return plotLeft;
	}
	public float getRight(){
		return plotLeft + plotWidth;
	}
	public float getTop(){
		return plotTop;
	}
	public float getBottom(){
		return plotTop + plotHeight;
	}
	public float getWidth(){
		return plotWidth;
	}
	public float getHeight(){
		return plotHeight;
	}

	public void setLeft(float _plotLeft){
		plotLeft = _plotLeft;
	}
	public void setWidth(float _plotWidth){
		plotWidth = _plotWidth;
	}
	public void setTop(float _plotTop){
		plotTop = _plotTop;
		//plotBottom = plotTop + plotHeight;
	}
	public void setHeight(float _plotHeight){
		plotHeight = _plotHeight;
		//plotBottom = plotTop + plotHeight;
	}

	//	public void setFillColor(int _fillColor){
	//		fillColor = _fillColor;
	//	}
}