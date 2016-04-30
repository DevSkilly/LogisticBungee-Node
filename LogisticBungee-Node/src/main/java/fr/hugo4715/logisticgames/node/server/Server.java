package fr.hugo4715.logisticgames.node.server;

import java.io.File;

import fr.hugo4715.logisticgames.node.Node;
import fr.hugo4715.logisticgames.node.util.IOUtils;


public class Server{
	
	private Process process;
	private ServerLogService logService;
	private String name;
	private long  startupTime;
	private int memoryUsed;
	
	
	public Server(Process process, String name, String gm) {
		startupTime = System.currentTimeMillis();
		this.process = process;
		this.logService = new ServerLogService(process.getInputStream(), name);
		this.name = name;
		
		new Thread(logService).start();
		memoryUsed = Node.getInstance().getConfig().getJSONObject("gamemodes").getJSONObject(gm).getInt("ram");
		Node.getInstance().incrementLoad(memoryUsed);
	}
	
	
	public long getStartupTime() {
		return startupTime;
	}


	public Process getProcess() {
		return process;
	}


	public ServerLogService getLogService() {
		return logService;
	}


	public String getName() {
		return name;
	}

	
	public boolean isAlive(){
		try{
			process.exitValue();
		}catch(Exception e){
			return true;
		}
		return false;
	}
	
	public void kill(){
		process.destroyForcibly();
		IOUtils.deleteDir(new File(ServerManager.getInstance().getDataDir().getAbsolutePath() + File.separator + name));
		Node.getInstance().decrementLoad(memoryUsed);
		System.out.println("Deleted and killed server " + name);
	}
	
	
}
