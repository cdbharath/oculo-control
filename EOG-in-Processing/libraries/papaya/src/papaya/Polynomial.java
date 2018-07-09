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
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Static Class for casting one variable type to another.
 * 
 * @author Nur Adila Faruk Senan
 */
public final class Polynomial {

	/**
	 * Makes this class non instantiable, but still let's others inherit from it.
	 */
	protected Polynomial() {}
	
	/**
	 * y = polyval(x,coeff) returns the value of a polynomial of degree n evaluated at x. 
	 * The input argument coeff is an array of length n+1 whose elements are the coefficients
	 * in descending powers of the polynomial to be evaluated.
	 * <p>
	 * y = coeff[0]*x^n + coeff[1]*x^(n-1) + .... + coeff[n-1]*x + coeff[n].
	 * </p>
	 * <p>
	 * E.g. for a quadratic function, y = coeff[0]*x^2 + coeff[1]*x + coeff[2].
	 * </p>
	 * @param x argument to the polynomial.
	 * @param coeff the coefficients of the polynomial.
	 */
	public static double polyval(double x, double coeff[] ) throws ArithmeticException {
		int n = coeff.length;
		double ans = coeff[0];
		for(int i=1; i<n; i++) ans = ans*x+coeff[i];
		return ans;
	}
	
	/**
	 * y = polyval(x,coeff) returns the array values of a polynomial of degree n evaluated at 
	 * each element of x. 
	 * The input argument coeff is an array of length n+1 whose elements are the coefficients
	 * in descending powers of the polynomial to be evaluated.
	 * <p>
	 * y = coeff[0]*x^n + coeff[1]*x^(n-1) + .... + coeff[n-1]*x + coeff[n].
	 * </p>
	 * <p>
	 * E.g. for a quadratic function, y = coeff[0]*x^2 + coeff[1]*x + coeff[2].
	 * </p>
	 * @param x array argument to the polynomial.
	 * @param coeff the coefficients of the polynomial.
	 */
	public static double[] polyval(double[] x, double coeff[] ) throws ArithmeticException {
		int size = x.length;
		double[] y = new double[size];
		for(int i=0; i<size; i++) {
			y[i] = polyval(x[i],coeff);			
		}
		return y;
	}
	
	/**
	 * y = polyval(x,coeff) returns the value of a polynomial of degree n evaluated at x. 
	 * The input argument coeff is an array of length n+1 whose elements are the coefficients
	 * in descending powers of the polynomial to be evaluated.
	 * <p>
	 * y = coeff[0]*x^n + coeff[1]*x^(n-1) + .... + coeff[n-1]*x + coeff[n].
	 * </p>
	 * <p>
	 * E.g. for a quadratic function, y = coeff[0]*x^2 + coeff[1]*x + coeff[2].
	 * </p>
	 * @param x argument to the polynomial.
	 * @param coeff the coefficients of the polynomial.
	 */
	public static float polyval(float x , float coeff[]) throws ArithmeticException {
		int n = coeff.length;
		float ans = coeff[0];
		for(int i=1; i<n; i++) ans = ans*x+coeff[i];
		return ans;
	}
	
	/**
	 * y = polyval(x,coeff) returns the value of a polynomial of degree n evaluated at x. 
	 * The input argument coeff is an array of length n+1 whose elements are the coefficients
	 * in descending powers of the polynomial to be evaluated.
	 * <p>
	 * y = coeff[0]*x^n + coeff[1]*x^(n-1) + .... + coeff[n-1]*x + coeff[n].
	 * </p>
	 * <p>
	 * E.g. for a quadratic function, y = coeff[0]*x^2 + coeff[1]*x + coeff[2].
	 * </p>
	 * @param x array argument to the polynomial.
	 * @param coeff the coefficients of the polynomial.
	 */
	public static float[] polyval(float[] x, float coeff[] ) throws ArithmeticException {
		int size = x.length;
		float[] y = new float[size];
		for(int i=0; i<size; i++) {
			y[i] = polyval(x[i],coeff);			
		}
		return y;
	}
}

