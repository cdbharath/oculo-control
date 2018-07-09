import papaya.*;

/* Using the Comparisons class for checking  the "freshman-15".
Here, we focus on just the MannWhitney method, but the
signTest and TTest can be used as well.
See also the OneWayAnova class for one-way anovas.
 */

// --------------------- Sketch-wide variables ----------------------
PFont titleFont, mediumFont, smallFont;

float[] BMI_April, BMI_Sept, WT_April, WT_Sept;
// create a "Sex" class that takes in the input csv file and organizes the data
// by sex
Sex M = new Sex(); 
Sex F = new Sex();
float[] pValue = new float[4];

// ------------------------ Initialisation --------------------------

// Initialise the data
void setup()
{
  size(800, 600);
  smooth();
  noLoop();

  titleFont = createFont("Helvetica", 20, true);
  mediumFont = createFont("Helvetica", 13, true);
  smallFont = createFont("Helvetica", 11, true);
  textFont(smallFont);

  //Read in the data
  String lines[] = loadStrings("FRESHMAN15.csv");
  int ln = lines.length-1; 
  // store everything in the M and F classes
  for (int i=0; i<ln; i++) {
    String[] pieces = splitTokens(lines[i+1], ",") ;
    if (pieces[0].equals("M") ) {     
      M.addElement(pieces);
    }
    else {
      F.addElement(pieces);
    }
  }

  /* Perform the Mann-Whitney test on male and female Weights and BMIs.
   before and after */
  M.MannWhitneyTest(); 
  F.MannWhitneyTest();
  // get the MannWhitney pvalues.
  pValue = new float[]{F.Wt_p,M.Wt_p,F.BMI_p,M.BMI_p};
  
  
  // Print everything out in the console to see what's going on with these here freshmen...
  println("Females:\np-value for BMI is "+F.BMI_p+", p-value for Weight is "+F.Wt_p);
  println("Males:\np-value for BMI is "+M.BMI_p+", p-value for Weight is "+M.Wt_p);
  println("Changes are insignificant...\n\nWhat about the differences between males and females?");
  
  // combine the April and September data since we've already seen that there's minimal difference.
  float[] allWt_F = Mat.concat(F.Wt_Sept,F.Wt_Apr); float[] allWt_M = Mat.concat(M.Wt_Sept,M.Wt_Apr);
  
  // compute the Mann-Whitney test statistic and p-valu
  float[] allWt = Comparison.mannWhitney(allWt_F,allWt_M);
  println("p-value for Weight difference between males and females: "+allWt[1]);
  
  // doing the same thing for BMI is simple enough. First we combine the data
  float[] allBMI_F = Mat.concat(F.BMI_Sept,F.BMI_Apr); float[] allBMI_M = Mat.concat(M.BMI_Sept,M.BMI_Apr);
  // then we compute the Mann-Whitney test statistic & p-value
  float[] allBMI = Comparison.mannWhitney(allBMI_F,allBMI_M);
  
  println("p-value for BMI difference between males and females: "+allBMI[1]);
  println("Aha! so, guys are in general heavier, but, since the BMI comparisons show insignificant differences\n"+
          "we can attribute the weight differences to guys being generally taller.");
}


//Visuals. How to use the subplot class.
// specify the colors for the different groups.
color fSept = color(50, 80, 200, 100); 
color fApr = color(50, 80, 200, 200);
color mSept = color(200, 50, 80, 100); 
color mApr = color(200, 50, 80, 200);
String[][] plotTitles = { {  "Weight: Females(kg)", "BMI: Females(kg/m^2)" } , {  "Weight: Males(kg)", "BMI: Males(kg/m^2)"}};

void draw() {
  background(255);
  // create the subplot.
  SubPlot splot = new SubPlot(this, 110, 100, 600, 400, 2, 2);

  for (int i=0; i<2; i++) {

    splot.setDataExtremes(0, F.ln+1, 40, 120,i,0);
    splot.setDataExtremes(0, F.ln+1, 10, 50,i,1);
    for (int j=0; j<2; j++) {
      textAlign(LEFT,TOP);
      splot.writeTitle(mediumFont,plotTitles[i][j],i,j);
      splot.yLabels(4,i,j);
    }
  }
  
  // x values for the scatter plot with each x value corresponding
  // to a subject.
  float[] fX = new float[F.ln]; 
  float[] mX = new float[M.ln];
  for (int i=0; i<F.ln; i++) {
    fX[i] = i+1;
  }
  for (int i=0; i<M.ln; i++) {
    mX[i] = i+1;
  }
  
  // draw the scatter plots
  // input it (xdata, ydata, color, subplot x number, subplot y number).
  splot.drawScatterPlot(fX, F.Wt_Sept, fSept, 0, 0);
  splot.drawScatterPlot(fX, F.Wt_Apr, fApr, 0, 0);

  splot.drawScatterPlot(mX, M.Wt_Sept, mSept, 1, 0);
  splot.drawScatterPlot(mX, M.Wt_Apr, mApr, 1, 0);

  splot.drawScatterPlot(fX, F.BMI_Sept, fSept, 0, 1);
  splot.drawScatterPlot(fX, F.BMI_Apr, fApr, 0, 1);

  splot.drawScatterPlot(mX, M.BMI_Sept, mSept, 1, 1);
  splot.drawScatterPlot(mX, M.BMI_Apr, mApr, 1, 1);

  // legend
  splot.legendHoriz("bottom", new int[]{fSept,fApr,mSept,mApr}, new String[]{"Females, Before","Females, After","Males, Before","Males, After"});
  // write the pvalues to the screen as well.
  textAlign(LEFT,TOP); textFont(smallFont);
  text("pValue: "+ nfc(pValue[0],4),splot.xLefts[0][0]+5,splot.yTops[0][0]+20);
  text("pValue: "+ nfc(pValue[1],4),splot.xLefts[1][0]+5,splot.yTops[1][0]+20);
  text("pValue: "+ nfc(pValue[2],4),splot.xLefts[0][1]+5,splot.yTops[0][1]+20);
  text("pValue: "+ nfc(pValue[3],4),splot.xLefts[1][1]+5,splot.yTops[1][1]+20);
  
  // plot title
  textFont(titleFont);
  text("Does the Freshman-15 exist?",splot.getLeft() - splot.s[0][0].leftBound,splot.getTop()-50);
}




// convenience class ----------------------------------------------------------------
class Sex {
  float[] BMI_Apr = new float[0];
  float[] BMI_Sept = new float[0];
  float[] Wt_Apr = new float[0];
  float[] Wt_Sept= new float[0];
  int ln = 0;

  // for the MannWhitney test, by Sex
  float BMI_p;
  float Wt_p;

  Sex() {
  }

  /* 
   Update the data. This is *not* the best way to do things (that would
   be to use an arraylist and then cast that to a float. 
   But this eliminates a few steps in the code so bear with me :)
   */
  void addElement(String[] s) {
    Wt_Sept = append(Wt_Sept, Float.parseFloat(s[1]) );
    Wt_Apr = append(Wt_Apr, Float.parseFloat(s[2]) );
    BMI_Sept = append(BMI_Sept, Float.parseFloat(s[3]) );
    BMI_Apr =  append(BMI_Apr, Float.parseFloat(s[4]) );
    ln = BMI_Apr.length;
  }

  /** compares the BMIs and Weights before and after */
  void MannWhitneyTest() {
    // get the pvalue which corresponds to the second value returned
    BMI_p = Comparison.mannWhitney(BMI_Apr, BMI_Sept)[1];
    // get the pvalue which corresponds to the second value returned
    Wt_p = Comparison.mannWhitney(Wt_Apr,Wt_Sept)[1];
  }
}

