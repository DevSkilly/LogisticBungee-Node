package fr.hugo4715.logisticgames.node.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ServerLogService implements Runnable{
	private BufferedReader stream;
	
	public ServerLogService(InputStream stream) {
		super();
		this.stream = new BufferedReader(new InputStreamReader(stream));
	}



	@Override
	public void run() {
		try {
			String s = "";
			while((s = stream.readLine()) != null){
				//do something?
			}
		} catch (IOException e) {
		}
	}

}
