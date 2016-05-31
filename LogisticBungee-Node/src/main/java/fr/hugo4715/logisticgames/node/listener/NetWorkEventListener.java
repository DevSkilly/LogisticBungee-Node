package fr.hugo4715.logisticgames.node.listener;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import fr.hugo4715.eventmanager2.EventManager;
import fr.hugo4715.eventmanager2.event.Event;
import fr.hugo4715.logisticgames.node.Node;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class NetWorkEventListener extends JedisPubSub {

	
	public NetWorkEventListener(final String channel) {
		final JedisPubSub pubsub = this;
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				try(Jedis j = Node.getInstance().getJedis()){
					j.subscribe(pubsub, channel);
				}
			}
		}).start();
	}
	
	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		System.out.println("Successfully started NetWorkEventListener on channel " + channel);
	}
	
	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
		System.out.println("NetWorkEventListener is not listening anymore");
	}
	
	@Override
	public void onMessage(String channel, String message) {
		byte[] data = message.getBytes();
		
		ObjectInputStream stream = null;
		try {
			stream = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(data)));
			Object obj = stream.readObject();
			
			if(obj instanceof  Event){
				System.out.println("Calling event " + obj.getClass().getSimpleName() + " (with data " + obj.toString() + ")");
				EventManager.getInstance().callEvent((Event) obj);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			if(stream != null)
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
	}
}
