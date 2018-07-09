
// This can  be modified to online EEG processing
//import matrixMath.*; //for matrix multiplications
import papaya.*; //for eigen value computations

float[][] matrix_multiply(float[][] X, float[][] Y) {
  int x1 = X.length;
  int x2= X[0].length;
  int y2 = Y[0].length;
  //  println(y1);
  float[][] multMat = new float[x1][y2];
  for (int i=0; i<x1; i++) {
    for (int j=0; j<y2; j++) {
      multMat[i][j]=0;
      for (int k=0; k<x2; k++) {
        multMat[i][j]=multMat[i][j]+X[i][k]*Y[k][j];
      }
    }
  }
  return multMat;
}

float[][] matrix_transpose(float[][] X) {
  int x1 = X.length;
  int x2 = X[0].length;
  float[][] tranMat = new float[x2][x1];
  for (int i=0; i<x1; i++) {
    for (int j=0; j<x2; j++) {
      tranMat[j][i]=X[i][j];
    }
  }
  return tranMat;
}

float[][] matrix_regularize(float[][] X, float lambda) {
  int x1=X.length;
  for (int i=0; i<x1; i++) {
    X[i][i]=X[i][i]+lambda;
  }
  return X;
}

float[][] scale_data(float[][] X){
  int x1=X.length;
  int x2=X[0].length;
  float[] maxval=new float[x2];
  float[] minval=new float[x2];
  for(int i=0;i<x2;i++){
    maxval[i]=X[0][i];
    minval[i]=X[0][i];
    for(int j=1;j<x1;j++){
      if(X[j][i]>maxval[i]) maxval[i]=X[j][i];
      if(X[j][i]<minval[i]) minval[i]=X[j][i];
    }
    for(int j=0;j<x1;j++){
      X[j][i]=X[j][i]*2/(maxval[i]-minval[i]);
    }
  }
  return X;
}

class EEG_Processing {
  int Ny;
  float threshold;
  int Nh;
  int Nt ;
  float[][][] sourceMod;

  // class constructor
  EEG_Processing() {
    
    threshold = corrthresh; 
    Nh=harmonics_arr.length;
    float[][][] sourceMod = new float[Nclass][Nt][Nh];

    /*for (int ctr1=0; ctr1<Nclass; ctr1++) {
     for (int ctr2=0; ctr2<Nh; ctr2++) {
     for (int ctr3=0; ctr3<Nt; ctr3++) {
     sourceMod[ctr1][ctr3][ctr2]=sin(TWO_PI*stimFrequencies[ctr1]*(ctr2+1)*ctr3);
     }
     }
     }*/
  }

  public double[] process(float[][] new_uvbuff) {      //holds raw EOG data that is new since the last call
    int Nbuff=new_uvbuff[0].length;
    Nt = buffLen;
    Ny = chanInd.length;
    //println(Ny);
   float[][] eeg_data = new float[Nt][Ny];
   // eeg_data = matrix_transpose(new_uvbuff);
    int len, ctr1, ctr2, ctr3;
    double[] corvec = new double[Nclass];
    float[][] sourceModsing = new float[Nt][2*Nh];
    float[] meanVar = new float[Ny];
    float totsum;
    // println(sourceMod[0][0][0]);
    int startInd=Nt-buffLen;
    // println(startInd);
    for (ctr1=0; ctr1<Nclass; ctr1++) { 
      for (ctr2=0; ctr2<Ny; ctr2++) {
        totsum=0;
        int tchan=chanInd[ctr2];
        for (int i=startInd; i<Nt; i++) {
          totsum=totsum+new_uvbuff[tchan][i];
        }
        meanVar[ctr2]=totsum/Nt; 
        ctr3 = 0;
        for (int k=startInd; k<Nt; k++) {
          eeg_data[ctr3++][ctr2]=new_uvbuff[tchan][k]-meanVar[ctr2];
        }
      }
      for (int i=0; i<Nt; i++) {
        for (int j=0; j<Nh; j++) {
          //sourceModsing[i][j]=sourceMod[ctr1][i][j];
          sourceModsing[i][2*j]=sin(TWO_PI*stimFrequencies[ctr1]*harmonics_arr[j]*i/fs_Hz);
          sourceModsing[i][2*j+1]=cos(TWO_PI*stimFrequencies[ctr1]*harmonics_arr[j]*i/fs_Hz);
          }
      }
      
      eeg_data=scale_data(eeg_data);
      float[][] Xtran = matrix_transpose(sourceModsing);
      float[][] Ytran = matrix_transpose(eeg_data);
      float[][] Cxx= matrix_multiply(Xtran, sourceModsing); 
      float[][] Cxy= matrix_multiply(Xtran, eeg_data); 
      float[][] Cyx= matrix_transpose(Cxy); 
      float[][] Cyy= matrix_multiply(Ytran, eeg_data); 
      
      Cyy=matrix_regularize(Cyy, reg_coeff);
      Cxx=matrix_regularize(Cxx, reg_coeff);
      float[][] Cxx_inv=Mat.inverse(Cxx); 
      float[][] Cyy_inv=Mat.inverse(Cyy); 
           
      float[][] prodmat=matrix_multiply(Cyy_inv, matrix_multiply(Cyx, matrix_multiply(Cxx_inv, Cxy))); 
      for(int i=0;i<Ny;i++){
        NaNs.replaceOriginalWith(prodmat[i],0);
      }
      //prodmat = matrix_regularize (prodmat,reg_coeff);
      //Mat.print(prodmat,5);println("\n");
      Eigenvalue eig= new Eigenvalue(prodmat); 
      double[] eigvals = eig.getRealEigenvalues(); 
      double maxval;
      int maxind;
      maxval=eigvals[0];
      for (int i=1; i<Ny; i++) {
        if (eigvals[i]>maxval)
          maxval=eigvals[i];
      }
      corvec[ctr1]=maxval;
    }

   double maxcorvec=0; 
    int claInd=0; 

    for (ctr1=0; ctr1<Nclass; ctr1++) {
      if (corvec[ctr1]>maxcorvec)
      {
        maxcorvec=corvec[ctr1]; 
        claInd=ctr1+1;
      }
    }
    if (maxcorvec<threshold)
    {
      claInd=0;
    }
   //println(claInd);
   Mat.print(corvec,5);println("\n");
   globCount.update_buffer(claInd);
   guesture.movement(claInd);
   if(globCount.globCounter==wait_time){
     globCount.globCounter=0;
     //println(globCount.besClas);
    // guesture.movement(globCount.besClas);
   }
    return corvec;

  }
} // end of class

