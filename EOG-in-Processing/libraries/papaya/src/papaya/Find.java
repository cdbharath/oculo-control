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
 * Static class for finding indices in an array corresponding to a given value/object. 
 * <p>
 * (Portions of this code were (conveniently!) copied and pasted from <a href="http://commons.apache.org/lang/download_lang.cgi/" 
 * target="_blank">commons.apache.org</a>.) :)
 * </p>
 *
 */
public final class Find implements PapayaConstants {

//	/**
//	 * Index to return if not found. 
//	 */
//	public static final int INDEX_NOT_FOUND = -1;
//	
//	/**
//	 * Index array to return if not found. 
//	 */
//	public static final int[] INDICES_NOT_FOUND = new int[]{-1};

	/**
	 * Makes this class non instantiable, but still let's others inherit from it.
	 */
	protected Find(){
	}
	
	// IndexOf search
    // ----------------------------------------------------------------------

    // Object IndexOf
    //-----------------------------------------------------------------------
    /**
     * <p>Finds the index of the given object in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param objectToFind  the object to find, may be {@code null}
     * @return the index of the object within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int indexOf(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind, 0);
    }

    /**
     * <p>Finds the index of the given object in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param objectToFind  the object to find, may be {@code null}
     * @param startIndex  the index to start searching at
     * @return the index of the object within the array starting at the index,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int indexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else if (array.getClass().getComponentType().isInstance(objectToFind)) {
            for (int i = startIndex; i < array.length; i++) {
                if (objectToFind.equals(array[i])) { 
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given object within the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to travers backwords looking for the object, may be {@code null}
     * @param objectToFind  the object to find, may be {@code null}
     * @return the last index of the object within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int lastIndexOf(Object[] array, Object objectToFind) {
        return lastIndexOf(array, objectToFind, Integer.MAX_VALUE);
    }

    /**
     * <p>Finds the last index of the given object in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} ({@code -1}). A startIndex larger than
     * the array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be {@code null}
     * @param objectToFind  the object to find, may be {@code null}
     * @param startIndex  the start index to travers backwards from
     * @return the last index of the object within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int lastIndexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i >= 0; i--) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else if (array.getClass().getComponentType().isInstance(objectToFind)) {
            for (int i = startIndex; i >= 0; i--) {
                if (objectToFind.equals(array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if the object is in the given array.</p>
     *
     * <p>The method returns {@code false} if a {@code null} array is passed in.</p>
     *
     * @param array  the array to search through
     * @param objectToFind  the object to find
     * @return {@code true} if the array contains the object
     */
    public static boolean contains(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind) != INDEX_NOT_FOUND;
    }
    
    /**
     * <p>Finds the indices containing the specified object in the array.</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the object to find, may be {@code null}
     * @param objectToFind  the object to find
     * @return the indices containing the object within the array,
     *  {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) if not found or {@code null} array input
     */
    public static int[] indicesWith(Object[] array, Object objectToFind) {
        return indicesWith(array, objectToFind, 0);
    }
    
    /**
     * <p>Finds the indices containing the specified object in the array.</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the object to find, may be {@code null}
     * @param objectToFind  the object to find
     * @param startIndex  the index to start searching at
     * @return the indices containing the object within the array,
     *  {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) if not found or {@code null} array input
     */
    public static int[] indicesWith(Object[] array, Object objectToFind, int startIndex) {
    	if (array == null) {
            return INDICES_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        Vector indices = new Vector();
        
        if (objectToFind == null) {
            for (int i = startIndex; i < array.length; i++) {
                if (array[i] == null) {
                    indices.add(i);
                }
            }
        } else if (array.getClass().getComponentType().isInstance(objectToFind)) {
            for (int i = startIndex; i < array.length; i++) {
                if (objectToFind.equals(array[i])) {
                    indices.add(i);
                }
            }
        }
        
        if(indices.size()>0){
        	return Cast.vectorToInt(indices);
        }
        else{      
        	return INDICES_NOT_FOUND;
        }
    }
    
    /**
     * <p>Finds the number of times a given value/object is present in an array.</p>
     *
     * @param array  the array to search through for the value/object
     * @param objectToFind  the object to find.
     * @return the number of times that value/object appears in the array
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
	public static int numRepeats(Object[] array, Object objectToFind) {
		int count=0; 
		if (objectToFind == null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == null) {
                    count++;
                }
            }
        } else if (array.getClass().getComponentType().isInstance(objectToFind)) {
            for (int i = 0; i < array.length; i++) {
                if (objectToFind.equals(array[i])) {
                    count++;
                }
            }
        }
		return count;
	}
    

    // long IndexOf
    //-----------------------------------------------------------------------
    /**
     * <p>Finds the index of the given value in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int indexOf(long[] array, long valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * <p>Finds the index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int indexOf(long[] array, long valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given value within the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to travers backwords looking for the object, may be {@code null}
     * @param valueToFind  the object to find
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int lastIndexOf(long[] array, long valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * <p>Finds the last index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} ({@code -1}). A startIndex larger than the
     * array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the start index to travers backwards from
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int lastIndexOf(long[] array, long valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if the value is in the given array.</p>
     *
     * <p>The method returns {@code false} if a {@code null} array is passed in.</p>
     *
     * @param array  the array to search through
     * @param valueToFind  the value to find
     * @return {@code true} if the array contains the object
     */
    public static boolean contains(long[] array, long valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }
    
    /**
     * <p>Finds the indices containing the specified value in the array.</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the value to find, may be {@code null}
     * @param valueToFind  the value to find
     * @return the indices containing the value within the array,
     *  {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) if not found or {@code null} array input
     */
    public static int[] indicesWith(long[] array, long valueToFind) {
        return indicesWith(array, valueToFind, 0);
    }
    
    /**
     * <p>Finds the indices containing the specified value in the array.</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the value to find, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the indices containing the value within the array,
     *  {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) if not found or {@code null} array input
     */
    public static int[] indicesWith(long[] array, long valueToFind, int startIndex) {
    	if (array == null) {
    		return INDICES_NOT_FOUND;
    	}
    	if (startIndex < 0) {
    		startIndex = 0;
    	}
    	Vector indices = new Vector();
    	for (int i = startIndex; i < array.length; i++) {
    		if (array[i] == valueToFind) {
    			indices.add(i);
    		}
    	}
    	return checkIndices(indices);
    }
    
    /**
     * <p>Finds the number of times a given value/object is present in an array.</p>
     *
     * @param array  the array to search through for the value/object
     * @param valueToFind  the object to find.
     * @return the number of times that value/object appears in the array
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
	public static int numRepeats(long[] array, long valueToFind) {
		int count=0; 
		for (int i=0; i<array.length; i++) {
			if (array[i] == valueToFind){
				count++;
			}
		}
		return count;
	}

    // int IndexOf
    //-----------------------------------------------------------------------
    /**
     * <p>Finds the index of the given value in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int indexOf(int[] array, int valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * <p>Finds the index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int indexOf(int[] array, int valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given value within the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to travers backwords looking for the object, may be {@code null}
     * @param valueToFind  the object to find
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int lastIndexOf(int[] array, int valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * <p>Finds the last index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} ({@code -1}). A startIndex larger than the
     * array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the start index to travers backwards from
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int lastIndexOf(int[] array, int valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if the value is in the given array.</p>
     *
     * <p>The method returns {@code false} if a {@code null} array is passed in.</p>
     *
     * @param array  the array to search through
     * @param valueToFind  the value to find
     * @return {@code true} if the array contains the object
     */
    public static boolean contains(int[] array, int valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }
    
    /**
     * <p>Finds the indices containing the specified value in the array.</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the value to find, may be {@code null}
     * @param valueToFind  the value to find
     * @return the indices containing the value within the array,
     *  {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) if not found or {@code null} array input
     */
    public static int[] indicesWith(int[] array, int valueToFind) {
        return indicesWith(array, valueToFind, 0);
    }
    
    /**
     * <p>Finds the indices containing the specified value in the array.</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the value to find, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the indices containing the value within the array,
     *  {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) if not found or {@code null} array input
     */
    public static int[] indicesWith(int[] array, int valueToFind, int startIndex) {
    	if (array == null) {
    		return INDICES_NOT_FOUND;
    	}
    	if (startIndex < 0) {
    		startIndex = 0;
    	}
    	Vector indices = new Vector();
    	for (int i = startIndex; i < array.length; i++) {
    		if (array[i] == valueToFind) {
    			indices.add(i);
    		}
    	}
    	return checkIndices(indices);
    }
    /**
     * <p>Finds the indices for the array elements within the min and max value (inclusive).</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     */
    public static int[] indicesWithin(int[] array, float minVal, float maxVal) {
    	if (array == null) {
    		return INDICES_NOT_FOUND;
    	}
    	Vector indices = new Vector();
    	for (int i = 0; i < array.length; i++) {
    		if (array[i] >= minVal && array[i]<= maxVal) {
    			indices.add(i);
    		}
    	}
    	return checkIndices(indices);
    }
    
    public static int[] indicesLessThanOrEqualTo(int[] array, int valueOfMax){
		ArrayList indices = new ArrayList();
		for(int i=0; i<array.length; i++){
			if(array[i]<=valueOfMax) indices.add(i);
		}
		return Cast.arrayListToInt(indices);
	}
	
	public static int[] indicesLessThan(int[] array, int valueOfMax){
		ArrayList indices = new ArrayList();
		for(int i=0; i<array.length; i++){
			if(array[i]<valueOfMax) indices.add(i);
		}
		return Cast.arrayListToInt(indices);
	}
	
	public static int[] indicesGreaterThanOrEqualTo(int[] array, int valueOfMin){
		ArrayList indices = new ArrayList();
		for(int i=0; i<array.length; i++){
			if(array[i]>=valueOfMin) indices.add(i);
		}
		return Cast.arrayListToInt(indices);
	}
	
	public static int[] indicesGreaterThan(int[] array, int valueOfMin){
		ArrayList indices = new ArrayList();
		for(int i=0; i<array.length; i++){
			if(array[i]>valueOfMin) indices.add(i);
		}
		return Cast.arrayListToInt(indices);
	}
	
    /**
     * <p>Finds the number of times a given value/object is present in an array.</p>
     *
     * @param array  the array to search through for the value/object
     * @param valueToFind  the object to find.
     * @return the number of times that value/object appears in the array
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
	public static int numRepeats(int[] array, int valueToFind) {
		int count=0; 
		for (int i=0; i<array.length; i++) {
			if (array[i] == valueToFind) {
				count++;
			}
		}
		return count;
	}
	
	/**
     * <p>Finds the number of elements less than or equal to the specified value.</p>
     *
     * @param array  the array to search through for the value/object
     * @param valueOfMax the maximum value
     * @return the number of elements ? that value
     */
	public static int numLessThanOrEqualTo(int[] array, int valueOfMax) {
		int count=0; 
		for (int i=0; i<array.length; i++) {
			if (array[i] <= valueOfMax) 	{
				count++;
			}
		}
		return count;
	}
	
	/**
     * <p>Finds the number of elements strictly less than the specified value.</p>
     *
     * @param array  the array to search through for the value/object
     * @param valueOfMax the maximum value
     * @return the number of elements strictly less than that value
     */
	public static int numLessThan(int[] array, int valueOfMax) {
		int count=0; 
		for (int i=0; i<array.length; i++) {
			if (array[i] < valueOfMax) 	{
				count++;
			}
		}
		return count;
	}
	
	/**
     * <p>Finds the number of elements greater than or equal to the specified value.</p>
     *
     * @param array  the array to search through for the value/object
     * @param valueOfMax the maximum value
     * @return the number of elements ? that value
     */
	public static int numGreaterThanOrEqualTo(int[] array, int valueOfMin) {
		int count=0; 
		for (int i=0; i<array.length; i++) {
			if (array[i] >= valueOfMin) 	{
				count++;
			}
		}
		return count;
	}
	
	/**
     * <p>Finds the number of elements strictly greater than the specified value.</p>
     *
     * @param array  the array to search through for the value/object
     * @param valueOfMax the maximum value
     * @return the number of elements strictly greater than that value
     */
	public static int numGreaterThan(int[] array, int valueOfMin) {
		int count=0; 
		for (int i=0; i<array.length; i++) {
			if (array[i] > valueOfMin) 	{
				count++;
			}
		}
		return count;
	}
	
	/** Find the unique elements in a int array (uses HashSets) */
	public static int[] uniqueElems(int[] array){
		Set<Integer> uniqueVals = new HashSet<Integer>();
		for(int i=0; i<array.length; i++){
			uniqueVals.add(array[i]);
		}
		return Cast.setToInt(uniqueVals);
	}
	
    // short IndexOf
    //-----------------------------------------------------------------------
    /**
     * <p>Finds the index of the given value in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int indexOf(short[] array, short valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * <p>Finds the index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int indexOf(short[] array, short valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given value within the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to travers backwords looking for the object, may be {@code null}
     * @param valueToFind  the object to find
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int lastIndexOf(short[] array, short valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * <p>Finds the last index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} ({@code -1}). A startIndex larger than the
     * array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the start index to travers backwards from
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int lastIndexOf(short[] array, short valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if the value is in the given array.</p>
     *
     * <p>The method returns {@code false} if a {@code null} array is passed in.</p>
     *
     * @param array  the array to search through
     * @param valueToFind  the value to find
     * @return {@code true} if the array contains the object
     */
    public static boolean contains(short[] array, short valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }
    
    /**
     * <p>Finds the indices containing the specified value in the array.</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the value to find, may be {@code null}
     * @param valueToFind  the value to find
     * @return the indices containing the value within the array,
     *  {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) if not found or {@code null} array input
     */
    public static int[] indicesWith(short[] array, short valueToFind) {
        return indicesWith(array, valueToFind, 0);
    }
    
    /**
     * <p>Finds the indices containing the specified value in the array.</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the value to find, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the indices containing the value within the array,
     *  {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) if not found or {@code null} array input
     */
    public static int[] indicesWith(short[] array, short valueToFind, int startIndex) {
    	if (array == null) {
    		return INDICES_NOT_FOUND;
    	}
    	if (startIndex < 0) {
    		startIndex = 0;
    	}
    	Vector indices = new Vector();
    	for (int i = startIndex; i < array.length; i++) {
    		if (array[i] == valueToFind) {
    			indices.add(i);
    		}
    	}
    	return checkIndices(indices);
    }
    /**
     * <p>Finds the number of times a given value/object is present in an array.</p>
     *
     * @param array  the array to search through for the value/object
     * @param valueToFind  the object to find.
     * @return the number of times that value/object appears in the array
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
	public static int numRepeats(short[] array, short valueToFind) {
		int count=0; 
		for (int i=0; i<array.length; i++) {
			if (array[i] == valueToFind) {
				count++;
			}
		}
		return count;
	}
    

    // char IndexOf
    //-----------------------------------------------------------------------
    /**
     * <p>Finds the index of the given value in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     * @since 2.1
     */
    public static int indexOf(char[] array, char valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * <p>Finds the index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     * @since 2.1
     */
    public static int indexOf(char[] array, char valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given value within the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to travers backwords looking for the object, may be {@code null}
     * @param valueToFind  the object to find
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     * @since 2.1
     */
    public static int lastIndexOf(char[] array, char valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * <p>Finds the last index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} ({@code -1}). A startIndex larger than the
     * array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the start index to travers backwards from
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     * @since 2.1
     */
    public static int lastIndexOf(char[] array, char valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if the value is in the given array.</p>
     *
     * <p>The method returns {@code false} if a {@code null} array is passed in.</p>
     *
     * @param array  the array to search through
     * @param valueToFind  the value to find
     * @return {@code true} if the array contains the object
     * @since 2.1
     */
    public static boolean contains(char[] array, char valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }
    
    /**
     * <p>Finds the indices containing the specified value in the array.</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the value to find, may be {@code null}
     * @param valueToFind  the value to find
     * @return the indices containing the value within the array,
     *  {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) if not found or {@code null} array input
     */
    public static int[] indicesWith(char[] array, char valueToFind) {
        return indicesWith(array, valueToFind, 0);
    }
    
    /**
     * <p>Finds the indices containing the specified value in the array.</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the value to find, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the indices containing the value within the array,
     *  {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) if not found or {@code null} array input
     */
    public static int[] indicesWith(char[] array, char valueToFind, int startIndex) {
    	if (array == null) {
    		return INDICES_NOT_FOUND;
    	}
    	if (startIndex < 0) {
    		startIndex = 0;
    	}
    	Vector indices = new Vector();
    	for (int i = startIndex; i < array.length; i++) {
    		if (array[i] == valueToFind) {
    			indices.add(i);
    		}
    	}
    	return checkIndices(indices);
    }
    
    /**
     * <p>Finds the number of times a given value/object is present in an array.</p>
     *
     * @param array  the array to search through for the value/object
     * @param valueToFind  the object to find.
     * @return the number of times that value/object appears in the array
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
	public static int numRepeats(char[] array,char valueToFind) {
		int count=0; 
		for (int i=0; i<array.length; i++) {
			if (array[i] == valueToFind) {
				count++;
			}
		}
		return count;
	}
    
    // byte IndexOf
    //-----------------------------------------------------------------------
    /**
     * <p>Finds the index of the given value in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int indexOf(byte[] array, byte valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * <p>Finds the index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int indexOf(byte[] array, byte valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given value within the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to travers backwords looking for the object, may be {@code null}
     * @param valueToFind  the object to find
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int lastIndexOf(byte[] array, byte valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * <p>Finds the last index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} ({@code -1}). A startIndex larger than the
     * array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the start index to travers backwards from
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int lastIndexOf(byte[] array, byte valueToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if the value is in the given array.</p>
     *
     * <p>The method returns {@code false} if a {@code null} array is passed in.</p>
     *
     * @param array  the array to search through
     * @param valueToFind  the value to find
     * @return {@code true} if the array contains the object
     */
    public static boolean contains(byte[] array, byte valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }
    
    /**
     * <p>Finds the indices containing the specified value in the array.</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the value to find, may be {@code null}
     * @param valueToFind  the value to find
     * @return the indices containing the value within the array,
     *  {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) if not found or {@code null} array input
     */
    public static int[] indicesWith(byte[] array, byte valueToFind) {
        return indicesWith(array, valueToFind, 0);
    }
    
    /**
     * <p>Finds the indices containing the specified value in the array.</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the value to find, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the indices containing the value within the array,
     *  {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) if not found or {@code null} array input
     */
    public static int[] indicesWith(byte[] array, byte valueToFind, int startIndex) {
    	if (array == null) {
    		return INDICES_NOT_FOUND;
    	}
    	if (startIndex < 0) {
    		startIndex = 0;
    	}
    	Vector indices = new Vector();
    	for (int i = startIndex; i < array.length; i++) {
    		if (array[i] == valueToFind) {
    			indices.add(i);
    		}
    	}
    	return checkIndices(indices);
    }
    
    /**
     * <p>Finds the number of times a given value/object is present in an array.</p>
     *
     * @param array  the array to search through for the value/object
     * @param valueToFind  the object to find.
     * @return the number of times that value/object appears in the array
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
	public static int numRepeats(byte[] array, byte valueToFind) {
		int count=0; 
		for (int i=0; i<array.length; i++) {
			if (array[i] == valueToFind)  {
				count++;
			}
		}
		return count;
	}
    

    // double IndexOf
    //-----------------------------------------------------------------------
    /**
     * <p>Finds the index of the given value in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int indexOf(double[] array, double valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * <p>Finds the index of the given value within a given tolerance in the array.
     * This method will return the index of the first value which falls between the region
     * defined by valueToFind - tolerance and valueToFind + tolerance.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param tolerance tolerance of the search
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int indexOf(double[] array, double valueToFind, double tolerance) {
        return indexOf(array, valueToFind, 0, tolerance);
    }

    /**
     * <p>Finds the index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int indexOf(double[] array, double valueToFind, int startIndex) {
    	if (array.length == 0) {
    		return INDEX_NOT_FOUND;
    	}
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the index of the given value in the array starting at the given index.
     * This method will return the index of the first value which falls between the region
     * defined by valueToFind - tolerance and valueToFind + tolerance.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @param tolerance tolerance of the search
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int indexOf(double[] array, double valueToFind, int startIndex, double tolerance) {
    	if (array.length == 0) {
    		return INDEX_NOT_FOUND;
    	}
        if (startIndex < 0) {
            startIndex = 0;
        }
        double min = valueToFind - tolerance;
        double max = valueToFind + tolerance;
        for (int i = startIndex; i < array.length; i++) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given value within the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to travers backwords looking for the object, may be {@code null}
     * @param valueToFind  the object to find
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int lastIndexOf(double[] array, double valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * <p>Finds the last index of the given value within a given tolerance in the array.
     * This method will return the index of the last value which falls between the region
     * defined by valueToFind - tolerance and valueToFind + tolerance.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param tolerance tolerance of the search
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int lastIndexOf(double[] array, double valueToFind, double tolerance) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE, tolerance);
    }

    /**
     * <p>Finds the last index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} ({@code -1}). A startIndex larger than the
     * array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the start index to travers backwards from
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int lastIndexOf(double[] array, double valueToFind, int startIndex) {
    	if (array.length == 0) {
    		return INDEX_NOT_FOUND;
    	}
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given value in the array starting at the given index.
     * This method will return the index of the last value which falls between the region
     * defined by valueToFind - tolerance and valueToFind + tolerance.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} ({@code -1}). A startIndex larger than the
     * array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the start index to travers backwards from
     * @param tolerance  search for value within plus/minus this amount
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int lastIndexOf(double[] array, double valueToFind, int startIndex, double tolerance) {
    	if (array.length == 0) {
    		return INDEX_NOT_FOUND;
    	}
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        double min = valueToFind - tolerance;
        double max = valueToFind + tolerance;
        for (int i = startIndex; i >= 0; i--) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if the value is in the given array.</p>
     *
     * <p>The method returns {@code false} if a {@code null} array is passed in.</p>
     *
     * @param array  the array to search through
     * @param valueToFind  the value to find
     * @return {@code true} if the array contains the object
     */
    public static boolean contains(double[] array, double valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if a value falling within the given tolerance is in the
     * given array.  If the array contains a value within the inclusive range
     * defined by (value - tolerance) to (value + tolerance).</p>
     *
     * <p>The method returns {@code false} if a {@code null} array
     * is passed in.</p>
     *
     * @param array  the array to search
     * @param valueToFind  the value to find
     * @param tolerance  the array contains the tolerance of the search
     * @return true if value falling within tolerance is in array
     */
    public static boolean contains(double[] array, double valueToFind, double tolerance) {
        return indexOf(array, valueToFind, 0, tolerance) != INDEX_NOT_FOUND;
    }
    
    /**
     * <p>Finds the indices containing the specified value in the array.</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the value to find, may be {@code null}
     * @param valueToFind  the value to find
     * @return the indices containing the value within the array,
     *  {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) if not found or {@code null} array input
     */
    public static int[] indicesWith(double[] array, double valueToFind) {
        return indicesWith(array, valueToFind, 0);
    }
    
    /**
     * <p>Finds the indices containing the specified value in the array.</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the value to find, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the indices containing the value within the array,
     *  {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) if not found or {@code null} array input
     */
    public static int[] indicesWith(double[] array, double valueToFind, int startIndex) {
    	if (array == null) {
    		return INDICES_NOT_FOUND;
    	}
    	if (startIndex < 0) {
    		startIndex = 0;
    	}
    	Vector indices = new Vector();
    	for (int i = startIndex; i < array.length; i++) {
    		if (array[i] == valueToFind) {
    			indices.add(i);
    		}
    	}
    	return checkIndices(indices);
    }
    
    /**
     * <p>Finds the number of times a given value/object is present in an array.</p>
     *
     * @param array  the array to search through for the value/object
     * @param valueToFind  the object to find.
     * @return the number of times that value/object appears in the array
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
	public static int numRepeats(double[] array, double valueToFind) {
		int count=0; 
		for (int i=0; i<array.length; i++) {
			if (array[i] == valueToFind) {
				count++;
			}
		}
		return count;
	}

    // float IndexOf
    //-----------------------------------------------------------------------
    /**
     * <p>Finds the index of the given value in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int indexOf(float[] array, float valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * <p>Finds the index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int indexOf(float[] array, float valueToFind, int startIndex) {
    	if (array.length == 0) {
    		return INDEX_NOT_FOUND;
    	}
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }


    /**
     * <p>Finds the last index of the given value within the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to travers backwords looking for the object, may be {@code null}
     * @param valueToFind  the object to find
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int lastIndexOf(float[] array, float valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * <p>Finds the last index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} ({@code -1}). A startIndex larger than the
     * array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the start index to travers backwards from
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int lastIndexOf(float[] array, float valueToFind, int startIndex) {
    	if (array.length == 0) {
    		return INDEX_NOT_FOUND;
    	}
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Checks if the value is in the given array.</p>
     *
     * <p>The method returns {@code false} if a {@code null} array is passed in.</p>
     *
     * @param array  the array to search through
     * @param valueToFind  the value to find
     * @return {@code true} if the array contains the object
     */
    public static boolean contains(float[] array, float valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }
    
    /**
     * <p>Finds the indices containing the specified value in the array.</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the value to find, may be {@code null}
     * @param valueToFind  the value to find
     * @return the indices containing the value within the array,
     *  {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) if not found or {@code null} array input
     */
    public static int[] indicesWith(float[] array, float valueToFind) {
        return indicesWith(array, valueToFind, 0);
    }
    
    /**
     * <p>Finds the indices containing the specified value in the array.</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the value to find, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the indices containing the value within the array,
     *  {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) if not found or {@code null} array input
     */
    public static int[] indicesWith(float[] array, float valueToFind, int startIndex) {
    	if (array == null) {
    		return INDICES_NOT_FOUND;
    	}
    	if (startIndex < 0) {
    		startIndex = 0;
    	}
    	Vector indices = new Vector();
    	for (int i = startIndex; i < array.length; i++) {
    		if (array[i] == valueToFind) {
    			indices.add(i);
    		}
    	}
    	return checkIndices(indices);
    }
    
    /**
     * <p>Finds the indices for the array elements within the min and max value (inclusive).</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     */
    public static int[] indicesWithin(float[] array, float minVal, float maxVal) {
    	if (array == null) {
    		return INDICES_NOT_FOUND;
    	}
    	Vector indices = new Vector();
    	for (int i = 0; i < array.length; i++) {
    		if (array[i] >= minVal && array[i]<= maxVal) {
    			indices.add(i);
    		}
    	}
    	return checkIndices(indices);
    }
    
    /**
     * <p>Finds the number of times a given value/object is present in an array.</p>
     *
     * @param array  the array to search through for the value/object
     * @param valueToFind  the object to find.
     * @return the number of times that value/object appears in the array
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
	public static int numRepeats(float[] array, float valueToFind) {
		int count=0; 
		for (int i=0; i<array.length; i++) {
			if (array[i] == valueToFind) 	{
				count++;
			}
		}
		return count;
	}
	
	public static int[] indicesLessThanOrEqualTo(float[] array, float valueOfMax){
		ArrayList indices = new ArrayList();
		for(int i=0; i<array.length; i++){
			if(array[i]<=valueOfMax) indices.add(i);
		}
		return Cast.arrayListToInt(indices);
	}
	
	public static int[] indicesLessThan(float[] array, float valueOfMax){
		ArrayList indices = new ArrayList();
		for(int i=0; i<array.length; i++){
			if(array[i]<valueOfMax) indices.add(i);
		}
		return Cast.arrayListToInt(indices);
	}
	
	public static int[] indicesGreaterThanOrEqualTo(float[] array, float valueOfMin){
		ArrayList indices = new ArrayList();
		for(int i=0; i<array.length; i++){
			if(array[i]>=valueOfMin) indices.add(i);
		}
		return Cast.arrayListToInt(indices);
	}
	
	public static int[] indicesGreaterThan(float[] array, float valueOfMin){
		ArrayList indices = new ArrayList();
		for(int i=0; i<array.length; i++){
			if(array[i]>valueOfMin) indices.add(i);
		}
		return Cast.arrayListToInt(indices);
	}
	
	/**
     * <p>Finds the number of elements less than or equal to the specified value.</p>
     *
     * @param array  the array to search through for the value/object
     * @param valueOfMax the maximum value
     * @return the number of elements ? that value
     */
	public static int numLessThanOrEqualTo(float[] array, float valueOfMax) {
		int count=0; 
		for (int i=0; i<array.length; i++) {
			if (array[i] <= valueOfMax) 	{
				count++;
			}
		}
		return count;
	}
	
	/**
     * <p>Finds the number of elements strictly less than the specified value.</p>
     *
     * @param array  the array to search through for the value/object
     * @param valueOfMax the maximum value
     * @return the number of elements strictly less than that value
     */
	public static int numLessThan(float[] array, float valueOfMax) {
		int count=0; 
		for (int i=0; i<array.length; i++) {
			if (array[i] < valueOfMax) 	{
				count++;
			}
		}
		return count;
	}
	
	/**
     * <p>Finds the number of elements greater than or equal to the specified value.</p>
     *
     * @param array  the array to search through for the value/object
     * @param valueOfMax the maximum value
     * @return the number of elements ? that value
     */
	public static int numGreaterThanOrEqualTo(float[] array, float valueOfMin) {
		int count=0; 
		for (int i=0; i<array.length; i++) {
			if (array[i] >= valueOfMin) 	{
				count++;
			}
		}
		return count;
	}
	
	/**
     * <p>Finds the number of elements strictly greater than the specified value.</p>
     *
     * @param array  the array to search through for the value/object
     * @param valueOfMax the maximum value
     * @return the number of elements strictly greater than that value
     */
	public static int numGreaterThan(float[] array, float valueOfMin) {
		int count=0; 
		for (int i=0; i<array.length; i++) {
			if (array[i] > valueOfMin) 	{
				count++;
			}
		}
		return count;
	}
	
	/** Find the unique elements in a float array (uses HashSets) */
	public static float[] uniqueElems(float[] array){
		Set<Float> uniqueVals = new HashSet<Float>();
		for(int i=0; i<array.length; i++){
			uniqueVals.add(array[i]);
		}
		return Cast.setToFloat(uniqueVals);
	}

    // boolean IndexOf
    //-----------------------------------------------------------------------
    /**
     * <p>Finds the index of the given value in the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int indexOf(boolean[] array, boolean valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * <p>Finds the index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).</p>
     *
     * @param array  the array to search through for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null}
     *  array input
     */
    public static int indexOf(boolean[] array, boolean valueToFind, int startIndex) {
    	if (array.length == 0) {
    		return INDEX_NOT_FOUND;
    	}
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Finds the last index of the given value within the array.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) if
     * {@code null} array input.</p>
     *
     * @param array  the array to travers backwords looking for the object, may be {@code null}
     * @param valueToFind  the object to find
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int lastIndexOf(boolean[] array, boolean valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * <p>Finds the last index of the given value in the array starting at the given index.</p>
     *
     * <p>This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.</p>
     *
     * <p>A negative startIndex will return {@link #INDEX_NOT_FOUND} ({@code -1}). A startIndex larger than
     * the array length will search from the end of the array.</p>
     *
     * @param array  the array to traverse for looking for the object, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the start index to travers backwards from
     * @return the last index of the value within the array,
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
    public static int lastIndexOf(boolean[] array, boolean valueToFind, int startIndex) {
    	if (array.length == 0) {
    		return INDEX_NOT_FOUND;
    	}
        if (startIndex < 0) {
            return INDEX_NOT_FOUND;
        } else if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; i--) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }
    
    /**
     * <p>Finds the indices containing the specified value in the array.</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the value to find, may be {@code null}
     * @param valueToFind  the value to find
     * @return the indices containing the value within the array,
     *  {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) if not found or {@code null} array input
     */
    public static int[] indicesWith(boolean[] array, boolean valueToFind) {
        return indicesWith(array, valueToFind, 0);
    }
    
    /**
     * <p>Finds the indices containing the specified value in the array.</p>
     *
     * <p>This method returns {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) for a {@code null} input array.</p>
     *
     * @param array  the array to search through for the value to find, may be {@code null}
     * @param valueToFind  the value to find
     * @param startIndex  the index to start searching at
     * @return the indices containing the value within the array,
     *  {@link #INDICES_NOT_FOUND} ({@code int[] = -1}) if not found or {@code null} array input
     */
    public static int[] indicesWith(boolean[] array, boolean valueToFind, int startIndex) {
    	if (array == null) {
    		return INDICES_NOT_FOUND;
    	}
    	if (startIndex < 0) {
    		startIndex = 0;
    	}
    	Vector indices = new Vector();
    	for (int i = startIndex; i < array.length; i++) {
    		if (array[i] == valueToFind) {
    			indices.add(i);
    		}
    	}
    	return checkIndices(indices);
    }
    
    /**
     * <p>Finds the number of times a given value/object is present in an array.</p>
     *
     * @param array  the array to search through for the value/object
     * @param valueToFind  the object to find.
     * @return the number of times that value/object appears in the array
     *  {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
     */
	public static int numRepeats(boolean[] array, boolean valueToFind) {
		int count=0; 
		for (int i=0; i<array.length; i++) {
			if (array[i]==valueToFind) {
				count++;
			}
		}
		return count;
	}
    
    /**
     * Private function to check if the indices exist
     * or otherwise. 
     * @param indices indices containing the value/object to find
     * @return either the indices or {@link INDICES_NOT_FOUND}
     * depending on whether that value/object exists or otherwise.
     */
    private static int[] checkIndices(Vector indices){
    	if(indices.size()>0){
    		return Cast.vectorToInt(indices);
    	}
    	else{      
    		return INDICES_NOT_FOUND;
    	}
    	
    }
}

