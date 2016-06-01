package com.example.robotController;

//Tianshu Bao
//Robot Control Project

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
 
public class MainActivity extends Activity {
	private static final String TAG = "Robot Control";
   
	ImageView btnUp, btnDown, btnLeft, btnRight, btnVoice, btnAccel, btnBreak;	//Robot Control buttons
	ImageView armUp, armDown, armLeft, armRight, armGrasp;	//Arm Control buttons
	TextView txtArduino;
	Handler h;
	TextView myTextView;
   
	final int RECIEVE_MESSAGE = 1;		// Status  for Handler
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private StringBuilder sb = new StringBuilder();
  
	private ConnectedThread mConnectedThread;
   
	// SPP UUID service
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
 
	// MAC-address for Bluetooth module
	private static String address = "00:13:03:27:00:09";
  
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	private boolean needtoConnect = true;
	
	private String previous_state = "4";
	
	private Region r_up;
	private Region r_down;
	private Region r_left;
	private Region r_right;
	
	private boolean moveLeft = false;
	private boolean moveRight = false;
	private boolean moveForward = false;
	private boolean moveBackward = false;
	
   
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 
		setContentView(R.layout.activity_main);
		
		txtArduino = (TextView) findViewById(R.id.txtArduino);		// for display the received data from the Arduino
	    myTextView = (TextView)findViewById(R.id.TextView1);
 
	    btnUp = (ImageView) findViewById(R.id.btnUp);	
	    //btnDown = (ImageView) findViewById(R.id.btnDown);	
	    //btnLeft = (ImageView) findViewById(R.id.btnLeft);
	    //btnRight = (ImageView) findViewById(R.id.btnRight);
	    btnVoice = (ImageView) findViewById(R.id.VoiceControl);
	    //btnAccel = (ImageView) findViewById(R.id.btnAccel);
	    btnBreak = (ImageView) findViewById(R.id.btnBrake);	    
	    
	    armUp = (ImageView) findViewById(R.id.armUp);
	    armDown = (ImageView) findViewById(R.id.armDown);
	    armLeft = (ImageView) findViewById(R.id.armLeft);
	    armRight = (ImageView) findViewById(R.id.armRight);
	    armGrasp = (ImageView) findViewById(R.id.armGrasp);
   
	    h = new Handler() {
	    	public void handleMessage(android.os.Message msg) {
	    		switch (msg.what) {
	    		case RECIEVE_MESSAGE:													// if receive massage
	            	byte[] readBuf = (byte[]) msg.obj;
	            	String strIncom = new String(readBuf, 0, msg.arg1);					// create string from bytes array
	            	sb.append(strIncom);												// append string
	            	int endOfLineIndex = sb.indexOf("\r\n");							// determine the end-of-line
	            	if (endOfLineIndex > 0) { 											// if end-of-line,
	            		String sbprint = sb.substring(0, endOfLineIndex);				// extract string
	                    sb.delete(0, sb.length());										// and clear
	                	txtArduino.setText("Data from Arduino: " + sbprint); 	        // update TextView
	            	}
	            	//Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
	            	break;
	    		}
	    	};
	    };
     
	    btAdapter = BluetoothAdapter.getDefaultAdapter();		// get Bluetooth adapter
	    checkBTState();
	    
	    Draw();
	    btnInitial();	    
	}
	
	//Initialize buttons
	private void btnInitial() {
		//Direction Control Button
		btnUp.setOnTouchListener(new OnTouchListener() {			
			public boolean onTouch(View v, MotionEvent event) {
				ImageView iv = (ImageView) v;
				
				Point point = new Point();
			    point.x = (int)event.getX();
			    point.y = (int)event.getY();
			    Log.d(TAG, "point: " + point);
			    		    
			    if (event.getAction() == MotionEvent.ACTION_DOWN) {
			    	if(r_up.contains((int)point.x,(int) point.y)) {
				        iv.setImageResource(R.drawable.forward);
				        mConnectedThread.write("1");
						previous_state = "1";
						moveForward = true;
						Toast.makeText(getBaseContext(), "Move Forward", Toast.LENGTH_SHORT).show();
			    	} else if (r_down.contains((int)point.x,(int) point.y)) {
				    	iv.setImageResource(R.drawable.backward);
				    	mConnectedThread.write("0");
						previous_state = "0";
						moveBackward = true;
						Toast.makeText(getBaseContext(), "Move Backward", Toast.LENGTH_SHORT).show();
				    } else if (r_left.contains((int)point.x,(int) point.y)) {
				    	iv.setImageResource(R.drawable.left);
				    	mConnectedThread.write("2");
				    	moveLeft = true;
						Toast.makeText(getBaseContext(), "Move Left", Toast.LENGTH_SHORT).show();
				    } else if (r_right.contains((int)point.x,(int) point.y)) {
				    	iv.setImageResource(R.drawable.right);
				    	mConnectedThread.write("3");
				    	moveRight = true;
						Toast.makeText(getBaseContext(), "Move Right", Toast.LENGTH_SHORT).show();
				    }        
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
				    if (moveForward) {
				    	iv.setImageResource(R.drawable.normal);
				    	mConnectedThread.write("4");
				    	moveForward = false;
				    } else if (moveBackward) {
				    	iv.setImageResource(R.drawable.normal);	
				    	mConnectedThread.write("4");
				    	moveBackward = false;
				    } else if (moveLeft) {
				    	iv.setImageResource(R.drawable.normal);
				    	//mConnectedThread.write(previous_state);
				    	mConnectedThread.write("4");
				    	moveLeft = false;
				    } else if (moveRight) {
				    	iv.setImageResource(R.drawable.normal);
				    	//mConnectedThread.write(previous_state);
				    	mConnectedThread.write("4");
				    	moveRight = false;
				    }
				}
							    
				return true;
			}
		});
	    
		/*
	    //Button Down
	    btnDown.setOnTouchListener(new OnTouchListener() {			
			public boolean onTouch(View v, MotionEvent event) {
				ImageView iv = (ImageView) v;
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					iv.setImageResource(R.drawable.down_pressed);
					mConnectedThread.write("0");
					previous_state = "0";
					Toast.makeText(getBaseContext(), "Move Forward", Toast.LENGTH_SHORT).show();
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					iv.setImageResource(R.drawable.down);
					//mConnectedThread.write("4");
				}
				return true;
			}
		});
	    
	    //Button Left
	    btnLeft.setOnTouchListener(new OnTouchListener() {			
			public boolean onTouch(View v, MotionEvent event) {
				ImageView iv = (ImageView) v;
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					iv.setImageResource(R.drawable.left_pressed);
					mConnectedThread.write("2");
					Toast.makeText(getBaseContext(), "Move Right", Toast.LENGTH_SHORT).show();
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					iv.setImageResource(R.drawable.left);
					mConnectedThread.write(previous_state);
				}
				return true;
			}
		});
	    
	    //Button Right
	    btnRight.setOnTouchListener(new OnTouchListener() {		
			public boolean onTouch(View v, MotionEvent event) {
				ImageView iv = (ImageView) v;
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					iv.setImageResource(R.drawable.right_pressed);
					mConnectedThread.write("3");
					Toast.makeText(getBaseContext(), "Move Right", Toast.LENGTH_SHORT).show();
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					iv.setImageResource(R.drawable.right);
					mConnectedThread.write(previous_state);
				}
				return true;
			}
		});
		*/
	    
	    //Voice Recognition
	    btnVoice.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		speak();
	    	}
	    });
	    
		
	    /*
	    //Accelerate button
	    btnAccel.setOnTouchListener(new OnTouchListener() {		
			public boolean onTouch(View v, MotionEvent event) {
				ImageView iv = (ImageView) v;
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					iv.setImageResource(R.drawable.faster_button_pressed);
					mConnectedThread.write("5");
					Toast.makeText(getBaseContext(), "Accelerate", Toast.LENGTH_SHORT).show();
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					iv.setImageResource(R.drawable.faster_button);
					mConnectedThread.write("6");
				}
				return true;
			}
		});
	    */
	    
	    //Break Button
	    btnBreak.setOnTouchListener(new OnTouchListener() {		
			public boolean onTouch(View v, MotionEvent event) {
				ImageView iv = (ImageView) v;
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					iv.setImageResource(R.drawable.brake_button_pressed);
					mConnectedThread.write("4");
					previous_state = "4";
					Toast.makeText(getBaseContext(), "Break", Toast.LENGTH_SHORT).show();
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					iv.setImageResource(R.drawable.brake_button);
					mConnectedThread.write("4");
				}
				return true;
			}
		});
	    
	    //Arm Grasp
	    armGrasp.setOnTouchListener(new OnTouchListener() {		
			public boolean onTouch(View v, MotionEvent event) {
				ImageView iv = (ImageView) v;
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mConnectedThread.write("8");
					iv.setImageResource(R.drawable.arm_grasp_press);
					Toast.makeText(getBaseContext(), "Arm Grasp", Toast.LENGTH_SHORT).show();
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					iv.setImageResource(R.drawable.arm_grasp);
				}
				return true;
			}
		});
	    
	    //Arm Up
	    armUp.setOnTouchListener(new OnTouchListener() {		
			public boolean onTouch(View v, MotionEvent event) {
				ImageView iv = (ImageView) v;
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mConnectedThread.write("9");
					iv.setImageResource(R.drawable.arm_up_press);
					Toast.makeText(getBaseContext(), "Arm Up", Toast.LENGTH_SHORT).show();
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					iv.setImageResource(R.drawable.arm_up);
					mConnectedThread.write("D");
				}
				return true;
			}
		});
	    
	    //Arm Down
	    armDown.setOnTouchListener(new OnTouchListener() {		
			public boolean onTouch(View v, MotionEvent event) {
				ImageView iv = (ImageView) v;
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mConnectedThread.write("A");
					iv.setImageResource(R.drawable.arm_down_press);
					Toast.makeText(getBaseContext(), "Arm Down", Toast.LENGTH_SHORT).show();
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					mConnectedThread.write("D");
					iv.setImageResource(R.drawable.arm_down);
				}
				return true;
			}
		});
	    
	    //Arm Left
	    armLeft.setOnTouchListener(new OnTouchListener() {		
			public boolean onTouch(View v, MotionEvent event) {
				ImageView iv = (ImageView) v;
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mConnectedThread.write("B");
					iv.setImageResource(R.drawable.arm_left_press);
					Toast.makeText(getBaseContext(), "Arm Left", Toast.LENGTH_SHORT).show();
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					mConnectedThread.write("D");
					iv.setImageResource(R.drawable.arm_left);
				}
				return true;
			}
		});
	    
	    //Arm Right
	    armRight.setOnTouchListener(new OnTouchListener() {		
			public boolean onTouch(View v, MotionEvent event) {
				ImageView iv = (ImageView) v;
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mConnectedThread.write("C");
					iv.setImageResource(R.drawable.arm_right_press);
					Toast.makeText(getBaseContext(), "Arm Right", Toast.LENGTH_SHORT).show();
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					mConnectedThread.write("D");
					iv.setImageResource(R.drawable.arm_right);
				}
				return true;
			}
		});
	}
	
	//Get positions of the direction control button
	public void Draw() {
		Path p_up = new Path();
		Path p_down = new Path();
		Path p_left = new Path();
		Path p_right = new Path();
		
		p_up.moveTo(78, 20);
		p_up.lineTo(154, 130);
	    p_up.lineTo(270, 130);
	    p_up.lineTo(330, 20);
	    p_up.close();
	    
	    p_down.moveTo(78, 364);
		p_down.lineTo(158, 270);
	    p_down.lineTo(250, 270);
	    p_down.lineTo(330, 364);
	    p_down.close();
	    
	    p_left.moveTo(20, 74);
		p_left.lineTo(134, 152);
	    p_left.lineTo(134, 272);
	    p_left.lineTo(20, 340);
	    p_left.close();
	    
	    p_right.moveTo(384, 74);
		p_right.lineTo(276, 146);
	    p_right.lineTo(276, 256);
	    p_right.lineTo(370, 330);
	    p_right.close();    
	    
	    
	    RectF rectUP = new RectF();
	    p_up.computeBounds(rectUP, true);
	    r_up = new Region();
	    r_up.setPath(p_up, new Region((int) rectUP.left, (int) rectUP.top, (int) rectUP.right, (int) rectUP.bottom));
	    
	    RectF rectDOWN = new RectF();
	    p_down.computeBounds(rectDOWN, true);
	    r_down = new Region();
	    r_down.setPath(p_down, new Region((int) rectDOWN.left, (int) rectDOWN.top, (int) rectDOWN.right, (int) rectDOWN.bottom));
	    
	    RectF rectLEFT = new RectF();
	    p_left.computeBounds(rectLEFT, true);
	    r_left = new Region();
	    r_left.setPath(p_left, new Region((int) rectLEFT.left, (int) rectLEFT.top, (int) rectLEFT.right, (int) rectLEFT.bottom));
	    
	    RectF rectRIGHT = new RectF();
	    p_right.computeBounds(rectRIGHT, true);
	    r_right = new Region();
	    r_right.setPath(p_right, new Region((int) rectRIGHT.left, (int) rectRIGHT.top, (int) rectRIGHT.right, (int) rectRIGHT.bottom));
	    
	}
  
	//Create bluetooth socket
	private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
		if(Build.VERSION.SDK_INT >= 10){
			try {
				final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
				return (BluetoothSocket) m.invoke(device, MY_UUID);
			} catch (Exception e) {
				Log.e(TAG, "Could not create Insecure RFComm Connection",e);
			}
		}
		return  device.createRfcommSocketToServiceRecord(MY_UUID);
	}
   
	@Override
	public void onResume() {
		super.onResume();
		
		
		if (btSocket != null && !btSocket.isConnected()) {
			needtoConnect = true;
		}
		
 
		if (needtoConnect) {
			Log.d(TAG, "...onResume - try connect...");
	   
		    // Set up a pointer to the remote device using it's mac address.
		    BluetoothDevice device = btAdapter.getRemoteDevice(address);
	    
		    //Create bluetooth socket
		    try {
		    	btSocket = createBluetoothSocket(device);
		    } catch (IOException e) {
		    	errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
		    }
	   
		    // Discovery is resource intensive.  
		    btAdapter.cancelDiscovery();
	   
		    // Establish the connection.  This will block until it connects.
		    Log.d(TAG, "...Connecting...");
		    try {
		    	btSocket.connect();
		    	Log.d(TAG, "....Connection ok...");
		    	Toast.makeText(getBaseContext(), "Bluetooth Connected", Toast.LENGTH_SHORT).show();
		    	needtoConnect = false;
		    } catch (IOException e) {
		    	try {
		        btSocket.close();
		        needtoConnect = true;
		    	} catch (IOException e2) {
		    		errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
		    	}
		    }
	     
		    Log.d(TAG, "...Create Socket...");
		   
		    // Create a data stream so we can talk to server.
		    mConnectedThread = new ConnectedThread(btSocket);
		    mConnectedThread.start();
		}
	}
 
	@Override
	public void onPause() {
		super.onPause();
 
		Log.d(TAG, "...In onPause()...");
  
	    /*
	    try     {
	      btSocket.close();
	    } catch (IOException e2) {
	      errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
	    }
	    */
	}
   
	private void checkBTState() {
		// Check for Bluetooth support and then check to make sure it is turned on
		if(btAdapter==null) { 
			errorExit("Fatal Error", "Bluetooth not support");
		} else {
			if (btAdapter.isEnabled()) {
				Log.d(TAG, "...Bluetooth ON...");
			} else {
				//Prompt user to turn on Bluetooth
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, 1);
			}
		}
	}
 
	private void errorExit(String title, String message){
		Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
		finish();
	}
 
	private class ConnectedThread extends Thread {
	    private final InputStream mmInStream;
	    private final OutputStream mmOutStream;
	 
	    public ConnectedThread(BluetoothSocket socket) {
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;
	 
	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) { }
	 
	        mmInStream = tmpIn;
	        mmOutStream = tmpOut;
	    }
	 
	    public void run() {
	        byte[] buffer = new byte[256];  // buffer store for the stream
	        int bytes; // bytes returned from read()

	        // Keep listening to the InputStream until an exception occurs
	        while (true) {
	        	try {
	                // Read from the InputStream
	                bytes = mmInStream.read(buffer);
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();		// Send to message queue Handler
	            } catch (IOException e) {
	                break;
	            }
	        }
	    }
	 
	    /* Call this from the main activity to send data to the remote device */
	    public void write(String message) {
	    	Log.d(TAG, "...Data to send: " + message + "...");
	    	byte[] msgBuffer = message.getBytes();
	    	try {
	            mmOutStream.write(msgBuffer);
	        } catch (IOException e) {
	            Log.d(TAG, "...Error data send: " + e.getMessage() + "...");     
	          }
	    }
	}
  
	//Voice Recognition
	public void speak() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		
		// Specify the calling package to identify your application
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
		
		// Display an hint to the user about what he should say.
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Control Command");
		
		// Given an hint to the recognizer about what the user is going to say
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}
  
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {
			if(resultCode == RESULT_OK) {
				ArrayList<String> voiceInput = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				String command = voiceInput.get(0);
				directionControl(command);
				myTextView.setText(command);
			}
		}else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
			showToastMessage("Audio Error");
		}else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
			showToastMessage("Client Error");
		}else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
			showToastMessage("Network Error");
		}else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
			showToastMessage("No Match");
		}else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
			showToastMessage("Server Error");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private String[] forward = {"go forward", "move forward", "go up", "go front"};
	private String[] backward = {"go backward", "move backward", "go back", "move back"};
	private String[] left = {"turn left", "move left", "go left"};
	private String[] right = {"turn right", "move right", "go right", "alright", "can write", "goal right"};
	private String[] stop = {"break", "stop", "hold on"};
	private String[] back = {"turn around", "turn back"};
	private String[] auto = {"self control", "autonomous", "come back", "auto control"};
	
	//Control the direction of the robot based on voice input
	private void directionControl(String command) {
		if (contains(forward, command)) {
			mConnectedThread.write("1");
		} else if (contains(backward, command)) {
			mConnectedThread.write("0");
		} else if (contains(left, command)) {
			controlLeft(command);
		} else if (contains(right, command)) {
			controlRight(command);
		} else if (contains(stop, command)) {
			mConnectedThread.write("4");
		} else if (contains(auto, command)) {
			mConnectedThread.write("7");
		} else if (contains(back, command)) {
			mConnectedThread.write("G");
		}
	}
	
	//Check if the input command is in default
	private boolean contains(String[] array, String command) {
		for (String s: array) {
			if (command.contains(s)) {
				return true;
			}
		}		
		return false;
	}
  
	//Control left turn
	private void controlLeft(String command) {
		if (command.contains("turn left 60")) {
			mConnectedThread.write("F");
		} else if(command.contains("turn left 30")) {
			mConnectedThread.write("E");
		} else {
			mConnectedThread.write("2"); //Default turn left 90 degrees
			delay(1300);
			mConnectedThread.write("4");
		}
	}
	
	//Control right turn
	private void controlRight(String command) {
		if (command.contains("turn right 60")) {
			mConnectedThread.write("I");
		} else if(command.contains("turn right 30")) {
			mConnectedThread.write("H");
		} else {
			mConnectedThread.write("3"); //Default turn right 90 degrees
			delay(1300);
			mConnectedThread.write("4");
		}
	}
	
	private void delay(int t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//Show toast message on screen
	void showToastMessage(String message){
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  	}
  
}