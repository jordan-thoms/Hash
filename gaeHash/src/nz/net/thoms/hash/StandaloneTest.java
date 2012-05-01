package nz.net.thoms.hash;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

import nz.net.thoms.hash.shared.Util;

import com.twmacinta.util.MD5;

public class StandaloneTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long starta = System.currentTimeMillis();
		int i;
		for (i=0; i< 1000000; i++) {
			i += 2;
			i += (int) Math.sin(i * (i + (int) System.currentTimeMillis()));
		}
		long enda = System.currentTimeMillis();
		System.out.println("took " + (enda - starta) + " i:" + i);

		
		MD5.initNativeLibrary(true);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String hash = "95ebc3c7b3b9f1d2c40fec14415d3cb8";
		String prefix = "";
		long length = 4;
		System.out.println(" hash: " + hash + " prefix: " + prefix + " length: " + length);
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
			fourthPart -= System.currentTimeMillis();

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
				System.out.println("Password " + candidate);
				
			}
			counter++;
			fourthPart += System.currentTimeMillis();
		}
		end = System.currentTimeMillis();
		float perS = counter / ((float) (end - start) / 1000);
		System.out.println("Did " + counter + " in " + (end - start) + " which is " + perS);
		System.out.println("First part: " + firstPart);
		System.out.println("second part: " + secondPart);
		System.out.println("third part: " + thirdPart);
		System.out.println("fourth part: " + fourthPart);
	}
}
