package nz.net.thoms.hadoophash;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import com.twmacinta.util.MD5;

public class HashMapper extends MapReduceBase implements Mapper<WritableComparable, HashValue, String, String> {
	
	@Override
	public void map(WritableComparable key, HashValue value,
			OutputCollector<String, String> output, Reporter reporter) throws IOException {
		String hash = value.getHash();
		String prefix = value.getPrefix();
		int length = value.getLength();
		System.out.println("Mapping key: " + key + " hash: " + hash + " prefix: " + prefix + " length: " + length);
		int counter = 0;
		MD5 md5 = new MD5();

		long start = System.currentTimeMillis();
		ArrayList<String> strings = Util.permute(Util.chars, (int) length - prefix.length());
		long end = System.currentTimeMillis();
		System.out.println("Permute took " + (end - start));
		start = System.currentTimeMillis();
		long firstPart = 0;
		long secondPart = 0;
		long thirdPart = 0;
		long fourthPart = 0;
		for (String postfix : strings) {
			String candidate = prefix.concat(postfix);
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
				System.out.println("Password is " + candidate);
				output.collect(hash, candidate);
			}
			counter++;
		}
		end = System.currentTimeMillis();
		float perS = counter / ((float) (end - start) / 1000);
		System.out.println("Did " + counter + " in " + (end - start) + " which is " + perS);
		System.out.println("First part: " + firstPart);
		System.out.println("second part: " + secondPart);
		System.out.println("third part: " + thirdPart);
		System.out.println("fourth part: " + fourthPart);
        reporter.getCounter("Hashes", "Checked").increment(counter);
	}


}
