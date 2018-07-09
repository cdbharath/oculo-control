# EOG signal acquisition and classification using ADS1299


<p align="center">
  <img src="https://github.com/bharath7kumar/Oculo-control/tree/master/images/me.jpeg" width="50%" height="50%" >
</p>

### Objectives of the Project:

1. Design a circuit that can process the electrode signals to signal of range 0-5V for supplying it to Arduino ADC

2. Transfer the data from Arduino to PC using HC-05 Bluetooth module

3. Acquire and transfer the EOG signals to PC using ADS1299 and Arduino

4. Build an interface that can make use of the acquired signal in Python

5. Write a classification Algorithm to classify the signals

6. Implement it in an Application

### Tools used for Implementation

1. Python
2. Processing (Java)

### Data transfer from Arduino to PC:

The HC-05 module is powered by 5V from the Arduino. The interfacing of Arduino and HC-05 is as follows. The transfer pin of Arduino is connected to receiver pin of HC-05 and receiver pin of Arduino is connected to the receiver pin of HC-05. The data acquired in the ADC pin is converted to voltage.

The baud rate is set to 115200 so that the speed of data transfer becomes 115200 bits per second. This is implemented with C language in Arduino IDE and uploaded to Arduino. A Python script is written in PC ensuring the reception of data bytes. This is ensured using the python module PySerial, that can decode the incoming byte and display it as output.

### Electrode Placement:

The placement of electrodes is important in EOG signal acquisition. The point of forehead is taken as reference, where there is very less electrical activity. Two electrodes are placed above and below an eye, and the other two are placed right of the right eye and left of the left eye. The iris has abundant electrons to create electrical activity during eye movements. This is the crux of EOG.

<p align="center">
  <img src="https://github.com/bharath7kumar/Oculo-control/tree/master/images/electrode.jpg" >
  Electrode placement
</p>

### ADS1299 and Arduino Interfacing :

The ADS1299 and Arduino communicate using SPI (Serial Peripheral Interface). In SPI, only one side generates the clock signal (usually called CLK or SCK for Serial Clock). The side that generates the clock is called the “master”, and the other side is called the “slave”. There is always only one master (which is almost always your microcontroller), but there can be multiple slaves (more on this in a bit).

When data is sent from the master to a slave, it’s sent on a data line called MOSI, for “Master Out / Slave In”. If the slave needs to send a response back to the master, the master will continue to generate a prearranged number of clock cycles, and the slave will put the data onto a third data line called MISO, for “Master In / Slave Out”.

The SS line is normally held high, which disconnects the slave from the SPI bus. (This type of logic is known as “active low,” and you’ll often see used it for enable and reset lines.) Just beforedata is sent to the slave, the line is brought low, which activates the slave. When you’re done using the slave, the line is made high again.

The Arduino to ADS1299 connections is as follows:

1 . Arduino pin D10 (SS = Slave Select) to ADS1299 pin 39 (CS = Chip Select)

2 . Arduino pin D11 (MOSI = Master Out, Slave In) to ADS1299 pin 34 (DIN = Data In)

3 . Arduino pin D12 (MISO = Master In, Slave Out) to ADS1299 pin 43 (DOUT = Data Out)

4 . Arduino pin D13 (SCK = Serial Clock) to ADS1299 pin 40 (SCLK), and of course

5 . Arduino pin GND to ADS1299 pins 33, 49, 51 (DGND = Digital Ground)

6 . Arduino pin 5V to ADS1299 5V pin for powering ADS1299


### Electrode connection:

There are 16 pins in ADS1299 for 8 channels. Each channels will have a respective +ve and

-ve pin. There are 2 SRB pins, these SRB pins when enabled, gets shorted with the entire row of pins.

This enables us to use the electrodes with respect to common reference.

When the SRB pins are disabled, each channel pins can be used as differential input, so that each electrode has separate reference. There are bias pins, where required bias for the output signal can be supplied. For our project, we had disabled both SRB pins, and connected the electrodes in channel 1 to use it in differential mode. We connected ground to the Bias pin.

Connecting electrodes to ADS1299

<p align="center">
  <img src="https://github.com/bharath7kumar/Oculo-control/tree/master/images/ADS1299.png" >
</p>


### Arduino code:

The channels are activated in the setup. Each data from the ADS1299 channels is updated to Arduino in the loop during every iteration. Filters are applied to these signals. The filters include two 60 hz notch filter and a DC component filter. These are converted to bytes and sent to PC serially.

The serial event part will respond to the received serial data. The received data will correspond to certain actions. Some include enabling and disabling channels.


### ADS1299 data format:

The data from ADS1299 and Arduino is sent to PC through serial communication in a specific format, so that its easier for us to differentiate signals from all channels at different instances. The data format is as follows

Byte 1 : START BYTE (0xa0).

Byte 2 : Size of Data Packet excluding START BYTE, STOP BYTE and Byte 2.

Byte 3 to 6 : ID for each Data Packet, this is incremented after the arrival of every Data Packet.

Byte 7 to 38 : Data for all 8 channels (4 bytes for each channel)

Byte 39 : STOP BYTE (0xc0)

The baud rate is set to 115200 bits per second in both Arduino and Python receiver script. The data is sent in the sequence as mentioned above from the Arduino. On the receiving end, the python script receives the START BYTE and checks for the next.

After the second byte is received, the channel loop is manipulated with respect to the value of this byte. Then 4 continuous bytes are received, this act as a counter of data packets and keep track of the number of data packets.

After this, every 4 bytes of data is acquired and saved in a list array with row number as the channel number. This way 36 bytes for 8 channels are received and save in list array of size 8 rows x 4 columns. The bytes are decoded first. Then the 4 bytes from each channel is converted to 32 bit signed integer. This integer is in 2s complement, so that we can store and transfer negative values too. We convert the 2s complement back to integer. This value is further converted to micro volts by multiplying with the scaling factor. The scaling factor is as follows.

<p align="center">
  <img src="https://github.com/bharath7kumar/Oculo-control/tree/master/images/factor.jpg" >
</p>

Finally the script waits for the STOP BYTE, if the STOP BYTE is equal to 0xc0, the Data packet is valid, else its invalid.


### User Interface:

There are 4 scripts written to acquire and display the signals. To run the user interface openbci_lsl.py is to be run. Python openbci_lsl.py command should be used in command prompt to open GUI (Graphical User Interface) or Python openbci_lsl.py –stream command should be used to just run the script in command prompt and print the channel outputs.

Running the script will call streamerlsl.py python script. This script will in turn call open_bci_v3.py , allowing the script to find the port to which Arduino is connected.


Then the streamerlsl.py sets the default settings to acquire the signal properly. After that, the script reads the initial data dumped by Arduino in the Flash memory. That will give us the necessary information to proceed with the user interface. The streaming is then started. The user interface can be used alongside the streaming with the help of threading module. The streaming is run in separate thread so that is doesn’t clash with the user interface.

There is a separate script for graphical user interface gui.py , which is called when openbci_lsl.py is executed. The GUI is done totally using PyQt4 module in python.


Disabling channels:

For disabling channels we send data (1 to 8) serially to Arduino to disable the respective channels. For enabling we send (q to i) serially to Arduino to enable the channels respective channels. The Arduino is coded accordingly to manipulate these settings.

Output when openbci_lsl.py is executed:

<p align="center">
  <img src="https://github.com/bharath7kumar/Oculo-control/tree/master/images/GUI.jpg" >
  The Graphical User Interface
</p>

The GUI contains options to change the port, if we are using multiple Arduino. Using the connect button the specific port is connected along with the modified settings. To change the settings we have to disconnect, change the settings and connect again to visualize the changes. Board Config button is used to change the state of the channels, whether to turn it on or off.
Output when openbci_lsl.py –stream is used:

<p align="center">
  <img src="https://github.com/bharath7kumar/Oculo-control/tree/master/images/1.png" >
  Command prompt user interface
</p>


The basic information is printed in the screen. From the output we have a list array with 8 elements. Each element is the output of the respective channels in micro volts. This is printed continuously in the command prompt window.



### Output:

<p align="center">
  <img src="https://github.com/bharath7kumar/Oculo-control/tree/master/images/right.png"  >
  Eye to right
</p>
<p align="center">
  <img src="https://github.com/bharath7kumar/Oculo-control/tree/master/images/left.png"  >
  Eye to left
</p>
<p align="center">
  <img src="https://github.com/bharath7kumar/Oculo-control/tree/master/images/bottom.png"  >
  Eye to bottom
</p>
<p align="center">
  <img src="https://github.com/bharath7kumar/Oculo-control/tree/master/images/top.png"  >
  Eye to top
</p>


### Classification Algorithm:

Three classification algorithms has been implemented for classifying the EOG signal.

1 . Thresholding

2 . Template Matching

3 . RNN 

Thresholding :

The output signals can be picturized from the above mentioned images. The main problem in just plainly classifying the signal using thresholding is that, there is both positive and negative peak for both left and right movement. This makes it difficult to differentiate between them. To overcome this edge variable is used.

Edges are detected by subtracting the current voltage output by the previous voltage output. If we detect a positive edge, the edge flag is set to 1. When the output voltage surpasses the threshold level, the status of the movement is set as “right”. Again, until we detect a negative peak, the status is right, else the status goes back to “centre”. Vice versa for left.

Template matching:

Templates for left, right, top, bottom are already predefined from previously collected data. The incoming data is stored in a buffer of length same as the template. This buffer is then compared with the template using Pearson’s correlation function. 

The output value of this function will be in range between 0 to 1, with 1 being highly corelated and 0 being no correlation.

RNN :

The algorithm is implemented totally using Keras, tensorflow backend. The data should be pre processed accordingly. The model used here is sequential model. The features are the outputs amplitude from the two channels. The output is passes through embedding and convolution layer.

Then the output is pooled and the output data is normalized. The resultant is sent to LSTM layer and made dense to give an output. Only algorithm is made, yet to be tested.


### Application:

The output of the classifier is used to control the turtle to navigate the maze given below. The objective is to find a way out of the maze using eye movement. This can be developed to eye-movement based keyboard and many other applications.

<p align="center">
  <img src="https://github.com/bharath7kumar/Oculo-control/tree/master/images/maze.jpg" >
  The maze game
</p>

### Circuit design:

A circuit is built to process the milli volt output from electrodes to the range of 0 to 5 volts, such the it can be given as input to Arduino

Different stages of the circuit:

1 . The signal from channels are given to instrumentation amplifier AD620 to get differential output

2 . A passive high pass filter to reduce low frequency noise and remove DC component.

3 . An active high pass filter to again reduce the low frequency noise.

4 . A passive low pass filter to reduce high frequency noise.

5 . A 2nd order Butterworth filter with higher roll frequency.

6 . Finally, an instrumentation amplifier to add 2.5V bias. Each stage has their own gain that finally gives us output between 0-5V.


<p align="center">
  <img src="https://github.com/bharath7kumar/Oculo-control/tree/master/images/2.jpg"  >
  AD620 Instrumentation Amplifier
</p>


<p align="center">
  <img src="https://github.com/bharath7kumar/Oculo-control/tree/master/images/3.jpg"  >
  Rest of the signal processing circuit
</p>

