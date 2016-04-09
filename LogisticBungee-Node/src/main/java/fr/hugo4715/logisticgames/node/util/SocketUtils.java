package fr.hugo4715.logisticgames.node.util;

import java.io.IOException;
import java.net.ServerSocket;

public class SocketUtils {
	public static boolean isAvailable(int port){
	    try {
	        // ServerSocket try to open a LOCAL port
	        new ServerSocket(port).close();
	        // local port can be opened, it's available
	        return false;
	    } catch(IOException e) {
	        // local port cannot be opened, it's in use
	        return true;
	    }
	}
}
