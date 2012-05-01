package nz.net.thoms.hash.mapreduce;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Logger;

import nz.net.thoms.hash.shared.Util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.naming.java.javaURLContextFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.mapreduce.AppEngineMapper;
import com.twmacinta.util.MD5;

public class HashMapper extends AppEngineMapper<Key, Entity, NullWritable, NullWritable> {
	private static final Logger log = Logger.getLogger(HashMapper.class.getName());

	private MessageDigest hasher;
	
	public HashMapper() {
		MD5.initNativeLibrary(true);
	}

	@Override
	public void taskSetup(Context context) {
	}

	@Override
	public void taskCleanup(Context context) {
	}

	@Override
	public void setup(Context context) {
		log.warning("Doing per-worker setup");
	}

	@Override
	public void cleanup(Context context) {
		log.warning("Doing per-worker cleanup");
	}

	@Override
	public void map(Key key, Entity value, Context context) {
		if (value.hasProperty("hash")) {
			String hash = (String) value.getProperty("hash");
			String prefix = (String) value.getProperty("prefix");
			long length = (Long) value.getProperty("length");
			log.warning("Mapping key: " + key + " hash: " + hash + " prefix: " + prefix + " length: " + length);
			int counter = 0;
			MD5 md5 = new MD5();

			long start = System.currentTimeMillis();
			ArrayList<String> strings = Util.permute(Util.chars, (int) length - prefix.length());
			long end = System.currentTimeMillis();
			log.warning("Permute took " + (end - start));
			start = System.currentTimeMillis();
			long firstPart = 0;
			long secondPart = 0;
			long thirdPart = 0;
			long fourthPart = 0;
			for (String postfix : strings) {
				String candidate = prefix.concat(postfix);
//				log.warning(candidate + " with prefix: " + prefix);
				//String hashC = DigestUtils.md5Hex(candidate);
				firstPart -= System.currentTimeMillis();
				md5.Init();
				firstPart += System.currentTimeMillis();
				secondPart -= System.currentTimeMillis();
				try {
					md5.Update(candidate, null);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				secondPart += System.currentTimeMillis();
				thirdPart -= System.currentTimeMillis();
				String hashC = md5.asHex();
				thirdPart += System.currentTimeMillis();
				
				if (hash.equals(hashC)) {
					log.warning("Password is " + candidate);
					DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();							
					Entity entry = new Entity("Results");
					entry.setProperty("hash", hash);
					entry.setProperty("password", candidate);
					datastore.put(entry);
				}
				counter++;
			}
			end = System.currentTimeMillis();
			float perS = counter / ((float) (end - start) / 1000);
			log.warning("Did " + counter + " in " + (end - start) + " which is " + perS);
			log.warning("First part: " + firstPart);
			log.warning("second part: " + secondPart);
			log.warning("third part: " + thirdPart);
			log.warning("fourth part: " + fourthPart);

	        context.getCounter("Hashes", "Checked").increment(counter);
		}
	}
}