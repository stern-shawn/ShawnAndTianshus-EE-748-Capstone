/*
	Author: Shawn Stern
	Date: 5/28/2013
	EE 478 Final Lab
	Digital Potentiometer Control

	Header for the MCP4131 digital pot. Uses Arduino SPI library.
*/


#ifndef MCP4131_h
#define MCP4131_h

#include "Arduino.h"
#include <SPI.h>

// SPI Pins
#define MOSI	11
#define MISO	12
#define SCK		13

// Pot min and max values
#define MCP4131_MIN 0
#define MCP4131_MAX 127


class digi_pot
{
public:
    digi_pot(int csPin);
	void increment();
	void decrement();
	void setTap(int value);
	
	
private:
    int cs;
	void enable();
	void disable();
};

#endif
