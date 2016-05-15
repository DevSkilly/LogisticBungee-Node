package fr.hugo4715.crackedbroker.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientHandler implements Runnable{
	private boolean logged = false;
	private Socket socket;

	private DataOutputStream out;
	private DataInputStream in;

	private boolean running = true;
	private CrackedBrokerServer srv;
	
	private List<byte[]> sub;
	
	private boolean listenAll = false;
	public ClientHandler(CrackedBrokerServer srv, Socket socket) throws IOException {
		this.sub = new ArrayList<>();
		this.srv = srv;
		this.socket = socket;
		log("Creating new Client handler...");
		
		out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		
	}

	@Override
	public void run(){
		log("running...");
		try {
			if(!logged){
				log("Waiting for password");
				int size = in.readInt();
				byte[] data = new byte[size];
				in.readFully(data, 0, data.length);
				
				String pass = new String(data, StandardCharsets.UTF_8);
				if(pass == null)return;
				if(!pass.equals(srv.getPassword())){
					log("Incorrect password! Rejecting connection");
					socket.close();
					return;
				}
				logged = true;
				log("Correct password! Accepting connection");

			}

			while(running){
				byte packetType = in.readByte();
				
				switch(packetType){
					case 0: //subsribe
						log("Received sub packet");
						int size = in.readInt();
						byte[] data = new byte[size];
						in.readFully(data, 0, data.length);
						if(!sub.contains(data))sub.add(data);
						log("Subscribed to " + new String(data));
						break;
					case 1:
						listenAll = true;
						break;
					case 2: //un-sub
						log("Received un-sub packet");
						int size2 = in.readInt();
						byte[] data2 = new byte[size2];
						in.readFully(data2, 0, data2.length);
						sub.remove(data2);
						log("Un-subscribed from " + new String(data2));
						break;
					case 3: //publish
						log("Received publish packet");
						int channelSize = in.readInt();
						byte[] channelData = new byte[channelSize];
						in.readFully(channelData, 0, channelData.length);
						
						int msgSize = in.readInt();
						byte[] msgData = new byte[msgSize];
						in.readFully(msgData, 0, msgData.length);
						
						srv.publish(channelData,msgData);
						break;
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isListening(byte[] channel){
		if(!listenAll)return true;
		
		for(byte[] l : sub){
			if(Arrays.toString(l).equals(Arrays.toString(channel))){
				return true;
			}
		}
		return false;
	}
	
	public void sendMessage(byte[] channel, byte[] msg) throws IOException{
		log("Sending msg....");
		out.writeByte((byte)0); //packet 0 is publish
		out.writeInt(channel.length);
		out.write(channel);
		
		out.writeInt(msg.length);
		out.write(msg);
		
		out.flush();
		log("Success!");
	}
	
	private void log(String msg){
		if(socket != null && !socket.isClosed()){
			System.out.println(socket.getInetAddress().getHostAddress() + " --> " + msg);
		}else{
			System.out.println("Not connected --> " + msg);

		}
	}

	public void stop(){
		running= false;
	}
	
	public boolean isLogged() {
		return logged;
	}

	public Socket getSocket() {
		return socket;
	}

	public DataOutputStream getOut() {
		return out;
	}

	public DataInputStream getIn() {
		return in;
	}

	public boolean isRunning() {
		return running;
	}

	public List<byte[]> getSub() {
		return sub;
	}

	public boolean isListenAll() {
		return listenAll;
	}


}
