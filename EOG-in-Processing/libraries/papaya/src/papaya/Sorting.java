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
 * Class for getting the array of indices that can be used to sort the array in ascending or
 * descending order. This is especially useful if you want to preserve the ordering of 
 * your original array, or if you want to manipulate more than one array according to the 
 * sorted order of only one of them.
 * <p>   
 * For example, assume we have three arrays X, Y and Z. We want to sort all three arrays by 
 * X (or some arbitrary comparison function). For example, we have<br>
 * <code>X={3, 2, 1}, Y={3.0, 1.0, 2.0}, Z={6.0, 7.0, 8.0}</code>. The output should 
 * be <code>
 * <br>
 * X={1, 2, 3}, Y={2.0, 1.0, 3.0}, Z={8.0, 7.0, 6.0}</code>. 
 * To do this, we can use the indices corresponding to the elements of <code>X</code> sorted
 * in ascending order: 
 * <p>
 * <code>X={3,2,1}</code> so <code>indicesForSorting = {2,1,0}</code> 
 * (<code>X[2]</code> holds the smallest value of <code>X</code>,
 * <code>X[1]</code> holds the next smallest value of <code>X</code>, and <code>X[0]</code> 
 * holds the largest value of <code>X</code>).
 * The <code>indicesForSorting</code> can then be used to arrange <code>Y</code> and <code>Z</code>
 * in the same order.
 * 
 * <p>
 * The algorithm used to obtain these indices is a modified version of the 
 * tuned quicksort proposed by Jon L. Bentley and M. Douglas McIlroy's "Engineering a
 * Sort Function", Software-Practice and Experience, Vol. 23(11)
 * P. 1249-1265 (November 1993) (program 7 of the paper; currently used in the Arrays.sort() method
 *  to sort arrays).
 * <p>It is a modified version in the sense that the original array remains unsorted. Instead,
 * a copy of the original array is used in the sorting routine, and the array of 
 * indices [0,1,2,...N-1] sorted concurrently with the array being sorted. 
 *
 *
 * @author Adila Faruk
 * 
 */
public final class Sorting {

	/**
	 * Makes this class non instantiable, but still let's others inherit from it.
	 */
	protected Sorting(){}
	
	private static final int SMALL = 7; // params used in original paper
	private static final int MEDIUM = 40; // params used in original paper

	/**
	 * Gets the array of indices that can be used to sort the array in ascending or
	 * descending order.
	 * 
	 * <p>
	 * The algorithm used to obtain these indices is a modified version of the 
	 * tuned quicksort proposed by Jon L. Bentley and M. Douglas McIlroy's "Engineering a
	 * Sort Function", Software-Practice and Experience, Vol. 23(11)
	 * P. 1249-1265 (November 1993) (program 7 of the paper; currently used in the Arrays.sort() method
	 * to sort arrays).
	 * <p>It is a modified version in the sense that the original array remains unsorted. Instead,
	 * a copy of the original array is used in the sorting routine, and the array of 
	 * indices [0,1,2,...N-1] sorted concurrently with the array being sorted. 
	 *
	 * @param a the input array
	 * @param isAscending true for data sorted in increasing order.
	 * @return integar array of indices (with length=data.length) that can be used
	 * to sort the data from low to high or vice-versa..
	 * * @throws IllegalArgumentException if <code>fromIndex &gt; toIndex</code>
	 * @throws ArrayIndexOutOfBoundsException if <code>fromIndex &lt; 0</code> or
	 *	       <code>toIndex &gt; a.length</code>
	 *
	 *
	*/
	public static int[] indices(float[] a, boolean isAscending) {
		return indices(a,0,a.length,isAscending);
	}
	public static int[] indices(double[] a, boolean isAscending) {
		return indices(a,0,a.length,isAscending);
	}
	public static int[] indices(int[] a, boolean isAscending) {
		return indices(a,0,a.length,isAscending);
	}
	/**
	 * Gets the array of indices that can be used to sort the section of the
	 * input array going from <code>fromIndex</code>(inclusive) to <code>toIndex</code>(exclusive) 
     * in ascending or descending order.
	 * 
	 * <p>
	 * The algorithm used to obtain these indices is a modified version of the 
	 * tuned quicksort proposed by Jon L. Bentley and M. Douglas McIlroy's "Engineering a
	 * Sort Function", Software-Practice and Experience, Vol. 23(11)
	 * P. 1249-1265 (November 1993) (program 7 of the paper; currently used in the Arrays.sort() method
	 * to sort arrays).
	 * <p>It is a modified version in the sense that the original array remains unsorted. Instead,
	 * a copy of the original array is used in the sorting routine, and the array of 
	 * indices [fromIndex,fromIndex+1, ... , toIndex-1] sorted concurrently with the array being sorted. 
	 * @param a the array to be sorted.
	 * @param fromIndex the index of the first element (inclusive) to be
	 *        sorted.
	 * @param toIndex the index of the last element (exclusive) to be sorted.
	 * @param isAscending true for data sorted in increasing order.
	 * @throws IllegalArgumentException if <code>fromIndex &gt; toIndex</code>
	 * @throws ArrayIndexOutOfBoundsException if <code>fromIndex &lt; 0</code> or
	 *	       <code>toIndex &gt; a.length</code>
	 */
	public static int[] indices(float[] a, int fromIndex, int toIndex, boolean isAscending) {
		rangeCheck(a.length, fromIndex, toIndex);
		int lenToSort = toIndex-fromIndex;
		//int[] indicesToSort = Mat.linspace(fromIndex, toIndex-1); 
		int[] indicesToSort = Mat.linspace(0,a.length-1); 
		float[] x = Arrays.copyOf(a,a.length);
		quickSort1(x, indicesToSort, fromIndex, lenToSort, isAscending); 
		return indicesToSort;
	}
	public static int[] indices(double[] a, int fromIndex, int toIndex, boolean isAscending) {
		rangeCheck(a.length, fromIndex, toIndex);
		int lenToSort = toIndex-fromIndex;
		//int[] indicesToSort = Mat.linspace(fromIndex, toIndex-1); 
		int[] indicesToSort = Mat.linspace(0,a.length-1); 
		double[] x = Arrays.copyOf(a,a.length);
		quickSort1(x, indicesToSort, fromIndex, lenToSort, isAscending); 
		return indicesToSort;
	}
	public static int[] indices(int[] a, int fromIndex, int toIndex, boolean isAscending) {
		rangeCheck(a.length, fromIndex, toIndex);
		int lenToSort = toIndex-fromIndex;
		//int[] indicesToSort = Mat.linspace(fromIndex, toIndex-1); 
		int[] indicesToSort = Mat.linspace(0,a.length-1); 
		int[] x = Arrays.copyOf(a,a.length);
		quickSort1(x, indicesToSort, fromIndex, lenToSort, isAscending); 
		return indicesToSort;
	}
	
	/**
	 * Sorts the input <code>x</code> and <code>y</code> arrays according to
	 * <code>x</code> and going from <code>fromIndex</code>(inclusive) to <code>toIndex</code>(exclusive) 
     * in ascending or descending order.
	 * 
	 * <p>
	 * The algorithm used to obtain these indices is a modified version of the 
	 * tuned quicksort proposed by Jon L. Bentley and M. Douglas McIlroy's "Engineering a
	 * Sort Function", Software-Practice and Experience, Vol. 23(11)
	 * P. 1249-1265 (November 1993) (program 7 of the paper; currently used in the Arrays.sort() method
	 * to sort arrays).
	 * <p>It is a modified version in the sense that two arrays are sorted according to the first
	 * array. At the end of the method, the section of both arrays going from 
	 * <code>fromIndex</code> (inclusive) to <code>toIndex</code>(exclusive) are sorted according 
	 * to the first array's order. <i>Use a copy of both arrays if you want to preserve the natural order of 
	 * the original arrays.</i>
	 * @param x the array to sort.
	 * @param y the second array, to sort according to <code>x</code>.
	 * @param fromIndex the index of the first element (inclusive) to be
	 *        sorted.
	 * @param toIndex the index of the last element (exclusive) to be sorted.
	 * @param isAscending true for data sorted in increasing order.
	 * @throws IllegalArgumentException if <code>fromIndex &gt; toIndex</code>
	 * @throws ArrayIndexOutOfBoundsException if <code>fromIndex &lt; 0</code> or
	 *	       <code>toIndex &gt; a.length</code>
	 * @see Arrays#sort(float[])
	 */
	public static void twoArrays(float[] x,  float[] y, int fromIndex, int toIndex, boolean isAscending) {
		rangeCheck(x.length, fromIndex, toIndex); 
		int lenToSort = toIndex-fromIndex;
		quickSort2(x, y, fromIndex, lenToSort, isAscending); 
	}
	public static void twoArrays(double[] x,  double[] y, int fromIndex, int toIndex, boolean isAscending) {
		rangeCheck(x.length, fromIndex, toIndex); 
		int lenToSort = toIndex-fromIndex;
		quickSort2(x, y, fromIndex, lenToSort, isAscending); 
	}
	public static void twoArrays(int[] x,  int[] y, int fromIndex, int toIndex, boolean isAscending) {
		rangeCheck(x.length, fromIndex, toIndex); 
		int lenToSort = toIndex-fromIndex;
		quickSort2(x, y, fromIndex, lenToSort, isAscending); 
	}

	/* Returns the index of the median of the three indexed floats */
	private static int med3(float x[], int a, int b, int c) {
		// isAscending/Descending doesn't make a difference since
		// we're getting the median
		float ab = compare(x[a],x[b],true);
		float ac = compare(x[a],x[c],true);
		float bc = compare(x[b],x[c],true);
		return (ab<0 ?
				(bc<0 ? b : ac<0 ? c : a) :
					(bc>0 ? b : ac>0 ? c : a));
	}
	/* Returns the index of the median of the three indexed floats */
	private static int med3(double x[], int a, int b, int c) {
		// isAscending/Descending doesn't make a difference since
		// we're getting the median
		double ab = compare(x[a],x[b],true);
		double ac = compare(x[a],x[c],true);
		double bc = compare(x[b],x[c],true);
		return (ab<0 ?
				(bc<0 ? b : ac<0 ? c : a) :
					(bc>0 ? b : ac>0 ? c : a));
	}
	/* Returns the index of the median of the three indexed floats */
	private static int med3(int x[], int a, int b, int c) {
		// isAscending/Descending doesn't make a difference since
		// we're getting the median
		int ab = compare(x[a],x[b],true);
		int ac = compare(x[a],x[c],true);
		int bc = compare(x[b],x[c],true);
		return (ab<0 ?
				(bc<0 ? b : ac<0 ? c : a) :
					(bc>0 ? b : ac>0 ? c : a));
	}

	/* compare(a,b) returns (a-b) if isAscending. else returns b-a*/
	private static void quickSort1(float[] x, int[] indices, int off, int len, boolean isAscending) {
		//isAscendingnsertion sort on smallest arrays. (Checked, and works). 
		if (len < SMALL) { 
			for (int i=off; i<len+off; i++){
				/* work backwards recursively. if x[j-1] > x[j], swap the two 
				 * so that the lower value (x[j]) is now at x[j-1].
				 */
				for (int j=i; j>off && compare(x[j-1],x[j],isAscending)>0; j--){
					swap(x, j, j-1);
					swap(indices, j, j-1);
				}
			}
			return;
		}
		// Larger arrays:
		// Choose a partition element, v that's the median of the array.
		int m = off + len/2;       // Small arrays, middle element
		if (len > SMALL) { 
			int l = off;  // start
			int n = off + len - 1; // end
			if (len > MEDIUM) {        // Big arrays, pseudomedian of 9
				int s = len/8;
				l = med3(x, l,     l+s, l+2*s);
				m = med3(x, m-s,   m,   m+s);
				n = med3(x, n-2*s, n-s, n);
			}
			m = med3(x, l, m, n); // Mid-size, med of 3
		}
		float v = x[m];  // the partition

		// Establish Invariant: v* (<v)* (>v)* v* (due to Sedgewick)
		/* Index b scans up from the bottom of the array until it reaches a 
		 * large element (greater than or equal to the partition value), 
		 * c scans down until it reaches a small element. 
		 * The two array elements are then swapped, and the scans continue until the pointers cross.
		 */
		int a = off, b = a, c = off + len - 1, d = c;
		while(true) {
			float comparison;
			// while b < last index and x[b]<=x[median]
			while (b <= c && (comparison=compare(x[b],v,isAscending))<=0) {
				// if x[b]==x[median], x[start++]=x[b], x[b] = x[start++] 
				if (comparison == 0){
					swap(indices, a, b); 
					swap(x, a++, b);					
				}	
				// increment b
				b++;
			}
			while (c >= b && (comparison=compare(x[c],v,isAscending))>=0) {
				if (comparison == 0){		
					swap(indices, c, d);
					swap(x, c, d--);					
				}
				c--;
			}
			/*At the end of the partitioning loop, b=c+1. 
			 * Stop, and swap x[0] and x[j] */
			if (b > c) break;
			swap(indices, b, c);
			swap(x, b++, c--);			
		}

		// Swap partition elements back to middle
		int s, n = off + len;
		s = Math.min(a-off, b-a  );  
		vecswap(x, off, b-s, s);
		vecswap(indices, off, b-s, s);

		s = Math.min(d-c, n-d-1);  
		vecswap(x, b,   n-s, s);
		vecswap(indices, b,   n-s, s);


		/* call quicksort recursively on the subarrays x[off]...x[b-a]
 		and x[end-(d-c)]...x[d-c]
		 */
		if ((s = b-a) > 1){
			quickSort1(x, indices, off, s, isAscending);			
		}
		if ((s = d-c) > 1){
			quickSort1(x, indices, n-s, s, isAscending);
		}		
	}
	/* compare(a,b) returns (a-b) if isAscending. else returns b-a*/
	private static void quickSort2(float[] x, float[] y, int off, int len, boolean isAscending) {
		//isAscendingnsertion sort on smallest arrays. (Checked, and works). 
		if (len < SMALL) { 
			for (int i=off; i<len+off; i++){
				/* work backwards recursively. if x[j-1] > x[j], swap the two 
				 * so that the lower value (x[j]) is now at x[j-1].
				 */
				for (int j=i; j>off && compare(x[j-1],x[j],isAscending)>0; j--){
					swap(x, j, j-1);
					swap(y, j, j-1);
				}
			}
			return;
		}
		// Larger arrays:
		// Choose a partition element, v that's the median of the array.
		int m = off + len/2;       // Small arrays, middle element
		if (len > SMALL) { 
			int l = off;  // start
			int n = off + len - 1; // end
			if (len > MEDIUM) {        // Big arrays, pseudomedian of 9
				int s = len/8;
				l = med3(x, l,     l+s, l+2*s);
				m = med3(x, m-s,   m,   m+s);
				n = med3(x, n-2*s, n-s, n);
			}
			m = med3(x, l, m, n); // Mid-size, med of 3
		}
		float v = x[m];  // the partition

		// Establish Invariant: v* (<v)* (>v)* v* (due to Sedgewick)
		/* Index b scans up from the bottom of the array until it reaches a 
		 * large element (greater than or equal to the partition value), 
		 * c scans down until it reaches a small element. 
		 * The two array elements are then swapped, and the scans continue until the pointers cross.
		 */
		int a = off, b = a, c = off + len - 1, d = c;
		while(true) {
			float comparison;
			// while b < last index and x[b]<=x[median]
			while (b <= c && (comparison=compare(x[b],v,isAscending))<=0) {
				// if x[b]==x[median], x[start++]=x[b], x[b] = x[start++] 
				if (comparison == 0){
					swap(y, a, b); 
					swap(x, a++, b);					
				}	
				// increment b
				b++;
			}
			while (c >= b && (comparison=compare(x[c],v,isAscending))>=0) {
				if (comparison == 0){		
					swap(y, c, d);
					swap(x, c, d--);					
				}
				c--;
			}
			/*At the end of the partitioning loop, b=c+1. 
			 * Stop, and swap x[0] and x[j] */
			if (b > c) break;
			swap(y, b, c);
			swap(x, b++, c--);			
		}

		// Swap partition elements back to middle
		int s, n = off + len;
		s = Math.min(a-off, b-a  );  
		vecswap(x, off, b-s, s);
		vecswap(y, off, b-s, s);

		s = Math.min(d-c, n-d-1);  
		vecswap(x, b,   n-s, s);
		vecswap(y, b,   n-s, s);


		/* call quicksort recursively on the subarrays x[off]...x[b-a]
 		and x[end-(d-c)]...x[d-c]
		 */
		if ((s = b-a) > 1){
			quickSort2(x, y, off, s, isAscending);			
		}
		if ((s = d-c) > 1){
			quickSort2(x, y, n-s, s, isAscending);
		}		
	}
	
	/* compare(a,b) returns (a-b) if isAscending. else returns b-a*/
	private static void quickSort1(int[] x, int[] indices, int off, int len, boolean isAscending) {
		//isAscendingnsertion sort on smallest arrays. (Checked, and works). 
		if (len < SMALL) { 
			for (int i=off; i<len+off; i++){
				/* work backwards recursively. if x[j-1] > x[j], swap the two 
				 * so that the lower value (x[j]) is now at x[j-1].
				 */
				for (int j=i; j>off && compare(x[j-1],x[j],isAscending)>0; j--){
					swap(x, j, j-1);
					swap(indices, j, j-1);
				}
			}
			return;
		}
		// Larger arrays:
		// Choose a partition element, v that's the median of the array.
		int m = off + len/2;       // Small arrays, middle element
		if (len > SMALL) { 
			int l = off;  // start
			int n = off + len - 1; // end
			if (len > MEDIUM) {        // Big arrays, pseudomedian of 9
				int s = len/8;
				l = med3(x, l,     l+s, l+2*s);
				m = med3(x, m-s,   m,   m+s);
				n = med3(x, n-2*s, n-s, n);
			}
			m = med3(x, l, m, n); // Mid-size, med of 3
		}
		int v = x[m];  // the partition
	
		// Establish Invariant: v* (<v)* (>v)* v* (due to Sedgewick)
		/* Index b scans up from the bottom of the array until it reaches a 
		 * large element (greater than or equal to the partition value), 
		 * c scans down until it reaches a small element. 
		 * The two array elements are then swapped, and the scans continue until the pointers cross.
		 */
		int a = off, b = a, c = off + len - 1, d = c;
		while(true) {
			int comparison;
			// while b < last index and x[b]<=x[median]
			while (b <= c && (comparison=compare(x[b],v,isAscending))<=0) {
				// if x[b]==x[median], x[start++]=x[b], x[b] = x[start++] 
				if (comparison == 0){
					swap(indices, a, b); 
					swap(x, a++, b);					
				}	
				// increment b
				b++;
			}
			while (c >= b && (comparison=compare(x[c],v,isAscending))>=0) {
				if (comparison == 0){		
					swap(indices, c, d);
					swap(x, c, d--);					
				}
				c--;
			}
			/*At the end of the partitioning loop, b=c+1. 
			 * Stop, and swap x[0] and x[j] */
			if (b > c) break;
			swap(indices, b, c);
			swap(x, b++, c--);			
		}
	
		// Swap partition elements back to middle
		int s, n = off + len;
		s = Math.min(a-off, b-a  );  
		vecswap(x, off, b-s, s);
		vecswap(indices, off, b-s, s);
	
		s = Math.min(d-c, n-d-1);  
		vecswap(x, b,   n-s, s);
		vecswap(indices, b,   n-s, s);
	
	
		/* call quicksort recursively on the subarrays x[off]...x[b-a]
		and x[end-(d-c)]...x[d-c]
		 */
		if ((s = b-a) > 1){
			quickSort1(x, indices, off, s, isAscending);			
		}
		if ((s = d-c) > 1){
			quickSort1(x, indices, n-s, s, isAscending);
		}		
	}
	/* compare(a,b) returns (a-b) if isAscending. else returns b-a*/
	private static void quickSort2(int[] x, int[] y, int off, int len, boolean isAscending) {
		//isAscendingnsertion sort on smallest arrays. (Checked, and works). 
		if (len < SMALL) { 
			for (int i=off; i<len+off; i++){
				/* work backwards recursively. if x[j-1] > x[j], swap the two 
				 * so that the lower value (x[j]) is now at x[j-1].
				 */
				for (int j=i; j>off && compare(x[j-1],x[j],isAscending)>0; j--){
					swap(x, j, j-1);
					swap(y, j, j-1);
				}
			}
			return;
		}
		// Larger arrays:
		// Choose a partition element, v that's the median of the array.
		int m = off + len/2;       // Small arrays, middle element
		if (len > SMALL) { 
			int l = off;  // start
			int n = off + len - 1; // end
			if (len > MEDIUM) {        // Big arrays, pseudomedian of 9
				int s = len/8;
				l = med3(x, l,     l+s, l+2*s);
				m = med3(x, m-s,   m,   m+s);
				n = med3(x, n-2*s, n-s, n);
			}
			m = med3(x, l, m, n); // Mid-size, med of 3
		}
		int v = x[m];  // the partition
	
		// Establish Invariant: v* (<v)* (>v)* v* (due to Sedgewick)
		/* Index b scans up from the bottom of the array until it reaches a 
		 * large element (greater than or equal to the partition value), 
		 * c scans down until it reaches a small element. 
		 * The two array elements are then swapped, and the scans continue until the pointers cross.
		 */
		int a = off, b = a, c = off + len - 1, d = c;
		while(true) {
			int comparison;
			// while b < last index and x[b]<=x[median]
			while (b <= c && (comparison=compare(x[b],v,isAscending))<=0) {
				// if x[b]==x[median], x[start++]=x[b], x[b] = x[start++] 
				if (comparison == 0){
					swap(y, a, b); 
					swap(x, a++, b);					
				}	
				// increment b
				b++;
			}
			while (c >= b && (comparison=compare(x[c],v,isAscending))>=0) {
				if (comparison == 0){		
					swap(y, c, d);
					swap(x, c, d--);					
				}
				c--;
			}
			/*At the end of the partitioning loop, b=c+1. 
			 * Stop, and swap x[0] and x[j] */
			if (b > c) break;
			swap(y, b, c);
			swap(x, b++, c--);			
		}
	
		// Swap partition elements back to middle
		int s, n = off + len;
		s = Math.min(a-off, b-a  );  
		vecswap(x, off, b-s, s);
		vecswap(y, off, b-s, s);
	
		s = Math.min(d-c, n-d-1);  
		vecswap(x, b,   n-s, s);
		vecswap(y, b,   n-s, s);
	
	
		/* call quicksort recursively on the subarrays x[off]...x[b-a]
		and x[end-(d-c)]...x[d-c]
		 */
		if ((s = b-a) > 1){
			quickSort2(x, y, off, s, isAscending);			
		}
		if ((s = d-c) > 1){
			quickSort2(x, y, n-s, s, isAscending);
		}		
	}
	/* compare(a,b) returns (a-b) if isAscending. else returns b-a*/
	private static void quickSort1(double[] x, int[] indices, int off, int len, boolean isAscending) {
		//isAscendingnsertion sort on smallest arrays. (Checked, and works). 
		if (len < SMALL) { 
			for (int i=off; i<len+off; i++){
				/* work backwards recursively. if x[j-1] > x[j], swap the two 
				 * so that the lower value (x[j]) is now at x[j-1].
				 */
				for (int j=i; j>off && compare(x[j-1],x[j],isAscending)>0; j--){
					swap(x, j, j-1);
					swap(indices, j, j-1);
				}
			}
			return;
		}
		// Larger arrays:
		// Choose a partition element, v that's the median of the array.
		int m = off + len/2;       // Small arrays, middle element
		if (len > SMALL) { 
			int l = off;  // start
			int n = off + len - 1; // end
			if (len > MEDIUM) {        // Big arrays, pseudomedian of 9
				int s = len/8;
				l = med3(x, l,     l+s, l+2*s);
				m = med3(x, m-s,   m,   m+s);
				n = med3(x, n-2*s, n-s, n);
			}
			m = med3(x, l, m, n); // Mid-size, med of 3
		}
		double v = x[m];  // the partition

		// Establish Invariant: v* (<v)* (>v)* v* (due to Sedgewick)
		/* Index b scans up from the bottom of the array until it reaches a 
		 * large element (greater than or equal to the partition value), 
		 * c scans down until it reaches a small element. 
		 * The two array elements are then swapped, and the scans continue until the pointers cross.
		 */
		int a = off, b = a, c = off + len - 1, d = c;
		while(true) {
			double comparison;
			// while b < last index and x[b]<=x[median]
			while (b <= c && (comparison=compare(x[b],v,isAscending))<=0) {
				// if x[b]==x[median], x[start++]=x[b], x[b] = x[start++] 
				if (comparison == 0){
					swap(indices, a, b); 
					swap(x, a++, b);					
				}	
				// increment b
				b++;
			}
			while (c >= b && (comparison=compare(x[c],v,isAscending))>=0) {
				if (comparison == 0){		
					swap(indices, c, d);
					swap(x, c, d--);					
				}
				c--;
			}
			/*At the end of the partitioning loop, b=c+1. 
			 * Stop, and swap x[0] and x[j] */
			if (b > c) break;
			swap(indices, b, c);
			swap(x, b++, c--);			
		}

		// Swap partition elements back to middle
		int s, n = off + len;
		s = Math.min(a-off, b-a  );  
		vecswap(x, off, b-s, s);
		vecswap(indices, off, b-s, s);

		s = Math.min(d-c, n-d-1);  
		vecswap(x, b,   n-s, s);
		vecswap(indices, b,   n-s, s);


		/* call quicksort recursively on the subarrays x[off]...x[b-a]
 		and x[end-(d-c)]...x[d-c]
		 */
		if ((s = b-a) > 1){
			quickSort1(x, indices, off, s, isAscending);			
		}
		if ((s = d-c) > 1){
			quickSort1(x, indices, n-s, s, isAscending);
		}		
	}
	/* compare(a,b) returns (a-b) if isAscending. else returns b-a*/
	private static void quickSort2(double[] x, double[] y, int off, int len, boolean isAscending) {
		//isAscendingnsertion sort on smallest arrays. (Checked, and works). 
		if (len < SMALL) { 
			for (int i=off; i<len+off; i++){
				/* work backwards recursively. if x[j-1] > x[j], swap the two 
				 * so that the lower value (x[j]) is now at x[j-1].
				 */
				for (int j=i; j>off && compare(x[j-1],x[j],isAscending)>0; j--){
					swap(x, j, j-1);
					swap(y, j, j-1);
				}
			}
			return;
		}
		// Larger arrays:
		// Choose a partition element, v that's the median of the array.
		int m = off + len/2;       // Small arrays, middle element
		if (len > SMALL) { 
			int l = off;  // start
			int n = off + len - 1; // end
			if (len > MEDIUM) {        // Big arrays, pseudomedian of 9
				int s = len/8;
				l = med3(x, l,     l+s, l+2*s);
				m = med3(x, m-s,   m,   m+s);
				n = med3(x, n-2*s, n-s, n);
			}
			m = med3(x, l, m, n); // Mid-size, med of 3
		}
		double v = x[m];  // the partition

		// Establish Invariant: v* (<v)* (>v)* v* (due to Sedgewick)
		/* Index b scans up from the bottom of the array until it reaches a 
		 * large element (greater than or equal to the partition value), 
		 * c scans down until it reaches a small element. 
		 * The two array elements are then swapped, and the scans continue until the pointers cross.
		 */
		int a = off, b = a, c = off + len - 1, d = c;
		while(true) {
			double comparison;
			// while b < last index and x[b]<=x[median]
			while (b <= c && (comparison=compare(x[b],v,isAscending))<=0) {
				// if x[b]==x[median], x[start++]=x[b], x[b] = x[start++] 
				if (comparison == 0){
					swap(y, a, b); 
					swap(x, a++, b);					
				}	
				// increment b
				b++;
			}
			while (c >= b && (comparison=compare(x[c],v,isAscending))>=0) {
				if (comparison == 0){		
					swap(y, c, d);
					swap(x, c, d--);					
				}
				c--;
			}
			/*At the end of the partitioning loop, b=c+1. 
			 * Stop, and swap x[0] and x[j] */
			if (b > c) break;
			swap(y, b, c);
			swap(x, b++, c--);			
		}

		// Swap partition elements back to middle
		int s, n = off + len;
		s = Math.min(a-off, b-a  );  
		vecswap(x, off, b-s, s);
		vecswap(y, off, b-s, s);

		s = Math.min(d-c, n-d-1);  
		vecswap(x, b,   n-s, s);
		vecswap(y, b,   n-s, s);


		/* call quicksort recursively on the subarrays x[off]...x[b-a]
 		and x[end-(d-c)]...x[d-c]
		 */
		if ((s = b-a) > 1){
			quickSort2(x, y, off, s, isAscending);			
		}
		if ((s = d-c) > 1){
			quickSort2(x, y, n-s, s, isAscending);
		}		
	}

	/** Ensures that <code>sgn(compare(x, y)) ==
	 * -sgn(compare(y, x))</code> for all <tt>x</tt> and <tt>y</tt>.  (This
	 * implies that <tt>compare(x, y)</tt> must throw an exception if and only
	 * if <tt>compare(y, x)</tt> throws an exception.)<p>
	 *
	 * The implementor must also ensure that the relation is transitive:
	 * <tt>((compare(x, y)&gt;0) &amp;&amp; (compare(y, z)&gt;0))</tt> implies
	 * <tt>compare(x, z)&gt;0</tt>.<p>
	 *
	 * Finally, the implementer must ensure that <tt>compare(x, y)==0</tt>
	 * implies that <tt>sgn(compare(x, z))==sgn(compare(y, z))</tt> for all
	 * <tt>z</tt>.<p>
	 *
	 */
	private static float compare(float value1, float value2, boolean isAscending) {
		if (isAscending) return value1 - value2;
		return value2 - value1;
	}
	private static double compare(double value1, double value2, boolean isAscending) {
		if (isAscending) return value1 - value2;
		return value2 - value1;
	}
	private static int compare(int value1, int value2, boolean isAscending) {
		if (isAscending) return value1 - value2;
		return value2 - value1;
	}
	/** Swaps x[a] with x[b]. */
	private static void swap(float x[], int a, int b) {
		float t = x[a]; x[a] = x[b]; x[b] = t;
	}
	/** Swaps x[a] with x[b]. */
	private static void swap(double x[], int a, int b) {
		double t = x[a]; x[a] = x[b]; x[b] = t;
	}
	/** Swaps x[a] with x[b]. */
	private static void swap(int x[], int a, int b) {
		int t = x[a]; x[a] = x[b]; x[b] = t;
	}

	/** Swaps x[a .. (a+n-1)] with x[b .. (b+n-1)].*/
	private static void vecswap(float x[], int a, int b, int n) {
		for (int i=0; i<n; i++, a++, b++)
			swap(x, a, b);
	}
	/** Swaps x[a .. (a+n-1)] with x[b .. (b+n-1)].*/
	private static void vecswap(double x[], int a, int b, int n) {
		for (int i=0; i<n; i++, a++, b++)
			swap(x, a, b);
	}
	/** Swaps x[a .. (a+n-1)] with x[b .. (b+n-1)].*/
	private static void vecswap(int x[], int a, int b, int n) {
		for (int i=0; i<n; i++, a++, b++)
			swap(x, a, b);
	}

	/**
	 * Check that fromIndex and toIndex are in range, and throw an
	 * appropriate exception if they aren't.
	 */
	private static void rangeCheck(int arrayLen, int fromIndex, int toIndex) {
		if (fromIndex > toIndex)
			throw new IllegalArgumentException("fromIndex(" + fromIndex +
					") > toIndex(" + toIndex+")");
		if (fromIndex < 0)
			throw new ArrayIndexOutOfBoundsException("from index: "+fromIndex);
		if (toIndex > arrayLen)
			throw new ArrayIndexOutOfBoundsException("to index: "+toIndex);
	}
}
