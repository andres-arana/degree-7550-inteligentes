package parser.json;

import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.List;

public class ParserContainer {
	
	private final Map<String, Object> map = new TreeMap<String, Object>();
	private List<String> keysSorted = null;
	private static final String SEPARATOR = ",";
	private String oldContainer = null;

	public ParserContainer(List<String> list) {
		keysSorted = list;
		this.clean();
	}

	public void save(String key, Object o) {
		map.put(key, o);
	}

	public boolean isValid(String key) {
		return (map.get(key) == "");
	}

	public String toCSV() {
		StringBuffer b = new StringBuffer();
		Iterator<String> it = keysSorted.iterator();
		while (it.hasNext()) {
			b.append("\"");
			b.append(map.get(it.next()));
			b.append("\"");
			b.append(SEPARATOR);
		}
		if (b.length()>0) b.deleteCharAt(b.length()-1);
		return b.toString();
	}

	public void clean() {
		Iterator<String> it = keysSorted.iterator();
		while (it.hasNext()) {
			map.put(it.next(), "");
		}
	}
}