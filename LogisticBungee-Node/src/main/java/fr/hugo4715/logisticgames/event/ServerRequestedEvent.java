package fr.hugo4715.logisticgames.event;

import java.util.UUID;

import fr.hugo4715.eventmanager2.event.Event;

public class ServerRequestedEvent extends Event {
	private static final long serialVersionUID = 1L;

	private UUID targetNode;
	private String gamemode;
	
	public ServerRequestedEvent(UUID id, String gamemode) {
		this.targetNode = id;
		this.gamemode = gamemode;
	}

	public UUID getTargetNode() {
		return targetNode;
	}

	public void setTargetNode(UUID targetNode) {
		this.targetNode = targetNode;
	}

	public String getGamemode() {
		return gamemode;
	}

	public void setGamemode(String gamemode) {
		this.gamemode = gamemode;
	}

	@Override
	public String getEventName() {
		return getClass().getSimpleName();
	}
	
	@Override
	public String toString() {
		return "Requesting a " + gamemode + " server to node " + getTargetNode().toString();  
	}

}
