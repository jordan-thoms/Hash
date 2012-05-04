package nz.net.thoms.hadoophash;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.lang.StringUtils;

public class HashcatHashExecutor implements HashExecutor {
	public HashcatHashExecutor() {

	}


	@Override
	public HashResult executeHashes(String hash, String prefix, int length) {
		Runtime r = Runtime.getRuntime();
		HashResult result = new HashResult();
		try {
			int postfix_length = length - prefix.length();
			String mask = prefix + StringUtils.repeat("?1", postfix_length);
			System.out.println("hashcat/hash.sh " + hash + " " + mask);
			Process p = r.exec("hashcat/hash.sh " + hash + " " + mask);
			InputStream in = p.getInputStream();
			BufferedInputStream buf = new BufferedInputStream(in);
			InputStreamReader inread = new InputStreamReader(buf);
			BufferedReader bufferedreader = new BufferedReader(inread);

			// Read the output
			String line;
			while ((line = bufferedreader.readLine()) != null) {
				if (line.startsWith("Checked:")) {
					line = line.replace("Checked:", "");
					try {
						result.checked = Integer.parseInt(line);
					} catch (NumberFormatException e) {
						result.checked = 0;
					}
				} else if(line.startsWith("Password:")) {
					line = line.replaceFirst("Password:", "");
					result.password = new String(line);
				}
			}
			// Check for failure
			try {
				if (p.waitFor() != 0) {
					System.err.println("exit value = " + p.exitValue());
				}
			} catch (InterruptedException e) {
				System.err.println(e);
			} finally {
				// Close the InputStream
				bufferedreader.close();
				inread.close();
				buf.close();
				in.close();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return result;
	}

}
