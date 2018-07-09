/*
 Example showing how to use Anova
-------------------------------------------------------
File/Data: 3Stooges (Similar to SAS's three_stooges.sas)

study design:
 college freshmen were asked to specify their
 favorite member of the three stooges, they also
 completed two questionniares, one measuring the
 amount of brain damage and the other, the
 amount of stupidity. (This is (honest to God), an actual
 dataset I found online. Awesome, huh?) :)

 garbage<-read.csv("/Users/adilapapaya/Desktop/Downloads/TrioloDatasets/GARBAGE.csv",header=TRUE,sep=",")
 Adila Faruk, 2012.
 */

// import the library
import papaya.*;

String[] columnTitles; 
int numColumns, numObs; // number of columns, and number of observations for each column
String[] sNames = new String[]{"Moe","Larry","Curly"};
Stooge[] stooge = new Stooge[3];

void setup() {
  size(500, 500);
  noLoop();
  float[][] data = parseData("3Stooges.csv");   
  // create the tree stooge classes
  for(int i:new int[]{0,1,2}){ 
    stooge[i] = new Stooge(sNames[i], data, 25*i, 25);
  }
  
  // create an array list with each element corresponding to a float array
  ArrayList<float[]>BD = new ArrayList<float[]>(); // brain damage
  ArrayList<float[]>SI = new ArrayList<float[]>(); // stupidity index
  for(int i=0; i<3; i++){
    BD.add(stooge[i].bDamage);
    SI.add(stooge[i].sIndex);
  }
 // cast the ArrayList to the "Collection" interface. 
 OneWayAnova anovaBD = new OneWayAnova((Collection<float[]>)BD);
 OneWayAnova anovaSI = new OneWayAnova((Collection<float[]>)SI);
 println("p-value for testing the null hypothesis of no difference in brain damage: "+ anovaBD.pValue());
 println("p-value for testing the null hypothesis of no difference in stupidity index: "+ anovaSI.pValue());
}


// Drawing --------------------------------------------------
float xLeft = 40, yTop = 40, xWidth = 420, yHeight = 420;
color b =color(50,80,250,150); // blue
color g= color(50,200,100,200); // green
color r= color(200,100,100,200); // red
int[] stoogeColor = new int[]{r,g,b};
color backgroundColor = color(250);
// font
PFont titleFont = createFont("Helvetica", 15, true);
PFont smallFont = createFont("Helvetica", 12, true);

void draw(){
  background(255);
  ScatterPlot s = new ScatterPlot(this,xLeft,yTop,xWidth,yHeight);
  s.setDataExtremes(0,70,0,70);
  s.drawRectangle=false;
  smooth();
  for(int i=0; i<3; i++){
    s.drawScatterPlot(stooge[i].bDamage,stooge[i].sIndex,0,stoogeColor[i]);
  }
  textFont(smallFont); fill(100);
  text("Leaving the labeling in your hands...",50,60);
}

// create a "stooge" class to store the brain damage and stupidity index, by stooge.

public class Stooge{
  float[] bDamage, sIndex;
  String name;
  int ln; // number of observations in that group
  
  // get the data from idxStart(inclusive) to idxEnd
  public Stooge(String name, float[][] data, int iStart,int ln){
    this.ln = ln; 
    this.name = name;
    this.bDamage = new float[ln]; this.sIndex = new float[ln];
    for(int i=0; i<ln; i++){
      int idx = iStart+i; // get the index corresponding to that stooge 
      bDamage[i] = data[idx][0]; sIndex[i] = data[idx][1]; 
    } 
  }

}

// parses the input file and organize the data
public float[][] parseData(String fileName){
  String[] s= loadStrings(fileName);
  columnTitles = splitTokens(s[0], ",");
  numObs = s.length-1; 
  numColumns = columnTitles.length;
  float[][] data = new float[numObs][ numColumns-1];
  // read in the rest of the data 
  for (int i=0; i<numObs; i++) {  
    String[] pieces = splitTokens(s[i+1], ",");
    for (int j=1; j<numColumns; j++) {
      // the raw data
      data[i][j-1] = Float.parseFloat(pieces[j]);
    }
  }
  return data;
}

