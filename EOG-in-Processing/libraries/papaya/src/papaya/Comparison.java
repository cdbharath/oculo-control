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
 * Contains a number of methods for comparing more than one dataset
 * against each other. Rank-based methods and are powerful in the sense that they do not rely
 * on the data assuming a normal distribution.
 */

public class Comparison implements PapayaConstants {
	/**
	 * Makes this class non instantiable, but still let's others inherit from it.
	 */
	public Comparison(){}

	/**
	 * Computes and returns the MannWhitney (or Wilcoxon Rank Sum) test statistic, <code>U</code> and p-value for 
	 * assessing whether one of two samples of independent  observations tends to have 
	 * larger values than the other. That is,
	 * <pre>U = min(U1,U2)</pre>
	 * where
	 * <pre>
	 * U1 = Sum(Ranks1)  - n1*(n1+1)/2, &nbsp;&nbsp;
	 * U2 = Sum(Ranks2) - n2*(n2+1)/2.
	 * </pre>
	 * and the significance, or p-value is computed assuming 
	 * a two-tailed null-hypothesis that both samples are similar.  
	 * @param data1 the first dataset
	 * @param data2 the second dataset
	 * @return a 2 element array with first element corresponding to the test statistic, and second
	 * equal to the p-value or significance.
	 * @throws IllegalArgumentException if either of the sample sizes is less than 8 (the normal approximation
	 *  is no longer valid in that case. Best to consult a table instead).		 
	 */
	public static float[] mannWhitney(float[] data1, float[] data2){
		int size1 = data1.length, size2 = data2.length;
		if(size1 < 8 || size2 < 8) 
			throw new IllegalArgumentException("size1 and size2 need to be greater than 8.");

		float[] allData = Mat.concat(data1,data2);
		float[] ranksAll = Rank.rank(allData,0);

		/* sum up the ranks of the first dataset*/
		float ranks1Sum = ranksAll[0];
		for(int i=1; i<size1; i++)  { ranks1Sum+=ranksAll[i];}

		/* Get the test statistics */
		float U1 = ranks1Sum - (size1 * (size1 + 1)) / 2;
		/* Use U1 + U2 = n1*n2 */
		float U2 = size1*size2 - U1;
		/* Take the minimum of the two. This becomes the test statistic */
		float minU = Math.min(U1,U2);

		// significance
		float sizeProd = size1*size2;
		float mean = sizeProd/2;
		double var = (sizeProd * (size1 + size2 + 1)) / 12.0;
		double z = (minU - mean)/Math.sqrt(var); /* Since we know z<0 */
		return new float[]{ minU, (float)twoTailedNormal(z)}; 
	}



	/**
	 * Performs the sign test on an input array and returns the test statistic used to 
	 * test the hypothesis that there is "no difference in medians" 
	 * between the continuous distributions of two random variables X and Y. It is used in
	 * the situation when we can draw <b>paired</b> samples from X and Y and is a convenient method
	 * as it makes very few assumptions about the nature of the distributions under test.
	 * </p>
	 * <p>To elaborate, the null hypothesis H0 states that given a random pair of measurements (X[i], Y[i]), 
	 * X[i] and Y[i] are equally likely to be larger than the other. 
	 * Now, let <code>n</code> be the number of pairs for which Y[i] &ne; X[i] and <code>w</code>
	 * be the number of pairs for which Y[i] - X[i] > 0. 
	 * Assuming that H0 is true, then W follows a binomial distribution W ~ b(n, 0.5). 
	 * 
	 * This method returns the sum of the terms <code>0</code> through <code>w</code> of the Binomial
	 * probability density.
	 * <pre> 
	 * Sum_(j=0 to w)  [ (n!)/(j!*(n-j)! ]  *  [ p^j * (1-p)^(n-j) ]
	 * from 0 to the test-statistic, or
	 * <p>
	 * p = Probability.binomcdf(.5,w,n).
	 * </p>
	 * For n &gt; 100, the normal distribution cdf is returned instead.
	 * If this is less than the critical value, then we can reject the null hypothesis that there
	 * is no difference between the samples X and Y.
	 * 
	 * @param x data set containing 1st observation sequence
	 * @param y data set containing 2nd observation sequence
	 * @return the significance, or p-value, assuming a two-tailed null hypothesis (i.e. no difference)
	 */
	public static float signTest(float[] x, float[] y){
		int size = x.length;
		Check.equalLengths(size,y);
		int numMore=0,numLess=0;
		for(int i=0; i<size; i++){
			int val = Float.compare(x[i],y[i]);
			if(val>0) numMore++;
			else if (val<0) numLess++;
		}
		int numTotal = numMore + numLess;
		double p; 
		int minOfTwo = Math.min(numMore,numLess);
		if(numTotal<100){
			p = Probability.binomcdf(.5, minOfTwo, numTotal);			
		} else{
			double z = (2*minOfTwo + 1 - numTotal)/Math.sqrt(numTotal);
			p = Probability.normcdf(z);
		}	
		return (float)p;
	}

	/** Methods related to comparing two populations. Each method returns an array with the
	 * first element containing the t-statistic, and the second corresponding to the 
	 * p-value, or significance, of rejecting the null hypothesis. 
	 * <p>
	 * Tests can be:<ul>
	 * <li>One-sample or two-sample</li>
	 * <li>One-sided or two-sided</li>
	 * <li>Paired or unpaired (for two-sample tests)</li>
	 * <li>Homoscedastic (equal variance assumption) or heteroscedastic
	 * (for two sample tests)</li>
	 * <li>Fixed significance level (boolean-valued) or returning p-values.
	 * </li></ul></p>
	 * <p>
	 * Test statistics are available for all tests.  Methods including "Test" in
	 * in their names perform tests, all other methods return t-statistics.  Among
	 * the "Test" methods, <code>double-</code>valued methods return p-values;
	 * <code>boolean-</code>valued methods perform fixed significance level tests.
	 * Significance levels are always specified as numbers between 0 and 0.5
	 * (e.g. tests at the 95% level  use <code>alpha=0.05</code>).</p>
	 * <p>
	* 
	 * Note: Inspired by the
	 * stats.inference.TTest class available in the Apache Commons math library. */
	public static class TTest{
		protected TTest(){}

		/** 
		 * Returns the t-statistic and p-value for checking whether the means 
		 * between the two datasets are different under the assumption that both
		 * datasets have equal variances. The p-value corresponds to the
		 * null hypothesis of no-difference (i.e. two-tailed).
		 * @return an array with the first element = t and the second element = p.
		 */
		public static float[] equalVar(float[] data1, float[] data2){
			int[] sizes = new int[]{data1.length,data2.length};
			float[] means = new float[]{Descriptive.mean(data1),Descriptive.mean(data2)};
			float[] variances = new float[]{Descriptive.var(data1,true),Descriptive.var(data2,true)};
			float pooledVariance = Descriptive.Pooled.var(variances,sizes,true);
			float sizeSum = 1f/sizes[0] + 1f/sizes[1];
			double t = (means[0]-means[1])/Math.sqrt(pooledVariance*sizeSum);
			int df = sizes[0] + sizes[1] - 2;
			float p = (float)twoTailedStudentT(t,df);
			return new float[]{(float)t,p};
		}

		/** Returns the t-statistic and p-value for checking whether the means 
		 * between the two datasets are different under the assumption that both
		 * datasets have unequal variances. The p-value corresponds to the
		 * null hypothesis of no-difference (i.e. two-tailed).
		 * This is also known as Welch's t-test.
		 * @return an array with the first element = t and the second element = p.
		 */
		public static float[] unequalVar(float[] data1, float[] data2){
			int size1 = data1.length; int size2 = data2.length; 
			float mean1 = Descriptive.mean(data1); float mean2 = Descriptive.mean(data2);
			float var1 = Descriptive.var(data1,true); float var2 = Descriptive.var(data2,true);
			float var1n1 = var1/size1; float var2n2 = var2/size2;
			// test statistic
			double t = (mean1-mean2)/Math.sqrt(var1n1 + var2n2);
			// df
			double numerator = (var1n1 + var2n2)*(var1n1 + var2n2);
			double denominator = var1n1*var1n1/(size1-1) + var2n2*var2n2/(size2-1);
			double df = (numerator/denominator);
			float p = (float)twoTailedStudentT(t,df);
			return new float[]{(float)t,p};
		}

		/** 
		 * Returns the t-statistic and p-value for checking a pair of dependent samples.
		 * That is, when there is only one sample that has been tested twice
		 * (repeated measures) or when there are two samples that have been 
		 * matched or "paired". The p-value corresponds to the
		 * null hypothesis of no-difference in both measurements (i.e. two-tailed).
		 * <p>Both datasets have to have equal lengths.
		 * @param before the dataset before treatment/measurement.
		 * @param after the dataset after treatment/measurement.
		 * @return an array with the first element = t and the second element = p.
		 */
		public static float[] paired(float[] before, float[] after){
			int size = before.length;
			Check.equalLengths(size,after);
			float[] diff =new float[size];
			float sum = 0;
			for (int i = 0; i < size; i++) {
				diff[i] += after[i]-before[i];
				sum+=diff[i];
			}	
			float mean = sum/size;
			float sd = Descriptive.std(diff,false) / (float)Math.sqrt(size);
			double t = mean/sd;
			float p = (float)twoTailedStudentT(t,size-1);
			return new float[]{(float)t,p};
		}


		/** Returns the welch's t-test degree of freedom */		 
		protected static float welchdf(float[] data1, float[] data2){
			int size1 = data1.length; int size2 = data2.length; 
			float var1 = Descriptive.var(data1,true); 
			float var2 = Descriptive.var(data2,true);
			float var1n1 = var1/size1; float var2n2 = var2/size2;
			double numerator = (var1n1 + var2n2)*(var1n1 + var2n2);
			double denominator = var1n1*var1n1/(size1-1) + var2n2*var2n2/(size2-1);
			return (float)(numerator/denominator);
		}

	}
	private static double twoTailedNormal(double z){
		if(z>0) return ( 2*(1.0- (Probability.normcdf(z)) ) );
		else return (2 * (Probability.normcdf(z)));
	}

	private static double twoTailedStudentT(double t, double df){
		if(t>0) return ( 2*(1.0- (Probability.tcdf(t,df)) ) );
		else return (2 * (Probability.tcdf(t,df)));
	}


	/**
	 * Non-parametric statistical hypothesis test used when comparing two related samples
	 * or repeated measurements on a single sample to assess whether their population mean 
	 * ranks differ (i.e. it's a paired difference test).
	 * It can be used as an alternative to the paired Student's t-test when the 
	 * population cannot be assumed to be normally distributed or the data is on the ordinal scale.
	 * <p>Note: 
	 * If we wish to make an inference about the mean (or about the median) difference, 
	 * then we assume the distribution of the differences is symmetric. 
	 * If we only want to test the hypothesis that the probability that the sum of a randomly 
	 * chosen pair of differences exceeding zero is 0.5 then no distributional assumption is needed.
	 * </p>
	 */
	//Wilcoxon signed-rank test

	/** A non-parametric method for testing whether samples originate from the same distribution.
	 * It is used for comparing more than two samples that are independent, or not related. 
	 * The parametric equivalence of the Kruskal-Wallis test is the one-way analysis of variance (ANOVA). 
	 * The factual null hypothesis is that the populations from which the samples originate, 
	 * have the same median. 
	 * When the Kruskal-Wallis test leads to significant results, 
	 * then at least one of the samples is different from the other samples. 
	 * The test does not identify where the differences occur or how many 
	 * differences actually occur. 
	 * It is an extension of the Mann?Whitney U test to 3 or more groups.
	 * The Mann-Whitney would help analyze the specific sample pairs for significant differences.
	 *
	 * <br> Since it is a non-parametric method, the Kruskal?Wallis test does not assume a normal distribution, 
	 * unlike the analogous one-way analysis of variance. 
	 * However, the test does assume an identically-shaped and scaled distribution for each group, 
	 * except for any difference in medians.
	 * */
	// Kruskal-Wallis test.

}
