package parser.main;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.BasicConfigurator;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import parser.fileutils.FileReader;
import parser.fileutils.FileWriter;
import parser.json.JsonParser;
import parser.utils.StringUtils;


/**
* Main class
*/
class Main {
	
	private static final Logger log = Logger.getLogger(Main.class);

	private static List<String> fields;


	public static void main(String[] args) {
		// Set up a simple configuration that logs on the console.
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);

		log.info("Initializing Json Parser");
		log.info("Reading headers/fields");
		fields = FileReader.getInstance().readLines("./src/main/resources/fields.txt");

		log.info("STARTING parser");
		JsonParser parser = JsonParser.getInstance();
		parser.setContainerFolder("./src/main/resources/proc");
		parser.setFilteredHeaders(fields);
		List<String> parsed = parser.parse();
		log.info("ENDING parser");

		if (fields == null) {
			return;
		}

		String savingPath = "./target/result.csv";
		log.info("Saving file " + savingPath);

		String headerJoined = StringUtils.join(fields, ",");

		FileWriter.getInstance().write(savingPath, headerJoined, parsed);

		// try {
		// 	Test.test();
		// } catch (Exception e) {
		// 	log.error(e);
		// }
	}
}