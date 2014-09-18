package com.example.sensorlogger;



import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity {
	
	private TextView accelTextView, lightTextView, giroTextView;
	private EditText addressEditText, portEditText;
	private Button connectButton;
	private SensorManager sensorManager;
	private Sensor accelerometer, light, giro;
	private float x,y,z;
	private float lux;
	private float wx,wy,wz;
	private boolean isConnected=false;
	private String hostAddress = "10.0.2.2";
	private int portNumber=8888;
	private String data;
	private ClientThread cThread;
	private long timeStart;
	private float time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		accelTextView = (TextView) findViewById(R.id.accelText);
		lightTextView = (TextView) findViewById(R.id.lightText);
		giroTextView = (TextView) findViewById(R.id.giroText);		
		
		addressEditText = (EditText) findViewById(R.id.addressEditText);
		portEditText = (EditText) findViewById(R.id.portEditText);
		
		connectButton = (Button) findViewById(R.id.connectButton);
		connectButton.setText("Connect");
		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		giro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

		
	}
	
	protected void onResume(){
		super.onResume();
		timeStart=SystemClock.elapsedRealtime();
		sensorManager.registerListener(accelerationListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(lightListener, light, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(giroListener, giro, SensorManager.SENSOR_DELAY_NORMAL);
		
	}
	
	protected void onPause(){
		super.onPause();
		sensorManager.unregisterListener(accelerationListener);
		sensorManager.unregisterListener(lightListener);
		sensorManager.unregisterListener(giroListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onConnectButtonClicked(View view){
		String err = null;
		if(!isConnected){
			hostAddress=addressEditText.getText().toString();
			String portNr = portEditText.getText().toString();
			
			try{
				portNumber=Integer.parseInt(portNr);
			}
			catch(Exception e){
				Toast toast=Toast.makeText(this, "Invalid port number", Toast.LENGTH_LONG);
				toast.show();
			}
			
			cThread = new ClientThread(hostAddress, portNumber);
			cThread.start();
			
			if((err = cThread.getErrorFeedback())!=null){
				Toast toast=Toast.makeText(this, "Error occured: "+ err, Toast.LENGTH_LONG);
				toast.show();
				err = null;
			}
			else{
				isConnected = true;
				connectButton.setText("Disconnect");
				Toast toast = Toast.makeText(this, "Connected", Toast.LENGTH_SHORT);
				toast.show();
			}
			
		}
		else{
			cThread.stopThread();
			if((err = cThread.getErrorFeedback())!=null){
				Toast toast=Toast.makeText(this, "Error occured: "+ err, Toast.LENGTH_LONG);
				toast.show();
				err=null;
			}
			isConnected=false;
			connectButton.setText("Connect");
			Toast toast = Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT);
			toast.show();
		
		}
		
				
	}
	
	private SensorEventListener accelerationListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent e) {
			x=e.values[0];
			y=e.values[1];
			z=e.values[2];
			refreshDisplay();
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accurancy) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private SensorEventListener lightListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			lux=event.values[0];
			refreshDisplay();
			
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private SensorEventListener giroListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			wx=event.values[0];
			wy=event.values[1];
			wz=event.values[2];
			refreshDisplay();
			
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
			
		}
	};
	
	private void refreshDisplay(){
		time=(SystemClock.elapsedRealtime()-timeStart)/1000;
		
		if(accelerometer==null)
			accelTextView.setText("Not avaliable");
		else{
			String accelOutput = String.format("X:%3.2f | Y:%3.2f | Z:%3.2f | T:%3.2f", x, y, z, time);
			accelTextView.setText(accelOutput);
			if(cThread!=null)
				cThread.setData(accelOutput);
		}
																																																
		if(light==null)
			lightTextView.setText("Not avaliable");
		else{
			String lightOutput = String.format("%3.2f",lux);
			lightTextView.setText(lightOutput);
		}
		
		if(giro==null)
			giroTextView.setText("Not avaliable");
		else{
			String giroOutput = String.format("wX:%3.2f | wY:%3.2f | wZ:%3.2f", wx, wy, wz);
			giroTextView.setText(giroOutput);
		}
		
	}
	
	
}
