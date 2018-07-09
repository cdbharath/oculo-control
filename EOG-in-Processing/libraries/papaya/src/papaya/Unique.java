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

/**
 * Class for getting and storing an <b>unsorted </b> array's unique elements,
 * the indices of these elements, and the number of times the elements occur.
 * <p>This class is overkill if:
 * <br>
 * you want to store <i>only</i> the unique elements. Use a Java
 * <a href="http://docs.oracle.com/javase/6/docs/api/java/util/Set.html/" target="_blank">Set</a> instead. 
 * <br>
 * you don't want to store everything in a class. Use the
 * {@link Descriptive#frequencies} method instead.
  */
public class Unique {
	
	/**
	 * ArrayList containing the unique values
	 */
	public ArrayList<Float> values = new ArrayList<Float>();
	/**
	 * ArrayList containing the frequencies of the corresponding unique value
	 */
	public ArrayList<Integer> frequencies = new ArrayList<Integer>();
	/**
	 * Class containing the integer arrays that hold the indices of each unique value.
	 */
	public int[][] idx;
	
	/**
	 * Class for storing the unique values in an <b>unsorted</b> data array. Does essentially the same
	 * thing as the Descriptive.frequencies method would, except:
	 * <br></br><br>
	 * - The input data array is unsorted.
	 * </br>
	 * <br>
	 * - You can choose to store the indices of each unique value as well by setting the @param storeIndices
	 * to <code>true</code>.
	 * </br>
	 * <br>
	 * - It (currently) only works for float arrays.
	 * </br>
	 * <p><b>Example:</b></p>
	 * <p>
	 * <code>data = (8,5,6,7,8,5,5) --> values = (5,6,7,8), frequencies = (3,1,1,2), </code>
	 * <code>idx[0] = {1,5,6}, idx[1] = {2}, idx[2] = {3}, idx[3] = {0,4} </code>
	 * </p>
	 * 
	 * @param data the data array 
	 * @param storeIndices set to <code>true</code> if you want to store the indices, 
	 * 		  else set to <code>false</code.
	 */
	public Unique(float[] data, boolean storeIndices){	
		
		// easy since we can use the sorted index
		float[] sortedData = Mat.copyThenSort(data);
		
		// get the discrete values and their associated frequencies
		Descriptive.frequencies(sortedData,values,frequencies);
		
		if(storeIndices){
			idx = new int[values.size()][1];
			// Make sure you use the original data and not the sorted one!
			for(int i=0; i< values.size(); i++){
				if(frequencies.get(i)==1){
					// just search until the first index
					idx[i][0] = Find.indexOf(data, values.get(i) );
				}
				else{
					// search the whole array.
					// TO DO: Find.indicesWith(data, valueToGet, startIndex, numOfRepeats
					idx[i] = Find.indicesWith(data, values.get(i));
				}
			}
		}
	}
}