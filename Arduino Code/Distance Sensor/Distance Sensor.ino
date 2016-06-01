// Output pin
int trig = 18;
// Measurement pin
int echo = 19;

// Storage variables
float duration, inches, cm;

void setup(){
	Serial.begin(9600);
	pinMode(trig, OUTPUT);
	pinMode(echo, INPUT); 
}

void loop() {
	// Prime sensor by sending 10us pulse
	digitalWrite(trig,LOW); 
	delayMicroseconds(2);
	digitalWrite(trig,HIGH); 	
	delayMicroseconds(10);
	digitalWrite(trig,LOW); 
	delayMicroseconds(2);

	// Record sensor output and format data
	duration = pulseIn(echo, HIGH);	// Measure pulse length in us
	inches = duration / 74 / 2;		// Convert to inches
	cm = duration / 29 / 2;			// Convert to cm

	// Report formatted results to user
	Serial.print("Inches: ");
	Serial.print(inches);
	Serial.print(", cm: ");
	Serial.print(cm);
	Serial.println();
	delay(250);						// Period between samples
}

