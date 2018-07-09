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

import java.util.ArrayList;

import processing.core.*;

/**
 * <p>Contains various methods for dealing with NaNs in your data.</p>
 * Currently only accommodates float and double arrays. Java's Integer.isNaN doesn't
 * seem to exist or I'd include that as well.
 * 
 * @author Adila Faruk
 *
 */
public final class NaNs {
	

	/**
	 * Makes this class non instantiable, but still let's others inherit from it.
	 */
	protected NaNs(){}
	

	/**
	 * Eliminate the NaNs from the input array. 
	 * 
	 * @param data the array containing NaNs
	 * @return the new array (with length < data.length) devoid
	 * of NaNs.
	 */
	public static double[] eliminate(double[] data){
		double[] data2 = new double[data.length];
		int j=0;
		for(int i=0; i<data.length; i++){
			if(!Double.isNaN(data[i])){
				data2[j++] = data[i];
			}
		}
		double[] eliminated = new double[j];
        System.arraycopy(data2, 0, eliminated, 0, j);
        return eliminated;		
	}
	
	
	/**
	 * Returns a new array with NaN elements in the original data set replaced with the value
	 * specified by newNaN.
	 * @param data the array containing NaNs
	 * @param newNaN the value to replace the NaNs with.
	 * @return new array with NaNs replaced with the values specified by newNaN.
	 */
	public static double[] replaceNewWith(double[] data, double newNaN){
		double[] replaced = new double[data.length];
		int j=0;
		for(int i=0; i<data.length; i++){
			if(Double.isNaN(data[i])) {
				replaced[i] = newNaN;
			}
			else{
				replaced[i] = data[i];
			}
		}
		return replaced;
	}
	
	/**
	 * Modifies the original data array by replacing the NaN elements in the data set with the value
	 * specified by newNaN.
	 * @param data the array containing NaNs
	 * @param newNaN the value to replace the NaNs with.
	 */
	public static void replaceOriginalWith(double[] data, double newNaN){
		int j=0;
		for(int i=0; i<data.length; i++){
			if(Double.isNaN(data[i])) data[i] = newNaN;	
		}
	}
	 
    /**
     * Checks for presence of NaNs in the data array. 
     *
     * @param data array to be searched for NaNs
     * @return true iff ranks contains one or more NaNs
     */
    public static boolean containsNaNs(double[] data) {
        for (int i = 0; i < data.length; i++) {
            if (Double.isNaN(data[i])) {
                return true;
            }
        }
        return false;
    }
    /**
     * Returns an array list of indexes where <code>data[i]</code> is <code>NaN.</code>
     *
     * @param data array to search for <code>NaNs</code>
     * @return list of indexes i such that <code>data[i] = NaN</code>
     */
    public static ArrayList<Integer> getNaNPositions(double[] data) {
        ArrayList<Integer> out = new ArrayList<Integer>();
        for (int i = 0; i < data.length; i++) {
            if (Double.isNaN(data[i])) {
                out.add(Integer.valueOf(i));
            }
        }
        return out;
    }
	
	
	/**
	 * Eliminate the NaNs from the input array. 
	 * 
	 * @param data the array containing NaNs
	 * @return the new array (with length < data.length) devoid
	 * of NaNs.
	 */
	public static float[] eliminate(float[] data){
		float[] data2 = new float[data.length];
		int j=0;
		for(int i=0; i<data.length; i++){
			if(!Float.isNaN(data[i])) {
				data2[j++] = data[i];
			}
		}
		float[] eliminated = new float[j];
        System.arraycopy(data2, 0, eliminated, 0, j);
        return eliminated;		
	}
	
	/**
	 * Returns a new array with NaN elements in the original data set replaced with the value
	 * specified by newNaN.
	 * @param data the array containing NaNs
	 * @param newNaN the value to replace the NaNs with.
	 * @return new array with NaNs replaced with the values specified by newNaN.
	 */
	public static float[] replaceNewWith(float[] data, float newNaN){
		float[] replaced = new float[data.length];
		int j=0;
		for(int i=0; i<data.length; i++){
			if(Float.isNaN(data[i])) {
				replaced[i] = newNaN;
			}
			else{
				replaced[i] = data[i];
			}
		}
		return replaced;
	}
	
	/**
	 * Modifies the original data array by replacing the NaN elements in the data set with the value
	 * specified by newNaN.
	 * @param data the array containing NaNs
	 * @param newNaN the value to replace the NaNs with.
	 */
	public static void replaceOriginalWith(float[] data, float newNaN){
		int j=0;
		for(int i=0; i<data.length; i++){
			if(Float.isNaN(data[i])) data[i] = newNaN;	
		}
	}
	 
    /**
     * Checks for presence of NaNs in the data array. 
     *
     * @param data array to be searched for NaNs
     * @return true iff ranks contains one or more NaNs
     */
    public static boolean containsNaNs(float[] data) {
        for (int i = 0; i < data.length; i++) {
            if (Float.isNaN(data[i])) {
                return true;
            }
        }
        return false;
    }
    /**
     * Returns an array list of indexes where <code>data[i]</code> is <code>NaN.</code>
     *
     * @param data array to search for <code>NaNs</code>
     * @return list of indexes i such that <code>data[i] = NaN</code>
     */
    public static ArrayList<Integer> getNaNPositions(float[] data) {
        ArrayList<Integer> out = new ArrayList<Integer>();
        for (int i = 0; i < data.length; i++) {
            if (Float.isNaN(data[i])) {
                out.add(Integer.valueOf(i));
            }
        }
        return out;
    }
	
}

///**
// * Eliminate the NaNs from the input array. 
// * 
// * @param data the array containing NaNs
// * @return the new array (with length < data.length) devoid
// * of NaNs.
// */
//public static int[] eliminate(int[] data){
//	int[] data2 = new int[data.length];
//	int j=0;
//	for(int i=0; i<data.length; i++){
//		if(!Integer.isNaN(data[i])){
//			data2[j++] = data[i];
//		}
//	}
//	int[] eliminated = new int[j];
//    System.arraycopy(data2, 0, eliminated, 0, j);
//    return eliminated;		
//}
//
///**
// * Replaces the NaN elements in the data set with the value
// * specified by newNaN.
// * @param data the array containing NaNs
// * @param newNaN the value to replace the NaNs with.
// * @return
// */
//public static int[] replaceWith(int[] data, int newNaN){
//	int[] replaced = new int[data.length];
//	int j=0;
//	for(int i=0; i<data.length; i++){
//		if(Integer.isNaN(data[i])) {
//			replaced[i] = newNaN;
//		}
//		else{
//			replaced[i] = data[i];
//		}
//	}
//	return replaced;
//}
// 
///**
// * Checks for presence of NaNs in the data array. 
// *
// * @param data array to be searched for NaNs
// * @return true iff ranks contains one or more NaNs
// */
//public static boolean containsNaNs(int[] data) {
//    for (int i = 0; i < data.length; i++) {
//        if (Integer.isNaN(data[i])) {
//            return true;
//        }
//    }
//    return false;
//}
///**
// * Returns an array list of indexes where <code>data[i]</code> is <code>NaN.</code>
// *
// * @param data array to search for <code>NaNs</code>
// * @return list of indexes i such that <code>data[i] = NaN</code>
// */
//public static ArrayList<Integer> getNaNPositions(int[] data) {
//    ArrayList<Integer> out = new ArrayList<Integer>();
//    for (int i = 0; i < data.length; i++) {
//        if (Integer.isNaN(data[i])) {
//            out.add(Integer.valueOf(i));
//        }
//    }
//    return out;
//}