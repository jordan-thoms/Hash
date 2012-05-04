package nz.net.thoms.hash.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import nz.net.thoms.hadoophash.Util;

import org.junit.Test;

public class UtilTest {

	@Test
	public void testPermute() {
		String[] chars = new String[] {"a", "b", "c"};
		ArrayList<String> result = Util.permute(chars, 3);
		assertTrue(result.contains("aaa"));
		assertTrue(result.contains("aab"));
		assertTrue(result.contains("aac"));
		assertTrue(result.contains("aba"));
		assertTrue(result.contains("abb"));
		assertTrue(result.contains("abc"));
		assertTrue(result.contains("cba"));
		assertTrue(result.contains("ccc"));
	}
	
	@Test
	public void testPermute2() {
		String[] chars = new String[] {"a", "b", "c"};
		ArrayList<String> result = Util.permute(chars, 1);
		assertTrue(result.contains("a"));
		assertTrue(result.contains("b"));
		assertTrue(result.contains("c"));
	}
	
	@Test
	public void testPermuteZero() {
		String[] chars = new String[] {"a", "b", "c"};
		ArrayList<String> result = Util.permute(chars, 0);
		assertTrue(result.isEmpty());
	}

}
