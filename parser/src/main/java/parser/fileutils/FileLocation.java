package parser.fileutils;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class FileLocation {
	
	private FileSystem system = null;
	private static FileLocation location = null;

	private FileLocation() {
		system = FileSystems.getDefault();
	}

	public static FileLocation getInstance() {
		if (location == null)
			location = new FileLocation();
		return location;
	}

	public String getLocation(String path) {
		Path p = system.getPath(path);
		return p.toString();
	}
}