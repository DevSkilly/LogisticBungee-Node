package fr.hugo4715.logisticgames.node.util;

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
			
			try(Jedis j = Node.getInstance().getJedis()){
				j.publish(Node.getInstance().getConfig().getJSONObject("redis").getString("prefix") + ":bungee", Node.getInstance().getId().toString() + "STATUS" + Node.getInstance().getLoad() + "&&" + Node.getInstance().getConfig().getJSONObject("node").getInt("maxLoad"));
			}
			try {
				sleep(wait);
			} catch (InterruptedException ignored) {}
		}
	}

}
