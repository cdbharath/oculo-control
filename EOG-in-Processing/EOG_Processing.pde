// This can  be modified to online EEG processing


class EOG_Processing{
  float fs_Hz;
  int nchan;
  float threshold;
  
  
  // class constructor
  EOG_Processing(float samplingFreq , int numberOfChan , float thresh){
    
   fs_Hz =  samplingFreq;
   nchan = numberOfChan;
   threshold = thresh; 
  }
  
  public void process( float [][] newData_uV){      //holds raw EOG data that is new since the last call
             
     int len;
     float average=0;
     len = newData_uV[0].length;
     for(int i = 0; i<len ; i++) average = average + abs(newData_uV[0][i]);
      average = average / len;
     if(average >= threshold){
       //guesture.movement(0);  // 0 -> Select
       println("Blink Detected");
     }       
 }
   
}
