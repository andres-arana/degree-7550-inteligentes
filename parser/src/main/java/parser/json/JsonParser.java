package parser.json;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;
import parser.fileutils.FileReader;
import parser.fileutils.FileLocation;
import parser.utils.StringUtils;

public class JsonParser {

	private XMLStreamReader reader = null;
	private static JsonParser parser = null;
	private File[] files = null;
	private List<String> headers = null;
	private static final Logger log = Logger.getLogger(JsonParser.class);
	private static final String EXTENSION = "txt";

	private JsonParser() {
		files = null;
	}

	public static JsonParser getInstance() {
		if (parser == null)
			parser = new JsonParser();
		return parser;
	}

	public void setContainerFolder(String folderPath) {
		//recover all files
		FileLocation locate = FileLocation.getInstance();
		File folder = new File(locate.getLocation(folderPath));

		if (!folder.exists()) {
			log.error("Folder <" + folder.getPath() + "> does not exists.");
		}

		files = folder.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getPath().endsWith(EXTENSION);
			}
		});
		log.debug("Files listed <"+files.length+"> inside folder <"+folder.getPath()+">");
	}

	public void setFilteredHeaders(List<String> headers) {
		this.headers = headers;
	}

	public List<String> parse() {
		if ((files == null) || (headers == null)) {
			log.error("Parser can not start");
			return null;
		}
		
		log.debug("Instancing the FileReader");
		FileReader fileReader = FileReader.getInstance();

		List<String> result = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			File json = files[i];
			log.debug("Reading file <" + json.getPath() + ">");

			//each line contains a json expression
			List<String> jsonListInsideFile = fileReader.readLines(json.getPath());

			//TODO read json
			for (int j = 0; j < jsonListInsideFile.size(); j++) {
				String message = jsonListInsideFile.get(j);
				try {
					//log.debug("Mapping message <"+message+">");
					List<String> o = mapping(toJsonObject(message));
					if (o!=null && !o.isEmpty()) result.addAll(o);
				} catch (JSONException e) {
					log.error("Occurred when mapping file <" + json.getPath() + "> with message: " + message, e);
					throw new RuntimeException(e);
				} catch (XMLStreamException e) {
					log.error("Occurred when mapping file <" + json.getPath() + "> with message: " + message, e);
					throw new RuntimeException(e);
				}
			}

		}
		log.info("Parse Rows <"+result.size()+">");
		return result;
	}

	private JSONObject toJsonObject(String message) {
		JSONObject json = null;
		try {
			log.debug("Parsing message to JSONObject");


			//FIX accept just if starts with "{"
			if (!message.startsWith("{")) {
				log.warn("Message <"+message+">is not a json! SKIPPING!");
				return null;
			}


			//TODO validate how to remove it
			//This is necessary to parse it.
			message = "{\"json\":"+message+"}";
			json = new JSONObject(message);
		} catch (JSONException e) {
			log.error("Parsing the string to json", e);
		}
		return json;
	}

	private List<String> mapping(JSONObject obj) throws JSONException, XMLStreamException {
		if (obj == null) return null;

		List<String> res = new ArrayList<String>();
		StringBuffer buffer = new StringBuffer();
		//Configuration config = new Configuration();
        MappedNamespaceConvention con = new MappedNamespaceConvention();
        reader = new MappedXMLStreamReader(obj, con);

		boolean read = false;
		int event = reader.next(); //the first is not important (contains the json)
		Deque<String> complexName = new ArrayDeque<String>();

		ParserContainer container = new ParserContainer(headers);
		String key = "";

        while (reader.hasNext()) {
        	
        	event = reader.next();
        	log.debug("Printing next() in reader <"+event+">");
        	
        	if (event == XMLStreamReader.START_ELEMENT) {
        		String elementName = reader.getName().getLocalPart();
        		complexName.push(elementName);
        		key = StringUtils.join(complexName, ".", true);
        		log.debug("-> ElementName <"+elementName+">, complexName <"+key+">");
        		if (headers.contains(key)) {
        			log.debug("-> ElementName <"+key+"> matches with headers!");
        			read = true;
        		}
        	}
        	if (event == XMLStreamReader.CHARACTERS) {
        		String text = reader.getText();
        		log.debug("-> Storing the text <"+text+">");

				if (read) {
					if (!container.isValid(key)) {
						String row = container.toCSV();
						log.info("Not valid, saving <"+row+">");
						res.add(row);
						container.clean();
					}
					container.save(key, text);
				}
        	}
        	if (event == XMLStreamReader.END_ELEMENT) {
        		if (!complexName.isEmpty()) complexName.pop();
        		log.debug("-> Reseting the read value");
        		read = false;
        	}
        	if (event == XMLStreamReader.END_DOCUMENT) {
        		log.debug("-> END of the DOCUMENT");
        	}
        }

        log.info("Mapping Rows <"+res.size()+">");
        reader.close();
        return res;
	}
	
}