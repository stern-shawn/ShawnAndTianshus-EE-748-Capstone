/*
	Author: Shawn Stern
	Date: 5/28/2013
	EE 478 Final Lab
	Digital Potentiometer Control

	Source file for the MCP4131 digital pot control
	code. Uses Arduino SPI library.
*/


#include "digi_pot.h"


digi_pot::digi_pot(int csPin)
{
	//Set CS pin
	cs = csPin;
	pinMode(cs, OUTPUT);
	disable();

	// SPI setup
	SPI.begin();
	SPI.setBitOrder(MSBFIRST);
	SPI.setDataMode(SPI_MODE0);
	SPI.setClockDivider(SPI_CLOCK_DIV128);
}

// Select the chip
void digi_pot::enable() {
	digitalWrite(cs, LOW);
}

// Deselect the chip
void digi_pot::disable() {
	digitalWrite(cs, HIGH);
}

// Increment the value of the pot by one step
void digi_pot::increment() {
	enable();
	SPI.transfer(0x06);
	disable();
}

// Decrement the value of the pot by one step
void digi_pot::decrement() {
	enable();
	SPI.transfer(0x0A);	
	disable();
}

// Set potentiometer to a specific value 0-127
void digi_pot::setTap(int value) {
	enable();
	SPI.transfer(0);
	SPI.transfer(value);
	disable();
}
