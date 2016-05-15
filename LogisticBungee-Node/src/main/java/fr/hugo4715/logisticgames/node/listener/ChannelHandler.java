package fr.hugo4715.logisticgames.node.listener;

import java.nio.charset.StandardCharsets;

import org.json.JSONException;
import org.json.JSONObject;

import fr.hugo4715.crackedbroker.commons.MessageReceivedEvent;
import fr.hugo4715.crackedbroker.commons.PubSubListener;
import fr.hugo4715.logisticgames.node.server.ServerManager;
import fr.hugo4715.logisticgames.node.server.ServerManager.ServerStartedCallBack;
import fr.hugo4715.logisticgames.node.util.Callback;

public class ChannelHandler implements PubSubListener{
	@Override
	public void onMessage(MessageReceivedEvent e) {
		System.out.println("Received " + new String(e.getMessage(), StandardCharsets.UTF_8) + " on channel " + new String(e.getChannel(), StandardCharsets.UTF_8));

		JSONObject msg;
		try{
			msg = new JSONObject(new String(e.getMessage(), StandardCharsets.UTF_8));
		}catch(JSONException ex){
			ex.printStackTrace();
			return;
		}

		if(msg.getString("cmd").equals("START")){

			String gm = msg.getString("gamemode");

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
