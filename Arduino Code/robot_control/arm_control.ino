int turn_degree = 90;
int rotate_degree = 30;

//Initialize arm servo motors
void arm_Init() {
  armMotor1.attach(3);
  armMotor2.attach(6);
  armMotor3.attach(9);
  armMotor1.write(90);
  delay(500);
  armMotor2.write(30);
  delay(500);
  armMotor3.write(120);
  delay(500);
}

//Grasp object
void grasp() {
  armMotor3.write(65);
  delay(500);
}

//Release object
void drop() {
  armMotor3.write(120);
  delay(500);
}

//Turn arm (Range from 10 to 170)
void arm_turn(int degree) {
  armMotor1.write(degree);
}

//Rotate arm (Range from 30 to 120)
void arm_rotate(int degree) {
  armMotor2.write(degree);
  delay(20);
}

void arm_up() {
  if (rotate_degree >= 30 && rotate_degree <= 120) {
    rotate_degree++;
    arm_rotate(rotate_degree);
    if (rotate_degree == 121) {
      rotate_degree = 120;
    }
  }
}

void arm_down() {
  if (rotate_degree >= 30 && rotate_degree <= 120) {
    rotate_degree--;
    arm_rotate(rotate_degree);
    if (rotate_degree == 29) {
      rotate_degree = 30;
    }
  }
}

void arm_left() {
  if (turn_degree >= 10 && turn_degree <= 170) {
    turn_degree--;
    arm_turn(turn_degree);
    if (turn_degree == 9) {
      turn_degree = 10;
    }
  }
}

void arm_right() {
  if (turn_degree >= 10 && turn_degree <= 170) {
    turn_degree++;
    arm_turn(turn_degree);
    if (turn_degree == 171) {
      turn_degree = 170;
    }
  }
}
