package fr.hugo4715.logisticgames.node.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Date;

import fr.hugo4715.logisticgames.node.Node;

public class ServerLogService implements Runnable{
	private BufferedReader stream;
	private String name;
	private Date startup;
	
	public ServerLogService(InputStream stream, String name) {
		this.name = name;
		this.stream = new BufferedReader(new InputStreamReader(stream));
		startup = Date.from(Instant.now());
	}

	@Override
	public void run() {
		System.out.println("Started server log service");
		try {
			String s = "";
			while((s = stream.readLine()) != null){
				if(Node.getInstance().getConfig().getJSONObject("node").getBoolean("logServer"))System.out.println(s);
//				try(Jedis j = Node.getInstance().getJedis()){
//					j.lpush(Node.getInstance().getConfig().getJSONObject("redis").getString("prefix") + ":srv-log:" + name + startup.toString(), s);
//					j.expire(Node.getInstance().getConfig().getJSONObject("redis").getString("prefix") + ":srv-log:" + name + startup.toString(), 60);
//				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}//will just stop the log service, not an issue since it is only for info
	}
}
