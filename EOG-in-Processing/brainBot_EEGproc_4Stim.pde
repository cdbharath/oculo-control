

///////////////////////////////////////////////
//
// GUI for controlling the ADS1299-based OpenBCI Shield
//
// Created: Chip Audette, Oct 2013 - Jan 2014
//
// Requires gwoptics graphing library for processing.  Built on V0.5.0
// http://www.gwoptics.org/processing/gwoptics_p5lib/
//
// 
// 
///////////////////////////////////////////////

import processing.serial.*;
import ddf.minim.analysis.*; //for FFT
import java.util.*; //for Array.copyOfRange()
import java.lang.Math; //for exp, log, sqrt...they seem better than Processing's built-in
//import processing.core.PApplet;

/*-----------------------------------------------------------------Modified by Surya ---------------------------------------------------------------*/
float fs_Hz = 250f;  //sample rate used by OpenBCI board
CheckerStimulation chSt  = null;

//EOG_Processing eogProcessing;   // need to change for eeg processing
//int numberOfEOGChannels = 1;
//float blinkThreshold = 200f;

GuestureDetection guesture  = null;

EEG_Processing eegProcessing;
global_counter globCount;
int[] chanInd = {0,1,2,3,4,5,6,7}; //specify channels being used
float corrthresh= 0.18; //correlation threshold value for zero class identification
float[] harmonics_arr={0.5,1,2,3}; //number of harmonics to be included in source model
float buffTime=3; //buffer time in seconds
int buffLen=floor(buffTime*fs_Hz); //buffer length in samples
float reg_coeff=1; //regularization coefficient for computation of covariance matrix
int globBuffLen=5;
int wait_time=floor(0.25*fs_Hz/50);

float flashFreq = 0;    // variable to store SSVEP stimulation frequency
float offsetTime = 10;  // Initial offset time (in sec) before highlighting checker pattern
float currTime;
int currHighlightedStimulus = -1;  // -1 for none, 0 for 15 Hz, 1 for 12 Hz, 2 for 10 Hz, 3 for 8.57 Hz
float[] stimFrequencies = {15,12,10,8.57};
int Nclass = 4;   // number of stimulation frequencies
float stimulusWindow = 7;
float restWindow = 3;
boolean synchMode = true; //false for asynchronous mode
int numberOfIterations = 0;
int maxIterations = 3;


//settings for BOT controller  

String botControl_portName = "COM3";   // bluetooth COM port
Serial serial_botControl = null;
boolean controlBot = false;   // true for sending commands for BOT via bluetooth

/*---------------------------------------------------------------------------------------------------------------------------------------------------*/


boolean useSyntheticData = false; //flip this to false when using OpenBCI

//Serial communications constants
openBCI_ADS1299 openBCI;
String openBCI_portName = "/dev/ttyACM0";   /************** CHANGE THIS TO MATCH THE COM PORT REPORTED ON *YOUR* COMPUTER *****************/

//these settings are for a single OpenBCI board
int openBCI_baud = 115200; //baud rate from the Arduino
int OpenBCI_Nchannels = 8; //normal OpenBCI has 8 channels
//use this for when daisy-chaining two OpenBCI boards
//int openBCI_baud = 2*115200; //baud rate from the Arduino
//int OpenBCI_Nchannels = 16; //daisy chain has 16 channels


//data constants
//float fs_Hz = 500f;  //sample rate used for ECG
float dataBuffX[];
float dataBuffY_uV[][]; //2D array to handle multiple data channels, each row is a new channel so that dataBuffY[3][] is channel 4
float dataBuffY_filtY_uV[][];
float data_std_uV[];
int nchan = OpenBCI_Nchannels;
//float scale_fac_uVolts_per_count = (4.5f / 24.0f / pow(2, 24)) * 1000000.f * 2.0f; //factor of 2 added 2013-11-10 to match empirical tests in my office on Friday
//float scale_fac_uVolts_per_count = (4.5f / 24.0f / pow(2, 24)) * 1000000.f * 2.0f;
float scale_fac_uVolts_per_count = (4.5f / 24.0f / pow(2, 24)) * 1000000.f * 2.0f;
//float scale_fac_uVolts_per_count = 1.0f; 
int prev_time_millis = 0;
final int nPointsPerUpdate = 50; //update screen after this many data points.  
float yLittleBuff[] = new float[nPointsPerUpdate];


float yLittleBuff_uV[][] = new float[nchan][nPointsPerUpdate];
float filtState[] = new float[nchan];

//filter constants
double[] b = new double[] { 
  1.396e-001, 0.0f, -2.792e-001, 0.0f, 1.396e-001
};
double[] a = new double[] { 
  1.0f, -2.6646e+000, 2.6098e+000, -1.2049e-000, 2.60e-001
};
filterConstants filtCoeff_bp =  new filterConstants(b, a, "Bandpass 0.1-40Hz");


double[] b2 = new double[] { 
  9.651e-001, -1.1933e-000, 2.2990e+000, -1.1933e-000, 9.651e-001
};
double[] a2 = new double[] {    
  1.000000000000000e+000, -1.2145e-000, 2.2978e+000, -1.1721e-000, 9.314e-001
}; 
filterConstants filtCoeff_notch =  new filterConstants(b2, a2, "Notch 50Hz");



//////The code below causes no filtering of the data
//double[] b = new double[] {1.0};
//double[] a = new double[] {1.0};
//filterConstants filtCoeff_bp =  new filterConstants(b,a,"No Filter");
//double[] b2 = new double[] {1.0};
//double[] a2 = new double[] {1.0};
//filterConstants filtCoeff_notch =  new filterConstants(b2,a2,"No Filter");
////////

//fft constants
int Nfft = 256; //set resolution of the FFT.  Use N=256 for normal, N=512 for MU waves
float fft_smooth_fac = 0.75f; //use value between [0 and 1].  Bigger is more smoothing.  Use 0.9 for MU waves, 0.75 for Alpha, 0.0 for no smoothing
FFT fftBuff[] = new FFT[nchan];   //from the minim library

//plotting constants
gui_Manager gui;
float vertScale_uV = 200.0f;
float displayTime_sec = 5f;
float dataBuff_len_sec = displayTime_sec+3f; //needs to be wider than actual display so that filter startup is hidden
//float displayTime_sec = 5f;
//float dataBuff_len_sec = buffLen;

//program constants
boolean isRunning=false;
boolean redrawScreenNow = true;
int openBCI_byteCount = 0;
int inByte = -1;    // Incoming serial data

//file writing variables
//PrintWriter fileoutput;
OutputFile_rawtxt fileoutput;
String output_fname;

//openBCI data packet
final int nDataBackBuff = 3*(int)fs_Hz;
dataPacket_ADS1299 dataPacketBuff[] = new dataPacket_ADS1299[nDataBackBuff]; //allocate the array, but doesn't call constructor.  Still need to call the constructor!
int curDataPacketInd = -1;
int lastReadDataPacketInd = -1;

void appendAndShift(float[] data, float[] newData) {
  int nshift = newData.length;
  int end = data.length-nshift;
  for (int i=0; i < end; i++) {
    data[i]=data[i+nshift];  //shift data points down by 1
  }
  for (int i=0; i<nshift; i++) {
    data[end+i] = newData[i];  //append new data
  }
}


void prepareData(float[] dataBuffX, float[][] dataBuffY_uV, float fs_Hz) {
  //initialize the x and y data
  int xoffset = dataBuffX.length - 1;
  for (int i=0; i < dataBuffX.length; i++) {
    dataBuffX[i] = ((float)(i-xoffset)) / fs_Hz; //x data goes from minus time up to zero
    for (int Ichan = 0; Ichan < nchan; Ichan++) { 
      dataBuffY_uV[Ichan][i] = 0f;  //make the y data all zeros
    }
  }
}

void initializeFFTObjects(FFT[] fftBuff, float[][] dataBuffY_uV, int N, float fs_Hz) {

  float[] fooData;
  for (int Ichan=0; Ichan < nchan; Ichan++) {
    //make the FFT objects...Following "SoundSpectrum" example that came with the Minim library
    //fftBuff[Ichan] = new FFT(Nfft, fs_Hz);  //I can't have this here...it must be in setup
    fftBuff[Ichan].window(FFT.HAMMING);

    //do the FFT on the initial data
    fooData = dataBuffY_uV[Ichan];
    fooData = Arrays.copyOfRange(fooData, fooData.length-Nfft, fooData.length); 
    fftBuff[Ichan].forward(fooData); //compute FFT on this channel of data
  }
}


void setup() {

  println("Starting setup...");

  //open window
  int win_x = 1200;  
  int win_y = 600;  //desktop PC
  size(win_x, win_y, P2D);
  //if (frame != null) frame.setResizable(true);  //make window resizable
  frameRate(60);

  //prepare data variables
  dataBuffX = new float[(int)(dataBuff_len_sec * fs_Hz)];
  dataBuffY_uV = new float[nchan][dataBuffX.length];
  dataBuffY_filtY_uV = new float[nchan][dataBuffX.length];
  data_std_uV = new float[nchan];
  for (int i=0; i<nDataBackBuff; i++) { 
    dataPacketBuff[i] = new dataPacket_ADS1299(nchan);
  }


  //initialize the data
  prepareData(dataBuffX, dataBuffY_uV, fs_Hz);

  //initialize the FFT objects
  for (int Ichan=0; Ichan < nchan; Ichan++) { 
    fftBuff[Ichan] = new FFT(Nfft, fs_Hz);
  };  //make the FFT objects
  initializeFFTObjects(fftBuff, dataBuffY_uV, Nfft, fs_Hz);

  //initilize the GUI
  gui = new gui_Manager(this, win_x, win_y, nchan, displayTime_sec, vertScale_uV);

  //associate the data to the GUI traces
  gui.initDataTraces(dataBuffX, dataBuffY_filtY_uV, fftBuff, data_std_uV);

  //open the data file for writing
  openNewLogFile();

  // Open the serial port to the Arduino that has the OpenBCI
  if (useSyntheticData == false) {
    if (true) {
      println(Serial.list());
      //openBCI_portName = Serial.list()[0]; //change this for your computer!
    }
    println("Opening Serial " + openBCI_portName);
    openBCI = new openBCI_ADS1299(this, openBCI_portName, openBCI_baud, nchan); //this also starts the data transfer after XX seconds
  }

  //start
  isRunning=true;

  /*-----------------------------------------------------------------Modified by Surya ---------------------------------------------------------------*/

  guesture = new GuestureDetection();

  chSt = new CheckerStimulation();
 /* stimFrequencies = new float[4];
  stimFrequencies[0]=15;
  stimFrequencies[1]=12;
  stimFrequencies[2]=10;
 stimFrequencies[3]=8.57;*/

  //eogProcessing = new EOG_Processing(fs_Hz,numberOfEOGChannels,blinkThreshold);   //need to change for eeg processing
  eegProcessing = new EEG_Processing();
  globCount= new global_counter(globBuffLen);

  // instantaiate bluetooth port if the controlBot(boolean) is true
  if (controlBot) {
    serial_botControl = new Serial(this, botControl_portName, 115200);
    serial_botControl.clear();
    println("Opening Serial " + botControl_portName);
  }

  /*----------------------------------------------------------------------------------------------------------------------------------------------------*/

  println("setup: Setup complete...");
}

int pointCounter = 0;
//boolean newData = true;
int prevBytes = 0; 
int prevMillis=millis();
int byteRate_perSec = 0;
boolean highlightOn = false;
void draw() {

  currTime = (float) (frameCount/60.0);
  if (isRunning) {
    if (useSyntheticData) {  //use synthetic data (for GUI debugging) or use real data from the Serial stream
      lastReadDataPacketInd = 0;
      for (int i = 0; i < nPointsPerUpdate; i++) {
        //synthesize data
        dataPacketBuff[lastReadDataPacketInd].sampleIndex++;
        synthesizeData(nchan, fs_Hz, scale_fac_uVolts_per_count, dataPacketBuff[lastReadDataPacketInd]);

        //gather the data into the "little buffer"
        for (int Ichan=0; Ichan < nchan; Ichan++) {
          //scale the data into engineering units..."microvolts"
          yLittleBuff_uV[Ichan][pointCounter] = dataPacketBuff[lastReadDataPacketInd].values[Ichan]* scale_fac_uVolts_per_count;
        }
        pointCounter++;
      }
    } else {
      openBCI.updateState();

      //gather any new data into the "little buffer"
      while ( (curDataPacketInd != lastReadDataPacketInd) && (pointCounter < nPointsPerUpdate)) {
        lastReadDataPacketInd = (lastReadDataPacketInd+1) % dataPacketBuff.length;  //increment to read the next packet
        for (int Ichan=0; Ichan < nchan; Ichan++) {   //loop over each cahnnel
          //scale the data into engineering units ("microvolts") and save to the "little buffer"
          yLittleBuff_uV[Ichan][pointCounter] = dataPacketBuff[lastReadDataPacketInd].values[Ichan] * scale_fac_uVolts_per_count;
        } 
        pointCounter++; //increment counter for "little buffer"
      }
    }

    //has enough data arrived to process it and update the GUI?
    //println("pointCounter " + pointCounter + ", nPointsPerUpdate " + nPointsPerUpdate);
    if (pointCounter >= nPointsPerUpdate) {
      pointCounter = 0;  //reset for next time
      byteRate_perSec = (int)(1000.f * ((float)(openBCI_byteCount - prevBytes)) / ((float)(millis() - prevMillis)));
      prevBytes = openBCI_byteCount; 
      prevMillis=millis();
      float foo_val;
      float prevFFTdata[] = new float[fftBuff[0].specSize()];

      for (int Ichan=0; Ichan < nchan; Ichan++) {
        //append data to larger buffer
        appendAndShift(dataBuffY_uV[Ichan], yLittleBuff_uV[Ichan]);

        //process the time domain data
        dataBuffY_filtY_uV[Ichan] = dataBuffY_uV[Ichan].clone();
        filterIIR(filtCoeff_notch.b, filtCoeff_notch.a, dataBuffY_filtY_uV[Ichan]); //notch
        filterIIR(filtCoeff_bp.b, filtCoeff_bp.a, dataBuffY_filtY_uV[Ichan]); //bandpass

        //update the FFT stuff
        for (int I=0; I < fftBuff[Ichan].specSize (); I++) prevFFTdata[I] = fftBuff[Ichan].getBand(I); //copy the old spectrum values
        float[] fooData_raw = dataBuffY_uV[Ichan];  //use the raw data
        fooData_raw = Arrays.copyOfRange(fooData_raw, fooData_raw.length-Nfft, fooData_raw.length);   //just grab the most recent block of data
        fftBuff[Ichan].forward(fooData_raw); //compute FFT on this channel of data

        //average the FFT with previous FFT data...log average
        double min_val = 0.01d;
        double foo;
        for (int I=0; I < fftBuff[Ichan].specSize (); I++) {   //loop over each fft bin
          if (prevFFTdata[I] < min_val) prevFFTdata[I] = (float)min_val; //make sure we're not too small for the log calls
          foo = fftBuff[Ichan].getBand(I); 
          if (foo < min_val) foo = min_val; //make sure this value isn't too small
          foo =   (1.0d-fft_smooth_fac) * java.lang.Math.log(java.lang.Math.pow(foo, 2));
          foo += fft_smooth_fac * java.lang.Math.log(java.lang.Math.pow((double)prevFFTdata[I], 2)); 
          foo_val = (float)java.lang.Math.sqrt(java.lang.Math.exp(foo)); //average in dB space
          fftBuff[Ichan].setBand(I, foo_val);
        }

        //compute the stddev of the signal...for the head plot
        float[] fooData_filt = dataBuffY_filtY_uV[Ichan];  //use the filtered data
        fooData_filt = Arrays.copyOfRange(fooData_filt, fooData_filt.length-Nfft, fooData_filt.length);   //just grab the most recent block of data
        data_std_uV[Ichan]=std(fooData_filt);
      }
      /*----------------------------------------------------------------------------------Modified by Sharat------------------------------------------------*/
      double[] corVec = eegProcessing.process(dataBuffY_filtY_uV);

      /*----------------------------------------------------------------------------------------------------------------------------------------------------*/


      /*------------------------------------------------------------------------------Modified by Surya -----------------------------------------*/
      //eogProcessing.process(yLittleBuff_uV);  // sent for EOG processing. Detects eye guestures 

      //println(yLittleBuff_uV[0].length);

      /*-----------------------------------------------------------------------------------------------------------------------------------------*/

      //tell the GUI that it has received new data via dumping new data into arrays that the GUI has pointers to
      gui.update();
      redrawScreenNow=true;
    } else {
      //not enough data has arrived yet.  do nothing more
    }

    //either way, update the title of the figure;
    frame.setTitle(int(frameRate) + " fps, Byte Count = " + openBCI_byteCount + ", bit rate = " + byteRate_perSec*8 + " bps" + ", Writing to " + output_fname);

    /*------------------------------------------------------------------------------Modified by Surya -----------------------------------------*/
    if (currTime == offsetTime ) {

      if (!highlightOn) {
        highlightOn = true;
        currHighlightedStimulus = currHighlightedStimulus+1;
        if (currHighlightedStimulus == 4) {
          currHighlightedStimulus=0;
          numberOfIterations++;
        }
        //println("currHighlightedStimulus" + Integer.toString(currHighlightedStimulus));
        chSt.highlight(currHighlightedStimulus);
        flashFreq = stimFrequencies[currHighlightedStimulus];
        offsetTime = offsetTime + stimulusWindow;
        if (numberOfIterations == maxIterations) {
          chSt.dispose();
          chSt = null;
          offsetTime =0;
        }
      } else {
        highlightOn = false;
        chSt.highlight(-1);   // highlight none. Rest mode
        flashFreq  = 0;       // user is not focusing on stimulus
        offsetTime = offsetTime + restWindow ;
      }
    }
    /*-----------------------------------------------------------------------------------------------------------------------------------------*/
  }

  if (redrawScreenNow) {
    //redraw the screen...not every time, get paced by when data is being plotted
    redrawScreenNow = false;  //reset for next time
    background(0);
    gui.draw();
  }
}

void serialEvent(Serial port) {
  //check to see which serial port it is
  if (port == openBCI.serial_openBCI) {
    boolean echoBytes = !openBCI.isStateNormal(); 
    openBCI.read(echoBytes);
    openBCI_byteCount++;
    if (openBCI.isNewDataPacketAvailable) {
      //copy packet into buffer of data packets
      curDataPacketInd = (curDataPacketInd+1) % dataPacketBuff.length; //this is also used to let the rest of the code that it may be time to do something
      openBCI.copyDataPacketTo(dataPacketBuff[curDataPacketInd]);  //resets isNewDataPacketAvailable to false

      //write this chunk of data to file
      fileoutput.writeRawData_dataPacket(dataPacketBuff[curDataPacketInd], scale_fac_uVolts_per_count, flashFreq);
    }
  } else {
    inByte = port.read();
    print(char(inByte));
  }
}

//interpret a keypress...the key pressed comes in as "key"
void keyPressed() {
  switch (key) {
  case '1':
    deactivateChannel(1-1); 
    break;
  case '2':
    deactivateChannel(2-1); 
    break;
  case '3':
    deactivateChannel(3-1); 
    break;
  case '4':
    deactivateChannel(4-1); 
    break;
  case '5':
    deactivateChannel(5-1); 
    break;
  case '6':
    deactivateChannel(6-1); 
    break;
  case '7':
    deactivateChannel(7-1); 
    break;
  case '8':
    deactivateChannel(8-1); 
    break;
  case 'q':
    activateChannel(1-1); 
    break;
  case 'w':
    activateChannel(2-1); 
    break;
  case 'e':
    activateChannel(3-1); 
    break;
  case 'r':
    activateChannel(4-1); 
    break;
  case 't':
    activateChannel(5-1); 
    break;
  case 'y':
    activateChannel(6-1); 
    break;
  case 'u':
    activateChannel(7-1); 
    break;
  case 'i':
    activateChannel(8-1); 
    break;
  case 's':
    stopButtonWasPressed();
    break;
  case 'm':
    println("'m' was pressed...taking screenshot...");
    saveFrame("OpenBCI-####.jpg");    // take a shot of that!
    break;

    /*----------------------------------------------modified by surya ---------------------------------------------------*/
    // This is to test the working   of the second applet. takes commands from keyboard
    // comment this block for guesture detection using EOG data.
  case 'W':
    guesture.movement(1);  // 1 -> Move up
    break;

  case 'S':
    guesture.movement(2);  // 2 -> Move down
    break;

  case 'A':
    guesture.movement(3);  // 3 -> Move left
    break;

  case 'D':
    guesture.movement(4);  // 4 -> Move right
    break;

  case 'C':
    guesture.movement(0);  // 0 -> Select
    break;

  default: 
    println("OpenBCI_GUI: '" + key + "' Pressed...sending to OpenBCI...");
    if (openBCI != null) openBCI.serial_openBCI.write(key + "\n"); //send the value as ascii with a newline character
  }
}

//swtich yard if a click is detected
void mousePressed() {

  //was the stopButton pressed?
  if (gui.stopButton.updateIsMouseHere()) { 
    stopButtonWasPressed(); 
    gui.stopButton.setIsActive(true);
    redrawScreenNow = true;
  }

  //check the channel buttons
  for (int Ibut = 0; Ibut < gui.chanButtons.length; Ibut++) {
    if (gui.chanButtons[Ibut].updateIsMouseHere()) { 
      toggleChannelState(Ibut);
      redrawScreenNow = true;
    }
  }
}

void mouseReleased() {
  //gui.stopButton.updateMouseIsReleased();
  gui.stopButton.setIsActive(false);
  redrawScreenNow = true;
}

//execute this function whenver the stop button is pressed
void stopButtonWasPressed() {
  //toggle the data transfer state of the ADS1299...stop it or start it...
  if (isRunning) {
    println("openBCI_GUI: stopButton was pressed...stopping data transfer...");
    if (openBCI != null) openBCI.stopDataTransfer();
    closeLogFile();
  } else { //not running
    openNewLogFile();  //open a new log file

      println("openBCI_GUI: startButton was pressed...starting data transfer...");
    if (openBCI != null) openBCI.startDataTransfer(); //use whatever was the previous data transfer mode (TXT vs BINARY)
  }

  isRunning = !isRunning;  //toggle the variable holding the current state of running

    //update the push button with new text based on the current running state
  //gui.stopButton.setActive(isRunning);
  if (isRunning) {
    gui.stopButton.setString(stopButton_pressToStop_txt);
  } else {
    gui.stopButton.setString(stopButton_pressToStart_txt);
  }
}

void synthesizeData(int nchan, float fs_Hz, float scale_fac_uVolts_per_count, dataPacket_ADS1299 curDataPacket) {
  float val_uV;
  for (int Ichan=0; Ichan < nchan; Ichan++) {
    if (gui.chanButtons[Ichan].isActive()==false) { //an INACTIVE button has not been pressed, which means that the channel itself is ACTIVE
      val_uV = randomGaussian()*sqrt(fs_Hz/2.0f); // ensures that it has amplitude of one unit per sqrt(Hz) of signal bandwidth
      //val_uV = random(1)*sqrt(fs_Hz/2.0f); // ensures that it has amplitude of one unit per sqrt(Hz) of signal bandwidth
      if (Ichan==0) val_uV*= 10f;  //scale one channel higher
    } else {
      val_uV = 0.0f;
    }
    curDataPacket.values[Ichan] = (int) (0.5f+ val_uV / scale_fac_uVolts_per_count); //convert to counts, the 0.5 is to ensure rounding
  }
}

//toggleChannelState: : Ichan is [0 nchan-1]
void toggleChannelState(int Ichan) {
  if ((Ichan >= 0) && (Ichan < gui.chanButtons.length)) {
    if (gui.chanButtons[Ichan].isActive()) { //button is pressed, which means the channel was NOT active
      //change to activate
      activateChannel(Ichan);
    } else {  //button is not pressed, which means the channel is active
      //change to activate
      deactivateChannel(Ichan);
    }
  }
}

//activateChannel: Ichan is [0 nchan-1]
void activateChannel(int Ichan) {
  println("OpenBCI_GUI: activating channel " + (Ichan+1));
  if (openBCI != null) openBCI.changeChannelState(Ichan, true); //activate
  gui.chanButtons[Ichan].setIsActive(false); //an active channel is a light-colored NOT-ACTIVE button
}  
void deactivateChannel(int Ichan) {
  println("OpenBCI_GUI: deactivating channel " + (Ichan+1));
  if (openBCI != null) openBCI.changeChannelState(Ichan, false); //de-activate
  gui.chanButtons[Ichan].setIsActive(true); //a deactivated channel is a dark-colored ACTIVE button
}

void openNewLogFile() {
  //close the file if it's open
  println("OpenBCI_GUI: closing log file");
  if (fileoutput != null) closeLogFile();

  //open the new file
  fileoutput = new OutputFile_rawtxt(fs_Hz);
  output_fname = fileoutput.fname;
  println("openBCI: openNewLogFile: opened output file: " + output_fname);
}

void closeLogFile() {
  fileoutput.closeFile();
}
