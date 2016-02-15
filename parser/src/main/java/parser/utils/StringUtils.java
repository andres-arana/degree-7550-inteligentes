package parser.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class StringUtils {

	public static String join(Collection<String> col, String separator) {
		String result = "";

		Iterator<String> it = col.iterator();
		int i = 0;
		while (it.hasNext()) {
			String str = it.next();
			result += str;
			if (col.size() != i + 1)
				result += separator;
			i++;
		}
		return result;
	}

	public static String join(Deque<String> deque, String separator, boolean reversed) {
		//magic, this is because of the reverse
		String[] a = new String[1];
		List<String> list = Arrays.asList(deque.toArray(a));
		if (reversed) Collections.reverse(list);
		String res = StringUtils.join(list, separator);
		if (reversed) Collections.reverseOrder();
		return res;
	}
	
}