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
/**
 * Cumulative distribution functions and corresponding inverses of certain probability distributions.
 * <p>
 * <b>Description:</b>
 * <br><code>...cdf</code> signifies the cumulative distribution function and is similar to 
 * the <code>q...</code> functions in <code>R</code> and the <code>...cdf</code> functions in 
 * <code>MATLAB</code>. The cdf represent the integral, from -infinity to <code>0</code> of the
 * corresponding continuous probability density function (pdf), or, the sum from <code>0</code> to </code>x</code>
 * of an associated finite pdf.</br>
 * 
 * <br><code>...inv</code> signifies the inverse of the cumulative distribution function and is similar to 
 * the <code>p...</code> functions in <code>R</code> and the <code>...inv</code> functions in 
 * <code>MATLAB</code>.</br>
 * 
 * <p><b>Implementation:</b>
 * <ul>
 * <li>Direct application examples: 
 * <br><code>normcdf(1.96) = 0.975</code> --> 97.5% of the curve is within +/-1.96 of 0.
 * <br><code>norminv(0.975) = 1.96</code> --> finds the point x (=1.96) such that <code>normcdf(x) = .975</code>.
 * <br><code>norminv(  normcdf(x) ) = x </code> 
 * <li>Significance tests:
 * <br>Two-tailed significance test with <code>alpha = 0.05</code> and <code>H_0: &mu;=0</code>
 * --> <code>p = 1-alpha/2 = 0.975</code>
 * --> Reject null hypothesis <code>&mu;=0</code> if
 * the test statistic is greater or less than than +/-1.96 <code>(= +/- norminv(p) )</code>.
 * 
 * <br>One-tailed significance test with <code>alpha = 0.05</code> and <code>H_0: &mu; &lt; 0</code>
 * --> <code>p = 1-alpha = 0.95</code>
 * --> Reject null hypothesis <code>&mu; &lt; 0</code> if
 * the test statistic is greater than 1.645 <code>(= norminv(p) )</code>.
 * <br> One-tailed significance test with <code>alpha = 0.05</code> and <code>H_0: &mu; &gt; 0</code>
 * --> <code>p = 1-alpha = 0.95</code>
 * --> Reject null hypothesis <code>&mu; &gt; 0</code> if
 * the test statistic is less than -1.645 <code>(= -norminv(p) )</code>.
 * </ul>
 * </p>
 * 
 * 
 * The code is partially adapted from the CERN Jet Java libraries, which in turn was adapted from the 
 * Cephes Mathematical Library. As far a as I can tell, Stephen L. Moshier wrote the original C++ code for 
 * CEPHES (1989, moshier@na-net.ornl.gov),  Gedeck (at Novartis) and Wolfgang Hoschek (at CERN, hats off to you mate!) 
 * then adapted it for the Java platform, making some significant changes along the way. 
 * <br>
 * 2012 rolls along, and Adila (hello!) finds the library and makes even more changes, increasing the
 * probability (ha.ha.) of things going massively wrong (pValue<.0001).
 *
 */

//public class Probability extends cern.jet.math.Constants {
public class Probability implements PapayaConstants {
	/*************************************************
	 *    COEFFICIENTS FOR METHOD  normalInverse()   *
	 *************************************************/
	/* approximation for 0 <= |y - 0.5| <= 3/8 */
	protected static final double P0[] = {
		-5.99633501014107895267E1,
		9.80010754185999661536E1,
		-5.66762857469070293439E1,
		1.39312609387279679503E1,
		-1.23916583867381258016E0,
	};
	protected static final double Q0[] = {
		1.00000000000000000000E0,
		1.95448858338141759834E0,
		4.67627912898881538453E0,
		8.63602421390890590575E1,
		-2.25462687854119370527E2,
		2.00260212380060660359E2,
		-8.20372256168333339912E1,
		1.59056225126211695515E1,
		-1.18331621121330003142E0,
	};


	/* Approximation for interval z = sqrt(-2 log y ) for z between 2 and 8
	 * i.e., y between exp(-2) = .135 and exp(-32) = 1.27e-14.
	 */
	protected static final double P1[] = {
		4.05544892305962419923E0,
		3.15251094599893866154E1,
		5.71628192246421288162E1,
		4.40805073893200834700E1,
		1.46849561928858024014E1,
		2.18663306850790267539E0,
		-1.40256079171354495875E-1,
		-3.50424626827848203418E-2,
		-8.57456785154685413611E-4,
	};
	protected static final double Q1[] = {
		1.00000000000000000000E0,
		1.57799883256466749731E1,
		4.53907635128879210584E1,
		4.13172038254672030440E1,
		1.50425385692907503408E1,
		2.50464946208309415979E0,
		-1.42182922854787788574E-1,
		-3.80806407691578277194E-2,
		-9.33259480895457427372E-4,
	};

	/* Approximation for interval z = sqrt(-2 log y ) between 8 and 64
	 * i.e., y between exp(-32) = 1.27e-14 and exp(-2048) = 3.67e-890.
	 */
	protected static final double  P2[] = {
		3.23774891776946035970E0,
		6.91522889068984211695E0,
		3.93881025292474443415E0,
		1.33303460815807542389E0,
		2.01485389549179081538E-1,
		1.23716634817820021358E-2,
		3.01581553508235416007E-4,
		2.65806974686737550832E-6,
		6.23974539184983293730E-9,
	};
	protected static final double  Q2[] = {
		1.00000000000000000000E0,
		6.02427039364742014255E0,
		3.67983563856160859403E0,
		1.37702099489081330271E0,
		2.16236993594496635890E-1,
		1.34204006088543189037E-2,
		3.28014464682127739104E-4,
		2.89247864745380683936E-6,
		6.79019408009981274425E-9,
	};

	/**
	 * Makes this class non instantiable, but still let's others inherit from it.
	 */
	protected Probability() {}
	/**
	 * Returns the area from zero to <code>x</code> under the beta density
	 * function.
	 * <pre>
	 * 
	 * P(x) = &Gamma;(a+b) / [ &Gamma;(a) * &Gamma;(b)  ] * Integral_(0 to x) [  t^(a-1) * (1-t)^(b-1) ] dt
	 *
	 * </pre>
	 * This function is identical to the incomplete beta
	 * integral function 
	 * 
	 * <code>Gamma.incompleteBeta(a, b, x)</code>.
	 *
	 * The complemented function is {@link betacdfComplemented}, specified by
	 *
	 * <code>1 - P(1-x)  =  Gamma.incompleteBeta( b, a, x )</code>;
	 *
	 */
	static public double betacdf(double x, double a, double b) {
		return Gamma.incompleteBeta( a, b, x );
	}
	/**
	 * Returns the area under the right hand tail (from <code>x</code> to
	 * infinity) of the beta density function.
	 * 
	 * This function is identical to the incomplete beta
	 * integral function <code>Gamma.incompleteBeta(b, a, x)</code>.
	 */
	static public double betacdfComplemented(double x, double a, double b ) {
		return Gamma.incompleteBeta( b, a, x );
	}
	
	/** 
	 * Returns the inverse of the beta cumulative distribution function.
	 * That is, given <code>p</code>, the function finds <code>x</code> such that
	 *	<pre>
	 *  Gamma.incompleteBeta( a, b, x ) = beta(x, a, b) = p;
	 *</pre>
	 *
	 * This function is identical to the incomplete beta inverse
	 * function <code>Gamma.incompleteBetaInverse(aa, bb, yy0)</code>.
	 *
	 * @param a the alpha parameter of the beta distribution.
	 * @param b the beta parameter of the beta distribution.
	 * @param p the value for which to solve for the corresponding <code>x</code> in the 
	 * incomplete Beta integral.
	 * @return the value <code>x</code> such that <code>beta( a, b, x ) = p</code>.
	 */
	static public double betainv( double p, double a, double b) {
		return Gamma.incompleteBetaInverse( a, b, p);
	}
	/**
	 * Returns the sum of the terms <code>0</code> through <code>k</code> of the Binomial
	 * probability density.
	 * <pre>
	 * 
	 * Sum_(j=0 to k)  [ (n!)/(j!*(n-j)! ]  *  [ p^j * (1-p)^(n-j) ]
	 *
	 * </pre>
	 * The terms are not summed directly; instead the incomplete
	 * beta integral is employed, according to the formula
	 * <p>
	 * <code>y = binomcdf( p, k, n) = Gamma.incompleteBeta( n-k, k+1, 1-p )</code>.
	 * <p>
	 * All arguments must be positive, 
	 * @param k end term.
	 * @param n the number of trials.
	 * @param p the probability of success (must be in <code>(0.0,1.0)</code>).
	 */
	static public double binomcdf(double p, int k, int n) {
		if( (p < 0.0) || (p > 1.0) ) throw new IllegalArgumentException();
		if( (k < 0) || (n < k) ) throw new IllegalArgumentException();

		if( k == n ) return( 1.0 );
		if( k == 0 ) return Math.pow( 1.0-p, n-k );

		return Gamma.incompleteBeta( n-k, k+1, 1.0 - p );
	}
	/**
	 * Returns the sum of the terms <code>k+1</code> through <code>n</code> of the Binomial
	 * probability density.
	 * <pre>
	 * 
	 * Sum_(j=k+1 to n)  [ (n!)/(j!*(n-j)! ]  *  [ p^j * (1-p)^(n-j) ]
	 *
	 * </pre>
	 * The terms are not summed directly; instead the incomplete
	 * beta integral is employed, according to the formula
	 * <p>
	 * <code>y = binomcdfComplemented( p, k, n ) = Gamma.incompleteBeta( k+1, n-k, p )</code>.
	 * <p>
	 * All arguments must be positive, 
	 * @param k (start-1) term.
	 * @param n the number of trials.
	 * @param p the probability of success (must be in <code>(0.0,1.0)</code>).
	 */
	static public double binomcdfComplemented( double p , int k, int n) {
		if( (p < 0.0) || (p > 1.0) ) throw new IllegalArgumentException();
		if( (k < 0) || (n < k) ) throw new IllegalArgumentException();

		if( k == n ) return( 0.0 );
		if( k == 0 ) return 1.0 - Math.pow( 1.0-p, n-k );

		return Gamma.incompleteBeta( k+1, n-k, p );
	}
	
	/** 
	 * Finds the event probability p such that the sum of the
	 * terms 0 through k of the Binomial probability density
	 * is equal to the given cumulative probability y. That is,
	 *<pre>
	 * binomcdf( p, k, n ) = y;
	 *</pre>
	 * This is accomplished using the inverse beta integral
	 * function and the relation
	 * <p>
	 * 1 - p = Gamma.incompleteBetaInverse( n-k, k+1, y ).
	 * </p>
	 * All arguments must be positive.
	 * @param k end term.
	 * @param n the number of trials.
	 * @param y the value to solve for such that <code>binomcdf( p, k, n ) = y</code>.
	 */	
	static public double binominv( double y, int k, int n){
	double dk, dn, p;

	if( (y < 0.0) || (y > 1.0) ) throw new IllegalArgumentException("Test statistic not within possible range.");
	if( (k < 0) || (n < k) ) throw new IllegalArgumentException();

	dn = n - k;
	if( k == 0 )
		{
		if( y > 0.8 )
			p = -Math.expm1( Math.log1p(y-1.0) / dn );
		else
			p = 1.0 - Math.pow( y, 1.0/dn );
		}
	else
		{
		dk = k + 1;
		p = Gamma.incompleteBeta( dn, dk, 0.5 );
		if( p > 0.5 )
			p = Gamma.incompleteBetaInverse( dk, dn, 1.0-y );
		else
			p = 1.0 - Gamma.incompleteBetaInverse( dn, dk, y );
		}
	return( p );
	}
	
	/**
	 * Returns the area under the left hand tail (from 0 to <code>x</code>)
	 * of the Chi square probability density function with
	 * <code>v</code> degrees of freedom.
	 * <pre>
	 * 
	 * P( x | v ) = 1/ [ 2^(v/2) * &Gamma;(v/2)  ] * Integral_(from 0 to x) [ t^(v/2-1) * e^(-t/2) ] dt
	 *                                  
	 * </pre>
	 * where <code>x</code> is the Chi-square variable.
	 * <p>
	 * The incomplete gamma integral is used, according to the
	 * formula
	 * <p>
	 * <code>y = chi2cdf( x, v ) = Gamma.incompleteGamma( v/2.0, x/2.0 )</code>.
	 * <p>
	 * The arguments must both be positive.
	 *
	 * @param v degrees of freedom.
	 * @param x integration end point.
	 */
	static public double chi2cdf(double x, double v) throws ArithmeticException { 
		if( x < 0.0 || v < 1.0 ) return 0.0;
		
		//if(v==2) return 1-Math.exp(x/2.0); // special case.
		
		return Gamma.incompleteGamma( v/2.0, x/2.0 );
	}
	/**
	 * Returns the area under the right hand tail (from <code>x</code> to
	 * infinity) of the Chi square probability density function
	 * with <code>v</code> degrees of freedom.
	 * <pre>
	 * 
	 * P( x | v ) = 1/ [ 2^(v/2) * &Gamma;(v/2)  ] * Integral_(from x to infinity) [ t^(v/2-1) * e^(-t/2) ] dt
	 *
	 * </pre>
	 * where <code>x</code> is the Chi-square variable.
	 *
	 * The incomplete gamma integral is used, according to the
	 * formula
	 *
	 * <code>y = chi2cdfComplemented( x, v ) = incompleteGammaComplement( v/2.0, x/2.0 )</code>.
	 *
	 * The arguments must both be positive.
	 *
	 * @param v degrees of freedom.
	 * @param x integration start point.
	 */
	static public double chi2cdfComplemented(double x, double v) throws ArithmeticException { 
		if( x < 0.0 || v < 1.0 ) return 0.0;
		return Gamma.incompleteGammaComplement( v/2.0, x/2.0 );
	}
	/**
	 * Returns the error function of the normal distribution.
	 * The integral is
	 * <pre>
	 * 
	 * erf(x) = 2/sqrt(PI) * Integral_(0 to x) [ e^(-t^2) ] dt
	 *                           
	 * </pre>
	 * <b>Implementation:</b>
	 * For <code>0 <= |x| < 1, erf(x) = x * P4(x^2)/Q5(x^2) </code>; otherwise
	 * <code>erf(x) = 1 - erfc(x)</code>.
	 * <p>
	 *
	 * @param x the integral end point.
	 */
	static public double erf(double x) throws ArithmeticException { 
		double y, z;
		final double T[] = {
				9.60497373987051638749E0,
				9.00260197203842689217E1,
				2.23200534594684319226E3,
				7.00332514112805075473E3,
				5.55923013010394962768E4
		};
		final double U[] = {
				1.00000000000000000000E0,
				3.35617141647503099647E1,
				5.21357949780152679795E2,
				4.59432382970980127987E3,
				2.26290000613890934246E4,
				4.92673942608635921086E4
		};

		if( Math.abs(x) > 1.0 ) return( 1.0 - erfComplemented(x) );
		z = x * x;
		y = x * Polynomial.polyval( z, T) / Polynomial.polyval( z, U);
		return y;
	}
	/**
	 * Returns the complementary Error function of the normal distribution; formerly named <code>erfc</code>.
	 * <pre>
	 * 
	 *  erfc(x) = 2/sqrt(PI) * Integral_(x to inf) [ e^(-t^2) ] dt
	 *  		= 1 - 2/sqrt(PI) * Integral_(0 to x) [ e^(-t^2) ] dt
	 *  		= 1 - erf(x).
	 *			  
	 * </pre>
	 * <b>Implementation:</b>
	 * For small x, <code>erfc(x) = 1 - erf(x)</code>; otherwise rational
	 * approximations are computed.
	 * <p>
	 * @param a corresponds to <code>x</code> above. An internal check is performed to see if
	 * <code>a</code> is &lt; 0 or otherwise. If <code>a &lt; 0 --> x = -a.</code> Else, <code>x = a</code>.
	 */
	static public double erfComplemented(double a) throws ArithmeticException { 
		double x,y,z,p,q;

		double P[] = {
				2.46196981473530512524E-10,
				5.64189564831068821977E-1,
				7.46321056442269912687E0,
				4.86371970985681366614E1,
				1.96520832956077098242E2,
				5.26445194995477358631E2,
				9.34528527171957607540E2,
				1.02755188689515710272E3,
				5.57535335369399327526E2
		};
		double Q[] = {
				1.00000000000000000000E0,
				1.32281951154744992508E1,
				8.67072140885989742329E1,
				3.54937778887819891062E2,
				9.75708501743205489753E2,
				1.82390916687909736289E3,
				2.24633760818710981792E3,
				1.65666309194161350182E3,
				5.57535340817727675546E2
		};

		double R[] = {
				5.64189583547755073984E-1,
				1.27536670759978104416E0,
				5.01905042251180477414E0,
				6.16021097993053585195E0,
				7.40974269950448939160E0,
				2.97886665372100240670E0
		};
		double S[] = {
				1.00000000000000000000E0, 
				2.26052863220117276590E0,
				9.39603524938001434673E0,
				1.20489539808096656605E1,
				1.70814450747565897222E1,
				9.60896809063285878198E0,
				3.36907645100081516050E0
		};

		if( a < 0.0 )   x = -a;
		else            x = a;

		if( x < 1.0 )   return 1.0 - erf(a);

		z = -a * a;

		if( z < -MAXLOG ) {
			if( a < 0 )  return( 2.0 );
			else         return( 0.0 );
		}

		z = Math.exp(z);

		if( x < 8.0 ) {
			p = Polynomial.polyval( x, P );
			q = Polynomial.polyval( x, Q );
		} else {
			p = Polynomial.polyval( x, R);
			q = Polynomial.polyval( x, S );
		}

		y = (z * p)/q;

		if( a < 0 ) y = 2.0 - y;

		if( y == 0.0 ) {
			if( a < 0 ) return 2.0;
			else        return( 0.0 );
		}

		return y;
	}
	/**
	 * Returns the area from zero to <code>x</code> under the F density
	 * function (also known as Snedcor's density or the
	 * variance ratio density); formerly named <code>fdtr</code>.
	 * Corresponds to the left hand tail of the F-distribution.
	 * 
	 * The incomplete beta integral is used, according to the formula
	 * <pre>
	 *
	 * P(x) = Gamma.incompleteBeta( df1/2, df2/2, (df1*x/(df2 + df1*x) ).
	 *
	 * </pre>
	 * 
	 * @param df1 ( greater than or equal to 1 )
	 * @param df2 ( greater than or equal to 1 )
	 * @param x the integration end point (greater than or equal to 0).
	 */
	public static double fcdf( double x, int df1, int df2 ){
		double a, b, w;

		if( (df1 < 1) || (df2 < 1) || x < 0.0 ) throw new IllegalArgumentException("F-distribution domain error.");
		a = df1;
		b = df2;
		w = a * x;
		w = w / (b + w);
		return Gamma.incompleteBeta(0.5*a, 0.5*b, w);
	}

	/**
	 * Returns the area from x to infinity under the F density
	 * function (also known as Snedcor's density or the
	 * variance ratio density); formerly named <code>fdtrc</code>. 
	 * Corresponds to the right hand tail of the F-distribution ( and 
	 * to the values presented in F-distribution tables). 
	 * 
	 * The incomplete beta integral is used, according to the
	 * relation
	 *<pre>
	 * 
	 * P(x) = Gamma.incompleteBeta( df2/2, df1/2, (df2/(df2 + df1*x) )
	 * 
	 * </pre>
	 * 
	 * @param df1  df1 ( greater than or equal to 1 )
	 * @param df2  df2 ( greater than or equal to 1 )
	 * @param x the left integration point. Corresponds to the F value in look-up tables.
	 * @return The area from <code>x</code> to inf. under the F density function.
	 */
	public static double fcdfComplemented(double x, int df1, int df2){

		double a, b, w;

		if( (df1 < 1) || (df2 < 1) || (x < 0.0) )  throw new IllegalArgumentException("Complementary F-distribution domain error.");

		a = df1;
		b = df2;
		w = b / (b + a * x); // df1 / (df1 + df2*x)
		return Gamma.incompleteBeta( 0.5*b, 0.5*a, w ); 
	}

	/**
	 * Finds the F density argument x such that the integral
	 * from 0 to <code>x</code> of the F density is equal to p; formerly named <code>fdtri</code>.
	 * That is, it solves for the value of <code>x</code>, given <code>p</code> such that
	 * <pre>
	 * fcdf(x,df1,df2) - p = 0.
	 * </pre>
	 * 
	 * This is accomplished using the inverse beta integral
	 * function and the relations
	 *<pre>
	 * z = incompleteBetaInverse( df2/2, df1/2, p ),
	 * x = 1 - df2 * (1-z) / (df1 z),
	 *</pre>
	 * if <code>p &gt; Gamma.incompleteBeta( 0.5*df1, 0.5*df2, 0.5 );</code>, or <code>p &lt; 0.001</code> and,
	 *<pre>
	 * z = incompleteBetaInverse( df1/2, df2/2, p ),
	 * x = 1 - df2 * z / (df1 (1-z)),
	 * </pre>
	 * otherwise. 
	 * The significance level for a one-sided test (or &alpha; in look-up tables) is then <code>1-x</code>
	 *  
	 * @param df1 degrees of freedom of the first dataset
	 * @param df2 degrees of freedom of the second dataset
	 * @param p the fcdf value for which to solve for x  
	 * @return the point x such that fcdf(x,df1,df2) = p.
	 * @throws IllegalArgumentException if df1 or df2 are less than 1, or p is not in (0,1].
	 */
	public static double finv( double p, int df1, int df2){
		double a, b, w, x;

		if( (df1 < 1) || (df2 < 1) || (p <= 0.0) || (p > 1.0) )
			throw new IllegalArgumentException("Domain error in fInverse");
		a = df1;
		b = df2;
		/* Compute probability for x = 0.5.  */
		w = Gamma.incompleteBeta( 0.5*b, 0.5*a, 0.5 );
		/* If that is greater than y, then the solution is in w < .5.
   		Otherwise, solve at 1-y to remove cancellation in (b - b*w).  */
		if( w > p || p < 0.001)
		{ 
			/* FComplemented: w = b / (b + a * x);  --> x = b(1/w- 1)/a = b*(1-w)/(a*w) */
			w = Gamma.incompleteBetaInverse( 0.5*b, 0.5*a, p );
			x = (b - b*w)/(a*w);
		}
		else
		{  
			/* F: w = a*x/ (a*x+b) --> solve for x = b*w/(a*(1-w))
			 */
			w = Gamma.incompleteBetaInverse( 0.5*a, 0.5*b, 1.0-p );// incbi( 0.5*a, 0.5*b, 1.0-p );
			x = b*w/(a*(1.0-w));
		}
		return x;
	}


	/**
	 * Returns the integral from zero to <code>x</code> of the gamma probability
	 * density function.
	 * <pre>
	 * 
	 * y = a^b / &Gamma;(b) * Integral_(0 to x) [ t^(b-1) e^(-a*t) ]dt
	 *
	 * </pre>
	 * The incomplete gamma integral is used, according to the
	 * relation
	 *
	 * <code>y = Gamma.incompleteGamma( b, a*x )</code>.
	 *
	 * @param a the paramater a (alpha) of the gamma distribution.
	 * @param b the paramater b (beta, lambda) of the gamma distribution.
	 * @param x integration end point.
	 * @return y the integral from zero to <code>x</code> of the gamma pdf.
	 */
	static public double gammacdf( double x, double a, double b ) {
		if( x < 0.0 ) return 0.0;
		return Gamma.incompleteGamma(b, a*x);
	}
	/**
	 * Returns the integral from <code>x</code> to infinity of the gamma
	 * probability density function:
	 * <pre>
	 * 
	 * y = a^b / &Gamma;(b) * Integral_(from x to infinity) [t^(b-1) * e^(-a*t) ] dt
	 *
	 * </pre>
	 * The incomplete gamma integral is used, according to the
	 * relation
	 * <p>
	 * y = Gamma.incompleteGammaComplement( b, a*x ).
	 *
	 * @param a the paramater a (alpha) of the gamma distribution.
	 * @param b the paramater b (beta, lambda) of the gamma distribution.
	 * @param x integration start point.
	 */
	static public double gammacdfComplemented( double x, double a, double b ) {
		if( x < 0.0 ) return 0.0;
		return Gamma.incompleteGammaComplement(b, a*x);
	}
	/**
	 * Returns the sum of the terms <code>0</code> through <code>k</code> of the Negative Binomial Distribution.
	 * <pre>
	 * 
	 * Sum_(from j=0 to k)  [ (n+j-1)! / ( j! * (n-1)! ) ] * [ p^n* (1-p)^j ]
	 *   
	 * </pre>
	 * In a sequence of Bernoulli trials, this is the probability
	 * that <code>k</code> or fewer failures precede the <code>n</code>-th success.
	 * <p>
	 * The terms are not computed individually; instead the incomplete
	 * beta integral is employed, according to the formula
	 * <p>
	 * <code>y = nbinomcdf( p, k, n ) = Gamma.incompleteBeta( n, k+1, p )</code>.
	 *
	 * All arguments must be positive, 
	 * @param k end term.
	 * @param n the number of trials.
	 * @param p the probability of success (must be in <code>(0.0,1.0)</code>).
	 */
	static public double nbinomcdf(double p, int k, int n) {
		if( (p < 0.0) || (p > 1.0) ) throw new IllegalArgumentException();
		if(k < 0) return 0.0;

		return Gamma.incompleteBeta( n, k+1, p );
	}
	/**
	 * Returns the sum of the terms <code>k+1</code> to infinity of the Negative
	 * Binomial distribution.
	 * <pre>
	 * 
	 * Sum_(from j=k+1 to inf)  [ (n+j-1)! / ( j! * (n-1)! ) ] * [ p^n* (1-p)^j ]
	 * 
	 * </pre>
	 * The terms are not computed individually; instead the incomplete
	 * beta integral is employed, according to the formula
	 * <p>
	 * y = nbinomcdfComplemented( p, k, n ) = Gamma.incompleteBeta( k+1, n, 1-p ).
	 *
	 * All arguments must be positive, 
	 * @param k (start-1) term.
	 * @param n the number of trials.
	 * @param p the probability of success (must be in <code>(0.0,1.0)</code>).
	 */
	static public double nbinomcdfComplemented( double p, int k, int n ) {
		if( (p < 0.0) || (p > 1.0) ) throw new IllegalArgumentException();
		if(k < 0) return 0.0;

		return Gamma.incompleteBeta( k+1, n, 1.0-p );
	}
	/**
	 * Returns the area under the Normal (Gaussian) probability density
	 * function, integrated from minus infinity to <code>x</code> (assumes mean is zero, variance is one).
	 * <pre>
	 * 
	 * normcdf(x) = 1/sqrt(2pi) * Integral_(-inf to x) [ e^(-t^2/2) ] dt
	 *           =  ( 1 + erf(z) ) / 2
	 *           =  erfc(z) / 2
	 *           
	 * </pre>
	 * where <code>z = x/sqrt(2)</code>.
	 * Computation is via the functions <code>erf</code> and <code>erfComplement</code>.
	 * <p>
	 * Relation to alpha value (or confidence level):
	 * <ul>
	 * <br>Two tailed test: <code>alpha = 2*( 1-normcdf(x) )</code>.</br>
	 * <br>one-tailed test: <code>alpha = ( 1-normcdf(x) )</code>.</br>
	 * </ul>
	 * </p>
	 * @param a <code>x</code> in the equation above. 
	 */
	static public double normcdf( double a) throws ArithmeticException { 
		double x, y, z;

		x = a * SQRTH;
		z = Math.abs(x);

		if( z < SQRTH ) y = 0.5 + 0.5 * erf(x);
		else {
			y = 0.5 * erfComplemented(z);
			if( x > 0 )  y = 1.0 - y;
		}

		return y;
	}
	/**
	 * Returns the area under the Normal (Gaussian) probability density
	 * function, integrated from minus infinity to <code>x</code> when the
	 * data has not been standardized (i.e. mean &ne;0, variance &ne;1).
	 * <pre>
	 *  
	 * normcdf(x,mean,variance) = 1/sqrt(2pi*v) * Integral_(-inf to x) [ e^(-(t-mean)^2 / 2v) ] dt
	 *
	 * </pre>
	 * where <code>v = variance</code>.
	 * Computation is via the functions <code>erf</code>.
	 *
	 * @param mean the mean of the normal distribution.
	 * @param variance the variance of the normal distribution.
	 * @param x the integration limit.
	 */
	static public double normcdf(double x, double mean, double variance) throws ArithmeticException {
		if (x>0) 
			return 0.5 + 0.5*erf((x-mean)/Math.sqrt(2.0*variance));
		else 
			return 0.5 - 0.5*erf((-(x-mean))/Math.sqrt(2.0*variance));
	}
	/**
	 * Returns the value, <code>x</code>, for which the area under the
	 * Normal (Gaussian) probability density function (integrated from
	 * minus infinity to <code>x</code>) is equal to the argument <code>y</code> 
	 * (assumes mean is zero, variance is one); formerly named <code>ndtri</code>.
	 * That is,
	 * <p>
	 * normcdf(x) = y0.
	 * </p>
	 * <p>
	 * For small arguments <code>0 < y < exp(-2)</code>, the program computes
	 * <code>z = sqrt( -2.0 * log(y) )</code>;  then the approximation is
	 * <code>x = z - log(z)/z  - (1/z) P(1/z) / Q(1/z)</code>.
	 * There are two rational functions P/Q, one for <code>0 < y < exp(-32)</code>
	 * and the other for <code>y</code> up to <code>exp(-2)</code>. 
	 * For larger arguments,
	 * <code>w = y - 0.5</code>, and  <code>x/sqrt(2pi) = w + w**3 R(w**2)/S(w**2))</code>.
	 * Finally, this returns <code>-infinity</code> for <code>y0 = 0</code> 
	 * and +infinity for <code>y0 = 1</code>.
	 *
	 * @param y0 the argument to the function
	 */
	static public double norminv( double y0) throws ArithmeticException { 
		double x, y, z, y2, x0, x1;
		int code;

		final double s2pi = Math.sqrt(2.0*Math.PI);

		if( y0 < 0.0 ) throw new IllegalArgumentException();
		if( y0 > 1.0 ) throw new IllegalArgumentException();
		if( y0==0.0 )return -Double.NEGATIVE_INFINITY;
		if( y0==1.0 )return Double.POSITIVE_INFINITY;
		
		code = 1;
		y = y0;
		if( y > (1.0 - 0.13533528323661269189) ) { /* 0.135... = exp(-2) */
			y = 1.0 - y;
			code = 0;
		}

		if( y > 0.13533528323661269189 ) {
			y = y - 0.5;
			y2 = y * y;
			x = y + y * (y2 * Polynomial.polyval( y2, P0)/Polynomial.polyval( y2, Q0));
			x = x * s2pi; 
			return(x);
		}

		x = Math.sqrt( -2.0 * Math.log(y) );
		x0 = x - Math.log(x)/x;

		z = 1.0/x;
		if( x < 8.0 ) /* y > exp(-32) = 1.2664165549e-14 */
			x1 = z * Polynomial.polyval( z, P1 )/Polynomial.polyval( z, Q1 );
		else
			x1 = z * Polynomial.polyval( z, P2 )/Polynomial.polyval( z, Q2 );
		x = x0 - x1;
		if( code != 0 )
			x = -x;
		return( x );
	}
	/**
	 * Returns the sum of the first <code>k</code> terms of the Poisson distribution.
	 * <pre>
	 * 
	 * Sum_(j=0 to k) [ e^(-m) *  m^j /j! ]
	 *
	 * </pre>
	 * The terms are not summed directly; instead the incomplete
	 * gamma integral is employed, according to the relation
	 * <p>
	 * <code>y = poissoncdf( mean, k m ) = Gamma.incompleteGammaComplement( k+1, mean )</code>.
	 *
	 * The arguments must both be positive.
	 *
	 * @param k number of terms.
	 * @param mean the mean of the poisson distribution.
	 */
	static public double poissoncdf( double mean, int k) throws ArithmeticException { 
		if( mean < 0 ) throw new IllegalArgumentException();
		if( k < 0 ) return 0.0;
		return Gamma.incompleteGammaComplement((double)(k+1) ,mean);
	}
	/**
	 * Returns the sum of the terms <code>k+1</code> to <code>Infinity</code> of the Poisson distribution.
	 * <pre>
	 *  
	 * Sum_(j=k+1 to inf.) [ e^(-m) *  m^j /j! ]
	 * 
	 * </pre>
	 * The terms are not summed directly; instead the incomplete
	 * gamma integral is employed, according to the formula
	 * <p>
	 * <code>y = poissoncdfComplemented( k, m ) = Gamma.incompleteGamma( k+1, m )</code>.
	 *
	 * The arguments must both be positive.
	 *
	 * @param k (start-1) term.
	 * @param mean the mean of the poisson distribution.
	 */
	static public double poissoncdfComplemented(int k, double mean) throws ArithmeticException { 
		if( mean < 0.0 ) throw new IllegalArgumentException();
		if( k < -1 ) return 0.0;
		return Gamma.incompleteGamma((double)(k+1),mean);
	}
	/**
	 * Returns the integral from minus infinity to <code>t</code> of the Student-t 
	 * distribution with <code>k &gt; 0</code> degrees of freedom.
	 * <pre>
	 * 
	 * &Gamma;((k+1)/2) / [ sqrt(k*pi) * &Gamma;(k/2)  ] * Integral_(-inf to t) [  ( 1+x^2/k )^( (-k+1)/2 ) ] dx
	 * 
	 * </pre>
	 * Relation to incomplete beta integral:
	 * <p>
	 * <br><code>tcdf(t,k) = 0.5 * Gamma.incompleteBeta( k/2, 1/2, z )</code>, when <code>t &lt; 0</code>,</br>
	 * <br><code>tcdf(t,k) = 1-0.5 * Gamma.incompleteBeta( k/2, 1/2, z )</code>, when <code>t &gt; 0</code>,</br>
	 * </p>
	 * where <code>z = k/(k + t^2)</code>.
	 * Relation to alpha value (or confidence level):
	 * <ul>
	 * <br>Two tailed test: <code>alpha = 2*( 1-tcdf(t,k) )</code>.</br>
	 * <br>one-tailed test: <code>alpha = ( 1-tcdf(t,k) )</code>.</br>
	 * </ul>
	 * @param k degrees of freedom.
	 * @param t integration end point.
	 */
	static public double tcdf(double t, double k) throws ArithmeticException { 
		if( k <= 0.0 ) throw new IllegalArgumentException();
		if( t == 0.0 ) return( 0.5 );
	
		if( t < 0 ){ 
			return 0.5 * Gamma.incompleteBeta( 0.5*k, 0.5, k / (k + t * t) );
		}
		else {
			return 1.0 - 0.5 * Gamma.incompleteBeta( 0.5*k, 0.5, k / (k + t * t) );
		}
	}
	/**
	 * Returns the value, <code>t</code>, for which the area under the
	 * Student-t probability density function (integrated from
	 * minus infinity to <code>t</code>) is equal to p.
	 * The function uses the tcdf function to determine the return
	 * value iteratively.
	 *
	 * @param p the fcdf value for which to solve for x  
	 * @param k the degrees of freedom
	 */
	public static double tinv(double p, double k) {
		if(p<0 || p>1) throw new IllegalArgumentException(p + "is not a valid value for p");
		
		if(p==.5) return 0;
		if(p==0.0) return tcdf(1e-8,k);
		if(p==1.0) return tcdf(1-1e-8,k);
		
		
		/* if p is in the left region, correct it. */
		double cumProb = p; // Cumulative probability = 1-alpha/2
		int multiplier = 1;
		// Solution lies in the left half of the student-T curve ( t < 0)
		// so we solve for the equivalent t-value in the right half, and then
		// multiply that by -1.
		if(cumProb < .5){
			cumProb = 1-cumProb; 
			multiplier = -1;
		}
		
		double f1,f2,f3;
		double x1,x2,x3;
		double g,s12;

		// make a first approximation using the norm-inv function.
		x1 = norminv(cumProb);
		// Return inverse of normal for large size (k > 30 is pretty sufficient usually).
		if (k > 200) {
			return x1;
		}

		/* Find a pair of x1,x2 that bracket zero to 
		   guarantee asymptotic convergence later on */
		f1 = tcdf(x1,k)-cumProb;
		x2 = x1; f2 = f1;
		do {
			if (f1>0) { 
				x2 = x2/2; // decrease x2
			} else {
				x2 = x2+x1; // increase x2
			}
			f2 = tcdf(x2,k)-cumProb; // 
		} while (f1*f2>0); 

		// Find better approximation now that f1 and f2 bracket zero.
		// Pegasus-method (also known as the false-position-method or regula-falsi-method)
		do {
			// Calculate slope of secant and t value for which it is 0.
			s12 = (f2-f1)/(x2-x1);
			x3 = x2 - f2/s12;

			// Calculate function value at x3
			f3 = tcdf(x3,k)-cumProb;
			if (Math.abs(f3)<1e-8) { // This criteria needs to be very tight!
				// We found a perfect value -> return
				return multiplier*x3;
			}

			if (f3*f2<0) {
				x1=x2; f1=f2;
				x2=x3; f2=f3;
			} else {
				g = f2/(f2+f3);
				f1=g*f1;
				x2=x3; f2=f3;
			}
		} while(Math.abs(x2-x1)>0.001);

		if (Math.abs(f2)<=Math.abs(f1)) {
			return multiplier*x2;
		} else {
			return multiplier*x1;
		}
	}
}
