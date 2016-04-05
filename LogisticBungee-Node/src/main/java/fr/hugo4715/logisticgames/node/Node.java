package fr.hugo4715.logisticgames.node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

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

	
	private Node() {
		try {
			setupConfig();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			System.err.println("Could not setup config, exiting...");
			return;
		}
		jedis = new JedisPool(config.getJSONObject("redis").getString("host"), config.getJSONObject("redis").getInt("port"));
}

	public JSONObject getConfig() {
		return config;
	}

	public JedisPool getJedis() {
		return jedis;
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
		
	}
}
