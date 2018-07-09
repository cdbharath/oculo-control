/**
 * papaya: A collection of utilities for Statistics and Matrix-related manipulations
 * http://adilapapaya.com/papayastatistics/, 1.1.0
 * Created by Adila Faruk, http://adilapapaya.com, May 2012, Last Updated April 2014
 *
 *
 * Copyright (C) 2014 Adila Faruk http://adilapapaya.com 
 * Copyright 1999 CERN - European Organization for Nuclear Research.
 * Copyright 1995 Stephen L.Moshier.
 * 
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
 * Note:
 * Some of this code was adapted from the CERN
 * Jet Java libraries, which in turn was adapted from the Cephes Mathematical Library. As far
 * as I can tell, Stephen L. Moshier wrote the original C++ code for CEPHES (1989, moshier@na-net.ornl.gov), 
 * Gedeck (at Novartis) and Wolfgang Hoschek (at CERN) then adapted it for the 
 * Java platform, making some significant changes along the way. 
 * <br>
 * 2012 rolls along, and Adila (hello!) finds the library and modifies it for Processing, adding a 
 * number of new methods and updating some of the present ones, all while talking about 
 * herself in the third person. :)
 */

package papaya;
import java.util.*;
import java.io.*;
import processing.core.*;

/**
 * Basic descriptive statistics class for exploratory data analysis. 
 * Methods for computing Correlations and Covariances are in the {@link Correlation}
 * class. Where appropriate, methods with similar functions are grouped into
 * static subclasses. For example, the {@link Sum} class contains the following
 * methods:
 * <ul>
 * <li>{@link Sum#inversions}
 * <li>{@link Sum#logs}
 * <li>{@link Sum#products}
 * <li>{@link Sum#powerDeviations}
 * <li>{@link Sum#powers}
 * <li>{@link Sum#squaredDeviations}
 * <li>{@link Sum#squares}
 * <li>{@link Sum#sum}
 * </ul>
 * while the {@link Mean} class has the
 * <ul>
 * <li>{@link Mean#columnMean}
 * <li>{@link Mean#rowMean}
 * <li>{@link Mean#arithmetic}
 * <li>{@link Mean#geometric}
 * <li>{@link Mean#harmonic}
 * <li>{@link Mean#trimmed}
 * </ul>
 * utilities.
 */
public class Descriptive extends Object {
	/**
	 * Makes this class non instantiable, but still let's others inherit from it.
	 */
	protected Descriptive() {}
	
	/**
	 * Checks if the given range is within the contained array's bounds.
	 * @throws IndexOutOfBoundsException if <code>to!=from-1 || from&lt;0 || from&gt;to || to&gt;=size</code>.
	 */
	protected static void checkRangeFromTo(int from, int to, int theSize) {
		if (to==from-1) return;
		if (from<0 || from>to || to>=theSize)
			throw new IndexOutOfBoundsException("from: "+from+", to: "+to+", size="+theSize);
	}
	/**
	 * 
	 * Computes the frequency (number of occurances, count) of each distinct value in the given <b>sorted data</b>.
	 * After this call, both <code>distinctValues</code> and <code>frequencies</code> have a new size (which is equal for both), 
	 * which is the number of distinct values in the sorted data.
	 * <p>
	 * Distinct values are filled into <code>distinctValues</code>, starting at index 0.
	 * The frequency of each distinct value is filled into <code>frequencies</code>, starting at index 0.
	 * As a result, the smallest distinct value (and its frequency) can be found at index 0, 
	 * the second smallest distinct value (and its frequency) at index 1, ..., 
	 * the largest distinct value (and its frequency) at index <code>distinctValues.size()-1</code>.
	 * </p>
	 * <b>Example:</b>
	 * </br>
	 * <code>sortedData = (5,6,6,7,8,8) --> distinctValues = (5,6,7,8), frequencies = (1,2,1,2)</code>
	 * </br></br>
	 * Code-wise, you would write:
	 * <pre> 
	 * ArrayList<Float> distinctValues = new ArrayList<Float>(); 
	 * 
	 * ArrayList<Integer> frequencies = new ArrayList<Integer>();  
	 * 
	 * <code>frequencies(sortedData,distinctValues,frequencies);</code>
	 * </pre>
	 *
	 * @param sortedData the data; must be sorted ascending.
	 * @param distinctValues a ArrayList to be filled with the distinct values; can have any size.
	 * @param frequencies a ArrayList to be filled with the frequencies; can have any size; 
	 * set this parameter to <code>null</code> to ignore it.
	 * 
	 * @see Frequency 
	 * @see Unique 
	 */
	public static void frequencies(float[] sortedData, ArrayList<Float> distinctValues, ArrayList<Integer> frequencies) {
		distinctValues.clear();
		if (frequencies!=null) frequencies.clear();

		int size = sortedData.length;
		int i=0;

		while (i<size) {
			float element = sortedData[i];
			int cursor = i;

			// determine run length (number of equal elements)
			while (++i < size  &&  sortedData[i]==element);

			int runLength = i - cursor;
			distinctValues.add(element);
			if (frequencies!=null) frequencies.add(runLength);
		}
	}
	
	/**Contains methods related to incrementally updating 
	 * previously computed variables such as the  , sum of squares, and sum of
	 * powers.
	 */
	private static class IncrUpdate{
		protected IncrUpdate(){}
		
		/**
		 * Incrementally maintains and updates minimum, maximum, sum and sum of squares of a data sequence.
		 *
		 * Assume we have already recorded some data sequence elements 
		 * and know their minimum, maximum, sum and sum of squares.
		 * Assume further, we are to record some more elements 
		 * and to derive updated values of minimum, maximum, sum and sum of squares.
		 * <p>
		 * This method computes those updated values without needing to know the already recorded elements.
		 * This is interesting for interactive online monitoring and/or applications that cannot keep the entire huge data sequence in memory.
		 * <p>
		 * <br>Definition of sumOfSquares: <code>sumOfSquares(data) = Sum ( data[i] * data[i] )</code>.
		 *
		 * @param data the additional elements to be incorporated into min, max, etc.
		 * @param from the index of the first element within <code>data</code> to consider.
		 * @param to the index of the last element within <code>data</code> to consider.
		 * The method incorporates elements <code>data[from], ..., data[to]</code>.
		 * @param inOut the old values in the following format:
		 * <ul>
		 * <li><code>inOut[0]</code> is the old minimum.
		 * <li><code>inOut[1]</code> is the old maximum.
		 * <li><code>inOut[2]</code> is the old sum.
		 * <li><code>inOut[3]</code> is the old sum of squares.
		 * </ul>
		 * If no data sequence elements have so far been recorded set the values as follows 
		 * <ul>
		 * <li><code>inOut[0] = Float.POSITIVE_INFINITY</code> as the old minimum.
		 * <li><code>inOut[1] = Float.NEGATIVE_INFINITY</code> as the old maximum.
		 * <li><code>inOut[2] = 0.0</code> as the old sum.
		 * <li><code>inOut[3] = 0.0</code> as the old sum of squares.
		 * </ul>
		 * <p> 
		 * @return the updated values filled into the <code>inOut</code> array.
		 */

		public static void sumOfSquares(float[] data, int from, int to, double[] inOut) {
			checkRangeFromTo(from,to,data.length);

			// read current values
			double min = inOut[0];
			double max = inOut[1];
			double sum = inOut[2];
			double sumSquares = inOut[3];

			for (int i=from; i<=to; i++) {
				float element = data[i];
				sum += element;
				sumSquares += element*element;
				if (element < min) min = element;
				if (element > max) max = element;

				/*
			double oldDeviation = element - mean;
			mean += oldDeviation / (N+1);
			sumSquaredDeviations += (element-mean)*oldDeviation; // cool, huh?
				 */

				/*
			double oldMean = mean;
			mean += (element - mean)/(N+1);
			if (N > 0) {
				sumSquaredDeviations += (element-mean)*(element-oldMean); // cool, huh?
			}
				 */

			}

			// store new values
			inOut[0] = min;
			inOut[1] = max;
			inOut[2] = sum;
			inOut[3] = sumSquares;

			// At this point of return the following postcondition holds:
			// data.length-from elements have been consumed by this call.
		}
		
		/**
		 * Incrementally maintains and updates various sums of powers of the form <code>Sum(data[i]<sup>k</sup>)</code>.
		 *
		 * Assume we have already recorded some data sequence elements <code>data[i]</code>
		 * and know the values of <code>Sum(data[i]<sup>from</sup>), Sum(data[i]<sup>from+1</sup>), ..., Sum(data[i]<sup>to</sup>)</code>.
		 * Assume further, we are to record some more elements 
		 * and to derive updated values of these sums.
		 * <p>
		 * This method computes those updated values without needing to know the already recorded elements.
		 * This is interesting for interactive online monitoring and/or applications that cannot keep the entire huge data sequence in memory.
		 * For example, the incremental computation of moments is based upon such sums of powers:
		 * <p>
		 * The moment of <code>k</code>-th order with constant <code>c</code> of a data sequence,
		 * is given by <code>Sum( (data[i]-c)<sup>k</sup> ) / data.size()</code>.
		 * It can incrementally be computed by using the equivalent formula
		 * <p>
		 * <code>moment(k,c) = m(k,c) / data.size()</code> where
		 * <br><code>m(k,c) = Sum( -1<sup>i</sup> * b(k,i) * c<sup>i</sup> * sumOfPowers(k-i))</code> for <code>i = 0 .. k</code> and
		 * <br><code>b(k,i) = </code>{@link cern.jet.math.Arithmetic#binomial(long,long) binomial(k,i)} and
		 * <br><code>sumOfPowers(k) = Sum( data[i]<sup>k</sup> )</code>.
		 * <p>
		 * @param data the additional elements to be incorporated into min, max, etc.
		 * @param from the index of the first element within <code>data</code> to consider.
		 * @param to the index of the last element within <code>data</code> to consider.
		 * The method incorporates elements <code>data[from], ..., data[to]</code>.
		 * 
		 * @param inOut the old values of the sums in the following format:
		 * <ul>
		 * <li><code>sumOfPowers[0]</code> is the old <code>Sum(data[i]<sup>fromSumIndex</sup>)</code>.
		 * <li><code>sumOfPowers[1]</code> is the old <code>Sum(data[i]<sup>fromSumIndex+1</sup>)</code>.
		 * <li>...
		 * <li><code>sumOfPowers[toSumIndex-fromSumIndex]</code> is the old <code>Sum(data[i]<sup>toSumIndex</sup>)</code>.
		 * </ul>
		 * If no data sequence elements have so far been recorded set all old values of the sums to <code>0.0</code>.
		 *
		 * @return the updated values filled into the <code>sumOfPowers</code> array.
		 */
		public static void sumOfPowers(float[] data, int from, int to, int fromSumIndex, int toSumIndex, double[] sumOfPowers) {
			int size = data.length;
			int lastIndex = toSumIndex - fromSumIndex;
			if (from > size || lastIndex+1 > sumOfPowers.length) throw new IllegalArgumentException();

			// optimized for common parameters
			if (fromSumIndex==1) { // handle quicker
				if (toSumIndex==2) { 
					double sum = sumOfPowers[0];
					double sumSquares = sumOfPowers[1];
					for (int i=from-1; ++i<=to; ) {
						float element = data[i];
						sum += element;
						sumSquares += element*element;
						//if (element < min) min = element;
						//else if (element > max) max = element;
					}
					sumOfPowers[0] += sum;
					sumOfPowers[1] += sumSquares;
					return;
				}
				else if (toSumIndex==3) { 
					double sum = sumOfPowers[0];
					double sumSquares = sumOfPowers[1];
					double sum_xxx = sumOfPowers[2];
					for (int i=from-1; ++i<=to; ) {
						float element = data[i];
						sum += element;
						sumSquares += element*element;
						sum_xxx += element*element*element;
						//if (element < min) min = element;
						//else if (element > max) max = element;
					}
					sumOfPowers[0] += sum;
					sumOfPowers[1] += sumSquares;
					sumOfPowers[2] += sum_xxx;
					return;
				}
				else if (toSumIndex==4) { // handle quicker
					double sum = sumOfPowers[0];
					double sumSquares = sumOfPowers[1];
					double sum_xxx = sumOfPowers[2];
					double sum_xxxx = sumOfPowers[3];
					for (int i=from-1; ++i<=to; ) {
						float element = data[i];
						sum += element;
						sumSquares += element*element;
						sum_xxx += element*element*element;
						sum_xxxx += element*element*element*element;
						//if (element < min) min = element;
						//else if (element > max) max = element;
					}
					sumOfPowers[0] += sum;
					sumOfPowers[1] += sumSquares;
					sumOfPowers[2] += sum_xxx;
					sumOfPowers[3] += sum_xxxx;
					return;
				}
			}

			if (fromSumIndex==toSumIndex || (fromSumIndex >= -1 && toSumIndex <= 5)) { // handle quicker
				for (int i=fromSumIndex; i<=toSumIndex; i++) {
					sumOfPowers[i-fromSumIndex] += Sum.powerDeviations(data,i,0.0f,from,to);
				}
				return;
			}
			// now the most general case:
			// optimized for maximum speed, but still not quite quick
			for (int i=from-1; ++i<=to; ) {
				float element = data[i];
				double pow = Math.pow(element,fromSumIndex);

				int j=0;
				for (int m=lastIndex; --m >= 0; ) {
					sumOfPowers[j++] += pow;
					pow *= element;
				}
				sumOfPowers[j] += pow;
			}	
			// At this point of return the following postcondition holds:
			// data.size()-fromIndex elements have been consumed by this call.
		}
		/**
		 * Incrementally maintains and updates sum and sum of squares of a <i>weighted</i> data sequence.
		 *
		 * Assume we have already recorded some data sequence elements 
		 * and know their sum and sum of squares.
		 * Assume further, we are to record some more elements 
		 * and to derive updated values of sum and sum of squares.
		 * <p>
		 * This method computes those updated values without needing to know the already recorded elements.
		 * This is interesting for interactive online monitoring and/or applications that cannot keep the entire huge data sequence in memory.
		 * <p>
		 * <br>Definition of sum: <code>sum = Sum ( data[i] * weights[i] )</code>.
		 * <br>Definition of sumOfSquares: <code>sumOfSquares = Sum ( data[i] * data[i] * weights[i])</code>.
		 *
		 *
		 * @param data the additional elements to be incorporated into min, max, etc.
		 * @param weights the weight of each element within <code>data</code>.
		 * @param from the index of the first element within <code>data</code> (and <code>weights</code>) to consider.
		 * @param to the index of the last element within <code>data</code> (and <code>weights</code>) to consider.
		 * The method incorporates elements <code>data[from], ..., data[to]</code>.
		 * @param inOut the old values in the following format:
		 * <ul>
		 * <li><code>inOut[0]</code> is the old sum.
		 * <li><code>inOut[1]</code> is the old sum of squares.
		 * </ul>
		 * If no data sequence elements have so far been recorded set the values as follows 
		 * <ul>
		 * <li><code>inOut[0] = 0.0</code> as the old sum.
		 * <li><code>inOut[1] = 0.0</code> as the old sum of squares.
		 * </ul>
		 *
		 * @return the updated values filled into the <code>inOut</code> array.
		 */
		public static void sumOfSquaresWeighted(float[] data, float[] weights, int from, int to, float[] inOut) {
			int dataSize = data.length;
			checkRangeFromTo(from,to,dataSize);
			if (dataSize != weights.length) throw new IllegalArgumentException("from="+from+", to="+to+", data.length="+dataSize+", weights.length="+weights.length);

			// read current values
			float sum = inOut[0];
			float sumOfSquares = inOut[1];

			for (int i=from-1; ++i<=to; ) {
				float element = data[i];
				float weight = weights[i];
				float prod = element*weight;

				sum += prod;
				sumOfSquares += element * prod;
			}

			// store new values
			inOut[0] = sum;
			inOut[1] = sumOfSquares;

			// At this point of return the following postcondition holds:
			// data.size()-from elements have been consumed by this call.
		}
		
	}// end IncrUpdate class
	/**
	 * Returns the kurtosis (aka excess) of a data sequence.
	 * @param moment4 the fourth central moment, which is <code>moment(data,4,mean)</code>.
	 * @param standardDeviation the standardDeviation.
	 */
	public static float kurtosis(float moment4, float standardDeviation) {
		return -3 + moment4 / (standardDeviation * standardDeviation * standardDeviation * standardDeviation);
	}
	/**
	 * Returns the kurtosis (aka excess) of a data sequence, which is 
	 * <code>-3 + moment(data,4,mean) / standardDeviation<sup>4</sup></code>.
	 */
	public static float kurtosis(float[] data, float mean, float standardDeviation) {
		return kurtosis(moment(data,4,mean), standardDeviation);
	}
	
	public static  int max(int a, int b) {
		return (a > b) ? a : b;
	}

	public static  float max(float a, float b) {
		return (a > b) ? a : b;
	}


	public static  double max(double a, double b) {
		return (a > b) ? a : b;
	}
	  
		  
	/**
	 * Returns the largest member of a data sequence.
	 */
	public static double max(double[] data) {
		int size = data.length;
		Check.size0(size);
		double max = data[size-1];
		for (int i = size-1; --i >= 0;) {
			if (data[i] > max) max = data[i];
		}
		return max;
	}
	/**
	 * Returns the largest member of a data sequence.
	 */
	public static float max(float[] data) {
		int size = data.length;
		Check.size0(size);
		float max = data[0];
		for (int i = 0; i<size; i++) {
			max = max(max,data[i]);
		}
		return max;
	}
	/**
	 * Returns the largest member of a data sequence.
	 */
	public static int max(int[] data) {
		int size = data.length;
		Check.size0(size);
		int max = data[0];
		for (int i = 1; i<size; i++) {
			max = max(max,data[i]);
		}
		return max;
	}
	/**
	 * Returns the largest member of a matrix.
	 */
	public static float max(float[][] data) {
		int size = data.length;
		float max = max(data[0]);
		for (int i = 0; i<size; i++) {
			max = max(max, max(data[i]));
		}
		return max;
	}
//	/**
//	 * Returns an array containing the max value of each column.
//	 */
//	public static float[] maxByColumn(float[][] data) {
//		int size = data.length;
//		float[] maxVals = max(data[0]);
//		for (int i = 0; i<size; i++) {
//			max = Math.max(max, max(data[i]));
//		}
//		return (float)max;
//	}
	/**
	 * Returns the largest member of a matrix.
	 */
	public static int max(int[][] data) {
		int size = data.length;
		int max = max(data[0]);
		for (int i = 1; i<size; i++) {
			max = max(max, max(data[i]));
		}
		return max;
	}
	
	/** Contains methods for computing the arithmetic, geometric, harmonic, trimmed, and winsorized
	 * means (among others).  
	 */
	public static class Mean{
		protected Mean(){}
		
		/**
		 * Returns an array containing the arithmetic mean of each column 
		 * of the input matrix.
		 * */
		public static float[] columnMean(float[][] data) {
			int numColumns = data[0].length; int numRows = data.length;
			float[] mean = new float[numColumns]; 
			for(int c=0; c<numColumns; c++){
				float sum=0;
				for(int r=0; r<numRows; r++) {
					sum+=data[r][c];
				}		
				mean[c] =sum/numRows;
			}
			return mean;
		}
		/**
		 * Returns an array containing the arithmetic mean of each row of the input matrix.  
		 */
		public static float[] rowMean(float[][] data) {
			float[] mean = new float[data.length];
			for(int i=0; i<data.length; i++) mean[i] = mean(data[i]);
			return mean;
		}
		/**
		 * Returns the arithmetic mean of a data sequence; 
		 * That is <code>Sum( data[i] ) / data.length </code>.
		 */
		public static float arithmetic(float[] data) {
			return Sum.sum(data) / data.length;
		}
		
		/**
		 * Returns the geometric mean of a data sequence.
		 * Note that for a geometric mean to be meaningful, the minimum of the data sequence must not be less or equal to zero.
		 * <br>
		 * The geometric mean is given by <code>pow( Product( data[i] ), 1/size)</code>
		 * which is equivalent to <code>Math.exp( Sum( Log(data[i]) ) / size)</code>.
		 * <br><br>
		 * The latter version is used here is the former easily results in overflows.
		 */
		public static float geometric(int size, float sumOfLogarithms) {
			return (float)Math.exp(sumOfLogarithms/size);	
			// this version would easily results in overflows
			//return Math.pow(product, 1/size);
		}
		/**
		 * Returns the geometric mean of a data sequence.
		 * Note that for a geometric mean to be meaningful, the minimum of the data sequence must not be less or equal to zero.
		 * <br>
		 * The geometric mean is given by <code>pow( Product( data[i] ), 1/data.size())</code>.
		 * This method tries to avoid overflows at the expense of an equivalent but somewhat slow definition:
		 * <code>geometricMean = Math.exp( Sum( Log(data[i]) ) / size)</code>.
		 */
		public static float geometric(float[] data) {
			return geometric(data.length, (float)Sum.logs(data,0,data.length-1));
		}
		/**
		 * Returns the harmonic mean of a data sequence.
		 *
		 * @param size the number of elements in the data sequence.
		 * @param sumOfInversions <code>Sum( 1.0 / data[i])</code>.
		 */
		public static float harmonic(int size, float sumOfInversions) {
			return size / sumOfInversions;
		}
	
		/**
		 * Returns the harmonic mean of a data sequence as <code>Sum( 1.0 / data[i])</code>.
		 *
		 * @param data array
		 */
		public static float harmonic(float[] data) {
			return (float)data.length / (float)Sum.inversions(data, 0, data.length-1);
		}
		/**
		 * Returns the trimmed arithmetic mean of a <b>sorted</b> data sequence.
		 * @param sortedData the data sequence; <b>must be sorted ascending</b>.
		 * @param mean the mean of the (full) sorted data sequence.
		 * @param left the number of leading elements to trim.
		 * @param right the number of trailing elements to trim.
		 * */
		public static float trimmed(float[] sortedData, float mean, int left, int right) {
			int size = sortedData.length;
			Check.size0(size);
			if (left+right >= size) throw new IllegalArgumentException("Not enough data. left: "+left+", right: "+right+", data length: "+ size);
			int origLn=size;
			for(int i=0; i<left; ++i)
				mean += (mean-sortedData[i])/(--size);
			for(int i=0; i<right; ++i)
				mean += (mean-sortedData[origLn-1-i])/(--size);
			return mean;
		}
		/**
		 * Returns the winsorized mean of a sorted data sequence.
		 *
		 * @param sortedData the data sequence; <b>must be sorted ascending</b>.
		 * @param mean the mean of the (full) sorted data sequence.
		 * @param left the number of leading elements to trim.
		 * @param right the number of trailing elements to trim.
		 */
		public static float winsorized(float[] sortedData, float mean, int left, int right) {
			int size = sortedData.length;
			Check.size0(size);
			if (left+right >= size) throw new IllegalArgumentException("Not enough data.");

			float leftElement = sortedData[left];
			float winsorMean = mean;
			for(int i=0; i<left; ++i)
				winsorMean += (leftElement-sortedData[i])/size;

			double rightElement = sortedData[size-1-right];
			for(int i=0; i<right; ++i)
				winsorMean += (rightElement-sortedData[size-1-i])/size;

			return winsorMean;
		}
	}// end mean class


	/**
	 * Returns the <i>arithmetic mean</i> of a data sequence; 
	 * That is <code>Sum( data[i] ) / data.length </code>.
	 * Similar to {@link Mean#arithmetic}.
	 */
	public static float mean(float[] data) {
		return Mean.arithmetic(data);
	}
	/**
	 * Returns the mean deviation of a dataset.
	 * That is <code>Sum( Math.abs(data[i]-mean)) ) / data.length</code>.
	 */
	public static float meanDeviation(float[] data, float mean) {

		int size = data.length;
		double sum=0;
		for (int i=size; --i >= 0;) sum += Math.abs(data[i]-mean);
		return (float)sum/size;
	}

	/**
	 * Returns the median of a data sequence.
	 * @param data the data sequence; 
	 * @param isSorted true if the data sequence is sorted (in ascending order), else false.
	 */
	public static float median(float[] data, boolean isSorted) {
		if(isSorted) return quantile(data, 0.5f);
		else{
			float[] sortedData = Mat.copyThenSort(data);
			return quantile(sortedData, 0.5f);
		}
	}
	
	public static int min(int a, int b) {
		return (a < b) ? a : b;
	}

	public static float min(float a, float b) {
		return (a < b) ? a : b;
	}
	public static double min(double a, double b) {
		return (a < b) ? a : b;
	}
	/**
	 * Returns the smallest member of a data sequence.
	 */
	public static double min(double[] data) {
		int size = data.length;
		Check.size0(size);
		double min = data[0];
		for (int i = 1; i<size; i++) {
			if (data[i] < min ) min = data[i];
		}
		return min;
	}
	/**
	 * Returns the smallest member of a data sequence.
	 */
	public static float min(float[] data) {
		int size = data.length;
		Check.size0(size);
		float min = data[0];
		for (int i = 1; i<size; i++) {
			min = min(min,data[i]);
		}
		return min;
	}
	
	/**
	 * Returns the smallest member of a matrix.
	 */
	public static float min(float[][] data) {
		int size = data.length;
		float min = min(data[0]);
		for (int i = 1; i<size; i++) {
			min = min(min, min(data[i]));
		}
		return min;
	}
	/**
	 * Returns the smallest member of a data sequence.
	 */
	public static int min(int[] data) {
		int size = data.length;
		Check.size0(size);
		int min = data[0];
		for (int i = 1; i<size; i++) {
			min = Math.min(min,data[i]);
		}
		return min;
	}
	
	/**
	 * Returns the smallest member of a matrix.
	 */
	public static int min(int[][] data) {
		int size = data.length;
		int min = min(data[0]);
		for (int i = 1; i<size; i++) {
			min = Math.min(min, min(data[i]));
		}
		return min;
	}

	/**
	 * Returns the array containing the elements that appear the most in a given dataset. 
	 * (The return type has to be an array since a dataset can have more than one mod value).
	 *
	 * @param data the data array
	 * @return the array containing the (distinct) elements that appear the most.
	 */
	public static float[] mod(float[] data){
		ArrayList<Float> distinctValues = new ArrayList<Float>();
		ArrayList<Integer> frequencies = new ArrayList<Integer>();		
		frequencies(data,distinctValues,frequencies);
		int maxRepeats = frequencies.get(0); int size = frequencies.size();
		for(int i=1; i<frequencies.size(); i++){
			if(frequencies.get(i)>maxRepeats) {
				maxRepeats = frequencies.get(i);
			}
		}
		ArrayList<Float> tempMod = new ArrayList<Float>();
		for(int i=0; i<size; i++){
			if(frequencies.get(i)==maxRepeats){
				tempMod.add( distinctValues.get(i) );
			}
		}
		return Cast.arrayListToFloat(tempMod);
	}


	/**
	 * Returns the moment of <code>k</code>-th order with constant <code>c</code> of a data sequence,
	 * which is <code>Sum( (data[i]-c)<sup>k</sup> ) / data.length</code>.
	 *
	 * @param sumOfPowers <code>sumOfPowers[m] == Sum( data[i]<sup>m</sup>) )</code> for <code>m = 0,1,..,k</code> as returned by method 
	 * {@link #incrementalUpdateSumsOfPowers(DoubleArrayList,int,int,int,int,double[])}.
	 * In particular there must hold <code>sumOfPowers.length == k+1</code>.
	 * @param size the number of elements of the data sequence.
	 */
	//	public static float moment(int k, float c, int size, float[] sumOfPowers) {
	//		double sum=0;
	//		int sign = 1;
	//		for (int i=0; i<=k; i++) {
	//			double y;
	//			if (i==0) y = 1;
	//			else if (i==1) y = c;
	//			else if (i==2) y = c*c;
	//			else if (i==3) y = c*c*c;
	//			else y = Math.pow(c, i);
	//			//sum += sign * 
	//			sum += sign * cern.jet.math.Arithmetic.binomial(k,i) * y * sumOfPowers[k-i];
	//			sign = -sign;
	//		}
	//		/*
	//	for (int i=0; i<=k; i++) {
	//		sum += sign * cern.jet.math.Arithmetic.binomial(k,i) * Math.pow(c, i) * sumOfPowers[k-i];
	//		sign = -sign;
	//	}
	//		 */
	//		return sum/size;
	//	}
	/**
	 * Returns the moment of <code>k</code>-th order with constant <code>c</code> of a data sequence,
	 * which is <code>Sum( (data[i]-c)<sup>k</sup> ) / data.size()</code>.
	 */
	public static float moment(float[] data, int k, float c) {
		return (float)Sum.powerDeviations(data,k,c) / data.length;
	}
	/**
	 * Returns the array containing all elements in the dataset that are less than 
	 * or equal to the <code>lowerLimit</code> 
	 * and more than or equal to the <code>upperLimit</code> 
	 * @param data the data array
	 * @param lowerLimit
	 * @param upperLimit
	 * @param isSorted true if the data array has been sorted in ascending order, else set to false.	 
	 */
	public static float[] outliers(float[] data, float lowerLimit, float upperLimit, boolean isSorted){
		int size = data.length;
		float[] sortedData = new float[size];
		if(isSorted){
			sortedData = data;
		}
		else{
			sortedData = Mat.copyThenSort(data);
		}
		float max = sortedData[size-1]; float min =  sortedData[0];
		float[] tempArray = new float[size];
		int i=0; 
		if(lowerLimit>=min){
			while(sortedData[i]<=lowerLimit){
				tempArray[i] = sortedData[i];
				i++;
			}
		}
		int newSize = i;
		i = size-1;
		if(upperLimit<=max){
			while(sortedData[i]>=upperLimit){
				tempArray[i] = sortedData[i];
				i--;
				newSize++;
			}
		}
		float[] outliers = Arrays.copyOf(tempArray,newSize);
		//Arrays.sort(outliers);
		return outliers;
	}
	
	/** Class for computing the pooled mean and variance of data sequences */
	public static class Pooled{
		protected Pooled(){}
		
		/**
		 * Returns the pooled mean of two data sequences.
		 * That is <code>(size1 * mean1 + size2 * mean2) / (size1 + size2)</code>.
		 * <p>
		* @param size1 the number of elements in data sequence 1.
		 * @param mean1 the mean of data sequence 1.
		 * @param size2 the number of elements in data sequence 2.
		 * @param mean2 the mean of data sequence 2.
		 */
		public static float mean(float mean1, int size1, float mean2, int size2) {
			return (size1 * mean1 + size2 * mean2) / (size1 + size2);
		}
		/**
		 * Returns the pooled mean of a <code>k</code> data sequences.
		 * That is <code>(size1 * mean1 + size2 * mean1 + ... + sizek * meank) / (size1 + size2 + ... + sizek)</code>.
		 * <p>
		 *
		 */
		public static float mean(float[] means, int[] sizes) {
			int size = sizes.length;
			Check.equalLengths(size,means);
			float sum=0, sumSizes=0;
			for(int i=0; i<size; i++){
				sum+=sizes[i]*means[i];
				sumSizes+=sizes[i];
			}
			return sum/sumSizes;
		}
		
		/**
		 * Returns the pooled variance of two data sequences. The unbiased estimate of the
		 * pooled variance is computed as
		 * <pre>
		 * unbiasedPooledVar = Sum( (size_i -1)*variance_i ) / Sum( (size_i-1) ),
		 * </pre>
		 * while the biased pooled variance is computed using
		 * <pre>
		 * biasedPooledVar = Sum( (size_i)*variance_i ) / Sum(size_i),
		 * 
		 * For more than 2 datasets, use {@link Pooled#var(float[],int[],boolean)}.
		 *
		 * @param size1 the number of elements in data sequence 1.
		 * @param variance1 the variance of data sequence 1.
		 * @param size2 the number of elements in data sequence 2.
		 * @param variance2 the variance of data sequence 2.
		 * @param unbiased set to true to return the unbiased pooled variance,
		 * false to return the biased version.
		 */
		public static float var(float variance1, int size1, float variance2, int size2, boolean unbiased) {
			if(!unbiased) return (size1 * variance1 + size2 * variance2) / (size1 + size2);
			else return((size1-1)*variance1+(size2-1)*variance2)/(size1+size2-2);
		}
		/**
		 * Returns the pooled variance of <code>k</code> data sequences. The unbiased estimate of the
		 * pooled variance is computed as
		 * <pre>
		 * unbiasedPooledVar = Sum( (sizes[i] -1)*variances[i] ) / Sum( (sizes[i]-1) ),
		 * </pre>
		 * while the biased pooled variance is computed using
		 * <pre>
		 * biasedPooledVar = Sum( (sizes[i])*variances[i] ) / Sum(sizes[i]),
		 */
		public static float var(float[] variances, int[] sizes, boolean unbiased) {
			int size = sizes.length;
			Check.equalLengths(size, variances);
			float sizeSum = sizes[0];
			float varianceSum=0;
			float pooledVar = 0;
			if(unbiased){
				for(int i=0; i<size; i++){
					varianceSum +=(sizes[i]-1)*variances[i];
					sizeSum+=sizes[i];
				}
				pooledVar = varianceSum/(sizeSum-size);
			}
			else{
				for(int i=0; i<size; i++){
					varianceSum +=(sizes[i])*variances[i];
					sizeSum+=sizes[i];
				}
				pooledVar = varianceSum/sizeSum;
			}
			return pooledVar;
		}
	}
	
	
	/**
	 * Returns the product, which is <code>Prod( data[i] )</code>.
	 * In other words: <code>data[0]*data[1]*...*data[data.length-1]</code>.
	 * This method uses the equivalent definition:
	 * <code>prod = pow( exp( Sum( Log(x[i]) ) / length*length)</code>.
	 */
	public static float product(int size, float sumOfLogarithms) {
		return (float)Math.pow(Math.exp(sumOfLogarithms/size), size);
	}
	/**
	 * Returns the product of a data sequence, which is <code>Prod( data[i] )</code>.
	 * In other words: <code>data[0]*data[1]*...*data[data.length-1]</code>.
	 * Note that you may easily get numeric overflows. Use {@link product(int,float)}
	 * instead to avoid that.
	 */
	public static float product(float[] data) {
		int size = data.length;
		float product = 1;
		for (int i=size; --i >= 0;) product *= data[i];
		return product;
	}

	/** 
	 * Returns the quartiles of the input data array (not necessarily sorted).
	 * 
	 * <p>Details:<br>
	 * The first quartile, or lower quartile (Q[0]), is the value that cuts off 
	 * the first 25% of the data when it is sorted in ascending order. 
	 * The second quartile, or median (Q[1]), is the value that cuts off the first 50%.
	 * The third quartile, or upper quartile (Q[2], is the value that cuts off the first 75%.
	 * </p>
	 * 
	 * @param data The data array.
	 * @param isSorted true if the data array has been sorted in ascending order, else set to false.
	 * @return The 3 quartile values Q[0], Q[1], and Q[2].
	 */
	public static float[] quartiles(float[] data, boolean isSorted){	
		float[] quartiles = new float[3];
		if(isSorted){
			quartiles = Descriptive.quantiles(data, new float[]{.25f,.5f,.75f});
		}
		else{
			quartiles = Descriptive.quantiles(Mat.copyThenSort(data), new float[]{.25f,.5f,.75f});
		}
		return quartiles;
	}

	/**
	 * Returns the <code>phi-</code>quantile; that is, an element <code>elem</code> 
	 * for which holds that <code>phi</code> percent of data elements are less than 
	 * <code>elem</code>.
	 * The quantile need not necessarily be contained in the data sequence, 
	 * it can be a linear interpolation.
	 * @param sortedData the data sequence; <b>must be sorted ascending</b>.
	 * @param phi the percentage; must satisfy <code>0 &lt;= phi &lt;= 1</code>.
	 */
	public static float quantile(float[] sortedData, float phi) {

		int size = sortedData.length;

		float index = phi * (size - 1) ;
		int lhs = (int)index ;
		float delta = index - lhs ;
		float result;

		if (size == 0) return 0.0f ;

		if (lhs == size - 1) {
			result = sortedData[lhs] ;
		}
		else {
			result = (1 - delta) * sortedData[lhs] + delta * sortedData[lhs + 1] ;
		}
		return result ;
	}
	/**
	 * Returns how many percent of the elements contained in the receiver are <code>&lt;= element</code>.
	 * Does linear interpolation if the element is not contained but lies in between 
	 * two contained elements.
	 *
	 * @param sortedList the list to be searched (<b>must be sorted ascending</b>).
	 * @param element the element to search for.
	 * @return the percentage <code>phi</code> of elements <code>&lt;= element</code> 
	 * (<code>0.0 &lt;= phi &lt;= 1.0)</code>.
	 */
	public static float quantileInverse(float[] sortedList, float element) {
		return rankInterpolated(sortedList,element) / sortedList.length;
	}
	/**
	 * Returns the quantiles of the specified percentages.
	 * The quantiles need not necessarily be contained in the data sequence, it can be a linear interpolation.
	 * @param sortedData the data sequence; <b>must be sorted ascending</b>.
	 * @param percentages the percentages for which quantiles are to be computed.
	 * Each percentage must be in the interval <code>[0.0f,1.0f]</code>.
	 * @return the quantiles.
	 */
	public static float[] quantiles(float[] sortedData, float[] percentages) {
		int s = percentages.length;
		ArrayList<Float> quantiles = new ArrayList<Float>(s);
		for (int i=0; i < s; i++) {
			quantiles.add(quantile(sortedData, percentages[i]));
		}
		return Cast.arrayListToFloat(quantiles);
	}
	/**
	 * Returns the linearly interpolated number of elements in an array that 
	 * are &le; a given element.
	 * The rank is the number of elements &le; element.
	 * Ranks are of the form <code>{0, 1, 2,..., sortedList.size()}</code>.
	 * <p> If no element is &le; element, then the rank is zero.</p>
	 * 
	 * <p>If the element lies in between two contained elements, then 
	 * linear interpolation is used and a non integer value is returned.</p>
	 *
	 * @param sortedList the list to be searched (must be sorted ascending).
	 * @param element the element to search for.
	 * @return the rank of the element.
	 */
	public static float rankInterpolated(float[] sortedList, float element) {
		int index = Arrays.binarySearch(sortedList,element);
		if (index >= 0) { // element found
			// skip to the right over multiple occurances of element.
			int to = index+1;
			int s = sortedList.length;
			while (to<s && sortedList[to]==element) to++;
			return to;
		}

		// element not found. Use linear interpolation
		int insertionPoint = -index-1;
		if (insertionPoint == 0 || insertionPoint==sortedList.length) return insertionPoint;

		float from = sortedList[insertionPoint-1];
		float to = sortedList[insertionPoint];
		float delta = (element-from) / (to-from); //linear interpolation
		return insertionPoint + delta;
	}
	/**
	 * Returns the RMS (Root-Mean-Square) of a data sequence.
	 * That is <code>Math.sqrt(Sum( data[i]*data[i] ) / data.length)</code>.
	 * The RMS of data sequence is the square-root of the mean of the squares of the elements in the data sequence.
	 * It is a measure of the average "size" of the elements of a data sequence.
	 *
	 * @param sumOfSquares <code>sumOfSquares(data) == Sum( data[i]*data[i] )</code> of the data sequence.
	 * @param size the number of elements in the data sequence.
	 */
	public static float rms(int size, double sumOfSquares) {
		return (float)Math.sqrt(sumOfSquares/size);
	}
	
	/**
	 * Contains sample-related statistical methods.
	 */
	private static class Sample{
		protected Sample(){}
		/**
		 * Returns the sample kurtosis (aka excess) of a data sequence.
		 * That is, 
		 * <pre>
		 * &gamma;_2 = moment4*n^2*(n+1)/ ((n-1)*(n-2)*(n-3)*var^2) - 3*(n-1)^2/ ( (n-2)*(n-3) ) 
		 *     = Sum( (y_i-ymean)^4 )*n*(n+1)/ ((n-1)*(n-2)*(n-3)*var^2) - 3*(n-1)^2/ ( (n-2)*(n-3) ), 
		 * </pre>
		 * where n is the length of the input data array.
		 * <p>
		 * Ref: R.R. Sokal, F.J. Rohlf, Biometry: the principles and practice of statistics
		 * in biological research (W.H. Freeman and Company, New York, 1998, 3rd edition)
		 * p. 114-115.
		 *
		 * @param size the number of elements of the data sequence.
		 * @param moment4 the fourth central moment, which is <code>moment(data,4,mean)</code>.
		 * @param sampleVariance the sample variance.
		 */
		public static float sampleKurtosis(int size, float moment4, float sampleVariance) {
			float n=size; // set to float to avoid NaNs.
			float s2=sampleVariance; // (y-ymean)^2/(n-1)
			float m4 = moment4*n;    // (y-ymean)^4
			return m4*n*(n+1) / ( (n-1)*(n-2)*(n-3)*s2*s2 )
					- 3.0f*(n-1)*(n-1)/((n-2)*(n-3));
		}
		/**
		 * Returns the sample kurtosis (aka excess) of a data sequence.
		 */
		public static float sampleKurtosis(float[] data, float mean, float sampleVariance) {
			return sampleKurtosis(data.length,moment(data,4,mean), sampleVariance);
		}
		/**
		 * Return the standard error of the sample kurtosis. That is,
		 * <pre>
		 * Sqrt( 24n*(n-1)*(n-1)/((n-3)*(n-2)*(n+3)*(n+5)) ), 
		 * </pre>
		 * where n is the length of the input array.
		 * <p>
		 * Ref: R.R. Sokal, F.J. Rohlf, Biometry: the principles and practice of statistics
		 * in biological research (W.H. Freeman and Company, New York, 1998, 3rd edition)
		 * p. 138.
		 * @param size the number of elements of the data sequence.
		 */
		public static float sampleKurtosisStandardError(int size) {
			float n=size; // set to float to avoid NaNs.
			return (float)Math.sqrt( 24.0*n*(n-1)*(n-1)/((n-3)*(n-2)*(n+3)*(n+5)) );
		}
		/**
		 * Returns the sample skew of a data sequence. That is
		 * <pre>
		 * &gamma;_1 = moment3*n^2 / ((n-1)*(n-2)*stdev^3)
		 *     = Sum( (y_i-ymean)^3 )/stdev^3  *  n /  ((n-1)*(n-2)), 
		 * </pre>
		 * where n is the length of the input data array.
		 * <p>
		 * Ref: R.R. Sokal, F.J. Rohlf, Biometry: the principles and practice of statistics
		 * in biological research (W.H. Freeman and Company, New York, 1998, 3rd edition)
		 * p. 114-115.
		 * @param size the number of elements of the data sequence.
		 * @param moment3 the third central moment, which is <code>moment(data,3,mean)</code>.
		 * @param sampleVariance the sample variance.
		 */
		public static float sampleSkew(int size, float moment3, float sampleVariance) {
			float  n=size; // set to float to avoid NaNs
			float s=(float)Math.sqrt(sampleVariance); // sqrt( (y-ymean)^2/(n-1) )
			float m3 = moment3*n;    // (y-ymean)^3
			return n*m3 / ((n-1)*(n-2)*s*s*s);
		}
		/**
		 * Returns the sample skew of a data sequence.
		 */
		public static float sampleSkew(float[] data, float mean, float sampleVariance) {
			return sampleSkew(data.length, moment(data,3,mean), sampleVariance);
		}
		/**
		 * Return the standard error of the sample skew. That is,
		 * <pre>
		 * Sqrt( 6n*(n-1)/((n-2)*(n+1)*(n+3)) ), 
		 * </pre>
		 * where n is the length of the input array.
		 * <p>
		 * Ref: R.R. Sokal, F.J. Rohlf, Biometry: the principles and practice of statistics
		 * in biological research (W.H. Freeman and Company, New York, 1998, 3rd edition)
		 * p. 138.
		 *
		 * @param size the number of elements of the data sequence.
		 */
		public static float sampleSkewStandardError(int size) {
			float n=size;
			return (float)Math.sqrt( 6.0*n*(n-1)/((n-2)*(n+1)*(n+3)) );
		}
	}
	
	/**
	 * Returns the skew of a data sequence when the 3rd moment has already been computed.
	 * @param moment3 the third central moment, which is <code>moment(data,3,mean)</code>.
	 * @param standardDeviation the standardDeviation.
	 */
	public static float skew(float moment3, float standardDeviation) {
		return moment3 / (standardDeviation * standardDeviation * standardDeviation);
	}
	/**
	 * Returns the skew of a data sequence, which is <code>moment(data,3,mean) / standardDeviation<sup>3</sup></code>.
	 */
	public static float skew(float[] data, float mean, float standardDeviation) {
		return skew(moment(data,3,mean), standardDeviation);
	}
	
	/**
	 * Returns the standard deviation of a dataset.
	 * 
	 * There are two definitions of the standard deviation:
	 * <pre>
	 * sigma_1 = 1/(N-1) * Sum( (x[i] - mean(x))^2 )
	 * sigma_2 = 1/(N) * Sum( (x[i] - mean(x))^2 )
	 * </pre>
	 * sigma_1 is the square root of an unbiased estimator of the variance of the population
	 * the x is drawn, as long as x consists of independent, identically distributed samples.
	 * sigma_2 corresponds to the second moment of the set of values about their mean.
	 * <p>
	 * std(data,unbiased==true) returns sigma_1 above, while std(data,unbiased==false) returns sigma_2.
	 * @param data the dataset  
	 * @param unbiased set to true to return the unbiased standard deviation,
	 * false to return the biased version.
	 */
	public static float std(float[] data, boolean unbiased) {
		return (float)Math.sqrt(var(data,unbiased));
	}
	
	
	/**
	 * Returns an array with each element of the array corresponding to
	 * the standard deviations of each column of the input
	 * matrix. Each column of the matrix corresponds to a dataset, and each row an observation.
	 * 
	 * There are two definitions of the standard deviation:
	 * <pre>
	 * sigma_1 = 1/(N-1) * Sum( (x[i] - mean(x))^2 )
	 * sigma_2 = 1/(N) * Sum( (x[i] - mean(x))^2 )
	 * </pre>
	 * sigma_1 is the square root of an unbiased estimator of the variance of the population
	 * the x is drawn, as long as x consists of independent, identically distributed samples.
	 * sigma_2 corresponds to the second moment of the set of values about their mean.
	 * <p>
	 * std(data,unbiased==true) returns sigma_1 above, while std(data,unbiased==false) returns sigma_2.
	 * 
	 * @param data the dataset
	 * @param unbiased set to true to return the unbiased standard deviation,
	 * false to return the biased version.
	 */
	public static float[] std(float[][] data, boolean unbiased) {
		int numColumns = data[0].length; 
		float[] std = new float[numColumns];
		float[] dataEach = new float[data.length];
		float variance = 0;
		for(int i=0; i<numColumns; i++){
			dataEach = Mat.column(data,i);
			variance = var(dataEach,unbiased);
			std[i] = (float)Math.sqrt(variance);
			//System.out.println("variance: "+ variance+", std: "+std[i]);
		}
		return std;
	}

	
	/**
	 * Returns the unbiased sample standard deviation assuming the sample is <b>normally distributed</b>.
	 *
	 * Ref: R.R. Sokal, F.J. Rohlf, Biometry: the principles and practice of statistics
	 * in biological research (W.H. Freeman and Company, New York, 1998, 3rd edition)
	 * p. 53.
	 * <p>See also <a href="http://http://en.wikipedia.org/wiki/Unbiased_estimation_of_standard_deviation#Bias_correction/" target="_blank">this entry on wikipedia.org</a>
	 *
	 * @param size the number of elements of the data sequence.
	 * @param sampleVariance the <b>sample variance</b>.
	 */
	public static float stdUnbiased(int size, float sampleVariance) {
		double s, Cn;
		int   n=size;
		// The standard deviation calculated as the sqrt of the variance underestimates
		// the unbiased standard deviation.
		s=Math.sqrt(sampleVariance);
		// It needs to be multiplied by this correction factor.
		if (n>30) {
			Cn = 1 + 1.0/(4*(n-1)); // Cn = 1+1/(4*(n-1));
		} else {
			Cn =  Math.sqrt((n-1)*0.5)*Gamma.gamma((n-1)*0.5)/Gamma.gamma(n*0.5);
		}
		return (float) (Cn*s);
	}

	/** Methods for computing  various different sums of datasets such as sum of inversions,
	 * logs, products, power deviations, squares, etc. */
	 
	public static class Sum{
		protected Sum(){};
		/**
		 * Returns the sum of inversions of a data sequence,
		 * which is <code>Sum( 1.0 / data[i])</code>.
		 * @param data the data sequence.
		 * @param from the index of the first data element (inclusive).
		 * @param to the index of the last data element (inclusive).
		 */
		public static double inversions(float[] data, int from, int to) {
			return powerDeviations(data,-1,0.0f,from,to);
		}
		/**
		 * Returns the sum of logarithms of a data sequence, which is <code>Sum( Log(data[i])</code>.
		 * @param data the data sequence.
		 * @param from the index of the first data element (inclusive).
		 * @param to the index of the last data element (inclusive).
		 */
		public static double logs(float[] data, int from, int to) {
			double logsum = 0;
			for (int i=from-1; ++i <= to;) logsum += Math.log(data[i]);
			return (float)logsum;
		}
		/**
		 * Returns the sum of the product of two data arrays, <code>Sum( x[i] * y[i])</code>.
		 * @param data1 the first data sequence.
		 * @param data2 the second data sequence
		 * @return the sum of the product of the two data sequences.
		 */
		public static double products(float[] data1, float[] data2) {
			int size = data1.length;
			Check.equalLengths(size,data2);

			double sumOfProduct = data1[0]*data2[0];
			for (int i=1; i<size; i++) sumOfProduct += (data1[i]*data2[i]);
			return sumOfProduct;
		}
		/**
		 * Returns <code>Sum( (data[i]-c)<sup>k</sup> )</code>; 
		 * optimized for common parameters like <code>c == 0.0</code> and/or <code>k == -2 .. 4</code>.
		 */
		public static double powerDeviations(float[] data, int k, float c) {
			return powerDeviations(data,k,c,0,data.length-1);
		}
		/**
		 * Returns <code>Sum( (data[i]-c)<sup>k</sup> )</code> for all <code>i = from .. to</code>; 
		 * optimized for common parameters like <code>c == 0.0</code> and/or <code>k == -2 .. 5</code>.
		 * Note that no checks are made for divisions by zero (important for  
		 * <code>k = -2</code> and <code>k = -1</code>), so think twice before using this
		 * if the data has elements = 0.
		 */

		public static double powerDeviations(final float[] data, final int k, final float c, final int from, final int to) {

			double sum = 0.0;
			double v;
			int i;
			switch (k) { // optimized for speed
			case -2: 
				if (c==0.0) for (i=from-1; ++i<=to; ) { v = data[i]; sum += 1/(v*v); }
				else for (i=from-1; ++i<=to; ) { v = data[i]-c; sum += 1/(v*v); }
				break;
			case -1:
				if (c==0.0) for (i=from-1; ++i<=to; ) sum += 1/(data[i]);
				else for (i=from-1; ++i<=to; ) sum += 1/(data[i]-c);
				break;
			case 0: 
				sum += to-from+1;
				break;
			case 1: 
				if (c==0.0) for (i=from-1; ++i<=to; ) sum += data[i];
				else for (i=from-1; ++i<=to; ) sum += data[i]-c;
				break;
			case 2: 
				if (c==0.0) for (i=from-1; ++i<=to; ) { v = data[i]; sum += v*v; }
				else for (i=from-1; ++i<=to; ) { v = data[i]-c; sum += v*v; }
				break;
			case 3: 
				if (c==0.0) for (i=from-1; ++i<=to; ) { v = data[i]; sum += v*v*v; }
				else for (i=from-1; ++i<=to; ) { v = data[i]-c; sum += v*v*v; }
				break;
			case 4: 
				if (c==0.0) for (i=from-1; ++i<=to; ) { v = data[i]; sum += v*v*v*v; }
				else for (i=from-1; ++i<=to; ) { v = data[i]-c; sum += v*v*v*v; }
				break;
			case 5: 
				if (c==0.0) for (i=from-1; ++i<=to; ) { v = data[i]; sum += v*v*v*v*v; }
				else for (i=from-1; ++i<=to; ) { v = data[i]-c; sum += v*v*v*v*v; }
				break;
			default:
				for (i=from-1; ++i<=to; ) sum += Math.pow(data[i]-c, k);
				break;
			}
			return sum;
		}
		/**
		 * Returns the sum of powers of a data sequence, which is <code>Sum ( data[i]<sup>k</sup> )</code>.
		 */
		public static double powers(float[] data, int k) {
			return powerDeviations(data,k,0);
		}
		/**
		 * Returns the sum of squared mean deviation of of a data sequence.
		 * That is <code>variance * (size-1) == Sum( (data[i] - mean)^2 )</code>.
		 *
		 * @param size the number of elements of the data sequence. 
		 * @param variance the variance of the data sequence.
		 */
		public static float squaredDeviations(int size, float variance) {
			return variance * (size-1);
		}
		/**
		 * Returns the sum of squares of a data sequence.
		 * That is <code>Sum ( data[i]*data[i] )</code>.
		 */
		public static double squares(float[] data) {
			return powerDeviations(data,2,0.0f);
		}
		/**
		 * Returns the simple sum of a data sequence.
		 * That is <code>Sum( data[i] )</code>. 
		 * @see Mat#sum(float[])  
		 * @see Mat#sum(float[], float[])  
		 * @see Mat#sum(float[],float)  
		 */
		public static float sum(float[] data) {
			return (float)Sum.powerDeviations(data,1,0.0f);
		}
	} // end Sum class
	
	/**
	 * Return the tukey five number summary of a dataset consisting of the minimum, maximum,
	 * and three quartile values.
	 * @param data the data array
	 * @return the array of five numbers.
	 */
	public static float[] tukeyFiveNum(float[] data){
		int size = data.length;
		float[] sortedArray = Mat.copyThenSort(data);		
		float[] Q = quartiles(sortedArray, true);
		float[] fiveNum = new float[5];
		fiveNum[0] = sortedArray[0]; 
		fiveNum[4]=sortedArray[size-1];
		fiveNum[1] = Q[0]; fiveNum[2] = Q[1]; fiveNum[3] = Q[2];
		return fiveNum;
	}

	/**
	 * Returns the variance of a dataset, V. 
	 * For matrices, var(X,unbiased=true) returns an array containing the variance of each column of X. 
	 * The result V is an unbiased estimator of the variance of the population from which
	 * X is drawn, as long as X consists of independent, identically distributed samples.
	 * <p>
	 * var(x,true) normalizes V by N - 1 if N &gt; 1, where N is the sample size. 
	 * This is an unbiased estimator of the variance of the population from which X is drawn, 
	 * as long as X consists of independent, identically distributed samples. For N = 1, 
	 * V is normalized by 1.
	 * <p>
	 * V = var(x,false) normalizes by N and produces the second moment of the sample about its mean. 
	 * 
	 * <p>Reference: 
	 * <br><a href="http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance/" target="_blank">
	 *    Algorithms for calculating variance, Wikipedia.org</a>
	 * <br><a href="http://www-uxsup.csx.cam.ac.uk/~fanf2/hermes/doc/antiforgery/stats.pdf" target="_blank">
	 * Incremental calculation of weighted mean and variance, Tony Finch </a>
	 * @param data the data sequence. 
	 * @param unbiased set to true to return the unbiased variance (division by (N-1)), false to return the biased value (division by N).
	 * @return the variance in the data.
	 */
	public static float var(float[] data, boolean unbiased) {
		int size = data.length;
		float varianceSum = varianceNotNormalized(data);
		if(unbiased && size>1) return varianceSum/(size-1);
		return varianceSum/size;
	}
	
	/**
	 * Returns an array containing the variance of each column of the input matrix X. 
	 * The result V is an unbiased estimator of the variance of the population from which
	 * X is drawn, as long as X consists of independent, identically distributed samples.
	 * <p>
	 * var(X,unbiased=true) normalizes V by N - 1 if N &gt; 1, where N is the sample size. 
	 * This is an unbiased estimator of the variance of the population from which X is drawn, 
	 * as long as X consists of independent, identically distributed samples. For N = 1, 
	 * V is normalized by 1.
	 * <p>
	 * V = var(X,unbiased=false) normalizes by N and produces the second moment of the sample about its mean. 
	 *
	 * <p>Reference: 
	 * <br><a href="http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance/" target="_blank">
	 *    Algorithms for calculating variance, Wikipedia.org</a>
	 * <br><a href="http://www-uxsup.csx.cam.ac.uk/~fanf2/hermes/doc/antiforgery/stats.pdf" target="_blank">
	 * Incremental calculation of weighted mean and variance, Tony Finch </a>
	 * @param data the data sequence. 
	 * @param unbiased set to true to return the unbiased variance (division by (N-1)), false to return the biased value (division by N).
	 * @return the variance in the data.
	 */
	public static float[] var(float[][] data, boolean unbiased) {
		int numColumns = data[0].length; 
		float[] variances = new float[numColumns];
		float[] dataEach = new float[data.length];
		for(int i=0; i<numColumns; i++){
			dataEach = Mat.column(data,i);
			variances[i] = var(dataEach,unbiased);			
		}
		return variances;
	}
		/**
	 * Returns the variance of the dataset which has <b>not</b> been normalized
	 * by the size.
	 * <p>
	 * formula: var(data) = (Sum.squares(data) - mean(data)^2) / size
	 */
	protected static float varianceNotNormalized(float[] data){
		float size = data.length;
		float mean = data[0]; 
		float varianceSum = 0;	
		for(int i = 1; i < size; ++i) {
			float stepSum = data[i]-mean; // deviation of data from previous mean
			mean += stepSum/(i+1); // mean_at_step_k = mean_at_step_k-1 + (data[k] - mean_at_step_k-1) / k
			varianceSum+=stepSum*(data[i]-mean);
		}
		return varianceSum;
	}
	
	
	/**
	 * Contains methods related to weighted datasets.
	 */
	public static class Weighted{
		protected Weighted(){}
		/**
		 * Returns the weighted mean of a data sequence. Used when the values in a set of 
		 * data do not share equal importance.
		 * That is <code> Sum (data[i] * weights[i]) / Sum ( weights[i] )</code>.
		 */
		public static float mean(float[] data, float[] weights) {
			int size = data.length;
			Check.equalLengths(size,weights);	
			float sum = 0.0f;
			float weightsSum = 0.0f;
			for (int i=size; --i >= 0; ) {
				sum += data[i] * weights[i];
				weightsSum += weights[i];
			}
			return sum/weightsSum;
		}
		/**
		 * Returns the weighted RMS (Root-Mean-Square) of a data sequence.
		 * That is <code>Sum( data[i] * data[i] * weights[i]) / Sum( data[i] * weights[i] )</code>,
		 * or in other words <code>sumOfProducts / sumOfSquaredProducts</code>.
		 *
		 * @param sumOfProducts <code>== Sum( data[i] * weights[i] )</code>.
		 * @param sumOfSquaredProducts <code>== Sum( data[i] * data[i] * weights[i] )</code>.
		 */
		public static float rms(float sumOfProducts, float sumOfSquaredProducts) {
			return sumOfProducts / sumOfSquaredProducts;
		}
		

		/**
		 * Returns the weighted variance of a data sequence of length N
		 * There are (unfortunately) many different definitions of the unbiased weighted variance. 
		 * Here, we use the following formula 
		 * <pre>
		 * weighted.var(x,w,unbiased=true) = biasCorrection * Sum ( w[i] * (x[i] - mu_w)^2  ) / Sum(w[i]).
		 * </pre>
		 * where <code>mu_w</code> corresponds to the weighted mean and the <code>biasCorrection</code>
		 * term 
		 * <pre>
		 * biasCorrection = ( Sum(w[i]) )^2  / ( ( Sum(w[i]) )^2 - Sum( w[i]^2 )  ).
		 * </pre>
		 * corrects for the bias in the variance. 
		 * weighted.var(x,w,unbiased=false) computes the biased weighted variance and is given by the formula above,
		 * but without the correction factor.
		 * 
		 * <p>The formula above agrees with that presented in
		 * <a href = "http://www.itl.nist.gov/div898/software/dataplot/refman2/ch2/weighvar.pdf" target = "_blank">
		 * the NIST Information Technology Laboratory page</a>.
		 * The algorithm used, is a one-pass formula credited to
		 * "D. H. D. West (1979). Communications of the ACM, 22, 9, 532-535: 
		 * Updating Mean and Variance Estimates: An Improved Method", and spelled out under the  
		 * <br><a href="http://en.wikipedia.org/wiki/#Weighted_incremental_algorithm/" target="_blank">
		 * weighted incremental algorithm</a> section of the link.
		 * 
		 * @param unbiased set to true to return the unbiased variance (division by (N-1)), false to return the biased value (division by N).
		 * @return the weighted variance.
		 */
		public static float var(float[] data, float[] weights, boolean unbiased) {
			int size = data.length;
			Check.equalLengths(size,weights);
			if(size==1) return 0;
			float weightSum = weights[0]; // Sum of weights from 0 to i
			float varianceSum = 0; 
			float mean = data[0];
			float weightSumSquared = weights[0]*weights[0];

			/** at step i:
			  mean= meanOld +weights[i]/weightSum * (data[i] - meanOld)
			  S[i]+= weights[i]*(data[i] - meanOld)*(data[i] - mean)
			 */
			for(int i=1; i<size; i++){
				float stepMean = data[i] - mean; // delta
				float stepWeight = weightSum + weights[i]; // temp
				mean+= stepMean*weights[i]/stepWeight;
				varianceSum += weights[i] * stepMean * (data[i]-mean);
				weightSum+=weights[i];
				weightSumSquared += weights[i]*weights[i];
			}	
			float denom = unbiased ? (weightSum*weightSum - weightSumSquared)/weightSum : weightSum;
			return denom !=0 ? varianceSum/(denom) : 0;
			
		}	
	}
	/**
	 * @return  the z-score of that element computed as <code>(x-mean)/standardDeviation</code>
	 */
	public static float zScore(float x, float mean, float standardDeviation){
		return (x-mean)/standardDeviation;
	}

	/**
	 * Returns the array of z-scores for a given data array.
	 * with each elment given by
	 * <code>z[i] = ( x[i] - mean ) / standardDeviation</code>.
	 * @return the standardized array, z
	 */
	public static float[] zScore(float[] x, float mean, float standardDeviation){
		float[] z = new float[x.length];
		for(int i=0; i<x.length; i++) z[i] = (x[i] - mean)/standardDeviation;
		return z;
	}
	
	/**
	 * Computes the standardized version of the input matrix. That is each, each element
	 * of column <code>j</code>, of the output is given by
	 * <code>Z[i][j] = ( X[i][j]- mean(X[,j]) / standardDeviation(X[,j])</code>
	 * where mean(X[,j]) is the mean value of column <code>j</code> of <code>X</code>.
	 */
	public static float[][] zScore(float[][] X, float[] means, float[] standardDeviations){
		int numRows = X.length; int numColumns = X[0].length;
		float[][] z = new float[numRows][numColumns];
		Check.equalLengths(numColumns,means);
		Check.equalLengths(numColumns,standardDeviations);
		for(int i=0; i<numRows; i++) {
			for(int j=0; j<numColumns; j++){
				z[i][j] = (X[i][j] - means[j])/standardDeviations[j];
			}
		}
		return z;
	}
}
