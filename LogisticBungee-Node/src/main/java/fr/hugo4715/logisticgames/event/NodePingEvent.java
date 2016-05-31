package fr.hugo4715.logisticgames.event;

import java.util.UUID;

import fr.hugo4715.eventmanager2.event.Event;

public class NodePingEvent extends Event {

	private UUID id;
	private int load;
	private int maxLoad;
	
	public NodePingEvent(UUID id, int load, int maxLoad) {
		this.id = id;
		this.load = load;
		this.maxLoad = maxLoad;
	}
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public int getLoad() {
		return load;
	}

	public void setLoad(int load) {
		this.load = load;
	}

	public int getMaxLoad() {
		return maxLoad;
	}

	public void setMaxLoad(int maxLoad) {
		this.maxLoad = maxLoad;
	}

	

	@Override
	public String getEventName() {
		return "NodePing";
	}
	
	

}
