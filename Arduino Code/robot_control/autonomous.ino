long duration;
int leftDistance, rightDistance;
const int dangerThresh = 8; //threshold for obstacles in cm

void selfControl() {
  int distanceFwd = ping();
  if (distanceFwd > dangerThresh) {  //if path is clear
    setColor(ledDigitalOne, COLORS[1]);
    drive_forward();
  } else {
    motor_stop();
    sensorMotor.write(0);
    delay(500);
    rightDistance = ping(); //scan to the right
    delay(500);
    sensorMotor.write(180);
    delay(700);
    leftDistance = ping();
    delay(500);
    sensorMotor.write(90);
    delay(100);
    compareDistance();
  } 
}

//Compare the distance of left and right
void compareDistance() {
  if (leftDistance>rightDistance) {  //if left is less obstructed, turn left
    setColor(ledDigitalOne, COLORS[4]);
    right_90();    
  } else if (rightDistance>leftDistance) {  //if right is less obstructed, turn right
    setColor(ledDigitalOne, COLORS[3]);
    left_90();
  } else {  //if they are equally obstruct, turn around
    turn_back();
  }
}

//Send out distance sensor signals
long ping() {
  // Prime sensor by sending 10us pulse
  digitalWrite(trig, LOW); 
  delayMicroseconds(2);
  digitalWrite(trig, HIGH); 	
  delayMicroseconds(10);
  digitalWrite(trig, LOW); 
  delayMicroseconds(2);
  
  //Convert duration into distance
  duration = pulseIn(echo, HIGH);
  return duration / 29 / 2;
}
