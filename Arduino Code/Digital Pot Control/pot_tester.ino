/*
  Author: Shawn Stern
  Date: 5/28/2013
  EE 478 Final Lab
  Digital Potentiometer Control

  Test code to verify we can increment/decrement the pot, in addition
  to setting it to a desired value at will without stepping. 

  Experiment setup:
  -Voltage divider output to Analog In Pin 2
  -Voltage divider set up with connections from wiper to B
  -Pins 13-10 connected to MCP4131 
    *Pin 13 = SCK
    *Pin 12 = SDI/SDO through 4.7k resistor
    *Pin 11 = SDI/SDO direct w/ wire
    *Pin 10 = ~CS
*/

#include <SPI.h>
#include <digi_pot.h>

//setup an intance of MCP4131
digi_pot MCP4131(10); // with a CS pin of 10

int tapValue = 1;     // value 0-127 for pot wiper
int val = 0;          // Analog voltage monitor
int count = 0;        // duh

void setup(void)
{
  Serial.begin(9600);
  Serial.println("MCP4231 Test:");
  
  // Initialize pot to lowest value
  MCP4131.setTap(MCP4131_MIN);
}

void loop(void)
{  
  val = analogRead(2);
  Serial.print("Voltage level: ");
  Serial.println(val, DEC);
 

  // Test inc/dec functionality
  if (count == 255) {
    count = 0;
  }
  
  if (count < 128) {    
    MCP4131.increment();
  } else {
    MCP4131.decrement();
  }
  
  count++;
  
  
  /*
  // Test value setting functionality
  MCP4131.setTap(tapValue);
  if (count < 128)
  {
    tapValue++;
  } else {
    tapValue--; 
  }

  count++;

  if (count == 255) {
    count = 0;
  }
  */ 

  delay(25);
}