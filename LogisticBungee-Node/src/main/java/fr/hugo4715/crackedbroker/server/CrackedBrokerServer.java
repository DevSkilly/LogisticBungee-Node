package fr.hugo4715.crackedbroker.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.hugo4715.crackedbroker.commons.MessageReceivedEvent;
import fr.hugo4715.crackedbroker.commons.PubSub;
import fr.hugo4715.crackedbroker.commons.PubSubListener;

public class CrackedBrokerServer implements PubSub, Runnable{
	private ServerSocket socket;

	private String password;

	private List<PubSubListener> globalListeners;
	private Map<PubSubListener, byte[]> listeners;

	private ExecutorService service;
	private boolean running = true;
	private List<ClientHandler> clients;
	public CrackedBrokerServer(String password, String ip, int port, int maxConnection) throws IOException {
		this.password = password;
		socket = new ServerSocket();
		socket.bind(new InetSocketAddress(ip, port));
		listeners = new HashMap<>();
		globalListeners = new ArrayList<>();
		clients = new ArrayList<>();

		if(maxConnection >= 1){
			service = Executors.newFixedThreadPool(maxConnection);
		}else{
			service = Executors.newCachedThreadPool();
			System.out.println("Warning: Using cached thread pool, you should consider using a limit to the maximum connection to limit overload");
		}

		new Thread(this).start();
	}

	public List<PubSubListener> getGlobalListeners() {
		return globalListeners;
	}

	public void setGlobalListeners(List<PubSubListener> globalListeners) {
		this.globalListeners = globalListeners;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public ServerSocket getSocket() {
		return socket;
	}

	public Map<PubSubListener, byte[]> getListeners() {
		return listeners;
	}

	public List<ClientHandler> getClients() {
		return clients;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void run(){
		while(running){
			try{
				ClientHandler c = new ClientHandler(this,socket.accept());
				service.submit(c);
				clients.add(c);
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}

	public String getPassword() {
		return password;
	}

	@Override
	public void publish(byte[] channel, byte[] message) {

		/*
		 * Server side
		 */
		for(PubSubListener l : globalListeners){
			l.onMessage(new MessageReceivedEvent(channel, message));
		}

		for(PubSubListener l : listeners.keySet()){
			if(!listeners.get(l).equals(channel))continue;
			l.onMessage(new MessageReceivedEvent(channel, message));
		}


		/*
		 * Client side
		 */
		for(ClientHandler c : clients){
			if(!c.isRunning() || !c.isLogged())continue;

			System.out.println("Should log: " + c.isListening(channel));
			
			if(!c.isListening(message))continue;

			try {
				c.sendMessage(channel, message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	public void subscribe(PubSubListener listener) {
		globalListeners.add(listener);
		System.out.println("Subscribed with global listener");
	}

	@Override
	public void subscribe(byte[] channel, PubSubListener listener) {
		listeners.put(listener,channel);
		System.out.println("Subscribed with listener " + new String(channel));
	}
}
