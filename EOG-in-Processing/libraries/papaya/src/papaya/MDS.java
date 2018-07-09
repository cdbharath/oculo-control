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
 * Contains methods for performing but classical and non-classical multidimensional scaling.
 * <p>
 * For an m-by-n input matrix with m observations and n variables, the output D is 
 * the symmetric m-by-m matrix with zeros along the diagonals and element ij 
 * specifying the distance between rows i and j.
 * 
 */
public final class MDS {

	/**
	 * Makes this class non instantiable, but still let's others inherit from it.
	 */
	protected MDS(){

	}
	static double MACHEP =  PapayaConstants.MACHEP;

	/**
	 * Performs classical (metric) multidimensional scaling, on an input matrix of Distances (computed
	 * using e.g. {@link Distance} ) and outputs the best-fitting p-dimensional configuration of points
	 * (<code>p&lt;N</code>). The output matrix <code>Y</code> has p columns with rows
	 * giving the coordinates of the points chosen to represent the dissimilarities. 
	 * When <code>D</code> corresponds to the Euclidian distance matrix, the <code>p</code>
	 * dimensional coordinates correspond exactly to the first <code>p</code> principal components.
	 * <p>
	 * <b>Remarks</b>
	 * <ul>
	 * <li>If <code>p &gt k</code>where <code>k</code> is the number of 
	 * positive eigenvalues of <code>Y*Y'</code>, then only the 
	 * first <code>k<code> columns of <code>Y</code> are computed. 
	 * </li>
	 * <li>Set <code>showEigenvalues</code> to true to print the
	 * values of the "scalar product matrix" <code>Y*Y'</code> to the screen. 
	 * (This is a good idea if you want to know how well the first <code>p</code> dimensions
	 * captures most of the variation in <code>D</code>.</li>
	 *  </ul>> 
	 * @param D the input distance matrix (see {@link Distance})
	 * @param p the dimension (number of columns) of the output matrix. 
	 * @param showEigenvalues whether to print the eigenvalues to the screen or otherwise.
	 * @return Y, N-by-p (or N-by-k) matrix consisting of N points in each of the p (or k) dimensions.
	 */
	public static float[][] classical(float[][] D, int p, boolean showEigenvalues){
		Check.square(D);
		int n = D.length; 
		float[][] P  = createP(n);
		float[][] B = new float[n][n];
		double sum = 0.0;
		// Doubly center D
		for(int i=0; i<n; i++){
			for(int j=i; j<n; j++){
				sum=0.0;
				for(int k=0; k<n; k++) {
					for(int l=0; l<n; l++) sum+= P[i][k] * D[k][l]*D[k][l] * P[l][j];
				}
				B[i][j] = (float)(-.5*sum);
				B[j][i] = B[i][j];
			}
		}
		Eigenvalue eigen = new Eigenvalue(B);
		// B is symmetric so all evals are real
		double[] e = eigen.getRealEigenvalues(); // ascending order
		double[][]V = eigen.getV();

		// sort eigenvalues in descending order
		int[] indices = Sorting.indices(e,false); // sort evals in descending order
		e = Mat.populate(e,indices); 
		if(showEigenvalues) {
			System.out.println("\nEigenvalues:");
			Mat.print(e,4);System.out.println();
		}
		int[] keep = find(e,MACHEP);
		int ln = Math.min(keep.length,p);
		
		if(p>keep.length) {
			System.out.println("\nNumber of dimensions exceeds the number of positive Eigenvalues. "+
				"Returning the first "+ln+" dimensions instead.\n");
		}
		float[][] Y = new float[n][ln];		
		if(keep.length==0) 
			System.out.println("\nUnable to find a suitable Y. Maximum eigenvalue is "+Descriptive.max(e)); 
		else{
			//Y = V(:,i(keep)) * diag(sqrt(e(keep)));
			double sqrtEig = 0.0;
			for(int c=0; c<ln; c++){
				int j = keep[c];
				sqrtEig = Math.sqrt(e[j]);
				for(int i=0; i<V.length; i++){
					Y[i][c] = (float)(V[i][indices[j]] * sqrtEig);
				}
			}
		}
		return Y;
	}

	private static float[][] createP(int size){
		float[][] P = new float[size][size];
		float val = 1/(float)size;
		for(int i=0;i<size;i++){
			P[i][i]=1-val;
			for(int j=i+1; j<size; j++) {P[i][j] = -val; P[j][i] = -val;}
		}
		return P;	
	}
	/* Finds all elements whose absolute value are within val. */
	private static int[] find(double[] v, double val){
		int[] indices = new int[v.length];
		int j=0;
		for(int i=0; i<v.length; i++){
			if((v[i])>val) {indices[j]=i; j++;}
		}
		return Arrays.copyOf(indices,j);
	}

}