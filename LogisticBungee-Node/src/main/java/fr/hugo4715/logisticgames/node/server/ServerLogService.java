package fr.hugo4715.logisticgames.node.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import fr.hugo4715.logisticgames.node.Node;

public class ServerLogService implements Runnable{
	private BufferedReader stream;
	
	public ServerLogService(InputStream stream) {
		super();
		this.stream = new BufferedReader(new InputStreamReader(stream));
	}

	@Override
	public void run() {
		System.out.println("Started server log service");
		try {
			String s = "";
			while((s = stream.readLine()) != null){
				if(Node.getInstance().getConfig().getJSONObject("node").getBoolean("logServer"))System.out.println(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}//will just stop the log service, not an issue since it is only for testing
	}
}
