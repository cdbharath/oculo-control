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

import java.awt.event.KeyEvent;

import processing.core.*;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PConstants;

/**
 * PapayaConstants stores some of the constants used in plotting.
 * I try to follow processing's conventions where possible 
 * as specified in processing.core.PConstants 
 * The <a href="http://processing.googlecode.com/svn/trunk/processing/build/javadoc/core/index.html" 
 * target="_blank">Processing Javadoc</a> has more details.
 * 
 */
public interface PapayaConstants{

	public final static int BASELINE = 0; 
	
	public final static int CORNER = 0; 
	 
	public final static int BOTTOM = 102;
	
	public final static int CENTER = 3;
	
	public final static int LEFT = 37;
	
	public final static int TOP = 101;
	
	public final static int RIGHT = 39;
	
	/** 
	 * Default font used.
	 */
	public final static String FONTNAME = "Helvetica";
	
	public final static float[] STROKEWEIGHT ={0.25f, .5f, .75f, 1};
	
	public final static int[] GRAY ={100, 150, 200, 240, 250};
	
	public final static int[] TEXTSIZE = {6,8,10,12};
	
	
	/**
	 * Index to return if a given value/object is not found in the {@link Cast} function. 
	 */
	public static final int INDEX_NOT_FOUND = -1;
	
	/**
	 * Index array to return if a given value/object is not found in the {@link Cast} function. 
	 */
	public static final int[] INDICES_NOT_FOUND = new int[]{-1};
	
	/**
	 * machine constants
	 */
	public static final double MACHEP =  1.11022302462515654042E-16;
	public static final double MAXLOG =  7.09782712893383996732E2;
	public static final double MINLOG = -7.451332191019412076235E2;
	public static final double MAXGAM = 171.624376956302725;
	public static final double SQTPI  =  2.50662827463100050242E0;
	public static final double SQRTH  =  7.07106781186547524401E-1;
	public static final double LOGPI  =  1.14472988584940017414;

	public static final double big = 4.503599627370496e15;
	public static final double biginv =  2.22044604925031308085e-16;
	
}

