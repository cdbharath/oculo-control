/*
 Example showing how to use the Unique class
 The dataset consists of Garbage collected from a number of different households.
 To load the file into R, use
 garbage<-read.csv("/Users/adilapapaya/Desktop/Downloads/TrioloDatasets/GARBAGE.csv",header=TRUE,sep=",")
 Adila Faruk, 2012.
 */

// import the library
import papaya.*;

// font
PFont titleFont = createFont("Helvetica", 15, true);
PFont smallFont = createFont("Helvetica", 12, true);
PFont tinyFont = createFont("Helvetica", 9, true);

String[] columnTitles; 
int numColumns, numObs; // number of columns, and number of observations for each column
float[][] data; // data by household size
float[][] indivTrash; // data normalized by the household size.
float[][] pctTotal; // individual trash production, as a % of total individual trash.
float[] maxVal;
Unique u; //Initialize the Unique class

void setup() {
  size(500, (int)yTop+(int)yHeight+60);
  noLoop();
  parseData("GARBAGE.csv");    
  // Get the unique (or distinct) household sizes and their frequencies.
  // Since we want to store the indices, set the "storeIndices" to true.
  u = new Unique(Mat.column(data,0),true);
}


// Drawing --------------------------------------------------
int currentTrash = 1; // the type of trash to visualize
float xLeft = 40, yTop = 90, xWidth = 420, yHeight = 560;
color low =color(50,80,250,150); color high =color(250,50,100,200); 


void draw(){
  background(255);
  // Create a visuals class for convenient x and y labeling, tick marks, etc
  Visuals v = new Visuals(this,xLeft,yTop,xWidth,yHeight); 
  v.drawRect();
  int uSize = u.values.size();
  float xPos;
  noStroke(); smooth(); 
  float cutoff = (maxVal[currentTrash]*.75); // cutoff for whether to highlight the value or not.
  
  for(int i=0; i<uSize; i++){   
    xPos = 50*(i+1); 
    for(int j=0; j< u.frequencies.get(i); j++){
      // access the values of the individual matrices by household
      // size by using the stored indices. 
      int idx = u.idx[i][j]; // idx corresponding to a household of size values.get(i).
      float value = indivTrash[idx][currentTrash]; // the numeric value
      float pct = pctTotal[idx][currentTrash]; // the value expressed as a pct of the total trash produced by the individuel
      pct = map(pct,0,.5,yTop+yHeight,yTop);
      if (value > cutoff){
        fill(50); textFont(tinyFont); textAlign(LEFT,CENTER);
        text(nfc(value,1)+" kg", xPos + 3, pct);
        fill(high); 
      }
      else {
        fill(low);
      }
      ellipse(xPos,pct,4,4);
    }
  }
  
  // Labeling. Messy. Feel free to ignore.
  String[] xLabels = new String[uSize];
  for(int i=0; i<uSize; i++){
    xLabels[i] = str(u.values.get(i));
  }
  v.xLabels(xLeft+10, xWidth-20,xLabels );
  v.yLabels(yTop+5,yHeight-10, 0, 50, 10);
  
  textFont(titleFont); fill(100); smooth();
  textAlign(LEFT,TOP);
  text("Is Individual Trash Production* Influenced by Household Size?", xLeft ,20);
  
  String category = "Category: "+ columnTitles[currentTrash+1];
  textAlign(CENTER,BOTTOM); textFont(titleFont); fill(50,80,200);
  text( "Category: "+ columnTitles[currentTrash+1],xLeft+xWidth/2,yTop-5); 
  
  textFont(tinyFont); textAlign(LEFT,TOP); fill(100);
  String explanation = "*Indiv. trash production computed as (household trash) / (household size)."+
                       "\n Pct.Total expressed as a pct. of the total trash produced by an indiv."+
                       "\n in a household of specified size."+
                       "\n Trash amounts within 75% of the maximum value of "+nfc(maxVal[currentTrash],1)+" kgs are highlighted";
   text(explanation, xLeft+10,yTop+10);
  text("(PressUP/DOWN to change category.)", xLeft, 40);
  
  // x and y labels
  textAlign(CENTER,TOP);
  textFont(smallFont); 
  text("Household Size", xLeft + xWidth/2, yTop + yHeight+20);
  text("% Total", xLeft, yTop-15);
}

void keyPressed(){
  if(keyCode==UP){
    // exclude "TOTAL" since that goes waaaay out of the plot limits.
    if(currentTrash<numColumns-3){
      currentTrash++; 
      redraw();
    }
  }
  if(keyCode==DOWN){
    if(currentTrash>0){
      currentTrash--; 
      redraw();
    }
  }
}

// parses the input file 
public void parseData(String fileName){
  // HHSIZE  METAL  PAPER  PLAS GLASS  FOOD YARD, TEXT OTHER TOTAL
  String[] s= loadStrings(fileName);
  columnTitles = splitTokens(s[0], ",");
  numObs = s.length-1; 
  numColumns = columnTitles.length;
  data = new float[numObs][ numColumns];
  indivTrash = new float[numObs][ numColumns-1]; // excludes the hhsize column
  pctTotal = new float[numObs][numColumns-1]; // excludes the hhsize column
  // read in the rest of the data 
  for (int i=0; i<numObs; i++) {  
    String[] pieces = splitTokens(s[i+1], ",");
    data[i][0] = Float.parseFloat(pieces[0]); //HHSize
    data[i][numColumns-1] = Float.parseFloat(pieces[numColumns-1]); // total trash
    for (int j=1; j<numColumns-1; j++) {
      // the raw data
      data[i][j] = Float.parseFloat(pieces[j]);
      // the data normalized by the household size.
      indivTrash[i][j-1] = data[i][j]/data[i][0];
      pctTotal[i][j-1] = indivTrash[i][j-1] / data[i][numColumns-1];
    }
  }
  
  // store the max value of each column
  maxVal = new float[numColumns-1];
  for(int i=0; i<numColumns-1; i++){
    maxVal[i] = Descriptive.max(Mat.column(indivTrash,i));
  }
}

