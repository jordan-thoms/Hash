package nz.net.thoms.hash.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import nz.net.thoms.hadoophash.HashInput;
import nz.net.thoms.hadoophash.HashInputSplit;
import nz.net.thoms.hadoophash.Util;

import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.junit.Test;

public class InputSplitTest {
	public static class MockJobConf extends JobConf {
		@Override
		public int getInt(String name, int def) {
			if (name.equals("tasks")) {
				return 300;
			} else if (name.equals("password_length")) {
				return 6;
			} else {
				return def;
			}
		}
	}
	@Test
	public void testGetSplitsHasAllPrefixes() throws IOException {
		MockJobConf jobConf = new MockJobConf();
		HashInput input = new HashInput();
		InputSplit[] splits = input.getSplits(jobConf, 1);
		ArrayList<String> prefixes = Util.permute(Util.chars, 2);
		for (String prefix : prefixes) {
			boolean found = false;
			for (InputSplit split : splits) {
				HashInputSplit hashSplit = ((HashInputSplit) split);
				if (hashSplit.getPrefixes().contains(prefix)) {
					found = true;
				}
			}
			if (found == false) {
				fail("Prefix " + prefix + " was not in any of the splits!");
			}
		}
	}
	
	@Test
	public void testGetSplitsIsBalanced() throws IOException {
		MockJobConf jobConf = new MockJobConf();
		HashInput input = new HashInput();
		InputSplit[] splits = input.getSplits(jobConf, 1);
		int maxPrefixesInSplit = 0;
		int minPrefixesInSplit = Integer.MAX_VALUE;
		for (InputSplit split : splits) {
			HashInputSplit hashSplit = ((HashInputSplit) split);
			maxPrefixesInSplit = Math.max(maxPrefixesInSplit, hashSplit.getPrefixes().size());
			minPrefixesInSplit = Math.min(minPrefixesInSplit, hashSplit.getPrefixes().size());
		}
		
		if ((maxPrefixesInSplit - minPrefixesInSplit) > 2) {
			fail("Max prefixes was " + maxPrefixesInSplit + " min: " +minPrefixesInSplit + " too unbalanced! ");
		}
	}
}
