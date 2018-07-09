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
 */

package papaya;
import java.util.*;
import java.io.*;
import processing.core.*;

/**
 * Contains utilities related to computing covariances, as well as linear and rank correlation.
 * Methods relating to computing 
 * <ul>
 * <li>normal correlations and covariances (biased or unbiased) are in the main class.
 * <li>weighted correlation and covariances are in the {@link Weighted} subclass.
 * <li>significance of the correlations (i.e. p-values) are in the {@link Significance} subclass.
 * </ul>
 * Whatever it is, remember that <i>correlation does not always imply causation</i>.
 */
public final class Correlation implements PapayaConstants {
	/**
	 * Makes this class non instantiable, but still let's others inherit from it.
	 */
	protected Correlation() {}
	/**
	 * Computes the sample autocorrelation by removing the sample mean from the input series,
	 * then normalizing the sequence by the sample variance. That is, it returns
	 * <pre>
	 * R(lag) = E[ (X[t] - mu) * ( X[t+lag] - mu ) ] / variance(X).	
	 * where 
	 * E[ (X[t] - mu) * ( X[t+lag] - mu ) ]  = 1/size(X) * Sum_(i=0 to size-lag)( (X[t]-mu)*X[t+lag]-mu) ). 
	 * </pre>
	 * Reference: Box, G. E. P., G. M. Jenkins, and G. C. Reinsel. 
	 * Time Series Analysis: Forecasting and Control. 3rd ed.
	 * Upper Saddle River, NJ: Prentice-Hall, 1994.
	 * <p>
	 * @param data the array of data
	 * @param lag the lag value. Has to be smaller than the data sequence length
	 * @param mean the data mean
	 * @param variance the data variance
	 * @return the autocorrelation value
	 */
	public static float auto(float[] data, int lag, float mean, float variance) {
		int size = data.length;
		if (lag >= size) throw new IllegalArgumentException("Lag is too large");
	    if(lag==0) return 1f;
		double run = 0;
		for( int i=0; i<size-lag; i++)
			run += (data[i]-mean)*(data[i+lag]-mean);
		float autocorr =  (float)((run/size)/variance);
		// get rid of accidental roundoff error 
		if(autocorr>=1) return 1f;
		if(autocorr<=-1) return -1f;
		return autocorr;
	}

	/**
	 * Returns the lag-1 autocorrelation of a dataset; 
	 * Note that this method uses computations different from 
	 * <code>auto(data, 1, mean, variance)</code>.
	 */
	public static float autoLag1(float[] data, float mean) {
		int size = data.length;
		double r1 ;
		double q = 0 ;
		double v = (data[0] - mean) * (data[0] - mean) ;

		for (int i = 1; i < size ; i++) {
			double delta0 = (data[i-1] - mean);
			double delta1 = (data[i] - mean);
			q += (delta0 * delta1 - q)/(i + 1);
			v += (delta1 * delta1 - v)/(i + 1);
		}
		r1 = q / v ;
		return (float)r1;
	}	
	/**
	 * Returns the (Pearson Product Moment) correlation matrix between multiple columns of a matrix
	 * with each column corresponding to a dataset, and each row an observation.
	 * That is, given <code>P</code> columns, <code>data1, data2, ... , dataP</code>,
	 * each of length n, it computes and returns the <code>P-by-P</code> correlation
	 * matrix, <code>C</code> with each element C<sub>JK</sub> given by
	 * <pre>
	 * C<sub>JK</sub> = C<sub>KJ</sub> = corr(dataJ,dataK,unbiasedValue).
	 * </pre>
	 * @param data The input data. Each column corresponds to a dataset; 
	 * each row corresponds to an observation 
	 * @param unbiased set to true to return the unbiased correlation,
	 * false to return the biased version.
	 * @return the correlation matrix (symmetric, by definition)
	 */
	public static float[][] linear(float[][] data, boolean unbiased){	
		int numColumns = data[0].length;
		int size = data.length;
		float[][] corrMat = new float[numColumns][numColumns];	
		float[] data1 = new float[size], data2 = new float[size];	
		for(int c1=0; c1<numColumns; c1++){
			corrMat[c1][c1]  = 1; // by definition
			// only compute the top-right half since it's symmetric.
			for(int c2=c1+1; c2<numColumns; c2++){
				data1 = Mat.column(data,c1);
				data2 = Mat.column(data,c2);
				corrMat[c1][c2] = linear(data1,data2, unbiased);
				corrMat[c2][c1] = corrMat[c1][c2];		
			}
		}
		return corrMat;
	}
	/**
	 * Returns the (Pearson Product Moment) linear correlation of two data sequences.
	 * It is related to the {@link cov(float[], float[], boolean)} function via
	 * <pre>
	 * <code>corr = cov(x,y,unbiasedValue)/sqrt( cov(x,x, unbiasedValue)*cov(y,y,unbiasedValue) )</code>
	 * </pre>
	 * @param unbiased set to true to return the unbiased correlation,
	 * false to return the biased version.
	 */
	public static float linear(float[] data1, float[] data2, boolean unbiased) {
		float denom = cov(data1,data1,unbiased)*cov(data2,data2,unbiased);
		return cov(data1,data2,unbiased)/(float)Math.sqrt(denom);
	}
	
	/**
	 * Computes Spearman's rank-correlation, or <code>rho</code>, between multiple columns of a matrix
	 * with each column corresponding to a dataset, and each row an observation.
	 * That is, each pair of columns are first converted to ranks rXJ, rXK 
	 * and the correlation between the ranks computed using the Pearson correlation coefficient 
	 * formula.
	 * @param unbiased set to true to return the unbiased correlation,
	 * false to return the biased version.
	 */
	public static float[][] spearman(float[][] data, boolean unbiased){
		
		int numColumns = data[0].length;
		int size = data.length;
		float[][] corrMat = new float[numColumns][numColumns];	
		float[] data1 = new float[size], data2 = new float[size];	
		for(int c1=0; c1<numColumns; c1++){
			corrMat[c1][c1]  = 1; // by definition
			// only compute the top-right half since it's symmetric.
			for(int c2=c1+1; c2<numColumns; c2++){
				data1 = Mat.column(data,c1);
				data2 = Mat.column(data,c2);
				corrMat[c1][c2] = spearman(data1,data2, unbiased);
				corrMat[c2][c1] = corrMat[c1][c2];		
			}
		}
		return corrMat;
	}
	/**
	 * Computes Spearman's rank-correlation, or <code>rho</code>.
	 * That is, the raw dataset Xi,Yi are first converted to ranks rXi, rYi 
	 * and the correlation between the ranks computed using the Pearson correlation coefficient 
	 * formula.
	 * @param unbiased set to true to return the unbiased correlation,
	 * false to return the biased version.
	 */
	public static float spearman(float[] x, float[] y, boolean unbiased){
		int size = x.length;
		Check.equalLengths(size,y);
		float[] rX = Rank.rank(x,0);
		float[] rY = Rank.rank(y,0);
		float rs = linear(rX,rY,unbiased);
		//System.out.print("\nx = \t"); Mat.print(x,1);
		//System.out.print("\ny = \t"); Mat.print(y,1);
		//System.out.println("\nrX = "); Mat.print(rX,1);
		//System.out.println("\nrY = "); Mat.print(rY,1);
		return rs;
	}
	/**
	 * Computes Kendall's rank correlation or <code>tau</code> between two datasets.
	 * That is, the raw dataset Xi,Yi are first converted to ranks rXi, rYi 
	 * and <code>tau</code> computed as
	 * <pre>tau = (nC - nD)/(sqrt(n0-n1)*sqrt(n0-n2))</pre>
	 * where
	 * <br><code>nC</code> is the number of concordant pairs</br>
	 * <br><code>nD</code> is the number of discordant pairs</br>
	 * <br><code>n0</code> is the sum of ranks = <code>n*(n-1)/2</code></br>
	 * <br><code>n1</code> is the sum of tied ranks in dataset 1</br>
	 * <br><code>n2</code> is the sum of tied ranks in dataset 2</br>
	 * @param unbiased set to true to return the unbiased correlation,
	 * false to return the biased version.
	 */
//	public static float kendall(float[] x, float[] y, boolean unbiased){
//		int size = x.length;
//		Check.equalLengths(size,y);
//		float[] rX = Rank.rank(x,0);
//		float[] rY = Rank.rank(y,0);
//		//int nConcordant = 
//		float rs = linear(rX,rY,unbiased);		
//		return rs;
//	}
	
	/**
	 * Returns the covariance of two data sequences <code>data1</code> and <code>data2</code>, 
	 * each of length N. That is,
	 * <pre> 
	 * cov(data1, data2) = E(  (data1[i] - mean(data1))* (data2[i] - mean(data2)) ),
	 * </pre>
	 * where E is the mathematical expectation.
	 * <p>
	 * cov(x,y,true) normalizes by N - 1, if N &gt; 1, where N is the number of observations. 
	 * This makes cov(x,y,true) the best unbiased estimate of the covariance matrix if the 
	 * observations are from a normal distribution. For N = 1, cov(x,y,true) normalizes by N.
     * <p>
     * cov(x,y,false) normalizes by N and produces the second moment matrix of the observations 
     * about their mean. 
     * @param data1 x 
     * @param data2 y
     * @param unbiased set to true to return the unbiased covariance (division by N-1), 
     * false to return the biased version (division by N).
     */
	public static float cov(float[] data1, float[] data2, boolean unbiased) {
		int size = data1.length;
		Check.equalLengths(size,data2);
		Check.size0(size);	
		
		float sumx = data1[0], sumy = data2[0], Sxy=0;
		for (int i=1; i<size; ++i) {
			sumx += data1[i];
			Sxy += (data1[i] - sumx/(i+1))*(data2[i] - sumy/i);
			sumy += data2[i];
		}
		if(!unbiased || size < 2){
			return Sxy/size;
		}
		else return Sxy/(size-1);
	}		
	
	/**
	 * Returns the covariance matrix of P data sequences, each of length N. 
	 * That is, given <code>P</code> columns, <code>data1, data2, ... , dataP</code>,
	 * each of length n, it computes and returns the <code>P-by-P</code> covariance
	 * matrix, <code>S</code> with each element S<sub>JK</sub> given by
	 * <pre>
	 * S<sub>JK</sub> = S<sub>KJ</sub> = cov(dataJ,dataK,bias).	
	 * </pre>
	 * 
	 * cov(data,true) normalizes by N - 1, if N &gt; 1, where N is the number of observations 
	 * (or the number of rows in the input matrix). 
	 * This makes cov(data,true) the best unbiased estimate of the covariance matrix if the 
	 * observations are from a normal distribution. For N = 1, cov(data) normalizes by N.
	 * 
	 * <p>
     * cov(data,false) normalizes by N and produces the second moment matrix of the observations 
     * about their mean. 
     * @param data Each column corresponds to a dataset; each row corresponds to an observation 
	 * @param unbiased set to true to return the unbiased covariance matrix, false to return the biased version. 
	 */
	public static float[][] cov(float[][] data, boolean unbiased) {
		int numColumns = data[0].length;
		int size = data.length;
		float[] data1 = new float[size], data2 = new float[size];
		float[][] covMat = new float[numColumns][numColumns];
		for(int c1=0; c1<numColumns; c1++){
			data1 = Mat.column(data,c1);				
			covMat[c1][c1]  = Descriptive.var(data1,unbiased);			
			// only compute the top-right half since it's symmetric.
			for(int c2=c1+1; c2<numColumns; c2++){					
				data2 = Mat.column(data,c2);
				covMat[c1][c2] = cov(data1,data2,unbiased);
				covMat[c2][c1] = covMat[c1][c2];		
			}
		}
		return covMat;
	}
	/**
	 * Contains methods related to computing the correlation and covariance of weighted
	 * datasets.
	 */
	
	public static class Weighted{
		/**
		 * Makes this class non instantiable, but still let's others inherit from it.
		 */
		protected Weighted(){};
		
		/**
		 * Returns the weighted linear correlation matrix C.
		 * If S is an estimate of the weighted covariance matrix, then element JK of the
		 * weighted correlation matrix C is then given by 
		 * <pre>
		 * C_JK = S_JK / sqrt(S_JJ,unbiasedValue)*sqrt(S_KK,unbiasedValue).
		 * </pre>
		 * See {@link Weighted#cov(float[][],boolean)}.
		 * @param data input matrix each column corresponding to a sample, each row an observation.
		 * @param weights the weights to use.
		 * @param unbiased set to true to return the unbiased weighted correlation matrix, false to return the biased version.
		 */
		public static float[][] corr(float[][] data, float[] weights, boolean unbiased){
			int size = data.length;		
			int numColumns = data[0].length;
			float[][] corr= new float[numColumns][numColumns];
			float[][] cov = cov(data,weights,unbiased);
			for(int c1=0; c1<numColumns; c1++){
				for(int c2 = c1; c2<numColumns; c2++){
					corr[c1][c2] = (float)(cov[c1][c2] / ( Math.sqrt(cov[c1][c1])*Math.sqrt(cov[c2][c2]) ));
					corr[c2][c1] = corr[c1][c2];
				}
			}
			return corr;
		}
		
		/**
		 * Returns the weighted linear correlation of two data sequences. 
		 * @param unbiased set to true to return the unbiased weighted correlation, false to return the biased version.
		 */
		public static float corr(float[] data1, float[] data2, float[] weights, boolean unbiased) {
			int size = data1.length;
			Check.equalLengths(size,data2); Check.equalLengths(size,weights);
			float denom = cov(data1,data1,weights,unbiased)*cov(data2,data2,weights,unbiased);
			return cov(data1,data2,weights,unbiased)/(float)Math.sqrt(denom);
		}
		
		/**
		 * Returns the weighted covariance between two data sequences.
		 * @param unbiased set to true to return the unbiased weighted covariance, false to return the biased version.
		 */
		public static float cov(float[] data1, float[] data2, float[] weights, boolean unbiased) {
			int size = data1.length;
			Check.equalLengths(size,data2); Check.equalLengths(size,weights);
			float mean1 = data1[0];
			float mean2 = data2[0];
			float covarianceSum = 0;
			float weightSum = weights[0]; // Sum of weights from 0 to i 
			float weightSumSquared = weights[0]*weights[0];
			float cov = 0;
			for(int i=1; i<size; i++){
				float stepMean1 = data1[i] - mean1;
				float stepMean2 = data2[i] - mean2;
				weightSum += weights[i];
				mean1 +=stepMean1*weights[i]/weightSum;
				mean2 +=stepMean2*weights[i]/weightSum;
				// covarianceSum +=weights[i]* (data[i][c1]-mean1) * (stepMean2);
				// alternatively
			    covarianceSum +=weights[i] * stepMean1 * (data1[i]-mean2);
				weightSumSquared += weights[i]*weights[i];					
			}
			if(unbiased) cov = covarianceSum*weightSum/(weightSum*weightSum - weightSumSquared) ;
			else cov = covarianceSum/weightSum;
			return cov;
		}
		
		/**
		 * Returns the weighted covariance matrix with element S_JK specifying the weighted
		 * covariance between column J and column K of the input matrix. 
		 * <p>
		 * The formula used to compute element <code>S_JK</code> of the unbiased covariance matrix is:
		 * <pre>
		 * S_JK = biasCorrection * Sum ( w[i] * ( dataJ[i] - mu_w(dataJ) )*( dataK[i] - mu_w(dataK) ) ) / Sum (w[i])
		 * </pre>
		 * where <code>mu_w</code> corresponds to the weighted mean of the dataset
		 * and the biasCorrection term above is
		 * <pre>
		 * biasCorrection = ( Sum(w[i]) )^2  / ( ( Sum(w[i]) )^2 - Sum( w[i]^2 )  ).
		 * </pre>
		 * The elements of the biased covariance matrix are computed sans the bias Correction factor.
		 * <p>
		 * Reference: <a href = "http://en.wikipedia.org/wiki/Sample_mean_and_sample_covariance" target="_blank">
		 * Covariance of weighted samples, wikipedia.org</a>.
		 * @param data input matrix each column corresponding to a sample, each row an observation.
		 * @param weights the weights to use.
		 * @param unbiased set to true to return the unbiased weighted covariance matrix, false to return the biased version.
		 */
		public static float[][] cov(float[][] data, float[] weights, boolean unbiased){
			int size = data.length;		
			int numColumns = data[0].length;
			float[][] cov = new float[numColumns][numColumns];
			for(int c1=0; c1<numColumns; c1++){
				for(int c2 = c1; c2<numColumns; c2++){
					// quicker than calling cov directly. Only thing
					// is that it computes the weightSumSquared over again at each pass.
					// Not sure if I should bother changing that though.
					float mean1 = data[0][c1];
					float mean2 = data[0][c2];
					float covarianceSum = 0;
					float weightSum = weights[0]; // Sum of weights from 0 to i 
					float weightSumSquared = weights[0]*weights[0];				
					for(int i=1; i<size; i++){
						float stepMean1 = data[i][c1] - mean1;
						float stepMean2 = data[i][c2] - mean2;
						weightSum += weights[i];
						mean1 +=stepMean1*weights[i]/weightSum;
						mean2 +=stepMean2*weights[i]/weightSum;
						// covarianceSum +=weights[i]* (data[i][c1]-mean1) * (stepMean2);
						// alternatively
						covarianceSum +=weights[i] * stepMean1 * (data[i][c2]-mean2);
						weightSumSquared += weights[i]*weights[i];					
					}					
					if(unbiased)cov[c1][c2] = covarianceSum*weightSum/(weightSum*weightSum - weightSumSquared) ;
					else cov[c1][c2] = covarianceSum/weightSum;
					
					cov[c2][c1] = cov[c1][c2];
				}
			}
			return cov;	
		}	
	} // end weighted class
	/** 
	 * Contains methods used to compute the significance, or pvalue of the input correlations.
	 * The significance is computed using the normal of student-t approximations and hence are
	 * <b>not to be used for small datasets</b>(i.e. size&lt;20). 
	 */
	public static class Significance{
		/**
		 * Makes this class non instantiable, but still let's others inherit from it.
		 */
		protected Significance(){};
		/**
		 * 
		 * The Durbin-Watson statistic is a test statistic used to detect the presence
		 * of autocorrelation ({@link Correlation#auto})
		 * in the residuals (prediction errors) from a regression analysis.
		 * <p>
		 * If <code>e_t</code> is the residual associated with the observation at time <code>t</code>, 
		 * and there are <code>T</code> observations, then the test statistic, <code>d</code> is
		 * </p>
		 * <pre>
		 * d =   Sum_{from t=2 to T} ( e_t - e_{t-1} )^2    /   Sum_{from t=1 to T} ( e_t )^2
		 * </pre>
		 * @return d the Durbin Watson test statistic
		 */
		public static float durbinWatson(float[] data) {
			int size = data.length;
			if (size < 2) throw new IllegalArgumentException("data sequence must contain at least two values.");
		
			float run = 0;
			float run_sq = 0;
			run_sq = data[0] * data[0];
			for(int i=1; i<size; i++) {
				float x = data[i] - data[i-1];
				run += x*x; // numerator
				run_sq += data[i] * data[i];  // denominator
			}
			return run / run_sq;
		}
		/** 
		 * Returns the p-value, or significance level of the linear correlation <code>r</code>
		 * between two datasets under the null hypothesis of no correlation.
		 * That is, <code>H_0: &rho;=0 </code> (No linear correlation; Two-tailed test). The p-value is 
		 * computed by transforming the correlation to create a t-statistic 
		 * having n-2 degrees of freedom:
		 * <pre> t = r * sqrt( (n-2)/(1-r^2) ),</pre>
		 * where n is the size of the corresponding datasets.
		 * @param r the linear correlation coefficient
		 * @param size the size of the corresponding datasets (i.e. number of rows/observations). 
		 * @throws IllegalArgumentException if the size is less than 20. Below this,
		 * the student-T approximation is inaccurate.
		 */
		public static float linear(float r, int size){
			double t = studentT(r,size-2);
			return (float)twoTailedStudentT(t,size-2);
		
		}
		/** 
		 * Returns the p-value, or significance level of the Spearman rank-correlation <code>rho</code>
		 * between two datasets under the null hypothesis of no correlation.
		 * That is, <code>H_0: &rho;_0=0 </code> (No linear correlation; Two-tailed test). 
		 * The p-value is computed by transforming the correlation to create a t-statistic 
		 * having n-2 degrees of freedom:
		 * <pre> t = rho * sqrt( (n-2)/(1-rho^2) ),</pre>
		 * where n is the size of the corresponding datasets.
		 * @param rho the Spearman-correlation coefficient
		 * @param size the size of the corresponding datasets (i.e. number of rows/observations). 
		 * @throws IllegalArgumentException if the size is less than 20. Below this,
		 * the student-T approximation is inaccurate.
		 */
		public static float spearman(float rho, int size){	
			Check.size20(size);
			double t = studentT(rho,size-2);
			return (float)twoTailedStudentT(t,size-2);		
		}
		
		/**
		 * Returns the Fisher transformation of the input correlation coefficient. That is,
		 * <pre>F(r) = atanh(r);</pre>
		 * @param corrcoef the correlation coefficient, r
		 * @return F(r)
		 */
		private static double fisher(float corrcoef){
			if(corrcoef==1) return MAXLOG;
			if(corrcoef==-1) return MINLOG;
			if(corrcoef==0) return 0.0f;
			double log = Math.log((1+corrcoef)/(1-corrcoef));
			return (.5*log);
		}
		/**
		 * Returns the Student-T test statistic of the input correlation coefficient. That is,
		 * <pre>t = abs(r) * sqrt( df/(1-r^2) ), 		df = size-2.</pre>
		 * @param corrcoef the correlation coefficient, r
		 * @param df the degrees of freedom. Corresponds to size-2
		 */
		private static double studentT(float corrcoef, int df){		
			if(corrcoef==1) return MAXLOG;
			if(corrcoef==-1) return MINLOG;
			if(corrcoef==0) return 0.0f;		
			return corrcoef*Math.sqrt( (df)/(1-corrcoef*corrcoef) );
		}
		
		private static double twoTailedNormal(double z){
			if(z>0) return ( 2*(1.0- (Probability.normcdf(z)) ) );
			else return (2 * (Probability.normcdf(z)));
		}
		
		private static double twoTailedStudentT(double t, int df){
			if(t>0) return ( 2*(1.0- (Probability.tcdf(t,df)) ) );
			else return (2 * (Probability.tcdf(t,df)));
		}
		
		/**
		 * Returns the z value of the Spearman rank-correlation. 
		 * That is,
		 * <pre>z = sqrt((n-3)(1.06)) * F(r), 		F(r) = atanh(r);</pre>
		 * @param corrcoef the correlation coefficient, r
		 * @param size the size of the dataset
		 */
		private static double zSpearman(float corrcoef, int size){
			Check.size3(size);
			double f = fisher(corrcoef);
			return Math.sqrt((size-3)/1.06)*f;
		}
	}// end test class
}// end Correlation class
