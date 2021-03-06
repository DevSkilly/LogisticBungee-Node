package fr.hugo4715.logisticgames.node.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
	public static void copy(File sourceLocation, File targetLocation) throws IOException {
		System.out.println("Copying " + sourceLocation.getAbsolutePath() + " to " + targetLocation.getAbsolutePath());
		a(sourceLocation, targetLocation);
		
	}
	
	private static void a(File sourceLocation, File targetLocation) throws IOException{
		if (sourceLocation.isDirectory()) {
			copyDirectory(sourceLocation, targetLocation);
		} else {
			copyFile(sourceLocation, targetLocation);
		}
	}

	private static void copyDirectory(File source, File target) throws IOException {
		if (!target.exists()) {
			target.mkdir();
		}

		for (String f : source.list()) {
			a(new File(source, f), new File(target, f));
		}
	}

	private static void copyFile(File source, File target) throws IOException {        
		try (
				InputStream in = new FileInputStream(source);
				OutputStream out = new FileOutputStream(target)
				) {
			byte[] buf = new byte[1024];
			int length;
			while ((length = in.read(buf)) > 0) {
				out.write(buf, 0, length);
			}
		}
	}

	public static void deleteDir(File file) {
		System.out.println("Deleting " + file.getAbsolutePath());

		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				deleteDir(f);
			}
		}
		file.delete();
	}
}
