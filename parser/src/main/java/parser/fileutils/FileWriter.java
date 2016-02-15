package parser.fileutils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.InvalidPathException;
import java.util.List;
import java.util.Iterator;
import org.apache.log4j.Logger;

public class FileWriter {
	
	private FileSystem system = null;
	private static FileWriter writer = null;
	private static final Logger log = Logger.getLogger(FileWriter.class);
	private static final String NEW_LINE = "\n";

	private FileWriter() {
		system = FileSystems.getDefault();
	}

	public static FileWriter getInstance() {
		if (writer == null)
			writer = new FileWriter();
		return writer;
	}

	public boolean write(String path, String header, List<String> content) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(header);
		buffer.append(NEW_LINE);
		Iterator<String> it = content.iterator();
		while (it.hasNext()) {
			buffer.append(it.next());
			buffer.append(NEW_LINE);
		}
		

		try {
			Path savedPath = system.getPath(path);
			Files.write(savedPath, buffer.toString().getBytes(), StandardOpenOption.CREATE);
		} catch (InvalidPathException 	e) {
			log.error("the path string cannot be converted", e);
			return false;
		} catch(UnsupportedOperationException e) {
			log.error("an unsupported option is specified", e);
			return false;
		} catch (IOException e) {
			log.error("Error occurs reading the file " + path,e);
			return false;
		} catch (SecurityException e) {
			log.error("The security manager restricts the reading over the file "+path,e);
			return false;
		}
		return true;
	}
}