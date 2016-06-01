#include <Servo.h>

Servo armMotor1;
Servo armMotor2;
Servo armMotor3;

void setup() {
  armMotor1.attach(3);
  armMotor2.attach(6);
  armMotor3.attach(9);
  armMotor1.write(90);
  armMotor2.write(90);
  armMotor3.write(90);
}

void loop() {
  
}
