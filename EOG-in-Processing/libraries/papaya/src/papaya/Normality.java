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
 * Contains various utilities for checking if the dataset comes from a normal distribution.
 */

public class Normality implements PapayaConstants {

	/**
	 * Makes this class non instantiable, but still let's others inherit from it.
	 */
	protected Normality(){}
	protected static void checkLength20(int size){
		if(size<20) throw new IllegalArgumentException("Dataset contains "+size+" elements which is" +
				"less than the minimum of 20.");
	}

	/**
	 * Return the Normal order statistic medians (<code>N</code>) necessary to produce a Q-Q plot 
	 * for a normal distribution (or normal probability plot). 
	 * That is, given a data array, this method computes the Normal order statistic medians
	 * and the ordered response values or <code>z</code>-scores of the input.
	 * A plot of the <code>N</code> vs <code>z</code> should form an approximate straight line if the 
	 * data is normally distributed; 
	 * departures from this straight line indicate departures from normality with the shape of the line
	 * providing clues as to the distribution of the data.
	 * <p>
	 * The normal order statistic medians are computed as
	 * <br><code>N[i] = Probability.norminv(U[i])</code>
	 * where <code>U[i]</code> are the uniform order statistic medians, given by
	 * <ul>
	 * <br>U[i] = 1 - U(n) for i = 1
	 * <br>U[i] = (i - 0.3175)/(n + 0.365) for i = 2, 3, ..., n-1 
	 * <br>U[i] = 0.5^(1/n) for i = n
	 * </ul>
	 * <p>Reference: <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/normprpl.htm" target="_blank">
	 * 				  NIST/SEMATECH e-Handbook of Statistical Methods, EDA Section 1.3.3.21: Normal Probability Plots</a>
	 * @param data the data array
	 * @return the Normal order statistic medians
	 * @see Descriptive#zScore(float[],float,float)
	 */
	public static float[] normalProbability(float[] data){
		int size = data.length;
		float[] N = new float[size];
		double factor = (double)1/size;
		double U = Math.pow(.5,factor); // U_n 
		N[0] = (float)Probability.norminv(1-U);
		N[size-1] = (float)Probability.norminv(U); 
		//U(i) = (i - 0.3175)/(n + 0.365) for i = 2, 3, ..., n-1
		for(int i=1; i<size-1; i++){
			U = (i+1-.3175f)/(size+0.365f);
			N[i] = (float)Probability.norminv(U);
		}		 
		return N;
	}

	

	/**
	 * Constructor. Once initialized, it computes the test statistics Z(b1), Z(b2), K^2.
	 * These can then be used to test one-sided and two-sided alternative hypothesis. 
	 * @param data the input dataset; has to have &ge; 20 elements.
	 */
	

	/** 
	 * Methods for computing the skewnewss, kurtosis, and D'Agostino-Peasrson K^2 "omnibus" test-statistics (that
	 * combine the former two), and accompanying significance (or p-values) 
	 * for testing the underlying population normality. 
	 * 
	 * <p><b>Implementation:</b> 
	 * 
	 * <br>Let <code>mk</code> denote the <code>k</code>th moment, 
	 * <code>mk = &sum;_(i=1 to n)(x_i - mean(x))/n.</code> Then, we can define the following:
	 * <br>Sample estimate of the third standardized moment: <code>b1 = m3/(m2)^(3/2)</code></br>
	 * <br>Sample estimate of the fourth standardized moment: <code>b2 = m4/m2</code></br>
	 * <br>The test statistics Z(b1), Z(b2) are approximately normally distributed under
	 * the null hypothesis of population normality. Both Z(b1) and Z(b2) can be used to
	 * test one-sided and two-sided alternative hypothesis. 
	 * 
	 * <p> 
	 * D'Agostino and Pearson (1973) also introduced the following "omnibus" test statistic,
	 * <br><code>K^2 = (Z(b1))^2 + (Z(b2))^2</code>,</br>
	 * which is able to detect deviations from normality due to either skewness or kurtosis 
	 * This K^2 statistic has an approximately Chi-Squared distribution with 2 degrees of 
	 * freedom when the population is normally distributed. 
	 * Similar to Z(b1) and Z(b2), K^2 can be used to test one-sided and two-sided alternative hypothesis. 
	 * 
	 * <p><b>Requirements</b>
	 * <br>data set has to have more than 20 elements. An error is thrown otherwise since the normal 
	 * approximation is no longer valid (technically, the it is valid for Zb1, as long as n &gt; 8, but 
	 * since we're computing everything, the requirement has been set a little higher.
	 * 
	 * <p><b>Remarks:</b>
	 * <ul>
	 * <li>
	 * The normal approximation for the zb1 is only valid for sample sizes &gt; 8. For smaller sample sizes,
	 * it is suggested that you consult a look-up table. Likewise, the normal approximation for the 
	 * <li>
	 * b1 and b2 differ from the Fisher g statistic parameters <code>g1</code> 
	 * and <code>g2</code> used in SAS, and SPSS but they are related by a linear transformation (see
	 * cited paper for details). 
	 * <li> 
	 * For users familiar with the R software package, this class is analogous to the
	 * R's <code>dagoTest</code> class in the fBasics library albeit with some implementation.
	 * </ul>
	 *<p> 
	 * References: 
	 * <br>D'Agostino, R., Albert Belanger, A., D'Agostino Jr, R., 1990 <a href="http://www.jstor.org/pss/2684359" target="_blank">
	 * A Suggestion for Using Powerful and Informative Tests of Normality</a>
	 * 
	 * <br>D?Agostino, R., Pearson, E., 1973. Tests for departures from normality. 
	 * Empirical results for the distribution of b1 and b2.Biometrika 60, 613?622.
	 */
	public static class Dago {

		protected Dago(){};
		
		/**
		 * Computes and returns an array containing the test statistic <code>zb1</code>associated with sqrt(b1) and
		 * the significance, or "p-value" of the skew test statistic <code>zb1</code>, assuming a
		 * two-tailed null hypothesis as well as . The first element of the output array is the 
		 * test statistic <code>zb1</code> associated with sqrt(b1) = m3/m2^(3/2) where mk is
		 * the kth momement as specified in {@link Descriptive#moment} and the second element is
		 * the p-value for the two-tailed null hypothesis with
		 * <pre>
		 * pValueSkew = 2* ( 1-Probability.normcdf( zb1 ) ).
		 * </pre>
		 * (D'Agostino et al. (1990)) . 
		 */
		public static float[] skew(float[] data){
			float n = data.length;
			checkLength20((int)n);

			float mean = Descriptive.mean(data);
			float m2 = Descriptive.moment(data,2,mean);
			double sd = Math.sqrt(m2);
			double m3 = Descriptive.moment(data,3,mean);
			double b1 = m3/(sd*sd*sd);
			double y = b1* Math.sqrt( (n+1)*(n+3)/( 6*(n-2) ) ); // standardized b1
			// standardized moment
			double beta2 = 3*(n*n+27*n-70)*(n+1)*(n+3)/ ( (n-2)*(n+5)*(n+7)*(n+9)  );
			double Wsquared = -1 + Math.sqrt(2*(beta2-1));
			double W = Math.sqrt(Wsquared);
			double delta = 1/Math.sqrt(Math.log(W));
			double alpha = Math.sqrt(2/(Wsquared-1));
			double yalpha = y/alpha;
			double Zb1 = (delta*Math.log(yalpha + Math.sqrt(yalpha*yalpha+1)));

			return new float[]{(float)Zb1,(float)twoTailedNormal(Zb1)};
		}

		/**
		 * Computes and returns an array containing the test statistic <code>zb2</code>associated with 
		 * b2 and the significance, or "p-value" of the kurtosis test statistic <code>zb2</code>, assuming a
		 * two-tailed null hypothesis as well as . The first element of the output array is the 
		 * test statistic <code>zb2</code> associated with b2 = m4/m2^2 where mk is
		 * the kth momement as specified in {@link Descriptive#moment}.  and the second element is
		 * the p-value for the two-tailed null hypothesis with
		 * <pre>
		 * pValueKurtosis = 2* ( 1-Probability.normcdf( zb2 ) ).
		 * </pre>
		 * (D'Agostino et al. (1990)) . 
		 */
		public static float[] kurtosis(float[] data){
			float n = data.length;
			checkLength20((int)n);
			float mean = Descriptive.mean(data);
			float m2 = Descriptive.moment(data,2,mean);
			double m4 = Descriptive.moment(data,4,mean);
			
			double b2 = m4/(m2*m2);
			double b2Mean = 3*(n-1)/(n+1);
			double b2Var = 24*n*(n-2)*(n-3)/( (n+1)*(n+1)*(n+3)*(n+5) );
			double x = (b2-b2Mean)/Math.sqrt(b2Var);
			double beta1 = 6*(n*n-5*n+2)/((n+7)*(n+9)) * Math.sqrt( (6*(n+3)*(n+5)) / (n*(n-2)*(n-3)) );  // third standardized moment of b2
			double A = 6+8/beta1* ( 2/beta1 + Math.sqrt(1+ 4/(beta1*beta1)) );
			double nineA = 9*A;
			double temp = (1-2/A)/( 1+x*Math.sqrt(2/(A-4)) );
			double Zb2 = ( (1-2/(nineA) - Math.pow (temp,1.0/3.0) )/ Math.sqrt(2/(nineA)));
			return new float[]{(float)Zb2,(float)twoTailedNormal(Zb2)};
		}
		
		/**
		 * Computes and returns an array containing the test statistic <code>chi2</code>, or the
		 * "omnibus" test statistic, and the significance, or "p-value" of this test statistic.
		 * The first element of the output array is the test statistic the ombibus test
		 * statistic <code>chi2</code> and the second element is
		 * the p-value:
		 * <pre>
		 * pValueChiSquared = ( 1-Probability.chi2cdf( chi2 ) ).
		 * </pre>
		 * (D'Agostino et al. (1990)) . 
		 */
		public static float[] chi2(float[] data){
			float n = data.length;
			checkLength20((int)n);
			float Zb1 = skew(data)[0]; float Zb2 = kurtosis(data)[0];
			double Ksquared = Zb1*Zb1 + Zb2*Zb2;
			float p = (float)( 1-Probability.chi2cdf(Ksquared,2.0) );
			return new float[]{(float)Ksquared,p};
		}
		/**
		 * Returns an array containing the three significance, or p-values, for testing normality.
		 * The first element corresponds to the p-value associated with D'Agostino's Chi-Squared
		 * test statistic (or omnibus test, {@link Dago#chi2}), the second corresponds to D'Agostino's 
		 * skew test ({@link Dago#skew}), and the 
		 * third corresponds to D'Agostino's kurtosis test ({@link Dago#kurtosis}).
		 */
		public static float[] pValues(float[] data){
			float n = data.length;
			if(n<20) throw new IllegalArgumentException("Dataset contains "+n+" elements which is" +
					"less than the minimum of 20.");
			float mean = Descriptive.mean(data);
			float m2 = Descriptive.moment(data,2,mean);
			double sd = Math.sqrt(m2);
			double m3 = Descriptive.moment(data,3,mean);
			double m4 = Descriptive.moment(data,4,mean);
			double b1 = m3/(sd*sd*sd);
			double y = b1* Math.sqrt( (n+1)*(n+3)/( 6*(n-2) ) ); // standardized b1
			// standardized moment
			double beta2 = 3*(n*n+27*n-70)*(n+1)*(n+3)/ ( (n-2)*(n+5)*(n+7)*(n+9)  );
			double Wsquared = -1 + Math.sqrt(2*(beta2-1));
			double W = Math.sqrt(Wsquared);
			double delta = 1/Math.sqrt(Math.log(W));
			double alpha = Math.sqrt(2/(Wsquared-1));
			double yalpha = y/alpha;
			double Zb1 = (delta*Math.log(yalpha + Math.sqrt(yalpha*yalpha+1)));
			// kurtosis
			double b2 = m4/(m2*m2);
			double b2Mean = 3*(n-1)/(n+1);
			double b2Var = 24*n*(n-2)*(n-3)/( (n+1)*(n+1)*(n+3)*(n+5) );
			double x = (b2-b2Mean)/Math.sqrt(b2Var);
			double beta1 = 6*(n*n-5*n+2)/((n+7)*(n+9)) * Math.sqrt( (6*(n+3)*(n+5)) / (n*(n-2)*(n-3)) );  // third standardized moment of b2
			double A = 6+8/beta1* ( 2/beta1 + Math.sqrt(1+ 4/(beta1*beta1)) );
			double nineA = 9*A;
			double temp = (1-2/A)/( 1+x*Math.sqrt(2/(A-4)) );
			double Zb2 = ( (1-2/(nineA) - Math.pow (temp,1.0/3.0) )/ Math.sqrt(2/(nineA)));
			// omnibus K-squared
			double Ksquared = Zb1*Zb1 + Zb2*Zb2;

			float pSkew = (float)twoTailedNormal(Zb1);
			float pKurtosis = (float)twoTailedNormal(Zb2);
			float pKSquared = (float)( 1-Probability.chi2cdf(Ksquared,2.0) );
			return new float[]{pKSquared,pSkew,pKurtosis};
		}
	}

	
private static double twoTailedNormal(double z){
	if(z>0) return ( 2*(1.0- (Probability.normcdf(z)) ) );
	else return (2 * (Probability.normcdf(z)));
}


}
