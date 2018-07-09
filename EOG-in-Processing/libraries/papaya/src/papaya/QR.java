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

/** QR Decomposition.
<p>
   For an m-by-n matrix A with m &ge; n, the QR decomposition is an m-by-n
   orthogonal matrix Q and an n-by-n upper triangular matrix R so that
   A = Q*R.
<p>
   The QR decompostion always exists, even if the matrix does not have
   full rank, so the constructor will never fail.  The primary use of the
   QR decomposition is in the least squares solution of nonsquare systems
   of simultaneous linear equations.  This will fail if isFullRank()
   returns false.
 <p>
<i>Shamelessly copied (and modified) from the 
<a href="http://math.nist.gov/javanumerics/jama/" target="_blank">JAMA Java
Matrix</a> package. To make things compatible with how most users use Processing, the
class take in float matrices. However, to preserve the acccuracy of the computations, the algorithm
first casts the input into a double array, prior to doing anything. All methods also return doubles; Use
{@link Cast#doubleToFloat(double[][])} if you want/need to cast everything back to floats for 
further (non-high-accuracy-dependant) processing (pun intended).</i>
 */




public class QR {
	/* ------------------------
	   Class variables
	 * ------------------------ */

	/** Array for internal storage of decomposition.
	   @serial internal array storage.
	 */
	private double[][] QR;

	/** Row and column dimensions.
	   @serial column dimension.
	   @serial row dimension.
	 */
	private int m, n;

	/** Array for internal storage of diagonal of R.
	   @serial diagonal of R.
	 */
	private double[] Rdiag;

	/* ------------------------
	   Constructor
	 * ------------------------ */

	/** QR Decomposition, computed by Householder reflections. 
	 *  @param A    Rectangular matrix
	 */

	public QR (float[][] A) {
		// Initialize.
		QR = Cast.floatToDouble(A);
		m = A.length;
		n = A[0].length;
		Rdiag = new double[n];

		// Main loop.
		for (int k = 0; k < n; k++) {
			// Compute 2-norm of k-th column without under/overflow.
			double nrm = 0;
			for (int i = k; i < m; i++) {
				nrm = SVD.hypot(nrm,QR[i][k]);
			}

			if (nrm != 0.0) {
				// Form k-th Householder vector.
				if (QR[k][k] < 0) {
					nrm = -nrm;
				}
				for (int i = k; i < m; i++) {
					QR[i][k] /= nrm;
				}
				QR[k][k] += 1.0;

				// Apply transformation to remaining columns.
				for (int j = k+1; j < n; j++) {
					double s = 0.0; 
					for (int i = k; i < m; i++) {
						s += QR[i][k]*QR[i][j];
					}
					s = -s/QR[k][k];
					for (int i = k; i < m; i++) {
						QR[i][j] += s*QR[i][k];
					}
				}
			}
			Rdiag[k] = -nrm;
		}
	}

	/* ------------------------
	   Public Methods
	 * ------------------------ */

	/** Is the matrix full rank?
	   @return     true if R, and hence A, has full rank.
	 */

	public boolean isFullRank () {
		for (int j = 0; j < n; j++) {
			if (Rdiag[j] == 0)
				return false;
		}
		return true;
	}

	/** Return the Householder vectors
	   @return     Lower trapezoidal matrix whose columns define the reflections
	 */

	public double[][] getH () {
		double[][] H = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (i >= j) {
					H[i][j] = QR[i][j];
				} else {
					H[i][j] = 0.0;
				}
			}
		}
		return H;
	}

	/** Return the upper triangular factor
	   @return     R
	 */

	public double[][] getR () {
		double[][] R = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i < j) {
					R[i][j] = QR[i][j];
				} else if (i == j) {
					R[i][j] = Rdiag[i];
				} else {
					R[i][j] = 0.0;
				}
			}
		}
		return R;
	}

	/** Generate and return the (economy-sized) orthogonal factor
	   @return     Q
	 */

	public double[][] getQ () {
		double[][] Q = new double[m][n];
		for (int k = n-1; k >= 0; k--) {
			for (int i = 0; i < m; i++) {
				Q[i][k] = 0.0;
			}
			Q[k][k] = 1.0;
			for (int j = k; j < n; j++) {
				if (QR[k][k] != 0) {
					double s = 0.0;
					for (int i = k; i < m; i++) {
						s += QR[i][k]*Q[i][j];
					}
					s = -s/QR[k][k];
					for (int i = k; i < m; i++) {
						Q[i][j] += s*QR[i][k];
					}
				}
			}
		}
		return Q;
	}

	/** Least squares solution of A*X = b
	   @param b    An array with the same length as the number of rows in A.
	   @return     x that minimizes the two norm of Q*R*x-b.
	   @exception  IllegalArgumentException  Matrix row dimensions must agree.
	   @exception  RuntimeException  Matrix is rank deficient.
	 */

	public double[] solve (float[] b) {
		if (b.length != m) {
			throw new IllegalArgumentException("Matrix row dimensions must agree.");
		}
		if (!this.isFullRank()) {
			throw new RuntimeException("Matrix is rank deficient.");
		}

		// Copy right hand side
		int nx = 1; //B[0].length;
		double[] X = Cast.floatToDouble(b);

		// Compute Y = transpose(Q)*B
		for (int k = 0; k < n; k++) {
			double s = 0.0; 
			for (int i = k; i < m; i++) {
				s += QR[i][k]*X[i];
			}
			s = -s/QR[k][k];
			for (int i = k; i < m; i++) {
				X[i] += s*QR[i][k];
			}

		}
		//Mat.print(Cast.doubleToFloat(X),4);
		// Solve R*x = y;
		for (int k = n-1; k >= 0; k--) {
			X[k] /= Rdiag[k];

			for (int i = 0; i < k; i++) {
				X[i] -= X[k]*QR[i][k];

			}
		}
		return X; 

	}

	/** Least squares solution of A*X = B
	   @param B    A Matrix with as many rows as A and any number of columns.
	   @return     X that minimizes the two norm of Q*R*X-B.
	   @exception  IllegalArgumentException  Matrix row dimensions must agree.
	   @exception  RuntimeException  Matrix is rank deficient.
	 */

	public double[][] solve (float[][] B) {
		if (B.length != m) {
			throw new IllegalArgumentException("Matrix row dimensions must agree.");
		}
		if (!this.isFullRank()) {
			throw new RuntimeException("Matrix is rank deficient.");
		}

		// Copy right hand side
		int nx = B[0].length;
		double[][] X = Cast.floatToDouble(B);

		// Compute Y = transpose(Q)*B
		for (int k = 0; k < n; k++) {
			for (int j = 0; j < nx; j++) {
				double s = 0.0; 
				for (int i = k; i < m; i++) {
					s += QR[i][k]*X[i][j];
				}
				s = -s/QR[k][k];
				for (int i = k; i < m; i++) {
					X[i][j] += s*QR[i][k];
				}
			}
		}
		// Solve R*X = Y;
		for (int k = n-1; k >= 0; k--) {
			for (int j = 0; j < nx; j++) {
				X[k][j] /= Rdiag[k];
			}
			for (int i = 0; i < k; i++) {
				for (int j = 0; j < nx; j++) {
					X[i][j] -= X[k][j]*QR[i][k];
				}
			}
		}
		return X; 
	}
}





