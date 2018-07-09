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

 * Computes the one-way ANOVA p-value to test the equality of two or more sample
 * means by analyzing the sample variances using the test statistic 
 * <code>F = variance between samples / variance within samples</code>.
 * Once initialized, the following values are computed and stored:
 * <ul>
 * <li>dfbg: degrees of freedom between the groups ({@link dfbg()}).
 * <li>ssbg: sum of squared deviates between the groups ({@link ssbg()}).
 * <li>dfwg: degrees of freedom within the groups, or "Error" dfs ({@link dfwg()}).
 * <li>sswg: sum of squared deviates within the groups ({@link sswg()}).
 * <li>F: the test statistic, computed as (ssbg/dfbg) / (sswg/dfwg) ({@link F()}).
 * <li>pValue: the p-value, or significance level, corresponding to a two-tailed
 * null hypothesis of no difference between the samples ({@link pValue()}).
 * </ul>
 * 
 * <p><strong>Preconditions</strong>: <ul>
 * <li>The categoryData <code>Collection</code> must contain
 * <code>float[]</code> arrays.</li>
 * <li> There must be at least two <code>float[]</code> arrays in the
 * <code>categoryData</code> collection and each of these arrays must
 * contain at least two values.</li></ul></p>
 * 
 * <p>This implementation is based on a description provided in Chapter 13 of the VassarStats
 * <a href="http://faculty.vassar.edu/lowry/ch13pt1.html" target="_blank">online textbook</a>
 * Mean values are not computed directly in the algorithm as this tends to cause round-off error.
 * Rather, we use the more stable <code>SumOfSquares - Sum^2</code>:
 * <pre>
 * Sum_(i=1 to n) (x_i - mean(x) )^2 = Sum_(i=1 to n)(x_i)^2 - (Sum_(i=1 to n) x_i)^2/n.
 * </pre>
 * </p>
 * 
 */


public class OneWayAnova{
	int dfbg = 0;
	int dfwg = 0;
	double sswg = 0; 
	double ssbg = 0;
	double sst = 0; // total sum of squares
	double F = 0;
	double p = -1;
	/**
	 * Performs an Analysis of Variance (ANOVA) for a collection of float arrays.
	 * @param categoryData the data coresponding to the different groups
	 * @see Collection
	 */
	public  OneWayAnova(Collection<float[]> categoryData){
		if(categoryData.size()<2)throw new IllegalArgumentException("Input needs to contain at least two float arrays");

		double totsum = 0;
		double totsumsq = 0;
		int totnum = 0;

		for (float[] data : categoryData) {
			if(data.length<2) throw new IllegalArgumentException("Each float array has to contain at least two elements");
			double sum = 0;
			double sumsq = 0;
			int num = 0;

			for (int i = 0; i < data.length; i++) {
				double val = data[i];

				double valSquared = val*val;
				// within category
				num++; 
				sum += val;
				sumsq +=valSquared;

				// for all categories
				totnum++;
				totsum += val;
				totsumsq += valSquared;
			}
			dfwg += num - 1; // df2 = sum(n_i-1)
			double ss = sumsq - sum * sum / num;
			sswg += ss; // add the sum of squares to the final tally
		}
		sst = totsumsq - totsum * totsum/totnum; // total sum of squares
		ssbg = sst - sswg; // error sum of squares
		dfbg = categoryData.size() - 1; // df1 = k-1
		double msbg = ssbg/dfbg;
		double mswg = sswg/dfwg;
		F = msbg/mswg;
		
		p = 1.0 - Probability.fcdf(F,dfbg,dfwg);
	}
	
	/** 
	 * Computes eta^2: a measure of the strength of the <i>curvilinear</i> relationship 
	 * between the independent and dependent variable. Useful in the context of
	 * one-way ANOVAs of independent samples. For example, the independent
	 * variable could be the amount of papaya consumed by 4 different groups 
	 * (0, 1, 2, or 3 papayas/dar) and the dependent variable could be a measurement of their
	 * satisfaction with life. 
	 */
	public float etasqrd(){
		return (float)(ssbg/sst);
	}
	/**
	 * Returns the degrees of freedom between groups. That is,
	 * if there are k groups, dfbg = k-1;
	 */
	public int dfbg(){
		return dfbg;
		
	}
	
	/**
	 * Returns the degrees of freedom within each group. That is,
	 * if there are k groups, and each have got n_i elements,
	 * dfwg = (n_1-1) + (n_2 -1) + ... + (n_k - 1);
	 */
	public int dfwg(){
		return dfwg;
		
	}
	
	/**
	 * Returns the test statistic F. That is,
	 * <pre> 
     * F = (ssbg/dfbg) / (sswg/dfwg);
     * </pre>
	 */
	public float F(){
		return (float)(F);
		
	}
	
	/**
	 * Returns the significance, or "p-value" of the test statistic {@link F()}.
	 * That is,
	 * <pre> 
     * p = 1 - Probability.fcdf(F,dfbg,dfwg);
     * </pre>
	 */
	public float pValue(){
		return (float)(p);
		
	}
	
	/**
	 * Returns the sum of squared deviates of the data within each group. That is,
	 * if there are k groups, and each have got n_i elements,
	 * <pre>
	 * sswg = Sum_(i=1 to n1) (xi1-mean(x1))^2 + Sum_(i=1 to n2) (xi2-mean(xk))^2 + ... + Sum_(i=1 to nk) (xik-mean(xk))^2;
	 * </pre>
	 */
	public float sswg(){
		return (float)sswg;
		
	}
	
	/**
	 * Returns the sum of squared deviates between each group. That is,
	 * if there are k groups, each with mean value y_i, then
	 * <pre>
	 * ssbg = Sum_(i=1 to k) (y_i)-mean(y))
	 * </pre>
	 */
	public float ssbg(){
		return (float)ssbg;
		
	}
	

}
