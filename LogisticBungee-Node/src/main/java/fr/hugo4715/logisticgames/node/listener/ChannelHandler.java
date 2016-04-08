package fr.hugo4715.logisticgames.node.listener;

import fr.hugo4715.logisticgames.node.server.ServerManager;
import fr.hugo4715.logisticgames.node.server.ServerManager.ServerStartedCallBack;
import fr.hugo4715.logisticgames.node.util.Callback;
import redis.clients.jedis.JedisPubSub;

public class ChannelHandler extends JedisPubSub {
	public ChannelHandler() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onMessage(String channel, String message) {
		System.out.println("Received " + message + " on channel " + channel);
		if(message.startsWith("START")){
			message = message.substring(5);
			
			String gm = message;
			System.out.println("Creating server with type " + gm);
			ServerManager.getInstance().startServer(gm, new Callback<ServerStartedCallBack>(){

				@Override
				public void done(ServerStartedCallBack result, Throwable error) {
					if(result == null && error != null){
						System.err.println("Error while starting server: " + error.getMessage());
						error.printStackTrace();
					}else{
						System.out.println("Started server on port " + result.getPort());
					}
				}
				
			});
		}
	}
}
