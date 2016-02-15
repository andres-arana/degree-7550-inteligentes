package parser.fileutils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import org.apache.log4j.Logger;

public class FileReader {
	
	private FileSystem system = null;
	private static FileReader reader = null;
	private static final Logger log = Logger.getLogger(FileReader.class);

	private FileReader() {
		system = FileSystems.getDefault();
	}

	public static FileReader getInstance() {
		if (reader == null)
			reader = new FileReader();
		return reader;
	}

	public List<String> readLines(String path) {
		List<String> fields = null;
		Path fieldsPath = system.getPath(path);
		try {
			fields = Files.readAllLines(fieldsPath, Charset.defaultCharset());
			log.debug("Reading <"+fields.size()+"> lines in file <"+path+">");
		} catch (IOException e) {
			log.error("Error occurs reading the file "+path,e);
		} catch (SecurityException e) {
			log.error("The security manager restricts the reading over the file " + path,e);
		}
		return fields;
	}
}