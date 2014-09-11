package com.example.sensorlogger;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ClientThread extends Thread{
	
	private Socket socket;
	private int portNumber;
	private String hostAddress;
	private PrintWriter out;
	private boolean isConnected;
	private String data;
	private String errorFeedback=null;
	
	public ClientThread(String hostAddress, int portNumber ){
		this.hostAddress=hostAddress;
		this.portNumber=portNumber;
	}

	@Override
	public void run() {
		try{
			InetAddress serverAddr =InetAddress.getByName(hostAddress);
			socket = new Socket(serverAddr,portNumber);
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			isConnected = true;
			while(isConnected){
				out.println(data);
				out.flush();
				Thread.sleep(2);
			}
		}
		catch(Exception e){
			errorFeedback=e.toString();
		}
		finally{
			try{
				socket.close();
				out.close();
				isConnected=false;
			}
			catch(Exception e){
				errorFeedback=e.toString();
			}
		}
		

	}
	public void setData(String data){
		this.data=data;
	}
	
	public void stopThread(){
		isConnected=false;
	}
	public String getErrorFeedback(){
		return errorFeedback;
	}

}
