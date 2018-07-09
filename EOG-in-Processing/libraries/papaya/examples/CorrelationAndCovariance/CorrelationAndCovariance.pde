/*
Example showing how to use some of the methods in the Correlation class.
 The dataset consists of Garbage collected from a number of different households.
 To load the file into R, use something similar to
 garbage<-read.csv("/Users/adilapapaya/Desktop/Downloads/TrioloDatasets/GARBAGE.csv",header=TRUE,sep=",")
 Adila Faruk, 2012.
 */

// import the library
import papaya.*;

String[] columnTitles; 
int numColumns, numObs; // number of columns, and number of observations for each column
float[][] data;// data by household size
float[][] indivTrash; // data normalized by the household size.
CorrelationPlot cPlot; //Initialize the correlation plot

void setup() {
  size(500, 500);
  noLoop();

  parseData("GARBAGE.csv");
  int numDecimals =4;  
  // get the covariance matrix and print the results to screen
  float[][] covMatrix = Correlation.cov(data, true);
  println("\n\nCovariance Matrix");
  Mat.print(covMatrix,columnTitles,columnTitles,numDecimals);

  // get the correlation matrix and print the results to screen
  float[][] corrMatrix = Correlation.linear(data, true);
  println("\n\nCorrelation Matrix");
  Mat.print(corrMatrix,columnTitles,columnTitles,numDecimals);
  
  // get the spearman correlation matrix
  float[][] spearmanCorrMatrix =Correlation.spearman(data, true);
  println("\n\nSpearman Correlation Matrix");
  Mat.print(spearmanCorrMatrix,columnTitles,columnTitles,numDecimals);
 
  
  /* For a weighted analysis, lets make the weight[i] = 1/householdsize[i].
  (i.e. look at things on an individual basis).*/
  float[] HHSize = Mat.column(data,0);
  float[] weights = Mat.inverse(HHSize,0); // if HHSize[i]==0, weights[i] = 0;
  
  // get the weighted covariance matrix, 
  float[][] weightedCovMatrix = Correlation.Weighted.cov(data, weights, true);
  println("\n\nWeighted Covariance Matrix");
  Mat.print(weightedCovMatrix,columnTitles,columnTitles,numDecimals);
 

  // get the weighted correlation matrix
  float[][] weightedCorrMatrix =Correlation.Weighted.corr(data, weights, true);
  println("\n\nWeighted Correlation Matrix");
  Mat.print(weightedCorrMatrix,columnTitles,columnTitles,numDecimals);
  println();
  
  noLoop();
}

// Drawing --------------------------------------------------
float xLeft = 40, yTop = 40, xWidth = 420, yHeight = 420;
color steelblue = 0xff1f77b4;
color orange = 0xffff7f0e;
color backgroundColor = color(250);
// font
PFont titleFont = createFont("Helvetica", 15, true);
PFont smallFont = createFont("Helvetica", 12, true);
PFont tinyFont = createFont("Helvetica", 9, true);

// Display all the data. The top left displays the raw data, the
// bottom right displays data normalized by household size.
// Interesting to see if the data distribution matches the correlation coefficients given in the 
// corr and weighted correlation matrices. 
void draw(){
  background(0);
  cPlot = new CorrelationPlot(this, xLeft,yTop,xWidth,yHeight);
  // draws the border & writes the labels. do this BEFORE drawing the data or you'll
  // cover it up.
  textFont(smallFont); // setting up the font prior to plotting.
  cPlot.drawBorderAndLabels(columnTitles, backgroundColor);  
  // Labeling --------------------------
  
  textFont(titleFont); fill(240); textAlign(RIGHT,BOTTOM);
  text("Household Trash", xLeft + xWidth, yTop -5);
  textAlign(LEFT,TOP); 
  text("Household Trash Normalized by Household Size",xLeft, yTop + yHeight + 5);
  
  // draw the correlation plot 
  // syntax: drawPlot(data, topRight(0) or bottomLeft(1), color of the dots, backgroundcolor; 
  // raw data
  cPlot.drawPlot(data,0,steelblue);
  // data normalized by household size
  cPlot.drawPlot(indivTrash,1,orange);  
  
  saveFrame("CorrelationMatrix.png");
}

// parses the input file and organize the data
public void parseData(String fileName){
  // HHSIZE  METAL  PAPER  PLAS GLASS  FOOD YARD, TEXT OTHER TOTAL
  String[] s= loadStrings(fileName);
  columnTitles = splitTokens(s[0], ",");
  numObs = s.length-1; 
  numColumns = columnTitles.length;
  data = new float[numObs][ numColumns];
  indivTrash = new float[numObs][ numColumns]; // excludes the hhsize column
  // read in the rest of the data 
  for (int i=0; i<numObs; i++) {  
    String[] pieces = splitTokens(s[i+1], ",");
    data[i][0] = Float.parseFloat(pieces[0]); //HHSize
    data[i][numColumns-1] = Float.parseFloat(pieces[numColumns-1]); // total trash
    indivTrash[i][0] = data[i][0];// don't normalize the HHSize
    for (int j=1; j<numColumns; j++) {
      // the raw data
      data[i][j] = Float.parseFloat(pieces[j]);
      // the data normalized by the household size.
      indivTrash[i][j] = data[i][j]/data[i][0];
    }
  }
}

