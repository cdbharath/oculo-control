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
 * Contains methods related to determining the linear linear 
 * relationship between two datasets (of equal arrays) such as the
 * best-fit linear line parameters, box-cox transformations, etc.
 */
public class Linear implements PapayaConstants{
	/**
	 * Returns the slope and y-intercept of the best fit linear line
	 * <code>z = slope*x + intercept</code> by minimizing the sum of least squares
	 * between <code>z</code> and the <code>y</code>. 
	 * @param x the x data
	 * @param y the y data
	 * @return a 2-by-1 array with the first element <code>coeff[0]</code>corresponding to 
	 * the slope, and the second element <code>coeff[1]</code> equal to the y-intercept.
	 */
	public static float[] bestFit(float[] x, float[] y){
		int size = x.length;
		Check.equalLengths(size,y);
		if(size<3) throw new IllegalArgumentException("Each data array needs to hold at least 3 values.");

		// the best-fit linear line coefficients.
		double sumX = 0; double sumY = 0; double sumXY = 0; double sumXX = 0;
		for(int i=0; i<size; i++){
			sumX+=x[i]; sumY+=y[i]; sumXY+=x[i]*y[i]; sumXX+=x[i]*x[i];
		}
		double numerator = sumXY - sumX*sumY/size; 
		double denominator = sumXX-sumX*sumX/size;
		float slope = (float)(numerator/denominator); 
		float intercept = (float)(sumY - slope*sumX)/size;
		return new float[]{slope,intercept};
	}
	
	/**
	 * Returns the slope of the best fit linear line for the prescribed y-intercept.
	 * That is, 
	 * <code>z = slope*x + intercept</code>, where the intercept is specified
	 * by the user. E.g. <code> intercept = 1</code> will result in <code>z = slope*x+1</code>.
	 * The <code>slope</code> is computed by minimizing the sum of least squares
	 * between <code>z</code> and the <code>y</code>. 
	 * @param x the x data
	 * @param y the y data
	 * @param intercept the intercept of the best-fit line with the y-axis. 
	 * 
	 * @return the slope of the best-fit linear line.
	 */
	public static float bestFit(float[] x, float[] y, float intercept){
		int size = x.length;
		Check.equalLengths(size,y);
		if(size<3) throw new IllegalArgumentException("Each data array needs to hold at least 3 values.");
		// the best-fit linear line coefficients.
		double sumX = 0; double sumY = 0; double sumXY = 0; double sumXX = 0;
		for(int i=0; i<size; i++){
			sumXY+=x[i]*y[i]; sumXX+=x[i]*x[i];
		}
		return (float)(sumXY/sumXX); 
	}
	/**
	 * Compute and return the array of residuals given by 
	 * <code>Delta_i = z_i - y_i</code>,
	 * where
	 * <code> z_i = (slope*x_i + intercept)</code> is the best fit linear line.
	 * @param slope the slope of the best-fit linear line
	 * @param intercept the y-intercept of the best fit linear line
	 */
	public static float[] residuals(float[] x, float[] y, float slope, float intercept){
		int size = x.length; Check.equalLengths(size,y);
		float[] delta = new float[size];
		for(int i=0; i<size; i++) delta[i] = y[i] - (slope*x[i] + intercept);
		return delta;
	}
	/**
	 * Compute and return the array of residuals given by 
	 * <code>Delta_i = z_i - y_i</code>,
	 * where
	 * <code> z_i = (slope*x_i + intercept)</code> is the best fit linear line.
	 * You'd basically use this to compute the spread of a best fit line (max - min)
	 */
	public static float[] residuals(float[] x, float[] y){
		int size = x.length; Check.equalLengths(size,y);
		float[] params = bestFit(x,y);
		float[] delta = new float[size];
		for(int i=0; i<size; i++) delta[i] = y[i]-(params[0]*x[i] + params[1]);
		return delta;
	}
	
	/** Contains methods related to computing the standard errors of the residuals, slope and intercept
	 * associated with the best-fit linear line. */
	public static class StdErr{
		protected StdErr(){}
		
		/**
		 * Returns the standard error of the residuals given the degrees of freedom.
		 * That is, 
		 * <pre>
		 * residualStdErr = Sqrt ( Sum( (delta[i] - mean(delta))^2 ) / df )
		 * </pre>
		 * where df is the degrees of freedom ( (n-1) if the y-intercept was pre-specified in the
		 * best-fit line, (n-2) if neither slope nor intercept were specified.)
		 * @param residuals
		 * @param df
		 * @return standard error of the residual
		 */
		public static float residual(float[] residuals, int df){
			int size = residuals.length;
			return (float)Math.sqrt( Descriptive.var(residuals,false) * size/df );
		}
		/**
		 * Returns the standard error of the computed slope given x and the
		 * standard error in the residuals. That is, 
		 * <pre>
		 * slopeStdErr = residualStdErr / Sum ( (x[i] - mean(x))^2 ).
		 * </pre>
		 * @param x the independent variable
		 * @param residualStdErr the standard error of the residual
		 * @return the error in the slope.
		 */
		public static float slope(float[] x, float residualStdErr ){
			float v = Descriptive.varianceNotNormalized(x);
			double slopeError = residualStdErr/Math.sqrt(v);
			return (float)slopeError;
		}
		/**
		 * Returns the standard error of the computed slope given x and the
		 * standard error in the residuals. That is, 
		 * <pre>
		 * interceptStdErr = slopeStdErr / Sqrt( Sum(x[i]*x[i])/size ).
		 * </pre>
		 * where the slopeStdErr is computed using the methods specified
		 * in {@link StdErr#slope}.
		 * @param x the independent variable
		 * @param residualStdErr the standard error of the residual
		 * @return error in the slope.
		 */
		public static float intercept(float[] x, float residualStdErr ){
			int size = x.length;
			float slopeError = slope(x,residualStdErr);
			double sumXX = 0;
			for(int i=0; i<size; i++) sumXX+=x[i]*x[i];
			return (float)(slopeError * Math.sqrt(sumXX/size));
		}
	}
	
	/** 
	 * Contains methods used to compute the significance, or pvalue of the input correlations.
	 * The significance is computed using the normal of student-t approximations and hence are
	 * <b>not to be used for small datasets</b>(i.e. size&lt;20). 
	 */
	public static class Significance{
		/** 
		 * Returns the p-value, or significance, of the computed slope under the null-hypothesis
		 * of slope = 0 (two-tailed test). The p-value is computed using the student-T distribution
		 * with df degrees of freedom and test statistic
		 * <pre> t = slope/slopeStdErr,</pre>
		 * @param slope the slope of the best fit linear line
		 * @param slopeStdErr the standard error of the slope
		 * @param df the degrees of freedom (typically (n-2), but could also be (n-1) if the 
		 * intercept was previously specified.
		 */
		public static float slope(float slope, float slopeStdErr, int df){
			double t = slope/slopeStdErr; 
			System.out.println("t slope: "+t);
			return (float)twoTailedStudentT(t,df);
		}
		/** 
		 * Returns the p-value, or significance, of the computed intercept under the null-hypothesis
		 * of intercept = 0 (two-tailed test). The p-value is computed using the student-T distribution
		 * with df degrees of freedom and test statistic
		 * <pre>t=intercept/interceptStdErr</pre>
		 * @param intercept the intercept of the best fit linear line
		 * @param interceptStdErr the standard error of the intercept
		 * @param df the degrees of freedom (n-2)
		 */
		public static float intercept(float intercept, float interceptStdErr, int df){
			double t = intercept/interceptStdErr; 
			System.out.println("t intercept: "+t);
			return (float)twoTailedStudentT(t,df);
		}
	}
	
	/**
	 * Contains methods related to the Box-Cox transformation of a data set; useful in 
	 * determining the best transformation that will yield the best method for converting
	 * a monotonic, non-linear relationship between <code>x</code> and <code>y</code> into
	 * a linear one. 
	 * <p>Reference:
	 * <br>Box, George E. P.; Cox, D. R. (1964). "An analysis of transformations".
	 * Journal of the Royal Statistical Society, Series B 26 (2): 211?252. 
	 * <br><a href="http://www.itl.nist.gov/div898/handbook/eda/section3/boxcoxli.htm" target="_blank">
	 * 	NIST/SEMATECH e-Handbook of Statistical Methods, EDA Section 1.3.3.6: Box-Cox Linearity Plots</a>
	 * (The reader's digest version of the original.)
	 *
	 */
	public static class BoxCox{
		protected BoxCox(){}
		
		/** 
		 * Performs the box-cox transformation on <code>y</code> for a sequence of &lambda; 
		 * and returns the array of linear correlation coefficients 
		 * between the <code>x</code> and the box-cox transformed <code>y</code>.
		 * at each value of &lambda; .
		 * The value of &lambda; corresponding to the maximum correlation 
		 * (or minimum for negative correlation) is the optimal choice for 
		 * &lambda; such that the relationship between x and T(y) is linear. 
		 * <p>Reference: 
		 * <br><a href="http://www.itl.nist.gov/div898/handbook/eda/section3/boxcoxli.htm" target="_blank">
		 * 	NIST/SEMATECH e-Handbook of Statistical Methods, EDA Section 1.3.3.6: Box-Cox Linearity Plots</a>
		 *
		 * @param x the independent data array
		 * @param y the dependent data array
		 * @param lambda an array of lambda values
		 * @return the array containing the linear correlation coefficients, r.
		 */
		public static float[] correlation(float[] x, float[] y, float []lambda){
			/* Check that the data is all positive */
			if(Descriptive.min(x)<=0) throw new IllegalArgumentException("y (the dependent variable) has to be strictly > 0.");
			int size = x.length;
			Check.equalLengths(size,y);
			float[] transformed = new float[size]; 
			int numSteps = lambda.length;  
			float[] corrcoef = new float[numSteps];
			for(int i=0; i<numSteps; i++){
				transformed = transform(y,lambda[i]);
				corrcoef[i] = Correlation.linear(x,transformed,false);
			}
			return corrcoef;
		}

		/** Performs the box-cox transformation, returning the transformed data.
		 * That is, it computes and returns
		 * <pre>
		 * y[i](&lambda;) = (y[i]^&lambda;-1) / &lambda; if &lambda; &ne; 0
		 * 
		 * y[i](&lambda;) = log(y[i])if &lambda; = 0
		 * </pre> 
		 * Requirements: all elements of <code>y</code> are &gt;0.
		 * @param data the data to be transformed
		 * @param lambda the lambda value
		 * @return the transformed data, or y(&lambda;) above.
		 * */
		public static float[] transform(float[] data, float lambda){
			/* Check that the data is all positive */
			if(Descriptive.min(data)<=0) throw new IllegalArgumentException("data has to be strictly > 0.");		
			int size = data.length;
			float[] transformed = new float[size];
			if(lambda==0){
				for(int i=0; i<size; i++) transformed[i] = (float)Math.log(data[i]);
			}
			else{
				for(int i=0; i<size; i++) {
					double numerator = Math.pow(data[i],lambda) - 1;
					transformed[i] = (float)numerator/lambda;
				}
			}
			return transformed;
		}
	}
	private static double twoTailedStudentT(double t, int df){
		System.out.println("Probability.tcdf(t,df): "+Probability.tcdf(t,df));
		if(t>0) return ( 2*(1.0- (Probability.tcdf(t,df)) ) );
		else return (2 * (Probability.tcdf(t,df)));
	}
}	








