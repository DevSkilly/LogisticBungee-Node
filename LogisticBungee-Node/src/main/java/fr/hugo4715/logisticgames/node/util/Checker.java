package fr.hugo4715.logisticgames.node.util;

import org.json.JSONObject;

import fr.hugo4715.logisticgames.node.Node;
import redis.clients.jedis.Jedis;

public class Checker extends Thread implements Runnable {

	private long wait;
	public Checker(int wait) {
		this.wait = wait;
		
	}
	@Override
	public void run() {
		while(true){
			JSONObject msg = new JSONObject();
			msg.put("uuid", Node.getInstance().getId().toString());
			msg.put("cmd", "STATUS");
			msg.put("load", Node.getInstance().getLoad());
			msg.put("maxLoad", Node.getInstance().getConfig().getJSONObject("node").getInt("maxLoad"));
			
			try(Jedis j = Node.getInstance().getJedis()){
				j.publish(Node.getInstance().getConfig().getJSONObject("redis").getString("prefix") + ":bungee", msg.toString());
			}
			try {
				sleep(wait);
			} catch (InterruptedException ignored) {}
		}
	}

}
