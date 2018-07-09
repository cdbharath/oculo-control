/*
Methods used here: 
- Polynomial
- Descriptive.frequencies for getting the distinct values,
- some Mat methods (sum, multiply)
- Find
- Cast
In some cases, the methods showcased here are suboptimal to other existing methods.
but are used anyway for illustrative purposes.

Adila Faruk, 2012.
 */
 
// import the library
import papaya.*;

// create a few different types of data points
int ln = 50;
// create a y matrix, consisting of 4 rows
float[] y0 = new float[ln]; 
float[] x = new float[ln];

float[] coeffs = new float[] {
  1.2, 0, 5
};
for (int i=0; i<ln; i++) {
  x[i] = i+1;
  y0[i] = round(random(-10, 10));
}
// y = 1.2x^2 + 5
float[] y1 = Polynomial.polyval(x, new float[] {1.2, 0, 5});

// sum the first and second rows
float[] y2 = Mat.sum(y0, y1);
// multiply x and y0
float[] y3 = Mat.multiply(x, y0);


/*
Get the distinct value in y0 and their corresponding frequencies.
This requires a sorted y0, hence the Mat.copyThenSort call.
Using sort directly will sort the original y0 which we don't want.

After getting the distinct values and their frequencies, we cast them
to float and int arrays. This is mainly to show how to use the Cast class
*/

ArrayList yDistinct = new ArrayList(); ArrayList frequencies = new ArrayList();
// call the function
Descriptive.frequencies(Mat.copyThenSort(y0),yDistinct,frequencies);
/*
Cast yDistinct and frequencies to float and int arrays
float[] yTemp = new float[yDistinct.size()]; yDistinct.toArray(yTemp); 
will give you a class cast exception since floats and ints are primitives.
*/

y3 = Cast.arrayListToFloat(yDistinct);
int[] freq = Cast.arrayListToInt(frequencies);
// print results to screen. set this to false here if you don't care to see this.
boolean printToScreen = true;
if(printToScreen){
  println("There are "+y3.length+" distinct values in y0. Their values and corresponding frequencies are:");
  for(int i=0; i<y3.length; i++){    
      println("Value: "+ y3[i]+", Frequency: "+freq[i]);
  }
}



/*
Find class: returns -1 if the element is not found
*/
// index of the first element in y0 containing 5.
int idxFirst = Find.indexOf(y0,5);
// index of the alst element in y0 containing 5
int idxLast = Find.lastIndexOf(y0,5);
// the number of times 5 is repeated in y0
int numRepeats = Find.numRepeats(y0,5);
// all the indices in y0 containing 5
int[] indices = Find.indicesWith(y0,5);

// print results to screen. set this to false here if you don't care to see this.
printToScreen = true;
if(printToScreen){
  println("\n\nFind class examples:\n\tFirst index of y0 containing 5: "+idxFirst);
  println("\tLast index of y0 containing 5: "+idxLast);
  println("\tNumber of times 5 is repeated: "+numRepeats);
  print("\tIndices of y0 containing 5: ");
  for(int i=0; i<indices.length-1; i++){
    print(indices[i]+", ");
  } print(indices[indices.length-1]);
}



