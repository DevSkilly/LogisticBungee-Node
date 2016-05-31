package fr.hugo4715.logisticgames.node.listener;

import fr.hugo4715.eventmanager2.EventManager;
import fr.hugo4715.eventmanager2.handler.EventHandler;
import fr.hugo4715.eventmanager2.handler.Listener;
import fr.hugo4715.logisticgames.event.ServerRequestedEvent;
import fr.hugo4715.logisticgames.node.Node;
import fr.hugo4715.logisticgames.node.server.ServerManager;
import fr.hugo4715.logisticgames.node.server.ServerManager.ServerStartedCallBack;
import fr.hugo4715.logisticgames.node.util.Callback;

public class EventListener implements Listener{
	
	public EventListener() {
		EventManager.getInstance().registerEvents(this);
		System.out.println("Event Listener is listening for event");
	}
	
	@EventHandler
	public void onServerStartRequest(ServerRequestedEvent e){
		if(!e.getTargetNode().toString().equals(Node.getInstance().getId().toString()))return;
		
		ServerManager.getInstance().startServer(e.getGamemode(), new Callback<ServerStartedCallBack>(){

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
