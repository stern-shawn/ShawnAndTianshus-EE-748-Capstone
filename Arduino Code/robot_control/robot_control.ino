#include <SPI.h>
#include <digi_pot.h>
#include <Servo.h>

char incomingByte;  // incoming data

int autonomous = 0;  //Robot in self-control mode or not

digi_pot MCP4131(10); // with a CS pin of 10
int speedup = 0;

Servo sensorMotor;  //Pin 5

Servo armMotor1;
Servo armMotor2;
Servo armMotor3;

int ledDigitalOne[] = {14, 15, 16}; //the three digital pins of the first digital LED 14 = redPin, 15 = greenPin, 16 = bluePin
const boolean ON = LOW; //Define on as LOW (this is because we use a common Anode RGB LED (common pin is connected to +5 volts)
const boolean OFF = HIGH; //Define off as HIGH

//Predefined Colors
const boolean RED[] = {ON, OFF, OFF};
const boolean GREEN[] = {OFF, ON, OFF};
const boolean BLUE[] = {OFF, OFF, ON};
const boolean YELLOW[] = {ON, ON, OFF};
const boolean CYAN[] = {OFF, ON, ON};
const boolean MAGENTA[] = {ON, OFF, ON};
const boolean WHITE[] = {ON, ON, ON};
const boolean BLACK[] = {OFF, OFF, OFF}; 

const boolean* COLORS[] = {RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA, WHITE, BLACK};


// Output pin
int trig = 18;
// Measurement pin
int echo = 19;

int check_distance = 0;

long distance = 0;

int grab = 0;
int up = 0;
int down = 0;
int left = 0;
int right = 0;

int brightness = 0;

void setup() {
    Serial.begin(9600); // initialization
    motor_Init();
    arm_Init();
    sensorMotor.attach(5);
    sensorMotor.write(90);
    MCP4131.setTap(127);
    pinMode(trig, OUTPUT);
    pinMode(echo, INPUT); 
    for(int i = 0; i < 3; i++){
       pinMode(ledDigitalOne[i], OUTPUT); //Set the three LED pins as outputs
    } 
}
 
void loop() {
    if (Serial.available() > 0) {  // if the data came
        incomingByte = Serial.read(); // read byte
        switch (incomingByte) {
          case '1': // 1 to move forward
            motor_stop();
            drive_forward();
            Serial.println("Moving Forward");  // print message
            setColor(ledDigitalOne, COLORS[1]); 
            check_distance = 1;
            break;
          case '0':  // 0 to move backward
            motor_stop();
            drive_backward();
            Serial.println("Moving Backward");
            setColor(ledDigitalOne, COLORS[0]); 
            check_distance = 0;
            break;
          case '2': // 2 to turn left
            motor_stop();
            turn_left();
            Serial.println("Moving Left");
            setColor(ledDigitalOne, COLORS[3]); 
            check_distance = 0;
            break;
          case '3':  // 3 to turn right
            motor_stop();
            turn_right();
            Serial.println("Moving Right");
            setColor(ledDigitalOne, COLORS[4]);
            check_distance = 0;
            break;
          case '4':  // 4 to stop
            motor_stop();
            Serial.println("Break");
            setColor(ledDigitalOne, COLORS[7]);
            check_distance = 0;
            autonomous = 0;
            break;
          case '5':
            speedup = 1;
            break;
          case '6':
            speedup = 0;
            break;
          case '7':  //enable self-control mode
            autonomous = 1;
            Serial.println("Enable autonomous mode");
            break;
          case '8':
            //Arm Grasp and Release
            if (grab == 0) {
              grasp();
              grab = 1;
            } else {
              drop();
              grab = 0;
            }
            break;
          case '9':
            //Arm Down
            down = 1;
            break;
          case 'A':
            //Arm Up
            up = 1;
            break;
          case 'B':
            //Arm Left
            left = 1;
            break;
          case 'C':
            //Arm Right 
            right = 1;
            break;
          case 'D':
            //Arm Stop
            left = 0;
            right = 0;
            up = 0;
            down = 0;
            break;
          case 'E':
            left_30();
            break;
          case 'F':
            left_60();
            break;
          case 'G':
            turn_back();
            break;
          case 'H':
            right_30();
            break;
          case 'I':
            right_60();
            break;
        }
    }
    
    //Self-control
    if (autonomous == 1) {
      selfControl();
    }
    
    //Distance in front of the robot
    distance = ping();
    brightness = map(distance, 10, 200, 0, 31);
    MCP4131.setTap(brightness);
    
    //Check distance  
    if (check_distance == 1 && autonomous == 0) {       
      Serial.println(distance);
      if (distance < 10) {  //if path is blocked, stop
        motor_stop();
        setColor(ledDigitalOne, COLORS[7]);
      }
    }
    
    /*
    if (speedup == 1) {
      MCP4131.decrement();
      delay(10);
    } else {
      MCP4131.increment();
      delay(10);
    }
    */
    if (up == 1) {
      arm_up();
    } else if (down == 1) {
      arm_down();
    } else if (left == 1) {
      arm_left();
    } else if (right == 1) {
      arm_right();
    }
}

// Sets an led to any color
void setColor(int* led, const boolean* color){ 
    boolean tempColor[] = {color[0], color[1], color[2]};
    setColor(led, tempColor);    
}

void setColor(int* led, boolean* color){ 
    for(int i = 0; i < 3; i++){    
        digitalWrite(led[i], color[i]);     
    }    
}






