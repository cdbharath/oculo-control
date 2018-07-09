/*
Illustrative example showing how to sort multiple arrays while preserving the order
of the original arrays by using the Sorting.indices method.

Adila Faruk, 2012.

 */
 
// import the library
import papaya.*;

// create a few different types of data points
int ln = 50; 
float[] x = new float[ln];
float[] y = new float[ln];

for (int i=0; i<ln; i++) {
  x[i] = i+1;
  y[i] = round(random(-10, 10)); // so we'll get whole numbers
}
// yPoly = 1.2x^2 + 5
float[] yPoly =  Polynomial.polyval(x, new float[] {1.2, 0, 5});



/* 
Sort yPoly according to y sorted in ascending order.
To do this, we get the indices corresponding to the sorted y0
and apply them to the remaining arrays. 
*/
int[] indices = Sorting.indices(y,true); // true = sorted ascending, false = sorted descending.
// print the results to screen.
println("\n\ny and yPoly, in y's ascending order:\n");
for(int i=0; i<ln; i++){
   print("y[indices["+i+"]] = " +y[indices[i]]);
   print(", \t yPoly[indices["+i+"]] = " + yPoly[indices[i]]+"\n");
}





