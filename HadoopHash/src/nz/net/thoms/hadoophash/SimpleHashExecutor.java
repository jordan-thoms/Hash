package nz.net.thoms.hadoophash;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.hadoop.io.Text;

import com.twmacinta.util.MD5;

public class SimpleHashExecutor implements HashExecutor {

	@Override
	public HashResult executeHashes(String hash, String prefix, int length) {
		int counter = 0;
		MD5 md5 = new MD5();
		HashResult result = new HashResult();
		long start = System.currentTimeMillis();
		ArrayList<String> strings = Util.permute(Util.chars, (int) length - prefix.length());
		long end = System.currentTimeMillis();
		System.out.println("Permute took " + (end - start));
		start = System.currentTimeMillis();
		for (String postfix : strings) {
			String candidate = prefix.concat(postfix);
			md5.Init();
			try {
				md5.Update(candidate, null);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String hashC = md5.asHex();
			
			if (hash.equals(hashC)) {
				result.password = candidate;
			}
			counter++;
		}
		end = System.currentTimeMillis();
		float perS = counter / ((float) (end - start) / 1000);
		System.err.println("Did " + counter + " in " + (end - start) + " which is " + perS);
		result.checked = counter;
		return result;
	}

}
