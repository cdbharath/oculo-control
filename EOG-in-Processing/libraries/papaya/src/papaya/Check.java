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
 * Class for checking the input data. Contains the error messages.
 */

final class Check {
	/**
	 * Makes this class non instantiable, but still let's others inherit from it.
	 */
	protected Check(){}

	/**
	 * Checks the length of two arrays.
	 * @param size size of the first array
	 * @param array the second array
	 */
	protected static void equalLengths(int size, float[] array){
		if(size!=array.length) throw new IllegalArgumentException("Input arrays have different lengths.");
	}
	protected static void equalLengths(int size, int[] array){
		if(size!=array.length) throw new IllegalArgumentException("Input arrays have different lengths.");
	}
	protected static void equalSizes(float[][] A, float[][] B){
		if(A.length!=B.length ||A[0].length!=B[0].length) throw new IllegalArgumentException("Matrix dimensions must agree.");
	}
	protected static void dimensionsForMultiplication(int numColumnsA,int numRowsB){
		if(numColumnsA != numRowsB) throw new IllegalArgumentException("Dimension mismatch for matrix multiplication.");
	}
	protected static void size0(int size){
		if(size<0) throw new IllegalArgumentException("Empty input array.");
	}
	protected static void size3(int size){
		if(size<3) throw new IllegalArgumentException("Input size needs to be greater than three.");
	}
	protected static void size20(int size){
		if(size<20) throw new IllegalArgumentException("Input size needs to be greater than twenty.");
	}
	protected static void square(float[][] A){
		if(A.length!=A[0].length) throw new IllegalArgumentException("Matrix is not square.");
	}
}
