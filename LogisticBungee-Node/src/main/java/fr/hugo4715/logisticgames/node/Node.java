package fr.hugo4715.logisticgames.node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
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

	protected JedisPool jedis;
	
	protected UUID id;

	
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
		if(config.getString("uuid") != null && !config.getString("uuid").isEmpty()){
			try{
				id = UUID.fromString(config.getString("uuid"));
			}catch(IllegalArgumentException ignored){}
		}
		jedis = new JedisPool(config.getJSONObject("redis").getString("host"), config.getJSONObject("redis").getInt("port"));
		
		register();
	}

	private void register() {
		try(Jedis j = getJedis()){
			j.publish(config.getJSONObject("redis").getString("prefix") + ":bungee", id.toString() + "NREGISTER");
		}
	}

	public JSONObject getConfig() {
		return config;
	}

	public Jedis getJedis() {
		Jedis j = jedis.getResource();
	
		if(config.getJSONObject("redis").getString("pass") != null && !config.getJSONObject("redis").getString("pass").isEmpty()){
			j.auth(config.getJSONObject("redis").getString("pass"));
		}
		return j;
	}

	private void setupConfig() throws JSONException, FileNotFoundException, IOException {
		if(configFile.exists()){
			System.out.println("Loading configuration...");
			config = new JSONObject(new String(IOUtils.readFully(new FileInputStream(configFile), Integer.MAX_VALUE, false)));
			System.out.println("Finished reading config");
		}else{
			System.out.println("Creating configuration...");
			try (InputStream in = this.getClass().getResourceAsStream("config.yml"); FileOutputStream out = new FileOutputStream(configFile)) {
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
