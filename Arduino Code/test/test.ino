char incomingByte;  // incoming data
int LED1 = 13;      // LED pin
int LED2 = 12;
 
void setup() {
  Serial.begin(9600); // initialization
  pinMode(LED1, OUTPUT);
  pinMode(LED2, OUTPUT);
  Serial.println("Press 1 to LED ON or 0 to LED OFF...");
}
 
void loop() {
  if (Serial.available() > 0) {  // if the data came
    incomingByte = Serial.read(); // read byte
    if(incomingByte == '0') {
       digitalWrite(LED1, LOW);  // if 1, switch LED Off
       Serial.println("Moving Backward");  // print message
    }
    if(incomingByte == '1') {
       digitalWrite(LED1, HIGH); // if 0, switch LED on
       Serial.println("Moving Forward");
    }
    if(incomingByte == '2') {
      digitalWrite(LED2, LOW);
      Serial.println("Moving Left");
    }
    if(incomingByte == '3') {
      digitalWrite(LED2, HIGH);
      Serial.println("Moving Right");
    }
  }
}
