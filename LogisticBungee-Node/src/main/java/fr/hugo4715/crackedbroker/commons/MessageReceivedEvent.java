package fr.hugo4715.crackedbroker.commons;

public class MessageReceivedEvent {
	private byte[] channel;
	private byte[] message;
	
	public MessageReceivedEvent(byte[] channel, byte[] message) {
		this.channel = channel;
		this.message = message;
	}

	public byte[] getChannel() {
		return channel;
	}

	public void setChannel(byte[] channel) {
		this.channel = channel;
	}

	public byte[] getMessage() {
		return message;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}
	
	
}
