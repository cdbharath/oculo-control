/*
Shows how to use the various probability distribution functions in the
papaya.Probability class.
 */
 
import papaya.*;
// ------------------------------------------------------------------------
double x = 0.90; // corresponds to the point on the distribution curve.

// beta, betaComplemented and betaInverse
double beta_a = random(0,10), beta_b = random(0,10);  
/* Area from zero to x under the beta density function. */
double beta = Probability.betacdf(x,beta_a,beta_b);
/* Area from x to infinity under the beta density function. */
double betaComplemented = Probability.betacdfComplemented(x,beta_a,beta_b);
/* Find x such that Probability.beta(a, b, x) = y; y here corresponds to beta */
double betaInverse = Probability.betainv(beta,beta_a,beta_b);

/* Print the results to screen */
println("Beta probability distribution (x = " +(float)x+", a =" +(float)beta_a+" and b = "+(float)beta_b+")");
println("Probability.betacdf(x,a,b): "+(float)beta+", "+"Probability.betacdfComplemented(x,a,b): "+(float)betaComplemented);  
println("x, computed using the betainv function: "+(float)betaInverse);  


// ------------------------------------------------------------------------
// chi2cdf, chi2cdfComplemented. 
/* Note: Probability.chi2cdf is equivalent to the pchisq function in R*/

int df = floor(random(0,50));
/* Area from 0 to x of the Chi square probability density function with df degrees of freedom. */
double chiSquare = Probability.chi2cdf(x,df);
/* Area from x to infinity under the chi square probability density function with df degrees of freedom. */
double chiSquareComplemented = Probability.chi2cdfComplemented(x,df);

/* Print the results to screen */
println("\nChi square probability distribution (x = " +(float)x+", with "+df+" degrees of freedom)");
println("Probability.chiSquare(x,df): "+(float)chiSquare+", "+"Probability.chiSquareComplemented(x,df): "+(float)chiSquareComplemented);  
//println("x, computed using the betaInverse function: "+betaInverse);  

// ------------------------------------------------------------------------
// F, FComplemented, FInverse. 
/* Note: Probability.fcdf(x) is equivalent to pf in R
         Probability.finv(x) is equivalent to qf in R */
int df1 = floor(random(1,20)); int df2 = floor(random(1,20));
/* Area from 0 to x of the F probability density function with degrees of freedom df1 and df2 */
double FDistr = Probability.fcdf(x,10,5);
/* Area from x to infinity under the F probability density function with df degrees of freedom. */
double FComplemented = Probability.fcdfComplemented(x,df1, df2);
/* Find x such that Probability.F(df1, df2, x) = y; y here corresponds to FDistr */
double FInverse = Probability.finv(FComplemented,df1,df2);

/* Print the results to screen */
println("\nF probability distribution (x =" +(float)x+" df1 = "+df1+" and df2 = "+df2+")");
println("Probability.F(x,df1,df2): "+FDistr+", "+"Probability.FComplemented(x,df1,df2): "+(float)FComplemented);  
println("x, computed using the FInverse function: "+(float)FInverse);  


// ------------------------------------------------------------------------
// normcdf, norminv. 
/* Note: Probability.norm(x) is equivalent to pnorm in R
         Probability.norminv(x) is equivalent to qnorm in R */
/* Area from 0 to x of the *standard* normal probability density function (mean 0, variance 1)*/
double normalDistr = Probability.normcdf(x);
/* Find x such that Probability.normal(x) = y; */
double normalInverse = Probability.norminv(normalDistr);

/* Print the results to screen */
println("\nStandard Normal distribution (x = " +(float)x+")");
println("Probability.normcdf(x): "+(float)normalDistr);  
println("x, computed using the norminv function: "+(float)normalInverse); 

// ------------------------------------------------------------------------
// tcdf, tinv for the student-T distribution
/* Note: Probability.tcdf(x) is equivalent to pt in R
         Probability.tinv(x) is equivalent to qt in R */
/* Area from 0 to x of the *standard* normal probability density function (mean 0, variance 1)*/
double t = Probability.tcdf(x,df);
/* Find x such that Probability.normal(x) = y; */
double tInverse = Probability.tinv(t,df);

/* Print the results to screen */
println("\nstudent-T distribution (x = " +(float)x+", with "+df+" degrees of freedom)");
println("Probability.tcdf(x,df): "+(float)t);  
println("x, computed using the tinv function: "+(float)tInverse); 

if(keyPressed){
  redraw();
}
