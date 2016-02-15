package parser.main;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import parser.fileutils.FileReader;
import parser.fileutils.FileWriter;
import parser.json.JsonParser;
import parser.utils.StringUtils;


import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;


/**
* Test class
*/
class Test {
	
	private static final Logger log = Logger.getLogger(Main.class);

	public static void test() throws Exception {
        JSONObject obj = new JSONObject("{ \"result\": { \"took\" : 507, \"timed_out\" : false,\"_shards\":{\"total\":12,\"successful\":12,\"failed\":0},\"hits\":{\"total\":0,\"max_score\":null,\"hits\":[]}}}");

       	//JSONObject obj = new JSONObject("{ \"root\" : { \"child1\" : 123, \"child3\" : \"child4\" } }");
        MappedNamespaceConvention con = new MappedNamespaceConvention();
        XMLStreamReader reader = new MappedXMLStreamReader(obj, con);
        
        while (reader.hasNext()) {
        	try {
	        	System.out.println(reader.next());
	            System.out.println(reader.getName().getLocalPart());
            
            	System.out.println(reader.getText());
            } catch (Exception e) {
            	log.error(e);
            }
            
        }

        
    }
}