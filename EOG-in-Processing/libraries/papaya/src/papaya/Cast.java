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

import java.util.*;
import java.io.*;
import processing.core.*;

/**
 * Static Class for casting Object arrays to their corresponding primitive type.
 * Similar to the <code>toArray(T[] a)</code> method specified in the java Collections
 * interface, but for float[] and int[] arrays (as opposed to Float[] and Integer[]).
 * <p>
 * 
 * </p>
 * @author Nur Adila Faruk Senan
 */
public final class Cast {

	/**
	 * Makes this class non instantiable, but still let's others inherit from it.
	 */
	protected Cast(){
	}
	
	/**
	 * function for casting ArrayList to int[]
	 * @param _inputDat ArrayList array that needs to be cast to int[]
	 * @return the resulting int[] array
	 */
	public static int[] arrayListToInt(ArrayList<Integer> _inputDat){
//		if(_inputDat.size()==0){
//			System.out.println("Warning: zero length array ");
//		}
		int[] outputDat = new int[_inputDat.size()];
		for(int i = 0; i<_inputDat.size(); i++)
			outputDat[i] = _inputDat.get(i);		
		return outputDat;
	}
	
	/**
	 * function for casting ArrayList to float[]
	 * @param _inputDat ArrayList array that needs to be cast to float[]
	 * @return the resulting float[] array
	 */
	public static float[] arrayListToFloat(ArrayList<Float> _inputDat){
		float[] outputDat = new float[_inputDat.size()];
		for(int i = 0; i<_inputDat.size(); i++){
			outputDat[i] = _inputDat.get(i);
		}		
		return outputDat;
	}
	
	/**
	 * function for casting float[][] to double[][]
	 * @param _inputDat float[][] 2d array
	 * @return deep copy of _inputDat with each element cast to a double
	 */
	public static double[][] floatToDouble(float[][] _inputDat){
		int numColumns = _inputDat[0].length; int numRows = _inputDat.length;
		double[][] outputDat = new double[numRows][numColumns];
		for(int i = 0; i<numRows; i++){
			for(int j = 0; j<numColumns; j++){
				outputDat[i][j] = (double)_inputDat[i][j];
			}
		}		
		return outputDat;
	}
	/**
	 * function for casting float[] to double[]
	 * @param _inputDat float[]  array
	 * @return deep copy of _inputDat with each element cast to a double
	 */
	public static double[] floatToDouble(float[] _inputDat){
		int numRows = _inputDat.length;
		double[] outputDat = new double[numRows];
		for(int i = 0; i<numRows; i++){
			outputDat[i] = (double)_inputDat[i];
		}		
		return outputDat;
	}
	/**
	 * function for casting double[][] to float[][]
	 * @param _inputDat double[][] 2d array
	 * @return deep copy of _inputDat with each element cast to a float
	 */
	public static float[][] doubleToFloat(double[][] _inputDat){
		int numColumns = _inputDat[0].length; 
		int numRows = _inputDat.length;
		float[][] outputDat = new float[numRows][numColumns];
		for(int i = 0; i<numRows; i++){
			for(int j = 0; j<numColumns; j++){
				outputDat[i][j] = (float)_inputDat[i][j];
			}
		}		
		return outputDat;
	}
	/**
	 * function for casting double[] to float[]
	 * @param _inputDat double[]  array
	 * @return deep copy of _inputDat with each element cast to a float
	 */
	public static float[] doubleToFloat(double[] _inputDat){
		int numRows = _inputDat.length;
		float[] outputDat = new float[numRows];
		for(int i = 0; i<numRows; i++){
			outputDat[i] = (float)_inputDat[i];
		}		
		return outputDat;
	}
	
	/**
	 * Function for casting hashMap keySet to String[]
	 * @param _hMap input HashMap
	 * @return Hashmap.keySet() cast to String[]
	 * Use only for the case where the HashMap Keys are Strings
	 */
	public static String[] keySetToString(HashMap _hMap){			
		return (String[])_hMap.keySet().toArray(new String[_hMap.size()] );
	}
	
	/**
	 * Function for casting hashMap keySet to float[]
	 * @param _hMap input HashMap 
	 * @return Hashmap.keySet() cast to float[]
	 * Use only for the case where the HashMap Keys are floats
	 */
	public static int[] keySetToInt(HashMap _hMap){
		int ln = _hMap.size();
		int[] outputDat =  new int[ln]; 
		Integer[] hMapKeySet = (Integer[])_hMap.keySet().toArray();
		for(int i=0; i<ln; i++)
			outputDat[i] = hMapKeySet[i].intValue();
		
		return outputDat;
	}
	/**
	 * Function for casting hashMap keySet to float[]
	 * @param _hMap input HashMap 
	 * @return Hashmap.keySet() cast to float[]
	 * Use only for the case where the HashMap Keys are floats
	 */
	public static float[] keySetToFloat(HashMap _hMap){
		int ln = _hMap.size();
		float[] outputDat =  new float[ln]; 
		Float[] hMapKeySet = (Float[])_hMap.keySet().toArray();
		for(int i=0; i<ln; i++)
			outputDat[i] = hMapKeySet[i].floatValue();
		
		return outputDat;
	}
	/**
	 * Function for casting a Set to float[]
	 */
	public static int[] setToInt(Set<Integer> theSet) {
		int[] a = new int[theSet.size()];
		int i = 0;
		for (Integer val : theSet) a[i++] = val;
		return a;
	}
	/**
	 * Function for casting a Set to float[]
	 */
	public static float[] setToFloat(Set<Float> theSet) {
		float[] a = new float[theSet.size()];
		int i = 0;
		for (Float val : theSet) a[i++] = val;
		return a;
	}
	
	/**
	 * Function for casting a Set to String array. This is really just one line of code, but it's 
	 * much easier to have it as an external function rather than having to remember it
	 */
	public static String[] setToString(Set<String> theSet) {
		return (String[])theSet.toArray(new String[theSet.size()]); 
	}
	
	
	/**
	 * function for casting Vector to int[]
	 * @param _inputDat Vector array that needs to be cast to int[]
	 * @return the resulting int[] array
	 */
	public static int[] vectorToInt(Vector<Integer> _inputDat){
		int[] outputDat = new int[_inputDat.size()];
		for(int i = 0; i<_inputDat.size(); i++)
			outputDat[i] = _inputDat.get(i);		
		return outputDat;
	}
	
	/**
	 * function for casting Vector to float[]
	 * @param _inputDat Vector array that needs to be cast to float[]
	 * @return the resulting float[] array
	 */
	public static float[] vectorToFloat(Vector<Float> _inputDat){
		float[] outputDat = new float[_inputDat.size()];
		for(int i = 0; i<_inputDat.size(); i++){
			outputDat[i] = _inputDat.get(i);
		}		
		return outputDat;
	}
	
	/**
	 * Returns a PVector array with the x, y, and z coordinates set to the input values
	 * (technically not a cast, but a reorganization).
	 */
	public static PVector[] floatToPVector(float[] x, float[] y, float[] z){
		int size = x.length;
		Check.equalLengths(size,y); Check.equalLengths(size, z);
		PVector[] p = new PVector[size];
		for(int i=0; i<size; i++){
			p[i] = new PVector(x[i],y[i],z[i]);
		}
		return p;
	}
	/**
	 * Returns a PVector array with the x, and coordinates set to the input values, and the z coordinates
	 * set to zero.
	 * (technically not a cast, but a reorganization).
	 */
	public static PVector[] floatToPVector(float[] x, float[] y){
		int size = x.length;
		Check.equalLengths(size,y); 
		PVector[] p = new PVector[size];
		for(int i=0; i<size; i++){
			p[i] = new PVector(x[i],y[i]);
		}
		return p; 
	}
	
	
	
	
}