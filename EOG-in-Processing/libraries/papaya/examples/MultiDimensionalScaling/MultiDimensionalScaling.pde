/*
 Example showing how to compute Distance matrices using the Distance
 method, and then apply them using the MDS class.
 The dataset consists of Bear Measurements (why not, right?).
 Adila Faruk, 2012.
 */

// import the library
import papaya.*;

String[] columnTitles; 
int numColumns, numObs, numM, numF; // number of columns, and number of observations for each column
float[][] data; float[] age;// bear data



void setup() {
  size(800, 630);
  noLoop();

  parseData("BEARS.csv");
  int numDecimals =4;  
  // Euclidian distance matrix. 
  float[][] euclidean = Distance.euclidean(data);
  println("\n\nFirst 10 elements of the Euclidian distance Matrix");  
  Mat.print(Mat.subMatrix(euclidean, 0, 9, 0, 9), numDecimals);

  // Standardized Euclidian distance matrix. 
  float[][] seuclidian = Distance.seuclidean(data);
  println("\n\nFirst 10 elements of the standardized Euclidian distance Matrix");  
  Mat.print(Mat.subMatrix(seuclidian, 0, 9, 0, 9), numDecimals);
  
  // Cityblock or Manhattan distance matrix
  float[][] cityblock =  Distance.cityblock(data);
  println("\n\nFirst 10 elements of the Cityblock distance Matrix");  
  Mat.print(Mat.subMatrix(cityblock, 0, 9, 0, 9), numDecimals);
  // other options: minkowski, mahalanobis, correlation, spearman, chebychev
  float[][] chebychev =  Distance.chebychev(data);
  float[][] correlation =  Distance.correlation(data);
  float[][] mahalanobis =  Distance.mahalanobis(data);
  float[][] minkowski =  Distance.minkowski(data,3); // cubic
  float[][] spearman =  Distance.spearman(data);
  println("\n\nFirst 10 elements of the Spearlman distance Matrix");  
  Mat.print(Mat.subMatrix(spearman, 0, 9, 0, 9), numDecimals);
   // use Mat.print to print the results out.
}

// Drawing --------------------------------------------------
float xLeft = 60, yTop = 90, xWidth = 700, yHeight = 630-140;
color r=color(200, 50, 80, 200); // green
color b =color(50, 80, 250, 150); // blue
color g=color(50, 200, 100, 200); // green
color backgroundColor = color(250);
// font
PFont titleFont = createFont("Helvetica", 15, true);
PFont smallFont = createFont("Helvetica", 12, true);
PFont tinyFont = createFont("Helvetica", 10, true);


void draw() {
  background(255);
  // draws a heat map of the data with darker colors indicating closer distances,
  HeatMap h = new HeatMap(this, xLeft, yTop, xWidth, yHeight);
  // use the euclidean distance matrix. You can also try out one of the other
  // distance matrices, but make sure to update the min/max values or the
  // colors won't come out right.
  float[][] distanceMat = Distance.euclidean(data);
  h.drawHeatMap(distanceMat,0f,Descriptive.max(distanceMat));
  // Those two bands you see correspond to male and female bands. The 
  // smaller grouping within them is because I've sorted the data by age
  // --> same-age bears are more similar.
  
  // NOTE:
  // You can also use the MDS.classical scaling method to find the x-y configuration
  // of points which capture most of the variation in the distances. 
  // Since the input distance matrix is euclidean,
  // the output coordinates are exactly the first p principal coordinate components. 
  // Set p = 2 to plot the results in a plane.
  /*
  float[][] cmdscale = MDS.classical(euclidean, 2,false); 
  x = new float[cmdscale.length]; y = new float[cmdscale.length];
  x = Mat.column(cmdscale,0); y = Mat.column(cmdscale,1);
  //and then plot x vs y //
  */
  
  // Labeling --------------------------
  textFont(titleFont); 
  fill(50,90,200); 
  textAlign(LEFT,BOTTOM); smooth();
  text("Euclidian Distance Metrics Between Bears." , xLeft, yTop-40);
  textAlign(LEFT, TOP); 
  textFont(smallFont);
  text("Darker colors indicate closer distances showing bears which are more 'similar'.\n"+
       "(Try sorting the csv file by something other than weight to see if you can find other patterns.)",
       xLeft,yTop-35);
}

// parses the input file and organize the data
public void parseData(String fileName) {
  // SEX AGE HEADLEN HEADWTH NECK LENGTH CHEST WEIGHT
  String[] s= loadStrings(fileName);
  columnTitles = splitTokens(s[0], ","); 
  columnTitles = subset(columnTitles, 1, columnTitles.length-1); // remove the column containing the sex
  numObs = s.length-1; 
  numM = 35; 
  numF = numObs - numM;
  numColumns = columnTitles.length;
  data = new float[numObs][numColumns];
  age = new float[numObs];
  // read in the rest of the data 
  for (int i=0; i<numObs; i++) {  
    String[] pieces = splitTokens(s[i+1], ",");
    age[i] = Float.parseFloat(pieces[1]);
    for (int j=0; j<numColumns; j++) {
      // the raw data
      data[i][j] = Float.parseFloat(pieces[j+1]);
    }
  }
  
  float[] means = Descriptive.Mean.columnMean(data);
  float[] stds = Descriptive.std(data,false); // division by n
  data = Descriptive.zScore(data,means,stds);
}



