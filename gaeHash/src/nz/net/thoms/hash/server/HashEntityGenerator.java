package nz.net.thoms.hash.server;

import java.util.ArrayList;

import nz.net.thoms.hash.shared.Util;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

public class HashEntityGenerator {
	private static int POSTFIX_LENGTH = 4;
	
	
	
	public static void generatePutEntities(String hash, int width) {
		int prefix_length = width - POSTFIX_LENGTH;
		if  (prefix_length < 0) {
			prefix_length = 0;
		}
		
		ArrayList<String> prefixes = Util.permute(Util.chars, prefix_length);
		if (!prefixes.isEmpty()) {
			for (String prefix: prefixes) {
				Util.putEntity(hash, prefix, width);
			}
		} else {
			Util.putEntity(hash, "", width);
		}
	}
}
