int motor_left[] = {2, 4};
int motor_right[] = {7, 8}; 

void motor_Init() {
    // Setup motors
    int i;
    for(i = 0; i < 2; i++) { 
        pinMode(motor_left[i], OUTPUT);
        pinMode(motor_right[i], OUTPUT);
    }
}

//Stop motor
void motor_stop() {
    digitalWrite(motor_left[0], LOW);
    digitalWrite(motor_left[1], LOW);
 
    digitalWrite(motor_right[0], LOW);
    digitalWrite(motor_right[1], LOW);
    delay(25);
}

//Move forward
void drive_backward() {
    digitalWrite(motor_left[0], HIGH);
    digitalWrite(motor_left[1], LOW);
   
    digitalWrite(motor_right[0], HIGH);
    digitalWrite(motor_right[1], LOW);
}

//Move backward 
void drive_forward() {
    digitalWrite(motor_left[0], LOW);
    digitalWrite(motor_left[1], HIGH);
 
    digitalWrite(motor_right[0], LOW);
    digitalWrite(motor_right[1], HIGH);
}

//Turn left 
void turn_left() {
    digitalWrite(motor_left[0], LOW);
    digitalWrite(motor_left[1], HIGH);
 
    digitalWrite(motor_right[0], HIGH);
    digitalWrite(motor_right[1], LOW);
}

//Turn right 
void turn_right() {
    digitalWrite(motor_left[0], HIGH);
    digitalWrite(motor_left[1], LOW);
 
    digitalWrite(motor_right[0], LOW);
    digitalWrite(motor_right[1], HIGH);
}

//Turn left 30 degrees
void left_30() {
  turn_left();
  delay(500);
  motor_stop();
}

void left_60() {
  turn_left();
  delay(700);
  motor_stop();
}

void left_90() {
  turn_left();
  delay(1100);
  motor_stop();
}

void turn_back() {
  turn_left();
  delay(2700);
  motor_stop();
}

void right_30() {
  turn_right();
  delay(500);
  motor_stop();
}

void right_60() {
  turn_right();
  delay(700);
  motor_stop();
}

void right_90() {
  turn_right();
  delay(1100);
  motor_stop();
}


