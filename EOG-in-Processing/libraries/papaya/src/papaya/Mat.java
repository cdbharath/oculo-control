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
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import processing.core.*;

/**
 * Static class for performing some basic matrix operations.
 * <p>
 * <b>Guidelines</b>
 * <br>
 * Methods that modify an array by a value always take the array as the first parameter, and
 * the value as the second.
 * For example,
 * <ul>
 * <br>{@link divide(float[] array, float numberToDivideBy)}
 * <br>{@link multiply(float[] array, float numberToMultiplyBy)}
 * <br>{@link sum(float[] array, float numberToAdd)}
 * </ul>
 * I try to follow that guiding principle with all methods that take in an input
 * array (e.g. {@link swap(float[] array, int index1, int index2)}, 
 * {@link populate(float[] array, int[] indices)}).
 * <p>
 * <b>Remarks</b>
 * <br>
 * Almost all the methods here take in float arrays or matrices, although I've also added in a few
 * methods that operate on integers. In addition, to avoid accidental array manipulation,
 * all methods return a <i>new</i> array or matrix instead of modifying the original one. E.g.
 * <br><code>C[i][j] = A[i][j]*B[i][j] return C</code>
 * <br>as opposed to 
 * <br><code>A[i][j] = A[i][j]*B[i][j], return A</code>
 * <br> in {@link dotMultiply(float[][],float[][])}.
 * (If you find it performing to the contrary though, please let me know!)
 * 
 * <p> 
 * Some non-trivial matrix-math related methods, including those for getting
 * the condition number, inverse, rank, and norms of a 2D input matrix are also included.
 * Making everything float-based instead of double-based naturally results in some precision loss but
 * the internal computations are done using doubles to minimize this. 
 * 
 * <p>
 * 
 * @see Cast
 * @see Find
 * @see NaNs
 * @see Rank
 * @see Sorting
 *
 */
public final class Mat {

	/**
	 * Makes this class non instantiable, but still let's others inherit from it.
	 */
	protected Mat(){

	}
	/**
	 * Appends the part of the specified list between <code>from</code> (inclusive) and 
	 * <code>to</code> (inclusive) to the receiver.
	 *
	 *@param listToAddTo ArrayList to add the elements to. <b>Clears this list before
	 * adding the elements in. 
	 * @param data the array to be added to the listToAddTo.
	 * @param from the index of the first element to be appended (inclusive).
	 * @param to the index of the last element to be appended (inclusive).
	 * @exception IndexOutOfBoundsException index is out of range 
	 * (<code>(from&lt;0 || from&gt;to || to&gt;=sortedData.length)</code>).
	 */
	private static void addAllOfFromTo(ArrayList listToAddTo, float[] sortedData,int from, int to){
		if (from<0 || from>to || to>sortedData.length) {
			throw new IndexOutOfBoundsException("from: "+from+", to: "+to+", sortedData.length: "+sortedData.length);
		}
		// print a warning just in case.
		if(listToAddTo.size()>0){
			System.out.println("Warning: Input ArrayList has size "+listToAddTo.size()+
					". Appending the data to the end of the end");
		}
		for(int i = from; i<=to; i++){
			listToAddTo.add(sortedData[i]);
		}
	}
	
	/** Return absolute values  of an array */
	public static float[] abs(float[] array){
		float[] output = new float[array.length];
		for(int i=0; i<array.length; i++) output[i] = (float)Math.abs(array[i]);
		return output;
	}
	/** Return absolute values  of an array */
	public static int[] abs(int[] array){
		int[] output = new int[array.length];
		for(int i=0; i<array.length; i++) output[i] = Math.abs(array[i]);
		return output;
	}
	/**
	 * Function to get columns of a 2-D matrix.
	 * @param inputMat inputMatrix.
	 * @param indexStart index of the start column (inclusive)
	 * @param indexEnd index of the end column (inclusive)
	 * @return outputMat consisting of the specified columns [indexStart, indexStart+1, ...., indexEnd]
	 */
	public static float[][] column(float[][] inputMat, int indexStart, int indexEnd) {

		if (indexEnd>=inputMat[0].length || indexStart >indexEnd || indexStart<0)
			throw new IllegalArgumentException("Invalid indices");
		float[][] outputMat = new float[inputMat.length][indexEnd-indexStart+1];
		int k;
		for (int i = 0; i<inputMat.length; i++) {
			k=0;
			for (int j = indexStart; j<=indexEnd; j++) {    
				outputMat[i][k] = inputMat[i][j];
				k++;
			}
		}
		return outputMat;
	}

	/**
	 * Function to get one column of a 2D matrix
	 * @param inputMat input matrix.
	 * @param columnIndex index of the column to get
	 * @return outputColumn the specified column
	 */
	public static float[] column(float[][] inputMat, int columnIndex) {

		float[] outputColumn = new float[inputMat.length];

		for (int i = 0; i<inputMat.length; i++) 
			outputColumn[i] = inputMat[i][columnIndex];

		return outputColumn;
	}
	
	public static int[] column(int[][] inputMat, int columnIndex) {

		int[] outputColumn = new int[inputMat.length];

		for (int i = 0; i<inputMat.length; i++) 
			outputColumn[i] = inputMat[i][columnIndex];

		return outputColumn;
	}
	/** Returns -1 if a is less than b, or 1 if a is greater than b, or 0. 
	This is the comparator function for natural order.*/
	public static int compare(int a, int b) {
	  return a < b ? -1 : a > b ? 1 : 0;
	}
	/** Returns -1 if a is less than b, or 1 if a is greater than b, or 0. 
	This is the comparator function for natural order. */

	public static float compare(float a, float b) {
	  return a < b ? -1 : a > b ? 1 : 0;
	}

	/**
	 * Concatenates two arrays <code>a</code> and <code>b</code> into a new array
	 * <code>c</code> such that <code>c = {a,b}</code>.
	 */
	public static float[] concat(float[] a, float[] b) {
		float[] c= new float[a.length+b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}
	public static int[] concat(int[] a, int[] b) {
		int[] c= new int[a.length+b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	/**
	 * Returns an array with of length <code>size</code> with
	 * each element equal to the specified <code>constValue</code>.
	 * @see Arrays.fill 
	 */
	public static float[] constant(float constValue, int size){
		float[] constant = new float[size];
		for(int i=0; i<size; i++) constant[i] = constValue;
		return constant;
	}
	/**
	 * Returns an array with of length <code>size</code> with
	 * each element equal to the specified <code>constValue</code>.
	 * @see Arrays.fill 
	 */
	public static int[] constant(int constValue, int size){
		int[] constant = new int[size];
		for(int i=0; i<size; i++) constant[i] = constValue;
		return constant;
	}

	/** Make a deep copy of a matrix */
	public static float[][] copy(float[][] data){
		int numRows = data.length; int numColumns = data[0].length;
		float[][] newMat = new float[numRows][numColumns];
		for(int i=0; i<numRows; i++){
			System.arraycopy(data[i], 0, newMat[i], 0, numColumns);
		}		
		return newMat;
	}
	/**
	 * Sorts and returns <b>a copy</b> of the input array in ascending order 
	 * @return a sorted copy of the input array.
	 */
	public static float[] copyThenSort(float[] data){
		float[] sortedData = new float[data.length];
		System.arraycopy(data, 0, sortedData, 0, data.length);
		Arrays.sort(sortedData);
		return sortedData;
	}
	
	/** vector cross product: c = a (cross) b */
	public static float[] cross(float[] a, float[] b){
		if(a.length!=3 || b.length!=3) throw new IllegalArgumentException("Inputs to Mat.cross have lengths !=3");
		float[] c = new float[3];
		c[0] = a[1]*b[2] - a[2]*b[1];
		c[1] = a[2]*b[0] - a[0]*b[2];
		c[2] = a[0]*b[1] - a[1]*b[0];
		return c;
	}

	/** returns the determinant of the <b>square</b> input matrix. 
	 * @throws IllegalArgumentException if the matrix is not square. */
	public static float det (float[][] data) {
		return (float)(new LU(data).det());
	}

	/**
	 * Divides all elements of input array by <code>value</code>.
	 * That is, <code> z[i] = x[i] / value</code>.
	 * @throws IllegalArgumentException if <code>value</code> is zero.
	 */
	public static float[] divide(float[] data, float value){
		if(value==0) throw new IllegalArgumentException("Division by zero");
		float[] divided = new float[data.length];
		for(int i=0; i<data.length; i++){
			divided[i] = data[i]/value;
		}
		return divided;
	}
	
	/**
	 * Element by element division such that z[i] = x[i]/y[i]. No checks are
	 * done to see if any y[i] == 0 so make sure you know what
	 * you're doing.
	 */
	public static float[] divide(float[] x, float[] y){
		int size = x.length; Check.equalLengths(size,y);
		float[] divided = new float[size];
		for(int i=0; i<size; i++){
			divided[i] = x[i]/y[i];
		}
		return divided;
	}
	
	/**
	 * Divides each element of the input matrix A by the input value
	 */
	public static float[][] divide(float[][] A, float value){
		if(value==0) throw new IllegalArgumentException("Division by zero");
		int numColumns = A[0].length; int numRows = A.length;
		float[][] C = new float[numRows][numColumns];
		for(int i=0; i<numRows; i++){
			for(int j=0; j<numColumns; j++){
				C[i][j] = A[i][j]/value;
			}
		}
		return C;
	}
	/**
	 * Element by element matrix division such that C_ij = A_ij/B_ij. No checks are
	 * done to see if the elements of B_ij == 0 so make sure you know what
	 * you're doing.
	 */
	public static float[][] dotDivide(float[][] A, float[][] B){
		int numColumns = A[0].length; int numRows = A.length;
		Check.equalSizes(A,B);
		float[][] C = new float[numRows][numColumns];
		for(int i=0; i<numRows; i++){
			for(int j=0; j<numColumns; j++){
				C[i][j] = A[i][j]/B[i][j];
			}
		}
		return C;
	}
	
	/**
	 * Element by element matrix multiplication such that C_ij = A_ij*B_ij. 
	 */
	public static float[][] dotMultiply(float[][] A, float[][] B){
		int numColumns = A[0].length; int numRows = A.length;
		Check.equalSizes(A,B);
		float[][] C = new float[numRows][numColumns];
		for(int i=0; i<numRows; i++){
			for(int j=0; j<numColumns; j++){
				C[i][j] = A[i][j]*B[i][j];
			}
		}
		return C;
	}
	
	/**
	 * dot product of two arrays such that z = sum(a_i*b_i)
	 */
	public static float dotProduct(float[] a, float[] b){
		int ln = a.length; 
		Check.equalLengths(ln,b);
		float sum = 0;
		for(int i=0; i<ln; i++){
			sum+=a[i]*b[i];
		}
		return sum;
	}
	
//	/**
//	 * Returns the real components of the eigenvalues of A. 
//	 */
//	public static float[] eigenvectorReal(float[][] A){
//		return new EigenvalueDecomposition(A).getRealEigenvalues(); 
//	}
//	/**
//	 * Returns the imaginary components of the eigenalues of A. 
//	 */
//	public static float[] eigenvectorComplex(float[][] A){
//		return new EigenvalueDecomposition(A).getImagEigenvalues(); 
//	}
	
	/** returns the identity matrix of the specified <code>dimension</code>*/
	public static float[][] identity(int dimension){
		float[][] ii = new float[dimension][dimension];
		for(int i=0; i<dimension; i++) ii[i][i] = 1.0f;
		return ii;
	}

	/**
	 * Returns the array z[i] = 1/x[i]; If x[i] is zero, it sets z[i] 
	 * to the specified <code>replaceZeroWith</code> value instead.
	 */
	public static float[] inverse(float[] data, float replaceZeroWith){
		float[] inverse = new float[data.length];
		for(int i=0; i<data.length; i++){
			if(data[i]==0)  inverse[i] = replaceZeroWith;
			else inverse[i] = 1/data[i];
		}
		return inverse;
	}
	/** Matrix inverse if A is square, pseudoinverse otherwise
	 * @return inverse(A) if A is square, pseudoinverse otherwise.
	 */
	public static float[][] inverse(float[][] A) {
		int numColumns = A[0].length; int numRows = A.length;
		float[][] inv = copy(A);
		if(numColumns==numRows)
			return Cast.doubleToFloat(new LU(inv).solve(identity(numRows)));
		else 
		    return Cast.doubleToFloat(new QR(inv).solve(identity(numRows)));
	}
	
	// checks if all elements in the array are the same
	public static boolean isConstant(float[] a){
		for(int i=1; i<a.length; i++) {
			if(a[i]!=a[i-1]) return false;
		}
		return true;
	}

	/**
	 * Returns an array with each element equal to the natural logarithm (the base-e logarithm) 
	 * the corresponding input array. 
	 * This function expects the values greater than 0.0; No internal checks are done.
	 */
	public static float[] log(float[] data){
		float[] log = new float[data.length];
		for(int i=0; i<data.length; i++) log[i] = (float)Math.log(data[i]); 
		return log;
	}
	/**
	 * Returns an array with each element equal to the log 10 value (base 10 logarithm) 
	 * of the corresponding input array. 
	 * This function expects the values greater than 0.0; No internal checks are done.
	 */
	public static float[] log10(float[] data){
		float[] log = new float[data.length];
		for(int i=0; i<data.length; i++) log[i] = (float)Math.log10(data[i]); 
		return log;
	}
	/**
	 * Returns an array with each element equal to the log A value 
	 * of the corresponding input array. This is
	 * computed using the property <code>log_A(x) =  log(x) / log(A)</code> where
	 * log is the natural logarithm (base e), and A is the new log base. 
	 * This function expects the values greater than 0.0; No internal checks are done.
	 * @param base the log base. E.g. to get log2, we would use logToBase(data,2).
	 */
	public static float[] logToBase(float[] data, float base){
		float[] log = new float[data.length];
		double logBase = Math.log(base);
		for(int i=0; i<data.length; i++) log[i] = (float)(Math.log(data[i])/logBase); 
		return log;
	}

	/**
	 * Returns the array of (end-start+1) points going from 
	 * <code>start</code> (inclusive) to <code>end</code> (inclusive):  
	 * <code> y[0] = start; y[1] = start+1; ... , y[n] = end;</code>.
	 * <br>
	 * For example, <code>int[] ii = linspace(2,5);</code>
	 * produces the array <code>ii = {2,3,4,5};</code> with length 4.
	 * <pre>
	 * It is similar to this:
	 * <code>
	 * for(int i=0; i<=end-start+1; i++){
	 * <br>&nbsp;&nbsp;&nbsp;	y[i] = i + start;
	 * <br>}
	 * </code>
	 * </pre>
	 * @param start the start index (inclusive)
	 * @param end the end index (inclusive);
	 */
	public static int[] linspace(int start, int end){
		int[] indices = new int[end-start+1];
		for(int i=0; i<end-start+1; i++)  indices[i] = i+start;
		return indices;
	}
	
	/**
	 * @param start the start index (inclusive)
	 * @param end the end index (inclusive);
	 * @param stepSize the stepsize to take
	 * @return array of length num = floor((end-start)/(float)stepSize) (meaning that it could end BEFORE 'end' if you
	 * don't size your stepsize properly.
	 */
	public static int[] linspace(int start, int end, int stepSize){
		int num = (int)Math.abs(Math.floor((end-start)/(float)stepSize)); 
		//System.out.println("Num in Mat.linspace = "+num+", stepsize = "+stepSize);
		int[] indices = new int[num];
		for(int i=0; i<num; i++)  indices[i] = i*(stepSize)+start;
		return indices;
	}
	/**
	 * @param start the start index (inclusive)
	 * @param end the end index (inclusive);
	 * @param stepSize the stepsize to take
	 * @return array of length num = ((end-start)/stepSize) 
	 */
	public static float[] linspace(float start, float end, float stepSize){
		int num = ((int)Math.floor((end+stepSize-start)/stepSize)); 
		//System.out.println("Num in Mat.linspace = "+num+", stepsize = "+stepSize);
		float[] vals = new float[num];
		for(int i=0; i<num; i++)  vals[i] = i*(stepSize)+start;
		return vals;
	}
	
	/** Returns the magnitude of the input array = sqrt(x1^2 + x2^2 + ... + xn^2) */ 
	public static float mag(float[] data){
		double magnitude = data[0]*data[0];
		for(int i=1; i<data.length; i++) magnitude +=data[i]*data[i];
		return (float)Math.sqrt(magnitude);
	}
	


	/**
	 * Convenience function to map a variable from one coordinate space to another.
	 * I.e. 
	 * <code> map(value, low1, high1, low2, high2) = low2 + (value - low1) * ( (high2-low2) /  (high1-low1) )</code>
	 * 
	 * <p> Similar to Processing's "map" function.</p>
	 * 
	 * @return mapped data array
	 */
	public static float map(float data, float low1, float high1, float low2, float high2){
		return low2 + (data-low1) * (high2-low2) / (high1-low1);
	}

	/** 
	 * Similar to map([] value, valueArrayMinimum, valueArrayMaximum, from, to)
	 * @return mapped data array
	 */
	public static float[] map(float[] data, float from, float to){		
		return map( data, Descriptive.min(data), Descriptive.max(data), from, to);
	}

	/**
	 * Function for mapping an input array of floats for plotting
	 * Extends processing "map" function to accomodate
	 * arrays. E.g.  map(value, low1, high1, low2, high2) 
	 * is now
	 * map([] value, low1, high1, low2, high2)
	 * @return mapped data array
	 */
	public static float[] map(float[] data, float low1, float high1, float low2, float high2){
		float[] mappedData = new float[data.length];
		float ratio;
		if( high1 == low1 ){
			System.out.println("Input array range is zero. Returning original array");
			return data; 
		}
		else{
			ratio = (high2-low2)/(high1-low1);
			for(int i=0; i<data.length; i++){
				mappedData[i] =  low2 + (data[i]-low1)*ratio;
			}
			return mappedData;
		}
	}

	/**
	 * Multiplies two matrices A and B returning C = A*B
	 * where C_ij = Sum(k=1 to n) A_ik*B_kj. 
	 * Precondition: Number of columns in A = number of rows in B.
	 */
	public static float[][] multiply(float[][] A, float[][] B){
		int numRowsA = A.length; 
		int numRowsB = B.length; 
		int numColumnsB = B[0].length;
		Check.dimensionsForMultiplication(A[0].length,numRowsB);
		float[][] C = new float[numRowsA][numColumnsB];
		for(int i=0; i<numRowsA; i++){
			for(int j=0; j<numColumnsB; j++){
				double sum=0.0;
				for (int k=0; k<numRowsB; k++) {
					sum+=A[i][k]*B[k][j];
				}
				C[i][j] = (float)sum;
			}
		}
		return C;
	}
	
	/**
	 * Multiplies the matrix A with the vector x returning A*x = y
	 * where  y_j= Sum(k=j to n) A_ij*x_j. 
	 * Precondition: Number of columns in A = number of elements in x.
	 */
	public static float[] multiply(float[][] A, float[] x){
		int numRowsA = A.length; 
		int numRowsX = x.length; 
		Check.dimensionsForMultiplication(A[0].length,numRowsX);
		float[]y = new float[numRowsA];
		for(int i=0; i<numRowsA; i++){
			double sum=0.0;
			for (int j=0; j<numRowsX; j++)  sum+=A[i][j]*x[j];
			y[i] = (float)sum;
		}
		return y;
	}

	/**
	 * Multiplies the matrix A with the scalar a returning a*A = B
	 * where  B_ij = a*A_ij. 
	 */
	public static float[][] multiply(float[][] A, float a){
		int numRows = A.length; 
		int numColumns = A[0].length; 
		float[][]B = new float[numRows][numColumns];
		for(int i=0; i<numRows; i++){
			for(int j=0; j<numColumns; j++){
				B[i][j] = a*A[i][j];
			}
		}
		return B;
	}

	/**
	 * Multiplies two arrays and returns the multiplied array.
	 * That is, <code> z[i] = x[i] * y[i]</code>.
	 */
	public static float[] multiply(float[] data1, float[] data2){
		int size = data1.length;
		Check.equalLengths(size,data2);
		float[] multiplied = new float[size];
		for(int i=0; i<size; i++){
			multiplied[i] = data1[i]*data2[i];
		}
		return multiplied;
	}

	/**
	 * Multiplies each element of an array by a number, and returns the multiplied array.
	 * That is, <code> z[i] = k * x[i] </code>.
	 */
	public static float[] multiply(float[] data, float number){
		float[] multiplied = new float[data.length];

		for(int i=0; i<data.length; i++){
			multiplied[i] = number*data[i];
		}
		return multiplied;
	}

	/** Returns the norm1 of the input matrix, equal to the 
	 * maximum absolute column sum of the matrix. */
	public static float norm1(float[][] data) {
		int numColumns = data[0].length; int numRows = data.length;
		float f = 0;
		// for each column
		for (int j = 0; j < numColumns; j++) {
			float s = 0;
			// for each row
			for (int i = 0; i < numRows; i++) {
				s += Math.abs(data[i][j]);
			}
			f = Math.max(f,s);// update the maximum
		}
		return f;
	}
	/** Returns the norm2 (or maximum singular value) of the input matrix.*/
	public static float norm2(float[][] data) {
		return (float)(new SVD(data).norm2());
	}
	/** Returns the norm2 or magnitude of an array. Similar to {@link mag(float[])}.*/
	public static float norm2(float[] data) {
		return mag(data);
	}

	/** /** Returns the Frobenius norm of the input matrix; 
	 * sqrt of sum of squares of all elements.
	 */
	public static float normF(float[][] data) {
		int numColumns = data[0].length; int numRows = data.length;
		double f = 0;
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				f += data[i][j]*data[i][j];
			}
		}
		return (float)Math.sqrt(f);
	}
	/** Returns the Infinity norm of the input matrix, equal to the
	 * maximum row sum. */
	public static float normInf(float[][] data) {
		int numColumns = data[0].length; int numRows = data.length;
		float f = 0; 
		// each row
		for (int i = 0; i < numRows; i++) {
			float s = 0;
			// each column
			for (int j = 0; j < numColumns; j++) {
				s += Math.abs(data[i][j]);
			}
			f = Math.max(f,s);
		}
		return f;
	}
	/** 
	 * Returns an array with elements consisting of the data 
	 * normalized to the specified min/max; new data goes from 0 to 1. 
	 * @return normalized data array
	 */
	public static float[] normalizeToMinMax(float[] data){
		float min = Descriptive.min(data);
		float range = Descriptive.max(data) - min;
		if(range==0){
			System.out.println("Data range is zero. Returning original array.");
			return data;
		}
		else{
			float[] normalizedDat = new float[data.length];
			for(int i=0; i<data.length; i++) normalizedDat[i] = (data[i] - min)/range;
			return normalizedDat;
		}
	}
	
	/** 
	 * Returns an array with elements given by z[i] = data[i] / sum(data);
	 * @return normalized data array
	 */
	public static float[] normalizeToSum(float[] data){
		float sum = sum(data);
		float[] normalizedDat = new float[data.length];
		for(int i=0; i<data.length; i++) normalizedDat[i] = data[i]/sum;
		return normalizedDat;
	}

	/**
	 * Returns an array with each element corresponding to fullDataset[indices[i]]. 
	 * That is, if the indices.length = n, then
	 * <pre>
	 * y[0] = fullDataset[ indices[0] ];
	 * y[1] = fullDataset[ indices[1] ];
	 * ...
	 * ...
	 * y[n-1] = fullDataset[ indices[n-1] ];
	 * </pre>
	 * @param indices the indices in the fullDataset you want to extract and store.
	 * @param fullDataset the full dataset.
	 */
	public static double[] populate( double[] fullDataset, int[] indices) {
		double[] outputDat = new double[indices.length];
		for (int i=0; i<indices.length; i++) {
			outputDat[i] = fullDataset[indices[i]];
		}
		return outputDat;
	}
	/**
	 * Returns an array with each element corresponding to fullDataset[indices[i]]. 
	 * That is, if the indices.length = n, then
	 * <pre>
	 * y[0] = fullDataset[ indices[0] ];
	 * y[1] = fullDataset[ indices[1] ];
	 * ...
	 * ...
	 * y[n-1] = fullDataset[ indices[n-1] ];
	 * </pre>
	 * @param indices the indices in the fullDataset you want to extract and store.
	 * @param fullDataset the full dataset.
	 */
	public static float[] populate( float[] fullDataset, int[] indices) {
		float[] outputDat = new float[indices.length];
		for (int i=0; i<indices.length; i++) {
			outputDat[i] = fullDataset[indices[i]];
		}
		return outputDat;
	}

	/**
	 * Returns an array with each element corresponding to fullDataset[indices[i]]. 
	 * That is, if the indices.length = n, then
	 * <pre>
	 * y[0] = fullDataset[ indices[0] ];
	 * y[1] = fullDataset[ indices[1] ];
	 * ...
	 * ...
	 * y[n-1] = fullDataset[ indices[n-1] ];
	 * </pre>
	 * @param indices the indices in the fullDataset you want to extract and store.
	 * @param fullDataset the full dataset.
	 */
	public static int[] populate( int[] fullDataset, int[] indices) {
		int[] outputDat = new int[indices.length];
		for (int i=0; i<indices.length; i++) {
			outputDat[i] = fullDataset[indices[i]];
		}
		return outputDat;
	}

	/**
	 * Returns an array with each element corresponding to fullDataset[indices[i]]. 
	 * That is, if the indices.length = n, then
	 * <pre>
	 * y[0] = fullDataset[ indices[0] ];
	 * y[1] = fullDataset[ indices[1] ];
	 * ...
	 * ...
	 * y[n-1] = fullDataset[ indices[n-1] ];
	 * </pre>
	 * @param indices the indices in the fullDataset you want to extract and store.
	 * @param fullDataset the full dataset.
	 */
	public static String[] populate(String[] fullDataset, int[] indices){
		String[] outputDat = new String[indices.length];
		for (int i=0; i<indices.length; i++) {
			outputDat[i] = fullDataset[indices[i]];
		}
		return outputDat;
	}
	/** 
	 * Print the array to the screen in a single line. Similar to <code>print</code>, 
	 * except that the entire array is printed on a single line on the screen and
	 * you can format the number of decimal digits directly instead of using Processing's
	 * <code>nf</code> or one of the other variants of it.
	 * @param data the matrix to be printed.
	 * @param d Number of digits after the decimal to display..
	 */
	public static void print(double[] data, int d) {
		if(d<0) throw new IllegalArgumentException("Can't display "+d+" decimal digits without recreating" +
				"the number system.");
		int numColumns = data.length;
		// number format
		DecimalFormat format = setupFormat(d);
		for (int j=0; j<numColumns; j++) {				
			System.out.print(format.format(data[j])+"\t");
		}
	}
	/** 
	 * Print the matrix to the screen with each row of the
	 * matrix taking up one line. Similar to <code>println</code>, except that
	 * each row of the matrix is printed on a single line on the screen and
	 * you can format the number of decimal digits directly instead of using Processing's
	 * <code>nf</code> or one of the other variants of it.
	 * @param data the matrix to be printed.
	 * @param d Number of digits after the decimal to display..
	 */
	public static void print(double[][] data, int d) {
		if(d<0) throw new IllegalArgumentException("Can't display "+d+" decimal digits without recreating" +
				"the number system.");
		int numRows = data.length;
		// number format
		DecimalFormat format = setupFormat(d);
		for (int i=0; i<numRows; i++) {
			for (int j=0; j<data[i].length; j++) {				
				System.out.print(format.format(data[i][j])+"\t");
			}
			System.out.println();
		}		
	}
	/** 
	 * Print the matrix to the screen with the columns and rows labeled according to the 
	 * input strings. Output resembles what you'd see in a .txt file (assuming the matrix isn't
	 * too large).
	 * @param data the matrix to be printed.
	 * @param d Number of digits after the decimal to display..
	 */
	public static void print(double[][] data, String[] columnLabels, String[] rowLabels, int d) {
		if(d<0) throw new IllegalArgumentException("Can't display "+d+" decimal digits without recreating" +
				"the number system.");
		int numColumns = data[0].length; int numRows = data.length;
		for (int j=0; j<numColumns; j++) {				
			System.out.print("\t"+columnLabels[j]);
		}
		// number format
		DecimalFormat format = setupFormat(d);
		for (int i=0; i<numRows; i++) {
			System.out.print("\n"+rowLabels[i]);
			for (int j=0; j<numColumns; j++) {				
				System.out.print("\t"+format.format(data[i][j]));
			}
		}		
	}
	/** 
	 * Print the array to the screen in a single line. Similar to <code>print</code>, 
	 * except that the entire array is printed on a single line on the screen and
	 * you can format the number of decimal digits directly instead of using Processing's
	 * <code>nf</code> or one of the other variants of it.
	 * @param data the matrix to be printed.
	 * @param d Number of digits after the decimal to display..
	 */
	public static void print(float[] data, int d) {
		if(d<0) throw new IllegalArgumentException("Can't display "+d+" decimal digits without recreating" +
				"the number system.");
		int numColumns = data.length;
		// number format
		DecimalFormat format = setupFormat(d);
		for (int j=0; j<numColumns; j++) {				
			System.out.print(format.format(data[j])+"\t");
		}
	}
	/** 
	 * Print the matrix to the screen with each row of the
	 * matrix taking up one line. Similar to <code>println</code>, except that
	 * each row of the matrix is printed on a single line on the screen and
	 * you can format the number of decimal digits directly instead of using Processing's
	 * <code>nf</code> or one of the other variants of it.
	 * @param data the matrix to be printed.
	 * @param d Number of digits after the decimal to display..
	 */
	public static void print(float[][] data, int d) {
		if(d<0) throw new IllegalArgumentException("Can't display "+d+" decimal digits without recreating" +
				"the number system.");
		int numRows = data.length;
		// number format
		DecimalFormat format = setupFormat(d);
		for (int i=0; i<numRows; i++) {
			for (int j=0; j<data[i].length; j++) {				
				System.out.print(format.format(data[i][j])+"\t");
			}
			System.out.println();
		}		
	}
	/** 
	 * Print the matrix to the screen with the columns and rows labeled according to the 
	 * input strings. Output resembles what you'd see in a .txt file (assuming the matrix isn't
	 * too large).
	 * @param data the matrix to be printed.
	 * @param d Number of digits after the decimal to display..
	 */
	public static void print(float[][] data, String[] columnLabels, String[] rowLabels, int d) {
		if(d<0) throw new IllegalArgumentException("Can't display "+d+" decimal digits without recreating" +
				"the number system.");
		int numColumns = data[0].length; int numRows = data.length;
		for (int j=0; j<numColumns; j++) {				
			System.out.print("\t"+columnLabels[j]);
		}
		// number format
		DecimalFormat format = setupFormat(d);
		for (int i=0; i<numRows; i++) {
			System.out.print("\n"+rowLabels[i]);
			for (int j=0; j<numColumns; j++) {				
				System.out.print("\t"+format.format(data[i][j]));
			}
		}		
	}
	
	/** 
	 * Print the array to the screen in a single line. Similar to <code>print</code>, 
	 * except that the entire array is printed on a single line on the screen and
	 * you can format the number of decimal digits directly instead of using Processing's
	 * <code>nf</code> or one of the other variants of it.
	 * @param data the matrix to be printed.
	 * @param d Number of digits after the decimal to display..
	 */
	public static void print(int[] data, int d) {
		if(d<0) throw new IllegalArgumentException("Can't display "+d+" decimal digits without recreating" +
				"the number system.");
		int numColumns = data.length;
		// number format
		DecimalFormat format = setupFormat(d);
		for (int j=0; j<numColumns; j++) {				
			System.out.print(format.format(data[j])+"\t");
		}
	}
	/** 
	 * Print the matrix to the screen with each row of the
	 * matrix taking up one line. Similar to <code>println</code>, except that
	 * each row of the matrix is printed on a single line on the screen and
	 * you can format the number of decimal digits directly instead of using Processing's
	 * <code>nf</code> or one of the other variants of it.
	 * @param data the matrix to be printed.
	 * @param d Number of digits after the decimal to display..
	 */
	public static void print(int[][] data, int d) {
		if(d<0) throw new IllegalArgumentException("Can't display "+d+" decimal digits without recreating" +
				"the number system.");
		int numRows = data.length;
		// number format
		DecimalFormat format = setupFormat(d);
		for (int i=0; i<numRows; i++) {
			for (int j=0; j<data[i].length; j++) {				
				System.out.print(format.format(data[i][j])+"\t");
			}
			System.out.println();
		}		
	}
	/** 
	 * Print the matrix to the screen with the columns and rows labeled according to the 
	 * input strings. Output resembles what you'd see in a .txt file (assuming the matrix isn't
	 * too large).
	 * @param data the matrix to be printed.
	 * @param d Number of digits after the decimal to display..
	 */
	public static void print(int[][] data, String[] columnLabels, String[] rowLabels, int d) {
		if(d<0) throw new IllegalArgumentException("Can't display "+d+" decimal digits without recreating" +
				"the number system.");
		int numColumns = data[0].length; int numRows = data.length;
		for (int j=0; j<numColumns; j++) {				
			System.out.print("\t"+columnLabels[j]);
		}
		// number format
		DecimalFormat format = setupFormat(d);
		for (int i=0; i<numRows; i++) {
			System.out.print("\n"+rowLabels[i]);
			for (int j=0; j<numColumns; j++) {				
				System.out.print("\t"+format.format(data[i][j]));
			}
		}		
	}

	/** Returns the effective numerical rank (obtained from SVD)
	 * of the input matrix. 
	 * See {@link Rank} for ranking the elements of an input 1D array.*/
	public static int rank (float[][] data) {
		return new SVD(data).rank();
	}

	/**
	 * Returns a new matrix B equal to A, except that all elements in A which are equal to <code>oldValue</code> with
	 * the <code>newValue</code>. Useful, for example, in replacing zeros with
	 * some other number (for {@link dotDivide(float[][], float[][])}), or for wall-street related book-keeping...
	 */
	public static float[][] replace(float[][] A, float oldValue, float newValue){
		int numRows = A.length; 
		int numColumns = A[0].length;
		float[][] C = new float[numRows][numColumns];
		for(int i=0; i<numRows; i++){
			for(int j=0; j<numColumns; j++){
				C[i][j] = (A[i][j]==oldValue ? newValue : A[i][j]);
			}
		}
		return C;
	}
	
	/**
	 * Returns a new array data2 which is similar to data, except that all elements in data
	 *  which are equal to <code>oldValue</code> are replaced with
	 * the <code>newValue</code>. Useful, for example, in replacing zeros with
	 * some other number (for {@link divide(float[], float[])}), or for wall-street related book-keeping...
	 */
	public static float[] replace(float[] data, float oldValue, float newValue){
		int numRows = data.length; 
		float[]C = new float[numRows];
		for(int i=0; i<numRows; i++){
				C[i] = (data[i]==oldValue ? newValue : data[i]);
		}
		return C;
	}
	/** Reshapes a matrix into the new specified dimensions.*/
	public static float[][] reshape(float[][] arr, int numRows, int numColumns){
	  int oldRow = arr.length; int oldColumn = arr[0].length;
	  if(oldRow*oldColumn!=numRows*numColumns) throw new IllegalArgumentException("Mat.reshape error: output has different number of elements than input");
	  float[][] newArr = new float[numRows][numColumns];
	  float count=0;
	  for(int r=0; r<oldRow; r++){
	    for(int c=0; c<oldColumn; c++){
	      int rowNum = (int)Math.floor(count/(numColumns));
	      newArr[rowNum][(int)(count)-rowNum*numColumns] = arr[r][c];
	      count++;
	    }
	    
	  }
	  return newArr;
	}
	
	/**reverses the order of the elements in an array.*/
	public static double[] reverse(double[] data){
		int size = data.length;
		int right=size-1;
		double[] reversed = new double[size];
		for (int left=0; left < size; left++) {
			// exchange the first and last
			reversed[right--]  = data[left]; 
		}
		return reversed;
	}	
	/**reverses the order of the elements in an array.*/
	public static float[] reverse(float[] data){
		int size = data.length;
		int right=size-1;
		float[] reversed = new float[size];
		for (int left=0; left < size; left++) {
			// exchange the first and last
			reversed[right--]  = data[left]; 
		}
		return reversed;
	}	
	/**reverses the order of the elements in an array.*/
	public static int[] reverse(int[] data){
		int size = data.length;
		int right=size-1;
		int[] reversed = new int[size];
		for (int left=0; left < size; left++) {
			// exchange the first and last
			reversed[right--]  = data[left]; 
		}
		return reversed;
	}

	/**
	 * Function for rounding to a given decimal place. 
	 * Convenient for displaying text when Processing's nf,nfc,etc is insufficient.
	 * <i>Only works if the input number has got more decimal places than the
	 * number to round to.</i>
	 * <p>
	 * Examples:
	 * <br>roundToDecimalPlace(1.234567, 2) --> 1.23;
	 * <br>roundToDecimalPlace(100, 2) --> 100;
	 * </p>
	 * @param number number to round.
	 * @param decimal decimal place to round to.
	 * @return number rounded to the specified decimal place
	 */
	public static float roundToDecimalPlace(float number, int decimal) {
		return (float) ( (Math.round((number*Math.pow(10, decimal))))/Math.pow(10, decimal) );
	}
	protected static DecimalFormat setupFormat(int d){
		DecimalFormat format = new DecimalFormat();
		format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(d);
		format.setMinimumFractionDigits(d);
		format.setGroupingUsed(false);
		return format;
	}
	
	/**
	 * Function for flooring a number to the closest interval. Useful for
	 * setting up the tick labels for plotting.
	 * <p>
	 * Examples:
	 * <br>floorToNearest(173.2,5) --> 170; 
	 * <br>floorToNearest(28,10) --> 20;
	 * </p>
	 * @param currentVal number to floor.
	 * @param interval to floor to. E.g. in fives, twos, tens, etc. 
	 * @return number.
	 */
	public static float floorToNearest(float currentVal, int interval){
		return (float)(Math.floor(currentVal/interval)*interval);
	}
	/**
	 * Function for ceiling a number to the closest interval. Useful for
	 * setting up the tick labels for plotting.
	 * <p>
	 * Examples:
	 * <br>ceilToNearest(173.2,5) --> 175; 
	 * <br>ceilToNearest(28,10) --> 30;
	 * </p>
	 * @param currentVal number to ceil.
	 * @param interval to ceil to. E.g. in fives, twos, tens, etc. 
	 * @return number.
	 */
	public static float ceilToNearest(float currentVal, int interval){
		return (float)(Math.ceil(currentVal/interval)*interval);
	}
	
	/**
	 * Splits (partitions) an array into sublists such that each sublist contains the elements with a given range.
	 * <code>splitters=(a,b,c,...,y,z)</code> defines the ranges <code>[-inf,a), [a,b), [b,c), ..., [y,z), [z,inf]</code>.
	 * <p><b>Examples:</b><br>
	 * <ul>
	 * <code>data = (1,2,3,4,5,8,8,8,10,11)</code>.
	 * <br><code>splitters=(2,8)</code> yields 3 bins: <code>(1), (2,3,4,5) (8,8,8,10,11)</code>.
	 * <br><code>splitters=()</code> yields 1 bin: <code>(1,2,3,4,5,8,8,8,10,11)</code>.
	 * <br><code>splitters=(-5)</code> yields 2 bins: <code>(), (1,2,3,4,5,8,8,8,10,11)</code>.
	 * <br><code>splitters=(100)</code> yields 2 bins: <code>(1,2,3,4,5,8,8,8,10,11), ()</code>.
	 * </ul>
	 * @param sortedList the list to be partitioned (<b>must be sorted ascending</b>).
	 * @param splitters the points at which the list shall be partitioned (<b>must be sorted ascending</b>).
	 * @return the sublists (an array with <code>length == splitters.length + 1</code>.
	 * Each sublist is returned sorted ascending.
	 */
	public static ArrayList[] split(float[] sortedList, float[] splitters) {
		// assertion: data is sorted ascending.
		// assertion: splitValues is sorted ascending.
		int numBins = splitters.length + 1;

		ArrayList[] bins = new ArrayList[numBins];
		for (int i=numBins; --i >= 0;) bins[i] = new ArrayList();

		int listSize = sortedList.length;
		int nextStart = 0;
		int i=0;
		while (nextStart < listSize && i < numBins-1) {
			float splitValue = splitters[i];
			int index = Arrays.binarySearch( sortedList,splitValue );
			if (index < 0) { // splitValue not found
				int insertionPosition = -index - 1;			
				addAllOfFromTo(bins[i], sortedList, nextStart,insertionPosition-1);
				nextStart = insertionPosition;
			}
			else { // splitValue found
				// For multiple identical elements ("runs"), binarySearch does not define which of all valid indexes is returned.
				// Thus, skip over to the first element of a run.
				do {
					index--;
				} while (index >= 0 && sortedList[index] == splitValue);

				addAllOfFromTo(bins[i], sortedList, nextStart, index);
				nextStart = index + 1;
			}
			i++;
		}
		// now fill the remainder
		addAllOfFromTo(bins[numBins-1] , sortedList, nextStart,sortedList.length-1);
		return bins;
	}

	/**
	 * Get a (2D) submatrix of a (2D) matrix (a copy, not a pointer).
	 * @param inputMat input matrix
	 * @param rowStart row to start at (inclusive)
	 * @param rowEnd row to end at (inclusive)
	 * @param columnStart column to start at (inclusive)
	 * @param columnEnd column to end at (inclusive)
	 * @return submatrix containing (rowEnd-rowStart+1) rows and (columnEnd-columnStart+1) columns; 
	 * copy of <code>A(rowStart:rowEnd, columnStart:columnEnd)</code>.
	 * @throws IllegalArgumentException if the rowStart, rowEnd, columnStart, or columnEnd parameters 
	 * are not within the inputMatrix size bounds.
	 */
	public static float[][] subMatrix(float[][] inputMat, int rowStart, int rowEnd, int columnStart, int columnEnd) {
		int numRows = rowEnd-rowStart+1;
		int numColumns = columnEnd-columnStart+1;
		if(numRows>inputMat.length || rowStart<0 || rowEnd>inputMat.length-1) throw new IllegalArgumentException("Row indices are not within the matrix bounds");
		if(numColumns>inputMat[0].length || columnStart<0 || columnEnd>inputMat[0].length-1) throw new IllegalArgumentException("Column indices are not within the matrix bounds");
		float[][] outputMat = new float[numRows][numColumns];

		for (int i = 0; i<numRows; i++) {
			for(int j = 0; j<numColumns; j++){
				outputMat[i][j] = inputMat[i+rowStart][j+columnStart];
			}
		}
		return outputMat;
	}
	/**
	 * Function to get a (2D) submatrix of a (2D) matrix (a copy, not a pointer).
	 * @param inputMat input matrix
	 * @param rowIndices the row indices
	 * @param columnStart column to start at (inclusive)
	 * @param columnEnd column to end at (inclusive)
	 * @return the submatrix containing a copy of the input matrix's specified rows from
	 * columnStart to columnEnd; copy of <code>A(rowIndices(:), columnStart:columnEnd)</code>.
	 * @throws IllegalArgumentException if the inputs are not within the inputMatrix size bounds.
	 */
	public static float[][] subMatrix(float[][] inputMat, int[] rowIndices, int columnStart, int columnEnd) {
		int numRows = rowIndices.length;
		int numColumns = columnEnd-columnStart+1;
		if(numRows>inputMat.length) throw new IllegalArgumentException("Row indices are not within the matrix bounds");
		if(numColumns>inputMat[0].length || columnStart<0 || columnEnd>inputMat[0].length-1) throw new IllegalArgumentException("Column indices are not within the matrix bounds");
		float[][] outputMat = new float[numRows][numColumns];
		for (int i = 0; i<numRows; i++) {
			for(int j = 0; j<numColumns; j++){
				outputMat[i][j] = inputMat[rowIndices[i]][j+columnStart];
			}
		}
		return outputMat;
	}
	/**
	 * Function to get a (2D) submatrix of a (2D) matrix (a copy, not a pointer).
	 * @param inputMat input matrix
	 * @param rowStart row to start at (inclusive)
	 * @param rowEnd column to end at (inclusive)
	 * @param columnIndices the column indices
	 * @return the submatrix containing the specified rows, and going from the original matrices
	 * rowStart to rowEnd columns; copy of <code>A(rowIndices(:), columnStart:columnEnd)</code>.
	 * @throws IllegalArgumentException if the inputs are not within the inputMatrix size bounds.
	 */
	public static float[][] subMatrix(float[][] inputMat, int rowStart, int rowEnd, int[] columnIndices) {
		int numRows = rowEnd-rowStart+1;
		int numColumns = columnIndices.length;
		if(numRows>inputMat.length || rowStart<0 || rowEnd>inputMat.length-1) throw new IllegalArgumentException("Row indices are not within the matrix bounds");
		if(numColumns>inputMat[0].length) throw new IllegalArgumentException("Column indices are not within the matrix bounds");
		float[][] outputMat = new float[numRows][numColumns];
		for (int i = 0; i<numRows; i++) {
			for(int j = 0; j<numColumns; j++){
				outputMat[i][j] = inputMat[i+rowStart][columnIndices[j]];
			}
		}
		return outputMat;
	}
	/**
	 * Function to get a (2D) submatrix of a (2D) matrix (a copy, not a pointer).
	 * @param inputMat input matrix
	 * @param rowIndices the row indices
	 * @param columnIndices the column indices
	 * @return the submatrix containing the specified rows, and going from the original matrices
	 * rowStart to rowEnd columns; copy of <code>A(rowIndices(:), columnStart:columnEnd)</code>.
	 * @throws IllegalArgumentException if the inputs are not within the inputMatrix size bounds.
	 */
	public static float[][] subMatrix(float[][] inputMat, int[] rowIndices, int[] columnIndices) {
		int numRows = rowIndices.length;
		int numColumns = columnIndices.length;
		if(numRows>inputMat.length) 
			throw new IllegalArgumentException("Row indices are not within the matrix bounds. Num Rows = "+numRows+", inputMat.length = "+inputMat.length);
		if(numColumns>inputMat[0].length) 
			throw new IllegalArgumentException("Column indices are not within the matrix bounds. Num Columns = "+numColumns+", inputMat[0].length = "+inputMat[0].length);
		float[][] outputMat = new float[numRows][numColumns];
		for (int i = 0; i<numRows; i++) {
			for(int j = 0; j<numColumns; j++){
				outputMat[i][j] = inputMat[rowIndices[i]][columnIndices[j]];
			}
		}
		return outputMat;
	}
	
	public static int[][] subMatrix(int[][] inputMat, int[] rowIndices, int[] columnIndices) {
		int numRows = rowIndices.length;
		int numColumns = columnIndices.length;
		if(numRows>inputMat.length) 
			throw new IllegalArgumentException("Row indices are not within the matrix bounds. Num Rows = "+numRows+", inputMat.length = "+inputMat.length);
		if(numColumns>inputMat[0].length) 
			throw new IllegalArgumentException("Column indices are not within the matrix bounds. Num Columns = "+numColumns+", inputMat[0].length = "+inputMat[0].length);
		int[][] outputMat = new int[numRows][numColumns];
		for (int i = 0; i<numRows; i++) {
			for(int j = 0; j<numColumns; j++){
				outputMat[i][j] = inputMat[rowIndices[i]][columnIndices[j]];
			}
		}
		return outputMat;
	}
	
	/**
	 * Returns the array z = x-y where each element of z[i] = x[i] - y[i]
	 */
	public static float[] subtract(float[] x, float[] y) {
		int size = x.length;
		Check.equalLengths(size,y);
		float[] z = new float[size];		
		for (int i = 0; i < size; i++)  z[i] = x[i] - y[i];
		return z;
	}
	
	/**
	 * Returns the matrix z = x-y where each element of z[i][j] = x[i][j] - y[i][j]
	 */
	public static float[][] subtract(float[][] x, float[][] y) {
		int numRows = x.length, numColumns = x[0].length;
		//Check.equalLengths(numRows,y.length);
		float[][] z = new float[numRows][];		
		for (int i = 0; i < numRows; i++) {
			z[i] = subtract(x[i],y[i]);
		}
		return z;
	}
	
	/**
	 * Returns the sum of a data sequence.
	 * That is <tt>Sum( data[i] )</tt>.
	 */
	public static float sum(float[] data) {
		float sum = data[0];		
		for (int i = 1; i < data.length; i++) sum += data[i];	
		return sum;
		//return sumOfPowerDeviations(data,1,0.0f);
	}
	/**
	 * Returns the sum of two data sequences.
	 * That is <tt>Sum( data[i] )</tt>.
	 */
	public static float[] sum(float[] data1, float[] data2) {
		int size = data1.length;
		Check.equalLengths(size,data2);
		float[] sum = new float[size];		
		for (int i = 0; i < data1.length; i++)  sum[i] = data1[i] + data2[i];
		return sum;
	}

	/**
	 * Sum of an array with a number. That is
	 * <tt>Sum( data[i]  + number)</tt>.
	 * 
	 * @return array with each element equal to <tt> data[i] + number</tt>
	 */
	public static float[] sum(float[] data, float number) {		
		float[] sum = new float[data.length];		
		for (int i = 0; i < data.length; i++) sum[i] = data[i] + number;
		return sum;
	}	

	/**
	 * Returns the sum of a data sequence.
	 * That is <tt>Sum( data[i] )</tt>.
	 */
	public static int sum(int[] data) {
		int sum = data[0];		
		for (int i = 1; i < data.length; i++) sum += data[i];	
		return sum;
		//return sumOfPowerDeviations(data,1,0.0f);
	}
	/**
	 * Returns the sum of two data sequences.
	 * That is <tt>Sum( data[i] )</tt>.
	 */
	public static int[] sum(int[] data1, int[] data2) {
		int size = data1.length;
		Check.equalLengths(size,data2);
		int[] sum = new int[size];	
		for (int i = 0; i < data1.length; i++)  sum[i] = data1[i] + data2[i];
		return sum;
	}

	/**
	 * Sum of an array with a number. That is
	 * <tt>Sum( data[i]  + number)</tt>.
	 * 
	 * @return array with each element equal to <tt> data[i] + number</tt>
	 */
	public static int[] sum(int[] data,int number) {
		int[] sum = new int[data.length];		
		for (int i = 0; i < data.length; i++) sum[i] = data[i] + number;
		return sum;
	}	

	/**
	 * Returns the matrix z = x+y where each element of z[i][j] = x[i][j] + y[i][j]
	 */
	public static float[][] sum(float[][] x, float[][] y) {
		int numRows = x.length, numColumns = x[0].length;
		//Check.equalLengths(numRows,y.length);
		float[][] z = new float[numRows][];		
		for (int i = 0; i < numRows; i++) {
			z[i] = sum(x[i],y[i]);
		}
		return z;
	}
	/**
	 * Swap function that swaps  the values in an array
	 * @param data is an array of floats
	 * @param oldIndex
	 * @param newIndex
	 * e.g. data[newIndex,oldIndex] = data[oldIndex,newIndex] 
	 */
	public static void swap(float[] data, int oldIndex, int newIndex){
		float tmp = data[oldIndex];
		data[oldIndex] = data[newIndex];
		data[newIndex] = tmp;
	}
	/**
	 * Swap function that swaps the values in an array (overload function)
	 * @param data is an array of integers
	 * @param oldIndex
	 * @param newIndex
	 * e.g. data[newIndex,oldIndex] = data[oldIndex,newIndex] 
	 */
	public static void swap(int[] data, int oldIndex, int newIndex){
		int tmp = data[oldIndex];
		data[oldIndex] = data[newIndex];
		data[newIndex] = tmp;
	}

	/** Returns the symmetric part of the input matrix ((A+A')/2).*/
	public static float[][] symmetric(float[][] data ){
		Check.square(data);
		int size = data.length;
		float[][] symm = new float[size][size];
		for(int c=0; c<size; c++){
			symm[c][c] = data[c][c];
			for(int r=c+1; r<size; r++){
				symm[c][r] = (data[c][r] + data[r][c])/2;
				symm[r][c] = symm[c][r];
			}
		}
		return symm;
	}
	/** Returns the skew symmetric part of the input matrix ((A-A')/2).*/
	public static float[][] skewSymmetric(float[][] data ){
		Check.square(data);
		int size = data.length;
		float[][] skew = new float[size][size];
		for(int c=0; c<size; c++){
			skew[c][c] = 0;
			for(int r=c+1; r<size; r++){
				skew[c][r] = (data[c][r] - data[r][c])/2;
				skew[r][c] = -skew[c][r];
			}
		}
		return skew;
	}
	
	/** Returns the matrix trace or sum of the diagonal elements. */
	public static float trace (float[][]data) {
		double t = 0;
		for (int i = 0; i < Math.min(data.length,data[0].length); i++) {
			t += data[i][i];
		}
		return (float)t;
	}
	/** Returns the transpose of the input matrix.*/
	public static float[][] transpose(float[][] data ){
		int numColumns = data[0].length;
		int numRows = data.length;
		float[][] mTranspose = new float[numColumns][numRows];
		for(int c=0; c<numColumns; c++){
			for(int r=0; r<numRows; r++){
				float val1 = data[r][c]; 
				mTranspose[c][r] = val1;	
			}
		}
		return mTranspose;
	}
	/**
	 * @return the transpose of the input matrix
	 */
	public static int[][] transpose(int[][] data ){
		int numColumns = data[0].length;
		int numRows = data.length;
		int[][] mTranspose = new int[numColumns][numRows];
		for(int c=0; c<numColumns; c++){
			for(int r=0; r<numRows; r++){
				mTranspose[c][r] = data[r][c];
			}
		}
		return mTranspose;
	}

	/**
	 * Checks to see if a number is in the range specified by [min,max].
	 * Returns true if <tt> min &le; number &le; max)</tt>.
	 * @param min left-most limit (inclusive)
	 * @param max right-most limit (inclusive)
	 * @param number the value to check for.
	 * @return true if the number is within the bounds, else false
	 */
	public static boolean within(float number, float min, float max) {
		if (number>=min && number<=max) return true;
		else return false;
	}
	
	/**
	 * returns an array containing all numbers within the min and max (inclusive).
	 * If nothing is within the min and max, an array of length 1 with the first element = -1 is returned
	 */
	public static float[] within(float[] numbers, float min, float max) {
		ArrayList<Float> arrayWithin = new ArrayList<Float>();
		for(int i=0; i<numbers.length; i++){
			if(within(numbers[i],min,max)) arrayWithin.add(numbers[i]);
		}
		if(arrayWithin.size()>0) return Cast.arrayListToFloat(arrayWithin);
		else return new float[]{-1};
	}

}