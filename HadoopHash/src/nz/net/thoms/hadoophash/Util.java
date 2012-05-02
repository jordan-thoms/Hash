package nz.net.thoms.hadoophash;

import java.util.ArrayList;


public class Util {
	
	public static String[] chars = new String[] {
		"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
		"q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
	};


	public static String hashIdentifier(String hash) {
		return hash.substring(0, 5);
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