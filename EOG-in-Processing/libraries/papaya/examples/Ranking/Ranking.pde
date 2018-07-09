/*
Getting the rank of the elements of an array using various strategies
for the tied values.

Adila Faruk, 2012.

 */
// import the library
import papaya.*;

// create a few different types of data points
int ln = 50;
// create a y matrix, consisting of 4 rows
float[] y = new float[ln]; 
float[] x = new float[ln];


for (int i=0; i<ln; i++) {
  x[i] = i+1;
  y[i] = round(random(-10, 10)); // so we'll get whole numbers
}

/*
Rank the elements of y[0] in ascending order.
*/
//Tied values are assigned the average of the applicable ranks
float[] rankTiesAveraged = Rank.rank(y, 0);
//Tied values are assigned the minimum applicable rank ( = the rank of the first appearance)
float[] rankTiesSetToMin = Rank.rank(y, 1);
//Tied values are assigned the maximum applicable rank ( = the rank of the last appearance)
float[] rankTiesSetToMax = Rank.rank(y, 2);
//Tied values are assigned ranks in order of occurrence in the original array
float[] rankTiesSetToSequential = Rank.rank(y, 3);

// print the results to the screen
println("\n\nRanks of the elements of y[0] (smaller elements are ranked lower):\n");
for(int i=0; i<ln; i++){
   println("y["+i+"] = " +y[i]);
   println("Rank method: Averages,   rank["+i+"] = " + rankTiesAveraged[i]);
   println("Rank method: Minimum,    rank["+i+"] = " + rankTiesSetToMin[i]);
   println("Rank method: Maximum,    rank["+i+"] = " + rankTiesSetToMax[i]);
   println("Rank method: Sequential, rank["+i+"] = " + rankTiesSetToSequential[i]+"\n");
}

