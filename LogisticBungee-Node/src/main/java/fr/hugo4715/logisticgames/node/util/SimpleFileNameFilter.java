package fr.hugo4715.logisticgames.node.util;

import java.io.File;
import java.io.FilenameFilter;

public class SimpleFileNameFilter implements FilenameFilter {
	
	private String[] type;
	
	public SimpleFileNameFilter(String... types) {
		super();
		this.type = types;
	}

	@Override
	public boolean accept(File dir, String name) {
		
		for(String key : type){
			if(name.endsWith(key))return true;
		}
		return false;
	}

}
