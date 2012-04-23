package nz.net.thoms.hash.shared;

import java.util.ArrayList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

public class Util {
	
	public static String[] chars = new String[] {
		"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
		"q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
	};

	static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();		

	public static String hashIdentifier(String hash) {
		return hash.substring(0, 5);
	}
	
	public static void putEntity(String hash, String prefix, int length) {
		Entity entry = new Entity(Util.hashIdentifier(hash));
		entry.setProperty("hash", hash);
		entry.setProperty("prefix", prefix);
		entry.setProperty("length", length);
		datastore.put(entry);
	}
	
	
	public static ArrayList<String> permute(String[] chars, int length) {
		ArrayList<String> result = new ArrayList<String>();
		if (length > 0) {
			permuteRecursive(result, chars, length, 0, "");
		}
		return result;
	}
	
	private static void permuteRecursive(ArrayList<String> result, String[] chars, int length, int depth, String prefix) {
		if (depth < length) {
			for (String c : chars) {
				permuteRecursive(result, chars, length, depth+1, prefix.concat(c));
			}
		} else {
			result.add(prefix);
		}
	}
}
