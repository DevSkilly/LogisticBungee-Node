package fr.hugo4715.logisticgames.node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import fr.hugo4715.crackedbroker.client.CrackedBrokerClient;
import fr.hugo4715.crackedbroker.commons.PubSub;
import fr.hugo4715.logisticgames.node.listener.ChannelHandler;
import fr.hugo4715.logisticgames.node.server.ServerManager;
import fr.hugo4715.logisticgames.node.util.Checker;
import sun.misc.IOUtils;

public class Node {
	private static Node instance;

	public static Node getInstance() {
		if (null == instance) {
			instance = new Node();
		}
		return instance;
	}

	protected File configFile = new File("config.json");

	protected JSONObject config;

	protected CrackedBrokerClient broker;
	
	protected UUID id;
	
	protected int load = 0;
	
	private Node() {
		id = UUID.randomUUID();
		try {
			setupConfig();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			System.err.println("Could not setup config, exiting...");
			return;
		}
		
		//in case it collide
		if(config.getJSONObject("node").getString("uuid") != null && !config.getJSONObject("node").getString("uuid").isEmpty()){
			try{
				id = UUID.fromString(config.getJSONObject("node").getString("uuid"));
			}catch(IllegalArgumentException ignored){}
		}
		try {
			broker = new CrackedBrokerClient(config.getJSONObject("broker").getString("host"), config.getJSONObject("broker").getInt("port"),config.getJSONObject("broker").getString("password"));
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		
		new Checker(config.getJSONObject("node").getInt("keepAliveTime")*900).start();
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				getPubSub().subscribe((getConfig().getJSONObject("redis").getString("prefix") + ":node:" + id.toString()).getBytes(StandardCharsets.UTF_8),new ChannelHandler());
			}
			
		}).start();
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
			@Override
			public void run() {
				ServerManager.getInstance().stopAll();
			}
		}));
	}

	public UUID getId() {
		return id;
	}

	public JSONObject getConfig() {
		return config;
	}

	public PubSub getPubSub() {
		return broker;
	}
	
	public void incrementLoad(int i){
		load += i;
	}
	
	public void decrementLoad(int i){
		load -= i;
	}
	public int getLoad() {
		return load;
	}

	private void setupConfig() throws JSONException, FileNotFoundException, IOException {
		if(configFile.exists()){
			System.out.println("Loading configuration...");
			config = new JSONObject(new String(IOUtils.readFully(new FileInputStream(configFile), Integer.MAX_VALUE, false)));
			System.out.println("Finished reading config");
		}else{
			System.out.println("Creating configuration...");
			try (InputStream in = this.getClass().getResourceAsStream("/config.json"); FileOutputStream out = new FileOutputStream(configFile)) {
				System.out.println(in);
				out.write(IOUtils.readFully(in, -1, false));
			}
			config = new JSONObject(new String(IOUtils.readFully(new FileInputStream(configFile), Integer.MAX_VALUE, false)));
			System.out.println("Created and loaded config");
		}
	}

	public static void main(String[] args) {
		Node.getInstance();
	}
}
