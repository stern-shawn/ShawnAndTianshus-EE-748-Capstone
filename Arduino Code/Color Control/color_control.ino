#include <SPI.h>
//#include <digi_pot.h>

//digi_pot MCP4131(10); // with a CS pin of 10

int tapValue = 1;     // value 0-127 for pot wiper
int val = 0;          // Analog voltage monitor
int count = 0;        // duh

int ledDigitalOne[] = {14, 15, 16}; //the three digital pins of the first digital LED 14 = redPin, 15 = greenPin, 16 = bluePin
const boolean ON = LOW; //Define on as LOW (this is because we use a common Anode RGB LED (common pin is connected to +5 volts)
const boolean OFF = HIGH; //Define off as HIGH

int choice = 0;
 
//Predefined Colors
const boolean RED[] = {ON, OFF, OFF};
const boolean GREEN[] = {OFF, ON, OFF};
const boolean BLUE[] = {OFF, OFF, ON};
const boolean YELLOW[] = {ON, ON, OFF};
const boolean CYAN[] = {OFF, ON, ON};
const boolean MAGENTA[] = {ON, OFF, ON};
const boolean WHITE[] = {ON, ON, ON};
const boolean BLACK[] = {OFF, OFF, OFF}; 
 
//An Array that stores the predefined colors (allows us to later randomly display a color)
 
const boolean* COLORS[] = {RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA, WHITE, BLACK};
 
void setup(){
     for(int i = 0; i < 3; i++){
       pinMode(ledDigitalOne[i], OUTPUT); //Set the three LED pins as outputs
    }       
    
    // Initialize pot to lowest value
    //MCP4131.setTap(MCP4131_MIN);
}
    
void loop(){
    // Test inc/dec functionality
    if (count == 63) {
      count = 0;
    } else {
      count++;
    }
    /*
    if (count < 31) {    
      MCP4131.increment();
    } else {
      MCP4131.decrement();
    }
    */
    /* Example - 2 Go through Random Colors  Set the LEDs to a random color*/
    //int rand = random(0, sizeof(COLORS) / 2);
    
    if (choice == 8) {
      choice = 0;
    } 
    setColor(ledDigitalOne, COLORS[choice]);      
    delay(1000); 
    choice++;
       
}
 
/*
Sets an led to any color
*/
void setColor(int* led, boolean* color){
 
    for(int i = 0; i < 3; i++){    
        digitalWrite(led[i], color[i]);     
    }
    
}

void setColor(int* led, const boolean* color){ 
    boolean tempColor[] = {color[0], color[1], color[2]};
    setColor(led, tempColor);    
}
