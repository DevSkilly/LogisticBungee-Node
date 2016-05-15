package fr.hugo4715.crackedbroker.commons;

public interface PubSub {
	public void publish(byte[] channel, byte[] message);
	
	public void subscribe(PubSubListener listener);
	public void subscribe(byte[] channel, PubSubListener listener);
}
