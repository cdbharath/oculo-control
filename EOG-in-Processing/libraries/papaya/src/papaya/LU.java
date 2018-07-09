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

/** LU Decomposition.
<p>
For an <code>m x n</code> matrix <code>A</code> with <code>m &gt; n</code>, the LU decomposition is an <code>m x n</code>
unit lower triangular matrix <code>L</code>, an <code>n x n</code> upper triangular matrix <code>U</code>,
and a permutation vector <code>piv</code> of length <code>m</code> so that <code>A(piv,:) = L*U</code>;
If <code>m &lt; n</code>, then <code>L</code> is <code>m x m</code> and <code>U</code> is <code>m x n</code>.
<P>
The LU decomposition with pivoting always exists, even if the matrix is
singular, so the constructor will never fail.  The primary use of the
LU decomposition is in the solution of square systems of simultaneous
linear equations.  This will fail if <code>isNonsingular()</code> returns false.
<p>
<i>Shamelessly copied (and modified) from the 
<a href="http://math.nist.gov/javanumerics/jama/" target="_blank">JAMA Java
Matrix</a> package. To make things compatible with how most users use Processing, the
class take in float matrices. However, to preserve the acccuracy of the computations, the algorithm
first casts the input into a double array, prior to doing anything. All methods also return doubles; Use
{@link Cast#doubleToFloat(double[][])} if you want/need to cast everything back to floats for 
further (non-high-accuracy-dependant) processing (pun intended).</i>
 */

public class LU {

	/* ------------------------
Class variables
	 * ------------------------ */

	/** Array for internal storage of decomposition.
@serial internal array storage.
	 */
	private double[][] LU;

	/** Row and column dimensions, and pivot sign.
@serial column dimension.
@serial row dimension.
@serial pivot sign.
	 */
	private int numRows, numColumns, pivsign; 

	/** Internal storage of pivot vector.
@serial pivot vector.
	 */
	private int[] piv;

	/* ------------------------
Constructor
	 * ------------------------ */

	/** Constructor. Takes in a matrix and computes the LU matrix. Additional
	 * parameters can be obtained by calling one of the available methods.
	 * @see getL()
	 * @see getU()
	 * @see getPivot()
	 * @see getFloatPivot()
	 * @see det()
	 */
	public LU (float[][] A) {

		// Use a "left-looking", dot-product, Crout/Doolittle algorithm.
		LU = Cast.floatToDouble(A);
		numRows = A.length;
		numColumns = A[0].length;
		piv = new int[numRows];
		for (int i = 0; i < numRows; i++) {
			piv[i] = i;
		}
		pivsign = 1;
		double[] LUrowi;
		double[] LUcolj = new double[numRows];

		// Outer loop.

		for (int j = 0; j < numColumns;j++) {

			// Make a copy of the j-th column to localize references.

			for (int i = 0; i < numRows; i++) {
				LUcolj[i] = LU[i][j];
			}

			// Apply previous transformations.

			for (int i = 0; i < numRows; i++) {
				LUrowi = LU[i];

				// Most of the time is spent in the following dot product.

				int kmax = Math.min(i,j);
				float s = 0.0f;
				for (int k = 0; k < kmax; k++) {
					s += LUrowi[k]*LUcolj[k];
				}

				LUrowi[j] = LUcolj[i] -= s;
			}

			// Find pivot and exchange if necessary.

			int p = j;
			for (int i = j+1; i < numRows; i++) {
				if (Math.abs(LUcolj[i]) > Math.abs(LUcolj[p])) {
					p = i;
				}
			}
			if (p != j) {
				for (int k = 0; k < numColumns; k++) {
					double t = LU[p][k]; LU[p][k] = LU[j][k]; LU[j][k] = t;
				}
				int k = piv[p]; piv[p] = piv[j]; piv[j] = k;
				pivsign = -pivsign;
			}

			// Compute multipliers.

			if (j < numRows & LU[j][j] != 0.0) {
				for (int i = j+1; i < numRows; i++) {
					LU[i][j] /= LU[j][j];
				}
			}
		}
	}

	/* ------------------------
Temporary, experimental code.
------------------------ *\

\** LU Decomposition, computed by Gaussian elimination.
<P>
This constructor computes L and U with the "daxpy"-based elimination
algorithm used in LINPACK and MATLAB.  In Java, we suspect the dot-product,
Crout algorithm will be faster.  We have temporarily included this
constructor until timing experiments confirm this suspicion.
<P>
@param  A             Rectangular matrix
@param  linpackflag   Use Gaussian elimination.  Actual value ignored.
@return               Structure to access L, U and piv.
	 *\

public LUDecomposition (Matrix A, int linpackflag) {
   // Initialize.
   LU = A.getArrayCopy();
   m = A.getRowDimension();
   n = A.getColumnDimension();
   piv = new int[m];
   for (int i = 0; i < numRows; i++) {
      piv[i] = i;
   }
   pivsign = 1;
   // Main loop.
   for (int k = 0; k < numColumns k++) {
      // Find pivot.
      int p = k;
      for (int i = k+1; i < numRows; i++) {
         if (Math.abs(LU[i][k]) > Math.abs(LU[p][k])) {
            p = i;
         }
      }
      // Exchange if necessary.
      if (p != k) {
         for (int j = 0; j < numColumns;j++) {
            double t = LU[p][j]; LU[p][j] = LU[k][j]; LU[k][j] = t;
         }
         int t = piv[p]; piv[p] = piv[k]; piv[k] = t;
         pivsign = -pivsign;
      }
      // Compute multipliers and eliminate k-th column.
      if (LU[k][k] != 0.0) {
         for (int i = k+1; i < numRows; i++) {
            LU[i][k] /= LU[k][k];
            for (int j = k+1; j < numColumns;j++) {
               LU[i][j] -= LU[i][k]*LU[k][j];
            }
         }
      }
   }
}

\* ------------------------
End of temporary code.
	 * ------------------------ */

	/* ------------------------
Public Methods
	 * ------------------------ */

	/** Is the matrix nonsingular?
@return     true if U, and hence A, is nonsingular.
	 */

	public boolean isNonsingular () {
		for (int j = 0; j < numColumns;j++) {
			if (LU[j][j] == 0)
				return false;
		}
		return true;
	}

	/** Return lower triangular factor, <code>L</code>.*/
	public double[][] getL () {
		double[][] L = new double[numRows][numColumns];
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns;j++) {
				if (i > j) {
					L[i][j] = LU[i][j];
				} else if (i == j) {
					L[i][j] = 1.0f;
				} else {
					L[i][j] = 0.0f;
				}
			}
		}
		return L;
	}

	/** Return upper triangular factor, <code>U</code>  */
	public double[][] getU () {
		double[][] U = new double[numColumns][numColumns];
		for (int i = 0; i < numColumns; i++) {
			for (int j = 0; j < numColumns;j++) {
				if (i <= j) {
					U[i][j] = LU[i][j];
				} else {
					U[i][j] = 0.0f;
				}
			}
		}
		return U;
	}

	/** Return pivot permutation vector
@return     piv
	 */

	public int[] getPivot () {
		int[] p = new int[numRows];
		for (int i = 0; i < numRows; i++) {
			p[i] = piv[i];
		}
		return p;
	}

	/** Return pivot permutation vector as a one-dimensional float array
	 *  @return piv
	 */

	public float[] getFloatPivot () {
		float[] vals = new float[numRows];
		for (int i = 0; i < numRows; i++) {
			vals[i] = (float) piv[i];
		}
		return vals;
	}

	/** Determinant
	 *@return     det(A)
	 *@exception  IllegalArgumentException  Matrix must be square
	 */
	public double det () {
		if (numRows != numColumns) {
			throw new IllegalArgumentException("Matrix must be square.");
		}
		double d = (double) pivsign;
		for (int j = 0; j < numColumns;j++) {
			d *= LU[j][j];
		}
		return d;
	}

	/** Solve A*x = b for x
	 * @param  b   An array with as many elements as the number of rows in A.
	 * @return     x so that L*U*x = b(piv,:)
	 * @exception  IllegalArgumentException Matrix row dimensions must agree.
	 * @exception  RuntimeException  Matrix is singular.
	 */
	public double[] solve (float[] b, boolean returnZeroesForSingularCase) {
		if (b.length != numRows) {
			throw new IllegalArgumentException("Matrix row dimensions must agree.");
		}
		if (!this.isNonsingular()) {
			if(returnZeroesForSingularCase) return new double[numColumns];
			else throw new RuntimeException("Matrix is singular.");
		}

		// Copy right hand side with pivoting
		double[] X = Cast.floatToDouble(Mat.populate(b,piv));
		//System.out.println(X.length+","+b.length+","+LU[0].length);
		// Solve L*Y = B(piv,:)
		for (int k = 0; k < numColumns; k++) {
			for (int i = k+1; i < numColumns; i++) {
				X[i] -= X[k]*LU[i][k];
			}
		}
		// Solve U*X = Y;
		for (int k = numColumns-1; k >= 0; k--) {
			X[k] /= LU[k][k];
			for (int i = 0; i < k; i++) {
				X[i] -= X[k]*LU[i][k];
			}
		}
		//Mat.print(X,3); System.out.print("\n");
		return X;
	}

	/** Solve A*X = B
	 * @param  B   A Matrix with as many rows as A and any number of columns.
	 * @return     X so that L*U*X = B(piv,:)
	 * @exception  IllegalArgumentException Matrix row dimensions must agree.
	 * @exception  RuntimeException  Matrix is singular.
	 */
	public double[][] solve (float[][] B) {
		if (B.length != numRows) {
			throw new IllegalArgumentException("Matrix row dimensions must agree.");
		}
		if (!this.isNonsingular()) {
			throw new RuntimeException("Matrix is singular.");
		}

		// Copy right hand side with pivoting
		int nx = B[0].length;
		double[][] X = subMatrix(B,piv,0,nx-1);
		// Solve L*Y = B(piv,:)
		for (int k = 0; k < numColumns; k++) {
			for (int i = k+1; i < numColumns; i++) {
				for (int j = 0; j < nx; j++) {
					X[i][j] -= X[k][j]*LU[i][k];
				}
			}
		}
		// Solve U*X = Y;
		for (int k = numColumns-1; k >= 0; k--) {
			for (int j = 0; j < nx; j++) {
				X[k][j] /= LU[k][k];
			}
			for (int i = 0; i < k; i++) {
				for (int j = 0; j < nx; j++) {
					X[i][j] -= X[k][j]*LU[i][k];
				}
			}
		}
		return X;
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
	private static double[][] subMatrix(float[][] inputMat, int[] rowIndices, int columnStart, int columnEnd) {
		int numRows = rowIndices.length;
		int numColumns = columnEnd-columnStart+1;
		double[][] outputMat = new double[numRows][numColumns];
		for (int i = 0; i<numRows; i++) {
			for(int j = 0; j<numColumns; j++){
				outputMat[i][j] = (double)inputMat[rowIndices[i]][j+columnStart];
			}
		}
		return outputMat;
	}

	/** Multiplies two arrays and returns the multiplied array.
	 * That is, <code> z[i] = x[i] * y[i]</code>.
	 */
	private static double[] multiply(float[] data1, int[] pivot){
		int size = data1.length;
		double[] multiplied = new double[size];
		for(int i=0; i<size; i++){
			multiplied[i] = (double)data1[i]*pivot[i];
		}
		return multiplied;
	}
}









