package fr.hugo4715.crackedbroker.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.hugo4715.crackedbroker.commons.MessageReceivedEvent;
import fr.hugo4715.crackedbroker.commons.PubSub;
import fr.hugo4715.crackedbroker.commons.PubSubListener;

public class CrackedBrokerClient implements PubSub{

	private String password;
	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;
	
	private Map<PubSubListener,byte[]> listeners;
	private List<PubSubListener> globalListeners;
	private boolean running = true;
	
	public CrackedBrokerClient(String ip, int port, String password) throws UnknownHostException, IOException {
		this.password = password;
		socket = new Socket(ip,port);
		out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		
		listeners = new HashMap<>();
		globalListeners = new ArrayList<>();
		
		out.writeInt(password.getBytes().length);
		out.write(password.getBytes());
		out.flush();
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				while(running){
					try{
						byte packetType = in.readByte();
						
						switch(packetType){
							case 0:
								System.out.println("Received msg");
								
								int channelSize = in.readInt();
								byte[] channelData = new byte[channelSize];
								in.readFully(channelData, 0, channelData.length);
								
								int msgSize = in.readInt();
								byte[] msgData = new byte[msgSize];
								in.readFully(msgData, 0, msgData.length);
								
								for(PubSubListener l : globalListeners){
									l.onMessage(new MessageReceivedEvent(channelData, msgData));
								}
								
								for(PubSubListener l : listeners.keySet()){
									if(Arrays.toString(listeners.get(l)).equals(Arrays.toString(channelData))){
										l.onMessage(new MessageReceivedEvent(channelData, msgData));
									}
								}
								break;
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	@Override
	public void publish(byte[] channel, byte[] message) {
		System.out.println("sending...");
		try {
			out.writeByte((byte)3);
			out.writeInt(channel.length);
			out.write(channel);
			
			out.writeInt(message.length);
			out.write(message);
			out.flush();
			System.out.println("Sended msg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isListening(byte[] channel){
		if(!globalListeners.isEmpty())return true;
		
		for(PubSubListener l : listeners.keySet()){
			if(Arrays.toString(listeners.get(l)).equals(Arrays.toString(channel))){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void subscribe(PubSubListener listener) {
		try {
			out.writeByte((byte)1);
			out.flush();
			globalListeners.add(listener);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void subscribe(byte[] channel, PubSubListener listener) {
		try {
			out.writeByte((byte)0);
			out.writeInt(channel.length);
			out.write(channel);
			out.flush();
			listeners.put(listener,channel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
