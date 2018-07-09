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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.io.*;
import processing.core.*;

/**
 * <p>Ranking based on the natural ordering on floats for a sequence of data that may also 
 * contain NaNs.</p>
 * <p>When present, NaNs are treated according to the configured NaNStrategy constants and ties
 * are handled using the configured tiesStrategy constants as follows:
 * 
 * <p> Strategies for handling NaN values in rank transformations.
 * <ul>
 * <li>0 (REMOVED, default) - NaNs are removed before the rank transform is applied</li> 
 * <li>1 (MINIMAL) - NaNs are treated as minimal in the ordering, equivalent to
 * (that is, tied with) <code>Float.NEGATIVE_INFINITY</code>.</li>
 * <li>2 (MAXIMAL) - NaNs are treated as maximal in the ordering, equivalent to
 * <code>Float.POSITIVE_INFINITY</code></li>
 * <li>3 (FIXED) - NaNs are left "in place," that is the rank transformation is
 * applied to the other elements in the input array, but the NaN elements
 * are returned unchanged.</li>
 * </ul>
 * </p>
 * 
 * <p>Strategies for handling tied values in rank transformations:
 * <ul>
 * <li>0 (AVERAGE, default) - Tied values are assigned the average of the applicable ranks.
 * For example, (1,3,4,3) is ranked as (1,2.5,4,2.5)</li>
 * <li>1 (MINIMUM) - Tied values are assigned the minimum applicable rank, or the rank
 * of the first occurrence. For example, (1,3,4,3) is ranked as (1,2,4,2)</li>
 * <li>2 (MAXIMUM) - Tied values are assigned the maximum applicable rank, or the rank
 * of the last occurrence. For example, (1,3,4,3) is ranked as (1,3,4,3)</li>
 * <li>3 (SEQUENTIAL) - Ties are assigned ranks in order of occurrence in the original array,
 * for example (1,3,4,3) is ranked as (1,2,4,3)</li>
  </ul>
 * </p>
 *
 * The defaults are 0 (REMOVED) and 0 (AVERAGE) for the NaNStrategy and TiesStrategy respectively.
 *
 * <p>Examples:
 * <table border="1" cellpadding="3">
 * <tr><th colspan="3">
 * Input data: (20, 17, 30, 42.3, 17, 50, Float.NaN, Float.NEGATIVE_INFINITY, 17)
 * </th></tr>
 * <tr><th>NaNStrategy</th><th>TiesStrategy</th>
 * <th><code>rank(data)</code></th>
 * <tr>
 * <td>0 (default = NaNs removed)</td>
 * <td>0 (default = ties averaged)</td>
 * <td>(5, 3, 6, 7, 3, 8, 1, 3)</td></tr>
 * <tr>
 * <td>0 (default = NaNs removed)</td>
 * <td>1 (MINIMUM)</td>
 * <td>(5, 2, 6, 7, 2, 8, 1, 2)</td></tr>
 * <tr>
 * <td>1 (MINIMAL)</td>
 * <td>0 (default = ties averaged)</td>
 * <td>(6, 4, 7, 8, 4, 9, 1.5, 1.5, 4)</td></tr>
 * <tr>
 * <td>1 (MINIMAL)</td>
 * <td>2 (MAXIMUM)</td>
 * <td>(6, 5, 7, 8, 5, 9, 2, 2, 5)</td></tr>
 * <tr>
 * <td>2 (MAXIMAL)</td>
 * <td>2 (MAXIMUM)/td>
 * <td>(5, 4, 6, 7, 4, 8, 9, 1, 4)</td></tr>
 * </table></p>
 *
 * (Code adapted from the <a href="http://commons.apache.org/math/userguide/stat.html#a1.6_Rank_transformations"
 * target="_blank">org.apache.commons.math.stat.ranking</a> package, and modified extensively).
 */
public class Rank {
	
	// NaNs ----------------------------------------
	/** NaNs are removed before computing ranks. Corresponds to default */
	private static final int REMOVED = 0; 

	/** NaNs are considered maximal in the ordering */
	private static final int MINIMAL = 1;		
	
	/** NaNs are considered minimal in the ordering */
	private static final int MAXIMAL = 2;
	
	/** NaNs are left in place */
	private static final int FIXED = 3;

	
	// TIES ----------------------------------------
    /** Ties get the average of applicable ranks. Corresponds to default */
	private static final int AVERAGE = 0;
	
    /** Ties get the minimum applicable rank */
	private static final int MINIMUM =1;

    /** Ties get the maximum applicable rank */
	private static final int  MAXIMUM = 2;

	/** Ties assigned sequential ranks in order of occurrence */
	private static final int SEQUENTIAL = 3;
	
	/**
	 * Makes this class non instantiable, but still let's others inherit from it.
	 */
	protected Rank(){}
	
	/**
	 * Rank an array containing NaN values using the natural ordering on Floats, with
	 * NaN values handled according to <code>nanStrategy</code> and ties
	 * resolved using <code>tiesStrategy</code>. 
	 * 
	 * <p>Input values that specify which strategy to use for handling tied values in the 
	 * rank transformations:
	 * <ul>
	 * <li>0 (AVERAGE, default) - Tied values are assigned the average of the applicable ranks.
	 * For example, (1,3,4,3) is ranked as (1,2.5,4,2.5)</li>
	 * <li>1 (MINIMUM) - Tied values are assigned the minimum applicable rank, or the rank
	 * of the first occurrence. For example, (1,3,4,3) is ranked as (1,2,4,2)</li>
	 * <li>2 (MAXIMUM) - Tied values are assigned the maximum applicable rank, or the rank
	 * of the last occurrence. For example, (1,3,4,3) is ranked as (1,3,4,3)</li>
	 * <li>3 (SEQUENTIAL) - Ties are assigned ranks in order of occurrence in the original array,
	 * for example (1,3,4,3) is ranked as (1,2,4,3)</li>
     * </ul>
	 * </p>
     *
	 * <p>Input values that specify which strategy to use for handling NaN values in the 
	 * rank transformations:
	 * <ul>
	 * <li>0 (REMOVED, default) - NaNs are removed before the rank transform is applied</li> 
	 * <li>1 (MINIMAL) - NaNs are treated as minimal in the ordering, equivalent to
	 * (that is, tied with) <code>Float.NEGATIVE_INFINITY</code>.</li>
	 * <li>2 (MAXIMAL) - NaNs are treated as maximal in the ordering, equivalent to
	 * <code>Float.POSITIVE_INFINITY</code></li>
	 * <li>3 (FIXED) - NaNs are left "in place," that is the rank transformation is
	 * applied to the other elements in the input array, but the NaN elements
	 * are returned unchanged.</li>
	 * </ul>
	 * </p>
	 * If the data array has no NaN values, use {@link rank(float[], int)} instead. It is quicker.
     * @param data array to be ranked. This is cast to a float array prior to ranking.
     * @param nanStrategy 0,1,2 or 3 corresponding to the NaN strategy to employ.
     * @param tiesStrategy 0,1,2 or 3 corresponding to the ties strategy to employ.
     * @return array of ranks
     */
    public static float[] rank(int[] data, int tiesStrategy, int nanStrategy) {
        checkNaNStrategy(nanStrategy);
        checkTiesStrategy(tiesStrategy);
        // Array recording initial positions of data to be ranked
        IntFloatPair[] ranks = new IntFloatPair[data.length];
        for (int i = 0; i < data.length; i++) {
            ranks[i] = new IntFloatPair((float)data[i], i);
        }        
        List<Integer> nanPositions  = nanStrategy(ranks, nanStrategy);
        // Sort the IntFloatPairs
        Arrays.sort(ranks);
        float[] out = new float[ranks.length];
        rankIt(ranks,out, tiesStrategy);
        
        if (nanStrategy==FIXED){
            restoreNaNs(out, nanPositions);
        }
        return out;
    }

	/**
	 * Rank an array containing NaN values using the natural ordering on Floats, with
	 * NaN values handled according to <code>nanStrategy</code> and ties
	 * resolved using <code>tiesStrategy</code>. 
	 * 
	 * <p>Input values that specify which strategy to use for handling tied values in the 
	 * rank transformations:
	 * <ul>
	 * <li>0 (AVERAGE, default) - Tied values are assigned the average of the applicable ranks.
	 * For example, (1,3,4,3) is ranked as (1,2.5,4,2.5)</li>
	 * <li>1 (MINIMUM) - Tied values are assigned the minimum applicable rank, or the rank
	 * of the first occurrence. For example, (1,3,4,3) is ranked as (1,2,4,2)</li>
	 * <li>2 (MAXIMUM) - Tied values are assigned the maximum applicable rank, or the rank
	 * of the last occurrence. For example, (1,3,4,3) is ranked as (1,3,4,3)</li>
	 * <li>3 (SEQUENTIAL) - Ties are assigned ranks in order of occurrence in the original array,
	 * for example (1,3,4,3) is ranked as (1,2,4,3)</li>
     * </ul>
	 * </p>
     *
	 * <p>Input values that specify which strategy to use for handling NaN values in the 
	 * rank transformations:
	 * <ul>
	 * <li>0 (REMOVED, default) - NaNs are removed before the rank transform is applied</li> 
	 * <li>1 (MINIMAL) - NaNs are treated as minimal in the ordering, equivalent to
	 * (that is, tied with) <code>Float.NEGATIVE_INFINITY</code>.</li>
	 * <li>2 (MAXIMAL) - NaNs are treated as maximal in the ordering, equivalent to
	 * <code>Float.POSITIVE_INFINITY</code></li>
	 * <li>3 (FIXED) - NaNs are left "in place," that is the rank transformation is
	 * applied to the other elements in the input array, but the NaN elements
	 * are returned unchanged.</li>
	 * </ul>
	 * </p>
	 * If the data array has no NaN values, use {@link rank(float[], int)} instead. It is quicker.
     * @param data array to be ranked
     * @param nanStrategy 0,1,2 or 3 corresponding to the NaN strategy to employ.
     * @param tiesStrategy 0,1,2 or 3 corresponding to the ties strategy to employ.
     * @return array of ranks
     */
    public static float[] rank(float[] data, int tiesStrategy, int nanStrategy) {
        checkNaNStrategy(nanStrategy);
        checkTiesStrategy(tiesStrategy);
        // Array recording initial positions of data to be ranked
        IntFloatPair[] ranks = new IntFloatPair[data.length];
        for (int i = 0; i < data.length; i++) {
            ranks[i] = new IntFloatPair(data[i], i);
        }        
        List<Integer> nanPositions  = nanStrategy(ranks, nanStrategy);
        // Sort the IntFloatPairs
        Arrays.sort(ranks);
        float[] out = new float[ranks.length];
        rankIt(ranks,out, tiesStrategy);
        
        if (nanStrategy==FIXED){
            restoreNaNs(out, nanPositions);
        }
        return out;
    }
    
    /**
	 * Rank an array (with no NaNs) using the natural ordering on Floats with ties
	 * resolved using <code>tiesStrategy</code>. 
     * <p>Input values that specify which strategy to use for handling NaN values in the 
	 * rank transformations:
	 * <ul>
	 * <li>0 (AVERAGE, default) - Tied values are assigned the average of the applicable ranks.
	 * For example, (1,3,4,3) is ranked as (1,2.5,4,2.5)</li>
	 * <li>1 (MINIMUM) - Tied values are assigned the minimum applicable rank, or the rank
	 * of the first occurrence. For example, (1,3,4,3) is ranked as (1,2,4,2)</li>
	 * <li>2 (MAXIMUM) - Tied values are assigned the maximum applicable rank, or the rank
	 * of the last occurrence. For example, (1,3,4,3) is ranked as (1,3,4,3)</li>
	 * <li>3 (SEQUENTIAL) - Ties are assigned ranks in order of occurrence in the original array,
	 * for example (1,3,4,3) is ranked as (1,2,4,3)</li>
    * </ul>
	 * </p>
    *
    * @param data array to be ranked. The array is cast to a float array prior to ranking
    * @param tiesStrategy the strategy to employ for ties.
    * @return array of ranks
    */
    public static float[] rank(int[] data, int tiesStrategy) {
    	checkTiesStrategy(tiesStrategy);
    	// Array recording initial positions of data to be ranked
        IntFloatPair[] ranks = new IntFloatPair[data.length];
        for (int i = 0; i < data.length; i++) {
            ranks[i] = new IntFloatPair((float)data[i], i);
        } 
       // Sort the IntFloatPairs
        Arrays.sort(ranks);
        float[] out = new float[ranks.length];
        rankIt(ranks,out,tiesStrategy);
        return out;
    }
    
    /**
	 * Rank an array (with no NaNs) using the natural ordering on Floats with ties
	 * resolved using <code>tiesStrategy</code>. 
     * <p>Input values that specify which strategy to use for handling NaN values in the 
	 * rank transformations:
	 * <ul>
	 * <li>0 (AVERAGE, default) - Tied values are assigned the average of the applicable ranks.
	 * For example, (1,3,4,3) is ranked as (1,2.5,4,2.5)</li>
	 * <li>1 (MINIMUM) - Tied values are assigned the minimum applicable rank, or the rank
	 * of the first occurrence. For example, (1,3,4,3) is ranked as (1,2,4,2)</li>
	 * <li>2 (MAXIMUM) - Tied values are assigned the maximum applicable rank, or the rank
	 * of the last occurrence. For example, (1,3,4,3) is ranked as (1,3,4,3)</li>
	 * <li>3 (SEQUENTIAL) - Ties are assigned ranks in order of occurrence in the original array,
	 * for example (1,3,4,3) is ranked as (1,2,4,3)</li>
    * </ul>
	 * </p>
    *
    * @param data array to be ranked.
    * @param tiesStrategy the strategy to employ for ties.
    * @return array of ranks
    */
    public static float[] rank(float[] data, int tiesStrategy) {
    	checkTiesStrategy(tiesStrategy);
    	// Array recording initial positions of data to be ranked
        IntFloatPair[] ranks = new IntFloatPair[data.length];
        for (int i = 0; i < data.length; i++) {
            ranks[i] = new IntFloatPair(data[i], i);
        } 
       // Sort the IntFloatPairs
        Arrays.sort(ranks);
        float[] out = new float[ranks.length];
        rankIt(ranks,out,tiesStrategy);
        return out;
    }
    
    /** The actual ranking takes place here. 
	 * @param ranks: the IntFloat pair of ranks
	 * @param out: the output ranks */
	private static void rankIt(IntFloatPair[] ranks, float[] out, int tiesStrategy){      
	    int pos = 1;  // position in sorted array

        // Walk the sorted array, filling output array using sorted positions,
        // resolving ties as we go
	    out[ranks[0].getPosition()] = pos;
	    List<Integer> tiesTrace = new ArrayList<Integer>();
	    tiesTrace.add(ranks[0].getPosition());
	    for (int i = 1; i < ranks.length; i++) {
	        if (Float.compare(ranks[i].getValue(), ranks[i - 1].getValue()) > 0) {
	            // tie sequence has ended (or had length 1)
	            pos = i + 1;
	            if (tiesTrace.size() > 1) {  // if seq is nontrivial, resolve
	                resolveTie(out, tiesTrace, tiesStrategy);
	            }
	            tiesTrace = new ArrayList<Integer>();
	            tiesTrace.add(ranks[i].getPosition());
	        } else {
	            // tie sequence continues
	            tiesTrace.add(ranks[i].getPosition());
	        }
	        out[ranks[i].getPosition()] = pos;
	    }
	    if (tiesTrace.size() > 1) {  // handle tie sequence at end
	        resolveTie(out, tiesTrace, tiesStrategy);
	    }
	}

	/* Check if the nanStrategy input is one of those specified */
    private static void checkNaNStrategy(int num){
    	if(num<0 || num>3) throw new IllegalArgumentException("NaN strategy option has to be 0, 1, 2 or 3.");
    }
    /* Check if the tiesStrategy input is one of those specified */
    private static void checkTiesStrategy(int num){
    	if(num<0 || num>3) throw new IllegalArgumentException("Ties strategy option has to be 0, 1, 2 or 3.");
    }
    
    /** Decide how to proceed with the NaN values */
    private static List<Integer> nanStrategy(IntFloatPair[] ranks, int nanStrategy){
    	List<Integer> nanPositions = null;

    	// Recode, remove or record positions of NaNs        
    	switch (nanStrategy) {
    	case MAXIMAL: // Replace NaNs with +INFs
    		recodeNaNs(ranks, Float.POSITIVE_INFINITY);
    		break;
    	case MINIMAL: // Replace NaNs with -INFs
    		recodeNaNs(ranks, Float.NEGATIVE_INFINITY);
    		break;
    	case REMOVED: // Drop NaNs from data
    		ranks = removeNaNs(ranks);
    		break;
    	case FIXED:   // Record positions of NaNs
    		nanPositions = getNanPositions(ranks);
    		break;
    	}
    	return nanPositions;
    }
    
    /**
     * Returns an array that is a copy of the input array with IntFloatPairs
     * having NaN values removed.
     * @param ranks input array
     * @return array with NaN-valued entries removed
     */
    private static IntFloatPair[] removeNaNs(IntFloatPair[] ranks) {
        if (!containsNaNs(ranks)) {
            return ranks;
        }
        IntFloatPair[] outRanks = new IntFloatPair[ranks.length];
        int j = 0;
        for (int i = 0; i < ranks.length; i++) {
            if (Float.isNaN(ranks[i].getValue())) {
                // drop, but adjust original ranks of later elements
                for (int k = i + 1; k < ranks.length; k++) {
                    ranks[k] = new IntFloatPair(
                            ranks[k].getValue(), ranks[k].getPosition() - 1);
                }
            } else {
                outRanks[j] = new IntFloatPair(
                        ranks[i].getValue(), ranks[i].getPosition());
                j++;
            }
        }
        IntFloatPair[] returnRanks = new IntFloatPair[j];
        System.arraycopy(outRanks, 0, returnRanks, 0, j);
        return returnRanks;
    }

    /**
     * Recodes NaN values to the given value.
     *
     * @param ranks array to recode
     * @param value the value to replace NaNs with
     */
    private  static void recodeNaNs(IntFloatPair[] ranks, float value) {
        for (int i = 0; i < ranks.length; i++) {
            if (Float.isNaN(ranks[i].getValue())) {
                ranks[i] = new IntFloatPair(
                        value, ranks[i].getPosition());
            }
        }
    }

    /**
     * Checks for presence of NaNs in <code>ranks.</code>
     *
     * @param ranks array to be searched for NaNs
     * @return true iff ranks contains one or more NaNs
     */
    private static boolean containsNaNs(IntFloatPair[] ranks) {
        for (int i = 0; i < ranks.length; i++) {
            if (Float.isNaN(ranks[i].getValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Resolve a sequence of ties, using the configured TiesStrategy.
     * The input <code>ranks</code> array is expected to take the same value
     * for all indices in <code>tiesTrace</code>.  The common value is recoded
     * according to the tiesStrategy. For example, if ranks = <5,8,2,6,2,7,1,2>,
     * tiesTrace = <2,4,7> and tiesStrategy is MINIMUM, ranks will be unchanged.
     * The same array and trace with tiesStrategy AVERAGE will come out
     * <5,8,3,6,3,7,1,3>.
     *
     * @param ranks array of ranks
     * @param tiesTrace list of indices where <code>ranks</code> is constant
     * -- that is, for any i and j in TiesTrace, <code> ranks[i] == ranks[j]
     * </code>
     */
    private static void resolveTie(float[] ranks, List<Integer> tiesTrace, int tiesStrategy) {

        // constant value of ranks over tiesTrace
        final float c = ranks[tiesTrace.get(0)];

        // length of sequence of tied ranks
        final int length = tiesTrace.size();

        switch (tiesStrategy) {
            case  AVERAGE:  // Replace ranks with average
            	double tmp = (2 * c + length - 1) / 2d;
                fill(ranks, tiesTrace, (float)tmp);
                break;
            case MAXIMUM:   // Replace ranks with maximum values
                fill(ranks, tiesTrace, c + length - 1);
                break;
            case MINIMUM:   // Replace ties with minimum
                fill(ranks, tiesTrace, c);
                break;           
            case SEQUENTIAL:  // Fill sequentially from c to c + length - 1
                // walk and fill
            	Iterator<Integer> iterator = tiesTrace.iterator();
                float f = (float)Math.round(c);
                int i = 0;
                while (iterator.hasNext()) {
                    ranks[iterator.next()] = f + i++;
                }
                break;         
        }
    }

    /**
     * Sets<code>data[i] = value</code> for each i in <code>tiesTrace.</code>
     *
     * @param data array to modify
     * @param tiesTrace list of index values to set
     * @param value value to set
     */
    private static void fill(float[] data, List<Integer> tiesTrace, float value) {
        Iterator<Integer> iterator = tiesTrace.iterator();
        while (iterator.hasNext()) {
            data[iterator.next()] = value;
        }
    }

    /**
     * Set <code>ranks[i] = Float.NaN</code> for each i in <code>nanPositions.</code>
     *
     * @param ranks array to modify
     * @param nanPositions list of index values to set to <code>Float.NaN</code>
     */
    private static void restoreNaNs(float[] ranks, List<Integer> nanPositions) {
        if (nanPositions.size() == 0) {
            return;
        }
        Iterator<Integer> iterator = nanPositions.iterator();
        while (iterator.hasNext()) {
            ranks[iterator.next().intValue()] = Float.NaN;
        }

    }

    /**
     * Returns a list of indexes where <code>ranks</code> is <code>NaN.</code>
     *
     * @param ranks array to search for <code>NaNs</code>
     * @return list of indexes i such that <code>ranks[i] = NaN</code>
     */
    private static List<Integer> getNanPositions(IntFloatPair[] ranks) {
        ArrayList<Integer> out = new ArrayList<Integer>();
        for (int i = 0; i < ranks.length; i++) {
            if (Float.isNaN(ranks[i].getValue())) {
                out.add(Integer.valueOf(i));
            }
        }
        return out;
    }

	
    /**
     * Represents the position of a float value in an ordering.
     * Comparable interface is implemented so Arrays.sort can be used
     * to sort an array of IntFloatPairs by value.  Note that the
     * implicitly defined natural ordering is NOT consistent with equals.
     */
    private static class IntFloatPair implements Comparable<IntFloatPair>  {

        /** Value of the pair */
        private final float value;

        /** Original position of the pair */
        private final int position;

        /**
         * Construct an IntFloatPair with the given value and position.
         * @param value the value of the pair
         * @param position the original position
         */
        public IntFloatPair(float value, int position) {
            this.value = value;
            this.position = position;
        }

        /**
         * Compare this IntFloatPair to another pair.
         * Only the <strong>values</strong> are compared.
         *
         * @param other the other pair to compare this to
         * @return result of <code>Float.compare(value, other.value)</code>
         */
        public int compareTo(IntFloatPair other) {
            return Float.compare(value, other.value);
        }

        /**
         * Returns the value of the pair.
         * @return value
         */
        public float getValue() {
            return value;
        }

        /**
         * Returns the original position of the pair.
         * @return position
         */
        public int getPosition() {
            return position;
        }
    }
}

//public class Rank {
//	private int[] rank;//= new int[numProducts];
//	private int[] idx;// = new int[numProducts]; 
//
//	public Rank(float[] sortedArray) {
//		int size = sortedArray.length;
//		rankTemp = new int[size];
//		int i=0;
//		while(i<sortedArray.length)) {
//			int j =i+1; int numRepeats = 1;
//			int r =(i+1); 
//			rank[i] = i+1;
//			while(sortedArray[j++]==sortedArray[i]){
//				r+=j+1;
//				numRepeats++; j++;
//			}
//			r /=numRepeats;
//			for(int k=i; k < j; k++){
//				rank[k] = r;
//			}
//			i=j;			
//		}
//	}
//
//	public int[] getRank() {
//		return rank;
//	}
//
//	public int[] getIndexSort(){
//		return idx;
//	}
//}