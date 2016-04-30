package fr.hugo4715.logisticgames.node.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import fr.hugo4715.logisticgames.node.Node;
import fr.hugo4715.logisticgames.node.util.Callback;
import fr.hugo4715.logisticgames.node.util.IOUtils;
import fr.hugo4715.logisticgames.node.util.SimpleFileNameFilter;
import fr.hugo4715.logisticgames.node.util.SocketUtils;

public class ServerManager {
	private static ServerManager instance;


	public static ServerManager getInstance() {
		if (null == instance) {
			instance = new ServerManager();
		}
		return instance;
	}

	private File serverDir;
	private File dataDir;
	private ExecutorService pool;

	public List<Server> getServers() {
		return servers;
	}

	private List<Server> servers;

	private ServerManager() {
		servers = Collections.synchronizedList(new ArrayList<Server>());;
		serverDir = new File("servers" + File.separator);
		dataDir = new File("data" + File.separator);
		IOUtils.deleteDir(dataDir);
		serverDir.mkdir();
		dataDir.mkdir();

		pool = Executors.newFixedThreadPool(1); //number of server startup at once
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				while(true){
					Iterator<Server> i = servers.iterator();
					while(i.hasNext()){
						Server srv = i.next();
						
						if(!srv.isAlive()){
							srv.kill();
							i.remove();
						}
					}
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ignored) {}
				}
			}
		}).start();
		
	}

	public void startServer(final String type, final Callback<ServerStartedCallBack> callback){
		final JSONObject gm;
		/*
		 * Type exist check
		 */
		try{
			gm =Node.getInstance().getConfig().getJSONObject("gamemodes").getJSONObject(type);
		}catch(JSONException e){
			System.out.println("type " + type + "not found");
			callback.done(null, new RuntimeException("404: server type not found"));
			return;
		}

		System.out.println("Creating new server of type " + type);
		pool.submit(new Runnable(){
			@Override
			public void run() {
				System.out.println("Starting creation");

				File path = new File(serverDir.getAbsolutePath() + File.separator + gm.getString("path"));
				int port = getPort();
				String srv = type + "-" + String.valueOf(port);
				File data = new File(dataDir.getAbsolutePath() + File.separator + srv);

				data.mkdir();

				try {
					System.out.println("Copying");
					IOUtils.copy(path, data);
					System.out.println("Finished copy");
				} catch (IOException e) {
					callback.done(null, e);
					return;
				}
				System.out.println("Starting");
				for(String f : data.list(new SimpleFileNameFilter("jar"))){
					System.out.println("jar file is " + f);
					try {
						String args = "java&&-jar&&" + f + "&&--port&&" + String.valueOf(port) ;

						System.out.println("Creating new server process with cmd " + args.replace("&&", " "));

						ProcessBuilder pb = new ProcessBuilder().command(args.split("&&"));
						pb.directory(data);
						System.out.println("Path: " + pb.directory().getAbsolutePath());
						System.out.println("Arguments: " + args.replace("&&", " "));
						Process process = pb.start();	
						servers.add(new Server(process, srv, type));
						System.out.println("Server successfully created!");
						callback.done(new ServerStartedCallBack(srv,port, data), null);
						return;
					} catch (Exception e) {
						callback.done(null, e);
						return;
					}
				}
			}
		});
	}


	public void stopAll(){
		for(Server srv : servers){
			srv.getProcess().destroyForcibly();
		}
	}

	public File getDataDir() {
		return dataDir;
	}

	public class ServerStartedCallBack{
		private Integer port;
		private String name;
		private File data;
		public ServerStartedCallBack(String name, int port, File data) {
			this.port = port;
			this.data = data;
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getPort() {
			return port;
		}
		public void setPort(Integer port) {
			this.port = port;
		}
		public File getData() {
			return data;
		}
		public void setData(File data) {
			this.data = data;
		}


	}
	
	public int getPort(){
		try {
			ServerSocket s = new ServerSocket(0);
			s.close();
			return s.getLocalPort();
		} catch (IOException e) {
			System.out.println("No free port found...");
			return -1;
		}
	}

	@Deprecated
	private Integer _getPort(){
		Random r = new Random();
		int choosed = r.nextInt(65500);
		while(choosed < 1024 || !SocketUtils.isAvailable(choosed)){ //port is not available or is below 1024
			choosed = r.nextInt(65500);
		}
		System.out.println("Choosed port " + choosed);
		return choosed;
	}
}
