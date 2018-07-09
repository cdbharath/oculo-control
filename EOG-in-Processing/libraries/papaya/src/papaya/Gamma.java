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
 * Gamma and Beta functions.
 * <p>
 * <b>Implementation:</b>
 * The code is mostly adapted from the CERN
 * Jet Java libraries, which in turn was adapted from the Cephes Mathematical Library. As far a
 * as I can tell, Stephen L. Moshier wrote the original C++ code for CEPHES (1989, moshier@na-net.ornl.gov)
 * and Wolfgang Hoschek (at CERN, hats off to you mate!) then adapted it for the 
 * Java platform, making some significant changes along the way. 
 * <br>
 *
 */
//public class Gamma extends cern.jet.math.Constants {
public class Gamma implements PapayaConstants {
	/**
	 * Makes this class non instantiable, but still let's others inherit from it.
	 */
	protected Gamma() {}
	/**
	 * Returns the beta function of the arguments.
	 * <pre>
	 * 
	 * beta( a, b )  = &Gamma;(a) * &Gamma;(b) ) &Gamma;(a+b)
	 *
	 * </pre>
	 */
	static public float beta(double a, double b) throws ArithmeticException {
		double y;

		y = a + b;
		y = gamma(y);
		if( y == 0.0 ) return 1.0f;

		if( a > b ) {
			y = gamma(a)/y;
			y *= gamma(b);
		}
		else {
			y = gamma(b)/y;
			y *= gamma(a);
		}

		return((float)y);
	}
	/**
	 * Returns the Gamma function of the argument, <code>&Gamma;(x)</code>
	 */
	static public double gamma(double x) throws ArithmeticException {

		double P[] = {
				1.60119522476751861407E-4,
				1.19135147006586384913E-3,
				1.04213797561761569935E-2,
				4.76367800457137231464E-2,
				2.07448227648435975150E-1,
				4.94214826801497100753E-1,
				9.99999999999999996796E-1
		};
		double Q[] = {
				-2.31581873324120129819E-5,
				5.39605580493303397842E-4,
				-4.45641913851797240494E-3,
				1.18139785222060435552E-2,
				3.58236398605498653373E-2,
				-2.34591795718243348568E-1,
				7.14304917030273074085E-2,
				1.00000000000000000320E0
		};
		//double MAXGAM = 171.624376956302725;
		//double LOGPI  = 1.14472988584940017414;

		double p, z;
		int i;

		double q = Math.abs(x);

		if( q > 33.0 ) {
			if( x < 0.0 ) {
				p = Math.floor(q);
				if( p == q ) throw new ArithmeticException("gamma: overflow");
				i = (int)p;
				z = q - p;
				if( z > 0.5 ) {
					p += 1.0;
					z = q - p;
				}
				z = q * Math.sin( Math.PI * z );
				if( z == 0.0 ) throw new ArithmeticException("gamma: overflow");
				z = Math.abs(z);
				z = Math.PI/(z * stirlingFormula(q) );

				return -z;
			} else {
				return stirlingFormula(x);
			}
		}

		z = 1.0;
		while( x >= 3.0 ) {
			x -= 1.0;
			z *= x;
		}

		while( x < 0.0 ) {
			if( x == 0.0 ) {
				throw new ArithmeticException("gamma: singular");
			} else
				if( x > -1.E-9 ) {
					return( z/((1.0 + 0.5772156649015329 * x) * x) );
				}
			z /= x;
			x += 1.0;
		}

		while( x < 2.0 ) {
			if( x == 0.0 ) {
				throw new ArithmeticException("gamma: singular");
			} else
				if( x < 1.e-9 ) {
					return( z/((1.0 + 0.5772156649015329 * x) * x) );
				}
			z /= x;
			x += 1.0;
		}

		if( (x == 2.0) || (x == 3.0) ) 	return z;

		x -= 2.0;
		p = Polynomial.polyval( x, P );
		q = Polynomial.polyval( x, Q );
		return  z * p / q;

	}
	/**
	 * Returns the Incomplete Beta Function evaluated from zero to <code>xx</code>; formerly named <code>ibeta</code>.
	 *
	 * @param aa the alpha parameter of the beta distribution.
	 * @param bb the beta parameter of the beta distribution.
	 * @param xx the integration end point.
	 */
	public static double incompleteBeta( double aa, double bb, double xx ) throws ArithmeticException {
		double a, b, t, x, xc, w, y;
		boolean flag;

		if( aa <= 0.0 || bb <= 0.0 ) throw new 
		ArithmeticException("incompleteBeta: Domain error!");

		if( (xx <= 0.0) || ( xx >= 1.0) ) {
			if( xx == 0.0 ) return 0.0;
			if( xx == 1.0 ) return 1.0;
			throw new ArithmeticException("incompleteBeta: Domain error!");
		}

		flag = false;
		if( (bb * xx) <= 1.0 && xx <= 0.95) {
			t = powerSeries(aa, bb, xx);
			return t;
		}

		w = 1.0 - xx;

		/* Reverse a and b if x is greater than the mean. */
		if( xx > (aa/(aa+bb)) ) {
			flag = true;
			a = bb;
			b = aa;
			xc = xx;
			x = w;
		} else {
			a = aa;
			b = bb;
			xc = w;
			x = xx;
		}

		if( flag  && (b * x) <= 1.0 && x <= 0.95) {
			t = powerSeries(a, b, x);
			if( t <= MACHEP ) 	t = 1.0 - MACHEP;
			else  		        t = 1.0 - t;
			return t;
		}

		/* Choose expansion for better convergence. */
		y = x * (a+b-2.0) - (a-1.0);
		if( y < 0.0 )
			w = incompleteBetaFraction1( a, b, x );
		else
			w = incompleteBetaFraction2( a, b, x ) / xc;

		/* Multiply w by the factor                    
		  x^a  (1-x)^b   &Gamma;(a+b) / ( a *&Gamma;(a) *&Gamma;(b) ) .   */

		y = a * Math.log(x);
		t = b * Math.log(xc);
		if( (a+b) < MAXGAM && Math.abs(y) < MAXLOG && Math.abs(t) < MAXLOG ) {
			t = Math.pow(xc,b);
			t *= Math.pow(x,a);
			t /= a;
			t *= w;
			t *= gamma(a+b) / (gamma(a) * gamma(b));
			if( flag ) {
				if( t <= MACHEP ) 	t = 1.0 - MACHEP;
				else  		        t = 1.0 - t;
			}
			return t;
		}
		/* Resort to logarithms.  */
		y += t + logGamma(a+b) - logGamma(a) - logGamma(b);
		y += Math.log(w/a);
		if( y < MINLOG )
			t = 0.0;
		else
			t = Math.exp(y);

		if( flag ) {
			if( t <= MACHEP ) 	t = 1.0 - MACHEP;
			else  		        t = 1.0 - t;
		}
		return t;
	}   

	/** 
	 * Returns the inverse of the incomplete Beta integral.
	 * That is, given <code>y</code>, the function finds <code>x</code> such that
	 *	<pre>
	 *  incompleteBeta( a, b, x ) = y.
	 *</pre>
	 * The routine performs interval halving until <code>incompleteBeta(a,b,x) - y = 0</code>,
	 * to within roughly <code>10^-13</code> of the true solution. If this precision is not reached, the
	 * method returns the current approximation of x, and prints a warning statement
	 * specifying the current error. Typically, this is on the order of 10^-12. 
	 * 
	 * @param aa the alpha parameter of the beta distribution.
	 * @param bb the beta parameter of the beta distribution.
	 * @param yy0 the value for which to solve for the corresponding <code>x</code> in the 
	 * incomplete Beta integral.
	 * @return the value <code>x</code> such that <code>incompleteBeta( a, b, x ) = y</code>.
	 */
	public static double incompleteBetaInverse(double aa,double bb,double yy0 )
	{
		double a=0, b=0, y0=0, d, y=0, x=0, x0, x1, lgm=0, yp, di=0, dithresh=0, yl, yh, xt;
		int i=0, rflg=0, dir=0, nflg;

		//ap_error::make_assertion(y>=0&&y<=1);
		if( yy0 <= 0 )
			return 0.0;
		if( yy0 >= 1.0 )
			return 1.0;
		x0 = 0.0;
		yl = 0.0; // ylow
		x1 = 1.0;
		yh = 1.0; // yhigh

		int count = 0;
		double precision = 1e-8;

		/* Make an initial approximation for the inverse function */
		
		if( aa <= 1.0 || bb <= 1.0 )
		{
			dithresh = 1.0e-6;
			rflg = 0;
			a = aa;
			b = bb;
			y0 = yy0;
			x = a/(a+b); // approximate x using the mean value
			y = incompleteBeta( a, b, x );
		}
		else{
			a=aa;
			b=bb;
			y0 = yy0;
			
			yp = -Probability.norminv(yy0);
			if( yy0 > 0.5 )
			{
				yp = -yp; // since the normal distribution is symmetric
			}
			// log gamma
			lgm = (yp * yp - 3.0)/6.0;
			x = 2.0/( 1.0/(2.0*a-1.0)  +  1.0/(2.0*b-1.0) );

			d = yp * Math.sqrt( x + lgm ) / x
					- ( 1.0/(2.0*b-1.0) - 1.0/(2.0*a-1.0) )
					* (lgm + 5.0/6.0 - 2.0/(3.0*x));
			d = 2.0 * d;
			if( d < MINLOG )
			{
				x = 0.0;
				return x;
			}
			x = a/( a + b * Math.exp(d) );
			y = incompleteBeta( a, b, x );
		}

		/* This only works because the incompleteBeta function is an increasing function */
		while (Math.abs(y-y0) > precision && count<1000)
		{	
			// if y>y0, we're too far right so move x1 to x
			if (y > y0)
			{
				x1 = x;
			}
			else // y<=y0 we're too far left so move x0 to the right.
			{
				x0 = x;
			}
			x = (x1 + x0) / 2;
			y = incompleteBeta( a, b, x );
			count++; // so we don't end up in a never-ending loop. 
		}
		if(count>=1000){
			System.out.println("Warning: incompleteBetaInverse: Could not solve to specified precision."+
							"Returning closest match ( error = " + Math.abs(y-y0)+" )");
		}
		return x;
	}

	/**
	 * Continued fraction expansion #1 for incomplete beta integral; formerly named <code>incbcf</code>.
	 */
	static double incompleteBetaFraction1( double a, double b, double x ) throws ArithmeticException {
		double xk, pk, pkm1, pkm2, qk, qkm1, qkm2;
		double k1, k2, k3, k4, k5, k6, k7, k8;
		double r, t, ans, thresh;
		int n;

		k1 = a;
		k2 = a + b;
		k3 = a;
		k4 = a + 1.0;
		k5 = 1.0;
		k6 = b - 1.0;
		k7 = k4;
		k8 = a + 2.0;

		pkm2 = 0.0;
		qkm2 = 1.0;
		pkm1 = 1.0;
		qkm1 = 1.0;
		ans = 1.0;
		r = 1.0;
		n = 0;
		thresh = 3.0 * MACHEP;
		do {
			xk = -( x * k1 * k2 )/( k3 * k4 );
			pk = pkm1 +  pkm2 * xk;
			qk = qkm1 +  qkm2 * xk;
			pkm2 = pkm1;
			pkm1 = pk;
			qkm2 = qkm1;
			qkm1 = qk;

			xk = ( x * k5 * k6 )/( k7 * k8 );
			pk = pkm1 +  pkm2 * xk;
			qk = qkm1 +  qkm2 * xk;
			pkm2 = pkm1;
			pkm1 = pk;
			qkm2 = qkm1;
			qkm1 = qk;

			if( qk != 0 )		r = pk/qk;
			if( r != 0 ) {
				t = Math.abs( (ans - r)/r );
				ans = r;
			}	else
				t = 1.0;

			if( t < thresh ) return ans;

			k1 += 1.0;
			k2 += 1.0;
			k3 += 2.0;
			k4 += 2.0;
			k5 += 1.0;
			k6 -= 1.0;
			k7 += 2.0;
			k8 += 2.0;

			if( (Math.abs(qk) + Math.abs(pk)) > big ) {
				pkm2 *= biginv;
				pkm1 *= biginv;
				qkm2 *= biginv;
				qkm1 *= biginv;
			}
			if( (Math.abs(qk) < biginv) || (Math.abs(pk) < biginv) ) {
				pkm2 *= big;
				pkm1 *= big;
				qkm2 *= big;
				qkm1 *= big;
			}
		} while( ++n < 300 );

		return ans;
	}   
	/**
	 * Continued fraction expansion #2 for incomplete beta integral; formerly named <code>incbd</code>.
	 */
	static double incompleteBetaFraction2( double a, double b, double x ) throws ArithmeticException {
		double xk, pk, pkm1, pkm2, qk, qkm1, qkm2;
		double k1, k2, k3, k4, k5, k6, k7, k8;
		double r, t, ans, z, thresh;
		int n;

		k1 = a;
		k2 = b - 1.0;
		k3 = a;
		k4 = a + 1.0;
		k5 = 1.0;
		k6 = a + b;
		k7 = a + 1.0;;
		k8 = a + 2.0;

		pkm2 = 0.0;
		qkm2 = 1.0;
		pkm1 = 1.0;
		qkm1 = 1.0;
		z = x / (1.0-x);
		ans = 1.0;
		r = 1.0;
		n = 0;
		thresh = 3.0 * MACHEP;
		do {
			xk = -( z * k1 * k2 )/( k3 * k4 );
			pk = pkm1 +  pkm2 * xk;
			qk = qkm1 +  qkm2 * xk;
			pkm2 = pkm1;
			pkm1 = pk;
			qkm2 = qkm1;
			qkm1 = qk;

			xk = ( z * k5 * k6 )/( k7 * k8 );
			pk = pkm1 +  pkm2 * xk;
			qk = qkm1 +  qkm2 * xk;
			pkm2 = pkm1;
			pkm1 = pk;
			qkm2 = qkm1;
			qkm1 = qk;

			if( qk != 0 )  r = pk/qk;
			if( r != 0 ) {
				t = Math.abs( (ans - r)/r );
				ans = r;
			} else
				t = 1.0;

			if( t < thresh ) return ans;

			k1 += 1.0;
			k2 -= 1.0;
			k3 += 2.0;
			k4 += 2.0;
			k5 += 1.0;
			k6 += 1.0;
			k7 += 2.0;
			k8 += 2.0;

			if( (Math.abs(qk) + Math.abs(pk)) > big ) {
				pkm2 *= biginv;
				pkm1 *= biginv;
				qkm2 *= biginv;
				qkm1 *= biginv;
			}
			if( (Math.abs(qk) < biginv) || (Math.abs(pk) < biginv) ) {
				pkm2 *= big;
				pkm1 *= big;
				qkm2 *= big;
				qkm1 *= big;
			}
		} while( ++n < 300 );

		return ans;
	}
	/**
	 * Returns the Incomplete Gamma function; formerly named <code>igamma</code>.
	 * @param a the parameter of the gamma distribution.
	 * @param x the integration end point.
	 */
	static public double incompleteGamma(double a, double x) 
			throws ArithmeticException {


		double ans, ax, c, r;

		if( x <= 0 || a <= 0 ) return 0.0;
		
		if( x > 1.0 && x > a ) return 1.0 - incompleteGammaComplement(a,x);

		/* Compute  x**a * exp(-x) / gamma(a)  */
		ax = a * Math.log(x) - x - logGamma(a);
		if( ax < -MAXLOG ) return( 0.0 );

		ax = Math.exp(ax);

		/* power series */
		r = a;
		c = 1.0;
		ans = 1.0;

		do {
			r += 1.0;
			c *= x/r;
			ans += c;
		}
		while( c/ans > MACHEP );

		return( ans * ax/a );

	}
	/**
	 * Returns the Complemented Incomplete Gamma function; formerly named <tt>igamc</tt>.
	 * @param a the parameter of the gamma distribution.
	 * @param x the integration start point.
	 */
	static public double incompleteGammaComplement( double a, double x ) throws ArithmeticException {
		double ans, ax, c, yc, r, t, y, z;
		double pk, pkm1, pkm2, qk, qkm1, qkm2;

		if( x <= 0 || a <= 0 ) return 1.0;

		if( x < 1.0 || x < a ) return 1.0 - incompleteGamma(a,x);

		ax = a * Math.log(x) - x - logGamma(a);
		if( ax < -MAXLOG ) return 0.0;

		ax = Math.exp(ax);

		/* continued fraction */
		y = 1.0 - a;
		z = x + y + 1.0;
		c = 0.0;
		pkm2 = 1.0;
		qkm2 = x;
		pkm1 = x + 1.0;
		qkm1 = z * x;
		ans = pkm1/qkm1;

		do {
			c += 1.0;
			y += 1.0;
			z += 2.0;
			yc = y * c;
			pk = pkm1 * z  -  pkm2 * yc;
			qk = qkm1 * z  -  qkm2 * yc;
			if( qk != 0 ) {
				r = pk/qk;
				t = Math.abs( (ans - r)/r );
				ans = r;
			} else
				t = 1.0;

			pkm2 = pkm1;
			pkm1 = pk;
			qkm2 = qkm1;
			qkm1 = qk;
			if( Math.abs(pk) > big ) {
				pkm2 *= biginv;
				pkm1 *= biginv;
				qkm2 *= biginv;
				qkm1 *= biginv;
			}
		} while( t > MACHEP );

		return ans * ax;
	}
	/**
	 * Returns the natural logarithm of the gamma function; formerly named <code>lgamma</code>.
	 */
	public static double logGamma(double x) throws ArithmeticException {
		double p, q, w, z;

		double A[] = {
				8.11614167470508450300E-4,
				-5.95061904284301438324E-4,
				7.93650340457716943945E-4,
				-2.77777777730099687205E-3,
				8.33333333333331927722E-2
		};
		double B[] = {
				-1.37825152569120859100E3,
				-3.88016315134637840924E4,
				-3.31612992738871184744E5,
				-1.16237097492762307383E6,
				-1.72173700820839662146E6,
				-8.53555664245765465627E5
		};
		double C[] = {
				1.00000000000000000000E0,
				-3.51815701436523470549E2,
				-1.70642106651881159223E4,
				-2.20528590553854454839E5,
				-1.13933444367982507207E6,
				-2.53252307177582951285E6,
				-2.01889141433532773231E6
		};

		if( x < -34.0 ) {
			q = -x;
			w = logGamma(q);
			p = Math.floor(q);
			if( p == q ) throw new ArithmeticException("logGamma: Overflow");
			z = q - p;
			if( z > 0.5 ) {
				p += 1.0;
				z = p - q;
			}
			z = q * Math.sin( Math.PI * z );
			if( z == 0.0 ) throw new 
			ArithmeticException("logGamma: Overflow");
			z = LOGPI - Math.log( z ) - w;
			return z;
		}

		if( x < 13.0 ) {
			z = 1.0;
			while( x >= 3.0 ) {
				x -= 1.0;
				z *= x;
			}
			while( x < 2.0 ) {
				if( x == 0.0 ) throw new 
				ArithmeticException("logGamma: Overflow");
				z /= x;
				x += 1.0;
			}
			if( z < 0.0 ) z = -z;
			if( x == 2.0 ) return Math.log(z);
			x -= 2.0;
			p = x * Polynomial.polyval( x, B) / Polynomial.polyval( x, C);
			return( Math.log(z) + p );
		}

		if( x > 2.556348e305 ) throw new 
		ArithmeticException("logGamma: Overflow");

		q = ( x - 0.5 ) * Math.log(x) - x + 0.91893853320467274178;
		//if( x > 1.0e8 ) return( q );
		if( x > 1.0e8 ) return( q );

		p = 1.0/(x*x);
		if( x >= 1000.0 )
			q += ((   7.9365079365079365079365e-4 * p
					- 2.7777777777777777777778e-3) *p
					+ 0.0833333333333333333333) / x;
		else
			q += Polynomial.polyval( p, A ) / x;
		return q;
	}
	/**
	 * Power series for incomplete beta integral; formerly named <tt>pseries</tt>.
	 * Use when b*x is small and x not too close to 1.  
	 */
	static double powerSeries( double a, double b, double x ) throws ArithmeticException {
		double s, t, u, v, n, t1, z, ai;

		ai = 1.0 / a;
		u = (1.0 - b) * x;
		v = u / (a + 1.0);
		t1 = v;
		t = u;
		n = 2.0;
		s = 0.0;
		z = MACHEP * ai;
		while( Math.abs(v) > z ) {
			u = (n - b) * x / n;
			t *= u;
			v = t / (a + n);
			s += v; 
			n += 1.0;
		}
		s += t1;
		s += ai;

		u = a * Math.log(x);
		if( (a+b) < MAXGAM && Math.abs(u) < MAXLOG ) {
			t = Gamma.gamma(a+b)/(Gamma.gamma(a)*Gamma.gamma(b));
			s = s * t * Math.pow(x,a);
		} else {
			t = Gamma.logGamma(a+b) - Gamma.logGamma(a) - Gamma.logGamma(b) + u + Math.log(s);
			if( t < MINLOG ) 	s = 0.0;
			else  	            s = Math.exp(t);
		}
		return s;
	}
	/**
	 * Returns the Gamma function computed by Stirling's formula; formerly named <tt>stirf</tt>.
	 * The polynomial STIR is valid for 33 <= x <= 172.
	 */
	static double stirlingFormula(double x) throws ArithmeticException {
		double STIR[] = {
				7.87311395793093628397E-4,
				-2.29549961613378126380E-4,
				-2.68132617805781232825E-3,
				3.47222221605458667310E-3,
				8.33333333333482257126E-2,
		};
		double MAXSTIR = 143.01608;

		double w = 1.0/x;
		double  y = Math.exp(x);

		w = 1.0 + w * Polynomial.polyval( w, STIR);

		if( x > MAXSTIR ) {
			/* Avoid overflow in Math.pow() */
			double v = Math.pow( x, 0.5 * x - 0.25 );
			y = v * (v / y);
		} else {
			y = Math.pow( x, x - 0.5 ) / y;
		}
		y = SQTPI * y * w;
		return y;
	}
}
