/*
 Example showing how to apply some of the matrix decomposition methods. 
 The dataset consists of Bear Measurements (why not, right?).
 the matrix analyzed is the covariance matrix which is both square and symmetric.
 Feel free to switch things up with a non-square matrix, or an asymmetric one.
 
 To load the file into R, use something along the lines of
 garbage<-read.csv("/Users/adilapapaya/Desktop/Downloads/TrioloDatasets/BEARS.csv",header=TRUE,sep=",")
 Adila Faruk, 2012.
 */

// import the library
import papaya.*;

String[] columnTitles; 
int numColumns, numObs, numM, numF; // number of columns, and number of observations for each column
float[][] data, dataM, dataF;// bear data
CorrelationPlot cPlot; //Initialize the correlation plot

void setup() {
  size(500, 500);
  noLoop();

  parseData("BEARS.csv");
  int numDecimals =4;  
  // get the covariance matrix. we'll use this as the core matrix
  // in the matrix decompositions methods to follow.
  float[][] covMatrix = Correlation.cov(data, true);
  println("\n\nCovariance Matrix");
  Mat.print(covMatrix,columnTitles,columnTitles,numDecimals);

  // now the fun starts. :)
  
  // 1. Eigenvalues ---------------------------------------------------
  //-------------------------------------------------------------------
  Eigenvalue eigen = new Eigenvalue(covMatrix);
  // cast the eigenvector matrix to a  float array for printing to screen
  float[][] V = Cast.doubleToFloat(eigen.getV());
  println("\nEigenvector Matrix");
  Mat.print(V,numDecimals);
  float[][] D = Cast.doubleToFloat(eigen.getD());
  println("\nBlock Diagonal Eigenvalue Matrix.");
  Mat.print(D,numDecimals);
  // check if covMatrix*v = lambda*v, where v is an eigenvector.
  float[] v = Mat.column(V,0); float lambda = D[0][0];
  float[] cv = Mat.multiply(covMatrix,v); float[] lambdav = Mat.multiply(v,lambda);
  println("\nCovarianceMatrix * v - Lambda * v");
  Mat.print(Mat.subtract(cv,lambdav),4); 
  
  // 2. SVD Decomposition ---------------------------------------------
  //-------------------------------------------------------------------
  SVD svd = new SVD(covMatrix);
  // covMatrix = U * S * transpose(V)
  int rank = svd.rank();
  float cond = (float)svd.cond();
  println("\nSVD Decomposition:\nRank is "+rank+", Condition Number is "+cond);
  float[][] U = Cast.doubleToFloat(svd.getU());
  println("\nU");
  Mat.print(U,numDecimals);
  float[][] S = Cast.doubleToFloat(svd.getS());
  println("\nS");
  Mat.print(S,numDecimals);
  float[][] VTrans = Mat.transpose( Cast.doubleToFloat(svd.getV()) );
  println("\nTranspose(V)");
  Mat.print(VTrans,numDecimals);
  println("\nNotice that, since the input matrix is symmetric, U = V via the Spectral Decomposition Theorem. :)");
  
  // 3. LU Decomposition ---------------------------------------------
  //-------------------------------------------------------------------
  LU lu = new LU(covMatrix);
  // covMatrix * pivotArray = L*U
  float det = (float)lu.det();
  println("\nLU Decomposition:\nDeterminant is "+det +" (=the product of singular values or eigenvalues)" );
  float[][] L = Cast.doubleToFloat(lu.getL());
  println("\nL");
  Mat.print(L,numDecimals);
  U = Cast.doubleToFloat(lu.getU());
  println("\nU");
  Mat.print(U,numDecimals);
  
  // 4. QR Decomposition ---------------------------------------------
  //------------------------------------------------------------------
  QR qr = new QR(covMatrix);
  // covMatrix = Q*R
  println("\nQR Decomposition");
  float[][] Q = Cast.doubleToFloat(qr.getQ());
  println("\nQ");
  Mat.print(Q,numDecimals);
  float[][] R = Cast.doubleToFloat(qr.getR());
  println("\nR");
  Mat.print(R,numDecimals);
  
  // Solving a system of linear equations --------------------
  // Using QR: Least squares solution of A*X = B 
  // where A and B both have the same number of rows 
  
  float[][] A = new float[][]{{1,5,9},{2,4,6},{1,6,3}}; 
  float[][] B = new float[][]{{2,3,-1},{9,3,1},{3,2,8}};
  //float[] B = new float[]{2,9,-1}; // if you want to try the case where B is an array
  float[][] X = Cast.doubleToFloat( new QR(A).solve(B) );
  println("\nQR Least squares solution X, to A*X = B");
  Mat.print(X,numDecimals); 
  //Mat.print(Mat.multiply(A,X),numDecimals); // should equal B
  
  
  // Using LU: Best for solving square matrices so we take just the upper 2x2 part of x
  X = Cast.doubleToFloat( new LU(A).solve(B) );
  println("\nLU solution X, to A*X = B");
  Mat.print(X,numDecimals);
  //Mat.print(Mat.multiply(A,X),numDecimals); // should equal B
}

// Drawing --------------------------------------------------
float xLeft = 40, yTop = 40, xWidth = 420, yHeight = 420;
color b =color(50,80,250,150); // blue
color g=color(50,200,100,200); // green
color backgroundColor = color(250);
// font
PFont titleFont = createFont("Helvetica", 15, true);
PFont smallFont = createFont("Helvetica", 12, true);
PFont tinyFont = createFont("Helvetica", 9, true);
String[] sex = {"Female Bears","Male Bears"};
// Display all the data. The top left displays the raw data, the
// bottom right displays data normalized by household size.
// Interesting to see if the data distribution matches the correlation coefficients given in the 
// corr and weighted correlation matrices. 
void draw(){
  background(255);
  cPlot = new CorrelationPlot(this, xLeft,yTop,xWidth,yHeight);
  // draws the border & writes the labels. do this BEFORE drawing the data or you'll
  // cover it up.
  textFont(smallFont); fill(240);// setting up the font prior to plotting.
  cPlot.drawBorderAndLabels(columnTitles, backgroundColor);  
  // Labeling --------------------------
  
  textFont(titleFont); fill(100); textAlign(RIGHT,BOTTOM);
  text("All Bears", xLeft + xWidth, yTop -5);
  textAlign(LEFT,TOP); 
  text(sex[sexIndex%2],xLeft, yTop + yHeight + 5);
  
  // draw the correlation plot 
  // syntax: drawPlot(data, topRight(0) or bottomLeft(1), color of the dots, backgroundcolor; 
  // raw data
  cPlot.drawPlot(data,0,b);
  // data normalized by household size
  if(sexIndex%2==0) cPlot.drawPlot(dataF,1,g);  
  else cPlot.drawPlot(dataM,1,g); 
}

// parses the input file and organize the data

public void parseData(String fileName){
  // SEX AGE HEADLEN HEADWTH NECK LENGTH	CHEST	WEIGHT
  String[] s= loadStrings(fileName);
  columnTitles = splitTokens(s[0], ","); 
  columnTitles = subset(columnTitles,1,columnTitles.length-1); // remove the column containing the sex
  numObs = s.length-1; 
  numM = 35; numF = numObs - numM;
  numColumns = columnTitles.length;
  data = new float[numObs][numColumns];
  dataM = new float[numM][numColumns];
  dataF = new float[numF][numColumns];
//  indivTrash = new float[numObs][ numColumns]; // excludes the hhsize column
  // read in the rest of the data 
  for (int i=0; i<numObs; i++) {  
    String[] pieces = splitTokens(s[i+1], ",");
    for (int j=1; j<=numColumns; j++) {
      // the raw data
      data[i][j-1] = Float.parseFloat(pieces[j]);
      // I've cheated a little and checked the input file: first 35 are males, 36 through 54 are females
      if(i<numM) dataM[i][j-1] = data[i][j-1];
      else dataF[i-numM][j-1] = data[i][j-1];
    }
  }
}

int sexIndex = 0;
void keyPressed(){
  if(keyCode==TAB){
    sexIndex++;
    redraw();
  }
}

