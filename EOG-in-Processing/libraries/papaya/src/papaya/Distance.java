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
 * Contains methods for computing various "distance" metrics for multidimensional scaling.
 * <p>
 * For an m-by-n input matrix with m observations and n variables, the output D is 
 * the symmetric m-by-m matrix with zeros along the diagonals and element ij 
 * specifying the distance between rows i and j.
 * 
 */
public final class Distance {

	/**
	 * Makes this class non instantiable, but still let's others inherit from it.
	 */
	protected Distance(){

	}

	/**
	 * Computes the cityblock (or Manhattan) distance matrix between pairs of objects 
	 * in the m-by-n data matrix X. That is,
	 * <pre>
	 * Dist[i][j] = (X[i][1] - X[j][1]) + (X[i][2] - X[j][2]) + ... + (X[i][n] - X[j][n]). 
	 * </pre>
	 * <p>
	 * Rows of X correspond to observations, and columns correspond to variables. 
	 * Returns the m-by-m matrix D with D[r1][r2] corresponding to the cityblock
	 * (or manhattan) distance between the rows of the input X.
	 */
	public static float[][] cityblock(float[][] X){
		int numRows = X.length; int numColumns = X[0].length;
		float[][] distMatrix = new float[numRows][numRows];
		double val = 0.0;
		for(int r = 0; r<numRows-1; r++){
			for(int r2=r+1; r2<numRows; r2++){
				double dist = 0.0;
				for(int c=0; c<numColumns; c++){
					val=X[r][c]-X[r2][c];
					dist+=Math.abs(val);
				}
				distMatrix[r][r2]=(float)Math.sqrt(dist);
				distMatrix[r2][r]=distMatrix[r][r2];
			}
		}
		return distMatrix;
	}
	/**
	 * Computes the Chebychev distance matrix between pairs of objects 
	 * in the m-by-n data matrix X. That is,
	 * <pre>
	 * Dist[i][j] =Max( |X[i][1] - X[j][1]| , |X[i][2] - X[j][2]| , ... , |(X[i][n] - X[j][n]|). 
	 * </pre>
	 * <p>
	 * Rows of X correspond to observations, and columns correspond to variables. 
	 * Returns the m-by-m matrix D with D[r1][r2] corresponding to the Chebychev
	 * distance between the rows of the input X.
	 */
	public static float[][] chebychev(float[][] X){
		int numRows = X.length; int numColumns = X[0].length;
		float[][] distMatrix = new float[numRows][numRows];
		double val = 0.0;
		for(int r = 0; r<numRows-1; r++){
			for(int r2=r+1; r2<numRows; r2++){
				double max = 0.0;
				for(int c=0; c<numColumns; c++){
					val=X[r][c]-X[r2][c];
					max = Math.max(max,val*val);
				}
				distMatrix[r][r2]=(float)max;
				distMatrix[r2][r]=distMatrix[r][r2];
			}
		}
		return distMatrix;
	}
	
	/**
	 * Computes the correlation distance matrix between pairs of objects 
	 * in the m-by-n data matrix X. E.g., if y1 and y2 corresponds to observation (or row)
	 * 1 and 2 of X, then 
	 * <pre>
	 * Dist[1][2] = 1 -  sum_{j=1^numColumns} ( ( y1_j - mean(y1) ) * ( y2_j - mean(y2) ) / ( n * std(y1)*std(y2) ) ), 
	 * </pre>
	 * where n = number of columns.
	 * <p>
	 * Rows of X correspond to observations, and columns correspond to variables. 
	 * Returns the m-by-m matrix D with D[r1][r2] corresponding to the correlation
	 * distance between the rows of the input X.
	 */
	public static float[][] correlation(float[][] X){
		int numRows = X.length; int numColumns = X[0].length;
		float[][] distMatrix = new float[numRows][numRows];
		float[] mean = new float[numRows]; float[] std = new float[numRows];
		for(int i=0; i<numRows; i++){
			mean[i]=Descriptive.mean(X[i]);
			std[i] = Descriptive.std(X[i],false); // divide by n
		}
		double val=0.0;
		for(int r = 0; r<numRows-1; r++){
			for(int r2=r+1; r2<numRows; r2++){
				double dist= 0.0;
				for(int c=0; c<numColumns; c++){
					val=(X[r][c]-mean[r])*(X[r2][c]-mean[r2]);
					dist += val/(numColumns*std[r]*std[r2]);
				}
				distMatrix[r][r2]=1-(float)dist;
				distMatrix[r2][r]=distMatrix[r][r2];
			}
		}
		return distMatrix;
	}
	
	/**
	 * Computes the cosine distance matrix between pairs of objects 
	 * in the m-by-n data matrix X. E.g., if y1 and y2 corresponds to observation (or row)
	 * 1 and 2 of X, then 
	 * <pre>
	 * Dist[1][2] = 1- sum_{j=1^numColumns} ( y1_j * y2_j ) / ( mag(y1)*mag(y2) ), 
	 * </pre>
	 * where <code>mag(y1) = sum_{i=1 to numColumns} (y1[j]*y1[j])</code> and likewise
	 * for <code>mag(y2)</code>.
	 * etc. 
	 * <p>
	 * Rows of X correspond to observations, and columns correspond to variables. 
	 * Returns the m-by-m matrix D with D[r1][r2] corresponding to the cosine
	 * distance between the rows of the input X.
	 */
	public static float[][] cosine(float[][] X){
		int numRows = X.length; int numColumns = X[0].length;
		float[][] distMatrix = new float[numRows][numRows];
		float[] mag = new float[numRows]; 
		for(int i=0; i<numRows; i++){
			mag[i]=Mat.mag(X[i]);
		}
		double val=0.0;
		for(int r = 0; r<numRows-1; r++){
			for(int r2=r+1; r2<numRows; r2++){
				double dist= 0.0;
				for(int c=0; c<numColumns; c++){				
					dist += X[r][c]*X[r2][c]/(mag[r]*mag[r2]);
				}
				distMatrix[r][r2]=1-(float)dist;
				distMatrix[r2][r]=distMatrix[r][r2];
			}
		}
		return distMatrix;
	}
	
	/**
	 * Computes the Euclidean distance between pairs of objects 
	 * in the m-by-n data matrix X. That is,
	 * <pre>
	 * Dist[i][j]^2 = (X[i][1] - X[j][1]) ^2+ (X[i][2] - X[j][2])^2 + ... + (X[i][n] - X[j][n])^2. 
	 * </pre>
	 * <p>
	 * Rows of X correspond to observations, and columns correspond to variables. 
	 * Returns the m-by-m matrix D with D[r1][r2] corresponding to the Euclidean
	 * distance between the rows of the input X.
	 */
	public static float[][] euclidean(float[][] X){
		int numRows = X.length; int numColumns = X[0].length;
		float[][] distMatrix = new float[numRows][numRows];
		double val = 0.0;
		for(int r = 0; r<numRows-1; r++){
			for(int r2=r+1; r2<numRows; r2++){
				double dist = 0.0;
				for(int c=0; c<numColumns; c++){
					val=X[r][c]-X[r2][c];
					dist+=val*val;
				}
				distMatrix[r][r2]=(float)Math.sqrt(dist);
				distMatrix[r2][r]=distMatrix[r][r2];
			}
		}
		return distMatrix;
	}

	/**
	 * Computes the standardized Euclidean distance between pairs of objects 
	 * in the m-by-n data matrix X by standardizing X first prior to computing
	 * the distances. The standardized X is computed using
	 * <pre>
	 * standardized value = (original value - mean)/standard deviation
	 * </pre>
	 * where the mean and standard deviation correspond to the column mean and std.
	 * <p>
	 * Rows of X correspond to observations, and columns correspond to variables. 
	 * Returns the m-by-m matrix D with D[r1][r2] corresponding to the standardized 
	 * Euclidean distance between the rows of the input X. 
	 */
	public static float[][] seuclidean(float[][] X){
		float[] means = Descriptive.Mean.columnMean(X);
		float[] stds = Descriptive.std(X,true);//unbiased
		float[][] distMatrix = euclidean(Descriptive.zScore(X,means,stds));
		return distMatrix;
	}
	/**
	 * Computes the Mahalanobis distance matrix of the m-by-n input matrix X. That is,
	 * <pre>
	 * Dist[i][j]^2 = (X[i][1] - X[j][1])^2/ invcov[1][1] + ... + (X[i][n] - X[j][n])^2/ invcov[n][n]. 
	 * </pre>
	 *
	 * Rows of X correspond to observations, and columns correspond to variables. 
	 * Returns the m-by-m matrix D with D[r1][r2] corresponding to the s 
	 * mahalanobis distance between the rows of the input X. 
	 */
	public static float[][] mahalanobis(float[][] X){
		int numRows = X.length; int numColumns = X[0].length;
		double[][] invCov = new LU(Correlation.cov(X,true)).solve(Mat.identity(numColumns));
		float[][] distMatrix = new float[numRows][numRows];
		for(int r = 0; r<numRows; r++){			
			for(int r2=r+1; r2<numRows; r2++){
				double dist = 0.0;
				for(int c=0; c<numColumns; c++){
					dist+=(X[r][c]-X[r2][c])*invCov[c][c]*(X[r][c]-X[r2][c]);
				}
				distMatrix[r][r2]=(float)Math.sqrt(dist);
				distMatrix[r2][r]=distMatrix[r][r2];
			}
		}		
		return distMatrix;		
	}

	/**
	 * Returns the Minkowski distance matrix of the m-by-n input matrix X. That is,
	 * <pre>
	 * Dist[i][j]^p= (X[i][1] - X[j][1])^p/ + (X[i][2] - X[j][2])^p + ... + (X[i][n] - X[j][n])^p.
	 * </pre>
	 * Notice that for the special case of p = 1, the Minkowski metric 
	 * gives the city block metric, for the special case of p = 2, 
	 * the Minkowski metric gives the Euclidean distance, 
	 * and for the special case of p = &inf;, the Minkowski metric gives the Chebychev distance.
	 * <i>Also notice that the larger the value of p, the higher the probability of causing overflow
	 * errors (which are, in turn, highly correlated to headaches and overall feelings of malaise).</i>
	 * @param exp the Minkowski exponent (positive).
	 */
	public static float[][] minkowski(float[][] X, int exp){
		if(exp<1) throw new IllegalArgumentException("Exponent has to be a scalar positive value");
		// check if it fits any of these 
		if(exp==1) return cityblock(X);
		if(exp==2) return euclidean(X);
		if(exp>100) return chebychev(X);
		// otherwise, compute it.
		int numRows = X.length; int numColumns = X[0].length;
		float[][] distMatrix = new float[numRows][numRows];
		for(int r = 0; r<numRows; r++){			
			for(int r2=r+1; r2<numRows; r2++){
				double v = 0.0; double sum = 0.0;	
				if(exp==3){ 
					for (int c=0; c<numColumns; c++){
						v = X[r][c]-X[r2][c]; sum += Math.abs(v*v*v);  
					}
				}
				else if(exp==4){
					for (int c=0; c<numColumns; c++){
						v = X[r][c]-X[r2][c]; sum += v*v*v*v; 
					}
				}
				else if(exp==5){					 
					for (int c=0; c<numColumns; c++){
						v = X[r][c]-X[r2][c];  sum += Math.abs(v*v*v*v*v); 
					}
				}	
				else{
					for (int c=0; c<numColumns; c++){
						v = X[r][c]-X[r2][c]; sum += Math.abs(Math.pow(v, exp));
					}
				}				
				double root = 1/(double)exp;
				distMatrix[r][r2] = (float)Math.pow(sum,1/(double)exp);
				distMatrix[r2][r] = distMatrix[r][r2];
			}
		}
		return distMatrix;
	}
	
	/**
	 * Computes the Spearman distance matrix of the m-by-n input matrix X. E.g., if y1 and y2 corresponds to observation (or row)
	 * 1 and 2 of X with corresponding ranks 
	 * <br/><code>r11, r12, ... , r1n</code> 
	 * <br/>
	 * and
	 * <br/><code>r21, r22, ... , r2n</code>, <br/>then
	 * <pre><code>
	 * Dist[1][2] = 1 -  sum_{j=1^numColumns} ( ( r1_j - mean(r1) ) * ( r2_j - mean(r2) ) / ( n * std(r1)*std(r2) ) ), 
	 * </code></pre>
	 * and similarly for the remaining rows.
	 * <p>
	 * Rows of X correspond to observations, and columns correspond to variables. 
	 * Returns the m-by-m matrix D with D[r1][r2] corresponding to the Spearman 
	 * distance between the rows of the input X. 
	 */
	public static float[][] spearman(float[][] X){
		int numRows = X.length; int numColumns = X[0].length;
		float[][] distMatrix = new float[numRows][numRows];
		// get the rank for all rows
		float[][] rank = new float[numRows][numColumns]; 
		float mean = (numColumns+1)/2;
		double[] std = new double[numRows];
		for(int i=0; i<numRows; i++) {
			rank[i]=Rank.rank(X[i],0);
			std[i] = Math.sqrt(Descriptive.varianceNotNormalized(rank[i]));
		}
		double val = 0.0;
		for(int r = 0; r<numRows; r++){			
			for(int r2=r+1; r2<numRows; r2++){
				double dist = 0.0;
				for(int c=0; c<numColumns; c++){
					// no need to divide by numColumns since I defined std using powerDeviations.
					val=(rank[r][c]-mean)*(rank[r2][c]-mean);
					dist += val/(std[r]*std[r2]);
				}
				distMatrix[r][r2]=1-(float)dist;
				distMatrix[r2][r]=distMatrix[r][r2];
			}
		}		
		return distMatrix;		
	}
	
	
	//	    if nargin < 3  % use default value for exponent
	//	        additionalArg = 2;
	//	    elseif varargin{1} > 0
	//	        additionalArg = varargin{1}; % get exponent from input args
	//	    else
	//	        error('stats:pdist:InvalidExponent',...
	//	              'The exponent for the Minkowski metric must be positive.');
	//	    end
	//	case 'cos' % Cosine
	//	    Xnorm = sqrt(sum(X.^2, 2));
	//	    if min(Xnorm) <= eps(full(max(Xnorm)))
	//	        error('stats:pdist:InappropriateDistance',...
	//	              ['Some points have small relative magnitudes, making them ', ...
	//	               'effectively zero.\nEither remove those points, or choose a ', ...
	//	               'distance other than cosine.'], []);
	//	    end
	//	    X = X ./ Xnorm(:,ones(1,p));
	//	    additionalArg = [];
	//	case 'cor' % Correlation
	//	    X = X - repmat(mean(X,2),1,p);
	//	    Xnorm = sqrt(sum(X.^2, 2));
	//	    if min(Xnorm) <= eps(full(max(Xnorm)))
	//	        error('stats:pdist:InappropriateDistance',...
	//	              ['Some points have small relative standard deviations, making ', ...
	//	               'them effectively constant.\nEither remove those points, or ', ...
	//	               'choose a distance other than correlation.'], []);
	//	    end
	//	    X = X ./ Xnorm(:,ones(1,p));
	//	    additionalArg = [];
	//	case 'spe'
	//	    X = tiedrank(X')'; % treat rows as a series
	//	    X = X - (p+1)/2; % subtract off the (constant) mean
	//	    Xnorm = sqrt(sum(X.^2, 2));
	//	    if min(Xnorm) <= eps(full(max(Xnorm)))
	//	        error('stats:pdist:InappropriateDistance',...
	//	              ['Some points have too many ties, making them effectively ', ...
	//	               'constant.\nEither remove those points, or choose a ', ...
	//	               'distance other than rank correlation.'], []);
	//	    end
	//	    X = X ./ Xnorm(:,ones(1,p));
	//	    additionalArg = [];
	//	otherwise
	//	    additionalArg = [];
	//	end

	

}