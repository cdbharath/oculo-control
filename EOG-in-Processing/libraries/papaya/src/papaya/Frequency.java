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

import java.util.*;
import java.io.*;
import processing.core.*;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Class for getting the frequency distribution, cumulative frequency distribution, and other
 * distribution-related parameters of a given array of <it>floats</it> or <it>ints</it>. 
 * To initialize, use something like this:
 * <p>
 * <code>Frequency freq= new Frequency(dataArray, minimumVal,maximumVal,widthOfEachBin);</code>
 * 
 * Once initialized, it stores 3 things:
 * <ul>
 * <li>The array containing the number of elements in each of the histogram bins ({@link getFrequency()}).
 * <li>The number of bins.
 * <li>The length of the full data array.
 * </ul>
 *   
 * <p>A number of different methods are available for computing other distribution-related parameters 
 * (e.g. the relative frequency distribution, the cumulative frequency distribution, etc).</p>
 * 
 * <p>See the FrequencyExample in the examples folder for an example.</p>
 * 
 * @author Adila Faruk
 */

// TO DO: OBJECTS!!!!
public class Frequency{

	private int numBins = 0;
	private int ln = 0;
	// stored as a float so that we don't mess things up when dividing!
	private float[] frequency; 

	/**
	 * Initialize the class by setting the minimum value, maximum value, and the bin width. 
	 * Upon initialization, the number of bins are determined and the 
	 * frequency distribution array is computed for the given input data and histogram bin width. 
	 * This frequency distribution can be accessed using the {@link getFrequency()} function. 
	 * <it>Note that this is the only place where you can set the frequency distribution.</it>
	 * 
	 * <p><b>How is the number of bins computed?</b>
	 * <br>Like this:
	 * <br><code>bins = floor[ (max - min) / binWidth ] +1 </code>, </br>
	 * as opposed to <code>ceil[ (max - min) / binWidth ]</code>.
	 * Why? The latter algorithm is problematic when <code>[ (max - min) / binWidth ]</code>
	 * is a whole number.
	 * E.g. let min = 0.0, max = 100.0, and binWidth = 10.0. We get 10 bins with 
	 * <ul>
	 * <li>1st bin  = bin[0]  --> contains elements 0.0 - 9.999...
	 * <li>2nd bin  = bin[1]  --> contains elements 10.0 - 19.999...
	 * <li>...
	 * <li>...
	 * <li>10th bin = bin[9]  --> contains elements 90.0-99.999...
	 * </ul>
	 * Poor 100. He's got nowhere to go but into an <it>Array Index out of bound Exception</it>!
	 * </p>
	 * <p><b>What element goes into what bin?</b>
	 * <br> Let <code>value = data[i];</code> 
	 * <br><code>value</code> will go into bin number <code>floor(  (_inputDat[i]-_minVal)/_binWidth );</code>
	 * </p>
	 * 
	 * @param _inputDat the input data
	 * @param _minVal the minimum value in the array 
	 * @param _maxVal the maximum value in the array
	 * @param _binWidth the width of each of the bins 
	 */
	public Frequency(float[] _inputDat, float _minVal, float _maxVal, float _binWidth){	
		numBins = (int)Math.floor((_maxVal - _minVal)/_binWidth) +1; 
		frequency = new float[numBins];
		for (int i=0; i<_inputDat.length; i++) {
			frequency[(int) Math.floor(  (_inputDat[i]-_minVal)/_binWidth ) ]++;
		}
		ln = _inputDat.length;
	}
	
	/**
	 * Initialize the class by setting the minimum value, maximum value, and the bin width. 
	 * Upon initialization, the number of bins are determined and the 
	 * frequency distribution array is computed for the given input data and histogram bin width. 
	 * This frequency distribution can be accessed using the {@link getFrequency()} function. 
	 * <it>Note that this is the only place where you can set the frequency distribution.</it>
	 * 
	 * <p>See {@link Frequency(float[] _inputDat, float _minVal, float _maxVal, float _binWidth)} for 
	 * more details.
	 * </p>
	 */
	public Frequency(int[] _inputDat, int _minVal, int _maxVal, int _binWidth){	
		numBins = ( (_maxVal - _minVal)/_binWidth) +1; 
		frequency = new float[numBins];
		for (int i=0; i<_inputDat.length; i++) {
			frequency[  (_inputDat[i]-_minVal)/_binWidth  ]++;
		}
		ln = _inputDat.length;
	}

	/**
	 * Computes the fraction of the total data that's in each bin.
	 * This is similar to dividing the number in each bin by the total
	 * number of elements in the data array.
	 */
	public float[] compRelFrequency() {
		return Mat.multiply( frequency, (float)1/ln );
	}

	/**
	 * Computes the percents of the total data that's in each bin.
	 * This isimilar to dividing the number in each bin by the total
	 * number of elements in the data array x 100%
	 */
	public float[] compRelFrequencyPct() {
		return Mat.multiply( frequency, (float)100/ln );
	}

	/**
	 * Computes the cumulative frequency of the data. 
	 */
	public float[] compCumFrequency() {
		float[] cumFreq = new float[frequency.length];
		cumFreq[0] = frequency[0];
		for(int i=1; i<frequency.length; i++){
			cumFreq[i] = frequency[i] + cumFreq[i-1];
		}
		return cumFreq;
	}
	/**
	 * Computes the cumulative relative frequency of the data.
	 * This is similar to dividing the number in each bin of
	 * the cumulative frequency array ({@link compCumFrequency()}) by
	 * the number of elements in the data array.
	 */
	public float[] compRelCumFrequency() {
		return Mat.multiply( compCumFrequency(), (float)1/ln);
	}
	/**
	 * Computes the cumulative frequency of the data as a percentage.
	 * This is similar to dividing the number in each bin of
	 * the cumulative frequency array ({@link compCumFrequency()}) by
	 * the number of elements in the data array x 100%.
	 */
	public float[] compRelCumFrequencyPct() {
		return Mat.multiply( compCumFrequency() , (float)100/ln);
	}

	// ------------------------------------------------
	// Get functions to access the private variables. 
	// No additional computations required; yay!
	/**
	 * Returns the pre-computed frequency array.
	 * An array of floats so division won't cause any surprises.
	 */
	public float[] getFrequency(){
		return frequency;
	}
	/**
	 * Returns the number of bins.
	 */
	public int getNumBins(){
		return numBins;
	}
	/**
	 * returns the length of the dataset.
	 */
	public int getLength(){
		return ln;
	}
}

